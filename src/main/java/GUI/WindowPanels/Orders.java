package GUI.WindowPanels;

import Database.OrderManagement;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

/**
 * Created by olekristianaune on 13.03.2016.
 */
public class Orders {

    static OrderManagement orderManagement = new OrderManagement();
    static DefaultTableModel orderModel;

    public Orders(JTable ordersTable, final JTextField searchOrders, JButton addOrderButton) {

        String[] headers = {"Id", "Name", "Phone", "Address", "Date", "Status"};

        orderModel = new DefaultTableModel();
        orderModel.setColumnIdentifiers(headers);

        ordersTable.setModel(orderModel);
        ordersTable.setAutoCreateRowSorter(true);

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

        updateOrders();

    }

    // Update Users function
    public static void updateOrders() {

        // Get users from database
        ArrayList<Object[]> users = orderManagement.getOrders();

        // Empties entries of Users table
        if (orderModel.getRowCount() > 0) {
            for (int i = orderModel.getRowCount() - 1; i > -1; i--) {
                orderModel.removeRow(i);
            }
        }

        // Add users from arraylist to table
        for (Object[] user : users) {
            orderModel.addRow(user);
        }
    }

    public static void updateOrders(ArrayList<Object[]> users) {

        // Empties entries of Users table
        if (orderModel.getRowCount() > 0) {
            for (int i = orderModel.getRowCount() - 1; i > -1; i--) {
                orderModel.removeRow(i);
            }
        }

        // Add users from arraylist to table
        for (Object[] user : users) {
            orderModel.addRow(user);
        }
    }

}
