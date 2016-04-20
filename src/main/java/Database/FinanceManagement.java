package Database;

import org.apache.commons.dbutils.DbUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Evdal on 09.04.2016.
 */
public class FinanceManagement extends Management{
    private final String sqlGetRecipes = "SELECT portions, price, recipe.recipe_id FROM order_recipe, recipe WHERE order_recipe.order_id = ? AND recipe.recipe_id = order_recipe.recipe_id;";
    private final String sqlAddIncome = "INSERT INTO finance VALUES(?,0,CURRENT_DATE);";
    private final String sqlAddOutcome = "INSERT INTO finance VALUES(0,?,CURRENT_DATE)";

    Connection conn = null;
    ResultSet res = null;
    PreparedStatement prep = null;


    public FinanceManagement(){
        super();
    }

    private boolean addDoubleToDatabase(String sql, Double input, String errMsg){
        if(setUp()){
            try{
                conn = getConnection();
                conn.setAutoCommit(false);
                prep = conn.prepareStatement(sql);
                prep.setDouble(1, input);
                if(prep.executeUpdate() <= 0)return false;
            }catch (SQLException e){
                System.err.println(errMsg);
                rollbackStatement();
                return false;
            }
            finally {
                finallyStatement(res, prep);
            }
        }
        return true;
    }

    /**
     *  Hadde bare lyst så jeg laget en add metode
     */

    public boolean addIncomeToDatabase(double income){
        return addDoubleToDatabase(sqlAddIncome, income, "Issue with adding income to database");
    }

    public boolean addOutcomeToDatabase(double outcome){
        return addDoubleToDatabase(sqlAddOutcome, outcome, "Issue with adding outcome to database");
    }

    public ArrayList<Object[]> getOrderRecipeInfo(int id){
        ArrayList<Object[]> out = new ArrayList<>();
        if(setUp()){
            try{
                conn = getConnection();
                prep = conn.prepareStatement(sqlGetRecipes);
                prep.setInt(1, id);
                System.out.println(prep.toString());
                res = prep.executeQuery();
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
                finallyStatement(res, prep);
            }
        }
        return out;
    }
}
