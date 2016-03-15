package Database;

import org.apache.commons.dbutils.DbUtils;

import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Created by Evdal on 15.03.2016.
 */
public class StatisticsManagement extends Management{
    public StatisticsManagement(){
        super();
    }
    /*
    Antall kunder per tid;
    Inntekter per tid
    Antall ordre per tid

     */
    public ArrayList<String> getOrders(String firstDate, String lastDate){
        ResultSet res;
        ArrayList<String> out = new ArrayList<String>();
        if(setUp()) {
            try {
                System.err.println("SELECT `order`.date from `order` where `date` >= DATE '" + firstDate +
                        "' AND `date` <= DATE '" + lastDate + "' order by date;");

                res = getScentence().executeQuery("SELECT `order`.date from `order` where `date` >= DATE '" + firstDate +
                        "' AND `date` <= DATE '" + lastDate + "' order by date;");
                System.out.println(out + "Hei");

                while (res.next()) {
                    out.add(res.getString("date"));
                }
            } catch (Exception e) {
                System.err.println("Issue with getting orders");
                return null;
            } finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        return out;
    }
}
