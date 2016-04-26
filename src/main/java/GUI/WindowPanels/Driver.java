package GUI.WindowPanels;

import Database.DeliveryManagement;
import Database.OrderManagement;
import Database.SettingsManagement;
import Database.UserManagement;
import Util.Delivery.CreateDeliveryRoute;
import Util.HelperClasses.ToggleSelectionModel;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.LoggerProvider;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import static Util.Delivery.CreateDeliveryRoute.UseReadyOrdersLatLng;
import static Util.Delivery.DeliveryRoute.geoCoder;
import static javax.swing.JOptionPane.*;

/**
 * Created by olekristianaune on 13.03.2016.
 */
public class Driver {
    static SettingsManagement sm = new SettingsManagement();
    private static String address = sm.getSystemAddress();
    private static String city = sm.getSystemCity();
    private static String country = sm.getSystemCountry();
    private static final String cateringAdress =  address + ", " + city +  ", " + country;
    static DefaultTableModel driverModel;
    private OrderManagement orderManagement = new OrderManagement();
    private UserManagement userManagement = new UserManagement();
    private static DeliveryManagement deliveryManagement = new DeliveryManagement();

    Browser browser;

    public static JComboBox driverDropdown;

    private static final String[] readyHeader = {"ID", "Name", "Phone", "Address", "Drive Order"}; // Header titles
    private static final String readyString = "Ready For Delivery";
    private static final int adressColumn = 3;

    private final String limitReachedErrorMessage = "Your limit for amount of deliveries per trip is reached. " +
            "Update orders to delivered to register more orders.";

    private String username;

