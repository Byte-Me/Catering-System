package Statistics;

import Database.StatisticsManagement;
import Statistics.graph.ChartCreator;

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

    public OrderStatistics(){
        super();
    }



    public Object[] createStatsFromOrders(String startDateS, String endDateS) { //[0] = JFreeChart, [1] = SumOrders
        StatisticsManagement stat = new StatisticsManagement();
        ArrayList<String> orders = stat.getDates(startDateS, endDateS, "order");
        ArrayList<Double> yValues = new ArrayList<Double>();
        ArrayList<String> xValues = new ArrayList<String>();
        String curDate = "";
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = getFormatter().parse(startDateS);
            endDate = getFormatter().parse(endDateS);

        } catch (ParseException e) {
            System.err.println("Issue with parsing date.");
            return null;
        }

        JPanel chart = null;
        int count = 1;

        try { //TODO: Ikke testet 19-03-2016.
            int timeDiff = checkDaysBetween(startDate, endDate);

            if (timeDiff > MONTHLIMIT) {
                System.out.println("Hei");
                ArrayList[] values = valuesMonth(orders);

                chart = ChartCreator.createLineChart("Orders", "Months", "Orders per month", (ArrayList<String>)values[0],
                        (ArrayList<Double>)values[1], "orders");
            }
            else if(timeDiff > WEEKLIMIT){
                ArrayList[] values = valuesWeek(orders);
                chart = ChartCreator.createLineChart("Orders", "Weeks", "Orders per week", (ArrayList<String>)values[0],
                        (ArrayList<Double>)values[1], "orders");

            }
            else {
                ArrayList[] values = valuesDay(orders);
                chart = ChartCreator.createLineChart("Orders", "Days", "Orders per day", (ArrayList<String>)values[0],
                        (ArrayList<Double>)values[1], "orders");

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
}

/*
ID, day_name
1, Monday
2, Tuesday
3.
 */