package Database;

import org.apache.commons.dbutils.DbUtils;

import java.sql.Connection;
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
        PRIVATE, CORPORATION;

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
    public enum CustStatus {
        INACTIVE, ACTIVE;

        public int getValue() {
            return super.ordinal();
        }

        public static CustStatus valueOf(int custStatusNr) {
            for (CustStatus status : CustStatus.values()) {
                if (status.ordinal() == custStatusNr) {
                    return status;
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

    Connection conn = null;
    PreparedStatement prep = null;
    ResultSet res = null;

    public ArrayList<Object[]> getCustomers(){
        if(setUp()){
            ArrayList<Object[]> out = new ArrayList<>();
            try{
                conn = getConnection();
                prep = conn.prepareStatement("SELECT * FROM customer WHERE status = 1;");//Status 1 for aktiv og 0 for inaktiv
                res = prep.executeQuery();
                while(res.next()) {
                    Object[] obj = new Object[6];
                    obj[0] = res.getString("name");
                    obj[1] = res.getString("email");
                    obj[2] = res.getString("phone");
                    obj[3] = res.getString("adress");
                    obj[4] = CustType.valueOf(res.getInt("cust_type"));
                    obj[5] = res.getInt("customer_id");

                    out.add(obj);
                }
            }
            catch (Exception e){
                System.err.println("Issue with getting customers.");
                return null;
            }
            finally{
                finallyStatement(res, prep);
            }
            return out;
        }
        else return null;
    }

    public ArrayList<Object[]> getDeletedCustomers(){
        if(setUp()){
            ArrayList<Object[]> out = new ArrayList<>();
            try{
                conn = getConnection();
                prep = conn.prepareStatement("SELECT * FROM customer WHERE status = 0;"); //Status 1 for aktiv og 0 for inaktiv
                res = prep.executeQuery();
                while(res.next()) {
                    Object[] obj = new Object[5];
                    obj[0] = res.getString("name");
                    obj[1] = res.getString("email");
                    obj[2] = res.getString("phone");
                    obj[3] = res.getString("adress");
                    obj[4] = CustType.valueOf(res.getInt("cust_type"));

                    out.add(obj);
                }
            }
            catch (Exception e){
                System.err.println("Issue with getting customers.");
                return null;
            }
            finally{
                finallyStatement(res, prep);
            }
            return out;
        }
        else return null;
    }

    public ArrayList<Object[]> customerSearch(String searchTerm){
        ArrayList<Object[]> out = new ArrayList<>();
        if(setUp()) {
            try {
                conn = getConnection();
                prep = conn.prepareStatement("SELECT * FROM customer WHERE name LIKE ? OR email LIKE ? OR phone LIKE ? OR adress LIKE ? AND status = 1 ORDER BY name;");
                prep.setString(1, "%" + searchTerm + "%");
                prep.setString(2, "%" + searchTerm + "%");
                prep.setString(3, "%" + searchTerm + "%");
                prep.setString(4, "%" + searchTerm + "%");
                res = prep.executeQuery();

                while (res.next()){
                    Object[] obj = new Object[5];
                    obj[0] = res.getString("name");
                    obj[1] = res.getString("email");
                    obj[2] = res.getString("phone");
                    obj[3] = res.getString("adress");
                    obj[4] = res.getInt("cust_type");
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
    private boolean addCustomer(String name, String email, String phone, String adress, int custType) {
        if (setUp()) {
            int rowChanged = 0;
            try{
                conn = getConnection();
                prep = conn.prepareStatement("SELECT name FROM customer WHERE email = ?;");
                prep.setString(1, email);
                res = prep.executeQuery();

                if(res.next()) {//finds if customer already in database
                    prep = conn.prepareStatement("SELECT cust_type FROM customer WHERE email = ?;");
                    prep.setString(1, email);
                    res = prep.executeQuery();
                    if(res.next()) {
                        //find status of customer, if status = -1, make customer active, else return false.
                        //Customer already exists.
                        if (res.getInt("status") == CustStatus.INACTIVE.getValue()) {
                            try {
                                conn.setAutoCommit(false);
                                prep = conn.prepareStatement("UPDATE customer SET cust_type = ? WHERE email = ?;");
                                prep.setInt(1, custType);
                                prep.setString(2, email);
                                rowChanged = prep.executeUpdate();
                            }catch (SQLException sqle){
                                System.err.println("ERROR 201: Update customer type failed");
                                rollbackStatement();
                                return false;
                            }finally {
                                conn.commit();
                                conn.setAutoCommit(true);
                            }
                            return true;
                        }
                        else return false;
                    }
                }else { //If not in database, create customer.
                    try {
                        conn.setAutoCommit(false);
                        prep = conn.prepareStatement("INSERT INTO customer VALUES(DEFAULT, ?, ?, ?, ?, ?, ?);");
                        prep.setString(1, name);
                        prep.setString(2, email);
                        prep.setString(3, phone);
                        prep.setString(4, adress);
                        prep.setInt(5, custType);
                        prep.setInt(6, CustStatus.ACTIVE.getValue());
                        rowChanged = prep.executeUpdate();
                    }catch (SQLException sqle){
                        System.err.println("ERROR 202: Failed to create customer");
                        rollbackStatement();
                        return false;
                    }finally {
                        conn.commit();
                        conn.setAutoCommit(true);
                    }
                }
            }
            catch (Exception e){
                System.err.println("Issue with adding customer.");
                rollbackStatement();
                return false;
            }
            finally{
                finallyStatement(res, prep);
            }
            return rowChanged > 0;
        }
        else return false;
    }

    public boolean addCustomerCompany(String name, String email, String phone, String streetAdress, String postCode, String city){
        String adress = adressFormatter(city, postCode, streetAdress);
        return addCustomer(name, email, phone, adress, CustType.CORPORATION.getValue());
    }

    public boolean addCustomerPerson(String firstname, String lastname, String email, String phone,
                                  String streetAdress, String postCode, String city){

        String adress = adressFormatter(city, postCode, streetAdress);
        String name = nameFormatter(firstname, lastname);

        return addCustomer(name, email, phone, adress, CustType.PRIVATE.getValue());
    }

    public boolean updateCustomerName(String email, String fName, String lName) {
        String newData = nameFormatter(fName,lName);
        return updateCustomerName(email, newData);
    }

    public boolean updateCustomerName(String email, String newData) {
        int rowChanged = 0;
        if (setUp()) {
            try {
                conn = getConnection();
                conn.setAutoCommit(false);
                prep = conn.prepareStatement("UPDATE customer SET `name` = ? WHERE email = ?;");
                prep.setString(1, newData);
                prep.setString(2, email);
                rowChanged = prep.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");
                rollbackStatement();
                return false;
            } finally {
                finallyStatement(res, prep);
            }
        }
        return rowChanged > 0;
    }
    public boolean updateCustomerEmail(String email, String newData) {
        int rowChanged = 0;
        if (setUp()) {
            try {
                conn = getConnection();
                conn.setAutoCommit(false);
                prep = conn.prepareStatement("UPDATE customer SET email = ? WHERE email = ?;");
                prep.setString(1, newData);
                prep.setString(2, email);
                rowChanged = prep.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");
                rollbackStatement();
                return false;

            } finally {
                finallyStatement(res, prep);
            }
        }
        return rowChanged > 0;
    }
    public boolean updateCustomerPhone(String email, String newData) {
        int rowChanged = 0;
        if (setUp()) {
            try {
                conn = getConnection();
                conn.setAutoCommit(false);
                prep = conn.prepareStatement("UPDATE customer SET phone = ? WHERE email = ?;");
                prep.setString(1, newData);
                prep.setString(2, email);
                rowChanged = prep.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");
                rollbackStatement();
                return false;

            } finally {
                finallyStatement(res, prep);
            }
        }
        return rowChanged > 0;
    }
    public Object[] getSingleCustomerInfo(String email){
        Object[] out =  new Object[6];
        if (setUp()) {
            try {
                conn = getConnection();
                prep = conn.prepareStatement("SELECT name, email, phone, adress, status, customer_id FROM customer WHERE email = ?;");
                prep.setString(1, email);
                res = prep.executeQuery();
                if(res.next()){
                    out[0] = res.getString("name");
                    out[1] = res.getString("email");
                    out[2] = res.getString("phone");
                    out[3] = res.getString("adress");
                    out[4] = res.getInt("status");
                    out[5] = res.getInt("customer_id");
                }
            } catch (SQLException e) {
                System.err.println("Issue with getting customer info.");
                return null;

            } finally {
                finallyStatement(res, prep);
            }
        }
        return out;
    }
    public boolean updateCustomerAdress(String email, String street, String postCode, String city) {
        int rowChanged = 0;
        String newData = adressFormatter(city, postCode,street);
        if (setUp()) {
            try {
                conn = getConnection();
                conn.setAutoCommit(false);
                prep = conn.prepareStatement("UPDATE customer SET adress = ? WHERE email = ?;");
                prep.setString(1, newData);
                prep.setString(2, email);
                rowChanged = prep.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");
                rollbackStatement();
                return false;

            } finally {
                finallyStatement(res, prep);
            }
        }
        return rowChanged > 0;
    }
    public boolean updateCustomerStatus(String email, int newData) {
        int rowChanged = 0;
        if (setUp()) {
            try {
                conn = getConnection();
                conn.setAutoCommit(false);
                prep = conn.prepareStatement("UPDATE customer SET status = ? WHERE email = ?;");
                prep.setInt(1, newData);
                prep.setString(2, email);
                rowChanged = prep.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");
                rollbackStatement();
                return false;

            } finally {
                finallyStatement(res, prep);
            }
        }
        return rowChanged > 0;
    }
    public boolean deleteCustomer(String email){
        return updateCustomerStatus(email, CustStatus.INACTIVE.getValue());
    }


}
