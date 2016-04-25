package GUI;

import Database.CustomerManagement;
import Database.FoodManagement;
import Database.OrderManagement;
import Util.HelperClasses.DateLabelFormatter;
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
import java.text.SimpleDateFormat;
import java.util.*;

import static GUI.WindowPanels.Orders.updateOrders;
import static javax.swing.JOptionPane.showInputDialog;
import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Created by olekristianaune on 16.03.2016.
 */
public class AddOrder extends JDialog{

    private JPanel mainPanel;
    private JComboBox<Object> customerDropdown;
    private JTable orderRecepies;
    private JList<String> recipesList;
    private JButton leftButton;
    private JButton rightButton;
    private JButton cancelButton;
    private JButton addOrderButton;
    private JTextArea commentTextArea;
    private JTextField searchRecipes;
    private JDatePickerImpl datePicker;
    private JSpinner timeSpinner;

    private final String newCustomer = "+ New customer";
    private final int recipeColumnNr = 0;
    private final int quantityColumnNr = 1;

    private FoodManagement foodManagement = new FoodManagement();
    private CustomerManagement customerManagement = new CustomerManagement();
    private ArrayList<Object[]> customers;

    /**
     * Constructor to the AddOrder graphical user interface.
     */
    public AddOrder() {
        setTitle("New Order");
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

        orderRecepies.setModel(addOrderModel);

        /* Create Customer Dropdown */
        updateDropdown();

        // TimeSpinner
        SpinnerDateModel timeModel = new SpinnerDateModel();
        timeModel.setCalendarField(Calendar.MINUTE);
        timeSpinner.setModel(timeModel);
        timeSpinner.setEditor(new JSpinner.DateEditor(timeSpinner, "HH:mm"));

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

        customerDropdown.addActionListener(e -> { //if value in dropdown is changed
            if (customerDropdown.getSelectedIndex() == customerDropdown.getItemCount()-1) { //if selected value is last index
                new AddCustomer(); //call addCustomer method.
                updateDropdown();

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
                int currentPortions = (Integer)addOrderModel.getValueAt(row, 1);
                if (currentPortions + portions >= 1) {
                    addOrderModel.setValueAt(currentPortions + portions, row, 1);
                }
            }
        });
        recipesList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2){
                    String selectedRecipe = recipesList.getSelectedValue();
                    int portions = 0; // FIXME: Add failsafe for parsing integer
                    try {
                        portions = Integer.parseInt(showInputDialog("How many portions of " + selectedRecipe.toLowerCase() + " do you want to add?"));
                    } catch (NumberFormatException e1) {
                    } catch (HeadlessException e1) {

                    }
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
        orderRecepies.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_DELETE){
                    int[] selected = orderRecepies.getSelectedRows();
                    for(int i =0; i<selected.length;i++){
                        addOrderModel.removeRow(selected[i]);
                    }
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });



        rightButton.addActionListener(e -> addOrderModel.removeRow(orderRecepies.getSelectedRow()));

        addOrderButton.addActionListener(e -> {
            Object[] selectedCustomer = customers.get(customerDropdown.getSelectedIndex());
            Date selectedDate = (Date)datePicker.getModel().getValue();
            DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
            String selectedDateString = dFormat.format(selectedDate);
            Date selectedTime = (Date)timeSpinner.getValue();
            DateFormat tFormat = new SimpleDateFormat("HH:mm:ss");
            String selectedTimeString = tFormat.format(selectedTime);
            String comment = commentTextArea.getText();

            ArrayList<Object[]> selectedRecipes = new ArrayList<>();
            for (int i = 0; i < addOrderModel.getRowCount(); i++) {
                selectedRecipes.add(new Object[]{addOrderModel.getValueAt(i, 1), addOrderModel.getValueAt(i, 0)});
            }

            OrderManagement orderManagement = new OrderManagement();

            boolean isAdded = orderManagement.createOrder((String)selectedCustomer[1], selectedDateString, selectedRecipes, comment, selectedTimeString);
            if(!isAdded) {
                showMessageDialog(null, "Could not add order.");
            }

            updateOrders();

            setVisible(false);
            dispose();
        });

        pack();
        setLocationRelativeTo(getParent());
        setModal(true);
        setVisible(true);
    }

    /**
     * Creates the various UI components.
     */
    private void createUIComponents() { // Creates the JDatePicker
        // Date Pickers start
        UtilDateModel model = new UtilDateModel();
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

    /**
     * Check if an order already exists in the order table.
     * @param table Table that contains the orders.
     * @param entry Entry to check against all the different orders.
     * @return The value of the index where the entry exists.
     */
    private int existsInTable(JTable table, String entry) {
        for (int i = 0; i < table.getRowCount(); i++) {
            if (table.getValueAt(i, 0).equals(entry)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Util.Updates the dropdown menu.
     */
    private void updateDropdown(){
        customerDropdown.removeAllItems();
        customers = customerManagement.getCustomers();
        for (Object[] customer : customers) {
            customerDropdown.addItem(customer[0]);
        }
        customerDropdown.addItem(newCustomer);

    }

}
