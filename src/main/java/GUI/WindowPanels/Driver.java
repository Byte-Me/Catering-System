package GUI.WindowPanels;

import HelperClasses.ToggleSelectionModel;
import com.teamdev.jxbrowser.chromium.*;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import static Delivery.CreateDeliveryRoute.UseReadyOrders;
import static Delivery.CreateDeliveryRoute.UseReadyOrdersLatLng;
import static Delivery.DeliveryRoute.geoCoder;
import static GUI.WindowPanels._Map.getMapHTML;

/**
 * Created by olekristianaune on 13.03.2016.
 */
public class Driver {
    private static final String cateringAdress = "Trondheim, Norway";
    static DefaultListModel<String> driverModel;

    Browser browser;
    private static Number[] mapCenter;

    public Driver(final JList<String> drivingList, JPanel mapPanel, JButton generateDrivingRouteButton) {

        driverModel = new DefaultListModel<String>(); // Model of the list
        drivingList.setModel(driverModel); // Add model to jList
        drivingList.setSelectionModel(new ToggleSelectionModel()); // Makes the list toggleable - used for zooming in and out on map

        drivingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Only one row can be selected at a time - makes sure map code works

        // TODO: Change from JList to JTable, show order details instead of addresses (meybe remove addresses from the table completely?)
        updateDrivingRoute(); // Calls function to create the list of addresses in the JList

        try {
            createMap(mapPanel, generateDrivingRouteButton);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Zoom map in to selected address on map - TODO: Make it possible to deselect and then zoom out to full map.
        drivingList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) { // Ensures value changed only fires once on change completed
                    if (drivingList.isSelectionEmpty()) {
                        browser.executeJavaScript("map.panTo(new google.maps.LatLng(" + mapCenter[0] + "," + mapCenter[1] + "));" +
                                "map.setZoom(" + mapCenter[2] + ");");
                    } else {
                        double[] geoLocation = geoCoder(drivingList.getSelectedValue(), 0); // Get the value (ie. address) of the selected row and geocode it

                        browser.executeJavaScript("map.panTo(new google.maps.LatLng(" + geoLocation[0] + "," + geoLocation[1] + "));" +
                                "map.setZoom(14);");
                    }

                }
            }
        });

    }

    public static void updateDrivingRoute() {
        ArrayList<String> orders = UseReadyOrders(cateringAdress);

        // Empties entries of Driver List
        if (driverModel.size() > 0) {
            for (int i = driverModel.size() - 1; i > -1; i--) {
                driverModel.remove(i);
            }
        }

        // Add elements to Driver List
        for (String order : orders) {
            driverModel.addElement(order);
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
        browser.loadHTML(getMapHTML());

        // Generate driving route
        generateDrivingRouteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateDrivingRoute();

                JSValue loaded = browser.executeJavaScriptAndReturnValue(getDrivingRoute());

                // FIXME: The following executes before the new map is generated.
                if(!loaded.isUndefined()) {
                    System.out.println("Map loaded?");
                    double mapLat = browser.executeJavaScriptAndReturnValue("map.getCenter().lat();").getNumberValue();
                    double mapLng = browser.executeJavaScriptAndReturnValue("map.getCenter().lng();").getNumberValue();
                    int mapZoom = (int) browser.executeJavaScriptAndReturnValue("map.getZoom();").getNumberValue();

                    mapCenter = new Number[]{mapLat, mapLng, mapZoom};
                }

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


            String output = "var request = {" +
                    "origin:" + startPoint + "," +
                    "destination:" + endPoint + "," +
                    "waypoints:" + waypts + "," +
                    "optimizeWaypoints:true," +
                    "travelMode: google.maps.TravelMode.DRIVING" +
                    "};" +
                    "directionsService.route(request, function(result, status) {" +
                    "if (status == google.maps.DirectionsStatus.OK) {" +
                    "directionsDisplay.setDirections(result);" +
                    "alert('directions created');" +
                    "}" +
                    "});";

            return output;
        } catch (NullPointerException npe) {
            System.err.println("NullPointerException -- UseReadyOrdersLatLng");
        }
        return "";

    }
}
