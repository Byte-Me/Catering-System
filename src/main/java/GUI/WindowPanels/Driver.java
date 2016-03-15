package GUI.WindowPanels;

import com.teamdev.jxbrowser.chromium.*;
import com.teamdev.jxbrowser.chromium.events.*;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import javax.swing.*;
import java.awt.*;
import java.awt.PrintJob;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.logging.Level;

import static Delivery.CreateDeliveryRoute.UseReadyOrders;
import static Delivery.CreateDeliveryRoute.UseReadyOrdersLatLng;
import static GUI.map.CreateMap.getMap;

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

        createMap(mapPanel, generateDrivingRouteButton);

    }

    public static void updateDrivingRoute() {
        ArrayList<String> orders = UseReadyOrders(cateringAdress);

        for (String order : orders) {
            driverModel.addElement(order);
        }
    }

    public static void createMap(JPanel mapPanel, JButton generateDrivingRouteButton) {
        final Browser browser = new Browser();
        BrowserView browserView = new BrowserView(browser);

        // Reduce logging -- doesn't work?
        LoggerProvider.setLevel(Level.OFF);

        mapPanel.setLayout(new BorderLayout());
        mapPanel.add(browserView, BorderLayout.CENTER);

        browser.loadURL("file:///Users/olekristianaune/Documents/Mine%20Filer/Java/IntelliJ/Catering-System/src/main/java/GUI/map.html");

        generateDrivingRouteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                browser.executeJavaScript(getDrivingRoute());
            }
        });
    }

    private static String getDrivingRoute() {
        ArrayList<double[]> coords = UseReadyOrdersLatLng(cateringAdress);
        try {
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
