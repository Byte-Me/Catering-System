package GUI.WindowPanels;

import Database.UserManagement;
import GUI.AddUser;
import GUI.EditUser;
import HelperClasses.TableCellListener;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import static javax.swing.JOptionPane.*;

/**
 * Created by olekristianaune on 13.03.2016.
 */
public class Users {

    static UserManagement userManagement = new UserManagement();
    static DefaultTableModel userModel;
    private static JTable localUserTable;
    static DefaultListSelectionModel listSelectionModel;

    // Create Users Pane
    public Users(final JPanel mainPanel, JButton addUserButton, final JTable userTable, final JTextField searchUsers, JButton deleteUsersButton, JButton editUserButton) {

        final int usernameColumnNr = 4;
        final int userTypeColumnNr = 5;

        localUserTable = userTable;

        String[] header = {"First Name", "Last Name", "Email", "Phone", "Username", "User Type"}; // Header titles

        //gjør celler un-editable.
        userModel = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        }; // Model of the table

        userModel.setColumnIdentifiers(header); // Add header to columns

        userTable.setModel(userModel); // Add model to table
        userTable.setAutoCreateRowSorter(true); // Auto sort table by row
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        listSelectionModel = new DefaultListSelectionModel();

        addUserButton.addActionListener(e -> new AddUser(mainPanel.getParent()));

        editUserButton.addActionListener(e ->{
            System.out.println(userTable.getSelectedColumn());
            //TODO: oppstår IndexOutOfBounds når en column er merket, så unmerket, deretter prøver edit user.
            if(userTable.getSelectedColumn() >= 0) { //TODO: sjekker ikke om flere columns er selected, velger øverste.
                String username = (String) userTable.getValueAt(userTable.getSelectedRow(), usernameColumnNr); //hent username for selected row
                new EditUser(mainPanel.getParent(), username);
            }

            else{
                showMessageDialog(null, "A user needs to be selected.");
            }

        });
        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    String username = (String) userModel.getValueAt(userTable.getSelectedRow(), usernameColumnNr);
                    new EditUser(mainPanel.getParent(), username);
                }
            }
        });

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

    }

    // Update Users function
    public static void updateUsers() {

        // Get users from database
        ArrayList<Object[]> users = userManagement.userInfo();

        updateUsers(users);
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
