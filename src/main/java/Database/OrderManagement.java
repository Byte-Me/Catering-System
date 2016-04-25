package Database;

import Food.FoodFinance;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import java.sql.*;
import java.util.*;

/**
 * Created by Evdal on 07.03.2016.
 */

public class OrderManagement extends Management {
    private FinanceManagement financeManagement = new FinanceManagement();
    private CustomerManagement customerManagement = new CustomerManagement();
    private FoodManagement foodManagement  = new FoodManagement();

    public OrderManagement() {
        super();
    }

    // SQL setning for UpdateStatus metode
    String sqlUpdateStatus = "UPDATE `order` SET status = ? WHERE order_id = ?;";

    // SQL setning for getDeletedOrders metode
    String sqlGetDeletedOrders = "SELECT `order`.order_id, customer.name ,customer.phone, customer.adress, `order`.date, `order`.status " +
            "FROM `order`, customer WHERE `order`.customer_id = customer.customer_id AND `order`.status = ? ORDER BY `date` DESC, status DESC;";

    // SQL setning for GetORders metode
    String sqlGetOrders = "SELECT `order`.order_id, customer.name ,customer.phone, customer.adress, `order`.date, `order`.status FROM `order`, customer WHERE " +
            "`order`.customer_id = customer.customer_id AND `order`.status >= ? ORDER BY `date` DESC, status DESC;";


    // SQL setning for OrderSearch metode
    String sqlOrderSearch = "SELECT `order`.order_id, customer.name ,customer.phone, customer.adress, `order`.date, `order`.status FROM `order`, customer WHERE " +
            "(order_id LIKE ? OR `order`.status LIKE ? OR `date` LIKE ? OR `name` LIKE ? OR phone LIKE ? OR adress LIKE ?) AND `order`.status >=" +
            " ? AND `order`.customer_id = customer.customer_id ORDER BY `order`.status DESC, `date` DESC;";

    // SQL setning for CreateOrderSub metode
    String sqlCreateOrderSub0 = "INSERT INTO `order` VALUES(DEFAULT, ?, ?, ?, ?, ?, ?,DEFAULT);";

    // Andre setninger under CreateOrderSub
    String sqlCreateOrderSub1 = "SELECT LAST_INSERT_ID() as id;";
    String sqlCreateOrderSub2 = "SELECT recipe_id FROM recipe WHERE name = ?;";
    String sqlCreateOrderSub3 = "INSERT INTO order_recipe VALUES( ?, ?, ?, DEFAULT);";

    // SQL setning for createOrder metode
    String sqlGetEmail = "SELECT customer_id FROM customer WHERE email = ?;";

    // SQL setning for getOrderInfoFromId metode
    String sqlGetOrderInfoFromId = "SELECT `order`.status, `order`.date, customer.name, `order`.note, `order`.time FROM `order`," +
            "customer WHERE `order`.order_id = ? AND `order`.customer_id = customer.customer_id;";

    // SQL setning for getRecipeFromOrder metode
    String sqlGetRecipesFromOrder = "SELECT recipe.name, order_recipe.portions, recipe.recipe_id FROM recipe, order_recipe WHERE order_recipe.order_id = ? AND order_recipe.recipe_id = recipe.recipe_id;";

    // SQL setning for updateOrderDate metode
    String sqlUpdateOrderDate = "UPDATE `order` SET date = ? WHERE order_id = ?";

    // SQL setning for updateOrderTime metode
    String sqlUpdateOrderTime = "UPDATE `order` SET time = ? WHERE order_id = ?";

    // SQL setning for updateOrderCustomer metode
    String sqlUpdateOrderCustomer = "UPDATE `order` SET customer_id = ? WHERE order_id = ?;";

    // SQL setning for updateOrderRecipe metode
    String sqlUpdateOrderRecipes1 = "DELETE FROM order_recipe WHERE order_id = ?";
    String sqlUpdateOrderRecipes2 = "INSERT INTO order_recipe VALUES(?,?,?,DEFAULT);";

    // SQL setning for updateOrderPortion metode
    String sqlUpdateOrderPortions = "UPDATE order_recipe SET portions = ? WHERE order_id = ? AND recipe_id = ?;";

    // SQL setning for updateOrderComment metode
    String sqlUpdateOrderComment = "UPDATE `order` SET note = ? WHERE order_id = ?;";




