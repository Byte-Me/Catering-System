package Database;

import org.apache.commons.dbutils.DbUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static Database.OrderManagement.OrderType;

/**
 * Created by Evdal on 16.03.2016.
 */


public class SubscriptionManagement extends Management{

    private final String getSubInfoFromId = "SELECT customer.name, date_to, date_from, sub_type FROM subscription, customer WHERE sub_id = ?" +
            " AND subscription.customer_id = customer.customer_id;";
    private final String getOrderInfoFromSub = "SELECT note, `time`, `date` FROM `order` WHERE sub_id = ?;";
    private final String getRecipeInfoFromSubAndDate = "SELECT recipe.name, order_recipe.portions FROM order_recipe, recipe, `order` WHERE" +
            " `order`.sub_id = ? AND `order`.`date` = ? AND order_recipe.recipe_id = recipe.recipe_id AND `order`.order_id = order_recipe.order_id;";
    private final String getRecipeInfoFromSubAndDate2 = "SELECT note, `time` FROM `order` WHERE" +
            " sub_id = ? AND `date` = ?;";
    private final String deleteSubscription1 = "DELETE FROM subscription WHERE sub_id = ?";
    private final String deleteSubscription2 = "DELETE FROM `order` WHERE sub_id = ?"; //resten er on cascade

