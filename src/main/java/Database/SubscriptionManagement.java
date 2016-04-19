package Database;

import org.apache.commons.dbutils.DbUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static Database.OrderManagement.OrdStatus;

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

    public ArrayList<Object[]> subscriptionSearch(String searchTerm){
        ResultSet res;
        ArrayList<Object[]> out = new ArrayList<>();
        if(setUp()) {
            try {
                PreparedStatement prep = getConnection().prepareStatement("SELECT subscription.sub_id, customer.name, " +
                                "subscription.date_from, subscription.date_to, subscription.sub_type" +
                        " FROM subscription, customer WHERE subscription.customer_id = customer.customer_id AND (" +
                        "subscription.sub_id LIKE ? OR customer.name LIKE ? OR subscription.date_from LIKE ? OR subscription.date_to " +
                        "LIKE ? OR subscription.sub_type LIKE ?) ORDER BY sub_id;");
                searchTerm = "%" + searchTerm + "%";
                prep.setString(1, searchTerm);
                prep.setString(2, searchTerm);
                prep.setString(3, searchTerm);
                prep.setString(4, searchTerm);
                prep.setString(5, searchTerm);
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
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }

            return out;
        }
        else return null;
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
                PreparedStatement prep = getConnection().prepareStatement("INSERT INTO subscription VALUES (DEFAULT,?,?,?,?,CURRENT_DATE,?);");
                prep.setInt(1, custID);
                prep.setString(2, dateTo);
                prep.setString(3, dateFrom);
                prep.setInt(4, 1); //active
                prep.setInt(5, frequency);
                System.out.println(prep.toString());
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
                getScentence().executeUpdate("UPDATE `order` SET status = "+ OrdStatus.INACTIVE.getValue()+
                        " WHERE sub_id = "+subId+" AND `date` >= CURRENT_DATE;");
                getScentence().executeUpdate("UPDATE `subscription` SET status = "+ OrdStatus.INACTIVE.getValue()+
                        " WHERE sub_id = "+subId+";");

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
    public boolean changeRecipes(int subID, int prevRecipeID, int newRecipeID){ //DENNE MÅ ENDRES TODO
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
    public Object[] getSubInfoFromId(int subId){ //DENNE MÅ ENDRES TODO
        Object[] out = new Object[4];
        if(setUp()){
            try{
                PreparedStatement prep = getConnection().prepareStatement(getSubInfoFromId);
                prep.setInt(1,subId);
                ResultSet res = prep.executeQuery();
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
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        return out;
    }
    public ArrayList<Object[]> getOrderInfoFromSub(int subId){ //DENNE MÅ ENDRES TODO
        ArrayList<Object[]> out = new ArrayList<>();
        if(setUp()){
            try{
                PreparedStatement prep = getConnection().prepareStatement(getOrderInfoFromSub);
                prep.setInt(1,subId);
                ResultSet res = prep.executeQuery();
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
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        return out;
    }
    public Object[] getRecipeInfoFromSubAndDate(int subId, String date){ //DENNE MÅ ENDRES TODO
        Object[] out = new Object[4];
        if(setUp()){
            try{
                PreparedStatement prep = getConnection().prepareStatement(getRecipeInfoFromSubAndDate);
                prep.setInt(1,subId);
                prep.setString(2,date);
                ResultSet res = prep.executeQuery();
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
                prep = getConnection().prepareStatement(getRecipeInfoFromSubAndDate2);
                prep.setInt(1, subId);
                prep.setString(2, date);
                prep.executeQuery();
                if(res.next()){
                    out[1] = res.getString("note");
                    out[2] = res.getString("time");
                }

            }catch(Exception e){
                System.err.println("Issue with getRecipeInfoFromSubAndDate.");
                return null;
            }
            finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        return out;
    }

}
