package Database;

import org.apache.commons.dbutils.DbUtils;
import org.omg.PortableInterceptor.INACTIVE;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Evdal on 07.03.2016.
 */
public class CustomerManagement extends Management{
    public CustomerManagement(){
        super();
    }

    public static enum CustStatus{
        INACTIVE(0),
        ACTIVE(1);

        private int value;

        private CustStatus(int value){
            this.value = value;
        }
        public int getValue(){
            return value;
        }

    }

    public ArrayList<Object[]> getCustomers(){
        if(setUp()){
            ArrayList<Object[]> out = new ArrayList<Object[]>();
            ResultSet res = null;
            try{
                res = getScentence().executeQuery("SELECT * FROM customer WHERE status = "+CustStatus.ACTIVE.getValue()+";"); //Status 1 for aktiv og 0 for inaktiv
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
                        "%' OR adress LIKE '%" + searchTerm + "%' AND status = "+CustStatus.ACTIVE.getValue()+" ORDER BY name;");

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
    private boolean addCustomer(String name, String email, String phone, String adress, int status) {
        if (setUp()) {
            int numb = 0;
            ResultSet res = null;
            try{
                getScentence().executeQuery("START TRANSACTION;");
                res = getScentence().executeQuery("SELECT name FROM customer WHERE email = '" + email + "';");
                if(res.next()) {
                    res = getScentence().executeQuery("SELECT status FROM customer WHERE email = '" + email + "';");

                    if(res.next()) {
                        if (res.getInt("status") == CustStatus.ACTIVE.getValue()) {
                            numb = getScentence().executeUpdate("UPDATE customer SET status = " + status + " WHERE email = '" + email + "';");
                            if (numb == 0) {
                                getScentence().executeQuery("COMMIT;");
                                return false;
                            }
                            else {
                                getScentence().executeQuery("COMMIT;");
                                return false;
                            }
                        }
                    }
                }else {
                    numb = getScentence().executeUpdate("INSERT INTO customer VALUES(DEFAULT, '" + name + "', '" + email + "', '" + phone +
                            "', '" + adress + "', DEFAULT);");
                }

                getScentence().executeQuery("COMMIT;");
            }
            catch (Exception e){
                System.err.println("Issue with adding customer.");
            }
            finally{
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
            if(numb > 0) return true;
            else return false;
        }
        else return false;
    }
    public boolean addCustomerCompany(String name, String email, String phone, String streetAdress, String postCode, String city){
        String adress = adressFormatter(city, postCode, streetAdress);
        if(addCustomer(name, email, phone, adress, 2)) return true;
        else return false;


    }
    public boolean addCustomerPerson(String firstname, String lastname, String email, String phone,
                                  String streetAdress, String postCode, String city){

        String adress = adressFormatter(city, postCode, streetAdress);
        String name = nameFormatter(firstname, lastname);
        if(addCustomer(name, email, phone, adress, CustStatus.ACTIVE.getValue())) return true;
        else return false;

    }
    public boolean deleteCustomer(String email){
        if(setUp()){
            int res = 0;
            try{
                res = getScentence().executeUpdate("UPDATE customer SET status = "+CustStatus.INACTIVE.getValue()+" WHERE email = '" + email + "';");
            }
            catch (Exception e){
                System.err.println("Issue with deleting customer");
            }
            finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
            if(res > 0) return true;
        }
        return false;
    }
    public boolean updateCustomerName(String email, String newData) {
        int rowChanged = 0;
        if (setUp()) {
            try {
                PreparedStatement prep = getConnection().prepareStatement("UPDATE customer SET `name` = ? WHERE email = ?;");
                prep.setString(1, newData);
                prep.setString(2, email);
                rowChanged = prep.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");
                return false;

            } finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());


            }
        }
        return rowChanged > 0;
    }
    public boolean updateCustomerEmail(String email, String newData) {
        int rowChanged = 0;
        if (setUp()) {
            try {
                PreparedStatement prep = getConnection().prepareStatement("UPDATE customer SET email = ? WHERE email = ?;");
                prep.setString(1, newData);
                prep.setString(2, email);
                rowChanged = prep.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");
                return false;

            } finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());


            }
        }
        return rowChanged > 0;
    }
    public boolean updateCustomerPhone(String email, String newData) {
        int rowChanged = 0;
        if (setUp()) {
            try {
                PreparedStatement prep = getConnection().prepareStatement("UPDATE customer SET phone = ? WHERE email = ?;");
                prep.setString(1, newData);
                prep.setString(2, email);
                rowChanged = prep.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");
                return false;

            } finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());


            }
        }
        return rowChanged > 0;
    }
    public boolean updateCustomerAdress(String email, String streetAdress, String postCode, String city) {
        String newData = adressFormatter(city, postCode, streetAdress);
        int rowChanged = 0;
        if (setUp()) {
            try {
                PreparedStatement prep = getConnection().prepareStatement("UPDATE customer SET adress = ? WHERE email = ?;");
                prep.setString(1, newData);
                prep.setString(2, email);
                rowChanged = prep.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");
                return false;

            } finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());


            }
        }
        return rowChanged > 0;
    }
    public boolean updateCustomerStatus(String email, int newData) {
        int rowChanged = 0;
        if (setUp()) {
            try {
                PreparedStatement prep = getConnection().prepareStatement("UPDATE customer SET status = ? WHERE email = ?;");
                prep.setInt(1, newData);
                prep.setString(2, email);
                rowChanged = prep.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");
                return false;

            } finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());


            }
        }
        return rowChanged > 0;
    }


}
