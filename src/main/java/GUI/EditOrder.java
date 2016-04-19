package GUI;

import Database.CustomerManagement;
import Database.FoodManagement;
import Database.OrderManagement;
import HelperClasses.DateLabelFormatter;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;

import static GUI.WindowPanels.Orders.updateOrders;
import static javax.swing.JOptionPane.showInputDialog;
import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Created by Evdal on 09.04.2016.
 */
public class EditOrder extends JDialog {

    private JPanel mainPanel;
    private JComboBox<Object> customerDropdown;
    private JTable recipeTable;
    private JList<String> recipesList;
    private JButton leftButton;
    private JButton rightButton;
    private JButton cancelButton;
    private JButton addOrderButton;
    private JTextArea commentTextArea;
    private JComboBox statusDropdown;
    private JTextField searchRecipes;
    private JDatePickerImpl datePicker;
    private JSpinner timeSpinner;
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

    private UtilDateModel model; // DatePicker model


    public EditOrder(int orderId) {
        setTitle("Edit Order");
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Images/icon32.png"));
        setIconImage(icon);

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

        /* Create Recipe List */
        final DefaultListModel<String> recipeModel = new DefaultListModel<>();
        recipesList.setModel(recipeModel);
        recipesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        final ArrayList<Object[]> recipes = foodManagement.getRecipes();

        for (Object[] recipe : recipes) {
            recipeModel.addElement((String)recipe[1]);
        }

        ArrayList<Object> searchList = new ArrayList<>(Arrays.asList(recipeModel.toArray()));
        searchRecipes.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                searchRecipes();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                searchRecipes();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                searchRecipes();
            }

            private void searchRecipes() {
                recipeModel.removeAllElements();
                for (Object o : searchList) {
                    String searchInput = searchRecipes.getText();
                    if (((String)o).toLowerCase().contains(searchInput.toLowerCase())) {
                        recipeModel.addElement((String)o);
                    }
                }
                recipesList.validate();
            }
        });


        //Getting info about selected order.
        Object[] orderInfo = orderManagement.getOrderInfoFromId(orderId);
        ArrayList<Object[]> orderRecipes = orderManagement.getRecipesFromOrder(orderId);

        //Adding info to textboxes.
        String customerName = (String)orderInfo[0];
        customerDropdown.setSelectedItem(customerName);

        // set value of JDatePicker
        String sDate = (String)orderInfo[1];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(sDate, formatter);
        model.setDate(date.getYear(), date.getMonthValue(), date.getDayOfMonth());

        // TimeSpinner
        SpinnerDateModel timeModel = new SpinnerDateModel();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        try {
            timeModel.setValue(timeFormat.parse((String)orderInfo[2]));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        timeSpinner.setModel(timeModel); // TODO: Set default if parse error?
        timeSpinner.setEditor(new JSpinner.DateEditor(timeSpinner, "HH:mm"));

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
                int currentPortions = Integer.parseInt((String)addOrderModel.getValueAt(row, 1));
                if (currentPortions + portions >= 1) {
                    addOrderModel.setValueAt(currentPortions + portions, row, 1);
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
                if(e.getClickCount() == 2) {
                    try {
                        String selectedRecipe = recipesList.getSelectedValue();
                        int portions = Integer.parseInt(showInputDialog("How many portions of " + selectedRecipe.toLowerCase() + " do you want to add?")); // FIXME: Add failsafe for parsing integer
                        if (existsInTable(recipeTable, selectedRecipe) == -1) {
                            addOrderModel.addRow(new Object[]{selectedRecipe, portions});
                        } else {
                            int row = existsInTable(recipeTable, selectedRecipe);
                            int currentPortions = Integer.parseInt((String) addOrderModel.getValueAt(row, 0));
                            if (currentPortions + portions >= 1) {
                                addOrderModel.setValueAt(currentPortions + portions, row, 0);
                            }
                        }
                    } catch (NumberFormatException ne) {
                        //message box cancelled.
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
                    try {
                        if (in != null || !in.equals("")) {
                            int portions = Integer.parseInt(in); // FIXME: Add failsafe for parsing integer
                            addOrderModel.setValueAt(portions, recipeTable.getSelectedRow(), quantityColumnNr);
                        } else {
                            showMessageDialog(null, "You need to input a number.");
                        }
                    }catch(NullPointerException ne){
                        //messagebox cancelled.
                    }
                }
            }
        });


        rightButton.addActionListener(e -> addOrderModel.removeRow(recipeTable.getSelectedRow()));

        //adds
        addOrderButton.addActionListener(e -> {
            Object[] selectedCustomer = customers.get(customerDropdown.getSelectedIndex());
            Date selectedDate = (Date)datePicker.getModel().getValue();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String selectedDateString = format.format(selectedDate);
            Date selectedTime = (Date)timeSpinner.getValue();
            DateFormat tFormat = new SimpleDateFormat("HH:mm:ss");
            String selectedTimeString = tFormat.format(selectedTime);
            String comment = commentTextArea.getText();

            ArrayList<Object[]> selectedRecipes = new ArrayList<>();
            for (int i = 0; i < addOrderModel.getRowCount(); i++) {
                selectedRecipes.add(new Object[]{addOrderModel.getValueAt(i, 1), addOrderModel.getValueAt(i, 0)});
            }

            OrderManagement orderManagement = new OrderManagement();
            boolean isUpdated = orderManagement.createOrder((String)selectedCustomer[1], selectedDateString, selectedRecipes, comment, selectedTimeString);
            if(!isUpdated) {
                showMessageDialog(null, "Issue with editing order.");
            } else {
                updateOrders();
                setVisible(false);
                dispose();
            }
        });

        pack();
        setLocationRelativeTo(getParent());
        setModal(true);
        setVisible(true);
    }

    private void createUIComponents() { // Creates the JDatePicker
        // Date Pickers start
        model = new UtilDateModel();
        Calendar cal = new GregorianCalendar();
        model.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
        model.setSelected(true);
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");

        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);

        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
    }

    private int existsInTable(JTable table, String entry) {
        for (int i = 0; i < table.getRowCount(); i++) {
            if (table.getValueAt(i, 0).equals(entry)) {
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

