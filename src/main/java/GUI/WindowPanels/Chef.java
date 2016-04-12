package GUI.WindowPanels;

import Database.FoodManagement;
import Database.OrderManagement;
import Database.UserManagement;
import GUI.AddIngredient;
import GUI.GenerateShoppingList;
import GUI.Recipes;

import static Database.OrderManagement.OrderType;
import static javax.swing.JOptionPane.*;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
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
    private OrderManagement orderManagement = new OrderManagement();
    static FoodManagement foodManagement = new FoodManagement();

    public Chef(final JPanel mainPanel, JTable prepareTable, JTable ingredientTable, JButton generateShoppingListButton, JButton recipesButton, JButton addIngredientButton) {

        recipesButton.addActionListener(e -> new Recipes(mainPanel.getParent()));

        addIngredientButton.addActionListener(e -> new AddIngredient(mainPanel.getParent()));

        generateShoppingListButton.addActionListener(e -> new GenerateShoppingList(mainPanel.getParent()));

        String[] prepareHeader = {"Order ID","Recipe", "Amount", "Time", "Notes", "Status", ""}; // Header titles
        String[] ingredientHeader = {"Ingredient", "Quantity", "Unit"}; // Header titles

        prepareModel = new DefaultTableModel() {
           /* public Class<?> getColumnClass(int columnIndex) {
                return getValueAt(0, columnIndex).getClass();
            }*/
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

        updateIngredients(); // FIXME: Remove this and make the tab autoUpdateable

        updatePrepareTable();

        //update order process
        prepareModel.addTableModelListener(e ->{
            int count = 0;
            boolean lookingForOrder = true;

            while(count < prepareTable.getRowCount() && lookingForOrder){
                if((Boolean)prepareTable.getValueAt(count, 6)){

                    int input = showConfirmDialog(null,"Do you want update status for orderID " +prepareTable.getValueAt(count, 0)+"?","",YES_NO_OPTION);
                    if(input==YES_OPTION) {
                        if (OrderType.ACTIVE == prepareTable.getValueAt(count, 5)) {
                            orderManagement.updateStatus((Integer) prepareTable.getValueAt(count, 0), OrderType.PROCESSING.getValue());

                        } else {
                            orderManagement.updateStatus((Integer) prepareTable.getValueAt(count, 0), OrderType.READY.getValue());

                        }
                        updatePrepareTable();
                        lookingForOrder = false;
                    }
                    else{
                        prepareTable.setValueAt(false,count, 6);
                    }
                }
                count++;
            }
        });

    }


    public void updatePrepareTable() {
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
