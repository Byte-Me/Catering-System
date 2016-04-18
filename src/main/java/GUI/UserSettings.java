package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by olekristianaune on 18.04.2016.
 */
public class UserSettings extends JDialog {

    private JButton saveButton;
    private JButton cancelButton;
    private JTextField firstName;
    private JTextField lastName;
    private JTextField email;
    private JTextField phone;
    private JTextField password1;
    private JTextField password2;
    private JPanel mainPanel;

    public UserSettings(Object[] user) {
        setTitle("User Settings");
        setContentPane(mainPanel);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Set image
        Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Images/icon32.png"));
        setIconImage(icon);

        // Set these fields
        firstName.setText("First");
        lastName.setText("Last");
        email.setText("Mail");
        phone.setText("Phone");

        saveButton.addActionListener(e -> {
            // TODO: Update user information for current user

            if (password1.getText().equals(password2.getText()) && !password1.getText().isEmpty() && !password2.getText().isEmpty()) {
                // TODO: Update password
            }

            if (true) { // Some condition here to check that the information was updated
                setVisible(false);
                dispose();
            }
        });

        cancelButton.addActionListener(e -> {
            setVisible(false);
            dispose();
        });

        pack();
        setLocationRelativeTo(getParent());
        setModal(true);
        setVisible(true);
    }
}
