package GUI;

import Database.CustomerManagement;
import Database.UserManagement;
import GUI.WindowPanels.*;
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
        Statistics statisticsPanel = new Statistics();
        Users usersPanel = new Users(mainPanel, addUserButton, userTable, searchUsers);
        Customers customersPanel = new Customers();
        Orders ordersPanel = new Orders();
        Driver driverPanel = new Driver(drivingList);
        Chef chefPanel = new Chef(prepareTable, ingredientTable);

        // FIXME: Move contents of these methods to seperate classes
        setupStatistics();
        setupCustomer();
        setupOrders();

        pack();
        setSize(1000, 600);
        setLocationRelativeTo(null);

        setVisible(true);
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
        customerTable.setAutoCreateRowSorter(true);

        // What happens when a cell in the table is changed?
        Action action = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                int usernameColumn = 4;
                TableCellListener tcl = (TableCellListener)e.getSource();

                int option = showOptionDialog(null,
                        "Change " + customerTable.getColumnName(tcl.getColumn()) + " from '" + tcl.getOldValue() + "' to '" + tcl.getNewValue() + "'?",
                        "Edit " + customerTable.getColumnName(tcl.getColumn()),
                        YES_NO_OPTION,
                        INFORMATION_MESSAGE,
                        null,
                        new Object[]{"Yes", "No"},
                        "No");

                // If yes, ubdate database

                /*
                if (option == 0) {
                    switch (tcl.getColumn()) {
                        case 0:
                            customerManagement.updateCustomerInfoName((String)userModel.getValueAt(tcl.getRow(), usernameColumn), (String)tcl.getNewValue());
                            break;
                        case 1:
                            customerManagement.updateCustomerInfoEmail((String)userModel.getValueAt(tcl.getRow(), usernameColumn), (String)tcl.getNewValue());
                            break;
                        case 2:
                            customerManagement.updateCustomerInfoPhone((String)userModel.getValueAt(tcl.getRow(), usernameColumn), (String)tcl.getNewValue());
                            break;
                        case 3:
                            customerManagement.updateCustomerInfoAddress((String)userModel.getValueAt(tcl.getRow(), usernameColumn), (String)tcl.getNewValue());
                            break;
                        default:
                            System.err.println(customerTable.getColumnName(tcl.getColumn()) + " does not yet have an implemetation.");
                    }

                }
                */

                // Update user table from database
                updateCustomer();
            }
        };
        TableCellListener tcl = new TableCellListener(customerTable, action); //TODO: Find out how to handle updating of combined fields

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



}
