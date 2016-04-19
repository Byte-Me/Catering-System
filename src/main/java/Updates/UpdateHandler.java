package Updates;

import javax.swing.*;

import static GUI.WindowPanels.Chef.updateIngredients;
import static GUI.WindowPanels.Chef.updatePrepareTable;
import static GUI.WindowPanels.Customers.updateCustomer;
import static GUI.WindowPanels.Customers.updateInactiveCustomer;
import static GUI.WindowPanels.Driver.updateDrivingRoute;
import static GUI.WindowPanels.Orders.updateOrders;
import static GUI.WindowPanels.Subscriptions.updateSubscriptions;
import static GUI.WindowPanels.Users.updateInactiveUsers;
import static GUI.WindowPanels.Users.updateUsers;

/**
 * Created by olekristianaune on 05.04.2016.
 */
public class UpdateHandler {
    private static Timer timer;
    private static int currTab;
    private static boolean autoUpdateStarted = false;

    // Used for right click handler
    public static int getCurrTab() {
        return currTab;
    }

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
        // TODO: Do some check for database connetion
        switch (currTab) {
            case 0:
                // Statistics - NO AUTO REFRESH HERE!
                break;
            case 1:
                updateUsers();
                updateInactiveUsers();
                break;
            case 2:
                updateCustomer();
                updateInactiveCustomer();
                break;
            case 3:
                updateSubscriptions();
                break;
            case 4:
                updateOrders();
                break;
            case 5:
                updateDrivingRoute();
                break;
            case 6:
                updatePrepareTable();
                updateIngredients();
                break;
            default:
                // Something wrong??
                System.err.println("Unknown tab selected which index " + currTab);
        }
    }

    // FIXME: AutoUpdate can cause problems if trying to edit a cell when update happens - either no updates directly in table or handle selected cell before autoUpdating
    public static void startAutoUpdate(int tabIndex) {
        if(!autoUpdateStarted) {
            currTab = tabIndex;
            timer = new Timer(300000, e -> {
                System.out.println("AutoUpdated tab " + currTab); // DEBUG
                updateTab();
                restartTimer();
            });
            startTimer();
        }
    }

}
