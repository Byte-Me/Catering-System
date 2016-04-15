package Database;

import org.apache.commons.dbutils.DbUtils;

import java.sql.ResultSet;
import java.util.ArrayList;

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

    public ArrayList<Object[]> getDeliveryInfo(ArrayList<String> adresses){
        if(setUp()) {
            ArrayList<Object[]> out = new ArrayList<>();
            try {
                for(String adress : adresses) {
                    ResultSet res = getScentence().executeQuery("SELECT `order`.order_id, customer.name, customer.phone, customer.adress FROM `order`, customer WHERE " +
                            "`order`.status = 3 AND `order`.customer_id = customer.customer_id AND customer.adress = '"+adress+"';");
                    if (res.next()) {
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
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());

            }
            return out;
        }
        else return null;
    }


    public ArrayList<Object[]> getDeliveryReady(){
        ResultSet res;
        if(setUp()) {
            ArrayList<Object[]> out = new ArrayList<>();

            try {
                res = getScentence().executeQuery("SELECT customer.phone, customer.adress FROM `order`, customer WHERE " +
                        "`order`.status = 3 AND `order`.customer_id = customer.customer_id;");
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
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());

            }
            return out;
        }
        else return null;
    }
    public ArrayList<String> getAdressReady(){
        ResultSet res;
        if(setUp()) {
            ArrayList<String> out = new ArrayList<>();

            try {
                res = getScentence().executeQuery("SELECT customer.adress FROM `order`, customer WHERE "+
                         "`order`.status = 3 AND `order`.customer_id = customer.customer_id;");
                while (res.next()) {
                    out.add(res.getString("adress"));
                }


            } catch (Exception e) {
                System.err.println("Issue with getting today's orders.");
                return null;
            } finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());

            }
            return out;
        }
        else return null;
    }
}
