package GUI;

import Database.UserManagement;
import GUI.WindowPanels.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static Updates.UpdateHandler.startAutoUpdate;
import static Updates.UpdateHandler.updateTab;
import static javax.swing.JOptionPane.showMessageDialog;

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
    private JToolBar menuBar;
    private JButton settingsButton;
    private JPanel statisticsSearchPanel;
    private JTable inactiveCustomerTable;
    private JButton reactivateCustomerButton;


    public MainWindow(UserManagement.UserType userType) {
        setTitle("Healthy Catering LTD");
        setContentPane(mainPanel); // Set the main content panel
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // Exit application when window is closed.

        Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Images/icon32.png"));
        setIconImage(icon);

        // Setup the different panels - keep referance for possible future need.
        // Will get disposed and garbage collected when MainWindow gets closed (When application is closed)
        Statistics statisticsPanel = new Statistics(statisticsSearchPanel, orderStatisticsPanel, statsPanel, barChartPanel);
        Users usersPanel = new Users(addUserButton, userTable, searchUsers, deleteUsersButton, editUserButton);
        Customers customersPanel = new Customers(addCustomerButton, customerTable, inactiveCustomerTable, searchCustomers, deleteCustomersButton, editCustomerButton, reactivateCustomerButton);
        Subscriptions subscriptionsPanel = new Subscriptions(subscriptionTable, searchSubscriptions, newSubscriptionButton, showEditSubscriptionButton, deleteSubscriptionButton);
        Orders ordersPanel = new Orders(ordersTable, searchOrders, addOrderButton, editOrderButton, deleteOrderButton);
        Driver driverPanel = new Driver(driverTable, mapPanel, generateDrivingRouteButton);
        Chef chefPanel = new Chef(prepareTable, ingredientTable, generateShoppingListButton, recipesButton, addIngredientButton, editIngredientButton);

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

        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(new AbstractAction("User Settings") {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMessageDialog(getParent(), "User Settings"); // FIXME: Change with actual User Settings panel
            }
        });
        if (userType == UserManagement.UserType.ADMIN) {
            popupMenu.add(new AbstractAction("Program Settings") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showMessageDialog(getParent(), "Program Settings"); // FIXME: Change with actual Program Settings panel
                }
            });
        }

        settingsButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                popupMenu.show(settingsButton, 0, settingsButton.getHeight());
            }
        });

        helpButton.addActionListener(e -> new HelpWindow());

        menuBar.setRollover(true);

        pack(); // Pack the window
        setSize(1000, 600); // Set window to desired size
        setLocationRelativeTo(null); // Open window in center of screen

        setVisible(true); // Show the window
    }
}
