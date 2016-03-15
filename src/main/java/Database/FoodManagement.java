package Database;

import org.apache.commons.dbutils.DbUtils;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

/**
 * Created by Evdal on 07.03.2016.
 */
public class FoodManagement extends Management{
    public FoodManagement(){
        super();
    }
    public ArrayList<Object[]> getIngredients(){
        ResultSet res = null;
        ArrayList<Object[]> out = new ArrayList<Object[]>();
        if(setUp()){
            try{

                res = getScentence().executeQuery("SELECT grocery_id, name, quantity, unit FROM grocery;");
                while (res.next()){
                    Object[] obj = new Object[3];
                    obj[0] = res.getString("name");
                    obj[1] = res.getInt("quantity");
                    obj[2] = res.getString("unit");
                    out.add(obj);
                }
            }
            /*
            2 = delivered
            1 = ready for delivery
            0 = active
            -1 = inactive

             */
            catch (Exception e){
                System.err.println("Issue with getting ingredients.");
                return null;
            }
            finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }

        }
        else return null;
        return out;
    }
    private ArrayList<Integer> getGroceryID (ArrayList<String> ingNames) throws Exception {
        ArrayList<Integer> out = new ArrayList<Integer>();
        ResultSet res = null;
        for (String name : ingNames) {
            res = getScentence().executeQuery("SELECT grocery_id FROM grocery WHERE name = '" + name + "';");
            if(res.next())out.add(res.getInt("grocery_id"));

        }
        return out;
    }
    private String getRecipeID(String name)throws Exception{
        ResultSet res = getScentence().executeQuery("SELECT recipe_id FROM recipe WHERE name = '" + name + "';");
        return Integer.toString(res.getInt("recipe_id"));
    }

    public boolean addRecipe(String name, ArrayList<String> ingNames){
        int numb = 0;
        if(setUp()){
            try{
                getScentence().executeUpdate("START TRANSACTION;");
                ArrayList<Integer> IDs = getGroceryID(ingNames);
                numb = getScentence().executeUpdate("INSERT INTO recipe VALUES(DEFAULT, '" + name + "');");
                if(numb == 0)return false;
                String recipeID = getRecipeID(name);
                for(Integer id : IDs){
                    numb = getScentence().executeUpdate("INSERT INTO recipe_grocery VALUES('" + recipeID + "', '" + id.toString() + "');");
                    if(numb == 0)return false;
                }
                getScentence().executeUpdate("COMMIT;");
            }
            catch (Exception e){
                System.err.println("Issue with adding recipe to database.");
                return false;
            }
            finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());

            }
            return true;
        }
        else return false;

    }
    public boolean addIngredient(String name, int price, String unit, int quantity){
        int res = 0;
        if(setUp()) {
            try {
                PreparedStatement prep = getConnection().prepareStatement("INSERT INTO grocery VALUES(DEFAULT,?,?,?,?);");
                prep.setString(1, name);
                prep.setInt(2, price);
                prep.setString(3, unit);
                prep.setInt(4, quantity);
                res = prep.executeUpdate();
            } catch (Exception e) {
                System.err.println("Issue with adding ingredient.");
                return false;
            }
        }
        return res > 0;
    }



}