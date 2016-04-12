package GUI;

import Database.FoodManagement;

import javax.swing.*;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by asdfLaptop on 11.03.2016.
 */
public class AddRecipe extends JDialog {
    private JPanel mainPane;
    private JButton cancelButton;
    private JButton addRecipeButton;
    private JScrollPane inRecipe;
    private JScrollPane inStorage;
    private JButton moveRight;
    private JButton moveLeft;
    private JTable inStorageTable;
    private JTable inRecipeTable;

    private static DefaultTableModel inStorageModel;
    private static DefaultTableModel inRecipeModel;

    FoodManagement foodManagement;

    public AddRecipe() {
        setContentPane(mainPane);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(getParent());
        setModal(true);

        final int nameColumnNr = 0;
        final int quantityColumnNr = 1;

        String[] ingredientHeader = {"Ingredient", "Quantity", "Unit"};

        inStorageModel = new DefaultTableModel(); // Model of the table
        inRecipeModel = new DefaultTableModel(); // Model of the table

        inStorageModel.setColumnIdentifiers(ingredientHeader);
        inRecipeModel.setColumnIdentifiers(ingredientHeader);

        inStorageTable.setModel(inStorageModel); // Add model to table
        inRecipeTable.setModel(inRecipeModel); // Add model to table

        inStorageTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        updateIngredients();

        moveLeft.addActionListener(e -> copyPasteData(inStorageTable.getSelectedRow(), true));

        moveRight.addActionListener(e -> copyPasteData(inRecipeTable.getSelectedRow(), false));

        addRecipeButton.addActionListener(e -> {
            foodManagement = new FoodManagement();
            String recipeName = JOptionPane.showInputDialog(null, "Name of recipe: ");
            String priceIn = JOptionPane.showInputDialog(null, "Salesprice for "+recipeName+": ");
            int recipePrice = Integer.parseInt(priceIn);

            ArrayList<Object[]> ingInfo = new ArrayList<>();
            for(int i = 0; i < inRecipeTable.getRowCount(); i++) {
                Object[] obj = new Object[2];
                obj[0] = inRecipeTable.getValueAt(i, nameColumnNr);
                obj[1] = inRecipeTable.getValueAt(i, quantityColumnNr);
                ingInfo.add(obj);
            }

            if(foodManagement.addRecipe(recipeName, ingInfo, recipePrice) && recipeName != null && recipePrice > 0) {
                JOptionPane.showMessageDialog(null, "Success!");
                setVisible(false);
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Error. Try again!");
            }

        });

        cancelButton.addActionListener(e -> {
            setVisible(false);
            dispose();
        });

        setVisible(true);
    }

    private void copyPasteData(int index, boolean left) {
        try {
            Object[] ingredient = new Object[3];
            if (left && inStorageTable.getSelectedRow() >= 0) {
                int unitsInRecipe = Integer.parseInt(JOptionPane.showInputDialog(null, "How many units: "));
                ingredient[0] = inStorageTable.getValueAt(index, 0);
                ingredient[1] = unitsInRecipe;
                ingredient[2] = inStorageTable.getValueAt(index, 2);
                if (unitsInRecipe > 0 && !existsInTable(inRecipeTable, inStorageTable.getValueAt(index, 0).toString())) {
                    inRecipeModel.addRow(ingredient);
                } else {
                    JOptionPane.showMessageDialog(null, "1. Units must be positive numbers.\n2. Two ingredients with the same name can\n not be used in a recipe.\n(Edit the quantity instead!)");
                }
            } else {
                inRecipeModel.removeRow(index);
            }
        } catch (Exception e){}
    }

    public static boolean existsInTable(JTable table, String name) {
        // Get row count
        int rowCount = table.getRowCount();

        // Check against all entries
        for (int i = 0; i < rowCount; i++) {
            if (table.getValueAt(i, 0).toString() == name) {
                return true;
            }
        }
        return false;
    }

    public static void updateIngredients() {
        FoodManagement foodManagement = new FoodManagement();
        ArrayList<Object[]> ingredients = foodManagement.getIngredients();

        inStorageModel.setRowCount(0);

        for (Object[] ingredient : ingredients) {
            inStorageModel.addRow(ingredient);
        }
    }
}
