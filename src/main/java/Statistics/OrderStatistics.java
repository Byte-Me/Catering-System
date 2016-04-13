package Statistics;

import Database.StatisticsManagement;
import Statistics.graph.ChartCreator;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import javax.swing.*;
import java.text.ParseException;
import java.util.*;

/**
 * Created by Evdal on 15.03.2016.
 *
 * //TODO metoden som lager grafen kan ta med alle dager/uker/mnd selv om de ikke har ordre i seg.
 *
 */
public class OrderStatistics extends Statistics{
    StatisticsManagement stat = new StatisticsManagement();

    public OrderStatistics(){
        super();
    }



    public Object[] createLineChartFromOrder(String startDateS, String endDateS) { //[0] = JFreeChart, [1] = SumOrders
        ArrayList<String> orders = stat.getDates(startDateS, endDateS, "order");
        ArrayList<Double> yValues = new ArrayList<>();
        ArrayList<String> xValues = new ArrayList<>(); // Brukes ikke?
        String curDate = ""; // Brukes ikke?
        Date startDate;
        Date endDate;
        try {
            startDate = getFormatter().parse(startDateS);
            endDate = getFormatter().parse(endDateS);

        } catch (ParseException e) {
            System.err.println("Issue with parsing date.");
            return null;
        }

        JPanel chart = null;
        int count = 1; // Brukes ikke?

        try {
            int timeDiff = checkDaysBetween(startDate, endDate);

            if (timeDiff > MONTHLIMIT) {
                ArrayList[] values = valuesMonth(orders);

                chart = ChartCreator.createLineChart("Orders per month", "Months", "Orders", (ArrayList<String>)values[0],
                        (ArrayList<Object>)values[1], "orders");
            }
            else if(timeDiff > WEEKLIMIT){
                ArrayList[] values = valuesWeek(orders);
                chart = ChartCreator.createLineChart("Orders per week", "Weeks", "Orders", (ArrayList<String>)values[0],
                        (ArrayList<Object>)values[1], "orders");

            }
            else {
                ArrayList[] values = valuesDay(orders);
                chart = ChartCreator.createLineChart("Orders per day", "Days", "Orders", (ArrayList<String>)values[0],
                        (ArrayList<Object>)values[1], "orders");

            }
        }
        catch (Exception e){
            System.err.println("Issue with creating graph.");
        }
        double sumOrders = 0;
        for(double value : yValues){
            sumOrders += value;
        }
        return new Object[]{chart, sumOrders};
    }
    public JPanel createBarChartFromOrder(String startDateS, String endDateS){
        ArrayList<String> orders = stat.getDates(startDateS, endDateS, "order"); //henter ordre mellom startdate og enddate

        try {
            // Brukes disse noe sted?
            Date startDate = getFormatter().parse(startDateS);
            Date endDate = getFormatter().parse(endDateS);

        } catch (ParseException e) {
            System.err.println("Issue with parsing date.");
            return null;
        }
        ArrayList<Object> yValues = new ArrayList<>(7); //for every day of week
        yValues.add(0);
        yValues.add(0);
        yValues.add(0);
        yValues.add(0);
        yValues.add(0);
        yValues.add(0);
        yValues.add(0);
        //from 1-7, 1 is sunday

        for(String order : orders){
            try {
                int index = getDayofWeek(getFormatter().parse(order)); //henter dayofweek
                yValues.set(index-1, (Integer)yValues.get(index-1)+1); //plusser en på index som korresponderer
            }
            catch (ParseException pe){
                System.err.println("Issue with parsing date.");
                pe.printStackTrace();
                return null;
            }
        }
        ArrayList<String> xValues = new ArrayList<>(7);
        xValues.add("Sunday");
        xValues.add("Monday");
        xValues.add("Tuesday");
        xValues.add("Wednesday");
        xValues.add("Thursday");
        xValues.add("Friday");
        xValues.add("Saturday");
        ChartPanel chart = ChartCreator.createBarChart("Orders by weekday", "Days", "Amount", xValues,
                yValues, "");

        return chart;

    }
}

/*
ID, day_name
1, Monday
2, Tuesday
3.
 */