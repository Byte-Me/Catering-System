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

    /**
     *
     * @param to
     * @param from
     * @return
     */
    protected int checkDaysBetween(Date to, Date from){
        long diff = from.getTime() - to.getTime();
        return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    /**
     *
     * @param values
     * @return
     */
    protected ArrayList[]valuesMonth(ArrayList<String> values){
        ArrayList<Double> yValues = new ArrayList<>();
        ArrayList<String> xValues = new ArrayList<>();
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

    /**
     *
     * @param values
     * @return
     */
    protected ArrayList[] valuesWeek(ArrayList<String> values){
        ArrayList<Double> yValues = new ArrayList<>();
        ArrayList<String> xValues = new ArrayList<>();
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

    /**
     *
     * @param values
     * @return
     */
    protected ArrayList[] valuesDay(ArrayList<String> values){
        ArrayList<Double> yValues = new ArrayList<>();
        ArrayList<String> xValues = new ArrayList<>();
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

    /**
     *
     * @param date
     * @return
     */
    protected int getDayofWeek(Date date){ //return ints from 1-7, monday - sunday
        cal.setTime(date);
        if (cal.get(cal.DAY_OF_WEEK) == 1) { // Move sunday til end of week
            return 7;
        }
        return cal.get(cal.DAY_OF_WEEK) - 1; // Rest of weekdays moved one position

    }

    /**
     *
     * @param date
     * @param time
     * @return
     */
    protected Date nextDate(Date date, int time){ //Calendar int, month, year, week, day.
        cal.setTime(date);
        cal.add(time, 1);
        return cal.getTime();
    }

    /**
     *
     * @param date1
     * @param date2
     * @return
     */
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

    /**
     *
     * @param date
     * @return
     */
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

    /**
     *
     * @param date
     * @return
     */
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

    /**
     *
     * @param date1
     * @param date2
     * @return
     */
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

    /**
     *
     * @param date1
     * @param date2
     * @return
     */
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

    /**
     *
     * @return
     */
    protected Calendar getCal() {
        return cal;
    }

    /**
     *
     * @return
     */
    protected SimpleDateFormat getFormatter() {
        return formatter;
    }
}
