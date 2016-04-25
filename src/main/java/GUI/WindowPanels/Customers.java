package GUI.WindowPanels;

import Database.CustomerManagement;
import GUI.AddCustomer;
import GUI.EditCustomer;
import Util.HelperClasses.MainTableModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Created by olekristianaune on 13.03.2016.
 */
public class Customers {

    static CustomerManagement customerManagement = new CustomerManagement();
    static MainTableModel customerModel;
    static  MainTableModel inactiveCustomerModel;
    static JTable customerTable;
    private static int emailColumnNr = 1;

    /**
     * Construrctor for the window panel Customers.
     * @param addCustomerButton         JButton that allows the user to add a new customer.
     * @param customerTable             JTable that display active customers.
     * @param inactiveCustomerTable     JTable that display inactive customers.
     * @param searchCustomers           JTextField that allows the user to search through customers.
     * @param deleteCustomerButton      JButton that deletes the selected user.
     * @param editCustomerButton        JButton that allows the user to edit the selected user.
     * @param reactivateCustomerButton  JButton that reactivates the selected user in the inactive users table.
     */
    public Customers(JButton addCustomerButton, final JTable customerTable, final JTable inactiveCustomerTable, final JTextField searchCustomers, JButton deleteCustomerButton, JButton editCustomerButton, JButton reactivateCustomerButton) {

        this.customerTable = customerTable;

        addCustomerButton.addActionListener(e -> new AddCustomer());

        editCustomerButton.addActionListener(e -> {
            editCustomer();
        });

        deleteCustomerButton.addActionListener(e1 -> deleteCustomer());

        reactivateCustomerButton.addActionListener(e -> {
            if (inactiveCustomerTable.getSelectedRow() > 0) {
                String customerEmail = (String) inactiveCustomerTable.getValueAt(inactiveCustomerTable.getSelectedRow(), emailColumnNr);
                customerManagement.updateCustomerStatus(customerEmail, CustomerManagement.CustStatus.ACTIVE.getValue()); //FIXME: What happens with corporation?
                updateCustomer();
                updateInactiveCustomer();
            }
        });


        // Right Click Menu
        JPopupMenu popupMenu = new JPopupMenu("Customers");
        popupMenu.add(new AbstractAction("New Customer") {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddCustomer();
            }
        });
        popupMenu.add(new AbstractAction("Edit Customer") {
            @Override
            public void actionPerformed(ActionEvent e) {
                editCustomer();
            }
        });
        popupMenu.add(new AbstractAction("Delete Customer") {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteCustomer();
            }
        });

        customerTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                int r = customerTable.rowAtPoint(e.getPoint());
                if (r >= 0 && r < customerTable.getRowCount()) {
                    customerTable.setRowSelectionInterval(r, r);
                } else {
                    customerTable.clearSelection();
                }

                int rowindex = customerTable.getSelectedRow();
                if (rowindex < 0)
                    return;

                if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                int r = customerTable.rowAtPoint(e.getPoint());
                if (r >= 0 && r < customerTable.getRowCount()) {
                    customerTable.setRowSelectionInterval(r, r);
                } else {
                    customerTable.clearSelection();
                }

                int rowindex = customerTable.getSelectedRow();
                if (rowindex < 0)
                    return;

                if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    if (customerTable.getSelectedRow() != -1) {
                        String email = (String) customerTable.getValueAt(customerTable.getSelectedRow(), emailColumnNr);
                        new EditCustomer(email);
                    }
                }
            }
        });

        String[] header = {"Name", "Email", "Phone", "Address", "Customer Type"}; // Header titles

        customerModel = new MainTableModel();
        customerModel.setColumnIdentifiers(header); // Add header to columns


        customerTable.setModel(customerModel); // Add model to table
        customerTable.setAutoCreateRowSorter(true);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //setting column widths -- FIXME: Find better way to do this
        customerTable.getColumnModel().getColumn(0).setMinWidth(210);
        customerTable.getColumnModel().getColumn(0).setMaxWidth(210);
        customerTable.getColumnModel().getColumn(1).setMinWidth(210);
        customerTable.getColumnModel().getColumn(1).setMaxWidth(210);
        customerTable.getColumnModel().getColumn(2).setMinWidth(130);
        customerTable.getColumnModel().getColumn(2).setMaxWidth(130);

        customerTable.getColumnModel().getColumn(4).setMinWidth(100);
        customerTable.getColumnModel().getColumn(4).setMaxWidth(100);

        inactiveCustomerModel = new MainTableModel();
        inactiveCustomerModel.setColumnIdentifiers(header);

        inactiveCustomerTable.setModel(inactiveCustomerModel);
        inactiveCustomerTable.setAutoCreateRowSorter(true);
        inactiveCustomerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


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

    }

    /**
     * Util.Updates the active customer table from the database.
     */
    public static void updateCustomer() {

        // Get customers from database
        ArrayList<Object[]> customers = customerManagement.getCustomers();

        updateCustomer(customers);

    }

    /**
     * Util.Updates the active customer table from the database.
     * @param customers List of active customers.
     */
    public static void updateCustomer(ArrayList<Object[]> customers) {

        // Empties entries of Users table
        customerModel.setRowCount(0);

        // Add users from arraylist to table
        for (Object[] customer : customers) {
            customerModel.addRow(customer);
        }
    }

    /**
     * Util.Updates the inactive customer table from the database.
     */
    public static void updateInactiveCustomer() {

        // Get inactive customers from database
        ArrayList<Object[]> inactiveCustomers = customerManagement.getDeletedCustomers();

        updateInactiveCustomer(inactiveCustomers);
    }

    /**
     * Util.Updates the inactive customer table from the database.
     * @param inactiveCustomers List of inactive customers
     */
    public static void updateInactiveCustomer(ArrayList<Object[]> inactiveCustomers) {

        // Empties entries of Users table
        inactiveCustomerModel.setRowCount(0);

        // Add users from arraylist to table
        for (Object[] customer : inactiveCustomers) {
            inactiveCustomerModel.addRow(customer);
        }
    }

    /**
     *
     */
    private static void editCustomer() {
        if (customerTable.getSelectedRow() != -1) {
            String email = (String) customerTable.getValueAt(customerTable.getSelectedRow(), emailColumnNr);
            new EditCustomer(email);
        } else {
            showMessageDialog(null, "A customer needs to be selected.");
        }
    }

    /**
     *
     */
    private static void deleteCustomer() {
        if (customerTable.getSelectedRow() > 0) {
            String customerEmail = (String) customerTable.getValueAt(customerTable.getSelectedRow(), emailColumnNr);
            customerManagement.deleteCustomer(customerEmail);
            updateCustomer();
            updateInactiveCustomer();
        }
    }


}
