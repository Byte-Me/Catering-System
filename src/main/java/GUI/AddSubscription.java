package GUI;

import Database.CustomerManagement;
import Database.FoodManagement;
import Database.OrderManagement;
import GUI.WindowPanels.Driver;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
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
public class AddSubscription extends JDialog{

    private JPanel mainPanel;
    private JButton cancelButton;
    private JCheckBox monCheckBox;
    private JCheckBox tueCheckBox;
    private JCheckBox wedCheckBox;
    private JCheckBox thuCheckBox;
    private JCheckBox sunCheckBox;
    private JCheckBox satCheckBox;
    private JCheckBox friCheckBox;
    private JComboBox customerDropdown;
    private JButton createSubscriptionButton;
    private JTabbedPane dayTabbedPane;
    private JFormattedTextField startDateField;
    private JFormattedTextField endDateField;
    private PanelForSubs testP;
    private PanelForSubs test;
    private JPanel monPanel;
    private JPanel tuePanel;
    private JPanel wedPanel;

    private final String defaultTimeValue = "12:00";
    private final String seconds = ":00";
    private final String newCustomer = "+ New customer";
    private final int recipeColumnNr = 0;
    private final int quantityColumnNr = 1;

    private FoodManagement foodManagement = new FoodManagement();
    private CustomerManagement customerManagement = new CustomerManagement();
    private ArrayList<Object[]> customers;




    public AddSubscription() {
        setTitle("New Subscription");
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Images/icon32.png"));
        setIconImage(icon);

        /* Cancel button */
        cancelButton.addActionListener(e -> {
            setVisible(false);
            dispose();
        });

        final DefaultTableModel addSubscriptionModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };

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

            startDateField.setFormatterFactory(new JFormattedTextField.AbstractFormatterFactory() { // Add format to field
                @Override
                public JFormattedTextField.AbstractFormatter getFormatter(JFormattedTextField tf) {
                    return dateMaskFormatter;
                }
            });

