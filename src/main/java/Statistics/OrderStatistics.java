package Statistics;

import Database.StatisticsManagement;
import Statistics.graph.ChartCreator;
import org.jfree.chart.ChartPanel;

import javax.swing.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

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

    /**
     *
     * @param startDateS
     * @param endDateS
     * @return
     */
    public Object[] createLineChartFromOrder(String startDateS, String endDateS) { //[0] = JFreeChart, [1] = SumOrders
        ArrayList<String> orders = stat.getDates(startDateS, endDateS, "order");
        JPanel chart = null;
        if (orders.isEmpty()) {
            chart = ChartCreator.createLineChart("No orders found", "", "", new ArrayList<>(),
                    new ArrayList<>(), "orders");
        }
        else {
            ArrayList<Double> yValues = new ArrayList<>();
            Date startDate;
            Date endDate;
            try {
                startDate = getFormatter().parse(startDateS);
                endDate = getFormatter().parse(endDateS);

            } catch (ParseException e) {
                System.err.println("Issue with parsing date.");
                return null;
            }

            try {
                int timeDiff = checkDaysBetween(startDate, endDate);

                if (timeDiff > MONTHLIMIT) {
                    ArrayList[] values = valuesMonth(orders);
                    chart = ChartCreator.createLineChart("Orders", "Months", "Orders per month", (ArrayList<String>) values[0],
                            (ArrayList<Object>) values[1], "orders");
                } else if (timeDiff > WEEKLIMIT) {
                    ArrayList[] values = valuesWeek(orders);
                    chart = ChartCreator.createLineChart("Orders", "Weeks", "Orders per week", (ArrayList<String>) values[0],
                            (ArrayList<Object>) values[1], "orders");

                } else {
                    ArrayList[] values = valuesDay(orders);
                    chart = ChartCreator.createLineChart("Orders", "Days", "Orders per day", (ArrayList<String>) values[0],
                            (ArrayList<Object>) values[1], "orders");

                }
            } catch (Exception e) {
                System.err.println("Issue with creating graph.");
            }

        }
        return new Object[]{chart, orders.size()};
    }

    /**
     *
     * @param startDateS
     * @param endDateS
     * @return
     */
    public JPanel createBarChartFromOrder(String startDateS, String endDateS){
        ArrayList<String> orders = stat.getDates(startDateS, endDateS, "order"); //henter ordre mellom startdate og enddate

        ChartPanel chart;
        if(orders.isEmpty()){
            chart = ChartCreator.createBarChart("No orders found", "", "", new ArrayList<>(),
                    new ArrayList<>(), "");
        } else {
            ArrayList<Object> yValues = new ArrayList<>(7); //for every day of week
            yValues.add(0);
            yValues.add(0);
            yValues.add(0);
            yValues.add(0);
            yValues.add(0);
            yValues.add(0);
            yValues.add(0);
            //from 1-7, 1 is sunday

            for (String order : orders) {
                try {
                    int index = getDayofWeek(getFormatter().parse(order)); //henter dayofweek
                    yValues.set(index - 1, (Integer) yValues.get(index - 1) + 1); //plusser en på index som korresponderer
                } catch (ParseException pe) {
                    System.err.println("Issue with parsing date.");
                    pe.printStackTrace();
                    return null;
                }
            }
            ArrayList<String> xValues = new ArrayList<>(7);
            xValues.add("Monday");
            xValues.add("Tuesday");
            xValues.add("Wednesday");
            xValues.add("Thursday");
            xValues.add("Friday");
            xValues.add("Saturday");
            xValues.add("Sunday");
            chart = ChartCreator.createBarChart("Orders by weekday", "Days", "Amount", xValues,
                    yValues, "");
        }
        chart.setDomainZoomable(false); // Remove zooming
        chart.setRangeZoomable(false); // Remove zooming
        chart.setPopupMenu(null); // Remove Popup menu
        return chart;

    }

    /**
     *
     * @param startDateS
     * @param endDateS
     * @return
     */
    public int getCancelledOrders(String startDateS, String endDateS){
        ArrayList<String> orders = stat.getCancelledDates(startDateS, endDateS, "order");
        return orders.size();
    }
}

/*
ID, day_name
1, Monday
2, Tuesday
3.
 */