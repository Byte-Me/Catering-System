package Testing;

import Database.*;
import Delivery.CreateDeliveryRoute;
import Statistics.*;
import Subscription.Subscriptions;
import org.junit.*;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EventListener;

import static org.junit.Assert.*;

/**
 * Created by Evdal on 09.03.2016.
 */
public class TestJUnitDB extends Management{
    private static CustomerManagement cust;
    private static DeliveryManagement deli;
    private static FoodManagement food;
    private static LoginManagement logi;
    private static OrderManagement orde;
    private static UserManagement user;
    private static SubscriptionManagement subs;

    private String[] validUser = new String[2];
    private String[] invalidUser = new String[2];

    private final int NO_ACCESS = -1;
    private final int ACCESS = 1;

    /**
     *  Bare for testing
     */
    private boolean testExecuteSQL(String sql){
        if(setUp()){
            PreparedStatement prep = null;
            try {
                getConnection().setAutoCommit(false);
                prep = getConnection().prepareStatement(sql);
                prep.execute();
            }catch (SQLException e){
                e.printStackTrace();
                rollbackStatement();
                return false;
            }finally {
                finallyStatement(null, prep);
            }
            return true;
        }
        return false;
    }




    @BeforeClass
    public static void DBsetUp(){
        try {
            cust = new CustomerManagement();
            deli = new DeliveryManagement();
            food = new FoodManagement();
            logi = new LoginManagement();
            orde = new OrderManagement();
            user = new UserManagement();
            subs = new SubscriptionManagement();
        }
        catch(Exception e) {
            System.err.println("Issue with databaseconnections! ");
            e.printStackTrace();
        }
    }
    @Before
    public void objSetUp(){
        validUser = new String[]{"Even","passord"};  //Accesslvl 1
        invalidUser = new String[]{"bruker", "pass"}; //accesslvl -1

    }
    @Test
    public void checkActiveSubscriptions(){

        // FIXME: veit ikke
        Subscriptions upt = new Subscriptions();
        assertTrue(upt.checkSubscriptionActive("2016-03-10", "2016-04-01", new Date()));
        assertFalse(upt.checkSubscriptionActive("2016-03-10", "2016-03-15", new Date()));

    }
    @Test
    public void loginTest(){
        int valid = 0;
        int invalid = 0;

        try {
            Object[] user1 = logi.login(validUser[0], validUser[1]);
            valid = (int)user1[5];
            Object[] user2 = logi.login(invalidUser[0], invalidUser[1]);
            invalid = (int)user2[5];

        }
        catch (Exception e){
            System.err.println("Issue with databaseconnections.");
            e.printStackTrace();
        }
        assertEquals(ACCESS, valid);
        assertEquals(NO_ACCESS, invalid);
    }

    @Test // fungerer bare dersom brukernavn på validUser endres til noe som ikke allerede finnes.
    public void createUser(){
        boolean validUser = false;
        boolean invalidUser = true;

        String fName = "Even2";
        String lName = "Dalen2";
        String uName = "EvenDalen!!2";
        String pass = "passord2";
        String email = "email2";
        String phone= "12345452";
        int accessLevel = 1;

        try{
            validUser = user.registerUser(fName, lName, uName, pass, email, phone, accessLevel);
            invalidUser = user.registerUser(fName, lName, uName, pass, email, phone, accessLevel);
            assertTrue(validUser);
            assertFalse(invalidUser);
        }
        catch (Exception e){
            System.err.println("Issue with databaseconnections! ");
            e.printStackTrace();
        }finally {
            String sql = "DELETE FROM user WHERE username = '" + uName + "';";
            testExecuteSQL(sql);
        }

    }
    @Test
    public void getUsers() {
        assertNotNull(user.userInfo());
    }
    @Test
    public void updateUsers(){
        assertTrue(user.updateUserInfoFName("krisss", "Det funket"));
        assertTrue(user.updateUserInfoLName("krisss", "Aasss"));
        assertTrue(user.updateUserInfoUsername("krisss", "krisss"));
        assertTrue(user.updateUserInfoPhone("krisss", "000800"));
        assertTrue(user.updateUserInfoEmail("krisss", "Kristaffer@kris.chrisP"));
        assertTrue(user.updateUserInfoAccessLevel("krisss", 3));
    }
    @Test
    public void getCustomers(){
        assertNotNull(cust.getCustomers());
    }
    @Test
    public void getIngredients(){
        assertNotNull(food.getIngredients());
    }
    @Test
    public void addRecipe(){
        ArrayList<Object[]> ing = new ArrayList<Object[]>();

        String name = "Test Name";
        int price = 100;

        ing.add(new Object[]{"Potet", 1});
        ing.add(new Object[]{"Fisk", 2});

        assertTrue(food.addRecipe(name, ing, price));
        String sql = "DELETE FROM recipe WHERE name = '" + name + "' AND price = '" + price + "';";
        testExecuteSQL(sql);
    }

