package Database;

import Encryption.Encryption;
import org.apache.commons.dbutils.DbUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserManagement extends Management {

    // Defines the User Types
    public enum UserType {
        ADMIN, SALE, DRIVER, CHEF;

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

    Connection conn = null;
    PreparedStatement prep = null;
    ResultSet res = null;

    public boolean registerUser(String firstname, String lastname, String username,
                                String password, String email, String phone, int accessLevel) {
        Encryption enc = new Encryption();
        int rowChanged = 0;
        if (setUp()) {
            try {
                conn = getConnection();
                String[] saltHash = enc.passEncoding(password);
                try {
                    prep = conn.prepareStatement("SELECT username FROM user WHERE username = ?;");
                    prep.setString(1, username);
                    res = prep.executeQuery();
                    if(res.next()) return false;
                } catch (Exception e){
                    rollbackStatement();
                    e.printStackTrace();
                    return false;
                }

                conn.setAutoCommit(false);
                prep = conn.prepareStatement("INSERT INTO user VALUES(DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
                prep.setString(1, username);
                prep.setString(2, saltHash[0]);
                prep.setString(3, saltHash[1]);
                prep.setString(4, firstname);
                prep.setString(5, lastname);
                prep.setString(6, phone);
                prep.setString(7, email);
                prep.setInt(8, accessLevel);
                prep.setInt(9, 1);
                rowChanged = prep.executeUpdate();

            } catch (Exception e) {
                System.err.println("Issue with creating user.");
                rollbackStatement();
                return false;
            } finally {
                finallyStatement(res, prep);
            }
        }
        return rowChanged > 0;
    }
    public ArrayList<Object[]> getDeletedUsers() {
        ArrayList<Object[]> out = new ArrayList<>();
        if (setUp()) {
            try {
                conn = getConnection();
                prep = conn.prepareStatement("SELECT first_name, last_name, email, phone, username, access_level FROM user WHERE status = 0 ORDER BY last_name;");
                res = prep.executeQuery();

                while (res.next()) {
                    Object[] obj = new Object[6];
                    obj[0] = res.getString("first_name");
                    obj[1] = res.getString("last_name");
                    obj[2] = res.getString("email");
                    obj[3] = res.getString("phone");
                    obj[4] = res.getString("username");
                    obj[5] = UserType.valueOf(res.getInt("access_level"));
                    out.add(obj);
                }

            } catch (SQLException e) {
                System.err.println("Issue with executing SQL scentence.");
                return null;
            }finally {
                finallyStatement(res, prep);
            }

            return out;
        } else return null;
    }
    public ArrayList<Object[]> getDrivers() {
        ArrayList<Object[]> out = new ArrayList<>();
        if (setUp()) {
            try {
                conn = getConnection();
                prep = conn.prepareStatement("select username from user WHERE status = ? AND access_level = ? order by username;");
                prep.setInt(1,1); //ACTIVE
                prep.setInt(2,UserType.DRIVER.getValue());
                res = prep.executeQuery();

                while (res.next()) {
                    Object[] obj = new Object[1];
                    obj[0] = res.getString("username");
                    out.add(obj);
                }
            } catch (SQLException e) {
                System.err.println("Issue with executing SQL scentence.");
                return null;
            } finally {
                finallyStatement(res, prep);
            }
            return out;
        } else return null;

    }


    public ArrayList<Object[]> userInfo() {
        ArrayList<Object[]> out = new ArrayList<>();
        if (setUp()) {
            try {
                conn = getConnection();
                prep = conn.prepareStatement("select first_name, last_name, email, phone, username, access_level from user WHERE status = 1 order by last_name;");
                res = prep.executeQuery();

                while (res.next()) {
                    Object[] obj = new Object[6];
                    obj[0] = res.getString("first_name");
                    obj[1] = res.getString("last_name");
                    obj[2] = res.getString("email");
                    obj[3] = res.getString("phone");
                    obj[4] = res.getString("username");
                    obj[5] = UserType.valueOf(res.getInt("access_level"));
                    out.add(obj);
                }
            } catch (SQLException e) {
                System.err.println("Issue with executing SQL scentence.");
                return null;
            } finally {
                finallyStatement(res, prep);
            }
            return out;
        } else return null;

    }

    public Object[] getSingleUserInfo(String username){
        Object[] out = new Object[6];
        if(setUp()){
            try {
                conn = getConnection();
                prep = conn.prepareStatement("SELECT first_name, last_name, email, phone, username, access_level FROM user WHERE username = ?;");
                prep.setString(1, username);
                res = prep.executeQuery();
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
                finallyStatement(res, prep);
            }
        }
        return out;
    }


    public boolean updateUserInfoFName(String username, String newData) {
        int rowChanged = 0;
        if (setUp()) {
            try {
                conn = getConnection();
                conn.setAutoCommit(false);
                prep = conn.prepareStatement("UPDATE user SET first_name = ? WHERE username = ?;");
                prep.setString(1, newData);
                prep.setString(2, username);
                rowChanged = prep.executeUpdate();

            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");
                rollbackStatement();
                return false;
            } finally {
                finallyStatement(res, prep);
            }
        }
        return rowChanged > 0;
    }

    public boolean updateUserPass(String username, String newData) {
        int rowChanged = 0;
        if (setUp()) {
            try {
                conn = getConnection();
                conn.setAutoCommit(false);
                Encryption enc = new Encryption();
                String[] saltHash = enc.passEncoding(newData);
                prep = conn.prepareStatement("UPDATE user SET salt = ?, hash = ? WHERE username = ?;");
                prep.setString(1, saltHash[0]);
                prep.setString(2, saltHash[1]);
                prep.setString(3, username);
                rowChanged = prep.executeUpdate();

            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");
                rollbackStatement();
                return false;
            } finally {
                finallyStatement(res, prep);
            }
        }
        return rowChanged > 0;
    }


    public boolean updateUserInfoLName(String username, String newData) {
        int rowChanged = 0;
        if (setUp()) {
            try {
                conn = getConnection();
                conn.setAutoCommit(false);
                prep = conn.prepareStatement("UPDATE user SET last_name = ? WHERE username = ?;");
                prep.setString(1, newData);
                prep.setString(2, username);
                rowChanged = prep.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");
                rollbackStatement();
                return false;
            } finally {
                finallyStatement(res, prep);
            }
        }
        return rowChanged > 0;
    }

    public boolean updateUserInfoUsername(String username, String newData) {
        int rowChanged = 0;
        if (setUp()) {
            try {
                conn = getConnection();
                conn.setAutoCommit(false);
                prep = conn.prepareStatement("UPDATE user SET username = ? WHERE username = ?;");
                prep.setString(1, newData);
                prep.setString(2, username);
                rowChanged = prep.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");
                rollbackStatement();
                return false;
            } finally {
                finallyStatement(res, prep);
            }
        }
        return rowChanged > 0;
    }

    public boolean updateUserInfoPhone(String username, String newData) {
        int rowChanged = 0;
        if (setUp()) {
            try {
                conn = getConnection();
                conn.setAutoCommit(false);
                prep = conn.prepareStatement("UPDATE user SET phone = ? WHERE username = ?;");
                prep.setString(1, newData);
                prep.setString(2, username);
                rowChanged = prep.executeUpdate();

            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");
                rollbackStatement();
                return false;
            } finally {
                finallyStatement(res, prep);
            }
        }
        return rowChanged > 0;
    }

    public boolean updateUserInfoEmail(String username, String newData) {
        int rowChanged = 0;
        if (setUp()) {
            try {
                conn = getConnection();
                conn.setAutoCommit(false);
                prep = conn.prepareStatement("UPDATE user SET email = ? WHERE username = ?;");
                prep.setString(1, newData);
                prep.setString(2, username);
                rowChanged = prep.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");
                rollbackStatement();
                return false;
            } finally {
                finallyStatement(res, prep);
            }
        }
        return rowChanged > 0;
    }
    public boolean updateUserInfoAccessLevel(String username, int newData) {
        int rowChanged = 0;
        if (setUp()) {
            try {
                conn = getConnection();
                conn.setAutoCommit(false);
                prep = conn.prepareStatement("UPDATE user SET access_level = ? WHERE username = ?;");
                prep.setInt(1, newData);
                prep.setString(2, username);
                rowChanged = prep.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");
                rollbackStatement();
                return false;
            } finally {
                finallyStatement(res, prep);
            }
        }
        return rowChanged > 0;
    }
    public ArrayList<Object[]> userSearch(String searchTerm){
        ArrayList<Object[]> out = new ArrayList<>();
        if(setUp()) {
            try {
                conn = getConnection();
                prep = conn.prepareStatement("SELECT username, first_name, last_name, phone, email, access_level" +
                        " FROM user WHERE username LIKE ? OR first_name LIKE ? OR last_name LIKE ? OR phone LIKE" +
                        " ? OR email LIKE ? OR access_level LIKE ? AND status = 1 ORDER BY last_name;");
                prep.setString(1, "%" + searchTerm + "%");
                prep.setString(2, "%" + searchTerm + "%");
                prep.setString(3, "%" + searchTerm + "%");
                prep.setString(4, "%" + searchTerm + "%");
                prep.setString(5, "%" + searchTerm + "%");
                prep.setString(6, "%" + searchTerm + "%");
                res = prep.executeQuery();
                while (res.next()) {
                    Object[] obj = new Object[6];
                    obj[0] = res.getString("first_name");
                    obj[1] = res.getString("last_name");
                    obj[2] = res.getString("email");
                    obj[3] = res.getString("phone");
                    obj[4] = res.getString("username");
                    obj[5] = UserType.valueOf(res.getInt("access_level"));
                    out.add(obj);
                }
            } catch (Exception e) {
                System.err.println("Issue with search.");
                return null;
            } finally {
                finallyStatement(res, prep);
            }
            return out;
        }
        else return null;
    }

    public boolean updateUserStatus(String username, int status) {
        int rowChanged = 0;
        if (setUp()) {
            try {
                conn = getConnection();
                conn.setAutoCommit(false);
                prep = conn.prepareStatement("UPDATE user SET status = ? WHERE username = ?;");
                prep.setInt(1, status);
                prep.setString(2, username);
                rowChanged = prep.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Issue with executing database update.");
                rollbackStatement();
                return false;
            } finally {
                finallyStatement(res, prep);
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


}