    private final String sqlCreateOrder = "SELECT customer_id FROM customer WHERE email = ?;";
    private final String getSqlGetRecipesFromOrder = "SELECT recipe.name, order_recipe.portions, order_recipe.recipe_id FROM recipe, order_recipe WHERE order_recipe.order_id = ? AND order_recipe.recipe_id = recipe.recipe_id;";


    Connection conn = null;
    PreparedStatement prep = null;
    ResultSet res = null;


    public enum OrderType {
        INACTIVE, ACTIVE, READY, DRIVING,DELIVERED;

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
            return constName.substring(0, 1) + constName.substring(1).toLowerCase();
        }

    }

    public boolean updateStatus(int orderID, int newStatus) {
        int rowChanged = 0;
        if (newStatus < OrderType.INACTIVE.getValue() || newStatus > OrderType.DELIVERED.getValue()) {
            return false;
        }
        if (setUp()) {
            conn = getConnection();
            try {
                conn.setAutoCommit(false);
                PreparedStatement prep = conn.prepareStatement(sqlUpdateStatus);
                prep.setInt(1, newStatus);
                prep.setInt(2, orderID);
                rowChanged = prep.executeUpdate();
            } catch (Exception e) {
                System.err.println("ERROR 010: Issue with updating order status.");
                return false;
            } finally {
                finallyStatement(res, prep);
            }
        }
        if (newStatus == OrderType.DELIVERED.getValue()) {
            financeManagement.addIncomeToDatabase(FoodFinance.findOrderPrice(orderID));
        }
        return rowChanged > 0;
    }

    public ArrayList<Object[]> getDeletedOrders() {
        ArrayList<Object[]> out = new ArrayList<>();
        if (setUp()) {
            //Henter info fra ordre der ordren er merket som inaktiv.
            conn = getConnection();
            try {
                prep = conn.prepareStatement(sqlGetDeletedOrders);
                prep.setInt(1, OrderType.INACTIVE.getValue());
                res = prep.executeQuery();
                while (res.next()) {
                    out.add(createList(res));
                }
            } catch (Exception e) {
                System.err.println("ERROR 005: Issue with fetching orders");
            } finally {
                finallyStatement(res, prep);
            }
        }
        return out;
    }

    public ArrayList<Object[]> getOrders() {
        ArrayList<Object[]> out = new ArrayList<>();
        if (setUp()) {
            conn = getConnection();
            //Henter info fra ordre der ordren ikke er merket som inaktiv.
            try {
                prep = conn.prepareStatement(sqlGetOrders);
                prep.setInt(1, OrderType.ACTIVE.getValue());
                res = prep.executeQuery();
                while (res.next()) {
                    out.add(createList(res));
                }
            } catch (Exception e) {
                System.err.println("ERROR 003: Issue with fetching orders.");
            } finally {
                finallyStatement(res, prep);
            }
        }
        return out;
    }

    private Object[] createList(ResultSet res) throws Exception {
        Object[] obj = new Object[6];
        obj[0] = res.getInt("order_id");
        obj[1] = res.getString("name");
        obj[2] = res.getString("phone");
        obj[3] = res.getString("adress");
        obj[4] = res.getString("date");
        obj[5] = OrderType.valueOf(res.getInt("status"));
        return obj;
    }

    public ArrayList<Object[]> orderSearch(String searchTerm) {
        ArrayList<Object[]> out = new ArrayList<>();
        if (setUp()) {
            conn = getConnection();
            try {
                searchTerm = "%" + searchTerm + "%";
                prep = conn.prepareStatement(sqlOrderSearch);
                prep.setString(1, searchTerm);
                prep.setString(2, searchTerm);
                prep.setString(3, searchTerm);
                prep.setString(4, searchTerm);
                prep.setString(5, searchTerm);
                prep.setString(6, searchTerm);
                prep.setInt(7, OrderType.ACTIVE.getValue());

                res = prep.executeQuery();
                while (res.next()) {
                    out.add(createList(res));
                }

            } catch (Exception e) {
                System.err.println("Issue with search.");
            } finally {
                finallyStatement(res, prep);
            }
        }
        return out;
    }

    public boolean createOrderSub(int id, String date, ArrayList<Object[]> recipes, String note, String time, int subId) { // recipes[0] = name, recipes[1] = portions.
        int rowChanged;
        if (setUp()) {
            conn = getConnection();
            try {
                ArrayList<Integer> recipeIDs = new ArrayList<>();
                conn.setAutoCommit(false);
                prep = conn.prepareStatement(sqlCreateOrderSub0);
                prep.setInt(1, OrderType.ACTIVE.getValue());
                prep.setString(2, date);
                prep.setInt(3, id);
                prep.setString(4, note);
                prep.setString(5, time);
                prep.setInt(6, subId);
                System.out.println(prep.toString());
                rowChanged = prep.executeUpdate(); //Legger inn orderen med status aktiv.
                int orderID = 0;

                if (rowChanged > 0) {
                    try {    // Må ikke bruke prep siden brukeren ikke kan endre input
                        res = getScentence().executeQuery(sqlCreateOrderSub1); // Henter den autoinkrementerte verdien.
                        if (res.next()) {
                            orderID = res.getInt("id");
                        }
                    } catch (SQLException sqle) {
                        System.err.println("ERROR 002: Issue getting order id");
                        rollbackStatement();
                        return false;
                    }
                } else {
                    rollbackStatement();
                    System.out.println("2");
                    return false;
                }

                for (Object[] name : recipes) { //[0] = quantity, [1] = name
                    prep = conn.prepareStatement(sqlCreateOrderSub2);
                    System.out.println(Arrays.toString(name));
                    prep.setObject(1, name[1]);
                    System.out.println(prep.toString());

                    res = prep.executeQuery();

                    if (res.next()) {
                        recipeIDs.add(res.getInt("recipe_id")); //Henter oppskrifts IDer for å koble oppskrifter med ordre.
                    } else {
                        rollbackStatement();
                        return false;
                    }
                }

                for (int i = 0; i < recipeIDs.size(); i++) {
                    prep = conn.prepareStatement(sqlCreateOrderSub3);
                    prep.setInt(1, orderID);
                    prep.setInt(2, recipeIDs.get(i));
                    prep.setObject(3, recipes.get(i)[0]);
                    System.out.println(prep.toString());

                    rowChanged = prep.executeUpdate();

                    if (!(rowChanged > 0)) {
                        rollbackStatement();
                        return false;
                    }
                }
            } catch (SQLException sqle) {
                sqle.printStackTrace();
                System.err.println("ERROR 001: Issue with creating order");
                rollbackStatement();
                return false;
            } finally {
                finallyStatement(res, prep);
            }
        } else {
            return false;
        }
        return true;
    }
