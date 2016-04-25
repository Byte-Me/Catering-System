package Updates;

import javax.swing.*;

import java.awt.*;

import static GUI.WindowPanels.Chef.updateIngredients;
import static GUI.WindowPanels.Chef.updatePrepareTable;
import static GUI.WindowPanels.Customers.updateCustomer;
import static GUI.WindowPanels.Customers.updateInactiveCustomer;
//import static GUI.WindowPanels.Driver.u;
import static GUI.WindowPanels.Orders.updateOrders;
import static GUI.WindowPanels.Subscriptions.updateSubscriptions;
import static GUI.WindowPanels.Users.updateInactiveUsers;
import static GUI.WindowPanels.Users.updateUsers;
import static GUI.WindowPanels.Driver.*;

/**
 * Created by olekristianaune on 05.04.2016.
 */
public class UpdateHandler {
    private static Timer timer;
    private static JTabbedPane tabbedPane;
    private static String[] tabs;
    private static boolean autoUpdateStarted = false;

    // Used for right click handler
    public static int getCurrTab() {
        return tabbedPane.getSelectedIndex();
    }

    private static void startTimer() {
        timer.start();
    }

    private static void restartTimer() {
        timer.restart();
    }

    private static String findNameOfTab(int index) {
        tabs = new String[tabbedPane.getTabCount()];
        for (int i = 0; i < tabs.length; i++) {
            tabs[i] = tabbedPane.getTitleAt(i);
        }
        if (tabs[index] != null && index < tabs.length) {
            return tabs[index].toLowerCase();
        }
        return null;
    }

    public static void updateTab() {
        // TODO: Do some check for database connetion

        String currentTab = findNameOfTab(tabbedPane.getSelectedIndex());

        System.out.println("Updating: " + currentTab + " with index: " + getCurrTab());

        switch (currentTab) {
            case "statistics":
                // Statistics - NO AUTO REFRESH HERE!
                break;
            case "users":
                updateUsers();
                updateInactiveUsers();
                break;
            case "customers":
                updateCustomer();
                updateInactiveCustomer();
                break;
            case "subscriptions":
                updateSubscriptions();
                break;
            case "orders":
                updateOrders();
                break;
            case "driver":
                updateDriverTable((String)driverDropdown.getItemAt(driverDropdown.getSelectedIndex()));
                break;
            case "chef":
                updatePrepareTable();
                updateIngredients();
                break;
            default:
                // Something wrong??
                System.err.println("Unknown tab selected which index " + getCurrTab());
        }
    }

    // FIXME: AutoUpdate can cause problems if trying to edit a cell when update happens - either no updates directly in table or handle selected cell before autoUpdating

    /**
     *
     * @param tabbedPane
     */
    public static void startAutoUpdate(JTabbedPane tabbedPane) {
        if(!autoUpdateStarted) {
            UpdateHandler.tabbedPane = tabbedPane;
            timer = new Timer(300000, e -> {
                updateTab();
                restartTimer();
            });
            startTimer();
        }
    }

}
