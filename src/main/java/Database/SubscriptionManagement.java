package Database;

import org.apache.commons.dbutils.DbUtils;

import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Created by Evdal on 16.03.2016.
 */
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
}
