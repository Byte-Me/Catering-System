package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by olekristianaune on 20.04.2016.
 */
public class SettingsManagement extends Management {

    /**
     * Constructor of SettingsManagement.
     */
    public SettingsManagement() {
        super();
    }

    // SQL setning for getting address
    private static String sqlSelectAddress = "SELECT `content` FROM `settings` WHERE id = 'address';";

    // SQL setning for setting address
    private static String sqlInsertAddress = "UPDATE `settings` SET content = ? WHERE id = 'address';";

    // SQL setning for getting city
    private static String sqlSelectCity = "SELECT `content` FROM `settings` WHERE id = 'city';";

    // SQL setning for setting city
    private static String sqlInsertCity = "UPDATE `settings` SET content = ? WHERE id = 'city';";

    // SQL setning for getting country
    private static String sqlSelectCountry = "SELECT `content` FROM `settings` WHERE id = 'country';";

    // SQL setning for setting country
    private static String sqlInsertCountry = "UPDATE `settings` SET content = ? WHERE id = 'country';";


    Connection conn = null;
    PreparedStatement prep = null;
    ResultSet res = null;

    /**
     * Gets system adress.
     * @return
     */
    public String getSystemAddress() {
        String out = null;
        if (setUp()) {
            conn = getConnection();
            try {
                conn.setAutoCommit(false);
                prep = conn.prepareStatement(sqlSelectAddress);
                res = prep.executeQuery();
                while (res.next()) {
                    out = res.getString("content");
                }
            } catch (Exception e) {
                System.err.println("ERROR 101: Issue with getting system address.");
                return null;
            } finally {
                finallyStatement(res, prep);
            }
        }
        return out;

    }

    /**
     * Set new system adress.
     * @param newValue New adress value.
     * @return
     */
    public boolean setSystemAddress(String newValue) {
        int rowChanged = 0;
        if (setUp()) {
            conn = getConnection();
            try {
                conn.setAutoCommit(false);
                prep = conn.prepareStatement(sqlInsertAddress);
                prep.setString(1, newValue);
                rowChanged = prep.executeUpdate();
            } catch (Exception e) {
                System.err.println("ERROR 102: Issue with updating system address.");
                return false;
            } finally {
                finallyStatement(res, prep);

            }
        }

        return rowChanged > 0;

    }

    /**
     * Gets system city.
     * @return
     */
    public String getSystemCity() {
        String out = null;
        if (setUp()) {
            conn = getConnection();
            try {
                conn.setAutoCommit(false);
                prep = conn.prepareStatement(sqlSelectCity);
                res = prep.executeQuery();
                while (res.next()) {
                    out = res.getString("content");
                }
            } catch (Exception e) {
                System.err.println("ERROR 103: Issue with getting system city.");
                return null;
            } finally {
                finallyStatement(res, prep);
            }
        }
        return out;

    }

    /**
     * Sets new system city.
     * @param newValue New system city value.
     * @return
     */
    public boolean setSystemCity(String newValue) {
        int rowChanged = 0;
        if (setUp()) {
            conn = getConnection();
            try {
                conn.setAutoCommit(false);
                prep = conn.prepareStatement(sqlInsertCity);
                prep.setString(1, newValue);
                rowChanged = prep.executeUpdate();
            } catch (Exception e) {
                System.err.println("ERROR 104: Issue with updating system city.");
                return false;
            } finally {
                finallyStatement(res, prep);

            }
        }

        return rowChanged > 0;

    }

    /**
     * Gets system country.
     * @return
     */
    public String getSystemCountry() {
        String out = null;
        if (setUp()) {
            conn = getConnection();
            try {
                conn.setAutoCommit(false);
                prep = conn.prepareStatement(sqlSelectCountry);
                res = prep.executeQuery();
                while (res.next()) {
                    out = res.getString("content");
                }
            } catch (Exception e) {
                System.err.println("ERROR 105: Issue with getting system country.");
                return null;
            } finally {
                finallyStatement(res, prep);
            }
        }
        return out;

    }

    /**
     * Sets new system country.
     * @param newValue New system country value.
     * @return
     */
    public boolean setSystemCountry(String newValue) {
        int rowChanged = 0;
        if (setUp()) {
            conn = getConnection();
            try {
                conn.setAutoCommit(false);
                prep = conn.prepareStatement(sqlInsertCountry);
                prep.setString(1, newValue);
                rowChanged = prep.executeUpdate();
            } catch (Exception e) {
                System.err.println("ERROR 106: Issue with updating system country.");
                return false;
            } finally {
                finallyStatement(res, prep);

            }
        }

        return rowChanged > 0;

    }



}
