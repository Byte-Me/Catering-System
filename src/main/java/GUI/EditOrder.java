package GUI;

import Database.CustomerManagement;
import Database.FoodManagement;
import Database.OrderManagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import static GUI.WindowPanels.Orders.updateOrders;
import static javax.swing.JOptionPane.showInputDialog;
import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Created by Evdal on 09.04.2016.
 */
public class EditOrder extends JDialog {

    private JPanel mainPanel;
    private JComboBox<Object> customerDropdown;
    private JFormattedTextField dateField;
    private JTable recipeTable;
    private JList<String> recipesList;
    private JButton leftButton;
    private JButton rightButton;
    private JButton cancelButton;
    private JButton addOrderButton;
    private JTextArea commentTextArea;
    private JFormattedTextField timeField;
    private JComboBox statusDropdown;
    private static JComboBox<Object> custDropHelp;

    private final String defaultTimeValue = "12:00";
    private final String seconds = ":00";
    private final String newCustomer = "+ New customer";
    private final int recipeColumnNr = 0;
    private final int quantityColumnNr = 1;
    private FoodManagement foodManagement = new FoodManagement();
    private CustomerManagement customerManagement = new CustomerManagement();
    private ArrayList<Object[]> customers;
    private OrderManagement orderManagement = new OrderManagement();


    public EditOrder(int orderId) {
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainPanel.getRootPane().setDefaultButton(cancelButton);
        pack();
        setLocationRelativeTo(getParent());
        setModal(true);

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

        recipeTable.setModel(addOrderModel);

        /* Create Customer Dropdown */
        updateDropdown();

        // Create Status Dropdown
        for(OrderManagement.OrderType status : OrderManagement.OrderType.values()){
            statusDropdown.addItem(status);
        }

        try {
            /* FormattedTextField for date, default value set to tomorrow */
            final MaskFormatter dateMaskFormatter = new MaskFormatter("####-##-##"); // Defining format pattern
            final MaskFormatter timeMaskFormatter = new MaskFormatter("##:##"); // Defining format pattern



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


        //Getting info about selected order.
        Object[] orderInfo = orderManagement.getOrderInfoFromId(orderId);
        ArrayList<Object[]> orderRecipes = orderManagement.getRecipesFromOrder(orderId);

        //Adding info to textboxes.
        String customerName = (String)orderInfo[0];
        customerDropdown.setSelectedItem(customerName);
        dateField.setText((String)orderInfo[1]);
        timeField.setText((String)orderInfo[2]);
        commentTextArea.setText((String)orderInfo[3]);
        statusDropdown.setSelectedItem(orderInfo[4]);

        //Adding recipes to table
        for(Object[] recipe : orderRecipes) {
            addOrderModel.addRow(recipe);
        }

        //Adds a customer if new customer is selected
        customerDropdown.addActionListener(e -> { //if value in dropdown is changed
            if (customerDropdown.getSelectedIndex() == customerDropdown.getItemCount()-1) { //if selected value is last index
                new AddCustomer(); //call addCustomer method.
                updateDropdown(); //TODO: Move updateDropdown to addCustomer, static methods needs to be fixed first.
            }

        });
        /* Left and Right buttons for adding and removing recipes from orders */
        leftButton.addActionListener(e -> {
            String selectedRecipe = recipesList.getSelectedValue();
            int portions = Integer.parseInt(showInputDialog("How many portions of " + selectedRecipe.toLowerCase() + " do you want to add?")); // FIXME: Add failsafe for parsing integer
            if (existsInTable(recipeTable, selectedRecipe) == -1) {
                addOrderModel.addRow(new Object[]{selectedRecipe,portions});
            } else {
                int row = existsInTable(recipeTable, selectedRecipe);
                int currentPortions = (Integer)addOrderModel.getValueAt(row, 0);
                if (currentPortions + portions >= 1) {
                    addOrderModel.setValueAt(currentPortions + portions, row, 0);
                }
            }
        });

        //Deletes recipes from recipeTable when recipe is selected and delete-key pressed.
        recipeTable.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_DELETE){
                    int[] selected = recipeTable.getSelectedRows();
                    for(int i =0; i<selected.length;i++){ //kanskje legge inn failsafe
                        addOrderModel.removeRow(selected[i]);
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        //does same as left-button when doubleclick is heard.
        recipesList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2){
                    String selectedRecipe = recipesList.getSelectedValue();
                    int portions = Integer.parseInt(showInputDialog("How many portions of " + selectedRecipe.toLowerCase() + " do you want to add?")); // FIXME: Add failsafe for parsing integer
                    if (existsInTable(recipeTable, selectedRecipe) == -1) {
                        addOrderModel.addRow(new Object[]{selectedRecipe,portions});
                    } else {
                        int row = existsInTable(recipeTable, selectedRecipe);
                        int currentPortions = (Integer)addOrderModel.getValueAt(row, 0);
                        if (currentPortions + portions >= 1) {
                            addOrderModel.setValueAt(currentPortions + portions, row, 0);
                        }
                    }
                }
            }
        });
        //updates portions when double-click is heard.
        recipeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String recipe = (String) recipeTable.getValueAt(recipeTable.getSelectedRow(), recipeColumnNr);
                    String in = showInputDialog("How many portions of " + recipe.toLowerCase() + " do you want?");
                    if(!in.equals("")) {
                        int portions = Integer.parseInt(in); // FIXME: Add failsafe for parsing integer
                        addOrderModel.setValueAt(portions, recipeTable.getSelectedRow(), quantityColumnNr);
                    }
                    else{
                        showMessageDialog(null, "You need to input a number.");
                    }
                }
            }
        });


        rightButton.addActionListener(e -> addOrderModel.removeRow(recipeTable.getSelectedRow()));

        //adds
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
                showMessageDialog(null, "Issue with editing order.");
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
    public void updateDropdown(){
        customerDropdown.removeAllItems();
        customers = customerManagement.getCustomers();
        for (Object[] customer : customers) {
            customerDropdown.addItem(customer[0]);
        }
        customerDropdown.addItem(newCustomer);

    }

}

