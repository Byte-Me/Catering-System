package GUI;

import Database.LoginManagement;
import Database.UserManagement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Created by olekristianaune on 03.03.2016.
 */
public class Login extends JFrame{
    private JTextField usernameTextField;
    private JPasswordField passwordPasswordField;
    private JButton loginButton;
    private JButton cancelButton;
    private JPanel mainPanel;
    private JLabel logoLabel;

    private LoginManagement dbconnect;

    public Login() {

        setContentPane(mainPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainPanel.getRootPane().setDefaultButton(loginButton);

        Image logo = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Images/icon-login.png"));
        logoLabel.setIcon(new ImageIcon(logo));

        loginButton.addActionListener(e -> {

            String inputUsr = usernameTextField.getText();
            String inputPass = new String(passwordPasswordField.getPassword());

            dbconnect = new LoginManagement();
            int userType = dbconnect.login(inputUsr, inputPass);


            if ( userType >= 0) {
                // Logged in :)

                // Open the main window
                new MainWindow(UserManagement.UserType.valueOf(userType));

                setVisible(false); //you can't see me!
                dispose(); //Destroy the JFrame object
            } else {
                showMessageDialog(null, "Kunne ikke logge inn.");
            }


        });

        cancelButton.addActionListener(e -> {
            // What happens on cancel?
            setVisible(false);
            dispose();
        });

        pack();
        setLocationRelativeTo(null);

        setVisible(true);
    }

}
