package Database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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
                res = getScentence().executeQuery("select first_name, last_name, email, phone, username, access_level from user;");
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
            } catch (SQLException e) {
                System.err.println("Issue with executing SQL scentence.");
                return null;
            }
        } else {
            return null;
        }
        return out;
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
}

    /*
    TODO:
        UserInfo: ArrayList med objectarrays for alle brukere. Firstname, lastname, email, phone, username, usertype
        Endre INSERT og SELECT setninger til de nye tabellene.

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