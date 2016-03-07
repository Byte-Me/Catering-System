package Database;

import java.sql.Connection;
import java.sql.Statement;
import Encryption.*;

public class UserManagement {
    private Connection connection;
    private Statement scentence;

    public UserManagement() throws Exception{
        DatabaseConnection c = new DatabaseConnection();
        connection = c.getConnection();

        scentence = connection.createStatement();
    }
    public boolean connected(){
        if(scentence != null)return true;
        else return false;
    }
    public boolean registrerUser(String username, String password, String email, int accessLevel)throws Exception{
        Encryption enc = new Encryption();
        String[] saltHash = enc.passEncoding(password);
        int rowChanged = scentence.executeUpdate("INSERT INTO user VALUES(DEFAULT, '" + username +
                "', '" + saltHash[0] + "', '" + saltHash[1] + "', '" + email + "', " + accessLevel + ");");
        if(rowChanged > 0)return true;
        return false;
    }

    public static void main(String[] args) throws Exception{
        UserManagement test = new UserManagement();
        if(test.connected())System.out.println("Success");
        if(test.registrerUser("Even", "pass", "ed@do.set", 2))System.out.println("Success!");
    }
}
