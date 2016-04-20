package GUI;

import Database.CustomerManagement;
import GUI.WindowPanels.Customers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Created by Evdal on 09.04.2016.
 */
public class EditCustomer extends JDialog{
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
        private JTabbedPane addCustTabs;
    private JTextField cMail;
    private JTextField cPhone;

    public CustomerManagement customerManagement = new CustomerManagement();

        public EditCustomer(String emailS) {
            setTitle("Edit Customer");
            setContentPane(mainPanel);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Images/icon32.png"));
            setIconImage(icon);


            // Close on cancel
            final ActionListener closeWindow = e -> {
                setVisible(false);
                dispose();
            };

            cancelButton.addActionListener(closeWindow);
            cCancelButton.addActionListener(closeWindow);

            Object[] custInfo = customerManagement.getSingleCustomerInfo(emailS);

            CustomerManagement.CustType custStatus = (CustomerManagement.CustType) custInfo[4];
            //addCustTabs.setSelectedIndex(custStatus); // What is this for??

            if (custStatus == CustomerManagement.CustType.PRIVATE) { //Person

                String[] fAndLName = formatName((String) custInfo[0]);
                String[] add = formatAdress((String) custInfo[3]);
                firstName.setText(fAndLName[0]);
                lastName.setText(fAndLName[1]);
                email.setText((String) custInfo[1]);
                phone.setText((String) custInfo[2]);
                address.setText(add[0]);
                postalCode.setText(add[1]);
                city.setText(add[2]);

                addCustTabs.remove(1); //Remove corporation tab

            } else if (custStatus == CustomerManagement.CustType.CORPORATION) { //customer
                String[] add = formatAdress((String) custInfo[3]);
                cName.setText((String) custInfo[0]);
                cMail.setText((String) custInfo[1]);
                cPhone.setText((String) custInfo[2]);
                cAddress.setText(add[0]);
                cPostalCode.setText(add[1]);
                cCity.setText(add[2]);

                addCustTabs.remove(0); // Remove private tab

            }


            // Add Private Customer
            addCustomerButton.addActionListener(e -> {
                String fName = firstName.getText();
                String lName = lastName.getText();
                String mail = email.getText();
                String phoneNr = phone.getText();
                String adr = address.getText();
                String pc = postalCode.getText();
                String pcCity = city.getText();

                if(
                customerManagement.updateCustomerName(emailS, fName, lName) &&
                        customerManagement.updateCustomerAdress(emailS,adr,pc,pcCity) &&
                        customerManagement.updateCustomerPhone(emailS,phoneNr) &&
                        customerManagement.updateCustomerEmail(emailS, mail)
                        ) {
                    showMessageDialog(null, "Customer updated.");
                }
                else{
                    showMessageDialog(null, "Issue with updating customer.");
                }

                // Update customer list
                Customers.updateCustomer();

                // Close window
                setVisible(false);
                dispose();



            });

            // Add Corporate Customer
            cAddCustomerButton.addActionListener(e -> {
                String name1 = cName.getText();
                String mail = cMail.getText();
                String phone = cPhone.getText();
                String adr = cAddress.getText();
                String pc = cPostalCode.getText();
                String pcCity = cCity.getText();

                if(customerManagement.updateCustomerName(emailS,name1) &&
                        customerManagement.updateCustomerAdress(emailS,adr,pc,pcCity) &&
                        customerManagement.updateCustomerPhone(emailS,phone) &&
                        customerManagement.updateCustomerEmail(emailS, mail)){
                    showMessageDialog(null, "Customer updated.");
                }
                else{
                    showMessageDialog(null, "Issue with updating customer.");
                }
                // Update customer list
                Customers.updateCustomer();

                // Close window
                setVisible(false);
                dispose();
            });

            pack();
            setLocationRelativeTo(getParent());
            setModal(true);
            setVisible(true);

        }
    private String[] formatName(String name) {
        String[] out = new String[2];
        String[] splitted = name.split(", ");
        out[0] = splitted[1]; //fornavn, etternavn, bytter rekkefølge.
        out[1] = splitted[0];
        return out;
    }
    private String[] formatAdress(String address) {
        String[] out = new String[3];
        String[] tmpSplit = address.split(", ", 3); //Dont add norway.
        String[] postBox = tmpSplit[1].split(" ");

        out[0] = tmpSplit[0]; //address
        out[1] = postBox[0]; //postnr
        out[2] = postBox[1]; //city
        return out;
    }

}
