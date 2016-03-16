package GUI;

import Database.CustomerManagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by olekristianaune on 16.03.2016.
 */
public class AddOrder extends JFrame {

    private JPanel mainPanel;
    private JComboBox customerDropdown;
    private JFormattedTextField dateField;
    private JTable orderRecepies;
    private JList recepies;
    private JButton leftButton;
    private JButton rightButton;
    private JButton cancelButton;
    private JButton addOrderButton;

    public AddOrder(Container parent) {
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainPanel.getRootPane().setDefaultButton(cancelButton);
        pack();
        setLocationRelativeTo(parent);

        /* Create Order Table */
        String[] headers = {"Quantity", "Recipe"};

        DefaultTableModel addOrderModel = new DefaultTableModel();
        addOrderModel.setColumnIdentifiers(headers);

        orderRecepies.setModel(addOrderModel);

        /* Create Customer Dropdown */
        CustomerManagement customerManagement = new CustomerManagement();
        ArrayList<Object[]> customers = customerManagement.getCustomers();
        for (Object[] customer : customers) {
            customerDropdown.addItem(customer[0]);
        }

        try {
            /* FormattedTextField for date, default value set to tomorrow */
            final MaskFormatter maskFormatter = new MaskFormatter("####-##-##"); // Defining format pattern

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Setup date format

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 1);
            Date tomorrowDate = new Date(cal.getTimeInMillis());

            maskFormatter.setPlaceholder(dateFormat.format(tomorrowDate)); // Placeholder

            dateField.setFormatterFactory(new JFormattedTextField.AbstractFormatterFactory() { // Add format to field
                @Override
                public JFormattedTextField.AbstractFormatter getFormatter(JFormattedTextField tf) {
                    return maskFormatter;
                }
            });
        } catch (Exception e) {
            System.err.println(e);
        }


        //createOrder(String customerMail, String date, ArrayList<Object[]> recipes)

        setVisible(true);
    }
}