    Connection conn = null;
    PreparedStatement prep = null;
    ResultSet res = null;

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
        ArrayList<Object[]> out = new ArrayList<>();
        if(setUp()){
            try {
                conn = getConnection();
                prep = conn.prepareStatement("SELECT subscription.sub_id, customer.name, " +
                        "subscription.date_from, subscription.date_to, subscription.sub_type FROM subscription, " +
                        "customer WHERE subscription.customer_id = customer.customer_id AND subscription.status = ?;");
                prep.setInt(1, SubType.ACTIVE.getValue());
                res = prep.executeQuery();
                while (res.next()){
                    out.add(createObj(res));
                }
            }
            catch (Exception e){
                System.err.println("Issue with subscriptions.");
                return null;
            }
            finally {
                finallyStatement(res,prep);
            }
        }
        return out;
    }
    public ArrayList<Object[]> getDeletedSubscriptions(){
        ArrayList<Object[]> out = new ArrayList<Object[]>();
        if(setUp()){
            try {
                conn = getConnection();
                prep = conn.prepareStatement("SELECT subscription.sub_id, customer.name, " +
                        "subscription.date_from, subscription.date_to, subscription.sub_type FROM subscription, " +
                        "customer WHERE subscription.customer_ID = customer.customer_id AND ? = subscription.status;");
                prep.setInt(1, SubType.INACTIVE.getValue());
                res = prep.executeQuery();
                while (res.next()){
                    out.add(createObj(res));
                }
            }
            catch (Exception e){
                System.err.println("Issue with subscriptions.");
                return null;
            }
            finally {
                finallyStatement(res,prep);
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

    public ArrayList<Object[]> subscriptionSearch(String searchTerm){
        ArrayList<Object[]> out = new ArrayList<>();
        if(setUp()) {
            try {
                conn = getConnection();
                prep = conn.prepareStatement("SELECT subscription.sub_id, customer.name, " +
                                "subscription.date_from, subscription.date_to, subscription.sub_type" +
                        " FROM subscription, customer WHERE subscription.customer_id = customer.customer_id AND (" +
                        "subscription.sub_id LIKE ? OR customer.name LIKE ? OR subscription.date_from LIKE ? OR subscription.date_to " +
                        "LIKE ? OR subscription.sub_type LIKE ?) ORDER BY sub_id;");
                prep.setString(1, "%" + searchTerm + "%");
                prep.setString(2, "%" + searchTerm + "%");
                prep.setString(3, "%" + searchTerm + "%");
                prep.setString(4, "%" + searchTerm + "%");
                prep.setString(5, "%" + searchTerm + "%");
                res = prep.executeQuery();

                while (res.next()) {
                    Object[] obj = new Object[5];
                    obj[0] = res.getInt("sub_id");
                    obj[1] = res.getString("name");
                    obj[2] = res.getString("date_to");
                    obj[3] = res.getString("date_from");
                    obj[4] = res.getInt("sub_type");
                    out.add(obj);
                }
            } catch (Exception e) {
                System.err.println("Issue with search.");
                return null;
            } finally {
                finallyStatement(res, prep);
            }
            return out;
        }
        else return null;
    }

    public int containsSubOrder(int subId){
        int orders = 0;
        if(setUp()){
            try{
                conn = getConnection();
                prep = conn.prepareStatement("SELECT count(order_id) as orders from `order` WHERE sub_id = ?;");
                prep.setInt(1, subId);
                res = prep.executeQuery();
                if(res.next()){
                    orders = res.getInt("orders");
                }
            }
            catch (Exception e){
                System.err.println("Issue with subscriptions.");
            }
            finally {
                finallyStatement(res,prep);

            }
        }
        return orders;
    }

    public int createSubscription(int custID, String dateFrom, String dateTo, int frequency, int subId){ //subid = -1 if not exists
        int subid = -1;
        if(setUp()){
            try {
                conn = getConnection();
                conn.setAutoCommit(false);
                if(subid >= 0){
                    deleteSubscription(subId);
                }
                prep = conn.prepareStatement("INSERT INTO subscription VALUES (DEFAULT,?,?,?,?,CURRENT_DATE,?);");
                prep.setInt(1, custID);
                prep.setString(2, dateTo);
                prep.setString(3, dateFrom);
                prep.setInt(4, 1); //active
                prep.setInt(5, frequency);
                prep.executeUpdate();

                prep = conn.prepareStatement("SELECT LAST_INSERT_ID() as id;");
                res = prep.executeQuery();
                if(res.next()){
                    subid = res.getInt("id");
                }

            } catch (SQLException e) {
                System.err.println("Issue with creating subscription");
                rollbackStatement();
                return -1;
            } finally {
                finallyStatement(res, prep);
            }
        }
        return subid;
    }

    public Object[] getSubInfoFromId(int subId){
        Object[] out = new Object[4];
        if(setUp()){
            try{
                conn = getConnection();
                prep = conn.prepareStatement(getSubInfoFromId);
                prep.setInt(1,subId);
                res = prep.executeQuery();
                if (res.next()){
                    out[0] = res.getString("name");
                    out[1] = res.getString("date_from");
                    out[2] = res.getString("date_to");
                    out[3] = res.getInt("sub_type");
                }
            }catch(Exception e){
                System.err.println("Issue with getSubInfoFromId.");
                return null;
            }
            finally {
                finallyStatement(res,prep);
            }
        }
        return out;
    }
    public ArrayList<Object[]> getOrderInfoFromSub(int subId){ //DENNE MÅ ENDRES TODO
        ArrayList<Object[]> out = new ArrayList<>();
        if(setUp()){
            try{
                conn = getConnection();
                prep = conn.prepareStatement(getOrderInfoFromSub);
                prep.setInt(1,subId);
                res = prep.executeQuery();
                while (res.next()){
                    Object[] obj = new Object[4];
                    obj[0] = res.getString("time");
                    obj[1] = res.getString("note");
                    obj[2] = res.getDate("date");
                    out.add(obj);
                }
            }catch(Exception e){
                System.err.println("Issue with getOrderInfoFromSub.");
                return null;
            }
            finally {
                finallyStatement(res,prep);
            }
        }
        return out;
    }
    public Object[] getRecipeInfoFromSubAndDate(int subId, String date){
        Object[] out = new Object[4];
        if(setUp()){
            try{
                conn = getConnection();
                prep = conn.prepareStatement(getRecipeInfoFromSubAndDate);
                prep.setInt(1,subId);
                prep.setString(2,date);
                res = prep.executeQuery();
                ArrayList<Object[]> recipeTableInfo = new ArrayList<>();

                //find recipe info
                while (res.next()){
                    Object[] obj = new Object[2];
                    obj[0] = res.getString("name");
                    obj[1] = res.getString("portions");
                    recipeTableInfo.add(obj);
                }
                out[0] = recipeTableInfo;
                //find specific day info
                prep = conn.prepareStatement(getRecipeInfoFromSubAndDate2);
                prep.setInt(1, subId);
                prep.setString(2, date);
                res = prep.executeQuery();
                if(res.next()){
                    out[1] = res.getString("note");
                    out[2] = res.getString("time");
                }

            }catch(Exception e){
                System.err.println("Issue with getRecipeInfoFromSubAndDate.");
                return null;
            }
            finally {
                finallyStatement(res,prep);
            }
        }
        return out;
    }
    public boolean deleteSubscription(int subId){
        if(setUp()){
            try{
                conn = getConnection();
                conn.setAutoCommit(false);
                //sletter subscriptionen
                prep = getConnection().prepareStatement(deleteSubscription1);
                prep.setInt(1,subId);
                prep.executeUpdate();
                //sletter orders koblet til sub
                prep = getConnection().prepareStatement(deleteSubscription2);
                prep.setInt(1,subId);
                prep.executeUpdate();


            }catch(Exception e){
                System.err.println("Issue with getRecipeInfoFromSubAndDate.");
                rollbackStatement();
                return false;
            }
            finally {
                finallyStatement(res, prep);
            }
        }
        return true;

    }

}
