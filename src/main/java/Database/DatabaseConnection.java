package Database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Evdal on 03.03.2016.
 *
 * I've started doing some work on databaseconnection, much more needed.
 */
public class DatabaseConnection {

    private final String username = "evend";
    private final String password = "aHqRlVPk";
    private final String databasedriver = "com.mysql.jdbc.Driver";
    private final String databasename = "jdbc:mysql://mysql.stud.iie.ntnu.no:3306/" + username +
            "?user=" + username + "&password=" + password;
    private Connection connection = null;

    public DatabaseConnection() {

        try {
            Class.forName(databasedriver);
            connection = DriverManager.getConnection(databasename);
        } catch (ClassNotFoundException cnfe) {
            System.err.println("Issue with database driver.");
            //cnfe.printStackTrace();
        } catch (SQLException SQLe) {
            System.err.println("Issue with connecting to database.");
        }

    }
    public Connection getConnection(){
        return connection;
    }


}

