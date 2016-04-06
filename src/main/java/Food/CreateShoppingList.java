package Food;

/**
 * Created by Evdal on 07.03.2016.
 */

import Database.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CreateShoppingList {
    public static ArrayList<Object[]> useOrdersToday(){

        FoodManagement food = new FoodManagement();
        ArrayList<Object[]> ingredientsNeeded = food.getRecipeIngredients(); // 0 is name, 1 is amount, 2 is unit and 3 is price.
        ingredientsNeeded = shortenArrayList(ingredientsNeeded);
        ArrayList<String> names = new ArrayList<>();

        names.addAll(ingredientsNeeded.stream().map(ing -> (String) ing[0]).collect(Collectors.toList()));
        ArrayList<Object[]> inStorage = food.getIngredientsInStorage(names); //0 is name, 1 is quantity in storage.

        ArrayList<Object[]> out = new ArrayList<>();

        for(int i = 0; i<ingredientsNeeded.size();i++){
                if (ingredientsNeeded.get(i)[0].equals(inStorage.get(i)[0])) { //if name == name
                    System.out.println(ingredientsNeeded.get(i)[1]);

                    if ((Integer) ingredientsNeeded.get(i)[1] > (Integer) inStorage.get(i)[1]) {
                        Object[] obj = new Object[4];
                        obj[0] = ingredientsNeeded.get(i)[0]; //name of ingredient
                        int neededQuant = (Integer) ingredientsNeeded.get(i)[1] - (Integer) inStorage.get(i)[1];
                        obj[1] = neededQuant; // quantity needed
                        obj[2] = ingredientsNeeded.get(i)[2];
                        obj[3] = neededQuant * (Integer) ingredientsNeeded.get(i)[3]; //
                        out.add(obj); //Sender ikke ut shoppingitem om det er nok igjen på storage.

                    }
                }
        }
        for(Object[] shoppingItem : out){
            System.out.println(Arrays.toString(shoppingItem));
        }
        return out;
    }
    private static ArrayList<Object[]> shortenArrayList(ArrayList<Object[]> in){
        ArrayList<Object[]> out = new ArrayList<>();
        for(int i=0; i<in.size();i++){
            boolean flag = true;
            for(Object[] tmp : out){
                if(tmp[0].equals(in.get(i)[0])) flag = false;
            }
            if(flag) {
                Object[] obj = new Object[4];
                obj[0] = in.get(i)[0];
                int sum = 0;
                obj[2] = in.get(i)[2];
                obj[3] = in.get(i)[3];
                for (int j = i; j < in.size(); j++) {
                    if (in.get(i)[0].equals(in.get(j)[0])) {
                        sum += (Integer)in.get(j)[1];
                    }
                }
                obj[1] = sum;
                out.add(obj);
            }

        }

        return out;
    }

}
