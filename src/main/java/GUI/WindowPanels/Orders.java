package GUI.WindowPanels;

import Database.OrderManagement;
import GUI.AddOrder;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by olekristianaune on 13.03.2016.
 */
public class Orders {

    static OrderManagement orderManagement = new OrderManagement();
    static DefaultTableModel orderModel;

    public Orders(final JPanel mainPanel, JTable ordersTable, final JTextField searchOrders, JButton addOrderButton, JButton editOrderButton, JButton deleteOrderButton) {

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

        addOrderButton.addActionListener(e -> new AddOrder(mainPanel.getParent()));

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
