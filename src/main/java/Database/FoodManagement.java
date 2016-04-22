package Database;

import org.apache.commons.dbutils.DbUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Created by Evdal on 07.03.2016.
 */
public class FoodManagement extends Management{
    private final String deleteRecipe = "DELETE FROM recipe_grocery WHERE recipe_id = ?;";

    public FoodManagement(){
        super();
    }

    /*
    3 = delivered
    2 = in the making
    1 = ready for delivery
    0 = active
    -1 = inactive
    */

    Connection conn = null;
    PreparedStatement prep = null;
    ResultSet res = null;

    public ArrayList<Object[]> getIngredients(){
        ArrayList<Object[]> out = new ArrayList<>();
        if(setUp()){
            try{
                conn = getConnection();
                prep = conn.prepareStatement("SELECT grocery_id, `name`, quantity, unit FROM grocery;");
                res = prep.executeQuery();
                while (res.next()){
                    Object[] obj = new Object[3];
                    obj[0] = res.getString("name");
                    obj[1] = res.getInt("quantity");
                    obj[2] = res.getString("unit");
                    out.add(obj);
                }
            }
            catch (SQLException e){
                System.err.println("Issue with getting ingredients.");
                return null;
            }
            finally {
                finallyStatement(res, prep);
            }
        }
        return out;
    }

    public Object[] getSingleIngredient(String ingredient){
        Object[] out = new Object[4];
        if(setUp()){
            try {
                conn = getConnection();
                prep = conn.prepareStatement("SELECT name, quantity, unit, price FROM grocery WHERE name = ?;");
                prep.setString(1, ingredient);
                res = prep.executeQuery();
                if(res.next()){
                    out[0] = res.getString("name");
                    out[1] = res.getInt("price");
                    out[2] = res.getInt("quantity");
                    out[3] = res.getString("unit");
                }
            }
            catch (SQLException e){
                System.err.println("Issue with getting ingredient info.");
                return null;
            }
            finally {
                finallyStatement(res, prep);
            }
        }
        return out;
    }

    public boolean updateIngredientName(String name, String newData) {
        int rowChanged = 0;
        if (setUp()) {
            try {
                conn = getConnection();
                conn.setAutoCommit(false);
                prep = conn.prepareStatement("UPDATE grocery SET name = ? WHERE name = ?;");
                prep.setString(1, newData);
                prep.setString(2, name);
                rowChanged = prep.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");
                rollbackStatement();
                return false;

            } finally {
                finallyStatement(res, prep);
            }
        }
        return rowChanged > 0;
    }

    public boolean updateIngredientPrice(String name, int newData) {
        int rowChanged = 0;
        if (setUp()) {
            try {
                conn = getConnection();
                conn.setAutoCommit(false);
                prep = conn.prepareStatement("UPDATE grocery SET price = ? WHERE name = ?;");
                prep.setInt(1, newData);
                prep.setString(2, name);
                rowChanged = prep.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");
                rollbackStatement();
                return false;
            } finally {
                finallyStatement(res, prep);
            }
        }
        return rowChanged > 0;
    }

    public boolean updateIngredientQuantity(String name, int newData) {
        int rowChanged = 0;
        if (setUp()) {
            try {
                conn = getConnection();
                conn.setAutoCommit(false);
                prep = conn.prepareStatement("UPDATE grocery SET quantity = ? WHERE name = ?;");
                prep.setInt(1, newData);
                prep.setString(2, name);
                rowChanged = prep.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");
                rollbackStatement();
                return false;

            } finally {
                finallyStatement(res, prep);
            }
        }
        return rowChanged > 0;
    }

    public boolean updateIngredientUnit(String name, String newData) {
        int rowChanged = 0;
        if (setUp()) {
            try {
                conn = getConnection();
                conn.setAutoCommit(false);
                prep = conn.prepareStatement("UPDATE grocery SET unit = ? WHERE name = ?;");
                prep.setString(1, newData);
                prep.setString(2, name);
                rowChanged = prep.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");
                rollbackStatement();
                return false;

            } finally {
                finallyStatement(res, prep);
            }
        }
        return rowChanged > 0;
    }


    public ArrayList<Object[]> getRecipes(){
        ArrayList<Object[]> out = new ArrayList<>();
        if(setUp()){
            try{
                conn = getConnection();
                prep = conn.prepareStatement("SELECT recipe_id, `name` FROM recipe;");
                res = prep.executeQuery();
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
                finallyStatement(res, prep);
            }
        }
        return out;
    }

