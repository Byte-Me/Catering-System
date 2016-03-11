package Database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

import Encryption.*;
import org.apache.commons.dbutils.DbUtils;

public class UserManagement extends Management {


    public UserManagement() {
        super();
    }

    public boolean registerUser(String firstname, String lastname, String username,
                                String password, String email, String phone, int accessLevel) {
        Encryption enc = new Encryption();
        int rowChanged = 0; //
        if (super.setUp()) {
            try {
                String[] saltHash = enc.passEncoding(password);
                try {
                    ResultSet res = getScentence().executeQuery("SELECT username FROM user WHERE username = '" + username + "';");
                    if(res.next()) return false;
                }
                catch (Exception e){

                }
                rowChanged = getScentence().executeUpdate("INSERT INTO user VALUES(DEFAULT, '" + username +
                        "', '" + saltHash[0] + "', '" + saltHash[1] + "', '" + firstname + "', '" + lastname
                        + "', '" + phone + "', '" + email + "', " + accessLevel + ");");
            } catch (Exception e) {
                System.err.println("Issue with creating user.");
                //  e.printStackTrace();
                return false;
            } finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());

            }
        }
        if (rowChanged > 0) return true;
        return false;

    }

    public ArrayList<Object[]> userInfo() {

        ResultSet res = null;
        ArrayList<Object[]> out = new ArrayList<Object[]>();
        if (super.setUp()) {

            try {
                res = getScentence().executeQuery("select first_name, last_name, email, phone, username, access_level" +
                        " from user order by last_name;");
                while (res.next()) {
                    Object[] obj = new Object[6];
                    obj[0] = res.getString("first_name");
                    obj[1] = res.getString("last_name");
                    obj[2] = res.getString("email");
                    obj[3] = res.getString("phone");
                    obj[4] = res.getString("username");
                    obj[5] = res.getInt("access_level");
                    out.add(obj);
                }
            } catch (SQLException e) {
                System.err.println("Issue with executing SQL scentence.");
                return null;
            }
            return out;
        } else return null;

    }

    public boolean updateUserInfoFName(String username, String newData) {
        int rowChanged = 0;
        if (super.setUp()) {
            try {
                rowChanged = getScentence().executeUpdate("UPDATE user SET first_name = '" + newData + "' WHERE username = '" + username + "';");
            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");
                return false;

            } finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());


            }
        }
        if(rowChanged > 0) return true;
        return false;
    }
    public boolean updateUserInfoLName(String username, String newData) {
        int rowChanged = 0;
        if (super.setUp()) {
            try {
                rowChanged = getScentence().executeUpdate("UPDATE user SET last_name = '" + newData + "' WHERE username = '" + username + "';");
            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");
                return false;

            } finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());


            }
        }
        if(rowChanged > 0) return true;
        return false;
    }
    public boolean updateUserInfoUsername(String username, String newData) {
        int rowChanged = 0;
        if (super.setUp()) {
            try {
                rowChanged = getScentence().executeUpdate("UPDATE user SET username = '" + newData + "' WHERE username = '" + username + "';");
            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");
                return false;

            } finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());


            }
        }
        if(rowChanged > 0) return true;
        return false;
    }
    public boolean updateUserInfoPhone(String username, String newData) {
        int rowChanged = 0;
        if (super.setUp()) {
            try {
                rowChanged = getScentence().executeUpdate("UPDATE user SET phone = '" + newData + "' WHERE username = '" + username + "';");
            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");
                return false;

            } finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());


            }
        }
        if(rowChanged > 0) return true;
        return false;
    }
    public boolean updateUserInfoEmail(String username, String newData) {
        int rowChanged = 0;
        if (super.setUp()) {
            try {
                rowChanged = getScentence().executeUpdate("UPDATE user SET email = '" + newData + "' WHERE username = '" + username + "';");
            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");
                return false;

            } finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());


            }
        }
        if(rowChanged > 0) return true;
        return false;
    }
    public boolean updateUserInfoAccessLevel(String username, int newData) {
        int rowChanged = 0;
        if (super.setUp()) {
            try {
                rowChanged = getScentence().executeUpdate("UPDATE user SET access_level = '" + newData + "' WHERE username = '" + username + "';");
            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");
                return false;

            } finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());


            }
        }
        if(rowChanged > 0) return true;
        return false;
    }
    public ArrayList<Object[]> userSearch(String searchTerm){
        ResultSet res = null;
        ArrayList<Object[]> out = new ArrayList<Object[]>();
        if(setUp()) {
            try {
                res = getScentence().executeQuery("SELECT username, first_name, last_name, phone, email, access_level" +
                        " FROM user WHERE username LIKE '%" + searchTerm + "%' OR first_name LIKE '%"
                        + searchTerm + "%' OR last_name LIKE '%" + searchTerm + "%' OR phone LIKE '%" + searchTerm +
                        "%' OR email LIKE '%" + searchTerm + "%' OR access_level LIKE '%" + searchTerm + "%' ORDER BY last_name;");
                //System.out.println("Hei");
                while (res.next()) {
                    Object[] obj = new Object[6];
                    obj[0] = res.getString("first_name");
                    obj[1] = res.getString("last_name");
                    obj[2] = res.getString("email");
                    obj[3] = res.getString("phone");
                    obj[4] = res.getString("username");
                    obj[5] = res.getString("access_level");
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
    }/*
    public boolean deleteUser(){
        ResultSet res = null;
        if(setUp()){
            try{
                res = getScentence().executeUpdate("UPDATE user SET active = ")
            }
            catch (Exception e){
                System.err.println("Issue with deleting user.");
            }
        }
    }*/

}

    /*
    TODO: Delete user.

     */


/*
    public static void main(String[] args){
        UserManagement test = new UserManagement();
        if(test.connected()) {
            if (test.registerUser("Even", "pass", "ed@do.set", 2)) System.out.println("Success!");
        }
    }
}
*/