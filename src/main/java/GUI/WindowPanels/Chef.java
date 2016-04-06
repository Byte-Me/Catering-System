package GUI.WindowPanels;

import Database.FoodManagement;
import GUI.AddIngredient;
import GUI.GenerateShoppingList;
import GUI.Recipes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by olekristianaune on 13.03.2016.
 */
public class Chef {

    DefaultTableModel prepareModel;
    static DefaultTableModel ingredientModel = new DefaultTableModel();

    FoodManagement foodManagement = new FoodManagement();

    public Chef(final JPanel mainPanel, JTable prepareTable, JTable ingredientTable, JButton generateShoppingListButton, JButton recipesButton, JButton addIngredientButton) {

        recipesButton.addActionListener(e -> new Recipes(mainPanel.getParent()));

        addIngredientButton.addActionListener(e -> new AddIngredient(mainPanel.getParent()));

        generateShoppingListButton.addActionListener(e -> new GenerateShoppingList(mainPanel.getParent()));

        String[] prepareHeader = {"Quantity", "Recipe", "Time", "Notes", "Ready for delivery"}; // Header titles
        String[] ingredientHeader = {"Ingredient", "Quantity", "Unit"}; // Header titles

        prepareModel = new DefaultTableModel() {
            public Class<?> getColumnClass(int columnIndex) {
                return getValueAt(0, columnIndex).getClass();
            }
        }; // Model of the table

        prepareModel.setColumnIdentifiers(prepareHeader); // Add header to columns
        ingredientModel.setColumnIdentifiers(ingredientHeader); // Add header to columns

        prepareTable.setModel(prepareModel); // Add model to table
        ingredientTable.setModel(ingredientModel); // Add model to table

        updateIngredients(); // FIXME: Remove this and make the tab autoUpdateable

    }

    public static void updateIngredients() {
        FoodManagement foodManagement = new FoodManagement();
        ArrayList<Object[]> ingredients = foodManagement.getIngredients();

        for(int i = 0; i < ingredientModel.getRowCount(); i++) {
            ingredientModel.removeRow(i);
        }

        for (Object[] ingredient : ingredients) {
            ingredientModel.addRow(ingredient);
        }
    }
}
