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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static GUI.WindowPanels.Orders.updateOrders;
import static javax.swing.JOptionPane.showInputDialog;
import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Created by olekristianaune on 16.03.2016.
 */
public class AddOrder extends JFrame{

    private JPanel mainPanel;
    private JComboBox<Object> customerDropdown;
    private JFormattedTextField dateField;
    private JTable orderRecepies;
    private JList<String> recipesList;
    private JButton leftButton;
    private JButton rightButton;
    private JButton cancelButton;
    private JButton addOrderButton;
    private JTextArea commentTextArea;
    private JFormattedTextField timeField;

    private final String defaultTimeValue = "12:00";
    private final String seconds = ":00";
    private final String newCustomer = "+ New customer";
    private final int recipeColumnNr = 0;
    private final int quantityColumnNr = 1;

    private FoodManagement foodManagement = new FoodManagement();
    private CustomerManagement customerManagement = new CustomerManagement();
    private ArrayList<Object[]> customers;




    public AddOrder(Container parent) {
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(parent);

        /* Cancel button */
        cancelButton.addActionListener(e -> {
            setVisible(false);
            dispose();
        });

        /* Create Order Table */
        String[] headers = {"Recipe", "Portions"};

        final DefaultTableModel addOrderModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };

        addOrderModel.setColumnIdentifiers(headers);

        orderRecepies.setModel(addOrderModel);

        /* Create Customer Dropdown */
        updateDropdown();

        try {
            /* FormattedTextField for date, default value set to tomorrow */
            final MaskFormatter dateMaskFormatter = new MaskFormatter("####-##-##"); // Defining format pattern
            final MaskFormatter timeMaskFormatter = new MaskFormatter("##:##"); // Defining format pattern

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Setup date format

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 1);
            Date tomorrowDate = new Date(cal.getTimeInMillis());

            dateMaskFormatter.setPlaceholder(dateFormat.format(tomorrowDate)); // Placeholder
            timeMaskFormatter.setPlaceholder(defaultTimeValue); // Placeholder

            dateField.setFormatterFactory(new JFormattedTextField.AbstractFormatterFactory() { // Add format to field
                @Override
                public JFormattedTextField.AbstractFormatter getFormatter(JFormattedTextField tf) {
                    return dateMaskFormatter;
                }
            });
            timeField.setFormatterFactory(new JFormattedTextField.AbstractFormatterFactory() { // Add format to field
                @Override
                public JFormattedTextField.AbstractFormatter getFormatter(JFormattedTextField tf) {
                    return timeMaskFormatter;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        /* Create Recipe List */
        final DefaultListModel<String> recipeModel = new DefaultListModel<>();
        recipesList.setModel(recipeModel);
        recipesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        final ArrayList<Object[]> recipes = foodManagement.getRecipes();

        for (Object[] recipe : recipes) {
            recipeModel.addElement((String)recipe[1]);
        }

        customerDropdown.addActionListener(e -> { //if value in dropdown is changed
            if (customerDropdown.getSelectedIndex() == customerDropdown.getItemCount()-1) { //if selected value is last index
                new AddCustomer(mainPanel.getParent()); //call addCustomer method.
                updateDropdown(); //FIXME: Denne oppdaterer for fort? ny kunde vises ikke før den oppdateres senere...
            }

        });
        /* Left and Right buttons for adding and removing recipes from orders */
        leftButton.addActionListener(e -> {
            String selectedRecipe = recipesList.getSelectedValue();
            int portions = Integer.parseInt(showInputDialog("How many portions of " + selectedRecipe.toLowerCase() + " do you want to add?")); // FIXME: Add failsafe for parsing integer
            if (existsInTable(orderRecepies, selectedRecipe) == -1) {
                addOrderModel.addRow(new Object[]{selectedRecipe,portions});
            } else {
                int row = existsInTable(orderRecepies, selectedRecipe);
                int currentPortions = (Integer)addOrderModel.getValueAt(row, 0);
                if (currentPortions + portions >= 1) {
                    addOrderModel.setValueAt(currentPortions + portions, row, 0);
                }
            }
        });
        recipesList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2){
                    String selectedRecipe = recipesList.getSelectedValue();
                    int portions = Integer.parseInt(showInputDialog("How many portions of " + selectedRecipe.toLowerCase() + " do you want to add?")); // FIXME: Add failsafe for parsing integer
                    if (existsInTable(orderRecepies, selectedRecipe) == -1) {
                        addOrderModel.addRow(new Object[]{selectedRecipe,portions});
                    } else {
                        int row = existsInTable(orderRecepies, selectedRecipe);
                        int currentPortions = (Integer)addOrderModel.getValueAt(row, 0);
                        if (currentPortions + portions >= 1) {
                            addOrderModel.setValueAt(currentPortions + portions, row, 0);
                        }
                    }
                }
            }
        });
        orderRecepies.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String recipe = (String) orderRecepies.getValueAt(orderRecepies.getSelectedRow(), recipeColumnNr);
                    String in = showInputDialog("How many portions of " + recipe.toLowerCase() + " do you want?");
                    if(!in.equals("")) {
                        int portions = Integer.parseInt(in); // FIXME: Add failsafe for parsing integer
                        addOrderModel.setValueAt(portions, orderRecepies.getSelectedRow(), quantityColumnNr);
                    }
                    else{
                        showMessageDialog(null, "You need to input a number.");
                    }
                }
            }
        });


        rightButton.addActionListener(e -> addOrderModel.removeRow(orderRecepies.getSelectedRow()));

        addOrderButton.addActionListener(e -> {
            Object[] selectedCustomer = customers.get(customerDropdown.getSelectedIndex());
            String selectedDate = dateField.getText();
            String selectedTime = timeField.getText();
            String comment = commentTextArea.getText();

            ArrayList<Object[]> selectedRecipes = new ArrayList<>();
            for (int i = 0; i < addOrderModel.getRowCount(); i++) {
                selectedRecipes.add(new Object[]{addOrderModel.getValueAt(i, 1), addOrderModel.getValueAt(i, 0)});
            }

            OrderManagement orderManagement = new OrderManagement();
            boolean isAdded = orderManagement.createOrder((String)selectedCustomer[1], selectedDate, selectedRecipes, comment, selectedTime+seconds);
            if(!isAdded) {
                showMessageDialog(null, "Kunne ikke legge til order.");
            }

            updateOrders();
            setVisible(false);
            dispose();
        });

        setVisible(true);
    }

    private int existsInTable(JTable table, String entry) {
        for (int i = 0; i < table.getRowCount(); i++) {
            if (table.getValueAt(i, 1).equals(entry)) {
                return i;
            }
        }
        return -1;
    }
    private void updateDropdown(){
        customerDropdown.removeAllItems();
        customers = customerManagement.getCustomers();
        for (Object[] customer : customers) {
            customerDropdown.addItem(customer[0]);
        }
        customerDropdown.addItem(newCustomer);

    }
}
