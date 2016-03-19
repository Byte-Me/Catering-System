package GUI.WindowPanels;

import com.google.common.io.Files;
import com.google.common.base.Charsets;
import com.teamdev.jxbrowser.chromium.*;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import static Delivery.CreateDeliveryRoute.UseReadyOrders;
import static Delivery.CreateDeliveryRoute.UseReadyOrdersLatLng;
import static GUI.WindowPanels._Map.getMapHTML;

/**
 * Created by olekristianaune on 13.03.2016.
 */
public class Driver {
    private static final String cateringAdress = "Trondheim, Norway";
    static DefaultListModel<String> driverModel;

    public Driver(JList<String> drivingList, JPanel mapPanel, JButton generateDrivingRouteButton) {

        driverModel = new DefaultListModel<String>(); // Model of the list
        drivingList.setModel(driverModel); // Add model to jList

        updateDrivingRoute();

        try {
            createMap(mapPanel, generateDrivingRouteButton);
        } catch (Exception e) {
            e.printStackTrace();
        }


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
        final Browser browser = new Browser();
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
                browser.executeJavaScript(getDrivingRoute());
            }
        });
    }

    private static String getDrivingRoute() {
        ArrayList<double[]> coords = UseReadyOrdersLatLng(cateringAdress);
        try {
            // TODO - make more robust, coords may be empty and return null
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
                    "}" +
                    "});";

            return output;
        } catch (NullPointerException npe) {
            System.err.println("NullPointerException -- UseReadyOrdersLatLng");
        }
        return "";

    }
}
