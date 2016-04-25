package GUI.WindowPanels;

import Database.OrderManagement;
import GUI.AddOrder;
import GUI.EditOrder;
import HelperClasses.MainTableModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import static javax.swing.JOptionPane.showInputDialog;
import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Created by olekristianaune on 13.03.2016.
 */
public class Orders {

    static OrderManagement orderManagement = new OrderManagement();
    static MainTableModel orderModel;
    private final int idColumnNr = 0;

    /**
     *
     * @param ordersTable
     * @param searchOrders
     * @param addOrderButton
     * @param editOrderButton
     * @param deleteOrderButton
     */
    public Orders(JTable ordersTable, final JTextField searchOrders, JButton addOrderButton, JButton editOrderButton, JButton deleteOrderButton) {

        String[] headers = {"ID", "Name", "Phone", "Address", "Date", "Status"};

        orderModel = new MainTableModel();

        orderModel.setColumnIdentifiers(headers);

        ordersTable.setModel(orderModel);
        ordersTable.setAutoCreateRowSorter(true);
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


        DefaultTableCellRenderer intRenderer = new DefaultTableCellRenderer();
        intRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        ordersTable.getColumnModel().getColumn(0).setCellRenderer(intRenderer);
        ordersTable.getColumnModel().getColumn(2).setCellRenderer(intRenderer);

        addOrderButton.addActionListener(e -> new AddOrder());

        //setting column widths -- FIXME: Find better way to to this
        ordersTable.getColumnModel().getColumn(0).setMinWidth(90);
        ordersTable.getColumnModel().getColumn(0).setMaxWidth(90);
        ordersTable.getColumnModel().getColumn(1).setMinWidth(190);
        ordersTable.getColumnModel().getColumn(1).setMaxWidth(190);
        ordersTable.getColumnModel().getColumn(2).setMinWidth(130);
        ordersTable.getColumnModel().getColumn(2).setMaxWidth(130);
        ordersTable.getColumnModel().getColumn(4).setMinWidth(130);
        ordersTable.getColumnModel().getColumn(4).setMaxWidth(130);
        ordersTable.getColumnModel().getColumn(5).setMinWidth(100);
        ordersTable.getColumnModel().getColumn(5).setMaxWidth(100);

        editOrderButton.addActionListener(e -> {
            if(ordersTable.getSelectedColumn() >= 0) {
                int id = (Integer)ordersTable.getValueAt(ordersTable.getSelectedRow(), idColumnNr); //hent username for selected row
                new EditOrder(id);
            }
            else{
                showMessageDialog(null, "An order needs to be selected.");
            }
        });

        deleteOrderButton.addActionListener(e -> {
            if(ordersTable.getSelectedColumn() >= 0){
                int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete order nr "+ordersTable.getValueAt(ordersTable.getSelectedRow(), idColumnNr)+"?",null,JOptionPane.YES_NO_OPTION);
                if(confirm == JOptionPane.YES_OPTION) {
                    int id = (Integer) ordersTable.getValueAt(ordersTable.getSelectedRow(), idColumnNr);
                    orderManagement.deleteOrder(id);
                    updateOrders();
                }
            }
            else {
                showMessageDialog(null, "An order needs to be selected");
            }

        });

        // Right Click Menu
        JPopupMenu popupMenu = new JPopupMenu("Orders");
        popupMenu.add(new AbstractAction("New Order") {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddOrder();
            }
        });
        popupMenu.add(new AbstractAction("Edit Order") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ordersTable.getSelectedRow() != -1) {
                    int id = (Integer) ordersTable.getValueAt(ordersTable.getSelectedRow(), idColumnNr);
                    new EditOrder(id);
                }
            }
        });
        popupMenu.add(new AbstractAction("Delete Order") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(ordersTable.getSelectedColumn() >= 0){
                    int id = (Integer)ordersTable.getValueAt(ordersTable.getSelectedRow(), idColumnNr);
                    orderManagement.deleteOrder(id);
                    updateOrders();
                }
            }
        });

        // Mouse Click Listener
        ordersTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {

                int r = ordersTable.rowAtPoint(e.getPoint());
                if (r >= 0 && r < ordersTable.getRowCount()) {
                    ordersTable.setRowSelectionInterval(r, r);
                } else {
                    ordersTable.clearSelection();
                }

                int rowindex = ordersTable.getSelectedRow();
                if (rowindex < 0)
                    return;

                if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {

                int r = ordersTable.rowAtPoint(e.getPoint());
                if (r >= 0 && r < ordersTable.getRowCount()) {
                    ordersTable.setRowSelectionInterval(r, r);
                } else {
                    ordersTable.clearSelection();
                }

                int rowindex = ordersTable.getSelectedRow();
                if (rowindex < 0)
                    return;

                if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    if (ordersTable.getSelectedRow() != -1) {
                        int id = (Integer) ordersTable.getValueAt(ordersTable.getSelectedRow(), idColumnNr);
                        new EditOrder(id);
                    }
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

    /**
     *
     */
    public static void updateOrders() {

        // Get users from database
        ArrayList<Object[]> orders = orderManagement.getOrders();

        updateOrders(orders);

    }

    /**
     *
     * @param orders
     */
    public static void updateOrders(ArrayList<Object[]> orders) {

        // Empties entries of Users table
        orderModel.setRowCount(0);

        // Add users from arraylist to table
        for (Object[] order : orders) {
            orderModel.addRow(order);
        }
    }

}
