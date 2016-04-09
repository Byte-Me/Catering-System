package Database;

import Database.Management;
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
public class OrderManagement extends Management {
    public OrderManagement(){
        super();
    }/*
    public ArrayList<Object[]> getOrders(){
        if(setUp()){
            try{
                getScentence().executeQuery("SELECT ")
            }
        }
    }*/
    public enum OrdStatus{
        INACTIVE(0),
        ACTIVE(1),
        READY_FOR_DELIVERY(2),
        DELIVERED(3);

        private int value;
        private OrdStatus(int value){
            this.value = value;
        }
        public int getValue(){
            return value;
        }
    }
    public enum OrderType {
        INACTIVE, ACTIVE, PROCESSING, READY, DELIVERED;

        public int getValue() {
            return super.ordinal();
        }

        public static OrderType valueOf(int userTypeNr) {
            for (OrderType type : OrderType.values()) {
                if (type.ordinal() == userTypeNr) {
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
    public boolean updateStatus(int orderID, int newStatus){
        int numb = 0;
        if(newStatus <= OrdStatus.INACTIVE.getValue() || newStatus >= OrdStatus.DELIVERED.getValue()) {
            return false;
        }

        if(setUp()){
            try{
                numb = getScentence().executeUpdate("UPDATE `order` SET status = '" + newStatus + "' WHERE order_id = '" + orderID + "';");
            }
            catch (Exception e){
                System.err.println("Issue with updating order status.");
                return false;
            }
            finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        return numb > 0;
    }
    public ArrayList<Object[]> getOrders(){
        ArrayList<Object[]> out = new ArrayList<Object[]>();
        if(setUp()){

            //Henter info fra ordre der ordren ikke er merket som inaktiv.
            try {

                ResultSet res = getScentence().executeQuery("SELECT `order`.order_id, customer.name ,customer.phone, customer.adress, `order`.date, `order`.status " +
                        "FROM `order`, customer WHERE `order`.customer_id = customer.customer_id AND `order`.status >= "+OrdStatus.ACTIVE.getValue()+
                        " ORDER BY `date` DESC, status DESC;");
                while (res.next()){
                    out.add(createList(res));

                }
            }
            catch (Exception e){
                System.err.println("Issue with fetching orders.");
            }
            finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        return out;
    }
    private Object[] createList(ResultSet res) throws Exception{
        Object[] obj = new Object[6];
        obj[0] = res.getInt("order_id");
        obj[1] = res.getString("name");
        obj[2] = res.getString("phone");
        obj[3] = res.getString("adress");
        obj[4] = res.getString("date");
        obj[5] = OrderType.valueOf(res.getInt("status"));
        return obj;
    }
    public ArrayList<Object[]> orderSearch(String searchTerm){
        ResultSet res;
        ArrayList<Object[]> out = new ArrayList<Object[]>();
        if(setUp()) {
            try {

                res = getScentence().executeQuery("SELECT `order`.order_id, customer.name ,customer.phone, customer.adress, `order`.date, `order`.status FROM `order`, customer WHERE (order_id LIKE '%" + searchTerm + "%' OR `order`.status LIKE '%" +
                        searchTerm + "%' OR `date` LIKE '%" + searchTerm +
                        "%' OR `name` LIKE '%" + searchTerm + "%' OR phone LIKE '%" + searchTerm + "%' OR adress LIKE '%" + searchTerm +
                        "%') AND `order`.status >= "+OrdStatus.ACTIVE.getValue()+
                        " AND `order`.customer_id = customer.customer_id ORDER BY `order`.status DESC, `date` DESC;");

                while (res.next()){
                    out.add(createList(res));
                }

            } catch (Exception e) {
                System.err.println("Issue with search.");
            } finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }

        }
        return out;

    }
    public boolean createOrder(String customerMail, String date, ArrayList<Object[]> recipes, String note, String time){
        int id = -1;
        try {
            if(setUp()) {
                getScentence().executeQuery("START TRANSACTION");
                ResultSet res = getScentence().executeQuery("SELECT customer_id FROM customer WHERE email = '" + customerMail + "';");
                if (res.next()) {
                    id = res.getInt("customer_id");
                }
                //Metode ment for GUI, her slipper man å sende inn en subscription id, og metoden finner Customer ID selv.
                //Deretter kalles create order for subscription med de nye verdiene.
            }

            if(!createOrderSub(id, date, recipes, note, time, -1)) return false; //-1 er verdien som blir satt dersom det ikke finnes en
                                                                            //subscription.
        } catch (Exception e) {
            System.err.println("Issue with registering order.");
            return false;
        } finally {
            DbUtils.closeQuietly(getScentence());
            DbUtils.closeQuietly(getConnection());
        }
        return true;
    }
    public boolean createOrderSub(int id, String date, ArrayList<Object[]> recipes, String note, String time, int subId){ // recipes[0] = name, recipes[1] = portions.
        if(setUp()){
            try{
                System.out.println("INSERT INTO `order` VALUES(DEFAULT, "+OrdStatus.ACTIVE.getValue()
                        +", '" + date + "', " + id + ", '"+ note + "', '"+ time + "', "+ subId + ");");
                ResultSet res;
                ArrayList<Integer> recipeIDs = new ArrayList<Integer>();
                getScentence().executeQuery("START TRANSACTION;");
                int rowChanged = getScentence().executeUpdate("INSERT INTO `order` VALUES(DEFAULT, "+OrdStatus.ACTIVE.getValue()
                        +", '" + date + "', " + id + ", '"+ note + "', '"+ time + "', "+ subId + ");"); //Legger inn orderen med status aktiv.

                int orderID = 0;
                if(rowChanged > 0) {
                    res = getScentence().executeQuery("SELECT LAST_INSERT_ID() as id;"); // Henter den autoinkrementerte verdien.
                    if(res.next()) {
                        orderID = res.getInt("id");
                    }
                }
                else{
                    getScentence().executeQuery("ROLLBACK;");
                    return false;

                }
                for(Object[] name : recipes) {

                    res = getScentence().executeQuery("SELECT recipe_id FROM recipe WHERE name = '" + name[0] + "';");

                    if (res.next()) {
                        recipeIDs.add(res.getInt("recipe_id")); //Henter oppskrifts IDer for å koble oppskrifter med ordre.
                    } else {
                        getScentence().executeQuery("ROLLBACK");

                        return false;
                    }
                }

                for (int i = 0; i < recipeIDs.size(); i++) {
                    rowChanged = getScentence().executeUpdate("INSERT INTO order_recipe VALUES(" + orderID + ", " + recipeIDs.get(i) +
                            ", '" + recipes.get(i)[1] + "');");
                    if (!(rowChanged > 0)) {
                        getScentence().executeQuery("ROLLBACK;");
                        return false;
                    }
                }

                getScentence().executeQuery("COMMIT;");
            }

            catch (Exception e){
                System.err.println("Issue with creating order.");
                try {
                    getScentence().executeQuery("ROLLBACK");
                }
                catch (Exception ee){
                    System.err.println("Issue with rolling back transaction.");
                }
                return false;

            }
            finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }

        }
        else return false;
        return true;
    }
    /*.getOrderInfoFromId(orderId);
        ArrayList<Object[]> orderRecipes = orderManagement.getRecipesFromOrder(orderId);
        */
    public Object[] getOrderInfoFromId(int orderId){
        Object[] out = new Object[5];
        if(setUp()) {
            try {

                PreparedStatement prep = getConnection().prepareStatement("SELECT `order`.status, `order`.date, " +
                        "customer.name, `order`.note, `order`.time FROM `order`, customer WHERE `order`.order_id = ?" +
                        " AND `order`.customer_id = customer.customer_id;");
                prep.setInt(1, orderId);
                ResultSet res = prep.executeQuery();
                if (res.next()){
                    out[0] = res.getString("name");
                    out[1] = res.getString("date");
                    out[2] = res.getString("time");
                    out[3] = res.getString("note");
                    out[4] = OrderType.valueOf(res.getInt("status"));
                }

            } catch (Exception e) {
                System.err.println("Issue with finding order.");
            } finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }

        }
        return out;
    }
    public ArrayList<Object[]> getRecipesFromOrder(int orderId){
        ArrayList<Object[]> out = new ArrayList<>();
        if(setUp()) {
            try {

                PreparedStatement prep = getConnection().prepareStatement("SELECT recipe.name, order_recipe.portions " +
                        "FROM recipe, order_recipe WHERE order_recipe.order_id = ?" +
                        " AND order_recipe.recipe_id = recipe.recipe_id;");
                prep.setInt(1, orderId);
                ResultSet res = prep.executeQuery();
                while (res.next()){
                    Object[] obj = new Object[2];
                    obj[0] = res.getString("name");
                    obj[1] = res.getString("portions");
                    out.add(obj);

                }

            } catch (Exception e) {
                System.err.println("Issue with finding order.");
            } finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }

        }
        return out;
    }
}

