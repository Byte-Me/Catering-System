package GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import static javax.swing.JOptionPane.*;

/**
 * Created by olekristianaune on 03.03.2016.
 */
public class Login extends JFrame{
    private JTextField brukernavnTextField;
    private JPasswordField passordPasswordField;
    private JButton loginButton;
    private JButton cancelButton;
    private JPanel mainPanel;

    public Login() {



        setContentPane(mainPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Gjør noe her

                char[] inputPass = passordPasswordField.getPassword();

                System.out.println("brukernavn: " + brukernavnTextField.getText());
                System.out.println("passord: " + inputPass);

                if (brukernavnTextField.getText().equals("bruker") && isCorrectPassword(inputPass)) {
                    showMessageDialog(null, "Innlogget");
                } else {

                    showMessageDialog(null, "Kunne ikke logge inn.");
                }


            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Gjør noe her
                showMessageDialog(null, "Cancel");
            }
        });

        setVisible(true);
    }

    private boolean isCorrectPassword(char[] input) {
        boolean isCorrect = true;
        char[] correctPassword = {'p', 'a', 's', 's'};

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
