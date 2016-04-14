package Database;

import com.sun.xml.internal.bind.v2.TODO;
import org.apache.commons.dbutils.DbUtils;

import java.sql.*;
import java.util.*;

/**
 * Created by Evdal on 07.03.2016.
 */
public class    OrderManagement extends Management {
    public OrderManagement(){
        super();
    }

    String sqlUpdateStatus = "UPDATE `order` SET status = ? WHERE order_id = ?;";

    String sqlGetOrders = "SELECT `order`.order_id, customer.name ,customer.phone, customer.adress, `order`.date, `order`.status FROM `order`, customer WHERE " +
                            "`order`.customer_id = customer.customer_id AND `order`.status >= ? ORDER BY `date` DESC, status DESC;";

    String sqlOrderSearch = "SELECT `order`.order_id, customer.name ,customer.phone, customer.adress, `order`.date, `order`.status FROM `order`, customer WHERE " +
                            "(order_id LIKE '%'+?+'%' OR `order`.status LIKE '%'+?+'%' OR `date` LIKE '%'+?+'%' OR `name` LIKE '%'+?+'%' OR phone LIKE '%'+?+'%' " +
                            "OR adress LIKE '%'+?+'%') AND `order`.status >= ? AND `order`.customer_id = customer.customer_id ORDER BY `order`.status DESC, `date` DESC;";

    String sqlCreateOrderSub0 = "INSERT INTO `order` VALUES(DEFAULT, ?, ?, ?, ?, ?, ?);";
    // Andre setninger under +CreateOrderSub
        String sqlCreateOrderSub1 = "SELECT LAST_INSERT_ID() as id;";
        String sqlCreateOrderSub2 = "SELECT recipe_id FROM recipe WHERE name = ?;";
        String sqlCreateOrderSub3 = "INSERT INTO order_recipe VALUES( ?, ?, ?);";

    String sqlCreateOrder = "SELECT customer_id FROM customer WHERE email = ?;";
    String sqlGetOrderInfoFromId = "SELECT recipe.name, order_recipe.portions FROM recipe, order_recipe WHERE order_recipe.order_id = ? AND order_recipe.recipe_id = recipe.recipe_id;";
    String sqlUpdateOrderDate = "UPDATE order SET date = ? WHERE order_id = ?";
    String sqlUpdateOrderTime = "UPDATE order SET time = ? WHERE order_id = ?";



    Connection conn = null;


    public enum OrdStatus{
        INACTIVE(0),
        ACTIVE(1),
        READY_FOR_DELIVERY(2),
        DELIVERED(3);

        private int value;
        OrdStatus(int value){
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
        int rowChanged = 0;
        if(newStatus < OrderType.INACTIVE.getValue() || newStatus > OrderType.DELIVERED.getValue()) {
            return false;
        }
        if(setUp()){
            conn = getConnection();
            try{
                conn.setAutoCommit(false);
                PreparedStatement prep = conn.prepareStatement(sqlUpdateStatus);
                prep.setInt(1, newStatus);
                prep.setInt(2, orderID);
                rowChanged = prep.executeUpdate();
            }
            catch (Exception e){
                System.err.println("Issue with updating order status.");
                try {
                    conn.rollback();
                    conn.setAutoCommit(true);
                }catch (SQLException sqle){
                    System.err.print("Could not rollback");
                }
                return false;
            }
            finally {
                try {
                    conn.commit();
                    conn.setAutoCommit(true);
                }catch (SQLException sqle){
                    sqle.printStackTrace();
                }
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(conn);
            }
        }
        return rowChanged > 0;
    }
    public ArrayList<Object[]> getOrders(){
        ArrayList<Object[]> out = new ArrayList<>();
        if(setUp()){
            conn = getConnection();
            //Henter info fra ordre der ordren ikke er merket som inaktiv.
            try {
                PreparedStatement prep = conn.prepareStatement(sqlGetOrders);
                prep.setInt(1, OrdStatus.ACTIVE.getValue());
                ResultSet res = prep.executeQuery();
                while (res.next()){
                    out.add(createList(res));
                }
                prep.close();
            }
            catch (Exception e){
                System.err.println("ERROR 003: Issue with fetching orders.");
            }
            finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(conn);
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
        ArrayList<Object[]> out = new ArrayList<>();
        if(setUp()) {
            conn = getConnection();
            try {
                PreparedStatement prep = conn.prepareStatement(sqlOrderSearch);
                prep.setString(1, searchTerm);
                prep.setString(2, searchTerm);
                prep.setString(3, searchTerm);
                prep.setString(4, searchTerm);
                prep.setString(5, searchTerm);
                prep.setString(6, searchTerm);
                prep.setInt(7, OrdStatus.ACTIVE.getValue());

                ResultSet res = prep.executeQuery();
                prep.close();

                while (res.next()){
                    out.add(createList(res));
                }

            } catch (Exception e) {
                System.err.println("Issue with search.");
            } finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(conn);
            }

        }
        return out;

    }

