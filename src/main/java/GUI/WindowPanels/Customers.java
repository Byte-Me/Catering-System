package GUI.WindowPanels;

import Database.CustomerManagement;
import GUI.AddCustomer;
import GUI.EditCustomer;
import HelperClasses.TableCellListener;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import static javax.swing.JOptionPane.*;

/**
 * Created by olekristianaune on 13.03.2016.
 */
public class Customers {

    static CustomerManagement customerManagement = new CustomerManagement();
    static DefaultTableModel customerModel;
    private int emailColumnNr = 1;
    private static JTable localCustomerTable;

    public Customers(final JPanel mainPanel, JButton addCustomerButton, final JTable customerTable, final JTextField searchCustomers, JButton deleteCustomerButton, JButton editCustomerButton) {

        addCustomerButton.addActionListener(new ActionListener() { // Button action listener
            public void actionPerformed(ActionEvent e) {
                new AddCustomer(mainPanel.getParent());
            }
        });

        editCustomerButton.addActionListener(e -> {
            if(customerTable.getSelectedColumn() >= 0) { //TODO: sjekker ikke om flere columns er selected, velger øverste.
                String email = (String) customerTable.getValueAt(customerTable.getSelectedRow(), emailColumnNr); //hent username for selected row
                new EditCustomer(mainPanel.getParent(), email);
            }
            else{
                showMessageDialog(null, "A customer needs to be selected.");
            }
        });

        customerTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    String email = (String) customerTable.getValueAt(customerTable.getSelectedRow(), emailColumnNr);
                    new EditCustomer(mainPanel.getParent(), email);
                }
            }
        });

        localCustomerTable = customerTable;

        String[] header = {"Name", "Email", "Phone", "Address"}; // Header titles

        customerModel = new DefaultTableModel(){// Model of the table
            @Override
            //Gjør celler un-editable.
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        customerModel.setColumnIdentifiers(header); // Add header to columns

        customerTable.setModel(customerModel); // Add model to table
        customerTable.setAutoCreateRowSorter(true);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // What happens when a cell in the table is changed?
 /*       Action action = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                int emailColumn = 1;
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
                if (option == 0) {
                    switch (tcl.getColumn()) {
                        case 0:
                            customerManagement.updateCustomerName((String)customerModel.getValueAt(tcl.getRow(), emailColumn), (String)tcl.getNewValue());
                            break;
                        case 1:
                            customerManagement.updateCustomerEmail((String)customerModel.getValueAt(tcl.getRow(), emailColumn), (String)tcl.getNewValue());
                            break;
                        case 2:
                            customerManagement.updateCustomerPhone((String)customerModel.getValueAt(tcl.getRow(), emailColumn), (String)tcl.getNewValue());
                            break;
                        case 3:
                            customerManagement.updateCustomerAdress((String)customerModel.getValueAt(tcl.getRow(), emailColumn), (String)tcl.getNewValue());
                            break;
                        default:
                            System.err.println(customerTable.getColumnName(tcl.getColumn()) + " does not yet have an implemetation.");
                    }

                }

                // Update user table from database
                updateCustomer();
            }
        };
        TableCellListener tcl = new TableCellListener(customerTable, action); //TODO: Find out how to handle updating of combined fields
*/
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
/*
        if (localCustomerTable.isEditing()) {
            localCustomerTable.getCellEditor().stopCellEditing();
        }*/

        // Empties entries of Users table
        customerModel.setRowCount(0);

        // Add users from arraylist to table
        for (Object[] customer : customers) {
            customerModel.addRow(customer);
        }
    }


}
