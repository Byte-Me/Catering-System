package Statistics;

import Database.StatisticsManagement;
import GUI.graph.ChartCreator;

import javax.swing.*;

/**
 * Created by Evdal on 15.03.2016.
 */
public class OrderStatistics {
    public JFrame createGraphFromOrders(){
        StatisticsManagement stat = new StatisticsManagement();
        JFrame out = ChartCreator.createLineChart("Orders", "Date", "Amount of orders", );
    }
}
