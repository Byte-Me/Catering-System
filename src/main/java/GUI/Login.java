package GUI;

import Database.LoginManagement;

import javax.swing.*;
import java.awt.*;

import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Created by olekristianaune on 03.03.2016.
 */
public class Login extends JFrame{
    private JTextField usernameTextField;
    private JPasswordField passwordPasswordField;
    private JButton loginButton;
    private JButton cancelButton;
    private JPanel mainPane;
    private JLabel logoLabel;

    private LoginManagement dbconnect;

    /**
     *
     */
    public Login() {
        setTitle("Login");
        setContentPane(mainPane);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainPane.getRootPane().setDefaultButton(loginButton);

        Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Images/icon32.png"));
        setIconImage(icon);

        Image logo = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Images/icon-login.png"));
        logoLabel.setIcon(new ImageIcon(logo));

        loginButton.addActionListener(e -> {

            String inputUsr = usernameTextField.getText();
            String inputPass = new String(passwordPasswordField.getPassword());

            dbconnect = new LoginManagement();
            Object[] user = dbconnect.login(inputUsr, inputPass);
            int status;
            if (user[6] != null) {
                status = (int) user[6];

                if (status == 1) {
                    // Successful login

                    // Open the main window
                    new MainWindow(user);

                    setVisible(false); //you can't see me!
                    dispose(); //Destroy the JFrame object
                } else {
                    showMessageDialog(null, "User deactivated. Contact admin.");
                }
            } else {
                showMessageDialog(null, "Wrong username or password");
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
