package Delivery;

import Database.DeliveryManagement;
import org.apache.commons.dbutils.DbUtils;

import java.util.ArrayList;

/**
 * Created by Evdal on 14.03.2016.
 */
public class CreateDeliveryRoute {
    public static ArrayList<String> UseReadyOrders(String startAdress){
        ArrayList<String> out;
        try {
            TravelingSalesman tsp = new TravelingSalesman(startAdress);
            DeliveryManagement dm = new DeliveryManagement();
            ArrayList<String> adressNames = dm.getAdressReady();
            ArrayList<double[]> positions = tsp.createPositionsArray(adressNames);
            tsp.bruteForceFindBestRoute(new ArrayList<double[]>(), positions);
            out = tsp.positionsToAdresses(tsp.getBestRoute(), adressNames);
        }
        catch (Exception e){
            System.err.println("Issue with creating delivery route.");
            return null;
        }
        return out;
    }
}
