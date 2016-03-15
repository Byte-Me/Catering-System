package Statistics;

import Database.StatisticsManagement;
import GUI.graph.ChartCreator;
import org.jfree.chart.JFreeChart;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by Evdal on 15.03.2016.
 */
public class OrderStatistics {
    public static JPanel createGraphFromOrders(String startDate, String endDate){
        StatisticsManagement stat = new StatisticsManagement();
        ArrayList<String> orders = stat.getOrders(startDate, endDate);
        ArrayList<String> yValues = new ArrayList<String>();
        ArrayList<String> xValues = new ArrayList<String>();
        String curDate = "";
        try{
            curDate = orders.get(0);
        }
        catch (Exception e){
            System.err.println("No orders found.");
            return null;
        }

        int count = 1;
        for(int i = 1; i<orders.size();i++){
            if(orders.get(i).equals(curDate)){
                count++;
            }
            else{
                yValues.add(""+count);
                xValues.add(orders.get(i-1));
                curDate = orders.get(i);
                count = 1;
            }
        }
        return ChartCreator.createLineChart("Orders", "Date", "Amount of orders", xValues, yValues, "Orders");
    }
}
