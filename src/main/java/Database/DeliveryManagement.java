package Database;

import org.apache.commons.dbutils.DbUtils;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Evdal on 07.03.2016.
 */
public class DeliveryManagement extends Management{
    public DeliveryManagement(){
        super();
    }

    //ORDER STAUTUS

    /*
    0 = inactive;
    1 = active
    2 = in the making
    3 = ready for delivery
    4 = delivered       order_id, Name, phone, address
     */

    //SQL setning for getOrdersFromDriver
    private final String sqlGetOrdersFromDriver2 = "SELECT `order`.order_id, customer.name, customer.phone, customer.adress FROM `order`, customer WHERE `order`.status = ? AND `order`.customer_id = customer.customer_id AND `order`.driver_username = ?";
    private final String sqlGetOrdersFromDriver1 = "SELECT `order`.order_id, customer.name, customer.phone, customer.adress FROM `order`, customer WHERE `order`.status = ? AND `order`.customer_id = customer.customer_id";
    private final String sqlConnectDriverToOrder = "UPDATE `order` SET driver_username = ? WHERE order_id = ?";
    private final String sqlCountDriverDeliveries = "SELECT count(*) AS x FROM `order` WHERE driver_username = ? AND status = ?;";

    Connection conn = null;
    ResultSet res = null;
    PreparedStatement prep = null;

    /**
     * Gets information of the delivery
     * @param orderIds Arraylist of every orderId
     * @return In a specific order and what adress that needs to be deliverd to
     */

    public ArrayList<Object[]> getDeliveryInfo(ArrayList<Integer> orderIds){
        if(setUp()) {
            ArrayList<Object[]> out = new ArrayList<>();
            try {
                conn = getConnection();
                for(Integer id : orderIds) {

                    prep = conn.prepareStatement("SELECT `order`.order_id, customer.name, customer.phone, customer.adress FROM `order`, customer WHERE " +
                            "`order`.customer_id = customer.customer_id AND `order`.order_id = ?;");
                    prep.setInt(1, id);
                    res = prep.executeQuery();

                    while (res.next()) {
                        Object[] obj = new Object[5];
                        obj[0] = res.getString("order_id");
                        obj[1] = res.getString("name");
                        obj[2] = res.getString("phone");
                        obj[3] = res.getString("adress");
                        out.add(obj);
                    }
                }
            } catch (Exception e) {
                System.err.println("Issue with getting customer information.");
                e.printStackTrace();
                return null;
            } finally {
                finallyStatement(res, prep);
            }
            return out;
        }
        else return null;
    }


    /**
     * Gets what delivery is ready to be deliverd
     * @return Every delivery that is ready to be deliverd
     */
    public ArrayList<Object[]> getDeliveryReady(){
        if(setUp()) {
            ArrayList<Object[]> out = new ArrayList<>();
            try {
                conn = getConnection();
                prep = conn.prepareStatement("SELECT customer.phone, customer.adress FROM `order`, customer WHERE " +
                        "`order`.status = 3 AND `order`.customer_id = customer.customer_id;");
                res = prep.executeQuery();
                while (res.next()) {
                    Object[] obj = new Object[2];
                    obj[0] = res.getString("adress");
                    obj[1] = res.getString("phone");
                    out.add(obj);
                }
            } catch (Exception e) {
                System.err.println("Issue with getting today's orders.");
                return null;
            } finally {
                finallyStatement(res, prep);
            }
            return out;
        }
        else return null;
    }

    /**
     * Gets orders for each driver
     * @param username The username of the driver
     * @return Arraylist with objects that contains the order id, name, phone number and adress
     */
    public ArrayList<Object[]> getOrdersForDriver(String username){
        ArrayList<Object[]> out = new ArrayList<>();
        if(setUp()) {
            try {
                if("Ready For Delivery".equals(username)){
                    PreparedStatement prep = getConnection().prepareStatement(sqlGetOrdersFromDriver1);
                    prep.setInt(1, OrderManagement.OrderType.READY.getValue());
                    res = prep.executeQuery();
                    while (res.next()){
                        Object[] obj = new Object[5];
                        obj[0] = res.getInt("order_id");
                        obj[1] = res.getString("name");
                        obj[2] = res.getString("phone");
                        obj[3] = res.getString("adress");
                        out.add(obj);
                    }
                }
                else {
                    PreparedStatement prep = getConnection().prepareStatement(sqlGetOrdersFromDriver2);
                    prep.setInt(1, OrderManagement.OrderType.DRIVING.getValue());
                    prep.setString(2, username);
                    res = prep.executeQuery();
                    while (res.next()) {
                        Object[] obj = new Object[5];
                        obj[0] = res.getInt("order_id");
                        obj[1] = res.getString("name");
                        obj[2] = res.getString("phone");
                        obj[3] = res.getString("adress");
                        out.add(obj);
                    }
                }
            } catch (Exception e) {
                System.err.println("Issue with getting drivers orders.");
                return null;
            } finally {
                finallyStatement(res, prep);

            }
            return out;
        }
        else return null;
    }

    /**
     * Gets every adress that the delivery is ready to deliver
     * @return Arraylist with every adress that is ready to deliver to
     */
    public ArrayList<String> getAdressReady(){
        if(setUp()) {
            ArrayList<String> out = new ArrayList<>();
            try {
                conn = getConnection();
                prep = conn.prepareStatement("SELECT customer.adress FROM `order`, customer WHERE "+
                        "`order`.status = 3 AND `order`.customer_id = customer.customer_id;");
                res = prep.executeQuery();

                while (res.next()) {
                    out.add(res.getString("adress"));
                }
            } catch (Exception e) {
                System.err.println("Issue with getting today's orders.");
                return null;
            } finally {
                finallyStatement(res, prep);
            }
            return out;
        }
        else return null;
    }

    /**
     * Connects a driver to a order
     * @param username The username of the driver
     * @param orderId The order id of the orders ready to deliver
     * @return True or false depending if the method was successful
     */
    public boolean connectDriverToOrder(String username, int orderId){
        int res = 0;
        if(setUp()) {
            try {
                PreparedStatement prep = getConnection().prepareStatement(sqlConnectDriverToOrder);
                prep.setString(1,username);
                prep.setInt(2,orderId);
                res = prep.executeUpdate();

            } catch (Exception e) {
                System.err.println("Issue with updating order.");
                return false;
            } finally {
                finallyStatement(null, prep);

            }
        }
        return res > 0;
    }

    /**
     * Count every order deliverd by driver
     * @param username The username of the driver
     * @return The number of deliverd deliveries
     */
    public int countDriverDeliveries(String username){
        int out = 0;
        if(setUp()) {
            try {
                PreparedStatement prep = getConnection().prepareStatement(sqlCountDriverDeliveries);
                prep.setString(1, username);
                prep.setInt(2, OrderManagement.OrderType.DRIVING.getValue());
                ResultSet res = prep.executeQuery();
                if(res.next()){
                    out = res.getInt("x");
                }

            } catch (Exception e) {
                System.err.println("Issue with updating order.");
                return -1;
            } finally {
                finallyStatement(res, prep);

            }
        }
        return out;
    }

}
