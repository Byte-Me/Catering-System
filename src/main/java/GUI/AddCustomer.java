package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by olekristianaune on 11.03.2016.
 */
public class AddCustomer extends JFrame{
    private JPanel mainPanel;
    private JTextField firstName;
    private JTextField lastName;
    private JTextField email;
    private JTextField phone;
    private JTextField address;
    private JTextField postalCode;
    private JButton cancelButton;
    private JButton addCustomerButton;
    private JTextField cName;
    private JTextField city;
    private JTextField cAddress;
    private JTextField cPostalCode;
    private JTextField cCity;
    private JButton cCancelButton;
    private JButton cAddCustomerButton;

    public AddCustomer(Container parent) {

        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(parent);

        // Close on cancel
        ActionListener closeWindow = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        };

        cancelButton.addActionListener(closeWindow);
        cCancelButton.addActionListener(closeWindow);

        // Add Private Customer
        addCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fName = firstName.getText();
                String lName = lastName.getText();
                String mail = email.getText();
                String phoneNr = phone.getText();
                String adr = address.getText();
                String pc = postalCode.getText();
                String pcCity = city.getText();

                // Kall en metode her :)
            }
        });

        // Add Corporate Customer
        cAddCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = cName.getText();
                String adr = cAddress.getText();
                String pc = cPostalCode.getText();
                String pcCity = cCity.getText();

                // Kall en metode her :)
            }
        });

        setVisible(true);

    }
}
