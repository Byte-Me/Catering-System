import GUI.*;
import Delivery.*;

import java.util.ArrayList;

/**
 * Created by olekristianaune on 07.03.2016.
 */
public class Program {
    public static void main(String[] args) throws Exception {
        Login loginForm = new Login();

        /*
        ArrayList<String> test = new ArrayList<String>();


        test.add("Paris, France");
        test.add("Madrid, Spain");
        test.add("London, England");
        test.add("Stockholm, Sweden");
        test.add("Copenhagen, Denmark");
        test.add("Berlin, Germany");
        test.add("Warsaw, Poland");
        test.add("Amsterdam, Netherlands");
        test.add("Rome, Italy");
        test.add("Lisbon, Portugal");



        TravelingSalesman delivery = new TravelingSalesman("Oslo, Norway"); //Start and end point

        ArrayList<double[]> positions = delivery.createPositionsArray(test);
        ArrayList<double[]> route = new ArrayList<double[]>();

        delivery.bruteForceFindBestRoute(route, positions);

        ArrayList<String> result = delivery.positionsToAdresses(delivery.getBestRoute(), test);

        for(String res : result){
            System.out.println(res);
        }
        */
    }
}
