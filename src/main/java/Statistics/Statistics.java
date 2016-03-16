package Statistics;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Evdal on 15.03.2016.
 */
public abstract class Statistics {
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    private Calendar cal = new GregorianCalendar();

    protected int checkDaysBetween(String date1, String date2) throws Exception {
        Date d1 = formatter.parse(date1);
        Date d2 = formatter.parse(date2);
        long diff = d2.getTime() - d1.getTime();
        return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }


    protected boolean isSameDay(String date1, String date2)throws Exception{
        Date start = formatter.parse(date1);
        Date end = formatter.parse(date2);
        cal.setTime(start);
        int day1 = cal.get(Calendar.DAY_OF_YEAR);
        cal.setTime(end);
        int day2 = cal.get(Calendar.DAY_OF_YEAR);

        if(day1 == day2) return true;
        else return false;
    }
    protected String getMonthName(String date)throws Exception{
        Date tmp = formatter.parse(date);
        cal.setTime(tmp);
        return cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());

    }
    protected String getWeekName(String date)throws Exception{
        Date tmp = formatter.parse(date);
        cal.setTime(tmp);
        return "" + cal.get(Calendar.WEEK_OF_YEAR);

    }

    protected boolean isSameWeek(String date1, String date2)throws Exception{
        Date start = formatter.parse(date1);
        Date end = formatter.parse(date2);
        cal.setTime(start);
        int week1 = cal.get(Calendar.WEEK_OF_YEAR);
        cal.setTime(end);
        int week2 = cal.get(Calendar.WEEK_OF_YEAR);

        if(week1 == week2) return true;
        else return false;
    }
    protected boolean isSameMonth(String date1, String date2)throws Exception{
        Date start = formatter.parse(date1);
        Date end = formatter.parse(date2);
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
