package Subscription;

import Database.CustomerManagement;
import Database.OrderManagement;
import Database.SubscriptionManagement;
import sun.util.resources.cldr.rm.CalendarData_rm_CH;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Evdal on 16.03.2016.
 */
public class Subscriptions {

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    private static OrderManagement order = new OrderManagement();
    private static SubscriptionManagement subMan = new SubscriptionManagement();
    private static CustomerManagement custMan = new CustomerManagement();
    private static Calendar cal = new GregorianCalendar();


    public ArrayList listActiveSubs() {
        ArrayList<Object[]> subs = subMan.getSubscriptions();
        ArrayList<Object[]> activeSubs = new ArrayList<>();
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
            System.err.println("Issue with parsing dates in subscriptions.");
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
    EKSAMPLE : {{"recipe1", "recipe2", "recipe3"},{3, 5, 3 }, {1},{comment},{time}}
    This example says i want 3 portions of "recipe1", 5 of "recipe2" and 3 of "recipe3" on monday.
    The Object list should include three lists. 1. is recipes, 2. are portions and the last only include the day as an INT,
    where Monday is 1 and Sunday is 7.

    note = information about each order, special needs etc.
     */
    public static boolean createSubscription(String email, String dateFrom, String dateTo, int weeksBetween, ArrayList<Object[][]> recipesWithDay){
        return editSubscription(email, dateFrom,dateTo,weeksBetween,recipesWithDay,-1);
    }

    public static boolean editSubscription(String email, String dateFrom, String dateTo, int weeksBetween, ArrayList<Object[][]> recipesWithDay, int subId) {
        Object[] cust = custMan.getSingleCustomerInfo(email);
        int custID = (Integer)cust[5];
        int subID = subMan.createSubscription(custID, dateFrom, dateTo, weeksBetween, subId);
        if (!(subID > 0)) {
            return false;
        }
        String prevDate = dateFrom;
        boolean flag = true;
            // +++
        int count = 0;
        while (flag) {
            for (Object[][] obj : recipesWithDay) {
                ArrayList<Object[]> recipes = new ArrayList<>();
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
                if (!order.createOrderSub(custID, prevDate, recipes, (String)obj[3][0], (String)obj[4][0], subID)){
                    return false;
                }
                count++;

            }
        }
        return false;
    }


    private static String findNextDate(int day, String prevDateS, String dateTo, int frequency) {
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
            if(formatter.format(cal.getTime()).equals(dateTo)) {
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
    public Object[] getSubInfoFromId(int subId){
        Object[] out = new Object[5];
        Object[] info = subMan.getSubInfoFromId(subId);

        String customer = (String)info[0];
        String dateFrom = (String)info[1];
        String dateTo = (String)info[2];
        int frequency = (Integer) info[3];

        //obj[0] = recipeTableInfo, obj[1] = comment obj[2] = time, obj[3] = day
        ArrayList<Object[]> recipeInfo = getRecipeInfoForSub(subId);
        out[0] = recipeInfo;
        out[1] = customer;
        out[2] = dateFrom;
        out[3] = dateTo;
        out[4] = frequency;
        return out;


    }
    private ArrayList<Object[]> getRecipeInfoForSub(int id){

        //henter orders som er laget med en spesiell subscription
        ArrayList<Object[]> out = new ArrayList<Object[]>();
        ArrayList<Object[]> orders = subMan.getOrderInfoFromSub(id); //lagt til ekstra plass for å si hvilken dag
        //finner like ordere og forkorter slik at hver dag bare kommer en gang

        orders = shortenOrders(orders);

        if(!orders.isEmpty()){
            for (Object[] order : orders) {
                String date = formatter.format((Date) order[2]);//

                //henter info om oppskrifter for den dagen
                Object[] obj = subMan.getRecipeInfoFromSubAndDate(id, date);
                obj[3] = getDayOfWeek((Date) order[2]); //sender inn dagen sammen med oppskriftene
                out.add(obj);
                //obj[0] = recipeTableInfo, obj[1] = comment obj[2] = time, obj[3] = day
            }
        }

        return out;
    }
    private ArrayList<Object[]> shortenOrders(ArrayList<Object[]> orders){
        ArrayList<Object[]> out = new ArrayList<>();
        for(Object[] order : orders){
            boolean added = false;
            for(Object[] o : out){
                if(sameDay((Date)order[2],(Date)o[2])){
                    added = true;
                }
            }
            if(!added){
                out.add(order);
            }
        }
        return out;
    }
    private boolean sameDay(Date date1, Date date2){
        cal.setTime(date1);
        int day1 = cal.get(Calendar.DAY_OF_WEEK);
        cal.setTime(date2);
        int day2 = cal.get(Calendar.DAY_OF_WEEK);
        if(day1 == day2) return true;
        else return false;
    }
    private int getDayOfWeek(Date date){
        cal.setTime(date);
        int day = cal.get(Calendar.DAY_OF_WEEK);
        if(day == 7){
            day = 1; //fra søndag som første dag til mandag som første dag.
        }
        else{
            day++;
        }
        return day;
    }

}