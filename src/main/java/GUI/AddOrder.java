package GUI;

import Database.CustomerManagement;
import Database.FoodManagement;
import Database.OrderManagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static GUI.WindowPanels.Orders.updateOrders;
import static javax.swing.JOptionPane.showInputDialog;

/**
 * Created by olekristianaune on 16.03.2016.
 */
public class AddOrder extends JFrame {

    private JPanel mainPanel;
    private JComboBox customerDropdown;
    private JFormattedTextField dateField;
    private JTable orderRecepies;
    private JList recipesList;
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

        /* Cancel button */
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });

        /* Create Order Table */
        String[] headers = {"Portions", "Recipe"};

        final DefaultTableModel addOrderModel = new DefaultTableModel();
        addOrderModel.setColumnIdentifiers(headers);

        orderRecepies.setModel(addOrderModel);

        /* Create Customer Dropdown */
        CustomerManagement customerManagement = new CustomerManagement();
        final ArrayList<Object[]> customers = customerManagement.getCustomers();
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

        /* Create Recipe List */
        final DefaultListModel<String> recipeModel = new DefaultListModel<String>();
        recipesList.setModel(recipeModel);
        recipesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        FoodManagement foodManagement = new FoodManagement();
        final ArrayList<Object[]> recipes = foodManagement.getRecipes();

        for (Object[] recipe : recipes) {
            recipeModel.addElement((String)recipe[1]);
        }

        /* Left and Right buttons for adding and removing recipes from orders */
        leftButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedRecipe = (String)recipesList.getSelectedValue();
                int portions = Integer.parseInt(showInputDialog("How many portions of " + selectedRecipe + " do you want to add?")); // FIXME: Add failsafe for parsing integer
                if (existsInTable(orderRecepies, selectedRecipe) == -1) {
                    addOrderModel.addRow(new Object[]{portions, selectedRecipe});
                } else {
                    int row = existsInTable(orderRecepies, selectedRecipe);
                    int currentPortions = (Integer)addOrderModel.getValueAt(row, 0);
                    if (currentPortions + portions >= 1) {
                        addOrderModel.setValueAt(currentPortions + portions, row, 0);
                    }
                }
            }
        });


        rightButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addOrderModel.removeRow(orderRecepies.getSelectedRow());
            }
        });

        addOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] selectedCustomer = customers.get(customerDropdown.getSelectedIndex());
                String selectedDate = dateField.getText();

                ArrayList<Object[]> selectedRecipes = new ArrayList<Object[]>();
                for (int i = 0; i < addOrderModel.getRowCount(); i++) {
                    selectedRecipes.add(new Object[]{(String)addOrderModel.getValueAt(i, 1), (Integer)addOrderModel.getValueAt(i, 0)});
                }

                OrderManagement orderManagement = new OrderManagement();
                boolean isAdded = orderManagement.createOrder((String)selectedCustomer[1], selectedDate, selectedRecipes);
                if(!isAdded) {
                    System.err.println("Kunne ikke legge til order");
                }

                updateOrders();
                setVisible(false);
                dispose();
            }
        });

        setVisible(true);
    }

    private int existsInTable(JTable table, String entry) {
        for (int i = 0; i < table.getRowCount(); i++) {
            if (((String)table.getValueAt(i, 1)).equals(entry)) {
                return i;
            }
        }
        return -1;
    }
}
