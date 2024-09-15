import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Calendar;

public class BloodBankOperationsGUI {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/donors";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static Connection conn;
    private static String loggedInUserBloodType;
    private static String loggedInUsername;

    public static void createAndShowGUI(String username) {
    	loggedInUsername = username;
        fetchBloodTypeFromDonorTable(username); // Fetch blood type of logged-in user
        if (loggedInUserBloodType == null) {
            JOptionPane.showMessageDialog(null, "Error: Unable to retrieve user's blood type.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFrame frame = new JFrame("Blood Bank Operations");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1037, 290);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);

        // Create table model
        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model);

        // Add columns to the table model
        model.addColumn("Blood Type");
        model.addColumn("Packets Available");
        model.addColumn("Cost per Packet");

        // Populate the table model with data from the database
        populateTable(model);

        // Add the table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Create buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));

        // Add donate blood button
        JButton donateButton = new JButton("Donate Blood");
        buttonPanel.add(donateButton);

        donateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                donateBlood(loggedInUserBloodType); // Donate blood using logged-in user's blood type
                // Refresh table after donation
                model.setRowCount(0); // Clear existing table data
                populateTable(model); // Populate table with updated data
            }
        });
        
        JButton donate1Button = new JButton("Donate Blood 01");
        buttonPanel.add(donate1Button);

        donate1Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                donate1Blood(loggedInUserBloodType); // Donate blood using logged-in user's blood type
                // Refresh table after donation
                model.setRowCount(0); // Clear existing table data
                populateTable(model); // Populate table with updated data
            }
        });
        
        JButton Profile = new JButton("Profile");
        buttonPanel.add(Profile);

        Profile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewUserDetails(username); // View next donation date for the logged-in user
            }
        });
        
        JButton purchaseAnyButton = new JButton("Purchase Blood");
        buttonPanel.add(purchaseAnyButton);

        purchaseAnyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String bloodType = JOptionPane.showInputDialog(mainPanel, "Enter the blood type to purchase:");
                if (bloodType != null && !bloodType.isEmpty()) {
                    purchaseBlood(bloodType);
                    model.setRowCount(0);
                    populateTable(model);
                } else {
                    JOptionPane.showMessageDialog(mainPanel, "Blood type cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        JButton viewBloodAvailabilityByDistrictButton = new JButton("View Availability");
        buttonPanel.add(viewBloodAvailabilityByDistrictButton);

        viewBloodAvailabilityByDistrictButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String district = JOptionPane.showInputDialog(mainPanel, "Enter the district to view blood availability:");
                if (district != null && !district.isEmpty()) {
                    fetchBloodAvailabilityByDistrict(district);
                } else {
                    JOptionPane.showMessageDialog(mainPanel, "District cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        
        // Add exit button
        JButton exitButton = new JButton("Exit");
        buttonPanel.add(exitButton);

        exitButton.addActionListener(e -> frame.dispose()); // Close the GUI when exit button is clicked

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private static void populateTable(DefaultTableModel model) {
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "SELECT * FROM blood_inventory";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            // Populate the table model with data from the result set
            while (rs.next()) {
                String bloodType = rs.getString("blood_type");
                int packetsAvailable = rs.getInt("packets_available");
                double costperPacket= rs.getDouble("Cost");
                model.addRow(new Object[]{bloodType, packetsAvailable, costperPacket});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error retrieving data from database.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private static void viewUserDetails(String username) {
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "SELECT name, blood_type, district, last_donation_date, next_donation_date FROM donors WHERE name = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            StringBuilder userDetails = new StringBuilder("User Details:\n\n");
            if (rs.next()) {
            	userDetails.append("Name: ").append(rs.getString("name")).append("\n");
                userDetails.append("Blood Type: ").append(rs.getString("blood_type")).append("\n");
                userDetails.append("District: ").append(rs.getString("district")).append("\n");
                userDetails.append("Last Donation Date: ").append(rs.getDate("last_donation_date")).append("\n");
                userDetails.append("Next Donation Date: ").append(rs.getDate("next_donation_date")).append("\n");
            } else {
                JOptionPane.showMessageDialog(null, "Date not found for user: " + username, "Error", JOptionPane.ERROR_MESSAGE);
            }
        
        JOptionPane.showMessageDialog(null, userDetails.toString(), "User Details", JOptionPane.INFORMATION_MESSAGE);
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error retrieving user details.", "Error", JOptionPane.ERROR_MESSAGE);
    } finally {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}

    private static boolean isTodayNextDonationDate() {
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "SELECT next_donation_date FROM donors WHERE name = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, loggedInUsername);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Date nextDonationDate = rs.getDate("next_donation_date");
                // Get today's date
                Calendar todayCalendar = Calendar.getInstance();
                todayCalendar.set(Calendar.HOUR_OF_DAY, 0);
                todayCalendar.set(Calendar.MINUTE, 0);
                todayCalendar.set(Calendar.SECOND, 0);
                todayCalendar.set(Calendar.MILLISECOND, 0);
                Date today = new Date(todayCalendar.getTimeInMillis());
                // Check if today's date is equal to the next donation date
                return nextDonationDate.equals(today);
            } else {
                // User not found or next donation date not set
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error retrieving next donation date.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private static void donate1Blood(String bloodType) {
        if (loggedInUsername == null) {
            JOptionPane.showMessageDialog(null, "Error: Unable to retrieve logged-in user's information.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if(!isTodayNextDonationDate() ) {
        	JOptionPane.showMessageDialog(null,  "You are not eligible to donate blood. Please check your next Donation date.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Check if the next donation date matches today's date
            String checkNextDonationDateQuery = "SELECT next_donation_date FROM donors WHERE name = ?";
            PreparedStatement checkNextDonationDateStmt = conn.prepareStatement(checkNextDonationDateQuery);
            checkNextDonationDateStmt.setString(1, loggedInUsername);
            ResultSet nextDonationDateResult = checkNextDonationDateStmt.executeQuery();
            if (nextDonationDateResult.next()) {
                Date nextDonationDate = nextDonationDateResult.getDate("next_donation_date");
                Date today = new Date(System.currentTimeMillis());
                if (nextDonationDate.after(today)) {
                    JOptionPane.showMessageDialog(null, "You cannot donate blood before your next donation date.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                JOptionPane.showMessageDialog(null, "Error: Next donation date not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update blood inventory
            String updateInventory1Query = "UPDATE blood_inventory SET packets_available = packets_available + 1 WHERE blood_type = ?";
            PreparedStatement inventory1pstmt = conn.prepareStatement(updateInventory1Query);
            inventory1pstmt.setString(1, bloodType);
            inventory1pstmt.executeUpdate();

            // Update last donation date and next donation date for the donor
            String updateDonor1Query = "UPDATE donors SET last_donation_date = ?, next_donation_date = DATE_ADD(NOW(), INTERVAL 56 DAY) WHERE name = ?";
            PreparedStatement donor1Stmt = conn.prepareStatement(updateDonor1Query);
            donor1Stmt.setDate(1, new java.sql.Date(System.currentTimeMillis())); // Set last donation date to current date
            donor1Stmt.setString(2, loggedInUsername);
            donor1Stmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Blood donated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error donating blood.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
        
    private static void donateBlood(String bloodType) {
    	if(loggedInUsername == null) {
    		JOptionPane.showMessageDialog(null,  "Error: Unable to retrieve logged-in user's information.", "Error", JOptionPane.ERROR_MESSAGE);
    		return;
    	}
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            // Update blood inventory
            String updateInventoryQuery = "UPDATE blood_inventory SET packets_available = packets_available + 1 WHERE blood_type = ?";
            PreparedStatement inventorypstmt = conn.prepareStatement(updateInventoryQuery);
            inventorypstmt.setString(1, bloodType);
            inventorypstmt.executeUpdate();
            
            //Update last donation date and next donation date for the donor
            String updateDonorQuery = "UPDATE donors SET last_donation_date = ?, next_donation_date = DATE_ADD(NOW(), INTERVAL 56 DAY) WHERE name = ?";
            PreparedStatement donorStmt = conn.prepareStatement(updateDonorQuery);
            donorStmt.setDate(1, new java.sql.Date(System.currentTimeMillis())); // Set last donation date to current date
            donorStmt.setString(2, loggedInUsername);
            donorStmt.executeUpdate();
            
            JOptionPane.showMessageDialog(null, "Blood donated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error donating blood.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void fetchBloodTypeFromDonorTable(String username) {
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "SELECT blood_type FROM donors WHERE name = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                loggedInUserBloodType = rs.getString("blood_type");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error retrieving user's blood type.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
            
    private static void purchaseBlood(String bloodType) {
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Prompt the user to enter the district
            String district = JOptionPane.showInputDialog(null, "Enter the district from where you want to purchase blood:");

            // Check if the blood type exists in the specified district
            String checkQuery = "SELECT COUNT(*) AS count FROM blood_inventory bi " +
                                "INNER JOIN donors d ON bi.blood_type = d.blood_type " +
                                "WHERE bi.blood_type = ? AND d.District = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, bloodType);
            checkStmt.setString(2, district);
            ResultSet checkResult = checkStmt.executeQuery();
            checkResult.next();
            int bloodTypeCount = checkResult.getInt("count");
            if (bloodTypeCount == 0) {
                JOptionPane.showMessageDialog(null, "Blood type '" + bloodType + "' is not available in the specified district.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get the cost per packet from the database
            String costQuery = "SELECT Cost FROM blood_inventory WHERE blood_type = ?";
            PreparedStatement costStmt = conn.prepareStatement(costQuery);
            costStmt.setString(1, bloodType);
            ResultSet costResult = costStmt.executeQuery();
            if (costResult.next()) {
                double costPerPacket = costResult.getDouble("Cost");

                // Ask the user for the number of packets to purchase
                String packetsInput = JOptionPane.showInputDialog(null, "Enter the number of packets to purchase for blood type " + bloodType + ":");
                if (packetsInput == null || packetsInput.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int packetsToPurchase = Integer.parseInt(packetsInput);

                // Compute the total cost
                double totalCost = packetsToPurchase * costPerPacket;

                // Ask the user to pay the total cost
                int confirm = JOptionPane.showConfirmDialog(null, "Total Cost: ₹" + totalCost + "\n\nDo you want to proceed with the purchase?", "Confirm Purchase", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    // Update the database to reflect the purchase
                    String updateQuery = "UPDATE blood_inventory SET packets_available = packets_available - ? WHERE blood_type = ? AND packets_available >= ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                    updateStmt.setInt(1, packetsToPurchase);
                    updateStmt.setString(2, bloodType);
                    updateStmt.setInt(3, packetsToPurchase);
                    int rowsUpdated = updateStmt.executeUpdate();

                    // Check if any rows were updated
                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(null, "Blood purchased successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Blood not available for purchase.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Purchase cancelled.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Cost per packet not found for blood type '" + bloodType + "'.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error purchasing blood: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Close the database connection in a finally block to ensure it's always closed
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private static String getLoggedInUserDistrict() {
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "SELECT district FROM donors WHERE name = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, loggedInUsername);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("district");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    
    private static void fetchBloodAvailabilityByDistrict(String district) {
        String District = getLoggedInUserDistrict();
        if (District == null || District.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Error: Unable to retrieve user's district", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "SELECT bi.blood_type, bi.packets_available " +
                    "FROM blood_inventory bi " +
                    "INNER JOIN donors d ON bi.blood_type = d.blood_type " +
                    "WHERE d.District = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, District);
            ResultSet rs = pstmt.executeQuery();

            // Create a table model to hold the data
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Blood Type");
            model.addColumn("Packets Available");

            // Populate the table model with data from the result set
            while (rs.next()) {
                String bloodType = rs.getString("blood_type");
                int packetsAvailable = rs.getInt("packets_available");
                model.addRow(new Object[]{bloodType, packetsAvailable});
            }

            // Create a JTable and set the model
            JTable table = new JTable(model);

            // Put the table in a scroll pane
            JScrollPane scrollPane = new JScrollPane(table);

            // Display the table in a dialog
            JOptionPane.showMessageDialog(null, scrollPane, "Blood Availability in District: " + District, JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error retrieving blood availability in district.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    
    public static String getLoggedInUsername() {
    	return loggedInUsername;
    }
}
