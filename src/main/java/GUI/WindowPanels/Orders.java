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

        //setting column widths
        ordersTable.getColumnModel().getColumn(0).setMinWidth(90);
        ordersTable.getColumnModel().getColumn(1).setMaxWidth(190);
        ordersTable.getColumnModel().getColumn(2).setMinWidth(130);
        ordersTable.getColumnModel().getColumn(0).setMaxWidth(90);
        ordersTable.getColumnModel().getColumn(1).setMinWidth(190);
        ordersTable.getColumnModel().getColumn(2).setMaxWidth(130);
        ordersTable.getColumnModel().getColumn(4).setMinWidth(130);
        ordersTable.getColumnModel().getColumn(4).setMaxWidth(130);
        ordersTable.getColumnModel().getColumn(5).setMinWidth(100);
        ordersTable.getColumnModel().getColumn(5).setMaxWidth(100);

        addOrderButton.addActionListener(e -> new AddOrder(mainPanel.getParent()));

        editOrderButton.addActionListener(e -> {
            if(ordersTable.getSelectedColumn() >= 0) { //TODO: sjekker ikke om flere columns er selected, velger øverste.
                int id = (Integer)ordersTable.getValueAt(ordersTable.getSelectedRow(), orderColumnNr); //hent username for selected row
                new EditOrder(mainPanel.getParent(), id);
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
                    new EditOrder(mainPanel.getParent(), id);
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
