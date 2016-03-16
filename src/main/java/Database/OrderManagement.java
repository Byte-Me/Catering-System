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
public class OrderManagement extends Management{
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
    public boolean updateStatus(int orderID, int newStatus){
        int numb = 0;
        if(newStatus < 0 || newStatus > 3) { // 0 = inactive, 1 = active, 2 = ready for delivery, 3 = delivered
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
            try {
                ResultSet res = getScentence().executeQuery("SELECT `order`.order_id, customer.name ,customer.phone, customer.adress, `order`.date, `order`.status " +
                        "FROM `order`, customer WHERE `order`.customer_id = customer.customer_id ORDER BY `date` DESC, status DESC;");
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
        obj[5] = res.getInt("status");
        return obj;
    }
    public ArrayList<Object[]> orderSearch(String searchTerm){ // TODO: IKKE TESTET!!
        ResultSet res;
        ArrayList<Object[]> out = new ArrayList<Object[]>();
        if(setUp()) {
            try {

                res = getScentence().executeQuery("SELECT `order`.order_id, customer.name ,customer.phone, customer.adress, `order`.date, `order`.status FROM `order`, customer WHERE (order_id LIKE '%" + searchTerm + "%' OR `order`.status LIKE '%" +
                        searchTerm + "%' OR `date` LIKE '%" + searchTerm +
                        "%' OR `name` LIKE '%" + searchTerm + "%' OR email LIKE '%" + searchTerm + "%') AND `order`.status >= 0 AND `order`.customer_id = customer.customer_id ORDER BY `order`.status DESC, `date` DESC;");

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
    public boolean createOrder(String customerMail, String date, ArrayList<Object[]> recipes){ // recipes[0] = name, recipes[1] = portions.
        if(setUp()){
            try{
                getScentence().executeQuery("START TRANSACTION");
                ResultSet res = getScentence().executeQuery("SELECT customer_id FROM customer WHERE email = '" + customerMail + "';");
                ArrayList<Integer> recipeIDs;
                if(res.next()){
                    int id = res.getInt("customer_id");
                    recipeIDs = new ArrayList<Integer>();
                    for(Object[] name : recipes){
                        res = getScentence().executeQuery("SELECT recipe_id FROM recipe WHERE `name` = '" + name[0] + "';");

                        if(res.next()){
                            recipeIDs.add(res.getInt("recipe_id"));
                        }
                        else{
                            getScentence().executeQuery("ROLLBACK");
                            return false;
                        }
                    }
                    int rowChanged = getScentence().executeUpdate("INSERT INTO `order` VALUES(DEFAULT, 1, '" + date + "', '" + id + "');");
                    if(rowChanged > 0) {
                        res = getScentence().executeQuery("SELECT LAST_INSERT_ID() as id;");
                        res.next();
                        int orderID = res.getInt("id");

                        for (int i = 0; i < recipeIDs.size(); i++){
                            rowChanged = getScentence().executeUpdate("INSERT INTO order_recipe VALUES(" + orderID + ", " + recipeIDs.get(i) +
                                    ", '" + recipes.get(i)[1] + "');");
                            if(rowChanged < 1){
                                getScentence().executeQuery("ROLLBACK;");
                                return false;
                            }
                        }
                    }
                    else{
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
}

