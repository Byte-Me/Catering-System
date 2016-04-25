package GUI;

import Database.UserManagement.UserType;
import GUI.WindowPanels.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static Util.Updates.UpdateHandler.startAutoUpdate;
import static Util.Updates.UpdateHandler.updateTab;


/**
 * Created by olekristianaune on 07.03.2016.
 */
public class MainWindow extends JFrame {
    private JPanel mainPanel;
    private JTabbedPane tabbedPane1;
    private JPanel statistics;
    private JPanel driver;
    private JPanel chef;
    private JPanel users;
    private JPanel customers;
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
    private JPanel orders;
    private JPanel subscriptions;
    private JTable in;
    private JButton reactivateUserButton;
    private JTable inactiveUserTable;
    private JSplitPane chefSplitPane;
    private JComboBox driverDropdown;
    private JButton fileButton;
    private JButton driverDetailsButton;

    /**
     * Constructor for MainWindow.
     * @param user Object list of users.
     */
    public MainWindow(Object[] user) {
        setTitle("Healthy Catering LTD");
        setContentPane(mainPanel); // Set the main content panel
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // Exit application when window is closed.

        Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Images/icon32.png"));
        setIconImage(icon);

        // Set name of tabs
        statistics.setName("statistics");
        users.setName("users");
        customers.setName("customers");
        subscriptions.setName("subscriptions");
        orders.setName("orders");
        driver.setName("driver");
        chef.setName("chef");

        // Setup the different panels - keep referance for possible future need.
        // Will get disposed and garbage collected when MainWindow gets closed (When application is closed)
        Statistics statisticsPanel = new Statistics(statisticsSearchPanel, orderStatisticsPanel, statsPanel, barChartPanel);
        Users usersPanel = new Users(addUserButton, userTable, inactiveUserTable, searchUsers, deleteUsersButton, editUserButton, reactivateUserButton);
        Customers customersPanel = new Customers(addCustomerButton, customerTable, inactiveCustomerTable, searchCustomers, deleteCustomersButton, editCustomerButton, reactivateCustomerButton);
        Subscriptions subscriptionsPanel = new Subscriptions(subscriptionTable, searchSubscriptions, newSubscriptionButton, showEditSubscriptionButton, deleteSubscriptionButton);
        Orders ordersPanel = new Orders(ordersTable, searchOrders, addOrderButton, editOrderButton, deleteOrderButton);
        Driver driverPanel = new Driver(driverTable, mapPanel, generateDrivingRouteButton, driverDropdown, user);
        Chef chefPanel = new Chef(prepareTable, ingredientTable, generateShoppingListButton, recipesButton, addIngredientButton, editIngredientButton, chefSplitPane);

        // Get tab icon images
        Image statisticsIcon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Images/tabs/statistics.png"));
        Image usersIcon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Images/tabs/users.png"));
        Image customersIcon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Images/tabs/customers.png"));
        Image subscriptionsIcon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Images/tabs/subscriptions.png"));
        Image ordersIcon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Images/tabs/orders.png"));
        Image driverIcon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Images/tabs/driver.png"));
        Image chefIcon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Images/tabs/chef.png"));

        // Set tab icons in correct tabs
        tabbedPane1.setIconAt(tabbedPane1.indexOfComponent(statistics), new ImageIcon(statisticsIcon));
        tabbedPane1.setIconAt(tabbedPane1.indexOfComponent(users), new ImageIcon(usersIcon));
        tabbedPane1.setIconAt(tabbedPane1.indexOfComponent(customers), new ImageIcon(customersIcon));
        tabbedPane1.setIconAt(tabbedPane1.indexOfComponent(subscriptions), new ImageIcon(subscriptionsIcon));
        tabbedPane1.setIconAt(tabbedPane1.indexOfComponent(orders), new ImageIcon(ordersIcon));
        tabbedPane1.setIconAt(tabbedPane1.indexOfComponent(driver), new ImageIcon(driverIcon));
        tabbedPane1.setIconAt(tabbedPane1.indexOfComponent(chef), new ImageIcon(chefIcon));

        // Remove panes the user does not have access to
        switch (UserType.valueOf((int)user[5])) {
            case ADMIN:
                // Admin have access to everything - remove nothing.
                break;
            case SALE:
                // Sale
                tabbedPane1.remove(users);
                tabbedPane1.remove(driver);
                tabbedPane1.remove(chef);
                break;
            case CHEF:
                // Chef
                tabbedPane1.remove(statistics);
                tabbedPane1.remove(users);
                tabbedPane1.remove(customers);
                tabbedPane1.remove(subscriptions);
                tabbedPane1.remove(orders);
                tabbedPane1.remove(driver);
                break;
            case DRIVER:
                // Driver
                tabbedPane1.remove(statistics);
                tabbedPane1.remove(users);
                tabbedPane1.remove(customers);
                tabbedPane1.remove(subscriptions);
                tabbedPane1.remove(orders);
                tabbedPane1.remove(chef);
                break;
            default:
                // For some reason we did not get a valid userType - print error message and close window.
                System.err.println("GUI for UserType " + UserType.valueOf((int)user[6]) + " not defined.");
                dispose();
        }

        JPopupMenu fileMenu = new JPopupMenu();
        fileMenu.add(new AbstractAction("Log Out") {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Login();
                setVisible(false);
                dispose();
            }
        });

        fileButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                fileMenu.show(fileButton, 0, fileButton.getHeight());
            }
        });


        JPopupMenu settingsMenu = new JPopupMenu();
        settingsMenu.add(new AbstractAction("User Settings") {
            @Override
            public void actionPerformed(ActionEvent e) {
                new UserSettings(user);
            }
        });
        if (UserType.valueOf((int)user[5]) == UserType.ADMIN) {
            settingsMenu.add(new AbstractAction("System Settings") {
                // Possibility to change address
                // Also possible to change database?
                // Is this saved locally to file?
                // In that case who gets the updated info?
                // If not, where do we save it?
                @Override
                public void actionPerformed(ActionEvent e) {
                    new SystemSettings();
                }
            });
        }

        settingsButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                settingsMenu.show(settingsButton, 0, settingsButton.getHeight());
            }
        });

        helpButton.addActionListener(e -> new HelpWindow());

        menuBar.setRollover(true);

        // Start loading content
        startAutoUpdate(tabbedPane1); // Start autoUpdate of tabs (every 5 minutes)
        updateTab(); // Initiate update
        tabbedPane1.addChangeListener(e -> updateTab()); // Update on tab change

        pack(); // Pack the window
        setSize(1000, 600); // Set window to desired size
        setLocationRelativeTo(null); // Open window in center of screen
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Set to fullscreen
        setVisible(true); // Show the window
    }
}

