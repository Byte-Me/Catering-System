package Database;

import org.apache.commons.dbutils.DbUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Evdal on 09.04.2016.
 */
public class FinanceManagement extends Management{
    public FinanceManagement(){
        super();
    }
    public boolean addIncomeToDatabase(double income){
        if(setUp()){
            try{
                PreparedStatement prep = getConnection().prepareStatement("INSERT INTO finance VALUES(?,0,CURRENT_DATE)");
                prep.setDouble(1, income);
                if(prep.executeUpdate() <= 0)return false;

            }catch (SQLException e){
                System.err.println("Issue with adding income to database.");
                return false;
            }
            finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        return true;
    }
    public boolean addOutcomeToDatabase(double outcome){
        if(setUp()){
            try{                                                                                //VALUES(income, outcome, date)
                PreparedStatement prep = getConnection().prepareStatement("INSERT INTO finance VALUES(0,?,CURRENT_DATE)");
                prep.setDouble(1, outcome);
                if(prep.executeUpdate() <= 0)return false;

            }catch (SQLException e){
                System.err.println("Issue with adding income to database.");
                return false;
            }
            finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        return true;
    }
}
