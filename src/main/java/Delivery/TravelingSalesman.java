package Delivery;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Evdal on 07.03.2016.
 */

/*
Eksample program:

public static void main(String[]args) throws Exception{

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

        bruteForceFindBestRoute(route, positions);

        ArrayList<String> result = positionsToAdresses(delivery.getBestRoute(), test);

        for(String res : result){
            System.out.println(res);
        }
    }

 */

public class TravelingSalesman {

    private ArrayList<double[]> bestRoute;
    private double[] startAdress;
    private String startPoint;

    public TravelingSalesman(String startAdress) throws Exception {

        this.startAdress = geoCoder(startAdress, 0);
        startPoint = startAdress;


    }

    //Hentet en del fra http://stackoverflow.com/questions/11703827/brute-force-algorithm-for-the-traveling-salesman-problem-in-java

    //r is current route, citiesNotInRoute are the remaining cities not added to route.
    public void bruteForceFindBestRoute
    (ArrayList<double[]> r,
     ArrayList<double[]> citiesNotInRoute) throws Exception {

        //checks wether there are cities not yet added to route.
        if (!citiesNotInRoute.isEmpty()) {
            //goes through the routes not added.
            for (int i = 0; i < citiesNotInRoute.size(); i++) {

                System.out.println("Route = "+r.size()+", NotInRoute = "+citiesNotInRoute.size());
                double[] justRemoved = citiesNotInRoute.remove(0);
                ArrayList<double[]> newRoute = (ArrayList<double[]>) r.clone();
                newRoute.add(justRemoved);

                bruteForceFindBestRoute(newRoute, citiesNotInRoute);
                citiesNotInRoute.add(justRemoved);
            }
        } else //if(citiesNotInRoute.isEmpty())
        {
            r.add(0, startAdress);
            r.add(startAdress);
            if (sumDistance(bestRoute) > sumDistance(r)) {
                bestRoute = r;


            }
        }


    }

    public double sumDistance(ArrayList<double[]> positions) {
        if (positions == null) return 99999.9;                   //A better solution may be wanted.
        ArrayList<Double> lengths = distancesInKM(positions);
        double sum = 0;
        for (double length : lengths) {
            sum += length;
        }
        return sum;
    }


    private ArrayList distancesInKM(ArrayList<double[]> positions) {
        ArrayList ut = new ArrayList();
        for (int i = 0; i < positions.size() - 1; i++) {
            ut.add(haversine(positions.get(i)[0], positions.get(i)[1],
                    positions.get(i + 1)[0], positions.get(i + 1)[1]));
        }
        return ut;
    }

    //Haversine function retrieved from the internet.
    public static final double R = 6372.8; // In kilometers

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }


    private double[] geoCoder(String adress, int index){
        double[] out = new double[3];
        final Geocoder geocoder = new Geocoder();
        GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(adress).getGeocoderRequest();
        double latitude;
        double longitude;
        GeocodeResponse geocoderResponse;

        try {
            geocoderResponse = geocoder.geocode(geocoderRequest);
            List<GeocoderResult> results = geocoderResponse.getResults();
            latitude = results.get(0).getGeometry().getLocation().getLat().doubleValue();
            longitude = results.get(0).getGeometry().getLocation().getLng().doubleValue();
        }
        catch (IOException ioe){
            System.err.println("Issue with IO.");
            return null;

       } catch (Exception e) {
            System.err.println("Issue with geocoding the string: " + adress + ". Try checking for typos.");
            return null;
        }
        out[0] = latitude;
        out[1] = longitude;
        out[2] = index;
        return out;
    }
    /*
        1. Brukere
        2. Kunder
        3. Ordre
        4. Ingredienser
        5. Oppskrifter
        6.
     */

    //Returns null if there is an issue with geocoding adresses. This needs too be handled elsewhere.
    public ArrayList<double[]> createPositionsArrayShortened(ArrayList<String> adresses){

        ArrayList<String> fix = new ArrayList<>();

        for (String address : adresses) {
            if (!fix.contains(address)) {
                fix.add(address);
            }
        }

        ArrayList<double[]> out = new ArrayList<>();

        for (int i = 0; i < fix.size(); i++) {
            try {
                out.add(geoCoder(fix.get(i), i));
            }
            catch (Exception e){
                System.err.println("Issue with GeoCoding: " + fix.get(i));
                return null;
            }
        }
        return out;

    }
    public ArrayList<double[]> createPositionsArray(ArrayList<String> adresses){



        ArrayList<double[]> out = new ArrayList<>();

        for (int i = 0; i < adresses.size(); i++) {
            try {
                out.add(geoCoder(adresses.get(i), i));
            }
            catch (Exception e){
                System.err.println("Issue with GeoCoding: " + adresses.get(i));
                return null;
            }
        }

        return out;

    }
    public ArrayList<String> positionsToAdressesShortened(ArrayList<double[]> inPositions,
                                                 ArrayList<String> originAdressList) {
        ArrayList<String> fix = new ArrayList<>();
        for (String a : originAdressList) {
            if (!fix.contains(a)) {
                fix.add(a);
            }
        }
        return positionsToAdresses(inPositions,fix);
    }
    public ArrayList<String> positionsToAdresses(ArrayList<double[]> inPositions,
                                                 ArrayList<String> originAdressList) {

        ArrayList<String> out = new ArrayList<>();
        ArrayList<double[]> positions = (ArrayList<double[]>) inPositions.clone();
        positions.remove(0);
        positions.remove(positions.size() - 1);
        positions.trimToSize();
    //    out.add(startPoint);
        for (double[] pos : positions) {
            out.add(originAdressList.get((int) pos[2])); //TODO: STARTPOINT, IS IT NEEDED?
        }
 //       out.add(startPoint);
        return out;

    }

    public String getStartAdress() {
        return startPoint;
    }

    public void setStartAdress(String startAdress) {
        this.startPoint = startAdress;
    }

    public ArrayList<double[]> getBestRoute() {
        return bestRoute;
    }
}
