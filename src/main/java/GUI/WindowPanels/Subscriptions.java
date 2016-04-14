package GUI.WindowPanels;

import Database.SubscriptionManagement;
import HelperClasses.MainTableModel;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by Evdal on 14.04.2016.
 */
public class Subscriptions {
    private JTable subscriptionTable;
    private JButton showEditSubscriptionButton;
    private JButton deleteSubscriptionButton;
    private JButton newSubscriptionButton;
    private JTextField searchSubscriptions;
    private static MainTableModel subsModel;

    private static SubscriptionManagement subscriptionManagement = new SubscriptionManagement();

    public Subscriptions(JTable subscriptionTable, JButton showEditSubscriptionButton, JButton deleteSubscriptionButton, JButton newSubscriptionButton, JTextField searchSubscriptions){
        this.subscriptionTable = subscriptionTable;
        this.deleteSubscriptionButton = deleteSubscriptionButton;
        this.newSubscriptionButton = newSubscriptionButton;
        this.showEditSubscriptionButton = showEditSubscriptionButton;
        this.searchSubscriptions = searchSubscriptions;

        //add headers to table
        String[] header = new String []{"ID","Name","Date from","Date to","Frequency in weeks"};

        subsModel = new MainTableModel();
        subscriptionTable.setModel(subsModel);
        subscriptionTable.setAutoCreateRowSorter(true);
        subscriptionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        subsModel.setColumnIdentifiers(header);

        updateSubs();

       // searchSubscriptions.;

    }
    public static void updateSubs(){
        ArrayList<Object[]> subs = subscriptionManagement.getSubscriptions();
        subsModel.setRowCount(0);
        for(Object[] sub : subs){
            subsModel.addRow(sub);
        }

    }
}
