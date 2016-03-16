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
                ResultSet res = getScentence().executeQuery("SELECT `order`.order_id, `order`.status, `order`.date, customer.name, customer.email FROM `order`, customer WHERE `order`.customer_id = customer.customer_id ORDER BY date DESC;");
                while(res.next()){
                    Object[] obj = new Object[5];
                    obj[0] = res.getInt("order_id");
                    obj[1] = res.getString("name");
                    obj[2] = res.getString("email");
                    obj[3] = res.getString("date");
                    obj[4] = res.getInt("status");
                    out.add(obj);
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
    public ArrayList<Object[]> orderSearch(String searchTerm){ // TODO: IKKE TESTET!!
        ResultSet res;
        ArrayList<Object[]> out = new ArrayList<Object[]>();
        if(setUp()) {
            try {
                res = getScentence().executeQuery("SELECT `order`.order_id, `order`.status, `order`.date, customer.name, customer.email FROM `order`, customer FROM customer WHERE order_id LIKE '%" + searchTerm + "%' OR status LIKE '%" +
                        searchTerm + "%' OR `date` LIKE '%" + searchTerm +
                        "%' OR `name` LIKE '%" + searchTerm + "%' OR email LIKE '%" + searchTerm + "%' AND status > 0 ORDER BY date DESC;");

                while (res.next()){
                    Object[] obj = new Object[5];
                    obj[0] = res.getInt("order_id");
                    obj[1] = res.getString("name");
                    obj[2] = res.getString("email");
                    obj[3] = res.getString("date");
                    obj[4] = res.getInt("status");
                    out.add(obj);
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
}

