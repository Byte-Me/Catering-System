package Food;

import Database.FoodManagement;
import Database.OrderManagement;

import java.util.ArrayList;

/**
 * Created by Evdal on 15.03.2016.
 */
public class Storage {

    // kan sende inn hele shoppinglisten!!!
    private static OrderManagement orderManagement = new OrderManagement();
    private static FoodManagement foodManagement = new FoodManagement();


    public static boolean removeFromStorage(int orderID){
        ArrayList<Object[]> recipeInfo = orderManagement.getRecipesFromOrder(orderID); //0 = name, 1 = portion, 2 = id
        //get recipes
        for(Object[] recipe : recipeInfo){
            //get ingredients in recipe
            ArrayList<Object[]> ingredients = foodManagement.getRecipeIngredients((Integer) recipe[2]); //1 = amount, 3 = id
            for(Object[] ing : ingredients){
                //remove ingredient, subtractedValue = amount*portion
                if(!foodManagement.removeIngredientFromStorage((Integer)ing[3], (Integer)ing[1]*(Integer)recipe[1])){
                    return false;
                }
            }

        }
        return true;
    }
}
