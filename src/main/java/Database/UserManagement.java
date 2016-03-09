package Database;

import java.sql.Connection;
import java.sql.Statement;
import Encryption.*;
import org.apache.commons.dbutils.DbUtils;

public class UserManagement extends Management{


    public UserManagement() throws Exception{
        super();
    }

    public boolean registerUser(String username, String password, String email, int accessLevel)throws Exception{
        Encryption enc = new Encryption();
        int rowChanged = 0;
        String[] saltHash = enc.passEncoding(password);
        try {
            setUp();
            rowChanged = getScentence().executeUpdate("INSERT INTO user VALUES(DEFAULT, '" + username +
                    "', '" + saltHash[0] + "', '" + saltHash[1] + "', '" + email + "', " + accessLevel + ");");
        }
        catch(Exception e){
            System.err.println("Issue with creating user.");
          //  e.printStackTrace();
        }
        finally {
            DbUtils.closeQuietly(getScentence());
            DbUtils.closeQuietly(getConnection());

        }
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
