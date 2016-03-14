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
    4 = delivered
     */

    public ArrayList<Object[]> getDeliveryReady(){
        ResultSet res = null;
        if(setUp()) {
            ArrayList<Object[]> out = new ArrayList<Object[]>();

            try {
                res = getScentence().executeQuery("SELECT recipe.name, customer.phone, customer.adress FROM `order`, customer, recipe WHERE `order`.status = 3 AND `order`.customer_id = customer.customer_id " +
                        "AND `order`.recipe_id = recipe.recipe_id;");
                while (res.next()) {
                    Object[] obj = new Object[3];
                    obj[0] = res.getString("adress");
                    obj[1] = res.getString("phone");
                    obj[2] = res.getString("phone");
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
        ResultSet res = null;
        if(setUp()) {
            ArrayList<String> out = new ArrayList<String>();

            try {
                res = getScentence().executeQuery("SELECT customer.adress FROM `order`, customer WHERE `order`.status = 3 AND `order`.customer_id = customer.customer_id;");
                while (res.next()) {
                    Object[] obj = new Object[3];
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
