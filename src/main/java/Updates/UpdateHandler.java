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

    /**
     * Get index of current active tab. Used for right click handler.
     * @return  Selected tab index
     */
    public static int getCurrTab() {
        return tabbedPane.getSelectedIndex();
    }

    /**
     * Starts timer for Auto-Update
     */
    private static void startTimer() {
        timer.start();
    }

    /**
     * Restarts timer for Auto-Update
     */
    private static void restartTimer() {
        timer.restart();
    }

    /**
     * Finds the title of a specified tab index.
     *
     * @param index Tab index
     * @return      Name of the tab (lowercase)
     */
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

    /**
     * Update code for the different tabs.
     */
    public static void updateTab() {
        // TODO: Do some check for database connetion

        String currentTab = findNameOfTab(tabbedPane.getSelectedIndex());

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
     * Starting a timer with an interval of 5 minutes, auto-updating the current active tab.
     *
     * @param tabbedPane    The JTabbedPane component of the main window.
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
