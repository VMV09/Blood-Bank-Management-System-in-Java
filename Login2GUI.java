import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Login2GUI {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/donors";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login2GUI::createAndShowGUI);
    }

    public static void createAndShowGUI() {
        JFrame frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        frame.add(panel);

        JLabel userLabel = new JLabel("Username:");
        JTextField userText = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordText = new JPasswordField(20);

        JButton loginButton = new JButton("Login");
        JButton cancelButton = new JButton("Cancel");

        panel.setLayout(new GridLayout(3, 2));
        panel.add(userLabel);
        panel.add(userText);
        panel.add(passwordLabel);
        panel.add(passwordText);
        panel.add(loginButton);
        panel.add(cancelButton);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = userText.getText();
                char[] passwordChars = passwordText.getPassword(); // Get password as char array
                String password = new String(passwordChars); // Convert char array to string

                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                     PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM donors WHERE name = ? AND password = ?")) {
                    pstmt.setString(1, name);
                    pstmt.setString(2, password);
                    ResultSet rs = pstmt.executeQuery();

                    if (rs.next()) {
                        frame.dispose(); // Close login window
                        BloodBankOperationsGUI.createAndShowGUI(name); // Open blood bank operations page
                    } else {
                        JOptionPane.showMessageDialog(panel, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(panel, "Error connecting to the database.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        frame.setVisible(true);
    }
}
