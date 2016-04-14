package GUI.WindowPanels;

import Database.FoodManagement;
import Database.OrderManagement;
import Database.UserManagement;
import GUI.*;
import HelperClasses.*;

import static Database.OrderManagement.OrderType;
import static javax.swing.JOptionPane.*;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * Created by olekristianaune on 13.03.2016.
 */
public class Chef {

    static DefaultTableModel prepareModel;
    static DefaultTableModel ingredientModel;
    private OrderManagement orderManagement = new OrderManagement();
    static FoodManagement foodManagement = new FoodManagement();

    public Chef(JTable prepareTable, JTable ingredientTable, JButton generateShoppingListButton, JButton recipesButton, JButton addIngredientButton, JButton editIngredientButton) {

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

        String[] prepareHeader = {"Order ID","Recipe", "Amount", "Time", "Notes", "Status", ""}; // Header titles
        String[] ingredientHeader = {"Ingredient", "Quantity", "Unit"}; // Header titles

        ingredientModel = new MainTableModel();
        prepareModel = new MainTableModel();

        prepareModel.setColumnIdentifiers(prepareHeader); // Add header to columns
        ingredientModel.setColumnIdentifiers(ingredientHeader); // Add header to columns

        prepareTable.setModel(prepareModel); // Add model to table
        ingredientTable.setModel(ingredientModel); // Add model to table
        ingredientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ingredientTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    String ingredient = (String)ingredientModel.getValueAt(ingredientTable.getSelectedRow(), 0);
                    new EditIngredient(ingredient);
                }
            }
        });

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