    @Test
    public void addIngredients(){
        String name = "Testern";

        assertTrue(food.addIngredient(name, 69, "stk", 1));

        String sql = "DELETE FROM grocery WHERE name = '" + name + "';";
        testExecuteSQL(sql);
    }
    @Test
    public void searchUser(){
        assertNotNull(user.userSearch("Even"));
    }
    @Test
    public void searchCustomer(){
        assertNotNull(cust.customerSearch("Even"));
    }
    @Test
    public void deleteCustomer(){
        assertTrue(cust.deleteCustomer("even@dalen.no"));
    }
    @Test
    public void getSubscription(){
        ArrayList<Object[]> obj = subs.getSubscriptions();
        assertTrue(!obj.isEmpty());
    }
    @Test
    public void addCustomer(){
        String email = "test@test.test";
        assertTrue(cust.addCustomerPerson("Even", "Dalen", email, "12345", "Toppenhaugberget 60", "1356", "Bekkestua"));
        String sql = "DELETE FROM customer WHERE email = '" + email + "';";
        testExecuteSQL(sql);
    }

    @Test
    public void getDeliverys(){
        assertNotNull(deli.getDeliveryReady());
        assertNotNull(deli.getAdressReady());

    }

    @Test
    public void updateOrderStatus(){
        assertTrue(orde.updateStatus(4, 1)); // 4 = ID, 1 = new status
    }
    @Test
    public void getOrders(){
        ArrayList<Object[]> obj = orde.getOrders();
        assertTrue(!obj.isEmpty());
    }
    @Test
    public void testOrderStatistics(){
        OrderStatistics order = new OrderStatistics();
        System.out.println(Arrays.toString(order.createLineChartFromOrder("2008-11-20", "2016-11-20")));
        assertNotNull(order.createLineChartFromOrder("2016-01-20", "2016-11-20"));
        assertNotNull(order.createBarChartFromOrder("2016-01-20", "2016-11-20"));
    }
    @Test
    public void testHansMetode(){
        ArrayList<Object[]> obj = food.getRecipes();
        assertTrue(!obj.isEmpty());
    }
    @Ignore
    public void getIngredientsFromRecipes(){
       // ArrayList<Object[]> obj = food.getRecipeIngredients();
      //  assertTrue(!obj.isEmpty());
    }
    @Test
    public void ingredientToStorage(){
        assertTrue(food.addIngredientToStorage("Potet", 1));
    }
    @Test
    public void removeIngredientFromStorage(){
     //   assertTrue(food.removeIngredientFromStorage("Barn", 1));
    }
    @Test
    public void getIngredientsToChef(){
        ArrayList<Object[]> obj = food.getRecipesForChef();
        assertTrue(!obj.isEmpty());
    }
    @Test
    public void addOrder(){
        ArrayList<Object[]> obj = new ArrayList<>();
        obj.add(new Object[]{"Catfish", 5});
        obj.add(new Object[]{"Potatodog", 2});
        assertTrue(orde.createOrder("Test@Test", "2016-03-21", obj, "Uten makrell", "20:00:00"));
    }
    @Test
    public void getDeliveryInfo(){
        ArrayList<String> adresses = new ArrayList<>();
        adresses.add("Rønningsbakken 12, 7045 Trondheim, Norway");
        adresses.add("Erling Skakkes Gate 40, 7045 Trondheim, Norway");
        assertNotNull(deli.getDeliveryInfo(adresses));

    }
    @Test
    //int custID, String dateFrom, String dateTo, int weeksBetween, ArrayList<Object[][]> recipesWithDay, String note

    public void testCreateSubs()throws SQLException{
        ArrayList<Object[][]> obj = new ArrayList<>();
        obj.add(new Object[][]{{"Catfish", "Potatodog"},{2, 3},{1}});
        obj.add(new Object[][]{{"Catfish"},{3},{3}});
        Subscriptions upt = new Subscriptions();
      //  boolean bool = upt.createSubscription(10, "2016-03-20", "2016-05-08", 2, obj, "Bare cat ikke fish", "20:00:00");
        //assertTrue(bool);
    }
    @Test
    public void testDeliveryRoute(){
        //assertNotNull(CreateDeliveryRoute.UseReadyOrders("Oslo, Norway"));
        //assertNotNull(CreateDeliveryRoute.UseReadyOrdersLanLat("Oslo, Norway"));
        System.out.println(CreateDeliveryRoute.UseReadyOrders("Oslo, Norway"));

        System.out.println(CreateDeliveryRoute.UseReadyOrdersLatLng("Oslo, Norway"));

    }
    @After
    public void objTearDown(){
        validUser = null;
        invalidUser = null;
    }
    @Test
    public void searchOrder(){
        ArrayList<Object[]> obj = orde.orderSearch("2016");

        assertTrue(!obj.isEmpty());
    }
    @AfterClass
    public static void DBTearDown(){
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
