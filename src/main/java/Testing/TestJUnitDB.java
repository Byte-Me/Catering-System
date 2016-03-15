package Testing;

import Database.*;
import Delivery.CreateDeliveryRoute;
import Food.CreateShoppingList;
import GUI.Login;
import org.junit.*;

import java.util.ArrayList;
import java.util.Arrays;

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
        }
        catch(Exception e) {
            System.err.println("Issue with databaseconnections! ");
            e.printStackTrace();
        }
    }
    @Before
    public void objSetUp(){
        validUser = new String[]{"Even","passord"}; ; //Accesslvl 1
        invalidUser = new String[]{"bruker", "pass"}; //accesslvl -1

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
        for(Object[] i : ing){
            System.out.println(Arrays.toString(i));
        }

        assertTrue(food.addRecipe("Oppskriftarererer", ing));
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
    public void testDeliveryRoute(){
        assertNotNull(CreateDeliveryRoute.UseReadyOrders("Oslo, Norway"));
    }
    @After
    public void objTearDown(){
        validUser = null;
        invalidUser = null;
    }
    @AfterClass
    public static void DBTearDown(){
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
