package Database;

import org.apache.commons.dbutils.DbUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Evdal on 15.03.2016.
 */
public class StatisticsManagement extends Management{

    /**
     * Constructor of StatisticsManagement.
     */
    public StatisticsManagement(){
        super();
    }
    /*
    Antall kunder per tid;
    Inntekter per tid
    Antall ordre per tid
*/

    Connection conn = null;
    ResultSet res = null;
    PreparedStatement prep = null;

    /**
     * List of finance info.
     * @param firstDate First date.
     * @param lastDate Last date.
     * @return
     */
    public ArrayList<long[]> getFinanceInfo(String firstDate, String lastDate){
        ArrayList<long[]> out = new ArrayList<>();
        if(setUp()) {
            try {
                conn = getConnection();
                prep = conn.prepareStatement("SELECT income, outcome from `finance` where `date` >= ? AND `date` <= ? order by date;");
                prep.setString(1, firstDate);
                prep.setString(2, lastDate);
                res = prep.executeQuery();

                while (res.next()) {
                    long[] dou = new long[2];
                    dou[0] = res.getLong("income");
                    dou[1] = res.getLong("outcome");
                    out.add(dou);
                }
            } catch (Exception e) {
                System.err.println("Issue with getting finance from db");
                return null;
            } finally {
                finallyStatement(res, prep);
            }
        }
        return out;
    }

    /**
     * List of dates.
     * @param firstDate First date.
     * @param lastDate Last date.
     * @param name Name.
     * @return
     */
    public ArrayList<String> getDates(String firstDate, String lastDate, String name){
        ArrayList<String> out = new ArrayList<>();
        if(setUp()) {
            try {
                conn = getConnection();                         // User can't set name of database
                prep = conn.prepareStatement("SELECT date FROM `" + name + "` WHERE date >= ? AND date <= ? AND status > 0 ORDER BY date");
                prep.setString(1, firstDate);
                prep.setString(2, lastDate);
                res = prep.executeQuery();
                while (res.next()) {
                    out.add(res.getString("date"));
                }
            } catch (Exception e) {
                System.err.println("Issue with getting dates");
                return null;
            } finally {
                finallyStatement(res, prep);
            }
        }
        return out;
    }

    /**
     * Gets cancelled dates.
     * @param firstDate First date.
     * @param lastDate Last date.
     * @param name Name.
     * @return
     */
    public ArrayList<String> getCancelledDates(String firstDate, String lastDate, String name){
        ArrayList<String> out = new ArrayList<>();
        if(setUp()) {
            try {
                conn = getConnection();
                prep = conn.prepareStatement("SELECT date from `" + name + "` where `date` >= ? AND `date` <= ? AND status = 0 order by date;");
                prep.setString(1, firstDate);
                prep.setString(2, lastDate);
                res = prep.executeQuery();
                while (res.next()) {
                    out.add(res.getString("date"));
                }
            } catch (Exception e) {
                System.err.println("Issue with getting dates");
                return null;
            } finally {
                finallyStatement(res, prep);
            }
        }
        return out;
    }

    /**
     * Gets subscription dates.
     * @return
     */
    public ArrayList<String[]> getSubDates(){
        ArrayList<String[]> out = new ArrayList<>();
        if(setUp()){
            try{
                conn = getConnection();
                prep = conn.prepareStatement("SELECT date_from, date_to FROM subscription;");
                res = prep.executeQuery();
                while(res.next()){
                    out.add(new String[]{res.getString("date_from"), res.getString("date_to")});
                }
            }
            catch (SQLException e){
                System.err.println("Issue with getting subscription dates.");
                return null;
            }
            finally {
                finallyStatement(res, prep);
            }
        }
        return out;
    }
}
