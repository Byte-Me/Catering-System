package Database;

import org.apache.commons.dbutils.DbUtils;

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

    // Defines the Customer Types
    public enum CustType {
        INACTIVE, PERSON, CORPORATION;

        public int getValue() {
            return super.ordinal();
        }

        public static CustType valueOf(int custTypeNr) {
            for (CustType type : CustType.values()) {
                if (type.ordinal() == custTypeNr) {
                    return type;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            String constName = super.toString();
            return constName.substring(0,1) + constName.substring(1).toLowerCase();
        }

    }

    public ArrayList<Object[]> getCustomers(){
        if(setUp()){
            ArrayList<Object[]> out = new ArrayList<Object[]>();
            ResultSet res;
            try{
                res = getScentence().executeQuery("SELECT * FROM customer WHERE status > "+CustType.INACTIVE.getValue()+";"); //Status 1 for aktiv og 0 for inaktiv
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

    public ArrayList<Object[]> getDeletedCustomers(){
        if(setUp()){
            ArrayList<Object[]> out = new ArrayList<Object[]>();
            ResultSet res = null;
            try{
                res = getScentence().executeQuery("SELECT * FROM customer WHERE status = "+CustType.INACTIVE.getValue()+";"); //Status 1 for aktiv og 0 for inaktiv
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
                        "%' OR adress LIKE '%" + searchTerm + "%' AND status >= 0 ORDER BY name;");

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
                if(res.next()) {//finds if customer already in database
                    res = getScentence().executeQuery("SELECT status FROM customer WHERE email = '" + email + "';");

                    if(res.next()) {
                        //find status of customer, if status = -1, make customer active, else return false.
                        //Customer already exists.
                        if (res.getInt("status") < 0) {
                            numb = getScentence().executeUpdate("UPDATE customer SET status = " + status + " WHERE email = '" + email + "';");
                            getScentence().executeQuery("COMMIT;");
                            return true;
                        }
                        else{
                            getScentence().executeQuery("COMMIT;");
                            return false;
                        }
                    }
                }else { //If not in database, create customer.
                    numb = getScentence().executeUpdate("INSERT INTO customer VALUES(DEFAULT, '" + name + "', '" + email + "', '" + phone +
                            "', '" + adress + "', "+status+");");
                }

                getScentence().executeQuery("COMMIT;");
            }
            catch (Exception e){
                System.err.println("Issue with adding customer.");
                return false;
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
        if(addCustomer(name, email, phone, adress, CustType.CORPORATION.getValue())) return true;
        else return false;


    }
    public boolean addCustomerPerson(String firstname, String lastname, String email, String phone,
                                  String streetAdress, String postCode, String city){

        String adress = adressFormatter(city, postCode, streetAdress);
        String name = nameFormatter(firstname, lastname);
        if(addCustomer(name, email, phone, adress, CustType.PERSON.getValue())) return true;
        else return false;

    }
    public boolean updateCustomerName(String email, String fName, String lName) {
        String newData = nameFormatter(fName,lName);
        if(updateCustomerName(email, newData)) return true;
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
    public Object[] getSingleCustomerInfo(String email){
        Object[] out =  new Object[5];
        if (setUp()) {
            try {
                ResultSet res = getScentence().executeQuery("SELECT name, email, phone, adress, status FROM customer WHERE email = '"
                        +email+"';");
                if(res.next()){
                    out[0] = res.getString("name");
                    out[1] = res.getString("email");
                    out[2] = res.getString("phone");
                    out[3] = res.getString("adress");
                    out[4] = res.getInt("status");
                }
            } catch (SQLException e) {
                System.err.println("Issue with getting customer info.");
                return null;

            } finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());

            }
        }
        return out;
    }
    public boolean updateCustomerAdress(String email, String street, String postCode, String city) {
        int rowChanged = 0;
        String newData = adressFormatter(city, postCode,street);
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
    public boolean deleteCustomer(String email){
        return updateCustomerStatus(email, CustType.INACTIVE.getValue());
    }


}
