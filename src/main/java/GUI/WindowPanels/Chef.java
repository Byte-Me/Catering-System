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
        prepareTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ingredientTable.setAutoCreateRowSorter(true);
        prepareTable.setAutoCreateRowSorter(true);

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
                if((Boolean)prepareTable.getValueAt(count, 6)){ //er checkbox checket?

                    int input = showConfirmDialog(null,"Do you want update status for orderID " +prepareTable.getValueAt(count, 0)+"?","",YES_NO_OPTION);
                    if(input==YES_OPTION) {
                        if (OrderType.ACTIVE == prepareTable.getValueAt(count, 5)) { //er den aktiv, sett til processsing
                            orderManagement.updateStatus((Integer) prepareTable.getValueAt(count, 0), OrderType.PROCESSING.getValue());

                        } else {//er den processing, set til ready
                            orderManagement.updateStatus((Integer) prepareTable.getValueAt(count, 0), OrderType.READY.getValue());

                        }
                        updatePrepareTable();
                        lookingForOrder = false;
                    }
                    else{
                        prepareTable.setValueAt(false, count, 6);
                    }
                }
                count++;
            }
        });

        //FIXME
        /*
        //Search prepare-table
        searchPrepare.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                searchFieldChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                searchFieldChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                searchFieldChange();
            }

            private void searchFieldChange() {
                String searchTerm = searchPrepare.getText();

                ArrayList<Object[]> searchResult = foodManagement.prepareSearch(searchTerm);

                updatePrepareTable(searchResult);
            }
        });
        */

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

    //FIXME
    /*
    public static void updateIngredients(ArrayList<Object[]> ingredients) {
        prepareModel.setRowCount(0);

        for (Object[] ingredient : ingredients) {
            prepareModel.addRow(ingredient);
        }
    }
    */

    //FIXME
    /*
    public static void updatePrepareTable(ArrayList<Object[]> recipes) {
        prepareModel.setRowCount(0);

        for (Object[] recipe : recipes) {
            recipe[6] = false;
            prepareModel.addRow(recipe);
        }
    }
    */

}
