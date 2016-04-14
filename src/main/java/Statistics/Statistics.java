package Statistics;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Evdal on 15.03.2016.
 */
public abstract class Statistics {
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    private Calendar cal = new GregorianCalendar();
    protected static int MONTHLIMIT = 200;
    protected static int WEEKLIMIT = 20;

    protected int checkDaysBetween(Date to, Date from){
        long diff = from.getTime() - to.getTime();
        return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }
    protected ArrayList[]valuesMonth(ArrayList<String> values){
        ArrayList<Double> yValues = new ArrayList<Double>();
        ArrayList<String> xValues = new ArrayList<String>();
        String curDate = values.get(0);
        int count;

        try {
            while(formatter.parse(curDate).before(formatter.parse(values.get(values.size()-1)))) {
                count = 0;
                for (int i = 0; i < values.size(); i++) {
                    if (isSameMonth(values.get(i), curDate)) {
                        count++;
                    }
                }
                xValues.add(getMonthName(curDate));
                yValues.add(Double.valueOf(count));
                curDate = formatter.format(nextDate(formatter.parse(curDate), Calendar.MONTH));

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new ArrayList[]{xValues, yValues};
    }
    protected ArrayList[] valuesWeek(ArrayList<String> values){
        ArrayList<Double> yValues = new ArrayList<Double>();
        ArrayList<String> xValues = new ArrayList<String>();
        String curDate = values.get(0);
        int count;
        try {
            while(formatter.parse(curDate).before(formatter.parse(values.get(values.size()-1)))) {
                count = 0;
                for (int i = 0; i < values.size(); i++) {
                    if (isSameWeek(values.get(i), curDate)) {
                        count++;
                    }
                }
                xValues.add(getWeekName(curDate));
                yValues.add(Double.valueOf(count));
                curDate = formatter.format(nextDate(formatter.parse(curDate), Calendar.WEEK_OF_YEAR ));

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new ArrayList[]{xValues, yValues};
    }
    protected ArrayList[] valuesDay(ArrayList<String> values){
        ArrayList<Double> yValues = new ArrayList<Double>();
        ArrayList<String> xValues = new ArrayList<String>();
        String curDate = values.get(0);
        int count;
        try {
            while(formatter.parse(curDate).before(formatter.parse(values.get(values.size()-1)))) {
                count = 0;
                for (int i = 0; i < values.size(); i++) {
                    if (isSameDay(values.get(i), curDate)) {
                        count++;
                    }
                }
                xValues.add((curDate));
                yValues.add(Double.valueOf(count));
                curDate = formatter.format(nextDate(formatter.parse(curDate), Calendar.DAY_OF_YEAR ));

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new ArrayList[]{xValues, yValues};
    }
    protected int getDayofWeek(Date date){ //return ints from 1-7, sunday is 1.
        cal.setTime(date);
        return cal.get(cal.DAY_OF_WEEK);

    }
    protected Date nextDate(Date date, int time){ //Calendar int, month, year, week, day.
        cal.setTime(date);
        cal.add(time, 1);
        return cal.getTime();
    }


    protected boolean isSameDay(String date1, String date2){
        Date start = null;
        Date end = null;
        try {
            start = formatter.parse(date1);
            end = formatter.parse(date2);
        } catch (ParseException e) {
            System.err.println("Issue with parsing dates.");
        }
        cal.setTime(start);
        int day1 = cal.get(Calendar.DAY_OF_YEAR);
        cal.setTime(end);
        int day2 = cal.get(Calendar.DAY_OF_YEAR);

        if(day1 == day2) return true;
        else return false;
    }
    protected String getMonthName(String date){
        Date tmp = null;
        try {
            tmp = formatter.parse(date);
        } catch (ParseException e) {
            System.err.println("Issue with parsing dates.");
        }
        cal.setTime(tmp);
        return cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())+" "+cal.get(Calendar.YEAR);

    }
    protected String getWeekName(String date){

        Date tmp = null;
        try {
            tmp = formatter.parse(date);
        } catch (ParseException e) {
            System.err.println("Issue with parsing dates.");
        }
        cal.setTime(tmp);
        return "" + cal.get(Calendar.WEEK_OF_YEAR);

    }

    protected boolean isSameWeek(String date1, String date2){
        Date start = null;
        Date end = null;
        try {

            start = formatter.parse(date1);
            end = formatter.parse(date2);
        }
        catch (ParseException e){
            System.err.println("Issue with parsing dates.");
        }
        cal.setTime(start);
        int week1 = cal.get(Calendar.WEEK_OF_YEAR);
        cal.setTime(end);
        int week2 = cal.get(Calendar.WEEK_OF_YEAR);

        if(week1 == week2) return true;
        else return false;
    }
    protected boolean isSameMonth(String date1, String date2){
        Date start = null;
        Date end = null;
        try {
            start = formatter.parse(date1);
            end = formatter.parse(date2);
        } catch (ParseException e) {
            System.err.println("Issue with parsing dates.");
        }
        cal.setTime(start);
        int month1 = cal.get(Calendar.MONTH);
        cal.setTime(end);
        int month2 = cal.get(Calendar.MONTH);

        if(month1 == month2) return true;
        else return false;
    }

    protected Calendar getCal() {
        return cal;
    }

    protected SimpleDateFormat getFormatter() {
        return formatter;
    }
}
