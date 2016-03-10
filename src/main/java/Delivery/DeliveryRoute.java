package Delivery;

/*

 * Created by Evdal on 03.03.2016.
 *
 * Returnerer automatisk til startadressen
*/

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DeliveryRoute {/*
    public static void main (String[] args)throws Exception{

        ArrayList<String> test = new ArrayList<String>();
        test.add("Oslo, Norway");
        test.add("Los Angeles, CA");
        test.add("Klæbuveien 126, Trondheim, Norway");
        test.add("Stavanger, Norway");
        test.add("Rønningsbakken 12, Trondheim, Norway");
        test.add("Bergen, Norway");


        ArrayList<double[]> temp = calculateOrder(createAdressArray(test));

        ArrayList<String> arranged = arrangeAdress(temp, test);
        ArrayList distArray = distancesInLanLat(temp);
        double sum = 0;
        System.out.println(arranged.get(0));
        for(int i = 1;i<arranged.size();i++){
            System.out.println(distArray.get(i-1));
            System.out.println(arranged.get(i));
        }


    }
*/

    public static double[] geoCoder(String adress, int index){
        double[] out = new double[3];
        final Geocoder geocoder = new Geocoder();
        GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(adress).getGeocoderRequest();
        GeocodeResponse geocoderResponse = null;
        try {
            geocoderResponse = geocoder.geocode(geocoderRequest);
        }
        catch (IOException ioe){
            System.err.println("Issue with geocoding adress: " + adress);
        }
        List<GeocoderResult> results = geocoderResponse.getResults();
        double latitude = results.get(0).getGeometry().getLocation().getLat().doubleValue();
        double longitude = results.get(0).getGeometry().getLocation().getLng().doubleValue();

        out[0] = latitude;
        out[1] = longitude;
        out[2] = index;
        return out;
    }

    //Hentet fra internet
    public static final double R = 6372.8; // In kilometers
    private static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }

    public static ArrayList<double[]> createAdressArray(ArrayList<String> adresses )throws Exception{
        ArrayList<double[]> out = new ArrayList<double[]>();

        for (int i= 0; i<adresses.size();i++){
            out.add(geoCoder(adresses.get(i), i));
        }
        return out;

    }

    public static ArrayList<double[]> calculateOrder(ArrayList<double[]> positions){
        ArrayList<Integer> indexed = new ArrayList<Integer>();
        ArrayList<double[]> out = new ArrayList<double[]>();
        int prevPos;
        int nextPos;
        indexed.add(0);
        out.add(positions.get(0));
        for (int i = 0; i<positions.size()-1;i++){
            prevPos = indexed.get(indexed.size()-1);

            double smallest = 99999.9;
            int smallestIndex = 0;
            nextPos = 0;

            for(int j=0; j <positions.size()-1;j++){

                if(prevPos == nextPos)nextPos++;

                double length = haversine(positions.get(prevPos)[0], positions.get(prevPos)[1],
                        positions.get(nextPos)[0], positions.get(nextPos)[1]);

                if(length < smallest && !indexed.contains(nextPos)) {
                    smallest = length;
                    smallestIndex = nextPos;
                }
                nextPos++;

            }
            indexed.add(smallestIndex);
            out.add(positions.get(smallestIndex));

        }
        return out;
    }
    private static ArrayList<String> arrangeAdress(ArrayList<double[]> posList, ArrayList<String> originAdressList){
        ArrayList<String> out = new ArrayList<String>();
        for(double[] pos : posList) {
            //Double d = new Double(pos[2]);
            out.add(originAdressList.get((int)pos[2]));
        }
        return out;

    }

    public static ArrayList distancesInLanLat(ArrayList<double[]> positions){
        ArrayList ut = new ArrayList();
        for(int i = 0; i<positions.size()-1;i++){
            ut.add(haversine(positions.get(i)[0], positions.get(i)[1],
                    positions.get(i+1)[0], positions.get(i+1)[1]));
        }
        return ut;
    }/*
    public static double sumDistance(ArrayList lengths){
        double sum = 0;
        for(int i=0;i<lengths.size();i++){
            sum += lengths.get(i);
        }
        return sum;
    }*/

}