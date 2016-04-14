package Database;

import org.apache.commons.dbutils.DbUtils;
import org.omg.PortableInterceptor.INACTIVE;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import static Database.OrderManagement.*;

/**
 * Created by Evdal on 16.03.2016.
 */


public class SubscriptionManagement extends Management{
    public SubscriptionManagement(){super();}

    // Defines the User Types
    public enum SubType {
        INACTIVE, ACTIVE;

        public int getValue() {
            return super.ordinal();
        }

        public static SubType valueOf(int subTypeNr) {
            for (SubType type : SubType.values()) {
                if (type.ordinal() == subTypeNr) {
                    return type;
                }
            }
            return null;
        }
    }

    public ArrayList<Object[]> getSubscriptions(){
        ArrayList<Object[]> out = new ArrayList<Object[]>();
        if(setUp()){
            try {
                ResultSet res = getScentence().executeQuery("SELECT subscription.sub_id, customer.name, " +
                        "subscription.date_from, subscription.date_to, subscription.sub_type FROM subscription, " +
                        "customer WHERE subscription.customer_id = customer.customer_id AND subscription.status = "+SubType.ACTIVE.getValue()
                +";");

                while (res.next()){
                    out.add(createObj(res));
                }

            }
            catch (Exception e){
                System.out.println("Issue with subscriptions.");
            }
            finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        return out;
    }
    public ArrayList<Object[]> getDeletedSubscriptions(){
        ArrayList<Object[]> out = new ArrayList<Object[]>();
        if(setUp()){
            try {
                ResultSet res = getScentence().executeQuery("SELECT subscription.sub_id, customer.name, " +
                        "subscription.date_from, subscription.date_to, subscription.sub_type FROM subscription, " +
                        "customer WHERE subscription.customer_ID = customer.customer_id AND "+SubType.INACTIVE.getValue()
                        +" = subscription.status;");

                while (res.next()){
                    out.add(createObj(res));
                }

            }
            catch (Exception e){
                System.out.println("Issue with subscriptions.");
            }
            finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        return out;
    }
    private Object[] createObj(ResultSet res)throws Exception{
        Object[] obj = new Object[5];

        obj[0] = res.getInt("sub_id");
        obj[1] = res.getString("name");
        obj[2] = res.getString("date_to");
        obj[3] = res.getString("date_from");
        obj[4] = res.getInt("sub_type"); // frequency.

        return obj;
    }
    public int containsSubOrder(int subId){
        int orders = 0;
        if(setUp()){
            try{
                ResultSet res = getScentence().executeQuery("SELECT count(order_id) as orders from `order` WHERE " +
                        "sub_id = " + subId + ";");
                if(res.next()){
                    orders = res.getInt("orders");
                }
            }
            catch (Exception e){
                System.err.println("Issue with subscriptions.");

            }
            finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        return 0;
    }
    public int createSubscription(int custID, String dateFrom, String dateTo, int frequency){
        int subid = -1;
        if(setUp()){
            try {
                getScentence().executeQuery("START TRANSACTION");
                PreparedStatement prep = getConnection().prepareStatement("INSERT INTO subscription VALUES (DEFAULT,?,?,?,?,?)");
                prep.setInt(1, custID);
                prep.setString(2, dateTo);
                prep.setString(3, dateFrom);
                prep.setInt(4, 1); //active
                prep.setInt(5, frequency);
                prep.executeUpdate();
                ResultSet res = getScentence().executeQuery("SELECT LAST_INSERT_ID() as id;");
                if(res.next()){
                    subid = res.getInt("id");
                }

            } catch (SQLException e) {
                System.err.println("Issue with creating subscription");
                try {
                    getScentence().executeQuery("ROLLBACK");
                } catch (SQLException e1) {
                    System.err.println("Issue with rolling back subscription transaction.");
                }
                return -1;
            } finally {
                try {
                    getScentence().executeQuery("COMMIT;");
                } catch (SQLException e) {
                }
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());

            }

        }
        return subid;
    }
    public boolean increaseSubLength(int subId, String dateTo){
        int out = -1;
        if(setUp()){
            String dateFrom = null;
            int frequency = 0;
            int custId = 0;
            try {
                ResultSet res = getScentence().executeQuery("SELECT date_to, customer_id, sub_type FROM subscription WHERE sub_id = "+subId+";");
                if(res.next()) {
                    dateFrom = res.getString("date_to"); //Henter den gamle til_datoen TODO: test for duplicate orders!
                    frequency = res.getInt("sub_type");
                    custId = res.getInt("customer_id");

                }
                out = createSubscription(custId, dateFrom,dateTo, frequency);
            } catch (SQLException e) {
                System.err.println("Issue with getting data from subId");
                return false;
            } finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getScentence());
            }
        }
        return out > -1;

    }
    public boolean deleteSubscription(int subId){ //setter orderstatus på alle ordre med riktig sub_id etter dagens dato til inaktiv
        if(setUp()){
            try {
                int res = getScentence().executeUpdate("UPDATE `order` SET status = "+ OrdStatus.INACTIVE.getValue()+
                        " WHERE sub_id = "+subId+" AND `date` >= CURRENT_DATE;");
                if(res > 0){
                    getScentence().executeUpdate("UPDATE `order` SET status = "+ OrdStatus.INACTIVE.getValue()+
                            " WHERE sub_id = "+subId+" AND `date` >= CURRENT_DATE;");
                }
                else return false;
            }
            catch (Exception e){
                System.err.println("Issue with deleting subscription.");
                return false;
            }
            finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        return true;
    }
    public boolean changeRecipes(int subID, int prevRecipeID, int newRecipeID){
        int rowChanged = 0;
        if(setUp()){
            try{
                rowChanged = getScentence().executeUpdate("UPDATE order_recipe SET recipe_id  = " + newRecipeID+
                        " WHERE order_id IN(SELECT " + "order_id FROM `order` WHERE sub_id = "+subID+") AND " +
                        "order_recipe.recipe_id = "+prevRecipeID+";");

            }catch(Exception e){
                System.err.println("Issue with changing recipes.");
                return false;
            }
            finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        return rowChanged > 0;
    }

}
