package Database;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Created by Evdal on 07.03.2016.
 */
public abstract class Management {
    private Connection connection;
    private Statement scentence;
    DatabaseConnection c;

    public Management() throws Exception{
        DatabaseConnection c = new DatabaseConnection();

    }
    public boolean connected(){
        if(scentence != null)return true;
        else return false;
    }
    public void setUp()throws Exception{
        try {
            connection = c.getConnection();
            scentence = connection.createStatement();
        }
        catch (Exception e){
            System.err.println("Connection to database failed.");
            e.printStackTrace();
            closeConnection();
        }

    }
    public void closeConnection(){
        try {
            scentence.close();
            connection.close();
        }
        catch (Exception e){
            System.err.println("Closing database failed.");
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public Statement getScentence() {
        return scentence;
    }
}
