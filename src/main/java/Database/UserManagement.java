package Database;

import java.sql.Connection;
import java.sql.Statement;
import Encryption.*;

public class UserManagement extends Management{


    public UserManagement() throws Exception{
        super();
    }

    public boolean registerUser(String username, String password, String email, int accessLevel)throws Exception{
        Encryption enc = new Encryption();
        String[] saltHash = enc.passEncoding(password);
        int rowChanged = getScentence().executeUpdate("INSERT INTO user VALUES(DEFAULT, '" + username +
                "', '" + saltHash[0] + "', '" + saltHash[1] + "', '" + email + "', " + accessLevel + ");");
        closeConnection();
        if(rowChanged > 0)return true;
        return false;
    }

    public static void main(String[] args) throws Exception{
        UserManagement test = new UserManagement();
        if(test.connected()) {
            if (test.registerUser("Even", "pass", "ed@do.set", 2)) System.out.println("Success!");
        }
    }
}
