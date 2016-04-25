package Database;


import Util.Encryption.Encryption;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by Evdal on 03.03.2016.
 *
 * Uses connection created in DatabaseConnection to
 * send statement to usertable.
 *
 */


public class LoginManagement extends Management{

    private String loginQuery = "SELECT username, first_name, last_name, email, phone, hash, salt, access_level, status FROM user WHERE username = ?;";

    /**
     * Constructor to the LoginManagement.
     */
    public LoginManagement(){
        super();
    }

    /*
        Users are stored in their own table in the database.
        They have information such as username, password(Hash and salt) as well as
        access-level.

        login returns an int, which is their access-level, if -1 is
        returned, their password or username was wrong.

     */

    /**
     * Users are stored in their own table in the database.
     * They have information such as username, password(Hash and salt) as well as
     * access-level.
     *
     * login returns an int, which is their access-level, if -1 is
     * returned, their password or username was wrong.
     * @param user Username.
     * @param pass Password.
     * @return User object.
     */
    public Object[] login(String user, String pass){

        //Testbruker:

        //username = bruker
        //password = password

        //username = Even
        //password = pass

        ResultSet res;
        Object[] out = new Object[7];
        if(setUp()) {
            try {
                PreparedStatement prep = getConnection().prepareStatement(loginQuery);
                prep.setString(1, user);
                res = prep.executeQuery();
                if (res.next()) {
                    Encryption encrypt = new Encryption();
                    if (encrypt.passDecoding(pass, res.getString("hash"), res.getString("salt"))) {
                        return new Object[] {res.getString("username"), res.getString("first_name"), res.getString("last_name"), res.getString("email"), res.getString("phone"), res.getInt("access_level"), res.getInt("status")};
                    }
                }
            } catch (Exception e) {
                //     e.printStackTrace();
                System.err.println("Issue with SQL connection.");
                return out;
            } finally {
                closeConnection();
            }
        }
        return out;
    }

}
