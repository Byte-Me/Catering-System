package GUI;

import Database.FoodManagement;
import Database.UserManagement;
import GUI.WindowPanels.*;

import javax.swing.*;

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
    private JList drivingList;
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


    public MainWindow(UserManagement.UserType userType) {
        setContentPane(mainPanel); // Set the main content panel
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit application when window is closed.

        // Setup the different panels - keep referance for possible future need.
        // Will get disposed and garbage collected when MainWindow gets closed (When application is closed)
        Statistics statisticsPanel = new Statistics(fromDate, toDate, getStatisticsButton);
        Users usersPanel = new Users(mainPanel, addUserButton, userTable, searchUsers, deleteUsersButton);
        Customers customersPanel = new Customers(mainPanel, addCustomerButton, customerTable, searchCustomers, deleteCustomersButton);
        Orders ordersPanel = new Orders(ordersTable, searchOrders, addOrderButton);
        Driver driverPanel = new Driver(drivingList, mapPanel, generateDrivingRouteButton);
        Chef chefPanel = new Chef(mainPanel, prepareTable, ingredientTable, generateShoppingListButton, recipesButton, addIngredientButton);

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

        pack(); // Pack the window
        setSize(1000, 600); // Set window to desired size
        setLocationRelativeTo(null); // Open window in center of screen

        setVisible(true); // Show the window
    }
}
