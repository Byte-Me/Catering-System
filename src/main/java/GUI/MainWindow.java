package GUI;

import Database.FoodManagement;
import Database.UserManagement;

import javax.swing.*;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.MaskFormatter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import HelperClasses.TableCellListener;

import static javax.swing.JOptionPane.showMessageDialog;
import static javax.swing.JOptionPane.showOptionDialog;
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
    private JButton searchCustomersButton;
    private JButton searchUsersButton;
    private JTable table1;
    private JButton searchOrdersButton;
    private JTextField serachOrders;
    private JButton addOrderButton;
    private JTextField searchCustomers;
    private JButton deleteCustomersButton;
    private JTextField searchUsers;
    private JButton deleteUsersButton;
    private JButton addIngredientButton;

    private static DefaultTableModel userModel;
    private static DefaultTableModel customerModel;
    private static DefaultListModel<String> driverModel;
    private static DefaultTableModel prepareModel;
    private static DefaultTableModel ingredientModel;

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
        setupSale();
        setupDriver();
        setupChef();

        pack();
        setSize(1000, 600);
        setLocationRelativeTo(null);

        setVisible(true);
    }

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

        updateUsers();

    }

    public static void updateUsers() {

        UserManagement userManagement = new UserManagement();
        ArrayList<Object[]> users = userManagement.userInfo();

        for(int i = 0; i < userModel.getRowCount(); i++) {
            userModel.removeRow(i);
        }

        for (Object[] user : users) {
            userModel.addRow(user);
        }
    }

    private void setupSale() {

        addCustomerButton.addActionListener(new ActionListener() { // Button action listener
            public void actionPerformed(ActionEvent e) {
                // TODO - make action for the button
            }
        });

        String[] header = {"Name", "Email", "Phone"}; // Header titles

        customerModel = new DefaultTableModel(); // Model of the table
        customerModel.setColumnIdentifiers(header); // Add header to columns

        customerTable.setModel(customerModel); // Add model to table

        // TODO - testdata (remove)
        customerModel.addRow(new Object[]{"Some Curporation LTD", "noe@noe.com", "45987700"});
        customerModel.addRow(new Object[]{"Johan Olsen", "noeannet@noeannet.com", "91482099"});

    }

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

        FoodManagement foodManagement = new FoodManagement();
        addRecipeButton.addActionListener(new ActionListener() { // Button action listener
            public void actionPerformed(ActionEvent e) {
                new AddRecipe(mainPanel.getParent());
            }
        });

        addIngredientButton.addActionListener(new ActionListener() { // Button action listener
            public void actionPerformed(ActionEvent e) {
                new AddIngredient(mainPanel.getParent());
            }
        });

        generateShoppingListButton.addActionListener(new ActionListener() { // Button action listener
            public void actionPerformed(ActionEvent e) {
                new GenerateShoppingList(mainPanel.getParent());
            }
        });

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

        updateIngredients();

        /*
        // What happens when a cell in the table is changed?
        Action action = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                TableCellListener tcl = (TableCellListener)e.getSource();
                FoodManagement foodManagement = new FoodManagement();
                int option;
                if ((tcl.getOldValue()).equals(tcl.getNewValue())) { // Why isn't this working on dropdown?
                    option = 1;
                } else {
                    option = showOptionDialog(null,
                            "Change " + userModel.getColumnName(tcl.getColumn()) + " from '" + tcl.getOldValue() + "' to '" + tcl.getNewValue() + "'?",
                            "Edit " + userModel.getColumnName(tcl.getColumn()),
                            YES_NO_OPTION,
                            INFORMATION_MESSAGE,
                            null,
                            new Object[]{"Yes", "No"},
                            "No");
                }


                // If yes, update database
                if (option == 0) {
                    switch (tcl.getColumn()) {
                        case 0:
                            try {
                                foodManagement.updateQuantity((String)ingredientModel.getValueAt(tcl.getRow(), 0), (String)tcl.getNewValue());
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                            break;
                        default:
                            System.err.println(userTable.getColumnName(tcl.getColumn()) + " does not yet have an implemetation.");
                    }

                }

                // Update user table from database
                //updateUsers();
            }
        };
        TableCellListener tcl = new TableCellListener(userTable, action);
        */
    }


    public static void updateIngredients() {
        FoodManagement foodManagement = new FoodManagement();
        ArrayList<Object[]> ingredients = foodManagement.getIngredients();

        for(int i = 0; i < ingredientModel.getRowCount(); i++) {
            ingredientModel.removeRow(i);
        }

        for (Object[] ingredient : ingredients) {
            ingredientModel.addRow(ingredient);
        }
    }
}
