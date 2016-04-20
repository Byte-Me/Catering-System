package GUI.WindowPanels;

import Database.FoodManagement;
import Database.OrderManagement;
import Food.Storage;
import GUI.AddIngredient;
import GUI.EditIngredient;
import GUI.GenerateShoppingList;
import GUI.Recipes;
import HelperClasses.MainTableModel;
import HelperClasses.ToggleSelectionModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import static Database.OrderManagement.OrderType;
import static javax.swing.JOptionPane.*;

/**
 * Created by olekristianaune on 13.03.2016.
 */
public class Chef {

    static DefaultTableModel prepareModel;
    static DefaultTableModel ingredientModel;
    private OrderManagement orderManagement = new OrderManagement();
    static FoodManagement foodManagement = new FoodManagement();

    public Chef(JTable prepareTable, JTable ingredientTable, JButton generateShoppingListButton, JButton recipesButton, JButton addIngredientButton, JButton editIngredientButton) {
        String[] prepareHeader = {"Order ID","Recipe", "Amount", "Time", "Notes", "Status", "Update"}; // Header titles
        String[] ingredientHeader = {"Ingredient", "Quantity", "Unit"}; // Header titles

        ingredientModel = new MainTableModel();
        prepareModel = new DefaultTableModel(){
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 6)
                    return Boolean.class;
                return super.getColumnClass(columnIndex);
            }
            @Override
            public boolean isCellEditable(int row, int col) {
                return (col == 6);
            }
        }; // Model of the table

        prepareModel.setColumnIdentifiers(prepareHeader); // Add header to columns
        ingredientModel.setColumnIdentifiers(ingredientHeader); // Add header to columns

        prepareTable.setModel(prepareModel); // Add model to table
        ingredientTable.setModel(ingredientModel); // Add model to table
        ingredientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        prepareTable.setSelectionModel(new ToggleSelectionModel()); // Makes the list toggleable

        recipesButton.addActionListener(e -> new Recipes());

        addIngredientButton.addActionListener(e -> new AddIngredient());

        editIngredientButton.addActionListener(e ->{
            try {
                if (ingredientTable.getSelectedRows().length == 1 ) {
                    String ingredient = (String) ingredientTable.getValueAt(ingredientTable.getSelectedRow(), 0);
                    new EditIngredient(ingredient);
                } else if(ingredientTable.getSelectedRows().length < 1){
                    showMessageDialog(null, "An ingredient needs to be selected.");
                }
                else{
                    showMessageDialog(null, "Only one ingredient can be selected.");
                }
            }
            catch (IndexOutOfBoundsException iobe){ //Oppstår exception jeg ikke forstår, derfor bare catcher det.
                showMessageDialog(null, "An ingredient needs to be selected.");
            }

        });

        generateShoppingListButton.addActionListener(e -> new GenerateShoppingList());

        ingredientTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    String ingredient = (String)ingredientModel.getValueAt(ingredientTable.getSelectedRow(), 0);
                    new EditIngredient(ingredient);
                }
            }
        });

        prepareTable.getSelectionModel().addListSelectionListener(e -> {
            int count = 0;
            boolean lookingForOrder = true;

            while(count < prepareTable.getRowCount() && lookingForOrder){ //blar gjennom radene
                if((Boolean)prepareTable.getValueAt(count, 6)){ //er checkbox checket?

                    try {
                        int input = showConfirmDialog(null, "Do you want update status for orderID " + prepareTable.getValueAt(count, 0) + "?", "", YES_NO_OPTION);
                        if (input == YES_OPTION) {
                            if (OrderType.ACTIVE == prepareTable.getValueAt(count, 5)) { //er den aktiv, set til processsing
                                orderManagement.updateStatus((Integer) prepareTable.getValueAt(count, 0), OrderType.PROCESSING.getValue());

                            } else {//er den processing, set til ready
                                orderManagement.updateStatus((Integer) prepareTable.getValueAt(count, 0), OrderType.READY.getValue());
                                int input1 = showConfirmDialog(null, "Do you want automatically remove ingredients from storage?", "", YES_NO_OPTION);
                                if (input1 == YES_OPTION) {
                                    Storage.removeFromStorage((Integer) prepareTable.getValueAt(count, 0));
                                }


                            }
                            updatePrepareTable();
                            lookingForOrder = false;
                        } else {
                            prepareTable.setValueAt(false, count, 6);
                        }
                    }
                    catch (NumberFormatException ne){
                        //
                    }
                }
                count++;
            }            try {
                if (!e.getValueIsAdjusting()) { // Ensures value changed only fires once on change completed
                    if (prepareTable.getSelectionModel().isSelectionEmpty()) {
                        updateIngredients();
                    } else {
                        String recipe = (String)prepareModel.getValueAt(prepareTable.getSelectedRow(), 1);
                        int recipeId = foodManagement.getRecipeIDPub(recipe);
                        ArrayList<Object[]> recipeInfo = foodManagement.getRecipeIngredients(recipeId);

                        ingredientModel.setRowCount(0);

                        for (Object[] tmp : recipeInfo) {
                            ingredientModel.addRow(tmp);
                        }
                    }

                }
            } catch(Exception ignore) {
                System.err.println("Error with getting ingredients in recipe.");
                ignore.printStackTrace();
            }
        });

    }


    public static void updatePrepareTable() {
        FoodManagement foodManagement = new FoodManagement();
        ArrayList<Object[]> recipes = foodManagement.getRecipesForChef();
        prepareModel.setRowCount(0);
        for (Object[] row : recipes) {
            row[6] = false;
            prepareModel.addRow(row);
        }
    }

    public static void updateIngredients() {
        FoodManagement foodManagement = new FoodManagement();
        ArrayList<Object[]> ingredients = foodManagement.getIngredients();

        ingredientModel.setRowCount(0);

        for (Object[] ingredient : ingredients) {
            ingredientModel.addRow(ingredient);
        }
    }

}
