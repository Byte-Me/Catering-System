package GUI;

import Database.UserManagement;
import GUI.WindowPanels.Users;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by olekristianaune on 09.03.2016.
 */
public class AddUser extends JFrame {
    private JTextField firstName;
    private JTextField lastName;
    private JTextField email;
    private JComboBox userType;
    private JTextField userName;
    private JTextField password;
    private JPanel mainPane;
    private JButton cancelButton;
    private JButton addUserButton;
    private JTextField phone;

    UserManagement userManagement;

    public AddUser(Container parent) {
        setContentPane(mainPane);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainPane.getRootPane().setDefaultButton(addUserButton);
        pack();
        setLocationRelativeTo(parent);

        // Setting up the userType select box
        userType.addItem("Admin");
        userType.addItem("Sale");
        userType.addItem("Driver");
        userType.addItem("Chef");

        addUserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String fName = firstName.getText();
                String lName = lastName.getText();
                String mail = email.getText();
                String phoneNr = phone.getText();
                int type = userType.getSelectedIndex();
                String uName = userName.getText();
                String pass = password.getText();


                userManagement = new UserManagement();
                // TODO - legg inn sjekk for om registrering var vellykket
                userManagement.registerUser(fName, lName, uName, pass, mail, phoneNr, type); // Legg til bruker i database

                Users.updateUsers();

                setVisible(false);
                dispose();

            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });

        setVisible(true);
    }
}
