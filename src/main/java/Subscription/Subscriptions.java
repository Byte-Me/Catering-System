package Subscription;

import Database.OrderManagement;
import Database.SubscriptionManagement;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static java.util.Date.*;

/**
 * Created by Evdal on 16.03.2016.
 */
public class Subscriptions {

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    private OrderManagement order = new OrderManagement();
    private SubscriptionManagement subMan = new SubscriptionManagement();

    public ArrayList listActiveSubs() {
        ArrayList<Object[]> subs = subMan.getSubscriptions();
        ArrayList<Object[]> activeSubs = new ArrayList<Object[]>();
        for (Object[] sub : subs) {
            if (checkSubscriptionActive((String) sub[2], (String) sub[3], new Date())) { //2 = dayfrom, 3 = dayto
                activeSubs.add(sub);
            }
        }
        return activeSubs;
    }

    public boolean checkSubscriptionActive(String dateFrom, String dateTo, Date today) {
        Date from = null;
        Date to = null;
        try {
            from = formatter.parse(dateFrom);
            to = formatter.parse(dateTo);
        } catch (ParseException e) {
            System.out.println("Issue with parsing dates in subscriptions.");
        }
        if (from.before(today) && to.after(today)) {
            return true;
        } else return false;
    }

    private int containsSubOrder(int subId) {
        return subMan.containsSubOrder(subId);//   recipesWithDay {{"recipe1", "recipe2", "recipe3"},{3, 5, 3 }, {1}} //[recipes], [portions of recipe], [day]
    }

    /*
    createSubscription constructor info:

    custID = ID for customer to whom the subscription is made for.

    dateFrom = yyyy/mm/dd for when det subscription will start. If the date is for instance a monday and the
    customers first order is on a friday. The orders will start next time that day occures.

    dateTo = yyyy/mm/dd for when the subscriptions ends.

    weeksBetween = the frequency of orders in weeks. If a customer wants an order every Monday, the frequency is 1.
    If the customer wants every 4th monday (every month) the frequency is 4.

    recipesWithDay = An arraylist with information about all recipes and their portion size as well as the day for the recipes.
    EKSAMPLE : {{"recipe1", "recipe2", "recipe3"},{3, 5, 3 }, {1}}
    This example says i want 3 portions of "recipe1", 5 of "recipe2" and 3 of "recipe3" on monday.
    The Object list should include three lists. 1. is recipes, 2. are portions and the last only include the day as an INT,
    where Monday is 1 and Sunday is 7.

    note = information about each order, special needs etc.
     */

    public boolean createSubscription(int custID, String dateFrom, String dateTo, int weeksBetween, ArrayList<Object[][]> recipesWithDay,
                                      String note, String time) {
        int subID = subMan.createSubscription(custID, dateFrom, dateTo, weeksBetween);
        if (!(subID > 0)) {
            System.out.println("FAGGOTS");
            return false;
        }
        String prevDate = dateFrom;
        boolean flag = true;
            // +++
        while (flag) {
            for (Object[][] obj : recipesWithDay) {
                ArrayList<Object[]> recipes = new ArrayList<Object[]>();
                for (int i = 0; i < obj[0].length; i++) {
                    Object[] tmp = new Object[2];
                    tmp[0] = obj[0][i];
                    tmp[1] = obj[1][i];
                    recipes.add(tmp);

                }


                prevDate = findNextDate((Integer) obj[2][0], prevDate, dateTo, weeksBetween);
                if (prevDate.equals("")){
                    return true;
                }

                if (!order.createOrderSub(custID, prevDate, recipes, note, time, subID)){
                    return false;
                }

            }
        }
        return false;
    }


    private String findNextDate(int day, String prevDateS, String dateTo, int frequency) {
        Date prevDate = null;
        try {
            prevDate = formatter.parse(prevDateS);
        } catch (ParseException e) {
            System.err.println("Issue with parsing date in subscription.");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(prevDate);
        if (day == 7) {
            day = 1;
        }               //SLIK UKEDAGENE ER SATT OPP CALENDAR.DAY_OF_WEEK
        else {
            day += 1;
        }
        int count = 0;

        boolean stillNotEndOfWeek = true;
        while (frequency >= count) {
            System.out.println(formatter.format(cal.getTime()));
            if(formatter.format(cal.getTime()).equals(dateTo)) {
                //System.out.println(dateTo);
                return "";
            }
            cal.add(Calendar.DAY_OF_YEAR, 1);
            if (cal.getFirstDayOfWeek() == cal.get(Calendar.DAY_OF_WEEK)) stillNotEndOfWeek = false;
            System.out.println("Current day: " + cal.get(Calendar.DAY_OF_WEEK) + " Goal day: " + day);
            if (cal.get(Calendar.DAY_OF_WEEK) == day) { //TODO: DAY MÅ VÆRE PÅ SAMME FORM SOM CALENDAR!!!!
                if (count == frequency - 1 || stillNotEndOfWeek) {
                    return formatter.format(cal.getTime());
                }
                else{
                    count++;
                }
            }
        }
        return null;
    }

}
