package Database;

import org.apache.commons.dbutils.DbUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Evdal on 09.04.2016.
 */
public class FinanceManagement extends Management{
    private final String getRecipes = "SELECT portions, price, recipe.recipe_id FROM order_recipe, recipe WHERE order_recipe.order_id = ? AND recipe.recipe_id = order_recipe.recipe_id;";

    public FinanceManagement(){
        super();
    }
    public boolean addIncomeToDatabase(double income){
        if(setUp()){
            try{
                PreparedStatement prep = getConnection().prepareStatement("INSERT INTO finance VALUES(?,0,CURRENT_DATE);");
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
    public ArrayList<Object[]> getOrderRecipeInfo(int id){
        ArrayList<Object[]> out = new ArrayList<>();
        if(setUp()){
            try{                                                                                //VALUES(income, outcome, date)
                PreparedStatement prep = getConnection().prepareStatement(getRecipes);
                prep.setInt(1, id);
                ResultSet res = prep.executeQuery();
                while (res.next()){
                    Object[] obj = new Object[2];
                    obj[0] = res.getInt("portions");
                    obj[1] = res.getDouble("price");
                    out.add(obj);
                }
            }catch (SQLException e){
                System.err.println("Issue with getting recipe price.");
            }
            finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        return out;
    }
}
