package Util.Statistics;

import Database.StatisticsManagement;
import Util.Statistics.graph.ChartCreator;

import javax.swing.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Evdal on 18.03.2016.
 */
public class CustomerStatistics extends Statistics{

    private StatisticsManagement stats = new StatisticsManagement();

    /**
     * JPanel for customer line graph.
     * @param dateFrom From date.
     * @param dateTo To date.
     * @return
     */
    public JPanel customerLineGraph(String dateFrom, String dateTo){
        ArrayList<String> customers = stats.getDates(dateFrom,dateTo,"customer");
        Date from = null;
        Date to = null;
        JPanel chart = null;
        try {
            from = getFormatter().parse(dateFrom);
            to = getFormatter().parse(dateTo);
        }
        catch (ParseException e){
            System.err.println("Issue with parsing dates.");
        }
        int dayBetween = checkDaysBetween(to, from);
        if(dayBetween > MONTHLIMIT){
            ArrayList[] values = valuesMonth(customers);
            chart = ChartCreator.createLineChart("Customers", "Months", "Amount", (ArrayList<String>)values[0],
                    (ArrayList<Object>)values[1], "customers");
        }
        else if(dayBetween > WEEKLIMIT){
            ArrayList[] values = valuesWeek(customers);
            chart = ChartCreator.createLineChart("Customers", "Weeks", "Amount", (ArrayList<String>)values[0],
                    (ArrayList<Object>)values[1], "customers");
        }
        else{
            ArrayList[] values = valuesDay(customers);
            chart = ChartCreator.createLineChart("Customers", "Days", "Amount", (ArrayList<String>)values[0],
                    (ArrayList<Object>)values[1], "customers");
        }
        return chart;


    }
}
