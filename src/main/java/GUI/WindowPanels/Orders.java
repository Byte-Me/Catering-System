package GUI.WindowPanels;

import Database.OrderManagement;
import GUI.AddOrder;
import GUI.EditCustomer;
import GUI.EditOrder;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Created by olekristianaune on 13.03.2016.
 */
public class Orders {

    static OrderManagement orderManagement = new OrderManagement();
    static DefaultTableModel orderModel;
    private final int orderColumnNr = 0;

    public Orders(JTable ordersTable, final JTextField searchOrders, JButton addOrderButton, JButton editOrderButton, JButton deleteOrderButton) {

        String[] headers = {"ID", "Name", "Phone", "Address", "Date", "Status"};

        orderModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };

        orderModel.setColumnIdentifiers(headers);

        ordersTable.setModel(orderModel);
        ordersTable.setAutoCreateRowSorter(true);
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        addOrderButton.addActionListener(e -> new AddOrder());

        editOrderButton.addActionListener(e -> {
            if(ordersTable.getSelectedColumn() >= 0) { //TODO: sjekker ikke om flere columns er selected, velger øverste.
                int id = (Integer)ordersTable.getValueAt(ordersTable.getSelectedRow(), orderColumnNr); //hent username for selected row
                new EditOrder(id);
            }
            else{
                showMessageDialog(null, "An order needs to be selected.");
            }
        });

        ordersTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    int id = (Integer) ordersTable.getValueAt(ordersTable.getSelectedRow(), orderColumnNr);
                    new EditOrder(id);
                }
            }
        });
        searchOrders.getDocument().addDocumentListener(new DocumentListener() {
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
                String searchTerm = searchOrders.getText();

                ArrayList<Object[]> searchResult = orderManagement.orderSearch(searchTerm);

                updateOrders(searchResult);
            }
        });

    }

    // Update Users function
    public static void updateOrders() {

        // Get users from database
        ArrayList<Object[]> users = orderManagement.getOrders();

        updateOrders(users);

    }

    public static void updateOrders(ArrayList<Object[]> users) {

        // Empties entries of Users table
        orderModel.setRowCount(0);

        // Add users from arraylist to table
        for (Object[] user : users) {
            orderModel.addRow(user);
        }
    }

}
