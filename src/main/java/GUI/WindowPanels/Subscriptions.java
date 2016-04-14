package GUI.WindowPanels;

import Database.SubscriptionManagement;
import GUI.AddOrder;
import GUI.EditOrder;
import HelperClasses.MainTableModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Created by olekristianaune on 13.03.2016.
 */
public class Subscriptions {

    static SubscriptionManagement subscriptionManagement = new SubscriptionManagement();
    static MainTableModel subscriptionModel;
    private final int orderColumnNr = 0;

    public Subscriptions(JTable subscriptionsTable, final JTextField searchSubscriptions, JButton addSubscriptionButton, JButton editSubscriptionButton, JButton deleteSubscriptionButton) {

        String[] headers = {"ID", "Name", "Phone", "Address", "Date", "Status"};

        subscriptionModel = new MainTableModel();

        subscriptionModel.setColumnIdentifiers(headers);

        subscriptionsTable.setModel(subscriptionModel);
        subscriptionsTable.setAutoCreateRowSorter(true);
        subscriptionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


        DefaultTableCellRenderer intRenderer = new DefaultTableCellRenderer();
        intRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        subscriptionsTable.getColumnModel().getColumn(0).setCellRenderer(intRenderer);
        subscriptionsTable.getColumnModel().getColumn(2).setCellRenderer(intRenderer);

        addSubscriptionButton.addActionListener(e -> new AddOrder());

        //setting column widths -- FIXME: Find better way to to this
        subscriptionsTable.getColumnModel().getColumn(0).setMinWidth(90);
        subscriptionsTable.getColumnModel().getColumn(0).setMaxWidth(90);
        subscriptionsTable.getColumnModel().getColumn(1).setMinWidth(190);
        subscriptionsTable.getColumnModel().getColumn(1).setMaxWidth(190);
        subscriptionsTable.getColumnModel().getColumn(2).setMinWidth(130);
        subscriptionsTable.getColumnModel().getColumn(2).setMaxWidth(130);
        subscriptionsTable.getColumnModel().getColumn(4).setMinWidth(130);
        subscriptionsTable.getColumnModel().getColumn(4).setMaxWidth(130);
        subscriptionsTable.getColumnModel().getColumn(5).setMinWidth(100);
        subscriptionsTable.getColumnModel().getColumn(5).setMaxWidth(100);

        editSubscriptionButton.addActionListener(e -> {
            if(subscriptionsTable.getSelectedColumn() >= 0) { //TODO: sjekker ikke om flere columns er selected, velger øverste.
                int id = (Integer)subscriptionsTable.getValueAt(subscriptionsTable.getSelectedRow(), orderColumnNr); //hent username for selected row
                new EditOrder(id);
            }
            else{
                showMessageDialog(null, "An order needs to be selected.");
            }
        });

        subscriptionsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    int id = (Integer) subscriptionsTable.getValueAt(subscriptionsTable.getSelectedRow(), orderColumnNr);
                    new EditOrder(id);
                }
            }
        });
        searchSubscriptions.getDocument().addDocumentListener(new DocumentListener() {
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
                String searchTerm = searchSubscriptions.getText();

                ArrayList<Object[]> searchResult = subscriptionManagement.subscriptionSearch(searchTerm);

                updateOrders(searchResult);
            }
        });

    }

    // Update Users function
    public static void updateSubscriptions() {

        // Get users from database
        ArrayList<Object[]> users = subscriptionManagement.getSubscriptions();

        updateOrders(users);

    }

    public static void updateOrders(ArrayList<Object[]> users) {

        // Empties entries of Users table
        subscriptionModel.setRowCount(0);

        // Add users from arraylist to table
        for (Object[] user : users) {
            subscriptionModel.addRow(user);
        }
    }

}
