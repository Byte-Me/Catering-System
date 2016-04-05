package Statistics;

import Database.StatisticsManagement;
import Database.SubscriptionManagement;
import Statistics.graph.ChartCreator;
import Subscription.Subscriptions;

import javax.swing.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Evdal on 19.03.2016.
 */
public class SubscriptionStatistics extends Statistics{
    //TODO: Subscriptions må ha en dato for når de ble opprettet.
    private StatisticsManagement stats = new StatisticsManagement();
    private SubscriptionManagement subMan = new SubscriptionManagement();
    private Subscriptions subs = new Subscriptions();
    public JPanel subCreatedLineGraph(String dateFrom, String dateTo){
        ArrayList<String> subscription = stats.getDates(dateFrom,dateTo,"subscription");
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
        if(dayBetween > MONTHLIMIT) {
            ArrayList[] values = valuesMonth(subscription);
            chart = ChartCreator.createLineChart("Subscriptions", "Months", "Amount", (ArrayList<String>) values[0],
                    (ArrayList<Double>) values[1], "subscriptions");
        }
        else if(dayBetween > WEEKLIMIT){
            ArrayList[] values = valuesWeek(subscription);
            chart = ChartCreator.createLineChart("Subscriptions", "Weeks", "Amount", (ArrayList<String>)values[0],
                    (ArrayList<Double>)values[1], "subscriptions");
        }
        else{
            ArrayList[] values = valuesDay(subscription);
            chart = ChartCreator.createLineChart("Subscriptions", "Days", "Amount", (ArrayList<String>)values[0],
                    (ArrayList<Double>)values[1], "subscriptions");
        }
        return chart;
    }
    public JPanel subActiveLineGraph(String dateFrom, String dateTo){
        ArrayList<String[]> subscription = stats.getSubDates();
        Date from = null;
        Date to = null;
        JPanel chart = null;
        ArrayList<String> xValues = new ArrayList<String>();
        ArrayList<Double> yValues = new ArrayList<Double>();


        try {
            from = getFormatter().parse(dateFrom);
            to = getFormatter().parse(dateTo);
        }
        catch (ParseException e){
            System.err.println("Issue with parsing dates.");
        }
        int daysBetween = checkDaysBetween(to, from);
        int subActive = 0;

        if(daysBetween > MONTHLIMIT) {
            boolean flag = true;
            while(flag) {
                xValues.add(getMonthName(getFormatter().format(from)));
                for (String[] sub : subscription) {
                    if (subs.checkSubscriptionActive(sub[0], sub[1], from)) {
                        subActive++;
                    }
                }
                yValues.add(Double.valueOf(subActive));
                subActive = 0;
                from = nextDate(from, Calendar.MONTH);
                if (from.before(to)) {
                    return ChartCreator.createLineChart("Active Subscriptions", "Months", "Amount", xValues,
                            yValues, "active subscriptions");
                }
            }
        }
        else if(daysBetween > WEEKLIMIT) {
            boolean flag = true;
            while(flag) {
                xValues.add(getWeekName(getFormatter().format(from)));
                for (String[] sub : subscription) {
                    if (subs.checkSubscriptionActive(sub[0], sub[1], from)) {
                        subActive++;
                    }
                }
                yValues.add(Double.valueOf(subActive));
                subActive = 0;
                from = nextDate(from, Calendar.WEEK_OF_YEAR);
                if (from.before(to)) {
                    return ChartCreator.createLineChart("Active Subscriptions", "Weeks", "Amount", xValues,
                            yValues, "active subscriptions");
                }
            }
        }
        else {
            boolean flag = true;
            while(flag) {
                xValues.add(getFormatter().format(from));
                for (String[] sub : subscription) {
                    if (subs.checkSubscriptionActive(sub[0], sub[1], from)) {
                        subActive++;
                    }
                }
                yValues.add(Double.valueOf(subActive));
                subActive = 0;
                from = nextDate(from, Calendar.WEEK_OF_YEAR);
                if (from.before(to)) {
                    return ChartCreator.createLineChart("Active Subscriptions", "Weeks", "Amount", xValues,
                            yValues, "active subscriptions");
                }
            }
        }
        return null;
    }

}