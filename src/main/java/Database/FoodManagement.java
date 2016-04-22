package Database;

import org.apache.commons.dbutils.DbUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Created by Evdal on 07.03.2016.
 */
public class FoodManagement extends Management{
    private final String deleteRecipe = "DELETE FROM recipe_grocery WHERE recipe_id = ?;";
    private final String updateOrderRecipeStatus = "UPDATE order_recipe SET status = ? WHERE order_id = ? AND recipe_id = ?";

    public FoodManagement(){
        super();
    }

    public enum OrderRecipeStatus {
        WAITING, PROCESSING;

        public int getValue() {
            return super.ordinal();
        }

        public static OrderRecipeStatus valueOf(int custTypeNr) {
            for (OrderRecipeStatus type : OrderRecipeStatus.values()) {
                if (type.ordinal() == custTypeNr) {
                    return type;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            String constName = super.toString();
            return constName.substring(0,1) + constName.substring(1).toLowerCase();
        }

    }

    public ArrayList<Object[]> getIngredients(){
        ResultSet res;
        ArrayList<Object[]> out = new ArrayList<>();
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
            3 = delivered
            2 = in the making
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

    public Object[] getSingleIngredient(String ingredient){
        Object[] out = new Object[4];
        if(setUp()){
            try {

                ResultSet res = getScentence().executeQuery("SELECT name, quantity, unit, price FROM grocery WHERE name = '" + ingredient + "';");
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
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        return out;
    }

    public boolean updateIngredientName(String name, String newData) {
        int rowChanged = 0;
        if (setUp()) {
            try {
                PreparedStatement prep = getConnection().prepareStatement("UPDATE grocery SET name = ? WHERE name = ?;");
                prep.setString(1, newData);
                prep.setString(2, name);

                rowChanged = prep.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");
                return false;

            } finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }

        return rowChanged > 0;
    }

    public boolean updateIngredientPrice(String name, int newData) {
        int rowChanged = 0;
        if (setUp()) {
            try {
                PreparedStatement prep = getConnection().prepareStatement("UPDATE grocery SET price = ? WHERE name = ?;");
                prep.setInt(1, newData);
                prep.setString(2, name);

                rowChanged = prep.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");
                return false;

            } finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }

        return rowChanged > 0;
    }

    public boolean updateIngredientQuantity(String name, int newData) {
        int rowChanged = 0;
        if (setUp()) {
            try {
                PreparedStatement prep = getConnection().prepareStatement("UPDATE grocery SET quantity = ? WHERE name = ?;");
                prep.setInt(1, newData);
                prep.setString(2, name);

                rowChanged = prep.executeUpdate();
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

    public boolean updateIngredientUnit(String name, String newData) {
        int rowChanged = 0;
        if (setUp()) {
            try {
                PreparedStatement prep = getConnection().prepareStatement("UPDATE grocery SET unit = ? WHERE name = ?;");
                prep.setString(1, newData);
                prep.setString(2, name);

                rowChanged = prep.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");
                return false;

            } finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }

        return rowChanged > 0;
    }


    public ArrayList<Object[]> getRecipes(){
        ResultSet res;
        ArrayList<Object[]> out = new ArrayList<>();
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

        ArrayList<Integer> out = new ArrayList<>();
        for (String name : ingNames) {
            ResultSet res = getScentence().executeQuery("SELECT grocery_id FROM grocery WHERE name = '" + name + "';");
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
    public int getRecipeIDPub(String name){
        int out = -1;
        if(setUp()) {
            try {

                ResultSet res = getScentence().executeQuery("SELECT recipe_id FROM recipe WHERE name = '" + name + "';");
                if (res.next()) {
                    out = res.getInt("recipe_id");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        return out;
    }

    public String getRecipeName(int id)throws Exception{
        ResultSet res;
        String out = null;
        if(setUp()){
            try{
                res = getScentence().executeQuery("SELECT name FROM recipe WHERE recipe_id = " + id + ";");
                while (res.next()){
                    out = res.getString("name");
                }

            } catch (Exception e){
                System.err.println("Issue with getting name of recipe.");
                return null;
            }
            finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        return out;
    }

    public boolean addRecipe(String name, ArrayList<Object[]> ingInfo, int price){
        int numb;
        if(setUp()){
            try{
                ArrayList<String> names = new ArrayList<>();
                for(Object[] ing : ingInfo ){
                    names.add((String)ing[0]);
                }

                getScentence().executeUpdate("START TRANSACTION;");
                ArrayList<Integer> IDs = getGroceryID(names);
                PreparedStatement prep = getConnection().prepareStatement("INSERT INTO recipe VALUES(DEFAULT, ?,?);");
                prep.setString(1, name);
                prep.setInt(2, price);
                if(prep.executeUpdate() == 0)return false;
                String recipeID = getRecipeID(name);


                for(int i= 0; i < IDs.size();i++){
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

    private boolean addGroceryRecipe(String name, ArrayList<Object[]> ingInfo, int price) {
        int numb;
        if(setUp()){
            try{
                ArrayList<String> names = new ArrayList<>();
                for(Object[] ing : ingInfo ){
                    names.add((String)ing[0]);
                }
                String recipeID = getRecipeID(name);
                ArrayList<Integer> IDs = getGroceryID(names);

                for(int i= 0; i < IDs.size();i++){
                    numb = getScentence().executeUpdate("INSERT INTO recipe_grocery VALUES('" + recipeID + "', '" + IDs.get(i).toString() + "', '"
                            + ingInfo.get(i)[1] + "');");
                    if(numb == 0)return false;
                }

                PreparedStatement prep = getConnection().prepareStatement("UPDATE recipe SET price = ? WHERE name = '" + name + "';");
                prep.setInt(1, price);
                prep.executeUpdate();

            } catch (Exception e){
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

    public boolean updateRecipe(String name, ArrayList<Object[]> ingInfo, int price, int id){
        if(setUp()) {
            try {
                PreparedStatement prep = getConnection().prepareStatement(deleteRecipe);
                prep.setInt(1, id);
                prep.executeUpdate();
                addGroceryRecipe(name, ingInfo, price);
            } catch (Exception e) {
                System.err.println("Issue with editing recipe.");
                return false;
            }
        }
        return true;
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

    public ArrayList<Object[]> getRecipeIngredients(int id){
        ArrayList<Object[]> out = new ArrayList<>();
        if(setUp()){
            ResultSet res;
            try{
                res = getScentence().executeQuery("SELECT recipe_grocery.amount, grocery.name, grocery.unit, recipe_grocery.grocery_id FROM grocery, recipe_grocery WHERE recipe_id = " + id + " AND recipe_grocery.grocery_id = grocery.grocery_id;");
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
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        return out;
    }

    public ArrayList<Object[]> getIngredientsInStorage(ArrayList<String> names){
        ArrayList<Object[]> out = new ArrayList<>();
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
        return out;
    }

    public boolean addIngredientToStorage(String name, int addedValue){ //ingredients[0] = name og ingredients[1] = added values
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

    public boolean removeIngredientFromStorage(int id, int subtractedValue){ //ingredients[0] = name og ingredients[1] = added values
        int numb = 0;
        if(setUp()){
            try {
                getScentence().executeQuery("START TRANSACTION;");
                ResultSet res = getScentence().executeQuery("SELECT quantity FROM grocery WHERE grocery_id = '" + id + "';");
                if(res.next()) {
                    int newQuant = res.getInt("quantity") - subtractedValue;
                    numb = getScentence().executeUpdate("UPDATE grocery SET quantity = '" + newQuant +
                            "' WHERE grocery_id = '" + id + "';");


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
        ArrayList<Object[]> out = new ArrayList<>();
        if(setUp()){
            try {
                ResultSet res = getScentence().executeQuery("SELECT recipe.name, order_recipe.portions, `order`.time, " +
                        "`order`.order_id, `order`.note, `order_recipe`.status FROM recipe, `order`, order_recipe WHERE " +
                        "`order`.order_id = order_recipe.order_id AND order_recipe.recipe_id = recipe.recipe_id " +
                        "AND `order`.status = 1 AND `order`.date = CURRENT_DATE ORDER BY `time` DESC;");
                while (res.next()){
                    Object[] obj = new Object[7];
                    obj[0] = res.getInt("order_id");
                    obj[1] = res.getString("name");
                    obj[2] = res.getString("portions");
                    obj[3] = res.getString("time");
                    obj[4] = res.getString("note");
                    obj[5] = OrderRecipeStatus.valueOf(res.getInt("status"));
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

    public ArrayList<Object[]> getIngredientsForShoppinglist(String fDate, String tDate){
        ArrayList<Object[]> out = new ArrayList<>();
        ArrayList<Object[]> IDs = getRecipeIDs(fDate, tDate);
        if(setUp()) {
            try {

                for (Object[] id : IDs) {
                    ArrayList<Object[]> tmp = new ArrayList<>();
                    ResultSet res = getScentence().executeQuery("SELECT grocery.name, grocery.unit, recipe_grocery.amount, grocery.price " +
                            "FROM grocery, recipe_grocery WHERE recipe_grocery.recipe_id = " + id[0] + " AND recipe_grocery.grocery_id = grocery.grocery_id;");
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
                }finally{
                    DbUtils.closeQuietly(getScentence());
                    DbUtils.closeQuietly(getConnection());

                }

        }
        return out;
    }

    public ArrayList<Object[]> getRecipeIDs(String fDate, String tDate){
        ArrayList<Object[]> out = new ArrayList<>();
        if(setUp()) {
            try {
                ResultSet res = getScentence().executeQuery("SELECT order_recipe.recipe_id, order_recipe.portions " +
                        "FROM order_recipe, `order` WHERE order_recipe.order_id = `order`.order_id AND `order`." +
                        "`date` >= "+fDate+" AND `order`.`date` <= "+tDate+";");


                while (res.next()) {
                    Object[] obj = new Object[2];
                    obj[0] = res.getInt("recipe_id");
                    obj[1] = res.getInt("portions");
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
    public int updateOrderRecipeStatus(int orderId, int recipeId, int newStatus){
        int out = 0;
        if(setUp()){
            try{
                PreparedStatement prep = getConnection().prepareStatement(updateOrderRecipeStatus);
                prep.setInt(1, newStatus);
                prep.setInt(2, orderId);
                prep.setInt(3, recipeId);
                out = prep.executeUpdate();

            } catch (Exception e){
                System.err.println("Issue with updating recipe_grocery status");
                return 0;
            }
            finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        return out;
    }

    public int getRecipePrice(String name)throws Exception{
        ResultSet res;
        int out = -1;
        if(setUp()){
            try{
                res = getScentence().executeQuery("SELECT price FROM recipe WHERE name = '" + name + "';");
                if(res.next()) {
                    out = res.getInt("price");
                }

            } catch (Exception e){
                System.err.println("Issue with getting price of recipe.");
                return -1;
            }
            finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        return out;
    }
    //FIXME
    /*
    public ArrayList<Object[]> prepareSearch(String searchTerm){
        ResultSet res;
        ArrayList<Object[]> out = new ArrayList<>();
        if(setUp()) {
            try {
                PreparedStatement prep = getConnection().prepareStatement("SELECT order_recipe.order_id, recipe.name" +
                        " FROM order_recipe, recipe WHERE recipe.recipe_id = order_recipe.recipe_id AND (" +
                        "recipe.recipe_id LIKE ? OR recipe.name LIKE ?");
                searchTerm = "%" + searchTerm + "%";
                prep.setString(1, searchTerm);
                prep.setString(2, searchTerm);
                res = prep.executeQuery();

                while (res.next()) {
                    Object[] obj = new Object[2];
                    obj[0] = res.getInt("order_id");
                    obj[1] = res.getString("name");
                    out.add(obj);
                }
            } catch (Exception e) {
                System.err.println("Issue with search.");
                e.printStackTrace();
                return null;
            } finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }

            return out;
        }
        else return null;
    }
    */
}