    public boolean createOrderSub(int id, String date, ArrayList<Object[]> recipes, String note, String time, int subId){ // recipes[0] = name, recipes[1] = portions.
        int rowChanged;
        if(setUp()){
            conn = getConnection();
            try{
                ArrayList<Integer> recipeIDs = new ArrayList<>();
                conn.setAutoCommit(false);
                PreparedStatement prep = conn.prepareStatement(sqlCreateOrderSub0);
                prep.setInt(1, OrdStatus.ACTIVE.getValue());
                prep.setString(2, date);
                prep.setInt(3, id);
                prep.setString(4, note);
                prep.setString(5, time);
                prep.setInt(6, subId);

                rowChanged = prep.executeUpdate(); //Legger inn orderen med status aktiv.

                ResultSet res;
                int orderID = 0;

                if(rowChanged > 0) {
                    try{    // Må ikke bruke prep siden brukeren ikke kan endre input
                        res = getScentence().executeQuery(sqlCreateOrderSub1); // Henter den autoinkrementerte verdien.
                        if(res.next()) {
                            orderID = res.getInt("id");
                        }
                    }catch (SQLException sqle){
                        System.err.println("ERROR 002: Issue getting order id");
                        conn.rollback();
                        return false;
                    }
                }
                else{
                    conn.rollback();
                    System.err.println("OVERRUN");
                    return false;
                }

                for(Object[] name : recipes) { //[0] = quantity, [1] = name
                    prep = conn.prepareStatement(sqlCreateOrderSub2);
                    prep.setObject(1, name[1]);
                    res = prep.executeQuery();

                    if (res.next()) {
                        recipeIDs.add(res.getInt("recipe_id")); //Henter oppskrifts IDer for å koble oppskrifter med ordre.
                    } else {
                        conn.rollback();
                        System.err.println("THE");
                        return false;
                    }
                    prep.close();
                }




                for (int i = 0; i < recipeIDs.size(); i++) {
                    prep = conn.prepareStatement(sqlCreateOrderSub3);
                    prep.setInt(1, orderID);
                    prep.setInt(2, recipeIDs.get(i));
                    prep.setObject(3, recipes.get(i)[0]);
                    rowChanged = prep.executeUpdate();

                    if (!(rowChanged > 0)) {
                        conn.rollback();
                        System.err.println("CAPITALIST");
                        return false;
                    }
                    prep.close();
                }
            }

            catch (SQLException sqle){
                System.err.println("ERROR 001: Issue with creating order");
                sqle.printStackTrace();
                try {
                    conn.rollback();
                }
                catch (SQLException ee){
                    System.err.println("Could not rollback");
                }
                return false;
            }
            finally {
                try {
                    conn.commit();
                    conn.setAutoCommit(true);
                }catch (SQLException sqle){
                    sqle.printStackTrace();
                }

                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        else {
            System.err.println("EVEN");
            return false;
        }
        return true;
    }



    public boolean createOrder(String customerMail, String date, ArrayList<Object[]> recipes, String note, String time){
        int id = -1;
        try {
            if(setUp()) {
                conn = getConnection();
                PreparedStatement prep = getConnection().prepareStatement(sqlCreateOrder);
                prep.setString(1, customerMail);
                ResultSet res = prep.executeQuery();

                if (res.next()) {
                    id = res.getInt("customer_id");
                }
                prep.close();
                //Metode ment for GUI, her slipper man å sende inn en subscription id, og metoden finner Customer ID selv.
                //Deretter kalles create order for subscription med de nye verdiene.
            }
            if(!createOrderSub(id, date, recipes, note, time, -1)){
                System.err.println("PLEASE");
                return false;
            }
            //-1 er verdien som blir satt dersom det ikke finnes en subscription.

        } catch (SQLException e) {
            System.err.println("Issue with registering order.");
            return false;
        } finally {
            DbUtils.closeQuietly(getScentence());
            DbUtils.closeQuietly(getConnection());
        }
        return true;
    }


    /*.getOrderInfoFromId(orderId);
        ArrayList<Object[]> orderRecipes = orderManagement.getRecipesFromOrder(orderId);
        */
    public Object[] getOrderInfoFromId(int orderId){
        Object[] out = new Object[5];
        if(setUp()) {
            conn = getConnection();
            try {

                PreparedStatement prep = conn.prepareStatement("SELECT `order`.status, `order`.date, " +
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
                prep.close();

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
            conn = getConnection();
            try {

                PreparedStatement prep = conn.prepareStatement(sqlGetOrderInfoFromId);
                prep.setInt(1, orderId);
                ResultSet res = prep.executeQuery();
                while (res.next()){
                    Object[] obj = new Object[2];
                    obj[0] = res.getString("name");
                    obj[1] = res.getString("portions");
                    out.add(obj);

                }
                prep.close();
            } catch (Exception e) {
                System.err.println("Issue with finding order.");
            } finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }

        }
        return out;
    }

    public boolean UpdateOrderDate(String orderDate, int orderID){
        int rowChanged = 0;
        if(setUp()) {
            conn = getConnection();
            try {
                getConnection().setAutoCommit(false);

                PreparedStatement prep = getConnection().prepareStatement(sqlUpdateOrderDate);
                prep.setString(1, orderDate);
                prep.setInt(2, orderID);
                rowChanged = prep.executeUpdate();

                prep.close();

            } catch (SQLException e) {
                System.err.println("Issue with updating order date");
                try {
                    getConnection().rollback();
                    getConnection().setAutoCommit(true);
                }catch (SQLException sqle){
                    System.err.println("Could not rollback");
                }

                return false;
            } finally {
                try {
                    conn.commit();
                    conn.setAutoCommit(true);
                }catch (SQLException sqle){
                    sqle.printStackTrace();
                }
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        if(rowChanged > 0) return true;
        return false;
    }

    public boolean updateOrderTime(String orderTime, int orderID){
        int rowChanged = 0;
        if(setUp()) {
            conn = getConnection();
            try {
                getConnection().setAutoCommit(false);

                PreparedStatement prep = getConnection().prepareStatement(sqlUpdateOrderTime);
                prep.setString(1, orderTime);
                prep.setInt(2, orderID);
                rowChanged = prep.executeUpdate();

                getConnection().commit();

            } catch (SQLException e) {
                System.err.println("Issue with updating order time");
                try {
                    getConnection().rollback();
                    getConnection().setAutoCommit(true);
                }catch (SQLException sqle){
                    System.err.println("Could not rollback");
                }

                return false;
            } finally {
                try {
                    getConnection().setAutoCommit(true);
                }catch (SQLException sqle){
                    sqle.printStackTrace();
                }
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        if(rowChanged > 0) return true;
        return false;
    }
    /*
    TODO: ska mekk
    */
    public boolean UpdateOrderCustomer(){
        return false;
    }
    public boolean UpdateOrderRecipe(){
        return false;
    }
    public boolean UpdateOrderComment(){
        return false;
    }
}

