package GUI.WindowPanels;

import Database.CustomerManagement;
import GUI.AddCustomer;
import GUI.EditCustomer;
import HelperClasses.MainTableModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Created by olekristianaune on 13.03.2016.
 */
public class Customers {

    static CustomerManagement customerManagement = new CustomerManagement();
    static DefaultTableModel customerModel;
    private int emailColumnNr = 1;

    public Customers(JButton addCustomerButton, final JTable customerTable, final JTextField searchCustomers, JButton deleteCustomerButton, JButton editCustomerButton) {

        addCustomerButton.addActionListener(new ActionListener() { // Button action listener
            public void actionPerformed(ActionEvent e) {
                new AddCustomer();
            }
        });

        editCustomerButton.addActionListener(e -> {
            if(customerTable.getSelectedColumn() >= 0) { //TODO: sjekker ikke om flere columns er selected, velger øverste.
                String email = (String) customerTable.getValueAt(customerTable.getSelectedRow(), emailColumnNr); //hent username for selected row
                new EditCustomer(email);
            }
            else{
                showMessageDialog(null, "A customer needs to be selected.");
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
                if (customerTable.getSelectedRow() != -1) {
                    String email = (String) customerTable.getValueAt(customerTable.getSelectedRow(), emailColumnNr);
                    new EditCustomer(email);
                }
            }
        });
        popupMenu.add(new AbstractAction("Delete Customer") {
            @Override
            public void actionPerformed(ActionEvent e) {

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
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    if (customerTable.getSelectedRow() != -1) {
                        String email = (String) customerTable.getValueAt(customerTable.getSelectedRow(), emailColumnNr);
                        new EditCustomer(email);
                    }
                }
            }
        });

        String[] header = {"Name", "Email", "Phone", "Address"}; // Header titles

        customerModel = new MainTableModel();
        customerModel.setColumnIdentifiers(header); // Add header to columns


        customerTable.setModel(customerModel); // Add model to table
        customerTable.setAutoCreateRowSorter(true);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //setting column widths -- FIXME: Find better way to do this
        customerTable.getColumnModel().getColumn(0).setMinWidth(210);
        customerTable.getColumnModel().getColumn(1).setMaxWidth(210);
        customerTable.getColumnModel().getColumn(2).setMinWidth(130);

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

    // Update Users function
    public static void updateCustomer() {

        // Get users from database
        ArrayList<Object[]> customers = customerManagement.getCustomers();

        updateCustomer(customers);

    }

    public static void updateCustomer(ArrayList<Object[]> customers) {

        // Empties entries of Users table
        customerModel.setRowCount(0);

        // Add users from arraylist to table
        for (Object[] customer : customers) {
            customerModel.addRow(customer);
        }
    }


}
