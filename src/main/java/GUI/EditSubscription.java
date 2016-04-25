package GUI;

import Database.CustomerManagement;
import Database.FoodManagement;
import HelperClasses.DateLabelFormatter;
import Subscription.Subscriptions;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Created by evend on 16.03.2016.
 */
public class EditSubscription extends JDialog{

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
    private PanelForSubs testP;
    private PanelForSubs monPanel;
    private PanelForSubs tuePanel;
    private PanelForSubs wedPanel;
    private PanelForSubs thuPanel;
    private PanelForSubs friPanel;
    private PanelForSubs satPanel;
    private PanelForSubs sunPanel;

    ArrayList<PanelForSubs> panelList = new ArrayList<PanelForSubs>();
    ArrayList<JCheckBox> checkList = new ArrayList<JCheckBox>();


    private final String defaultTimeValue = "12:00";
    private final String seconds = ":00";
    private final String newCustomer = "+ New customer";
    private final int recipeColumnNr = 0;
    private final int quantityColumnNr = 1;

    private FoodManagement foodManagement = new FoodManagement();
    private CustomerManagement customerManagement = new CustomerManagement();
    private ArrayList<Object[]> customers;
    private Subscriptions subs = new Subscriptions();

    /**
     * A constructor generating a window with automaticallt
     * filled in input fields needed to edit a subscription.
     *
     * There is some non-dynamic logic used to create the  checkboxes
     * and corresponding order-input fields.
     *
     * Also includes actionlisteners for an add subscription button. Which
     * sends the information to the database.
     *
     * @param subId The id of subscription to be edited, used to fill inn information.
     */
    public EditSubscription(int subId) {
        setTitle("Edit Subscription");
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Images/icon32.png"));
        setIconImage(icon);
        setPreferredSize(new Dimension(900,600));

        //fixing only valid numbers in spinner
        SpinnerModel model = new SpinnerNumberModel(1, 1, 100, 1);
        frequencySpinner.setModel(model);
        updateDropdown();

        //adding to lists for more dynamic coding
        checkList.add(monCheckBox);
        checkList.add(tueCheckBox);
        checkList.add(wedCheckBox);
        checkList.add(thuCheckBox);
        checkList.add(friCheckBox);
        checkList.add(satCheckBox);
        checkList.add(sunCheckBox);




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
        fillSubInfo(subId);

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
            addMonPanel();
        });
        tueCheckBox.addActionListener(e ->{
            addTuePanel();
        });
        wedCheckBox.addActionListener(e ->{
            addWedPanel();
        });
        thuCheckBox.addActionListener(e ->{
            addThuPanel();
        });
        friCheckBox.addActionListener(e ->{
            addFriPanel();
        });
        satCheckBox.addActionListener(e ->{
            addSatPanel();
        });

        sunCheckBox.addActionListener(e ->{
            addSunPanel();
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
            checkList = new ArrayList<>();
            checkList.add(monCheckBox);
            checkList.add(tueCheckBox);
            checkList.add(wedCheckBox);
            checkList.add(thuCheckBox);
            checkList.add(friCheckBox);
            checkList.add(satCheckBox);
            checkList.add(sunCheckBox);

            panelList = new ArrayList<>();
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
            boolean isAdded = Subscriptions.editSubscription((String)selectedCustomer[1],fromDate,toDate,frequency,panelValues, subId);
            if(!isAdded) {
                showMessageDialog(null, "Could not edit subscription.");
            }
            GUI.WindowPanels.Subscriptions.updateSubscriptions();
            setVisible(false);
            dispose();
        });

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


    /**
     * Updates the dropdown of customers, used when window is opened and if a new customer is added.
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
     * Creates JDatePicker with correct dates. Needed to create a custom palette from form.

     */
    private void createUIComponents() {

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

    /**
     * Private method used by the constructor to fill in subscription information.
     * @param id Subscription id to know which subscription to update.
     */
    private void fillSubInfo(int id){
        Object[] info = subs.getSubInfoFromId(id);
        ArrayList<Object[]> panelInfo = (ArrayList<Object[]>)info[0];
        for(Object[] panel : panelInfo) {
            checkList.get((Integer) panel[3] - 1).setSelected(true);
        }
        for(int i =0; i<checkList.size();i++){
            if(checkList.get(i).isSelected()){
                switch(i) {
                    case 0:
                        addMonPanel();
                        break;
                    case 1:
                        addTuePanel();
                        break;
                    case 2:
                        addWedPanel();
                        break;
                    case 3:
                        addThuPanel();
                        break;
                    case 4:
                        addFriPanel();
                        break;
                    case 5:
                        addSatPanel();
                        break;
                    case 6:
                        addSunPanel();
                        break;
                }
            }
        }
        int count = 0;
        for(int i =0;i<panelList.size();i++){
            if(panelList.get(i) != null) {
                panelList.get(i).addValues(panelInfo.get(count)); //panel[3]= day, -1 for index
                count++;
            }
        }
        customerDropdown.setSelectedItem(info[1]);

        // set value of JDatePicker
        String sDate = (String)info[2];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dateFrom = LocalDate.parse(sDate, formatter);
        startDatePick.getModel().setDate(dateFrom.getYear(), dateFrom.getMonthValue(), dateFrom.getDayOfMonth());

        String eDate = (String)info[3];
        LocalDate dateTo = LocalDate.parse(eDate, formatter);
        endDatePick.getModel().setDate(dateTo.getYear(), dateTo.getMonthValue(), dateTo.getDayOfMonth());

        frequencySpinner.getModel().setValue(info[4]);

        newSubscriptionButton.addActionListener(e ->{

        });

    }

    /**
     * private method to add a spesific panel to the window.
     */
    private void addMonPanel(){
        if(monPanel == null) {
            monPanel = new PanelForSubs(1);
        }
        int index = 0;
        if(monCheckBox.isSelected()) {
            dayTabbedPane.insertTab("Monday",null,monPanel.getMainPanel(),null,index);
            panelList.add(monPanel);
            dayTabbedPane.setSelectedIndex(index);

        }
        else{
            dayTabbedPane.remove(index);
        }
    }

    /**
     * private method to add a spesific panel to the window.

     */
    private void addTuePanel(){
        if(tuePanel == null) {
            tuePanel = new PanelForSubs(2);
        }
        if(tueCheckBox.isSelected()) {
            int index =0;
            for(int i = 0; i<dayTabbedPane.getTabCount();i++){
                if(dayTabbedPane.getTitleAt(i).equals("Monday")){
                    index++;
                }

            }
            if(index > dayTabbedPane.getTabCount()) index = dayTabbedPane.getTabCount();
            dayTabbedPane.insertTab("Tuesday",null,tuePanel.getMainPanel(),null,index);
            panelList.add(tuePanel);
            dayTabbedPane.setSelectedIndex(index);

        }
        else{
            for(int i = 0; i<dayTabbedPane.getTabCount();i++) {
                if (dayTabbedPane.getTitleAt(i).equals("Tuesday")) {
                    dayTabbedPane.remove(i);
                }
            }
        }
    }

    /**
     * private method to add a spesific panel to the window.

     */
    private void addWedPanel(){
        if(wedPanel == null) {
            wedPanel = new PanelForSubs(3);
        }
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
            panelList.add(wedPanel);
            dayTabbedPane.setSelectedIndex(index);


        }
        else{
            for(int i = 0; i<dayTabbedPane.getTabCount();i++) {
                if (dayTabbedPane.getTitleAt(i).equals("Wednesday")) {
                    dayTabbedPane.remove(i);
                }
            }
        }
    }

    /**
     * private method to add a spesific panel to the window.

     */
    private void addThuPanel(){
        if(thuPanel == null) {
            thuPanel = new PanelForSubs(4);
        }
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
            panelList.add(thuPanel);
            dayTabbedPane.setSelectedIndex(index);


        }
        else{
            for(int i = 0; i<dayTabbedPane.getTabCount();i++) {
                if (dayTabbedPane.getTitleAt(i).equals("Thursday")) {
                    dayTabbedPane.remove(i);
                }
            }
        }
    }

    /**
     * private method to add a spesific panel to the window.

     */
    private void addFriPanel(){
        if(friPanel == null) {
            friPanel = new PanelForSubs(5);
        }
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
            panelList.add(friPanel);
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
    }

    /**
     * private method to add a spesific panel to the window.

     */
    private void addSatPanel(){
        if(satPanel == null) {
            satPanel = new PanelForSubs(6);
        }
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
            panelList.add(satPanel);
            dayTabbedPane.setSelectedIndex(index);

        }
        else{
            for(int i = 0; i<dayTabbedPane.getTabCount();i++) {
                if (dayTabbedPane.getTitleAt(i).equals("Saturday")) {
                    dayTabbedPane.remove(i);
                }
            }
        }
    }

    /**
     * private method to add a spesific panel to the window.

     */
    private void addSunPanel(){
        if(sunPanel == null) {
            sunPanel = new PanelForSubs(7);
        }
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
            panelList.add(sunPanel);
            dayTabbedPane.setSelectedIndex(index);


        }
        else{
            for(int i = 0; i<dayTabbedPane.getTabCount();i++) {
                if (dayTabbedPane.getTitleAt(i).equals("Sunday")) {
                    dayTabbedPane.remove(i);
                }
            }
        }

    }


}
