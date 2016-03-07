package Database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Evdal on 03.03.2016.
 *
 * Uses connection created in DatabaseConnection to
 * send statement to usertable.
 *
 */
public class UserLoginFromDatabase {
    private DatabaseConnection classConnect = new DatabaseConnection();
    private Connection connection;
    private Statement scentence = null;
    private final int passColumnNumber = 1; // NBNB skal endres.

    public static int NO_ACCESS = -1;
    public static int ADMIN_ACCESS = 0;
    public static int SALES_ACCESS = 1;
    public static int CHEF_ACCESS = 2;
    public static int DRIVER_ACCESS = 3;



    public UserLoginFromDatabase(){
        Connection connection = classConnect.getConnection();
        try {
            Statement scentence = connection.createStatement();
        }
        catch (SQLException SQLe){
            SQLe.getMessage();
        }

        // Check if connection is NULL!!! NB! NB!

    }

    /*
        Users are stored in their own table in the database.
        They have information such as username, password and
        access-level.

        login returns an int, which is their access-level, if -1 is
        returned, their password or username was wrong.

     */
    public int login(String user, String pass) throws SQLException {
        ResultSet res = null;
        if(scentence != null) {
            try {
                res = scentence.executeQuery("select username, password, access_level from users " +
                        "where username = '" + user + "';");
                if (pass.equals(res.getString(passColumnNumber))) { // denne må endres!!
                    return res.getInt("access_level");
                }
            }
            catch (SQLException e){
                return -1;
            }

        }
        return -1;
    }
}
