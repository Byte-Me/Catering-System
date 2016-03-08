package GUI;

import Database.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import static javax.swing.JOptionPane.*;

/**
 * Created by olekristianaune on 03.03.2016.
 */
public class Login extends JFrame{
    private JTextField usernameTextField;
    private JPasswordField passwordPasswordField;
    private JButton loginButton;
    private JButton cancelButton;
    private JPanel mainPanel;

    private UserLoginFromDatabase dbconnect = new UserLoginFromDatabase();

    public Login() {

        setContentPane(mainPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String inputUsr = usernameTextField.getText();
                String inputPass = new String(passwordPasswordField.getPassword());

                System.out.println(inputUsr);
                System.out.println(inputPass);

                int userType = -1;


                try {
                    userType = dbconnect.login(inputUsr, inputPass);
                } catch (Exception ex) {
                    System.out.println(ex);
                }

                System.out.println(userType);

                if ( userType >= 0) {
                    // Logged in :)

                    // Open the main window
                    MainWindow mainWindow = new MainWindow();

                    setVisible(false); //you can't see me!
                    dispose(); //Destroy the JFrame object
                } else {
                    showMessageDialog(null, "Kunne ikke logge inn.");
                }


            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // What happens on cancel?
                setVisible(false);
                dispose();
            }
        });

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
