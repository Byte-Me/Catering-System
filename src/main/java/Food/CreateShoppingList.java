package Food;

/**
 * Created by Evdal on 07.03.2016.
 */

import Database.*;

import java.util.ArrayList;

public class CreateShoppingList {
    public static ArrayList<Object[]> useOrdersToday(){
        FoodManagement food = new FoodManagement();
        ArrayList<Object[]> ingredientsNeeded = food.getRecipeIngredients(); // 0 is name, 1 is amount and 2 is price.
        ArrayList<String> names = new ArrayList<String>();
        for(Object[] ing : ingredientsNeeded){
            names.add((String)ing[0]);
        }
        ArrayList<Object[]> inStorage = food.getIngredientsInStorage(names); //0 is name, 1 is quantity in storage.

        ArrayList<Object[]> out = new ArrayList<Object[]>();

        for(int i = 0; i<ingredientsNeeded.size();i++){
            if(ingredientsNeeded.get(i)[0].equals(inStorage.get(i)[0])) { //if name == name
                if((Integer)ingredientsNeeded.get(i)[1] > (Integer)inStorage.get(i)[1]){
                    Object[] obj = new Object[3];
                    obj[0] = ingredientsNeeded.get(i)[0]; //name of ingredient
                    int neededQuant = (Integer)ingredientsNeeded.get(i)[1] - (Integer)inStorage.get(i)[1];
                    obj[1] = neededQuant; // quantity needed
                    obj[2] = neededQuant * (Integer)ingredientsNeeded.get(i)[2];
                    out.add(obj);
                }
            }
        }
        return out;
    }

}
