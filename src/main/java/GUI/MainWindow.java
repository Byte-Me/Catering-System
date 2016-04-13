package GUI;

import Database.FoodManagement;
import Database.UserManagement;
import GUI.WindowPanels.*;
import Updates.UpdateHandler;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static Updates.UpdateHandler.startAutoUpdate;
import static Updates.UpdateHandler.updateTab;

/**
 * Created by olekristianaune on 07.03.2016.
 */
public class MainWindow extends JFrame {
    private JPanel mainPanel;
    private JTabbedPane tabbedPane1;
    private JPanel statistics;
    private JPanel driver;
    private JPanel chef;
    private JPanel administration;
    private JPanel sale;
    private JButton addUserButton;
    private JTable userTable;
    private JFormattedTextField fromDate;
    private JFormattedTextField toDate;
    private JButton getStatisticsButton;
    private JButton addCustomerButton;
    private JTable customerTable;
    private JPanel mapPanel;
    private JTable prepareTable;
    private JTable ingredientTable;
    private JButton generateShoppingListButton;
    private JButton recipesButton;
    private JTable ordersTable;
    private JTextField searchOrders;
    private JButton addOrderButton;
    private JTextField searchCustomers;
    private JButton deleteCustomersButton;
    private JTextField searchUsers;
    private JButton deleteUsersButton;
    private JButton generateDrivingRouteButton;
    private JButton addIngredientButton;
    private JButton editOrderButton;
    private JButton deleteOrderButton;
    private JButton helpButton;
    private JButton fileButton;
    private JButton settingsButton;
    private JButton editUserButton;
    private JButton editCustomerButton;
    private JButton editIngredientButton;
    private JTable driverTable;
    private JPanel orderStatisticsPanel;
    private JPanel barChartPanel;
    private JPanel statsPanel;
    private JTable subscriptionTable;
    private JTextField searchSubscriptions;
    private JButton deleteSubscriptionButton;
    private JButton showEditSubscriptionButton;
    private JButton newSubscriptionButton;


    public MainWindow(UserManagement.UserType userType) {
        setContentPane(mainPanel); // Set the main content panel
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // Exit application when window is closed.

        Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Images/icon32.png"));
        setIconImage(icon);

        // Setup the different panels - keep referance for possible future need.
        // Will get disposed and garbage collected when MainWindow gets closed (When application is closed)
        Statistics statisticsPanel = new Statistics(fromDate, toDate, getStatisticsButton, orderStatisticsPanel, statsPanel, barChartPanel);
        Users usersPanel = new Users(addUserButton, userTable, searchUsers, deleteUsersButton, editUserButton);
        Customers customersPanel = new Customers(addCustomerButton, customerTable, searchCustomers, deleteCustomersButton, editCustomerButton);
        Subscriptions subscriptionsPanel = new Subscriptions(subscriptionTable, searchSubscriptions, newSubscriptionButton, showEditSubscriptionButton, deleteSubscriptionButton);
        Orders ordersPanel = new Orders(ordersTable, searchOrders, addOrderButton, editOrderButton, deleteOrderButton);
        Driver driverPanel = new Driver(driverTable, mapPanel, generateDrivingRouteButton);
        Chef chefPanel = new Chef(prepareTable, ingredientTable, generateShoppingListButton, recipesButton, addIngredientButton);

        // Remove panes the user does not have access to
        switch (userType) {
            case ADMIN:
                // Admin have access to everything, therefore remove nothing.
                break;
            case SALE:
                // Sale
                tabbedPane1.remove(administration);
                tabbedPane1.remove(chef);
                tabbedPane1.remove(driver);
                break;
            case CHEF:
                // Chef
                tabbedPane1.remove(statistics);
                tabbedPane1.remove(driver);
                tabbedPane1.remove(administration);
                tabbedPane1.remove(sale);
                break;
            case DRIVER:
                // Driver
                tabbedPane1.remove(statistics);
                tabbedPane1.remove(chef);
                tabbedPane1.remove(administration);
                tabbedPane1.remove(sale);
                break;
            default:
                // For some reason we did not get a valid userType - print error message and close window.
                System.err.println("GUI for UserType " + userType + " not defined.");
                dispose();
        }

        updateTab(tabbedPane1.getSelectedIndex()); // TODO: Check if this is needed, initiate fist tab
        startAutoUpdate(tabbedPane1.getSelectedIndex()); // Start autoUpdate of tabs - TODO: Check interval on timer
        tabbedPane1.addChangeListener(e -> updateTab(tabbedPane1.getSelectedIndex()));

        helpButton.addActionListener(e -> new HelpWindow());

        pack(); // Pack the window
        setSize(1000, 600); // Set window to desired size
        setLocationRelativeTo(null); // Open window in center of screen

        setVisible(true); // Show the window
    }
}
