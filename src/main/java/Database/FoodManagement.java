package Database;

import org.apache.commons.dbutils.DbUtils;

import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Created by Evdal on 07.03.2016.
 */
public class FoodManagement extends Management{
    public FoodManagement(){
        super();
    }
    public ArrayList<Object[]> getIngredients(){
        ResultSet res = null;
        ArrayList<Object[]> out = new ArrayList<Object[]>();
        if(setUp()){
            try{

                res = getScentence().executeQuery("SELECT name, quantity, unit FROM grocery;");
                while (res.next()){
                    Object[] obj = new Object[3];
                    obj[0] = res.getString("name");
                    obj[1] = res.getInt("quantity");
                    obj[2] = res.getString("unit");
                    out.add(obj);
                }
            }
            /*
            2 = delivered
            1 = ready for delivery
            0 = active
            -1 = inactive

             */
            catch (Exception e){
                System.err.println("Issue with getting ingredients.");
                return null;
            }
            finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }

        }
        else return null;
        return out;
    }


}