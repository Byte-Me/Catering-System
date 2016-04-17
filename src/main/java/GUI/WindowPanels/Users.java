package GUI.WindowPanels;

import Database.UserManagement;
import GUI.AddUser;
import GUI.EditUser;
import HelperClasses.MainTableModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
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
    public Users(JButton addUserButton, final JTable userTable, final JTextField searchUsers, JButton deleteUsersButton, JButton editUserButton) {

        final int usernameColumnNr = 4;
        final int userTypeColumnNr = 5;

        localUserTable = userTable;

        String[] header = {"First Name", "Last Name", "Email", "Phone", "Username", "User Type"}; // Header titles

        //gjør celler un-editable.
        userModel = new MainTableModel();

        userModel.setColumnIdentifiers(header); // Add header to columns

        userTable.setModel(userModel); // Add model to table
        userTable.setAutoCreateRowSorter(true); // Auto sort table by row
       // userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        listSelectionModel = new DefaultListSelectionModel();

        addUserButton.addActionListener(e -> new AddUser());

        editUserButton.addActionListener(e ->{
            try {
                if (userTable.getSelectedRows().length == 1 ) { //TODO: sjekker ikke om flere columns er selected, velger øverste.
                    String username = (String) userTable.getValueAt(userTable.getSelectedRow(), usernameColumnNr); //hent username for selected row
                    new EditUser(username);
                } else if(userTable.getSelectedRows().length < 1){
                    showMessageDialog(null, "A user needs to be selected.");
                }
                else{
                    showMessageDialog(null, "Only one user can be selected.");
                }
            }
            catch (IndexOutOfBoundsException iobe){ //Oppstår exception jeg ikke forstår, derfor bare catcher det.
                showMessageDialog(null, "A user needs to be selected.");
            }

        });

        // Right Click Menu
        JPopupMenu popupMenu = new JPopupMenu("Users");
        popupMenu.add(new AbstractAction("New User") {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddUser();
            }
        });
        popupMenu.add(new AbstractAction("Edit User") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (userTable.getSelectedRow() != -1) {
                    String username = (String) userModel.getValueAt(userTable.getSelectedRow(), usernameColumnNr);
                    new EditUser(username);
                }
            }
        });
        popupMenu.add(new AbstractAction("Delete User") {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        // Mouse Click Listener
        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int r = userTable.rowAtPoint(e.getPoint());
                if (r >= 0 && r < userTable.getRowCount()) {
                    userTable.setRowSelectionInterval(r, r);
                } else {
                    userTable.clearSelection();
                }

                int rowindex = userTable.getSelectedRow();
                if (rowindex < 0)
                    return;

                if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                int r = userTable.rowAtPoint(e.getPoint());
                if (r >= 0 && r < userTable.getRowCount()) {
                    userTable.setRowSelectionInterval(r, r);
                } else {
                    userTable.clearSelection();
                }

                int rowindex = userTable.getSelectedRow();
                if (rowindex < 0)
                    return;

                if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    if (userTable.getSelectedRow() != -1) {
                        String username = (String) userModel.getValueAt(userTable.getSelectedRow(), usernameColumnNr);
                        new EditUser(username);
                    }
                }
            }
        });

        deleteUsersButton.addActionListener(e ->{
            if(userTable.getSelectedRows().length < 1){
                showMessageDialog(null, "A user needs to be selected.");
            }
            else{
                int[] users = userTable.getSelectedRows();
                for(int i = 0; i<users.length;i++){
                    String username = (String)userModel.getValueAt(users[i], 4);
                    String message = "Are you sure you want to delete user: "+username+"?"; //username
                    int answer = showOptionDialog(null, message, "Delete User",YES_NO_OPTION,QUESTION_MESSAGE,null, new Object[]{"Yes", "No"}, "No");
                    if(answer == YES_OPTION){
                        userManagement.deleteUser(username); //TODO: lag backend.
                        updateUsers();
                    }
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

    // TODO: Make generic update function (also make generic table model with right click menu)

    // Update Users function
    public static void updateUsers() {

        //clears selection in row, important
        listSelectionModel.clearSelection();

        // Get users from database
        ArrayList<Object[]> users = userManagement.userInfo();

        updateUsers(users);
    }

    public static void updateUsers(ArrayList<Object[]> users) {

        // Empties entries of Users table
        userModel.setRowCount(0);

        // Add users from arraylist to table
        for (Object[] user : users) {
            userModel.addRow(user);
        }
    }
}
