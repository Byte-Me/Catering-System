package Food;

import Database.FoodManagement;

import java.util.ArrayList;

/**
 * Created by Evdal on 15.03.2016.
 */
public class Storage {

    // kan sende inn hele shoppinglisten!!!
    public boolean removeFromStorage(ArrayList<Object[]> ing){ //tar inn liste med to objekter i hvert element. 0 = navn og 1 = amount;
        FoodManagement food = new FoodManagement();
        for(Object[] i : ing){
            Object[] tmp = new Object[]{i[0], i[1]}; //name , quantity
            if(!food.removeIngredientFromStorage((String)i[0], (Integer)i[1])){
                return false;
            }
        }
        return true;
    }
}
