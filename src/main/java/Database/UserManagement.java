package Database;

import Encryption.Encryption;
import org.apache.commons.dbutils.DbUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserManagement extends Management {

    // Defines the User Types
    public enum UserType {
        ADMIN, SALE, DRIVER, CHEF,INACTIVE;

        public int getValue() {
            return super.ordinal();
        }

        public static UserType valueOf(int userTypeNr) {
            for (UserType type : UserType.values()) {
                if (type.ordinal() == userTypeNr) {
                    return type;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            String constName = super.toString();
            return constName.substring(0,1) + constName.substring(1).toLowerCase();
        }

    }


    public UserManagement() {
        super();
    }

    public boolean registerUser(String firstname, String lastname, String username,
                                String password, String email, String phone, int accessLevel) {
        Encryption enc = new Encryption();
        int rowChanged = 0;
        if (setUp()) {
            try {
                String[] saltHash = enc.passEncoding(password);
                try {
                    ResultSet res = getScentence().executeQuery("SELECT username FROM user WHERE username = '" + username + "';");
                    if(res.next()) return false;
                } catch (Exception e){

                }

                getConnection().setAutoCommit(false);
                rowChanged = getScentence().executeUpdate("INSERT INTO user VALUES(DEFAULT, '" + username +
                        "', '" + saltHash[0] + "', '" + saltHash[1] + "', '" + firstname + "', '" + lastname
                        + "', '" + phone + "', '" + email + "', " + accessLevel + ");");

                getConnection().commit();
            } catch (Exception e) {
                System.err.println("Issue with creating user.");

                try {
                    getConnection().rollback();
                    getConnection().setAutoCommit(true);
                } catch (SQLException sqle){
                    System.err.println("Could not rollback");
                }

                return false;
            } finally {

                try {
                    getConnection().setAutoCommit(true);
                }catch (SQLException sqle){
                    sqle.printStackTrace();
                }

                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());

            }
        }
        return rowChanged > 0;
    }
    public ArrayList<Object[]> getDeletedUsers() {

        ResultSet res;
        ArrayList<Object[]> out = new ArrayList<>();
        if (setUp()) {

            try {

                res = getScentence().executeQuery("select first_name, last_name, email, phone, username, access_level from user" +
                        " WHERE status = 0 order by last_name;");
                while (res.next()) {
                    Object[] obj = new Object[6];
                    obj[0] = res.getString("first_name");
                    obj[1] = res.getString("last_name");
                    obj[2] = res.getString("email");
                    obj[3] = res.getString("phone");
                    obj[4] = res.getString("username");
                    // Convert access level from int to string

                    obj[5] = UserType.valueOf(res.getInt("access_level"));
                    out.add(obj);
                }
            } catch (SQLException e) {
                System.err.println("Issue with executing SQL scentence.");
                return null;
            }
            return out;
        } else return null;

    }


    public ArrayList<Object[]> userInfo() {

        ResultSet res;
        ArrayList<Object[]> out = new ArrayList<>();
        if (setUp()) {

            try {

                res = getScentence().executeQuery("select first_name, last_name, email, phone, username, access_level from user" +
                        " WHERE status = 1 order by last_name;");
                while (res.next()) {
                    Object[] obj = new Object[6];
                    obj[0] = res.getString("first_name");
                    obj[1] = res.getString("last_name");
                    obj[2] = res.getString("email");
                    obj[3] = res.getString("phone");
                    obj[4] = res.getString("username");
                    // Convert access level from int to string

                    obj[5] = UserType.valueOf(res.getInt("access_level"));
                    out.add(obj);
                }
            } catch (SQLException e) {
                System.err.println("Issue with executing SQL scentence.");
                return null;
            }
            return out;
        } else return null;

    }

    public Object[] getSingleUserInfo(String username){
        Object[] out = new Object[6];
        if(setUp()){
            try {

                ResultSet res = getScentence().executeQuery("SELECT first_name, last_name, email, phone, username, access_level" +
                        " FROM user WHERE username = '"+username+"';");
                if(res.next()){
                    out[0] = res.getString("first_name");
                    out[1] = res.getString("last_name");
                    out[2] = res.getString("email");
                    out[3] = res.getString("phone");
                    out[4] = res.getString("username");
                    out[5] = res.getInt("access_level");
                }
            }
            catch (SQLException e){
                System.err.println("Issue with getting user info.");
                return null;
            }
            finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        return out;
    }




    /**
     * Legger til transakjoner til update setningene
     * sånn ingen kan endre de når man updater!
    */

    public boolean updateUserInfoFName(String username, String newData) {
        int rowChanged = 0;
        if (setUp()) {
            try {
                getConnection().setAutoCommit(false);

                PreparedStatement prep = getConnection().prepareStatement("UPDATE user SET first_name = ? WHERE username = ?;");
                prep.setString(1, newData);
                prep.setString(2, username);
                rowChanged = prep.executeUpdate();

                getConnection().commit();
            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");
                try {
                    getConnection().rollback();
                    getConnection().setAutoCommit(true);
                }catch (SQLException sqle){
                    System.err.println("Could not rollback");
                }
                return false;
            } finally {
                try {
                    getConnection().setAutoCommit(true);
                }catch (SQLException sqle){
                    sqle.printStackTrace();
                }
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        return rowChanged > 0;
    }

    public boolean updateUserPass(String username, String newData) {
        int rowChanged = 0;
        if (setUp()) {
            try {
                getConnection().setAutoCommit(false);

                Encryption enc = new Encryption();
                String[] saltHash = enc.passEncoding(newData);
                PreparedStatement prep = getConnection().prepareStatement("UPDATE user SET salt = ?, hash = ? WHERE username = ?;");
                prep.setString(1, saltHash[0]);
                prep.setString(2, saltHash[1]);
                prep.setString(3, username);
                rowChanged = prep.executeUpdate();

                getConnection().commit();
            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");

                try {
                    getConnection().rollback();
                    getConnection().setAutoCommit(true);
                }catch (SQLException sqle){
                    System.err.println("Could not rollback");
                }

                return false;
            } finally {

                try {
                    getConnection().setAutoCommit(true);
                }catch (SQLException sqle){
                    sqle.printStackTrace();
                }

                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        return rowChanged > 0;
    }


    public boolean updateUserInfoLName(String username, String newData) {
        int rowChanged = 0;
        if (setUp()) {
            try {
                getConnection().setAutoCommit(false);

                PreparedStatement prep = getConnection().prepareStatement("UPDATE user SET last_name = ? WHERE username = ?;");
                prep.setString(1, newData);
                prep.setString(2, username);
                rowChanged = prep.executeUpdate();

                getConnection().commit();
                prep.close();
            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");

                try {
                    getConnection().rollback();
                    getConnection().setAutoCommit(true);
                }catch (SQLException sqle){
                    System.err.println("Could not rollback");
                }

                return false;
            } finally {
                try {
                    getConnection().setAutoCommit(true);
                }catch (SQLException sqle){
                    sqle.printStackTrace();
                }

                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        return rowChanged > 0;
    }
    public boolean updateUserInfoUsername(String username, String newData) {
        int rowChanged = 0;
        if (setUp()) {
            try {
                getConnection().setAutoCommit(false);

                PreparedStatement prep = getConnection().prepareStatement("UPDATE user SET username = ? WHERE username = ?;");
                prep.setString(1, newData);
                prep.setString(2, username);
                rowChanged = prep.executeUpdate();

                getConnection().commit();
                prep.close();
            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");

                try {
                    getConnection().rollback();
                    getConnection().setAutoCommit(true);
                }catch (SQLException sqle){
                    System.err.println("Could not rollback");
                }

                return false;
            } finally {

                try {
                    getConnection().setAutoCommit(true);
                }catch (SQLException sqle){
                    sqle.printStackTrace();
                }

                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        return rowChanged > 0;
    }
    public boolean updateUserInfoPhone(String username, String newData) {
        int rowChanged = 0;
        if (setUp()) {
            try {
                getConnection().setAutoCommit(false);

                PreparedStatement prep = getConnection().prepareStatement("UPDATE user SET phone = ? WHERE username = ?;");
                prep.setString(1, newData);
                prep.setString(2, username);
                rowChanged = prep.executeUpdate();

                getConnection().commit();
                prep.close();
            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");

                try {
                    getConnection().rollback();
                    getConnection().setAutoCommit(true);
                }catch (SQLException sqle){
                    System.err.println("Could not rollback");
                }

                return false;
            } finally {

                try {
                    getConnection().setAutoCommit(true);
                }catch (SQLException sqle){
                    sqle.printStackTrace();
                }

                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        return rowChanged > 0;
    }
    public boolean updateUserInfoEmail(String username, String newData) {
        int rowChanged = 0;
        if (setUp()) {
            try {
                getConnection().setAutoCommit(false);

                PreparedStatement prep = getConnection().prepareStatement("UPDATE user SET email = ? WHERE username = ?;");
                prep.setString(1, newData);
                prep.setString(2, username);
                rowChanged = prep.executeUpdate();

                getConnection().commit();
                prep.close();
            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");

                try {
                    getConnection().rollback();
                    getConnection().setAutoCommit(true);
                }catch (SQLException sqle){
                    System.err.println("Could not rollback");
                }

                return false;
            } finally {

                try {
                    getConnection().setAutoCommit(true);
                }catch (SQLException sqle){
                    sqle.printStackTrace();
                }

                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        return rowChanged > 0;
    }
    public boolean updateUserInfoAccessLevel(String username, int newData) {
        int rowChanged = 0;
        if (setUp()) {
            try {
                getConnection().setAutoCommit(false);

                PreparedStatement prep = getConnection().prepareStatement("UPDATE user SET access_level = ? WHERE username = ?;");
                prep.setInt(1, newData);
                prep.setString(2, username);
                rowChanged = prep.executeUpdate();

                getConnection().commit();
                prep.close();
            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");

                try {
                    getConnection().rollback();
                    getConnection().setAutoCommit(true);
                }catch (SQLException sqle){
                    System.err.println("Could not rollback");
                }

                return false;
            } finally {

                try {
                    getConnection().setAutoCommit(true);
                }
                catch (SQLException sqle){
                    sqle.printStackTrace();
                }

                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        return rowChanged > 0;
    }
    public ArrayList<Object[]> userSearch(String searchTerm){
        ResultSet res;
        ArrayList<Object[]> out = new ArrayList<>();
        if(setUp()) {
            try {
                PreparedStatement prep = getConnection().prepareStatement("SELECT username, first_name, last_name, phone, email, access_level" +
                        " FROM user WHERE username LIKE ? OR first_name LIKE ? OR last_name LIKE ? OR phone LIKE ? OR email LIKE ? OR access_level LIKE ? AND status = 1 ORDER BY last_name;");
                searchTerm = "%" + searchTerm + "%";
                prep.setString(1, searchTerm);
                prep.setString(2, searchTerm);
                prep.setString(3, searchTerm);
                prep.setString(4, searchTerm);
                prep.setString(5, searchTerm);
                prep.setString(6, searchTerm);

                res = prep.executeQuery();

                while (res.next()) {
                    Object[] obj = new Object[6];
                    obj[0] = res.getString("first_name");
                    obj[1] = res.getString("last_name");
                    obj[2] = res.getString("email");
                    obj[3] = res.getString("phone");
                    obj[4] = res.getString("username");
                    // Convert access level from int to string
                    obj[5] = UserType.valueOf(res.getInt("access_level"));
                    out.add(obj);
                }
                prep.close();
            } catch (Exception e) {
                System.err.println("Issue with search.");
                return null;
            } finally {
                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }

            return out;
        }
        else return null;
    }

    public boolean updateUserStatus(String username, int status) {
        int rowChanged = 0;
        if (setUp()) {
            try {
                getConnection().setAutoCommit(false);

                PreparedStatement prep = getConnection().prepareStatement("UPDATE user SET status = ? WHERE username = ?;");
                prep.setInt(1, status);
                prep.setString(2, username);
                rowChanged = prep.executeUpdate();

                getConnection().commit();
                prep.close();
            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");

                try {
                    getConnection().rollback();
                    getConnection().setAutoCommit(true);
                }catch (SQLException sqle){
                    System.err.println("Could not rollback");
                }

                return false;
            } finally {

                try {
                    getConnection().setAutoCommit(true);
                }
                catch (SQLException sqle){
                    sqle.printStackTrace();
                }

                DbUtils.closeQuietly(getScentence());
                DbUtils.closeQuietly(getConnection());
            }
        }
        return rowChanged > 0;
    }

    public boolean deleteUser(String username){
        return updateUserStatus(username, 0);
    }

    public boolean reactivateUser(String username){
        return updateUserStatus(username, 1);
    }

    /*

    public boolean deleteUser(){
        ResultSet res = null;
        if(setUp()){
            try{
                res = getScentence().executeUpdate("UPDATE user SET active = ")
            }
            catch (Exception e){
                System.err.println("Issue with deleting user.");
            }
        }
    }*/

}



/*
    public static void main(String[] args){
        UserManagement test = new UserManagement();
        if(test.connected()) {
            if (test.registerUser("Even", "pass", "ed@do.set", 2)) System.out.println("Success!");
        }
    }
}
*/