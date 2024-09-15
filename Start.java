import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Start {
    private JFrame frame;

    public Start() {
        frame = new JFrame("Main Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(802, 289);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Vertical layout

        JLabel titleLabel = new JLabel("B.N.M Blood Bank");
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 22));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the title
        panel.add(titleLabel);
        
        JTextArea objectivesText = new JTextArea();
        objectivesText.setEditable(false);
        objectivesText.setLineWrap(true);
        objectivesText.setWrapStyleWord(true);
        objectivesText.setText("A blood bank is a vital institution that plays a crucial role in " +
                "providing safe and timely blood transfusions to patients in need. The primary objectives " +
                "of a blood bank include collecting, storing, and distributing blood and blood products " +
                "to hospitals and medical facilities. By ensuring an adequate supply of blood, blood banks " +
                "help save countless lives every day, especially in emergency situations, surgeries, and " +
                "treatment of various medical conditions. Blood donation, which forms the backbone of a " +
                "blood bank, is a noble act that allows individuals to contribute to the well-being of " +
                "their communities.");

        objectivesText.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the paragraph
        panel.add(objectivesText);


        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Center-aligned buttons with gap between them

        JButton loginButton1 = new JButton("Admin");
        loginButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                LoginGUI.createAndShowGUI();
            }
        });
        buttonPanel.add(loginButton1);

        JButton loginButton2 = new JButton("Donor");
        loginButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Login2GUI.createAndShowGUI();
            }
        });
        buttonPanel.add(loginButton2);

        panel.add(buttonPanel);

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the exit button
        panel.add(exitButton);

        frame.add(panel);
    }

    public void showMainPage() {
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Start mainGUI = new Start();
            mainGUI.showMainPage();
        });
    }
}
