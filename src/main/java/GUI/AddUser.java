package GUI;

import javax.swing.*;
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

    public AddUser() {
        setContentPane(mainPane);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainPane.getRootPane().setDefaultButton(addUserButton);
        pack();

        // Setting up the userType select box
        userType.addItem("Admin");
        userType.addItem("Sale");
        userType.addItem("Driver");
        userType.addItem("Chef");

        addUserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //TODO
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
