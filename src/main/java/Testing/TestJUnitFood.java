package Testing;

import Food.CreateShoppingList;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Evdal on 15.03.2016.
 */
public class TestJUnitFood {
    @Test
    public void createShoppingList(){
        ArrayList<Object[]> test = CreateShoppingList.useOrdersToday();
        assertNotNull(test);


    }
}