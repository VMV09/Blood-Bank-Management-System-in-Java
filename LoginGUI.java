import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginGUI {
    private static final String USERNAME = "Vishruth";
    private static final String PASSWORD = "VMV@123";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginGUI::createAndShowGUI);
    }

    static void createAndShowGUI() {
        JFrame frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 150);
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
                String username = userText.getText();
                String password = String.valueOf(passwordText.getPassword());

                if (username.equals(USERNAME) && password.equals(PASSWORD)) {
                    frame.dispose(); // Close login window
                    // Open the main Blood Bank Program GUI
                    bbmsGUI.createAndShowGUI();
                } else {
                    JOptionPane.showMessageDialog(panel, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
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
