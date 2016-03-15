package Statistics;

import Database.StatisticsManagement;
import GUI.graph.ChartCreator;
import org.jfree.chart.JFreeChart;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Evdal on 15.03.2016.
 *
 * //TODO metoden som lager grafen kan ta med alle dager/uker/mnd selv om de ikke har ordre i seg.
 *
 */
public class OrderStatistics {
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    private Calendar cal = new GregorianCalendar();

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

    private int checkDaysBetween(String date1, String date2) throws Exception {
        Date d1 = formatter.parse(date1);
        Date d2 = formatter.parse(date2);
        long diff = d2.getTime() - d1.getTime();
        return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }


    public boolean isSameDay(String date1, String date2)throws Exception{
        Date start = formatter.parse(date1);
        Date end = formatter.parse(date2);
        cal.setTime(start);
        int day1 = cal.get(Calendar.DAY_OF_YEAR);
        cal.setTime(end);
        int day2 = cal.get(Calendar.DAY_OF_YEAR);

        if(day1 == day2) return true;
        else return false;
    }
    private String getMonthName(String date)throws Exception{
        Date tmp = formatter.parse(date);
        cal.setTime(tmp);
        return cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());

    }
    private String getWeekName(String date)throws Exception{
        Date tmp = formatter.parse(date);
        cal.setTime(tmp);
        return "" + cal.get(Calendar.WEEK_OF_YEAR);

    }

    public boolean isSameWeek(String date1, String date2)throws Exception{
        Date start = formatter.parse(date1);
        Date end = formatter.parse(date2);
        cal.setTime(start);
        int week1 = cal.get(Calendar.WEEK_OF_YEAR);
        cal.setTime(end);
        int week2 = cal.get(Calendar.WEEK_OF_YEAR);

        if(week1 == week2) return true;
        else return false;
    }
    public boolean isSameMonth(String date1, String date2)throws Exception{
        Date start = formatter.parse(date1);
        Date end = formatter.parse(date2);
        cal.setTime(start);
        int month1 = cal.get(Calendar.MONTH);
        cal.setTime(end);
        int month2 = cal.get(Calendar.MONTH);

        if(month1 == month2) return true;
        else return false;
    }



}
