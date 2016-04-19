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

    public Management(){
        //DatabaseConnection c; //new DatabaseConnection();

    }
    protected boolean connected(){
        return scentence != null;
    }
    protected boolean setUp(){
        try {
            c = new DatabaseConnection();
            connection = c.getConnection();
            scentence = connection.createStatement();
        } catch (Exception e) {
            System.err.println("Connecting to database failed.");
            DbUtils.closeQuietly(scentence);
            DbUtils.closeQuietly(connection);
            //    e.printStackTrace();
            return false;
        }
        return !(connection == null || scentence == null);
    }
    protected void closeConnection(){
        try {
            DbUtils.closeQuietly(scentence);
            DbUtils.closeQuietly(connection);
        }
        catch (Exception e){
            System.err.println("Problem with closing connection");
        }



    }
    protected String adressFormatter(String city, String postal_code, String street){
        return street + ", " + postal_code + " " + city + ", Norway";
    }
    protected String nameFormatter(String firstname, String lastname){
        return lastname + ", " + firstname;
    }

    /*
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

    protected Statement getScentence() {
        return scentence;
    }

    protected Connection getConnection() { return connection; }
}