    /**
     * Construrctor for the window panel Customers.
     * @param driverTable                   JTable containing all the drivers.
     * @param mapPanel                      JPanel containing the map with the plugin JxBrowser.
     * @param generateDrivingRouteButton    JButton that uses an algorythm to generate the best suited driving route.
     * @param driverDropdown                JComboBox with a dropdown menu of all the diferent drivers.
     * @param currentUser                   The current user for a driving route.
     */
    public Driver(final JTable driverTable, JPanel mapPanel, JButton generateDrivingRouteButton, JComboBox driverDropdown, Object[] currentUser) {

        driverModel = new DefaultTableModel(){
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4)
                    return Boolean.class;
                return super.getColumnClass(columnIndex);
            }
            @Override
            public boolean isCellEditable(int row, int col) {
                return (col == 4);
            }
        }; // Model of the table

        username = (String)currentUser[0];
        driverModel.setColumnIdentifiers(readyHeader);

        driverTable.setModel(driverModel); // Add model to jTable
        driverTable.setSelectionModel(new ToggleSelectionModel()); // Makes the list toggleable - used for zooming in and out on map

        driverTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Only one row can be selected at a time - makes sure map code works

        //setting column widths
        driverTable.getColumnModel().getColumn(0).setMinWidth(60);
        driverTable.getColumnModel().getColumn(0).setMaxWidth(60);

        this.driverDropdown = driverDropdown;
        updateDropdown();
        updateDriverTable(readyString);

        //updating orderstatus
        driverModel.addTableModelListener(e ->{
            int count = 0;
            boolean lookingForOrder = true;
            int input = 0;
            while(count < driverTable.getRowCount() && lookingForOrder){
                if((Boolean)driverTable.getValueAt(count, 4)){
                    if(isDrivingLimitReached(username)) {
                        showMessageDialog(null, limitReachedErrorMessage);
                        lookingForOrder = false;
                    }
                    else{
                        OrderManagement.OrderType type;
                        if (driverDropdown.getSelectedIndex() == 0) {
                            type = OrderManagement.OrderType.DRIVING;
                        } else {
                            type = OrderManagement.OrderType.DELIVERED;
                        }
                        input = showConfirmDialog(null, "Do you want update status for orderID " + driverTable.getValueAt(count, 0) + "?", null, YES_NO_OPTION);
                        if (input == YES_OPTION) {
                            int id = Integer.parseInt(driverTable.getValueAt(count, 0).toString());
                            deliveryManagement.connectDriverToOrder(username, id); //username
                            orderManagement.updateStatus(id, type.getValue());
                            updateDriverTable((String) driverDropdown.getItemAt(driverDropdown.getSelectedIndex()));
                            lookingForOrder = false;
                         } else {
                            driverTable.setValueAt(false, count, 4);
                            lookingForOrder = false;
                        }
                    }
                }
                count++;
            }
        });
        try {
            createMap(mapPanel, generateDrivingRouteButton);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Zoom map in to selected address on map - TODO: Make it possible to deselect and then zoom out to full map.
        driverTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // Ensures value changed only fires once on change completed
                if (driverTable.getSelectionModel().isSelectionEmpty()) {
                    // zoom out to full map
                    browser.executeJavaScript(getDrivingRoute());
                } else {
                    double[] geoLocation = geoCoder((String) driverTable.getValueAt(driverTable.getSelectedRow(), 3), 0); // Get the value (ie. address) of the selected row and geocode it

                    browser.executeJavaScript("map.panTo(new google.maps.LatLng(" + geoLocation[0] + "," + geoLocation[1] + "));" +
                            "map.setZoom(14);");
                }

            }
        });
        driverDropdown.addActionListener(e ->{
            //oppdaterer table om driver endres
            driverTable.clearSelection();
            driverTable.getColumnModel().getColumn(4).setHeaderValue( driverDropdown.getSelectedIndex() == 0 ? "Drive Order" : "Delivered");
            String username = (String)driverDropdown.getItemAt(driverDropdown.getSelectedIndex());
            updateDriverTable(username);
            driverTable.updateUI();
        });

    }

    /**
     * Creates a map with the JxBrowser plugin.
     * @param mapPanel JPanel that contains the map.
     * @param generateDrivingRouteButton JButton that generates a driving route.
     * @throws IOException Exception if map is not loaded properly.
     */
    public void createMap(JPanel mapPanel, JButton generateDrivingRouteButton) throws IOException {

        // Reduce logging -- doesn't work?
        LoggerProvider.getChromiumProcessLogger().setLevel(Level.OFF);
        LoggerProvider.getIPCLogger().setLevel(Level.OFF);
        LoggerProvider.getBrowserLogger().setLevel(Level.OFF);

        // Add a JxBrowser
        browser = new Browser();
        BrowserView browserView = new BrowserView(browser);

        // Add browserView to JPanel "mapPanel"
        mapPanel.setLayout(new BorderLayout());
        mapPanel.add(browserView, BorderLayout.CENTER);

        browser.loadURL("http://byte-me.github.io/Catering-System/Map/map.html");

        // Generate driving route
        generateDrivingRouteButton.addActionListener(e -> {
            updateDriverTable((String)driverDropdown.getItemAt(driverDropdown.getSelectedIndex()));

            browser.executeJavaScript(getDrivingRoute());

            updateDriverTableSorted((String)driverDropdown.getItemAt(driverDropdown.getSelectedIndex()));

        });
    }

    /**
     * Get the driving route for the drivers.
     * @return Driving route for driver.
     */
    private static String getDrivingRoute() {

        ArrayList<double[]> coords = UseReadyOrdersLatLng(cateringAdress, getAddresses());
        try {
            String startPoint = "new google.maps.LatLng(" + coords.get(0)[0] + "," + coords.get(0)[1] + ")";
            String endPoint = "new google.maps.LatLng(" + coords.get(coords.size()-1)[0] + "," + coords.get(coords.size()-1)[1] + ")";
            String waypts = "[";
            for (int i = 1; i < coords.size()-2; i++) {
                waypts += "{location:new google.maps.LatLng(" + coords.get(i)[0] + "," + coords.get(i)[1] + "), stopover:true},";
            }
            waypts += "{location:new google.maps.LatLng(" + coords.get(coords.size()-2)[0] + "," + coords.get(coords.size()-2)[1] + "), stopover:true}]";


            return "var request = {" +
                    "origin:" + startPoint + "," +
                    "destination:" + endPoint + "," +
                    "waypoints:" + waypts + "," +
                    "optimizeWaypoints:true," +
                    "travelMode: google.maps.TravelMode.DRIVING" +
                    "};" +
                    "directionsService.route(request, function(result, status) {" +
                    "if (status == google.maps.DirectionsStatus.OK) {" +
                    "directionsDisplay.setDirections(result);" +
                    "}" +
                    "});";
        } catch (NullPointerException npe) {
            System.err.println("NullPointerException -- UseReadyOrdersLatLng");
        }
        return "";

    }

    /**
     * Util.Updates the dropdown menu with drivers from the database.
     */
    private void updateDropdown(){
        driverDropdown.removeAllItems();
        ArrayList<Object[]> drivers = userManagement.getDrivers();
        driverDropdown.addItem(readyString);
        for (Object[] driver : drivers) {
            driverDropdown.addItem(driver[0]);
        }
    }

    /**
     * Util.Updates the driver table specific to the driver.
     * @param username Current driver assigned to the task.
     */
    public static void updateDriverTable(String username){
        driverModel.setRowCount(0);
        ArrayList<Object[]> orders = deliveryManagement.getOrdersForDriver(username);
        for(Object[] order : orders){
            order[order.length-1] = false;
            driverModel.addRow(order);
        }
    }

    /**
     * Util.Updates and sort the driver table specific to the driver.
     * @param username Current driver assigned to the task.
     */
    public static void updateDriverTableSorted(String username){
        //gets orders assigned to the current driver.
        ArrayList<Object[]> orders = deliveryManagement.getOrdersForDriver(username);
        //creates arraylist of adresses and orderIds
        ArrayList<String> addresses = new ArrayList<>();
        ArrayList<Integer> orderIds = new ArrayList<>();
        for(Object[] order : orders){
            addresses.add((String)order[3]); //adress, orderid
            orderIds.add((Integer)order[0]); //adress, orderid

        }
        ArrayList<Object[]> sortedOrders = CreateDeliveryRoute.orderListForTable(cateringAdress,addresses,orderIds);
        driverModel.setRowCount(0);
        for(Object[] order : sortedOrders){
            order[order.length-1] = false;
            driverModel.addRow(order);
        }
    }

    /**
     * Get the addresses from the table.
     * @return List of addresses.
     */
    private static ArrayList<String> getAddresses(){
        ArrayList<String> out = new ArrayList<>();
        for(int i = 0; i<driverModel.getRowCount();i++){
            out.add((String)driverModel.getValueAt(i,adressColumn));
        }
        return out;
    }

    /**
     * Checks if the driving limit is reached for a specific driver.
     * @param username Username of the driver.
     * @return True or false if the driving limit is reached.
     */
    private boolean isDrivingLimitReached(String username){
        int limit = 8;
        int count = deliveryManagement.countDriverDeliveries(username);
        return count >= limit;
    }
}