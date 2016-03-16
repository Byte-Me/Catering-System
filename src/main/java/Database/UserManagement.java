package Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

import Encryption.*;
import org.apache.commons.dbutils.DbUtils;

public class UserManagement extends Management {

    // Defines the User Types
    public enum UserType {
        ADMIN, SALE, DRIVER, CHEF;

        public int getValue() {
            return super.ordinal();
        }

        public static UserType valueOf(int userTypeNr) {
            for (UserType type : UserType.values()) {
                if (type.ordinal() == userTypeNr) {
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


    public UserManagement() {
        super();
    }

    public boolean registerUser(String firstname, String lastname, String username,
                                String password, String email, String phone, int accessLevel) {
        Encryption enc = new Encryption();
        int rowChanged = 0; //
        if (setUp()) {
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
        if (setUp()) {

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
                    // Convert access level from int to string
                    obj[5] = UserType.valueOf((Integer.parseInt(res.getString("access_level"))));
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
        if (setUp()) {
            try {
                PreparedStatement prep = getConnection().prepareStatement("UPDATE user SET first_name = ? WHERE username = ?;");
                prep.setString(1, newData);
                prep.setString(2, username);

                rowChanged = prep.executeUpdate();
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
        if (setUp()) {
            try {
                PreparedStatement prep = getConnection().prepareStatement("UPDATE user SET last_name = ? WHERE username = ?;");
                prep.setString(1, newData);
                prep.setString(2, username);
                rowChanged = prep.executeUpdate();
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
        if (setUp()) {
            try {
                PreparedStatement prep = getConnection().prepareStatement("UPDATE user SET username = ? WHERE username = ?;");
                prep.setString(1, newData);
                prep.setString(2, username);
                rowChanged = prep.executeUpdate();
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
        if (setUp()) {
            try {
                PreparedStatement prep = getConnection().prepareStatement("UPDATE user SET phone = ? WHERE username = ?;");
                prep.setString(1, newData);
                prep.setString(2, username);
                rowChanged = prep.executeUpdate();
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
        if (setUp()) {
            try {
                PreparedStatement prep = getConnection().prepareStatement("UPDATE user SET email = ? WHERE username = ?;");
                prep.setString(1, newData);
                prep.setString(2, username);
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
    public boolean updateUserInfoAccessLevel(String username, int newData) {
        int rowChanged = 0;
        if (setUp()) {
            try {
                PreparedStatement prep = getConnection().prepareStatement("UPDATE user SET access_level = ? WHERE username = ?;");
                prep.setInt(1, newData);
                prep.setString(2, username);
                rowChanged = prep.executeUpdate();
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
                PreparedStatement prep = getConnection().prepareStatement("SELECT username, first_name, last_name, phone, email, access_level" +
                        " FROM user WHERE username LIKE ? OR first_name LIKE ? OR last_name LIKE ? OR phone LIKE ? OR email LIKE ? OR access_level LIKE ? ORDER BY last_name;");
                searchTerm = "%" + searchTerm + "%";
                prep.setString(1, searchTerm);
                prep.setString(2, searchTerm);
                prep.setString(3, searchTerm);
                prep.setString(4, searchTerm);
                prep.setString(5, searchTerm);
                prep.setString(6, searchTerm);

                res = prep.executeQuery();

                while (res.next()) {
                    Object[] obj = new Object[6];
                    obj[0] = res.getString("first_name");
                    obj[1] = res.getString("last_name");
                    obj[2] = res.getString("email");
                    obj[3] = res.getString("phone");
                    obj[4] = res.getString("username");
                    // Convert access level from int to string
                    obj[5] = UserType.valueOf((Integer.parseInt(res.getString("access_level"))));
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