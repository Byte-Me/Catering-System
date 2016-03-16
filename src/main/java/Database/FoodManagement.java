package Database;

import org.apache.commons.dbutils.DbUtils;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
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
        ResultSet res;
        ArrayList<Object[]> out = new ArrayList<Object[]>();
        if(setUp()){
            try{
                res = getScentence().executeQuery("SELECT grocery_id, `name`, quantity, unit FROM grocery;");

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
        return out;
    }

    public ArrayList<Object[]> getRecipes(){
        ResultSet res;
        ArrayList<Object[]> out = new ArrayList<Object[]>();
        if(setUp()){
            try{
                res = getScentence().executeQuery("SELECT recipe_id, `name` FROM recipe;");

                while (res.next()){
                    Object[] obj = new Object[2];
                    obj[0] = res.getInt("recipe_id");
                    obj[1] = res.getString("name");
                    out.add(obj);
                }

            } catch (Exception e){
                System.err.println("Issue with getting ingredients.");
                return null;
            }
            finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }

        }
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
        if(res.next()) {
            return Integer.toString(res.getInt("recipe_id"));
        }
        else return null;
    }

    public boolean addRecipe(String name, ArrayList<Object[]> ingInfo){
        int numb;
        if(setUp()){
            try{
                ArrayList<String> names = new ArrayList<String>();
                for(Object[] ing : ingInfo ){
                    names.add((String)ing[0]);
                }

                getScentence().executeUpdate("START TRANSACTION;");
                ArrayList<Integer> IDs = getGroceryID(names);
                numb = getScentence().executeUpdate("INSERT INTO recipe VALUES(DEFAULT, '" + name + "');");
                if(numb == 0)return false;
                String recipeID = getRecipeID(name);


                for(int i= 0; i < IDs.size();i++){ //
                    numb = getScentence().executeUpdate("INSERT INTO recipe_grocery VALUES('" + recipeID + "', '" + IDs.get(i).toString() + "', '"
                            + ingInfo.get(i)[1] + "');");
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

    public ArrayList<Object[]> getRecipeIngredients(){
        ArrayList<Object[]> out = new ArrayList<Object[]>();
        if(setUp()){
            ResultSet res;
            try{
                res = getScentence().executeQuery("SELECT grocery.price, recipe_grocery.amount, grocery.name, grocery.unit FROM grocery, recipe_grocery, `order` WHERE `order`.date = " +
                        "CURRENT_DATE AND recipe_grocery.recipe_id = `order`.recipe_id AND grocery.grocery_id = recipe_grocery.grocery_id;");
                while(res.next()){
                    Object[] obj = new Object[4];
                    obj[0] = res.getString("name");
                    obj[1] = res.getInt("amount");
                    obj[2] = res.getString("unit");
                    obj[3] = res.getInt("price");
                    out.add(obj);
                }
            }

            catch (Exception e){
                System.err.println("Issue with getting ingredients from recipes.");
            }
            finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        return out;
    }

    public ArrayList<Object[]> getIngredientsInStorage(ArrayList<String> names){
        ArrayList<Object[]> out = new ArrayList<Object[]>();
        if(setUp()){
            ResultSet res;
            try {
                for (String name : names) {
                    res = getScentence().executeQuery("SELECT grocery.name, quantity FROM grocery WHERE grocery.name = '" + name + "';");
                    if(res.next()){
                        Object[] obj = new Object[2];
                        obj[0] = res.getString("name");
                        obj[1] = res.getInt("quantity");
                        out.add(obj);
                    }
                    else return null;
                }
            }
            catch(Exception e){
                System.err.println("Issue with getting ingredients from storage.");
                return null;
            }
            finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        return out; // returnerer i samme rekkefølge som
    }

    public boolean addIngredientToStorage(String name, int addedValue){         //ingredients[0] = name og ingredients[1] = added values
        int numb = 0;
        if(setUp()){
            try {
                getScentence().executeQuery("START TRANSACTION;");
                ResultSet res = getScentence().executeQuery("SELECT quantity FROM grocery WHERE name = '" + name + "';");
                if(res.next()) {
                    int newQuant = res.getInt("quantity") + addedValue;
                    numb = getScentence().executeUpdate("UPDATE grocery SET quantity = '" + newQuant + "' WHERE name = '" + name + "';");


                }
                getScentence().executeQuery("COMMIT;");

            }
            catch (Exception e){
                System.err.println("Issue with adding ingredient to storage");
                return false;
            }
            finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        return numb > 0;
    }

    public boolean removeIngredientFromStorage(String name, int subtractedValue){         //ingredients[0] = name og ingredients[1] = added values
        int numb = 0;
        if(setUp()){
            try {
                getScentence().executeQuery("START TRANSACTION;");
                ResultSet res = getScentence().executeQuery("SELECT quantity FROM grocery WHERE name = '" + name + "';");
                if(res.next()) {
                    int newQuant = res.getInt("quantity") - subtractedValue;
                    numb = getScentence().executeUpdate("UPDATE grocery SET quantity = '" + newQuant + "' WHERE name = '" + name + "';");


                }
                getScentence().executeQuery("COMMIT;");

            }
            catch (Exception e){
                System.err.println("Issue with adding ingredient to storage");
                try {
                    getScentence().executeQuery("ROLLBACK");
                } catch (SQLException e1) {

                }
                return false;
            }
            finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        return numb > 0;
    }

    public ArrayList<Object[]> getRecipesForChef(){
        ArrayList<Object[]> out = new ArrayList<Object[]>();
        if(setUp()){
            try {
                ResultSet res = getScentence().executeQuery("SELECT recipe.name, order_recipe.portions, `order`.date, `order`.order_id FROM recipe, `order`, order_recipe " +
                        "WHERE `order`.order_id = order_recipe.order_id AND order_recipe.recipe_id = recipe.recipe_id ORDER BY `date` DESC, order_id;");
                while (res.next()){
                    Object[] obj = new Object[4];
                    obj[0] = res.getString("name");
                    obj[1] = res.getInt("portions");
                    obj[2] = res.getString("date");
                    obj[3] = res.getInt("order_id");
                    out.add(obj);

                }
            }catch (Exception e){
                System.err.println("Issue with getting recipes.");
            }
            finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        return out;
    }

    public ArrayList<Object[]> getIngredientsForOrder(int order_id, String recipeName){
        ArrayList<Object[]> out = new ArrayList<Object[]>();
        if(setUp()) {
            try {
                System.out.println("SELECT grocery.name, grocery.unit, recipe_grocery.amount, order_recipe.portions " +
                        "FROM grocery, order_recipe, recipe_grocery WHERE order_recipe.order_id = " + order_id + " AND order_recipe.recipe_id = " +
                        "recipe_grocery.recipe_id AND recipe_grocery.grocery_id = grocery.grocery_id AND recipe.name = '" + recipeName + "';");
                ResultSet res = getScentence().executeQuery("SELECT grocery.name, grocery.unit, recipe_grocery.amount, order_recipe.portions " +
                        "FROM grocery, order_recipe, recipe_grocery WHERE order_recipe.order_id = " + order_id + " AND order_recipe.recipe_id = " +
                        "recipe_grocery.recipe_id AND recipe_grocery.grocery_id = grocery.grocery_id AND recipe.name = '" + recipeName + "';");

                while (res.next()) {
                    Object[] obj = new Object[3];
                    obj[0] = res.getString("name");
                    obj[1] = res.getInt("portions") * res.getInt("amount");
                    obj[2] = res.getString("unit");
                    out.add(obj);
                }
            } catch (Exception e) {
                System.err.println("Issue with getting ingredients from order_id.");
            } finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());

            }
        }
        return out;
    }

    public boolean updateQuantity(String recipeName, String newData) throws Exception {
        int rowChanged = 0;
        if (super.setUp()) {
            try {
                int recipeID = Integer.parseInt(getRecipeID(recipeName));
                rowChanged = getScentence().executeUpdate("UPDATE recipe_grocery SET amount = '" + newData + "' WHERE recipe_id = recipeID;");
            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");
                return false;

            } finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());


            }
        }
        if(rowChanged > 0) return true;
        return false;
    }
}