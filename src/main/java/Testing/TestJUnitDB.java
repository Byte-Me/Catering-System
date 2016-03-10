package Testing;

import Database.*;
import GUI.Login;
import org.junit.*;

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

    @Test
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
    @Ignore
    public void updateUsers(){
        assertTrue(user.updateUserInfoFName("kris", "Kristaffer"));
        assertTrue(user.updateUserInfoLName("kris", "Aasss"));
        assertTrue(user.updateUserInfoUsername("kris", "kris"));
        assertTrue(user.updateUserInfoPhone("kris", "000000"));
        assertTrue(user.updateUserInfoEmail("kris", "Kristaffer@kris.chrisP"));
        assertTrue(user.updateUserInfoAccessLevel("kris", 3));
    }
    @Test
    public void getIngredients(){
        assertNotNull(food.getIngredients());
    }
    @Ignore
    public void addRecipe(){
        assertNotNull(food.getIngredients());
    }
    @Ignore
    public void addIngredients(){
        assertTrue(food.addIngredient("Potet", 100, "kg"));
    }
    @Test
    public void searchUser(){
        assertNotNull(user.userSearch("Even"));
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