    private ArrayList<Integer> getGroceryID (ArrayList<String> ingNames){
        ArrayList<Integer> out = new ArrayList<>();
        if(setUp()){
            try {
                conn = getConnection();
                for (String name : ingNames){
                    prep = conn.prepareStatement("SELECT grocery_id FROM grocery WHERE name = ?;");
                    prep.setString(1, name);
                    res = prep.executeQuery();
                    if(res.next()){
                        out.add(res.getInt("grocery_id"));
                    }
                    prep.close();
                    res.close();
                }
            }catch (SQLException sqle) {
                System.err.println("Issue getting grocery id");
                return null;
            }
        }
        return out;
    }

    private String getRecipeID(String name){
        String recipeid = null;
        if(setUp()) {
            try {
                conn = getConnection();
                prep = conn.prepareStatement("SELECT recipe_id FROM recipe WHERE name = ?;");
                prep.setString(1, name);
                res = prep.executeQuery();
                if (res.next()) {
                    recipeid = Integer.toString(res.getInt("recipe_id"));
                }
                res.close();
                prep.close();
            } catch (SQLException sqle) {
                System.err.println("Issue with getting recipe id");
                return null;
            }
        }
        return recipeid;
    }

    public int getRecipeIDPub(String name){
        int out = -1;
        if(setUp()) {
            try {
                conn = getConnection();
                prep = conn.prepareStatement("SELECT recipe_id FROM recipe WHERE name = ?;");
                prep.setString(1, name);
                res = prep.executeQuery();
                if (res.next()) {
                    out = res.getInt("recipe_id");
                }
            } catch (SQLException e) {
                System.err.println("Issue with getting recipe id");
                return -1;
            } finally {
                finallyStatement(res, prep);
            }
        }
        return out;
    }

    public String getRecipeName(int id){
        String out = null;
        if(setUp()){
            try{
                conn = getConnection();
                prep = conn.prepareStatement("SELECT name FROM recipe WHERE recipe_id = ?;");
                prep.setInt(1, id);
                res = prep.executeQuery();
                while (res.next()){
                    out = res.getString("name");
                }
            } catch (Exception e){
                System.err.println("Issue with getting name of recipe.");
                return null;
            }
            finally {
                finallyStatement(res, prep);
            }
        }
        return out;
    }

    public boolean addRecipe(String name, ArrayList<Object[]> ingInfo, int price){
        int numb = 0;

        try {
            if(setUp()){
                ArrayList<String> names = new ArrayList<>();
                for(Object[] ing : ingInfo ){
                    names.add((String)ing[0]);
                }
                ArrayList<Integer> IDs = getGroceryID(names);

                try {
                    conn.setAutoCommit(false);
                    prep = conn.prepareStatement("INSERT INTO recipe VALUES(DEFAULT, ?,?);");
                    prep.setString(1, name);
                    prep.setInt(2, price);
                    if(prep.executeUpdate() == 0)return false;
                }catch (SQLException sqle){
                    System.err.println("Issue adding values into recipe");
                    sqle.printStackTrace();
                    rollbackStatement();
                    return false;
                }finally {
                    conn.commit();
                    conn.setAutoCommit(true);
                }

                ///////

                try {
                    conn.setAutoCommit(false);
                    int recipeID = Integer.parseInt(getRecipeID(name));
                    for(int i= 0; i < IDs.size();i++){
                        prep = conn.prepareStatement("INSERT INTO recipe_grocery VALUES(?, ?, ?);");
                        prep.setInt(1, recipeID);
                        prep.setString(2, IDs.get(i).toString());
                        prep.setObject(3, ingInfo.get(i)[1]);
                        numb = prep.executeUpdate();
                    }
                }catch (SQLException sqle){
                    System.err.println("Issue adding values into recipe_grocery");
                    rollbackStatement();
                    sqle.printStackTrace();
                    return false;
                }finally {
                    finallyStatement(res, prep);
                }
                return true;
            }
            else return false;
        }catch (Exception e){
            e.printStackTrace();
        }
        return numb > 0;
    }

