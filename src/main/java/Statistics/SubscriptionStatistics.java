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

    /**
     *
     * @param dateFrom
     * @param dateTo
     * @return
     */
    public int getSubCount(String dateFrom, String dateTo) {
        ArrayList<String> subscription = stats.getDates(dateFrom, dateTo, "subscription"); //kunne gjort i sql
        return subscription.size();
    }

    /**
     *
     * @param dateFrom
     * @param dateTo
     * @return
     */
    public int getCancelledSubCount(String dateFrom, String dateTo) {
        ArrayList<String> subscription = stats.getCancelledDates(dateFrom, dateTo, "subscription"); //kunne gjort i sql
        return subscription.size();
    }

    /**
     *
     * @param dateFrom
     * @param dateTo
     * @return
     */
    public int getActiveSubCount(String dateFrom, String dateTo) {
        ArrayList<String[]> subscription = stats.getSubDates(); //kunne gjort i sql
        int count = 0;
        for(String[] sub : subscription){
            if(subs.checkSubscriptionActive(sub[0],sub[1],new Date())){
                count++;
            }
        }
        return count;
    }

    /**
     *
     * @param dateFrom
     * @param dateTo
     * @return
     */
    public JPanel subActiveLineGraph(String dateFrom, String dateTo){
        ArrayList<String[]> subscription = stats.getSubDates();
        Date from = null;
        Date to = null;
        JPanel chart = null;
        ArrayList<String> xValues = new ArrayList<String>();
        ArrayList<Object> yValues = new ArrayList<>();


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
