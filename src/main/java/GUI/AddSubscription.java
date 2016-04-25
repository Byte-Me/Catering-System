package GUI;

import Database.CustomerManagement;
import Database.FoodManagement;
import Database.OrderManagement;
import Database.SubscriptionManagement;
import GUI.WindowPanels.Driver;
import HelperClasses.DateLabelFormatter;
import Subscription.Subscriptions;
import javafx.scene.layout.Pane;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import javax.swing.text.html.ObjectView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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
    private JTabbedPane dayTabbedPane;
    private JPanel buttonPanel;
    private JButton newSubscriptionButton;
    private JPanel topPanel;
    private JSpinner frequencySpinner;
    private JDatePickerImpl startDatePick;
    private JDatePickerImpl endDatePick;
    private JTextArea commentArea;
    private PanelForSubs testP;
    private PanelForSubs monPanel;
    private PanelForSubs tuePanel;
    private PanelForSubs wedPanel;
    private PanelForSubs thuPanel;
    private PanelForSubs friPanel;
    private PanelForSubs satPanel;
    private PanelForSubs sunPanel;



    private final String defaultTimeValue = "12:00";
    private final String seconds = ":00";
    private final String newCustomer = "+ New customer";
    private final int recipeColumnNr = 0;
    private final int quantityColumnNr = 1;

    private FoodManagement foodManagement = new FoodManagement();
    private CustomerManagement customerManagement = new CustomerManagement();
    private ArrayList<Object[]> customers;

    /**
     * Constructor to the AddSubscription graphical user interface.
     */
    public AddSubscription() {
        setTitle("New Subscription");
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Images/icon32.png"));
        setIconImage(icon);
        setPreferredSize(new Dimension(900,600));

        //fixing only valid numbers in spinner
        SpinnerModel model = new SpinnerNumberModel(1, 1, 100, 1);
        frequencySpinner.setModel(model);
        updateDropdown();


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
            monPanel = new PanelForSubs(1);
            int index = 0;
            if(monCheckBox.isSelected()) {
                dayTabbedPane.insertTab("Monday",null,monPanel.getMainPanel(),null,index);
                dayTabbedPane.setSelectedIndex(index);

            }
            else{
                dayTabbedPane.remove(index);
            }
        });
        tueCheckBox.addActionListener(e ->{
            tuePanel = new PanelForSubs(2);
            if(tueCheckBox.isSelected()) {
                int index =0;
                for(int i = 0; i<dayTabbedPane.getTabCount();i++){
                    if(dayTabbedPane.getTitleAt(i).equals("Monday")){
                        index++;
                    }

                }
                if(index > dayTabbedPane.getTabCount()) index = dayTabbedPane.getTabCount();
                dayTabbedPane.insertTab("Tuesday",null,tuePanel.getMainPanel(),null,index);
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
            wedPanel = new PanelForSubs(3);
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
                dayTabbedPane.insertTab("Wednesday",null,wedPanel.getMainPanel(),null,index);
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
            thuPanel = new PanelForSubs(4);
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
                dayTabbedPane.insertTab("Thursday",null,thuPanel.getMainPanel(),null,index);
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
            friPanel = new PanelForSubs(5);
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
                dayTabbedPane.insertTab("Friday",null,friPanel.getMainPanel(),null,index);
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
            satPanel = new PanelForSubs(6);
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
                dayTabbedPane.insertTab("Saturday",null,satPanel.getMainPanel(),null,index);
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
            sunPanel = new PanelForSubs(7);
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
                dayTabbedPane.insertTab("Sunday",null,sunPanel.getMainPanel(),null,index);
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



        newSubscriptionButton.addActionListener(e -> {


            ArrayList<PanelForSubs> panelList = new ArrayList<PanelForSubs>();
            ArrayList<JCheckBox> checkList = new ArrayList<JCheckBox>();

            Object[] selectedCustomer = customers.get(customerDropdown.getSelectedIndex());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String fromDate = dateFormat.format((Date)startDatePick.getModel().getValue());
            String toDate = dateFormat.format((Date)endDatePick.getModel().getValue());
            int frequency = (Integer)frequencySpinner.getValue();

            //creating lists for different checkboxes and panels

            checkList.add(monCheckBox);
            checkList.add(tueCheckBox);
            checkList.add(wedCheckBox);
            checkList.add(thuCheckBox);
            checkList.add(friCheckBox);
            checkList.add(satCheckBox);
            checkList.add(sunCheckBox);

            panelList.add(monPanel);
            panelList.add(tuePanel);
            panelList.add(wedPanel);
            panelList.add(thuPanel);
            panelList.add(friPanel);
            panelList.add(satPanel);
            panelList.add(sunPanel);

            ArrayList<Object[][]> panelValues = new ArrayList<>();
            for(int i = 0; i<panelList.size();i++){
                if(checkList.get(i).isSelected()){
                    Object[][] info = panelList.get(i).getValues();
                    panelValues.add(info);
                }
            }//FIXME: legger bare inn første dag!!!

            for(Object[][] obj : panelValues){
                for(int i = 0; i<obj.length;i++){
                }
            }
            boolean isAdded = Subscriptions.createSubscription((String)selectedCustomer[1],fromDate,toDate,frequency,panelValues);
            if(!isAdded) {
                showMessageDialog(null, "Could not create subscription.");
            }

            setVisible(false);
            dispose();
            GUI.WindowPanels.Subscriptions.updateSubscriptions();
        });

        pack();
        setLocationRelativeTo(getParent());
        setModal(true);
        setVisible(true);

    }

    /**
     *
     * @param table
     * @param entry
     * @return
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
     *
     */
    private void updateDropdown(){
        customerDropdown.removeAllItems();
        customers = customerManagement.getCustomers();
        for (Object[] customer : customers) {
            customerDropdown.addItem(customer[0]);
        }
        customerDropdown.addItem(newCustomer);

    }

    /**
     *
     */
    private void createUIComponents() {
        // TODO: place custom component creation code here

        // Date Pickers start
        UtilDateModel fModel = new UtilDateModel();
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        fModel.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
        fModel.setSelected(true);
        UtilDateModel tModel = new UtilDateModel();
        cal.add(Calendar.MONTH, 1);
        tModel.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
        tModel.setSelected(true);
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");

        JDatePanelImpl fromPanel = new JDatePanelImpl(fModel, p);
        JDatePanelImpl toPanel = new JDatePanelImpl(tModel, p);

        startDatePick = new JDatePickerImpl(fromPanel, new DateLabelFormatter());
        endDatePick = new JDatePickerImpl(toPanel, new DateLabelFormatter());
    }

}