    private boolean addGroceryRecipe(String name, ArrayList<Object[]> ingInfo, int price) {
        int numb = 0;
        try {
            if(setUp()){
                ArrayList<String> names = new ArrayList<>();
                for(Object[] ing : ingInfo ){
                    names.add((String)ing[0]);
                }
                ArrayList<Integer> IDs = getGroceryID(names);
                int recipeID = Integer.parseInt(getRecipeID(name));
                conn = getConnection();

                try {
                    for(int i= 0; i < IDs.size();i++){
                        conn.setAutoCommit(false);
                        prep = conn.prepareStatement("INSERT INTO recipe_grocery VALUES(?, ? ,?);");
                        prep.setInt(1, recipeID);
                        prep.setString(2, IDs.get(i).toString());
                        prep.setObject(3, ingInfo.get(i)[1]);
                        numb = prep.executeUpdate();
                    }
                }catch (SQLException sqle){
                    sqle.printStackTrace();
                    rollbackStatement();
                    return false;
                }finally {
                    conn.commit();
                    conn.setAutoCommit(true);
                }

                ///////

                try {
                    conn.setAutoCommit(false);
                    prep = conn.prepareStatement("UPDATE recipe SET price = ? WHERE name = ?;");
                    prep.setInt(1, price);
                    prep.setString(2, name);
                    prep.executeUpdate();
                }catch (SQLException sqle){
                    sqle.printStackTrace();
                    rollbackStatement();
                    return false;
                }finally {
                    finallyStatement(res, prep);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return numb > 0;
    }

    public boolean updateRecipe(String name, ArrayList<Object[]> ingInfo, int price, int id){
        if(setUp()) {
            try {
                conn = getConnection();
                prep = conn.prepareStatement(deleteRecipe);
                prep.setInt(1, id);
                prep.executeUpdate();
                addGroceryRecipe(name, ingInfo, price);
            } catch (Exception e) {
                System.err.println("Issue with editing recipe.");
                rollbackStatement();
                return false;
            } finally {
                finallyStatement(res, prep);
            }
        }
        return true;
    }


    public boolean addIngredient(String name, int price, String unit, int quantity){
        int numb = 0;
        if(setUp()) {
            try {
                conn = getConnection();
                prep = conn.prepareStatement("INSERT INTO grocery VALUES(DEFAULT,?,?,?,?);");
                prep.setString(1, name);
                prep.setInt(2, price);
                prep.setString(3, unit);
                prep.setInt(4, quantity);
                numb = prep.executeUpdate();
            } catch (Exception e) {
                System.err.println("Issue with adding ingredient.");
                rollbackStatement();
                return false;
            }finally {
                finallyStatement(res, prep);
            }
        }
        return numb > 0;
    }

    public ArrayList<Object[]> getRecipeIngredients(int id){
        ArrayList<Object[]> out = new ArrayList<>();
        if(setUp()){
            try{
                conn = getConnection();
                prep = conn.prepareStatement("SELECT recipe_grocery.amount, grocery.name, grocery.unit, recipe_grocery.grocery_id " +
                        "FROM grocery, recipe_grocery WHERE recipe_id = ? AND recipe_grocery.grocery_id = grocery.grocery_id;");
                prep.setInt(1, id);
                res = prep.executeQuery();
                while(res.next()){
                    Object[] obj = new Object[4];
                    obj[0] = res.getString("name");
                    obj[1] = res.getInt("amount");
                    obj[2] = res.getString("unit");
                    obj[3] = res.getInt("grocery_id");
                    out.add(obj);
                }
            }
            catch (Exception e){
                System.err.println("Issue with getting ingredients from recipes.");
            }
            finally {
                finallyStatement(res, prep);
            }
        }
        return out;
    }

    public ArrayList<Object[]> getIngredientsInStorage(ArrayList<String> names){
        ArrayList<Object[]> out = new ArrayList<>();
        if(setUp()){
            try {
                conn = getConnection();

                for (String name : names) {
                    prep = conn.prepareStatement("SELECT grocery.name, quantity FROM grocery WHERE grocery.name = ?;");
                    prep.setString(1, name);
                    res = prep.executeQuery();
                    if(res.next()){
                        Object[] obj = new Object[2];
                        obj[0] = res.getString("name");
                        obj[1] = res.getInt("quantity");
                        out.add(obj);
                    }
                    else return null;
                }
            }
            catch(SQLException e){
                System.err.println("Issue with getting ingredients from storage.");
                e.printStackTrace();
                return null;
            }
            finally {
                finallyStatement(res, prep);
            }
        }
        return out;
    }

    public boolean addIngredientToStorage(String name, int addedValue){ //ingredients[0] = name og ingredients[1] = added values
        int numb = 0;
        if(setUp()){
            try {
                conn = getConnection();
                prep = conn.prepareStatement("SELECT quantity FROM grocery WHERE name = ?;");
                prep.setString(1, name);
                res = prep.executeQuery();

                if(res.next()) {
                    int newQuant = res.getInt("quantity") + addedValue;
                    conn.setAutoCommit(false);
                    prep = conn.prepareStatement("UPDATE grocery SET quantity = ? WHERE name = ?;");
                    prep.setInt(1, newQuant);
                    prep.setString(2, name);
                    numb = prep.executeUpdate();
                }
            }
            catch (Exception e){
                System.err.println("Issue with adding ingredient to storage");
                rollbackStatement();
                return false;
            }
            finally {
                finallyStatement(res, prep);
            }
        }
        return numb > 0;
    }

    public boolean removeIngredientFromStorage(int id, int subtractedValue){ //ingredients[0] = name og ingredients[1] = added values
        int numb = 0;
        if(setUp()){
            try {
                conn = getConnection();
                prep = conn.prepareStatement("SELECT quantity FROM grocery WHERE grocery_id = ?;");
                prep.setInt(1, id);
                res = prep.executeQuery();
                if(res.next()) {
                    int newQuant = res.getInt("quantity") - subtractedValue;
                    conn.setAutoCommit(false);
                    prep = conn.prepareStatement("UPDATE grocery SET quantity = ? WHERE grocery_id = ?;");
                    prep.setInt(1, newQuant);
                    prep.setInt(2, id);
                    numb = prep.executeUpdate();
                }
            }
            catch (Exception e){
                System.err.println("Issue with adding ingredient to storage");
                rollbackStatement();
                return false;
            }
            finally {
                finallyStatement(res, prep);
            }
        }
        return numb > 0;
    }

    public ArrayList<Object[]> getRecipesForChef(){
        ArrayList<Object[]> out = new ArrayList<>();
        if(setUp()){
            try {
                conn = getConnection();
                prep = conn.prepareStatement("SELECT recipe.name, order_recipe.portions, `order`.time, " +
                        "`order`.order_id, `order`.note, `order`.status FROM recipe, `order`, order_recipe WHERE " +
                        "`order`.order_id = order_recipe.order_id AND order_recipe.recipe_id = recipe.recipe_id " +
                        "AND status > 0 AND status < 3 AND `order`.date = CURRENT_DATE ORDER BY `time` DESC;");
                res = prep.executeQuery();
                while (res.next()){
                    Object[] obj = new Object[7];
                    obj[0] = res.getInt("order_id");
                    obj[1] = res.getString("name");
                    obj[2] = res.getString("portions");
                    obj[3] = res.getString("time");
                    obj[4] = res.getString("note");
                    obj[5] = OrderManagement.OrderType.valueOf(res.getInt("status"));
                    out.add(obj);
                }
            }catch (Exception e){
                System.err.println("Issue with getting recipes.");
                return null;
            }
            finally {
                finallyStatement(res, prep);
            }
        }
        return out;
    }

    public ArrayList<Object[]> getIngredientsForShoppinglist(String fDate, String tDate){
        ArrayList<Object[]> out = new ArrayList<>();
        ArrayList<Object[]> IDs = getRecipeIDs(fDate, tDate);
        if(setUp()) {
            try {
                conn = getConnection();
                for (Object[] id : IDs) {
                    ArrayList<Object[]> tmp = new ArrayList<>();
                    prep = conn.prepareStatement("SELECT grocery.name, grocery.unit, recipe_grocery.amount, grocery.price " +
                            "FROM grocery, recipe_grocery WHERE recipe_grocery.recipe_id = ? AND recipe_grocery.grocery_id = grocery.grocery_id;");
                    prep.setObject(1, id[0]);
                    res = prep.executeQuery();
                    while (res.next()) {
                        Object[] obj = new Object[4];
                        obj[0] = res.getString("name");
                        obj[1] = res.getInt("amount") * (Integer)id[1];
                        obj[2] = res.getString("unit");
                        obj[3] = res.getInt("price");
                        tmp.add(obj);
                    }
                    out.addAll(tmp);
                }

            }catch(Exception e){
                System.err.println("Issue with getting ingredients from order_id.");
                return null;
            }finally{
                finallyStatement(res, prep);
            }

        }
        return out;
    }

    public ArrayList<Object[]> getRecipeIDs(String fDate, String tDate){
        ArrayList<Object[]> out = new ArrayList<>();
        if(setUp()) {
            try {
                conn = getConnection();
                prep = conn.prepareStatement("SELECT order_recipe.recipe_id, order_recipe.portions " +
                        "FROM order_recipe, `order` WHERE order_recipe.order_id = `order`.order_id AND `order`." +
                        "`date` >= ? AND `order`.`date` <= ?;");
                prep.setString(1, fDate);
                prep.setString(2, tDate);
                res = prep.executeQuery();
                while (res.next()) {
                    Object[] obj = new Object[2];
                    obj[0] = res.getInt("recipe_id");
                    obj[1] = res.getInt("portions");
                    out.add(obj);
                }
            } catch (Exception e) {
                System.err.println("Issue with getting ingredients from order_id.");
                return null;
            } finally {
                finallyStatement(res, prep);
            }
        }
        return out;
    }

    public int getRecipePrice(String name){
        int out = -1;
        if(setUp()){
            try{
                conn.getAutoCommit();
                prep = conn.prepareStatement("SELECT price FROM recipe WHERE name = ?;");
                prep.setString(1, name);
                res = prep.executeQuery();
                if(res.next()) {
                    out = res.getInt("price");
                }
            } catch (Exception e){
                System.err.println("Issue with getting price of recipe.");
                return -1;
            }
            finally {
                finallyStatement(res, prep);
            }
        }
        return out;
    }
}