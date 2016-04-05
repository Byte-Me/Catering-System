package Database;

import org.apache.commons.dbutils.DbUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
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

     */ //TODO: slett når klassen er blitt testet.
    public ArrayList<String> getOrders(String firstDate, String lastDate){
        ResultSet res;
        ArrayList<String> out = new ArrayList<String>();
        if(setUp()) {
            try {
                res = getScentence().executeQuery("SELECT `order`.date from `order` where `date` >= DATE '" + firstDate +
                        "' AND `date` <= DATE '" + lastDate + "' order by date;");

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
//TODO: Ikke testet 19.03.2016
    public ArrayList<String> getDates(String firstDate, String lastDate, String name){
        ResultSet res;
        ArrayList<String> out = new ArrayList<String>();
        if(setUp()) {
            try {
                res = getScentence().executeQuery("SELECT `"+name+"`.date from `"+name+"` where `date` >= DATE '" + firstDate +
                        "' AND `date` <= DATE '" + lastDate + "' order by date;");

                while (res.next()) {
                    out.add(res.getString("date"));
                }
            } catch (Exception e) {
                System.err.println("Issue with getting customers");
                return null;
            } finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        return out;
    }

    public ArrayList<String[]> getSubDates(){
        ArrayList<String[]> out = new ArrayList<String[]>();
        if(setUp()){
            try{
                ResultSet res = getScentence().executeQuery("SELECT date_from, date_to FROM subscription;");
                while(res.next()){
                    out.add(new String[]{res.getString("date_from"), res.getString("date_to")});
                }
            }
            catch (SQLException e){
                System.err.println("Issue with getting subscription dates.");
                return null;
            }
            finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        return out;
    }
}
