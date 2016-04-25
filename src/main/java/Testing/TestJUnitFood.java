package Testing;

import Food.CreateShoppingList;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Evdal on 15.03.2016.
 */
public class TestJUnitFood {
    CreateShoppingList csList;
    @BeforeClass
    public void setUp(){
        csList = new CreateShoppingList();
    }
    @Test
    public void createShoppingList(){
        ArrayList<Object[]> test = CreateShoppingList.withDates("2015-04-22", "2016-04-22");
        assertNotNull(test);
        assertFalse(test.isEmpty());


    }
}
