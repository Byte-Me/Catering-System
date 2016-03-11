package GUI;

import Database.CustomerManagement;
import Database.UserManagement;
import HelperClasses.TableCellListener;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.MaskFormatter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Enumeration;

import static javax.swing.JOptionPane.*;

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
    private JButton addRecipeButton;
    private JTable table1;
    private JButton searchOrdersButton;
    private JTextField serachOrders;
    private JButton addOrderButton;
    private JTextField searchCustomers;
    private JButton deleteCustomersButton;
    private JTextField searchUsers;
    private JButton deleteUsersButton;

    private static DefaultTableModel userModel;
    private static DefaultTableModel customerModel;
    private static DefaultListModel<String> driverModel;
    private static DefaultTableModel prepareModel;
    private static DefaultTableModel ingredientModel;

    private static UserManagement userManagement = new UserManagement();
    private static CustomerManagement customerManagement = new CustomerManagement();

    public MainWindow(int userType) {
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create GUI for specific user type
        switch (userType) {
            case 0:
                // Admin have access to everything
                break;
            case 1:
                // Sale
                tabbedPane1.remove(administration);
                tabbedPane1.remove(chef);
                tabbedPane1.remove(driver);
                break;
            case 2:
                // Chef
                tabbedPane1.remove(statistics);
                tabbedPane1.remove(driver);
                tabbedPane1.remove(administration);
                tabbedPane1.remove(sale);
                break;
            case 3:
                // Driver
                tabbedPane1.remove(statistics);
                tabbedPane1.remove(chef);
                tabbedPane1.remove(administration);
                tabbedPane1.remove(sale);
                break;
            default:
        }

        // Setup the different panels
        setupAdministration();
        setupStatistics();
        setupCustomer();
        setupOrders();
        setupDriver();
        setupChef();

        pack();
        setSize(1000, 600);
        setLocationRelativeTo(null);

        setVisible(true);
    }

    // Create Users Pane
    private void setupAdministration() {

        addUserButton.addActionListener(new ActionListener() { // Button action listener
            public void actionPerformed(ActionEvent e) {
                new AddUser(mainPanel.getParent());
            }
        });

        String[] header = {"First Name", "Last Name", "Email", "Phone", "Username", "User Type"}; // Header titles

        userModel = new DefaultTableModel(); // Model of the table
        userModel.setColumnIdentifiers(header); // Add header to columns

        userTable.setModel(userModel); // Add model to table
        userTable.setAutoCreateRowSorter(true); // Auto sort table by row

        // What happens when a cell in the table is changed?
        Action action = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                int usernameColumn = 4;
                TableCellListener tcl = (TableCellListener)e.getSource();

                int option = showOptionDialog(null,
                        "Change " + userModel.getColumnName(tcl.getColumn()) + " from '" + tcl.getOldValue() + "' to '" + tcl.getNewValue() + "'?",
                        "Edit " + userModel.getColumnName(tcl.getColumn()),
                        YES_NO_OPTION,
                        INFORMATION_MESSAGE,
                        null,
                        new Object[]{"Yes", "No"},
                        "No");

                // If yes, ubdate database
                if (option == 0) {
                    switch (tcl.getColumn()) {
                        case 0:
                            userManagement.updateUserInfoFName((String)userModel.getValueAt(tcl.getRow(), usernameColumn), (String)tcl.getNewValue());
                            break;
                        case 1:
                            userManagement.updateUserInfoLName((String)userModel.getValueAt(tcl.getRow(), usernameColumn), (String)tcl.getNewValue());
                            break;
                        case 2:
                            userManagement.updateUserInfoEmail((String)userModel.getValueAt(tcl.getRow(), usernameColumn), (String)tcl.getNewValue());
                            break;
                        case 3:
                            userManagement.updateUserInfoPhone((String)userModel.getValueAt(tcl.getRow(), usernameColumn), (String)tcl.getNewValue());
                            break;
                        case 4:
                            userManagement.updateUserInfoUsername((String)userModel.getValueAt(tcl.getRow(), usernameColumn), (String)tcl.getNewValue());
                            break;
                        case 5:
                            userManagement.updateUserInfoAccessLevel((String)userModel.getValueAt(tcl.getRow(), usernameColumn), Integer.parseInt((String)tcl.getNewValue()));
                            break;
                        default:
                            System.err.println(userTable.getColumnName(tcl.getColumn()) + " does not yet have an implemetation.");
                    }

                }

                // Update user table from database
                updateUsers();
            }
        };
        TableCellListener tcl = new TableCellListener(userTable, action);


        // Serach field input changed?
        searchUsers.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                searchFieldChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                searchFieldChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                searchFieldChange();
            }

            private void searchFieldChange() {
                String searchTerm = searchUsers.getText();

                ArrayList<Object[]> searchResult = userManagement.userSearch(searchTerm);

                updateUsers(searchResult);
            }
        });

        updateUsers();

    }

    // Update Users function
    public static void updateUsers() {

        // Get users from database
        ArrayList<Object[]> users = userManagement.userInfo();

        // Empties entries of Users table
        if (userModel.getRowCount() > 0) {
            for (int i = userModel.getRowCount() - 1; i > -1; i--) {
                userModel.removeRow(i);
            }
        }

        // Add users from arraylist to table
        for (Object[] user : users) {
            userModel.addRow(user);
        }
    }

    public static void updateUsers(ArrayList<Object[]> users) {

        // Empties entries of Users table
        if (userModel.getRowCount() > 0) {
            for (int i = userModel.getRowCount() - 1; i > -1; i--) {
                userModel.removeRow(i);
            }
        }

        // Add users from arraylist to table
        for (Object[] user : users) {
            userModel.addRow(user);
        }
    }

    // Creating Customer Pane
    private void setupCustomer() {

        addCustomerButton.addActionListener(new ActionListener() { // Button action listener
            public void actionPerformed(ActionEvent e) {
                new AddCustomer(mainPanel.getParent());
            }
        });

        String[] header = {"Name", "Email", "Phone", "Address"}; // Header titles

        customerModel = new DefaultTableModel(); // Model of the table
        customerModel.setColumnIdentifiers(header); // Add header to columns

        customerTable.setModel(customerModel); // Add model to table

        // Serach field input changed?
        searchCustomers.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                searchFieldChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                searchFieldChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                searchFieldChange();
            }

            private void searchFieldChange() {
                String searchTerm = searchCustomers.getText();

                ArrayList<Object[]> searchResult = customerManagement.customerSearch(searchTerm);

                updateCustomer(searchResult);
            }
        });

        updateCustomer();

    }

    // Update Users function
    public static void updateCustomer() {

        // Get users from database
        ArrayList<Object[]> customers = customerManagement.getCustomers();

        // Empties entries of Users table
        if (customerModel.getRowCount() > 0) {
            for (int i = customerModel.getRowCount() - 1; i > -1; i--) {
                customerModel.removeRow(i);
            }
        }

        // Add users from arraylist to table
        for (Object[] customer : customers) {
            customerModel.addRow(customer);
        }
    }

    public static void updateCustomer(ArrayList<Object[]> customers) {

        // Empties entries of Users table
        if (customerModel.getRowCount() > 0) {
            for (int i = customerModel.getRowCount() - 1; i > -1; i--) {
                customerModel.removeRow(i);
            }
        }

        // Add users from arraylist to table
        for (Object[] customer : customers) {
            customerModel.addRow(customer);
        }
    }

    // Create Orders Pane
    public void setupOrders() {

    }

    // Creating Statistics Pane
    private void setupStatistics() {

        try {
            final MaskFormatter maskFormatter = new MaskFormatter("##/##/####"); // Defining format pattern
            //maskFormatter.setPlaceholderCharacter('_');
            maskFormatter.setPlaceholder("00-00-0000"); // Placeholder

            fromDate.setFormatterFactory(new JFormattedTextField.AbstractFormatterFactory() { // Add format to field
                @Override
                public JFormattedTextField.AbstractFormatter getFormatter(JFormattedTextField tf) {
                    return maskFormatter;
                }
            });

            toDate.setFormatterFactory(new JFormattedTextField.AbstractFormatterFactory() { // Add format to field
                @Override
                public JFormattedTextField.AbstractFormatter getFormatter(JFormattedTextField tf) {
                    return maskFormatter;
                }
            });

        } catch(Exception e) {
            System.err.println(e);
        }


        getStatisticsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String fDate = fromDate.getText();
                String tDate = toDate.getText();

                System.out.println("From: " + fDate + " To: " + tDate);
            }
        });


    }

    private void setupDriver() {

        driverModel = new DefaultListModel<String>(); // Model of the list
        drivingList.setModel(driverModel); // Add model to jList

        // TODO - testdata (remove)
        driverModel.addElement("Some Curporation LTD    Adresseveien 4, Sted, Land  91482099");

    }

    private void setupChef() {

        String[] prepareHeader = {"Quantity", "Recipe", "Notes", "Ready for delivery"}; // Header titles
        String[] ingredientHeader = {"Ingredient", "Quantity", "Unit"}; // Header titles

        prepareModel = new DefaultTableModel() {
            public Class<?> getColumnClass(int columnIndex) {
                return getValueAt(0, columnIndex).getClass();
            }
        }; // Model of the table
        ingredientModel = new DefaultTableModel(); // Model of the table

        prepareModel.setColumnIdentifiers(prepareHeader); // Add header to columns
        ingredientModel.setColumnIdentifiers(ingredientHeader); // Add header to columns

        prepareTable.setModel(prepareModel); // Add model to table
        ingredientTable.setModel(ingredientModel); // Add model to table



        // TODO - testdata (remove)
        prepareModel.addRow(new Object[]{3, "Paella", "Without seafood", false});

        // TODO - testdata (remove)
        ingredientModel.addRow(new Object[]{"Suagr", 200, "grams"});
        ingredientModel.addRow(new Object[]{"Meatballs", 3000, "pieces"});

    }

}
