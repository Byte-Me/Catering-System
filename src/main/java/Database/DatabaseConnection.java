package Database;
import java.sql.*;

/**
 * Created by Evdal on 03.03.2016.
 *
 * I've started doing some work on databaseconnection, much more needed.
 */
public class DatabaseConnection {

    private final String username = "evend";  //
    private final String password = "aHqRlVPk";
    private final String databasedriver = "com.mysql.jdbc.Driver";
    private final String databasename = "jdbc:mysql://mysql.stud.iie.ntnu.no:3306/" + username +
            "?user=" + username + "&password=" + password;
    private final Connection connection = null;

    public DatabaseConnection() {

        try {
            Connection connection = DriverManager.getConnection(databasename);
        } catch (SQLException SQLe) {
            SQLe.getMessage();
        }

    }
    public Connection getConnection(){
        return connection;
    }

}

