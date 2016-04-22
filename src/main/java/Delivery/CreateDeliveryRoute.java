package Delivery;


import Database.DeliveryManagement;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Evdal on 14.03.2016.
 */
public class CreateDeliveryRoute {
    public static ArrayList<Object[]> UseReadyOrders(String startAdress, ArrayList<String> adressNames){
        ArrayList<Object[]> out;
        DeliveryManagement dev = new DeliveryManagement();
        try {
            TravelingSalesman tsp = new TravelingSalesman(startAdress);
            DeliveryManagement dm = new DeliveryManagement();
        //    ArrayList<String> adressNames = dm.getAdressReady();
            ArrayList<double[]> positions = tsp.createPositionsArrayShortened(adressNames);
            tsp.bruteForceFindBestRoute(new ArrayList<>(), positions);
            ArrayList<String> tmp = tsp.positionsToAdressesShortened(tsp.getBestRoute(), adressNames);
            out = dev.getDeliveryInfo(tmp);
        }
        catch (Exception e){
            System.err.println("Issue with creating delivery route.");
            return null;
        }

        return out;
    }

    public static ArrayList<Object[]> orderListForTable(String startAdress, ArrayList<String> adressNames){
        ArrayList<Object[]> out;
        DeliveryManagement dev = new DeliveryManagement();
        try {
            TravelingSalesman tsp = new TravelingSalesman(startAdress);
          //  ArrayList<String> adressNames = dm.getAdressReady();
            System.out.println(adressNames);
            ArrayList<double[]> positions = tsp.createPositionsArrayShortened(adressNames);
            tsp.bruteForceFindBestRoute(new ArrayList<>(), positions);
            ArrayList<String> tmp = tsp.positionsToAdresses(tsp.getBestRoute(), adressNames);
            System.out.println(tmp);
            ArrayList<double[]> br = tsp.getBestRoute();
            out = dev.getDeliveryInfo(tmp);
        }
        catch (Exception e){
            System.err.println("Issue with creating delivery route.");
            return null;
        }

        return out;
    }


    public static ArrayList<double[]> UseReadyOrdersLatLng(String startAdress, ArrayList<String> adressNames){

        ArrayList<double[]> out;
        try {
            TravelingSalesman tsp = new TravelingSalesman(startAdress);
            DeliveryManagement dm = new DeliveryManagement();
       //     ArrayList<String> adressNames = dm.getAdressReady();
            ArrayList<double[]> positions = tsp.createPositionsArray(adressNames);
            tsp.bruteForceFindBestRoute(new ArrayList<>(), positions);
            out = tsp.getBestRoute();
        }
        catch (Exception e){
            System.err.println("Issue with creating delivery route.");
            return null;
        }
        return out;
    }
}
