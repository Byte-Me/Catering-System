package Database;

import org.apache.commons.dbutils.DbUtils;

import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Created by Evdal on 07.03.2016.
 */
public class CustomerManagement extends Management{
    public CustomerManagement(){
        super();
    }
    public ArrayList<Object[]> getCustomers(){
        if(setUp()){
            ArrayList<Object[]> out = new ArrayList<Object[]>();
            ResultSet res = null;
            try{
                System.out.println("SELECT * " +
                        "FROM customer;");
                res = getScentence().executeQuery("SELECT * " +
                        "FROM customer;");  //WHERE status = 1 : Status 1 for akttiv og 0 for inaktiv
                while(res.next()) {
                    Object[] obj = new Object[4];
                    obj[0] = res.getString("last_name") + ", " + res.getString("first_name");
                    obj[1] = res.getString("email");
                    obj[2] = res.getString("phone");
                    obj[3] = res.getString("street") + " " + res.getString("street_number") + ", "
                            + res.getString("postal_code") + " " + res.getString("city");
                    out.add(obj);
                }

            }
            catch (Exception e){
                System.err.println("Issue with getting customers.");
                return null;
            }
            finally{
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getScentence());
            }
            return out;
        }
        else return null;
    }


}
