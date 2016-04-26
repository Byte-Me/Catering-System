package Testing;

import java.util.ArrayList;

import static org.junit.Assert.*;

import Util.Delivery.CreateDeliveryRoute;
import Util.Delivery.TravelingSalesman;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Evdal on 09.03.2016.
 */
public class TestJUnitDelivery {
    private TravelingSalesman tsp;



    @Before
    public void setUp()throws Exception{
        tsp = new TravelingSalesman("Oslo, Norway");
    }

    @Test
    public void testTSP() throws Exception{
        ArrayList<String> test = new ArrayList<String>();


        //  test.add("Oslo, Norway");
        test.add("Paris, France");
        test.add("Madrid, Spain");
        test.add("London, England");
        test.add("Stockholm, Sweden");
        test.add("Copenhagen, Denmark");
        test.add("Berlin, Germany");


        ArrayList<String> answer = new ArrayList<String>();

        answer.add("London, England");
        answer.add("Madrid, Spain");
        answer.add("Paris, France");
        answer.add("Berlin, Germany");
        answer.add("Copenhagen, Denmark");
        answer.add("Stockholm, Sweden");

       // test = CreateDeliveryRoute.deliveryListForTest("Oslo, Norway",test);
        assertArrayEquals(answer.toArray(), test.toArray());
    }
    @After
    public void tearDown(){
        tsp = null;
    }

}
