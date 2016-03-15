package Database;


import java.sql.*;

import Encryption.*;
import org.apache.commons.dbutils.DbUtils;

/**
 * Created by Evdal on 03.03.2016.
 *
 * Uses connection created in DatabaseConnection to
 * send statement to usertable.
 *
 */


public class LoginManagement extends Management{

    public static final int NO_ACCESS = -1;
    public static final int ADMIN_ACCESS = 0;
    public static final int SALES_ACCESS = 1;
    public static final int CHEF_ACCESS = 2;
    public static final int DRIVER_ACCESS = 3;


    public LoginManagement(){
        super();
    }

    /*
        Users are stored in their own table in the database.
        They have information such as username, password(Hash and salt) as well as
        access-level.

        login returns an int, which is their access-level, if -1 is
        returned, their password or username was wrong.

     */
    public int login(String user, String pass){


        //Testbruker:

        //username = bruker
        //password = password

        //username = Even
        //password = pass

        ResultSet res = null;
        if(setUp()) {
            try {
                PreparedStatement prep = getConnection().prepareStatement("select username, hash, salt, access_level from user " +
                        "where username = ?;");
                prep.setString(1, user);
                res = prep.executeQuery();
                if (res.next()) {
                    Encryption encrypt = new Encryption();
                    if (encrypt.passDecoding(pass, res.getString("hash"), res.getString("salt"))) {
                        return res.getInt("access_level");
                    }
                }
            } catch (Exception e) {
                //     e.printStackTrace();
                System.err.println("Issue with SQL connection.");
                return -1;
            } finally {
                super.closeConnection();
            }
        }
        return -1;
    }

}
