package Statistics;

import Database.StatisticsManagement;
import Statistics.graph.ChartCreator;

import javax.swing.*;
import java.util.*;

/**
 * Created by Evdal on 15.03.2016.
 *
 * //TODO metoden som lager grafen kan ta med alle dager/uker/mnd selv om de ikke har ordre i seg.
 *
 */
public class OrderStatistics extends Statistics{

    public OrderStatistics(){
        super();
    }



    public JPanel createGraphFromOrders(String startDate, String endDate) {
        StatisticsManagement stat = new StatisticsManagement();
        ArrayList<String> orders = stat.getOrders(startDate, endDate);
        ArrayList<Double> yValues = new ArrayList<Double>();
        ArrayList<String> xValues = new ArrayList<String>();
        String curDate = "";
        System.out.println(orders);
        try {
            curDate = orders.get(0);
        } catch (Exception e) {
            System.err.println("No orders found.");
            return null;
        }
        JPanel chart = null;
        int count = 1;
        try {
            int timeDiff = checkDaysBetween(startDate, endDate);

            if (timeDiff > 160) {
                for (int i = 1; i < orders.size(); i++) {
                    if (isSameMonth(curDate, orders.get(i))) {
                        count++;
                        if(i == orders.size()-1){
                            yValues.add(Double.valueOf(count));
                            xValues.add(getMonthName(orders.get(i)));
                        }
                    } else {
                        yValues.add(Double.valueOf(count));
                        xValues.add(getMonthName(orders.get(i - 1)));
                        curDate = orders.get(i);
                        count = 1;
                    }
                }

                chart = ChartCreator.createLineChart("Orders", "Months", "Orders per month", xValues, yValues, "orders");
            }
            else if(timeDiff > 30){
                for (int i = 1; i < orders.size(); i++) {
                    if (isSameWeek(curDate, orders.get(i))) {
                        count++;
                        if(i == orders.size()-1){
                            yValues.add(Double.valueOf(count));
                            xValues.add(getWeekName(orders.get(i)));
                        }
                    } else {
                        yValues.add(Double.valueOf(count));
                        xValues.add(getWeekName(orders.get(i - 1)));
                        curDate = orders.get(i);
                        count = 1;
                    }
                }
                System.out.println("Orders" + "Days" + "Orders per day" + xValues + yValues + "orders");

                chart = ChartCreator.createLineChart("Orders", "Weeks", "Orders per week", xValues, yValues, "orders");

            }


            else {
                for (int i = 1; i < orders.size(); i++) {
                    if (orders.get(i).equals(curDate)) {
                        count++;
                        if(i == orders.size()-1){
                            yValues.add(Double.valueOf(count));
                            xValues.add(orders.get(i));
                        }
                    }
                    else{
                        yValues.add(Double.valueOf(count));
                        xValues.add(orders.get(i - 1));
                        curDate = orders.get(i);
                        count = 1;
                    }
                }
                chart = ChartCreator.createLineChart("Orders", "Days", "Orders per day", xValues, yValues, "orders");

            }
        }
        catch (Exception e){
            System.err.println("Issue with creating graph.");
        }

        return chart;
    }


}
