import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class bbmsGUI {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/donors";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    @SuppressWarnings("unused")
    private static Connection conn;
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    static void createAndShowGUI() {
        JFrame frame = new JFrame("Blood Bank Program");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);
        frame.setLocationRelativeTo(null);
        
        JPanel panel = new JPanel();
      
        frame.add(panel);
        
        JLabel titleLabel = new JLabel("B.N.M Blood Bank");
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 22));
        panel.add(titleLabel);
        
        JButton addDonorButton = new JButton("Add Donor");
        JButton listDonorsButton = new JButton("List Donors");
        JButton searchDonorButton = new JButton("Search Donor");
        JButton deleteDonorButton = new JButton("Delete Donor"); 
        JButton exitButton = new JButton("Exit");

        panel.add(addDonorButton);
        panel.add(listDonorsButton);
        panel.add(searchDonorButton);
        panel.add(deleteDonorButton);
        panel.add(exitButton);
        frame.setSize(300, 300);

        addDonorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addDonor(panel);
            }
        });

        listDonorsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                listDonors();
            }
        });
        searchDonorButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		searchDonor(panel);
        	}
        });
        deleteDonorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteDonor(panel);
            }
        });

        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        addDonorButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        listDonorsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchDonorButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteDonorButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        frame.setVisible(true);
    }

    private static void addDonor(JPanel panel) {
    	JTextField idField = new JTextField(10);
        JTextField nameField = new JTextField(20);
        JComboBox<String> bloodTypeComboBox = new JComboBox<>(new String[]{"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"});
        JTextField districtField = new JTextField(50);
        JTextField lastDonationDateField = new JTextField(20);
        JTextField contactNumberField = new JTextField(20);
        JTextField sexField = new JTextField(20);
        JTextField ageField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(10);
        
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(10,10));
        inputPanel.add(new JLabel("Customer Id:"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Donor Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Blood Type:"));
        inputPanel.add(bloodTypeComboBox);
        inputPanel.add(new JLabel("District:"));
        inputPanel.add(districtField);
        inputPanel.add(new JLabel("Last Donation Date (YYYY-MM-DD):"));
        inputPanel.add(lastDonationDateField);
        inputPanel.add(new JLabel("Contact Number:"));
        inputPanel.add(contactNumberField);
        inputPanel.add(new JLabel("Sex:"));
        inputPanel.add(sexField);
        inputPanel.add(new JLabel("Age:")); // Label for age field
        inputPanel.add(ageField);
        inputPanel.add(new JLabel("Password:"));
        inputPanel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(panel, inputPanel, "Enter Donor Information", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
        	String customer_id = idField.getText();
            String name = nameField.getText();
            String bloodType = (String)bloodTypeComboBox.getSelectedItem();
            String district = districtField.getText();
            String lastDonationDate = lastDonationDateField.getText();
            String contactNumber = contactNumberField.getText();
            String sex = sexField.getText(); // Get value from sex field
            String age = ageField.getText();
            char[] passwordChars = passwordField.getPassword(); // Get password as char array
            String password = new String(passwordChars);

            if (isValidDateFormat(lastDonationDate)) {
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                     Statement stmt = conn.createStatement()) {
                	
                	LocalDate lastDonation = LocalDate.parse(lastDonationDate, dateFormatter);
                	LocalDate nextDonation = lastDonation.plusMonths(6);
                	String nextDonationDate = nextDonation.format(dateFormatter);

                	String insertSQL = "INSERT INTO donors (customer_id, name, blood_type, District, last_donation_date, next_donation_date, contact_number, sex, age, password) " +
                            "VALUES ('" + customer_id + "', '" + name + "', '" + bloodType + "', '" + district +"', '" + lastDonationDate + "','" + nextDonationDate + "', '" + contactNumber + "','" + sex + "', '" + age + "', '" + password + "' )";
                    conn.createStatement().executeUpdate(insertSQL);
                    JOptionPane.showMessageDialog(panel, "Donor added successfully.");
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(panel, "Error adding donor. Please try again.");
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Invalid date format. Please use the format YYYY-MM-DD.");
            }
        }
    }

    private static boolean isValidDateFormat(String date) {
        return date.matches("\\d{4}-\\d{2}-\\d{2}");
    }
    
    private static void searchDonor(JPanel panel) {
        // Create a dropdown box for selection
        String[] searchOptions = {"Blood Type", "District"};
        JComboBox<String> searchComboBox = new JComboBox<>(searchOptions);

        // Create the input panel with the dropdown box
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 2));
        inputPanel.add(new JLabel("Search By:"));
        inputPanel.add(searchComboBox);

        // Show the input panel in a dialog
        int result = JOptionPane.showConfirmDialog(panel, inputPanel, "Select Search Option", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String selectedOption = (String) searchComboBox.getSelectedItem();
            if (selectedOption.equals("Blood Type")) {
                // Code for searching by blood type
                searchByBloodType(panel);
            } else if (selectedOption.equals("District")) {
                // Code for searching by district
                searchByDistrict(panel);
            } else {
                // Handle other cases or errors
                JOptionPane.showMessageDialog(panel, "Invalid selection. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private static void searchByBloodType(JPanel panel) {
        // Define an array of blood types
        String[] bloodTypes = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        
        // Create a dropdown box with the blood types
        JComboBox<String> bloodTypeComboBox = new JComboBox<>(bloodTypes);
        
        // Create the input panel with the dropdown box
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(1, 2));
        inputPanel.add(new JLabel("Blood Type:"));
        inputPanel.add(bloodTypeComboBox);

        // Show the input panel in a dialog
        int result = JOptionPane.showConfirmDialog(panel, inputPanel, "Search Donor by Blood Type", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String bloodType = (String) bloodTypeComboBox.getSelectedItem(); // Get the selected blood type
            
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement()) {

                String searchSQL = "SELECT * FROM donors WHERE blood_type = '" + bloodType + "'";
                ResultSet resultSet = stmt.executeQuery(searchSQL);
                StringBuilder donorsList = new StringBuilder();

                while (resultSet.next()) {
                    donorsList.append("ID: ").append(resultSet.getString("customer_id")).append("\n");
                    donorsList.append("Name: ").append(resultSet.getString("name")).append("\n");
                    donorsList.append("Blood Type: ").append(resultSet.getString("blood_type")).append("\n");
                    donorsList.append("District: ").append(resultSet.getString("District")).append("\n");
                    donorsList.append("Last Donation Date: ").append(resultSet.getString("last_donation_date")).append("\n");
                    donorsList.append("Contact Number: ").append(resultSet.getString("contact_number")).append("\n");
                    donorsList.append("Next Donation Date: ").append(resultSet.getString("next_donation_date")).append("\n");
                    donorsList.append("------------------------------\n");
                }

                if (donorsList.length() > 0) {
                    JOptionPane.showMessageDialog(panel, donorsList.toString(), "Donors with Blood Type " + bloodType, JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(panel, "No donors found with blood type " + bloodType, "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(panel, "Error searching donors. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    

    private static void searchByDistrict(JPanel panel) {
        JTextField districtField = new JTextField(20);
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(1, 2));
        inputPanel.add(new JLabel("District:"));
        inputPanel.add(districtField);

        int result = JOptionPane.showConfirmDialog(panel, inputPanel, "Search Donor by District", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String district = districtField.getText();

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement()) {

                String searchSQL = "SELECT * FROM donors WHERE district = '" + district + "'";
                ResultSet resultSet = stmt.executeQuery(searchSQL);
                StringBuilder donorsList = new StringBuilder();

                while (resultSet.next()) {
                    donorsList.append("ID: ").append(resultSet.getString("customer_id")).append("\n");
                    donorsList.append("Name: ").append(resultSet.getString("name")).append("\n");
                    donorsList.append("Blood Type: ").append(resultSet.getString("blood_type")).append("\n");
                    donorsList.append("District: ").append(resultSet.getString("District")).append("\n");
                    donorsList.append("Last Donation Date: ").append(resultSet.getString("last_donation_date")).append("\n");
                    donorsList.append("Next Donation Date: ").append(resultSet.getString("next_donation_date")).append("\n");
                    donorsList.append("Contact Number: ").append(resultSet.getString("contact_number")).append("\n");
                    donorsList.append("------------------------------\n");
                }

                if (donorsList.length() > 0) {
                    JOptionPane.showMessageDialog(panel, donorsList.toString(), "Donors in District " + district, JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(panel, "No donors found in district " + district, "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(panel, "Error searching donors. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void deleteDonor(JPanel panel) {
        JTextField donorIdField = new JTextField(20);
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(1, 2));
        inputPanel.add(new JLabel("Donor ID:"));
        inputPanel.add(donorIdField);

        int result = JOptionPane.showConfirmDialog(panel, inputPanel, "Enter Donor ID to Delete", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String donorId = donorIdField.getText();

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement()) {

                String deleteSQL = "DELETE FROM donors WHERE customer_id = " + donorId;
                int rowsAffected = stmt.executeUpdate(deleteSQL);

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(panel, "Donor deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(panel, "No donor found with the given ID.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(panel, "Error deleting donor. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private static void listDonors() {
        JFrame frame = new JFrame("List of Donors");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);

        // Create table model
        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model);

        // Add columns to the table model
        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Blood Type");
        model.addColumn("District");
        model.addColumn("Last Donation Date");
        model.addColumn("Next Donation Date");
        model.addColumn("Contact Number");

        // Populate the table model with data from the database
        populateDonorsTable(model);

        // Add the table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Create filter button
        JButton filterButton = new JButton("Filter by District");
        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String district = JOptionPane.showInputDialog(frame, "Enter district to filter:");
                if (district != null && !district.isEmpty()) {
                    model.setRowCount(0); // Clear existing table data
                    filterDonorsByDistrict(model, district); // Filter donors by district
                    addBackButton(frame, mainPanel, model); // Add back button
                } else {
                    JOptionPane.showMessageDialog(frame, "District cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        mainPanel.add(filterButton, BorderLayout.NORTH);

        frame.setVisible(true);
    }

    private static void populateDonorsTable(DefaultTableModel model) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "SELECT * FROM donors";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            // Populate the table model with data from the result set
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("customer_id"),
                        rs.getString("name"),
                        rs.getString("blood_type"),
                        rs.getString("District"),
                        rs.getString("last_donation_date"),
                        rs.getString("next_donation_date"),
                        rs.getString("contact_number")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error retrieving data from database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void filterDonorsByDistrict(DefaultTableModel model, String district) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "SELECT * FROM donors WHERE District = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, district);
            ResultSet rs = pstmt.executeQuery();

            // Populate the table model with filtered data
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("customer_id"),
                        rs.getString("name"),
                        rs.getString("blood_type"),
                        rs.getString("District"),
                        rs.getString("last_donation_date"),
                        rs.getString("next_donation_date"),
                        rs.getString("contact_number")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error retrieving data from database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void addBackButton(JFrame frame, JPanel panel, DefaultTableModel model) {
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.setRowCount(0); // Clear filtered table data
                populateDonorsTable(model); // Reload original table data
                panel.remove(backButton); // Remove back button
                frame.revalidate(); // Refresh frame
            }
        });
        panel.add(backButton, BorderLayout.SOUTH);
        frame.revalidate(); // Refresh frame
    }
}

       
    




    