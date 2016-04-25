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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;

import static Database.OrderManagement.OrderType;
import static javax.swing.JOptionPane.*;

/**
 * Created by magnusfn on 13.03.2016.
 */
public class Chef {
    static DefaultTableModel prepareModel;
    static DefaultTableModel ingredientModel;
    private OrderManagement orderManagement = new OrderManagement();
    static FoodManagement foodManagement = new FoodManagement();

    /**
     * Construrctor for the window panel Chef.
     * @param prepareTable                  JTable with all orders that are ready to prepare.
     * @param ingredientTable               JTable with ingredients.
     * @param generateShoppingListButton    JButton that generates a shopping list.
     * @param recipesButton                 JButton that shows the recipes.
     * @param addIngredientButton           JButton that opens a new ingredient interface.
     * @param editIngredientButton          JButton that opens a edit ingredient interface.
     * @param chefSplitPane                 JSplitPane that contains both prepareTable and ingredientTable.
     */
    public Chef(JTable prepareTable, JTable ingredientTable, JButton generateShoppingListButton, JButton recipesButton, JButton addIngredientButton, JButton editIngredientButton, JSplitPane chefSplitPane) {
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
        prepareTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ingredientTable.setAutoCreateRowSorter(true);

        chefSplitPane.setResizeWeight(0.50);

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
            catch (IndexOutOfBoundsException iobe){
                showMessageDialog(null, "An ingredient needs to be selected.");
            }
        });

        generateShoppingListButton.addActionListener(e -> new GenerateShoppingList());

        // Right Click Menu
        JPopupMenu popupMenu = new JPopupMenu("Ingredients");
        popupMenu.add(new AbstractAction("New Ingredient") {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddIngredient();
            }
        });
        popupMenu.add(new AbstractAction("Edit Ingredient") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ingredientTable.getSelectedRow() != -1) {
                    String ingredient = (String) ingredientTable.getValueAt(ingredientTable.getSelectedRow(), 0);
                    new EditIngredient(ingredient);
                }
            }
        });

        // mouse listener
        ingredientTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                int r = ingredientTable.rowAtPoint(e.getPoint());
                if (r >= 0 && r < ingredientTable.getRowCount()) {
                    ingredientTable.setRowSelectionInterval(r, r);
                } else {
                    ingredientTable.clearSelection();
                }

                int rowindex = ingredientTable.getSelectedRow();
                if (rowindex < 0)
                    return;

                if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {

                int r = ingredientTable.rowAtPoint(e.getPoint());
                if (r >= 0 && r < ingredientTable.getRowCount()) {
                    ingredientTable.setRowSelectionInterval(r, r);
                } else {
                    ingredientTable.clearSelection();
                }

                int rowindex = ingredientTable.getSelectedRow();
                if (rowindex < 0)
                    return;

                if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    String ingredient = (String)ingredientModel.getValueAt(ingredientTable.getSelectedRow(), 0);
                    new EditIngredient(ingredient);
                }
            }
        });

        prepareTable.getSelectionModel().addListSelectionListener(e -> {
            try {
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

        //update order process
        prepareModel.addTableModelListener(e ->{
            int count = 0;
            boolean lookingForOrder = true;

            while(count < prepareTable.getRowCount() && lookingForOrder){ //blar gjennom radene
                //finds amount of orders with same order id
                int orderId = -1;
                if((Boolean)prepareTable.getValueAt(count, 6)){ //er checkbox checket?

                    //get ids
                    orderId = (Integer)prepareTable.getValueAt(count, 0);
                    int recipeId = foodManagement.getRecipeIDPub((String)prepareModel.getValueAt(count,1));

                    //update orderrecipe
                    int orderCount = 0;
                    int processingCount = 0;

                    //updates table
                    updatePrepareTable();

                    //checks wether all rows with same id is checked.
                    for(int i = 0; i<prepareModel.getRowCount();i++){
                        if(orderId == (Integer)prepareTable.getValueAt(i, 0)){
                            orderCount++;
                        }
                        if(FoodManagement.OrderRecipeStatus.PROCESSING == prepareTable.getValueAt(i, 5)){
                            processingCount++;
                        }
                    }
                    if(orderCount-1 == processingCount && prepareTable.getValueAt(count, 5)!= FoodManagement.OrderRecipeStatus.PROCESSING){
                        int input = showConfirmDialog(null,"Do you want set order " +prepareTable.getValueAt(count, 0)+" as ready for delivery?","",YES_NO_OPTION);
                        if(input==YES_OPTION) {
                            orderManagement.updateStatus((Integer) prepareTable.getValueAt(count, 0), OrderType.READY.getValue());
                            lookingForOrder = false;
                        }
                        else{
                            prepareTable.setValueAt(false, count, 6);
                        }
                    }
                    else {
                        foodManagement.updateOrderRecipeStatus(orderId, recipeId, FoodManagement.OrderRecipeStatus.PROCESSING.getValue());
                    }
                    updatePrepareTable();
                }
                count++;
            }
        });


    }

    /**
     * Updates the prepare table from the database.
     */
    public static void updatePrepareTable() {
        FoodManagement foodManagement = new FoodManagement();
        ArrayList<Object[]> recipes = foodManagement.getRecipesForChef();

        prepareModel.setRowCount(0);

        for (Object[] row : recipes) {
            row[6] = false;
            prepareModel.addRow(row);
        }

    }

    /**
     * Updates the ingredient table from the database.
     */
    public static void updateIngredients() {
        FoodManagement foodManagement = new FoodManagement();
        ArrayList<Object[]> ingredients = foodManagement.getIngredients();

        ingredientModel.setRowCount(0);

        for (Object[] ingredient : ingredients) {
            ingredientModel.addRow(ingredient);
        }
    }
    
}
