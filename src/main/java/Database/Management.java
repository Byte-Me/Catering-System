package Database;

import org.apache.commons.dbutils.DbUtils;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Created by Evdal on 07.03.2016.
 */
public abstract class Management {
    private Connection connection;
    private Statement scentence;
    private DatabaseConnection c;

    public Management() throws Exception{
        //DatabaseConnection c; //new DatabaseConnection();

    }
    public boolean connected(){
        if(scentence != null)return true;
        else return false;
    }
    public void setUp()throws Exception {
        try {
            c = new DatabaseConnection();
            connection = c.getConnection();
            scentence = connection.createStatement();
        } catch (Exception e) {
            System.err.println("Connecting to database failed.");
            //    e.printStackTrace();
        }
    }
    public void closeConnection(){
        try {
            DbUtils.close(scentence);
            DbUtils.close(connection);
        }
        catch (Exception e){
            System.err.println("Problem with closing connection");
        }



    }/*
    public void closeConnection(){
        try {

            scentence.close();
            connection.close();
        }
        catch (Exception e){
            System.err.println("Closing database failed.");
         //   e.printStackTrace();
        }
    }*/

    public Statement getScentence() {
        return scentence;
    }

    public Connection getConnection() { return connection; }
}
