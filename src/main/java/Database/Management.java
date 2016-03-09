package Database;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Created by Evdal on 07.03.2016.
 */
public abstract class Management {
    private Connection connection;
    private Statement scentence;

    public Management() throws Exception{
        DatabaseConnection c = new DatabaseConnection();
        connection = c.getConnection();
        scentence = connection.createStatement();
    }
    public boolean connected(){
        if(scentence != null)return true;
        else return false;
    }
    public void closeConnection()throws Exception{
        scentence.close();
        connection.close();
    }

    public Connection getConnection() {
        return connection;
    }

    public Statement getScentence() {
        return scentence;
    }
}
