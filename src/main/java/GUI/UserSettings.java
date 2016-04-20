package GUI;

import Database.UserManagement;

import javax.swing.*;
import java.awt.*;

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

        String username = user[0].toString();

        // Set these fields
        firstName.setText(user[1].toString());
        lastName.setText(user[2].toString());
        email.setText(user[3].toString());
        phone.setText(user[4].toString());

        saveButton.addActionListener(e -> {
            UserManagement um = new UserManagement();
            boolean updated;
            // Update user information for current user

            // Check if it is necessary to update info (ie. if field info is different from info in user array
            if (!user[1].toString().equals(firstName.getText()) && !firstName.getText().isEmpty()) {
                // Update first_name
                updated = um.updateUserInfoFName(username, firstName.getText()); // TODO: Check if sucessful
                if (updated) {
                    user[1] = firstName.getText();
                }
            }
            if (!user[2].toString().equals(lastName.getText()) && !lastName.getText().isEmpty()) {
                // Update last_name
                updated = um.updateUserInfoLName(username, lastName.getText());  // TODO: Check if sucessful
                if (updated) {
                    user[2] = lastName.getText();
                }
            }
            if (!user[3].toString().equals(email.getText()) && !email.getText().isEmpty()) {
                // Update email
                updated = um.updateUserInfoEmail(username, email.getText());  // TODO: Check if sucessful
                if (updated) {
                    user[3] = email.getText();
                }
            }
            if (!user[4].toString().equals(phone.getText()) && !phone.getText().isEmpty()) {
                // Update phone
                updated = um.updateUserInfoPhone(username, phone.getText());  // TODO: Check if sucessful
                if (updated) {
                    user[4] = phone.getText();
                }
            }
            if (password1.getText().equals(password2.getText()) && !password1.getText().isEmpty() && !password2.getText().isEmpty()) {
                // Update password
                updated = um.updateUserPass(username, password1.getText());  // TODO: Check if sucessful
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
