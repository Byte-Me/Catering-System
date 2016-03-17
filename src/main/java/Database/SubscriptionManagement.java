package Database;

import org.apache.commons.dbutils.DbUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Evdal on 16.03.2016.
 */

        //TODO: create enum with subscription_type, 1 = weekly, 2 = monthly;

public class SubscriptionManagement extends Management{
    public SubscriptionManagement(){super();}

    public ArrayList<Object[]> getSubscriptions(){
        ArrayList<Object[]> out = new ArrayList<Object[]>();
        if(setUp()){
            try {
                ResultSet res = getScentence().executeQuery("SELECT subscription.sub_id, customer.name, " +
                        "subscription.date_from, subscription.date_to, subscription.sub_type FROM subscription, " +
                        "customer WHERE subscription.customer_ID = customer.customer_id");

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
        obj[1] = res.getString("customer_name");
        obj[2] = res.getString("date_to");
        obj[3] = res.getString("date_from");
        obj[4] = res.getInt("sub_type"); // 0 = weekly, 1 = monthly.
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
    public int createSubscription(int custID, String dateFrom, String dateTo, int type){ //type = 1 = weekly, type = 2 = monthly
        int subid = -1;
        if(setUp()){
            try {
                getScentence().executeQuery("START TRANSACTION");
                PreparedStatement prep = getConnection().prepareStatement("INSERT INTO subscription VALUES (DEFAULT,?,?,?,?)");
                prep.setInt(1, custID);
                prep.setString(2, dateTo);
                prep.setString(3, dateFrom);
                prep.setInt(4, type);
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


}
