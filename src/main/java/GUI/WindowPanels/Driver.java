package GUI.WindowPanels;

import Database.OrderManagement;
import HelperClasses.ToggleSelectionModel;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.LoggerProvider;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import static Delivery.CreateDeliveryRoute.UseReadyOrders;
import static Delivery.CreateDeliveryRoute.UseReadyOrdersLatLng;
import static Delivery.DeliveryRoute.geoCoder;
import static javax.swing.JOptionPane.*;

/**
 * Created by olekristianaune on 13.03.2016.
 */
public class Driver {
    private static final String cateringAdress = "Trondheim, Norway";
    static DefaultTableModel driverModel;
    private OrderManagement orderManagement = new OrderManagement();

    Browser browser;
    private static Number[] mapCenter;

    public Driver(final JTable driverTable, JPanel mapPanel, JButton generateDrivingRouteButton) {

        String[] header = {"ID", "Name", "Phone", "Address","Delivered"}; // Header titles

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

        driverModel.setColumnIdentifiers(header);

        driverTable.setModel(driverModel); // Add model to jTable
        driverTable.setSelectionModel(new ToggleSelectionModel()); // Makes the list toggleable - used for zooming in and out on map

        driverTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Only one row can be selected at a time - makes sure map code works

        //setting column widths
        driverTable.getColumnModel().getColumn(0).setMinWidth(60);
        driverTable.getColumnModel().getColumn(0).setMaxWidth(60);

        //updating orderstatus
        driverModel.addTableModelListener(e ->{
            int count = 0;
            boolean lookingForOrder = true;

            while(count < driverTable.getRowCount() && lookingForOrder){
                if((Boolean)driverTable.getValueAt(count, 4)){

                    int input = showConfirmDialog(null,"Do you want update status for orderID " +driverTable.getValueAt(count, 0)+"?","",YES_NO_OPTION);
                    if(input==YES_OPTION) {
                        orderManagement.updateStatus(Integer.parseInt((String)driverTable.getValueAt(count, 0)), OrderManagement.OrderType.DELIVERED.getValue());
                        updateDrivingRoute();
                        lookingForOrder = false;
                    }
                    else{
                        driverTable.setValueAt(false,count, 4);
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
                    // FIXME
                    /*
                    browser.executeJavaScript("map.panTo(new google.maps.LatLng(" + mapCenter[0] + "," + mapCenter[1] + "));" +
                            "map.setZoom(" + mapCenter[2] + ");");
                    */
                } else {
                    double[] geoLocation = geoCoder((String) driverTable.getValueAt(driverTable.getSelectedRow(), 3), 0); // Get the value (ie. address) of the selected row and geocode it

                    browser.executeJavaScript("map.panTo(new google.maps.LatLng(" + geoLocation[0] + "," + geoLocation[1] + "));" +
                            "map.setZoom(14);");
                }

            }
        });

    }

    public static void updateDrivingRoute() {
        ArrayList<Object[]> orders = UseReadyOrders(cateringAdress);

        // Empties entries of Users table
        driverModel.setRowCount(0);

        // Add users from arraylist to table
        for (Object[] order : orders) {
            order[4] = false;
            driverModel.addRow(order);
        }
    }

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

        // Load website
        browser.loadURL(getClass().getResource("/Map/map.html").toExternalForm());

        // Generate driving route
        generateDrivingRouteButton.addActionListener(e -> {
            updateDrivingRoute();

            JSValue loaded = browser.executeJavaScriptAndReturnValue(getDrivingRoute());

            // FIXME: The following executes before the new map is generated.
            if(!loaded.isUndefined()) {
                double mapLat = browser.executeJavaScriptAndReturnValue("map.getCenter().lat();").getNumberValue();
                double mapLng = browser.executeJavaScriptAndReturnValue("map.getCenter().lng();").getNumberValue();
                int mapZoom = (int) browser.executeJavaScriptAndReturnValue("map.getZoom();").getNumberValue();

                mapCenter = new Number[]{mapLat, mapLng, mapZoom};
            }

        });
    }

    private static String getDrivingRoute() {
        ArrayList<double[]> coords = UseReadyOrdersLatLng(cateringAdress);
        try {
            // TODO - make more robust, coords may be empty and return null !IMPORTANT
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
}