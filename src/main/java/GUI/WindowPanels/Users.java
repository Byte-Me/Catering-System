package GUI.WindowPanels;

import Database.UserManagement;
import GUI.AddUser;
import HelperClasses.TableCellListener;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.showOptionDialog;

/**
 * Created by olekristianaune on 13.03.2016.
 */
public class Users {

    static UserManagement userManagement = new UserManagement();
    static DefaultTableModel userModel;

    // Create Users Pane
    public Users(final JPanel mainPanel, JButton addUserButton, final JTable userTable, final JTextField searchUsers) {

        final int usernameColumnNr = 4;
        final int userTypeColumnNr = 5;

        String[] header = {"First Name", "Last Name", "Email", "Phone", "Username", "User Type"}; // Header titles

        userModel = new DefaultTableModel(); // Model of the table
        userModel.setColumnIdentifiers(header); // Add header to columns

        userTable.setModel(userModel); // Add model to table
        userTable.setAutoCreateRowSorter(true); // Auto sort table by row

        // Create dropdown for UserType
        JComboBox<String> userTypeDropDown = new JComboBox<String>();
        userTypeDropDown.addItem("Admin");
        userTypeDropDown.addItem("Sale");
        userTypeDropDown.addItem("Driver");
        userTypeDropDown.addItem("Chef");

        // Add dropdown to table
        TableColumn userTypeColumn = userTable.getColumnModel().getColumn(userTypeColumnNr);
        userTypeColumn.setCellEditor(new DefaultCellEditor(userTypeDropDown));

        addUserButton.addActionListener(new ActionListener() { // Button action listener
            public void actionPerformed(ActionEvent e) {
                new AddUser(mainPanel.getParent());
            }
        });

        // What happens when a cell in the table is changed?
        Action action = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                TableCellListener tcl = (TableCellListener)e.getSource();

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


                // If yes, ubdate database
                if (option == 0) {
                    switch (tcl.getColumn()) {
                        case 0:
                            userManagement.updateUserInfoFName((String)userModel.getValueAt(tcl.getRow(), usernameColumnNr), (String)tcl.getNewValue());
                            break;
                        case 1:
                            userManagement.updateUserInfoLName((String)userModel.getValueAt(tcl.getRow(), usernameColumnNr), (String)tcl.getNewValue());
                            break;
                        case 2:
                            userManagement.updateUserInfoEmail((String)userModel.getValueAt(tcl.getRow(), usernameColumnNr), (String)tcl.getNewValue());
                            break;
                        case 3:
                            userManagement.updateUserInfoPhone((String)userModel.getValueAt(tcl.getRow(), usernameColumnNr), (String)tcl.getNewValue());
                            break;
                        case 4:
                            userManagement.updateUserInfoUsername((String)userModel.getValueAt(tcl.getRow(), usernameColumnNr), (String)tcl.getNewValue());
                            break;
                        case 5:
                            // Handle the dropdown differently
                            userManagement.updateUserInfoAccessLevel((String)userModel.getValueAt(tcl.getRow(), usernameColumnNr), UserManagement.UserType.valueOf(((String)tcl.getNewValue()).toUpperCase()).getValue());
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
}