            endDateField.setFormatterFactory(new JFormattedTextField.AbstractFormatterFactory() { // Add format to field
                @Override
                public JFormattedTextField.AbstractFormatter getFormatter(JFormattedTextField tf) {
                    return dateMaskFormatter;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        customerDropdown.addActionListener(e -> { //if value in dropdown is changed
            if (customerDropdown.getSelectedIndex() == customerDropdown.getItemCount()-1) { //if selected value is last index
                new AddCustomer(); //call addCustomer method.
                updateDropdown(); //FIXME: Denne oppdaterer for fort? ny kunde vises ikke før den oppdateres senere...
            }
        });

        /*
            checkbox listeners
         */


        monCheckBox.addActionListener(e ->{
            JPanel day = new PanelForSubs();
          //  day.setOpaque(false);
            int index = 0;
            JPanel panel = new JPanel();
            panel.add(new JLabel("Dette funket"));
            if(monCheckBox.isSelected()) {
      //          dayTabbedPane.insertTab("Monday",null,panel,null,index);
                dayTabbedPane.insertTab("Monday",null,monPanel,null,index);
             /*   dayTabbedPane.setTabComponentAt(index, day);
                dayTabbedPane.setTitleAt(index, "Monday");
                dayTabbedPane.setSelectedIndex(index);*/


            }
            else{
                dayTabbedPane.remove(index);
            }
        });
        tueCheckBox.addActionListener(e ->{
            JPanel day = new PanelForSubs();
            if(tueCheckBox.isSelected()) {
                int index =0;
                for(int i = 0; i<dayTabbedPane.getTabCount();i++){
                    if(dayTabbedPane.getTitleAt(i).equals("Monday")){
                        index++;
                    }

                }
                if(index > dayTabbedPane.getTabCount()) index = dayTabbedPane.getTabCount();
                dayTabbedPane.insertTab("Tuesday",null,monPanel,null,index);


                dayTabbedPane.setSelectedIndex(index);

            }
            else{
                for(int i = 0; i<dayTabbedPane.getTabCount();i++) {
                    if (dayTabbedPane.getTitleAt(i).equals("Tuesday")) {
                        dayTabbedPane.remove(i);
                    }
                }
            }
        });
        wedCheckBox.addActionListener(e ->{
            Component day = new PanelForSubs();
            if(wedCheckBox.isSelected()) {
                int index =0;
                for(int i = 0; i<dayTabbedPane.getTabCount();i++){
                    if(dayTabbedPane.getTitleAt(i).equals("Monday")){
                        index++;
                    }
                    if(dayTabbedPane.getTitleAt(i).equals("Tuesday")){
                        index++;
                    }

                }
                if(index > dayTabbedPane.getTabCount()) index = dayTabbedPane.getTabCount();
                dayTabbedPane.add(day,index);
                dayTabbedPane.setTitleAt(index, "Wednesday");
                dayTabbedPane.setSelectedIndex(index);

            }
            else{
                for(int i = 0; i<dayTabbedPane.getTabCount();i++) {
                    if (dayTabbedPane.getTitleAt(i).equals("Wednesday")) {
                        dayTabbedPane.remove(i);
                    }
                }
            }
        });
        thuCheckBox.addActionListener(e ->{
            Component day = new PanelForSubs();
            if(thuCheckBox.isSelected()){
                int index =0;
                for(int i = 0; i<dayTabbedPane.getTabCount();i++){
                    if(dayTabbedPane.getTitleAt(i).equals("Monday")){
                        index++;
                    }
                    if(dayTabbedPane.getTitleAt(i).equals("Tuesday")){
                        index++;
                    }
                    if(dayTabbedPane.getTitleAt(i).equals("Wednesday")){
                        index++;
                    }

                }
                if(index > dayTabbedPane.getTabCount()) index = dayTabbedPane.getTabCount();
                dayTabbedPane.add(day,index);
                dayTabbedPane.setTitleAt(index, "Thursday");
                dayTabbedPane.setSelectedIndex(index);

            }
        else{
            for(int i = 0; i<dayTabbedPane.getTabCount();i++) {
                if (dayTabbedPane.getTitleAt(i).equals("Thursday")) {
                    dayTabbedPane.remove(i);
                }
            }
        }
        });
        friCheckBox.addActionListener(e ->{
            Component day = new PanelForSubs();
            if(friCheckBox.isSelected()){
                int index =0;
                for(int i = 0; i<dayTabbedPane.getTabCount();i++){
                    if(dayTabbedPane.getTitleAt(i).equals("Monday")){
                        index++;
                    }
                    if(dayTabbedPane.getTitleAt(i).equals("Tuesday")){
                        index++;
                    }
                    if(dayTabbedPane.getTitleAt(i).equals("Wednesday")){
                        index++;
                    }
                    if(dayTabbedPane.getTitleAt(i).equals("Thursday")){
                        index++;
                    }

                }
                if(index > dayTabbedPane.getTabCount()) index = dayTabbedPane.getTabCount();
                dayTabbedPane.add(day,index);
                dayTabbedPane.setTitleAt(index, "Friday");
                dayTabbedPane.setSelectedIndex(index);

            }
            else{
                for(int i = 0; i<dayTabbedPane.getTabCount();i++) {
                    if (dayTabbedPane.getTitleAt(i).equals("Friday")) {
                        dayTabbedPane.remove(i);
                    }
                }
            }
            dayTabbedPane.revalidate();
        });
        satCheckBox.addActionListener(e ->{
            Component day = new PanelForSubs();
            if(satCheckBox.isSelected()){
                int index =0;
                for(int i = 0; i<dayTabbedPane.getTabCount();i++){
                    if(dayTabbedPane.getTitleAt(i).equals("Monday")){
                        index++;
                    }
                    if(dayTabbedPane.getTitleAt(i).equals("Tuesday")){
                        index++;
                    }
                    if(dayTabbedPane.getTitleAt(i).equals("Wednesday")){
                        index++;
                    }
                    if(dayTabbedPane.getTitleAt(i).equals("Thursday")){
                        index++;
                    }
                    if(dayTabbedPane.getTitleAt(i).equals("Friday")){
                        index++;
                    }

                }
                if(index > dayTabbedPane.getTabCount()) index = dayTabbedPane.getTabCount();
                dayTabbedPane.add(day,index);
                dayTabbedPane.setTitleAt(index, "Saturday");
                dayTabbedPane.setSelectedIndex(index);
            }
            else{
                for(int i = 0; i<dayTabbedPane.getTabCount();i++) {
                    if (dayTabbedPane.getTitleAt(i).equals("Saturday")) {
                        dayTabbedPane.remove(i);
                    }
                }
            }
        });
        sunCheckBox.addActionListener(e ->{
            Component day = new PanelForSubs();
            if(sunCheckBox.isSelected()){
                int index =0;
                for(int i = 0; i<dayTabbedPane.getTabCount();i++){
                    if(dayTabbedPane.getTitleAt(i).equals("Monday")){
                        index++;
                    }
                    if(dayTabbedPane.getTitleAt(i).equals("Tuesday")){
                        index++;
                    }
                    if(dayTabbedPane.getTitleAt(i).equals("Wednesday")){
                        index++;
                    }
                    if(dayTabbedPane.getTitleAt(i).equals("Thursday")){
                        index++;
                    }
                    if(dayTabbedPane.getTitleAt(i).equals("Friday")){
                        index++;
                    }
                    if(dayTabbedPane.getTitleAt(i).equals("Saturday")){
                        index++;
                    }

                }
                if(index > dayTabbedPane.getTabCount()) index = dayTabbedPane.getTabCount();
                dayTabbedPane.add(day,index);
                dayTabbedPane.setTitleAt(index, "Sunday");
                dayTabbedPane.setSelectedIndex(index);

            }
            else{
                for(int i = 0; i<dayTabbedPane.getTabCount();i++) {
                    if (dayTabbedPane.getTitleAt(i).equals("Sunday")) {
                        dayTabbedPane.remove(i);
                    }
                }
            }
        });

        /* Left and Right buttons for adding and removing recipes from orders */
/*
        createSubscriptionButton.addActionListener(e -> {
            Object[] selectedCustomer = customers.get(customerDropdown.getSelectedIndex());
            String selectedDate = startDateField.getText();
            String selectedTime = endDateField.getText();

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
*/
        pack();
        setLocationRelativeTo(getParent());
        setModal(true);
        setVisible(true);
    }

    private int existsInTable(JTable table, String entry) {
        for (int i = 0; i < table.getRowCount(); i++) {
            if (table.getValueAt(i, 0).equals(entry)) {
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

    private void createUIComponents() {
        // TODO: place custom component creation code here
        testP = new PanelForSubs();

    }
}
