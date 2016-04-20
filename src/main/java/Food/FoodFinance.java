package Food;

import Database.FinanceManagement;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Evdal on 14.04.2016.
 */
public class FoodFinance {
    private static FinanceManagement financeManagement = new FinanceManagement();
    public static double findOrderPrice(int orderId){
        double out = 0;
        ArrayList<Object[]> recipePrices = financeManagement.getOrderRecipeInfo(orderId);
        for(Object[] recipe : recipePrices){
            out += Double.valueOf((Integer)recipe[0])* (Double)recipe[1];
        }
        return out;
    }
}
