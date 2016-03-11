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
                res = getScentence().executeQuery("SELECT * FROM customer WHERE status = 1"); //Status 1 for aktiv og 0 for inaktiv
                while(res.next()) {
                    Object[] obj = new Object[4];
                    obj[0] = res.getString("name");
                    obj[1] = res.getString("email");
                    obj[2] = res.getString("phone");
                    obj[3] = res.getString("adress");
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
    public ArrayList<Object[]> customerSearch(String searchTerm){
        ResultSet res = null;
        ArrayList<Object[]> out = new ArrayList<Object[]>();
        if(setUp()) {
            try {
                res = getScentence().executeQuery("SELECT * FROM customer WHERE name LIKE '%" + searchTerm + "%' OR email LIKE '%" +
                        searchTerm + "%' OR phone LIKE '%" + searchTerm +
                        "%' OR adress LIKE '%" + searchTerm + "%' AND status = 1 ORDER BY name;");

                while (res.next()){
                    Object[] obj = new Object[4];
                    obj[0] = res.getString("name");
                    obj[1] = res.getString("email");
                    obj[2] = res.getString("phone");
                    obj[3] = res.getString("adress");
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
    private boolean addCustomer(String name, String email, String phone, String adress) {
        if (setUp()) {
            int res = 0;
            try{

                res = getScentence().executeUpdate("INSERT INTO customer VALUES(DEFAULT, '" + name + "', '" + email + "', '" + phone +
                        "', '" + adress + "', DEFAULT);");
                System.out.println("Etter");

            }
            catch (Exception e){
                System.err.println("Issue with adding customer.");
            }
            finally{
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
            if(res > 0) return true;
            else return false;
        }
        else return false;
    }
    public boolean addCustomerCompany(String name, String email, String phone, String streetAdress, String postCode, String city){
        String adress = adressFormatter(streetAdress, postCode, city);
        if(addCustomer(name, email, phone, adress)) return true;
        else return false;


    }
    public boolean addCustomerPerson(String firstname, String lastname, String email, String phone,
                                  String streetAdress, String postCode, String city){

        String adress = adressFormatter(streetAdress, postCode, city);
        String name = nameFormatter(firstname, lastname);
        if(addCustomer(name, email, phone, adress)) return true;
        else return false;

    }


}
