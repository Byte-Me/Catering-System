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

        // Reduce logging
        LoggerProvider.setLevel(Level.OFF);

        mapPanel.setLayout(new BorderLayout());
        mapPanel.add(browserView, BorderLayout.CENTER);

        //browser.loadURL("map.html");
        browser.loadURL("file:///Users/olekristianaune/Documents/Mine%20Filer/Java/IntelliJ/Catering-System/src/main/java/GUI/WindowPanels/map.html");

        generateDrivingRouteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                /*
                JSValue document = browser.executeJavaScriptAndReturnValue("document");
                JSValue setStartPoint = document.asObject().getProperty("setStartPoint");
                JSValue setEndPoint = document.asObject().getProperty("setEndPoint");
                JSValue addWaypoint = document.asObject().getProperty("addWaypoint");
                JSValue calcRoute = document.asObject().getProperty("calcRoute");

                setStartPoint.asFunction().invoke(document.asObject(), 51.943571, 6.463856);
                setEndPoint.asFunction().invoke(document.asObject(), 51.947462, 6.467941);
                addWaypoint.asFunction().invoke(document.asObject(), 51.945032, 6.465776);
                addWaypoint.asFunction().invoke(document.asObject(), 51.945538, 6.469413);

                calcRoute.asFunction().invoke(document.asObject());
                */

            }
        });



    }
}
