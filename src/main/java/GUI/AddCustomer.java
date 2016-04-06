package GUI;

import Database.CustomerManagement;
import GUI.WindowPanels.Customers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static javax.swing.JOptionPane.showMessageDialog;

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

    public CustomerManagement customerManagement = new CustomerManagement();

    public AddCustomer(Container parent) {

        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(parent);

        // Close on cancel
        final ActionListener closeWindow = e -> {
            setVisible(false);
            dispose();
        };

        cancelButton.addActionListener(closeWindow);
        cCancelButton.addActionListener(closeWindow);

        // Add Private Customer
        addCustomerButton.addActionListener(e -> {
            String fName = firstName.getText();
            String lName = lastName.getText();
            String mail = email.getText();
            String phoneNr = phone.getText();
            String adr = address.getText();
            String pc = postalCode.getText();
            String pcCity = city.getText();

            boolean addedCustomer = customerManagement.addCustomerPerson(fName, lName, mail, phoneNr, adr, pc, pcCity);

            if (addedCustomer) {
                // Update customer list
                Customers.updateCustomer();

                // Close window
                setVisible(false);
                dispose();
            } else {
                showMessageDialog(null, "Could not create user, please try again.");
            }
        });

        // Add Corporate Customer
        cAddCustomerButton.addActionListener(e -> {
            String name1 = cName.getText();
            String adr = cAddress.getText();
            String pc = cPostalCode.getText();
            String pcCity = cCity.getText();

            boolean addedCustomer = customerManagement.addCustomerCompany(name1, "", "", adr, pc, pcCity);

            if (addedCustomer) {
                // Update customer list
                Customers.updateCustomer();

                // Close window
                setVisible(false);
                dispose();
            } else {
                showMessageDialog(null, "Could not create user, please try again.");
            }
        });

        setVisible(true);

    }
}
