package GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by olekristianaune on 18.04.2016.
 */
public class UserSettings extends JFrame {

    private JButton saveButton;
    private JButton cancelButton;
    private JTextField firstName;
    private JTextField lastName;
    private JTextField email;
    private JTextField phone;
    private JTextField password1;
    private JTextField password2;

    public UserSettings(Object[] user) {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

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
        });

        cancelButton.addActionListener(e -> {
            setVisible(false);
            dispose();
        });

        pack();
        setLocationRelativeTo(getParent());
        setVisible(true);
    }
}
