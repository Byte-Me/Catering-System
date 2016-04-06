package Updates;

import javax.swing.*;

import static GUI.WindowPanels.Customers.updateCustomer;
import static GUI.WindowPanels.Driver.updateDrivingRoute;
import static GUI.WindowPanels.Orders.updateOrders;
import static GUI.WindowPanels.Users.updateUsers;

/**
 * Created by olekristianaune on 05.04.2016.
 */
public class UpdateHandler {
    private static Timer timer;
    private static int currTab;
    private static boolean autoUpdateStarted = false;

    private static void startTimer() {
        timer.start();
    }

    private static void restartTimer() {
        timer.restart();
    }

    // Used for updates from the GUI
    public static void updateTab(int tabIndex) {
        currTab = tabIndex;
        updateTab();
    }

    private static void updateTab() {
        switch (currTab) {
            case 0:
                // Statistics - NO AUTO REFRESH HERE!
                break;
            case 1:
                updateUsers();
                break;
            case 2:
                updateCustomer();
                break;
            case 3:
                updateOrders();
                break;
            case 4:
                updateDrivingRoute();
                break;
            case 5:
                // Some function
                break;
            default:
                // Something wrong??
                System.err.println("Unknown tab selected whith index " + currTab);
        }
    }

    public static void startAutoUpdate(int tabIndex) {
        if(!autoUpdateStarted) {
            currTab = tabIndex;
            timer = new Timer(30000, e -> {
                System.out.println("AutoUpdated tab " + currTab);
                updateTab();
                restartTimer();
            });
            startTimer();
        }
    }

}
