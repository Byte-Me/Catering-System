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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainPanel.getRootPane().setDefaultButton(loginButton);

        Image logo = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Images/icon-login.png"));
        logoLabel.setIcon(new ImageIcon(logo));

        loginButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                String inputUsr = usernameTextField.getText();
                String inputPass = new String(passwordPasswordField.getPassword());

                dbconnect = new LoginManagement();
                int userType = dbconnect.login(inputUsr, inputPass);


                if ( userType >= 0) {
                    // Logged in :)

                    // Open the main window
                    MainWindow mainWindow = new MainWindow(UserManagement.UserType.valueOf(userType));

                    setVisible(false); //you can't see me!
                    dispose(); //Destroy the JFrame object
                } else {
                    showMessageDialog(null, "Kunne ikke logge inn.");
                }


            }
        });

        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                // What happens on cancel?
                setVisible(false);
                dispose();
            }
        });

        pack();
        setLocationRelativeTo(null);

        setVisible(true);
    }

    private boolean isCorrectPassword(char[] input) {
        boolean isCorrect = true;
        char[] correctPassword = "pass".toCharArray();

        if (input.length != correctPassword.length) {
            isCorrect = false;
        } else {
            isCorrect = Arrays.equals(input, correctPassword);
        }
        //Zero out the password.
        Arrays.fill(correctPassword,'0');

        return isCorrect;
    }
}
