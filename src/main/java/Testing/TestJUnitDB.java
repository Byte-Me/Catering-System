package Testing;

import Database.*;
import Delivery.CreateDeliveryRoute;
import Statistics.*;
import Subscription.Subscriptions;
import org.junit.*;

import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by Evdal on 09.03.2016.
 */
public class TestJUnitDB{
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
        Subscriptions upt = new Subscriptions();
        assertTrue(upt.checkSubscriptionActive("2016-03-10", "2016-04-01", new Date()));
        assertFalse(upt.checkSubscriptionActive("2016-03-10", "2016-03-15", new Date()));

    }
    @Test
    public void loginTest(){
        int valid = 0;
        int invalid = 0;

        try {
            valid = logi.login(validUser[0], validUser[1]);
            invalid = logi.login(invalidUser[0], invalidUser[1]);

        }
        catch (Exception e){
            System.err.println("Issue with databaseconnections.");
      //      e.printStackTrace();
        }
        assertEquals(ACCESS, valid);
        assertEquals(NO_ACCESS, invalid);



    }

    @Test // fungerer bare dersom brukernavn på validUser endres til noe som ikke allerede finnes.
    public void createUser(){
        boolean validUser = false;
        boolean invalidUser = true;
        try{
            validUser = user.registerUser("Even", "Dalen", "EvenDalen!!", "passord", "email", "1234545", 1);
            invalidUser = user.registerUser("Even", "Dalen", "EvenD", "passord", "email", "1234545", 1);

        }
        catch (Exception e){
            System.err.println("Issue with databaseconnections! ");
            e.printStackTrace();
        }
        assertTrue(validUser);
        assertFalse(invalidUser);
    }
    @Test
    public void getUsers() {
        assertNotNull(user.userInfo());
    }
    @Test // TODO: username fungerer ikke?
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
        ing.add(new Object[]{"Potet", 1});
        ing.add(new Object[]{"Fisk", 2});

        assertTrue(food.addRecipe("Oppskriftarererer", ing, 100));
    }

    @Test
    public void addIngredients(){
        assertTrue(food.addIngredient("Barn", 100, "kg", 0));
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
        assertTrue(cust.addCustomerPerson("Even", "Dalen", "even@dalen.no", "12345", "Toppenhaugberget 60", "1356", "Bekkestua"));

        //String firstname, String lastname, String email, String phone,
        //String streetAdress, String postCode, String city
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
        boolean bool = upt.createSubscription(10, "2016-03-20", "2016-05-08", 2, obj, "Bare cat ikke fish", "20:00:00");
        assertTrue(bool);
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
