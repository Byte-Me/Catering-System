package GUI;

import Database.UserManagement;
import GUI.WindowPanels.Users;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Created by Evdal on 09.04.2016.
 */
public class EditUser extends JDialog{
    private JTextField firstName;
    private JTextField lastName;
    private JTextField email;
    private JComboBox<String> userType;
    private JTextField userName;
    private JTextField password;
    private JPanel mainPane;
    private JButton cancelButton;
    private JButton addUserButton;
    private JTextField phone;
    private JRadioButton changePasswordRadioButton;

    UserManagement userManagement = new UserManagement(); //TODO: Dette må testes, db var nede 09.04.2016


    public EditUser(String username) {
        setContentPane(mainPane);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //  mainPane.getRootPane().setDefaultButton(addUserButton); //TODO: ønsker vi denne?
        pack();
        setLocationRelativeTo(getParent());
        setModal(true);

        // Setting up the userType select box
        userType.addItem("Admin");
        userType.addItem("Sale");
        userType.addItem("Driver");
        userType.addItem("Chef");

        Object[] userInfo = userManagement.getSingleUserInfo(username); //Henter info om spesifikk bruker

        firstName.setText((String)userInfo[0]);
        lastName.setText((String)userInfo[1]);
        email.setText((String)userInfo[2]);
        phone.setText((String)userInfo[3]);
        userName.setText((String)userInfo[4]);
        userType.setSelectedIndex((Integer)userInfo[5]);


        addUserButton.addActionListener(e -> {
            String fName = firstName.getText();
            String lName = lastName.getText();
            String mail = email.getText();
            String phoneNr = phone.getText();
            int type1 = userType.getSelectedIndex();
            String uName = userName.getText();

            if(changePasswordRadioButton.isSelected()){ //TODO: skal vi legge inn skriv inn tidligere passord?
                String newPass = JOptionPane.showInputDialog("Input new password:");
                userManagement.updateUserPass(username, newPass);
            }
            if(userManagement.updateUserInfoFName(username, fName) &&
            userManagement.updateUserInfoLName(username, lName) &&
            userManagement.updateUserInfoEmail(username, mail) &&
            userManagement.updateUserInfoPhone(username, phoneNr) &&
            userManagement.updateUserInfoAccessLevel(username, type1) &&
                userManagement.updateUserInfoUsername(username, uName) )
                        {

                JOptionPane.showMessageDialog(null, "User updated.");
            }
            else{
                JOptionPane.showMessageDialog(null, "Issue with updating user.");
            }

            Users.updateUsers();

            setVisible(false);
            dispose();

        });

        cancelButton.addActionListener(e -> {
            setVisible(false);
            dispose();
        });

        setVisible(true);
    }
}