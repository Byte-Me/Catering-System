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
    public int getSubCount(String dateFrom, String dateTo) {
        ArrayList<String> subscription = stats.getDates(dateFrom, dateTo, "subscription"); //kunne gjort i sql
        return subscription.size();
    }
    public int getCancelledSubCount(String dateFrom, String dateTo) {
        ArrayList<String> subscription = stats.getCancelledDates(dateFrom, dateTo, "subscription"); //kunne gjort i sql
        return subscription.size();
    }
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

    // FIXME: Veit ikke om eg kan slett denna
    {

        /*    Date from = null;
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
                    (ArrayList<Object>) values[1], "subscriptions");
        }
        else if(dayBetween > WEEKLIMIT){
            ArrayList[] values = valuesWeek(subscription);
            chart = ChartCreator.createLineChart("Subscriptions", "Weeks", "Amount", (ArrayList<String>)values[0],
                    (ArrayList<Object>)values[1], "subscriptions");
        }
        else{
            ArrayList[] values = valuesDay(subscription);
            chart = ChartCreator.createLineChart("Subscriptions", "Days", "Amount", (ArrayList<String>)values[0],
                    (ArrayList<Object>)values[1], "subscriptions");
        }
        return chart;
    }
    */
    }
}