/*
    public int getOrderID(String email){
        int id = -1;
        if(setUp()){
            try {
                conn = getConnection();
                prep = conn.prepareStatement(sqlGetEmail);
                prep.setString(1, email);
                res = prep.executeQuery();

                if (res.next()) {
                    id = res.getInt("customer_id");
                }
            }catch (SQLException sqle){
                System.err.println("ERROR 014: Issue with finding order id");
            }finally {
                finallyStatement();
            }
        }
        return id;
    }*/


    public boolean createOrder(String email, String date, ArrayList<Object[]> recipes, String note, String time) {
        int id = -1;
        try {
            if (setUp()) {
                conn = getConnection();
                prep = conn.prepareStatement(sqlGetEmail);
                prep.setString(1, email);
                res = prep.executeQuery();

                if (res.next()) {
                    id = res.getInt("customer_id");
                }
                //Metode ment for GUI, her slipper man å sende inn en subscription id, og metoden finner Customer ID selv.
                //Deretter kalles create order for subscription med de nye verdiene.
            }
            if (!createOrderSub(id, date, recipes, note, time, -1)) {
                return false;
            }
            //-1 er verdien som blir satt dersom det ikke finnes en subscription.

        } catch (SQLException e) {
            System.err.println("ERROR 011: Issue with registering order.");
            return false;
        } finally {
            finallyStatement(res, prep);
        }
        return true;
    }


    /*.getOrderInfoFromId(orderId);
        ArrayList<Object[]> orderRecipes = orderManagement.getRecipesFromOrder(orderId);
        */
    public Object[] getOrderInfoFromId(int orderId) {
        Object[] out = new Object[6];
        if (setUp()) {
            conn = getConnection();
            try {
                prep = conn.prepareStatement(sqlGetOrderInfoFromId);
                prep.setInt(1, orderId);
                res = prep.executeQuery();
                if (res.next()) {
                    out[0] = res.getString("name");
                    out[1] = res.getString("date");
                    out[2] = res.getString("time");
                    out[3] = res.getString("note");
                    out[4] = OrderType.valueOf(res.getInt("status"));

                }
            } catch (Exception e) {
                System.err.println("ERROR 012: Issue with finding order.");
            } finally {
                finallyStatement(res, prep);
            }

        }
        return out;
    }

    public ArrayList<Object[]> getRecipesFromOrder(int orderId) {
        ArrayList<Object[]> out = new ArrayList<>();
        if (setUp()) {
            conn = getConnection();
            try {
                prep = conn.prepareStatement(sqlGetRecipesFromOrder);
                prep.setInt(1, orderId);
                res = prep.executeQuery();
                while (res.next()) {
                    Object[] obj = new Object[2];
                    obj[0] = res.getString("name");
                    obj[1] = res.getString("portions");
                    out.add(obj);
                }
            } catch (Exception e) {
                System.err.println("ERROR 013: Issue with finding order.");
            } finally {
                finallyStatement(res, prep);
            }
        }
        return out;
    }


    // Sletter/ setter en ordre til inaktiv
    public void deleteOrder(int orderID) {
        updateStatus(orderID, OrderType.INACTIVE.getValue());
    }

    private boolean updateDatabase(String sql, int orderID, String input, String errorMessage) {
        int rowChanged = 0;
        if (setUp()) {
            conn = getConnection();
            try {
                conn.setAutoCommit(false);
                prep = conn.prepareStatement(sql);
                prep.setString(1, input);
                prep.setInt(2, orderID);
                rowChanged = prep.executeUpdate();
            } catch (SQLException e) {
                System.err.println(errorMessage);
                rollbackStatement();
                return false;
            } finally {
                finallyStatement(res, prep);
            }
        }
        return rowChanged > 0;
    }

    private boolean updateDatabase(String sql, int orderID, int input1, int input2, String errorMessage) {
        int rowChanged = 0;
        if (setUp()) {
            conn = getConnection();
            try {
                conn.setAutoCommit(false);
                prep = conn.prepareStatement(sql);
                prep.setInt(1, input1);
                prep.setInt(2, orderID);
                prep.setInt(3, input2);
                rowChanged = prep.executeUpdate();
            } catch (SQLException sqle) {
                System.err.println(errorMessage);
                rollbackStatement();
                return false;
            } finally {
                finallyStatement(res, prep);
            }
        }
        return rowChanged > 0;
    }

    public boolean updateOrderDate(String orderDate, int orderID) {
        return updateDatabase(sqlUpdateOrderDate, orderID, orderDate, "Issue with updating order date");
    }

    public boolean updateOrderTime(String orderTime, int orderID) {
        return updateDatabase(sqlUpdateOrderTime, orderID, orderTime, "Issue with updating order time");
    }

    public boolean updateOrderCustomer(int orderID, int newCustId) {
        int rowChanged = 0;
        if (setUp()) {
            conn = getConnection();
            try {
                conn.setAutoCommit(false);
                prep = conn.prepareStatement(sqlUpdateOrderCustomer);
                prep.setInt(1, newCustId);
                prep.setInt(2, orderID);
                rowChanged = prep.executeUpdate();
            } catch (SQLException sqle) {
                System.err.println("ERROR 004: Issue updating order customer id");
                rollbackStatement();
                return false;
            } finally {
                finallyStatement(res, prep);
            }
        }
        return rowChanged > 0;
    }


    public boolean updateOrderRecipes(int orderID, ArrayList<Object[]> recipes) { //obj[1] = recipe_id, obj[0] = portions
        int rowChanged = 0;
        if (setUp()) {
            conn = getConnection();
            try {
                conn.setAutoCommit(false);
                prep = conn.prepareStatement(sqlUpdateOrderRecipes1); //deletes order_recipe rows
                prep.setInt(1, orderID);
                rowChanged = prep.executeUpdate();
                for (Object[] recipe : recipes) {
                    int recipeId = foodManagement.getRecipeIDPub((String) recipe[1]);
                    prep = conn.prepareStatement(sqlUpdateOrderRecipes2);
                    prep.setInt(1, orderID);
                    prep.setInt(2, recipeId);
                    prep.setObject(3, recipe[0]);
                    prep.executeUpdate();
                }
            } catch (SQLException sqle) {
                System.err.println("ERROR 007: Issue with updating order recipe");
                rollbackStatement();
                return false;
            } finally {
                finallyStatement(res, prep);
            }
        }
        return rowChanged > 0;

    }

    public boolean updateRecipePortions(int newPortions, int recipeID, int orderID) {
        return updateDatabase(sqlUpdateOrderPortions, orderID, newPortions, recipeID, "Issue with updating order portions");
    }

    public boolean updateOrderComment(String comment, int orderID) {
        return updateDatabase(sqlUpdateOrderComment, orderID, comment, "Issue with updating order comment");
    }
}