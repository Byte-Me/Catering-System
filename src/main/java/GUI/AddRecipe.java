package GUI;

import Database.FoodManagement;
import HelperClasses.MainTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

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
        setTitle("New Recipe");
        setContentPane(mainPane);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Images/icon32.png"));
        setIconImage(icon);

        final int nameColumnNr = 0;
        final int quantityColumnNr = 1;

        String[] ingredientHeader = {"Ingredient", "Quantity", "Unit"};

        inStorageModel = new MainTableModel(); // Model of the table
        inRecipeModel = new MainTableModel(); // Model of the table

        inStorageModel.setColumnIdentifiers(ingredientHeader);
        inRecipeModel.setColumnIdentifiers(ingredientHeader);

        inStorageTable.setModel(inStorageModel); // Add model to table
        inRecipeTable.setModel(inRecipeModel); // Add model to table

        inStorageTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        updateIngredients();

        moveLeft.addActionListener(e -> copyPasteData(inStorageTable.getSelectedRow(), true));

        moveRight.addActionListener(e -> copyPasteData(inRecipeTable.getSelectedRow(), false));

        inStorageTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    copyPasteData(inStorageTable.getSelectedRow(), true);
                }
            }
        });


        addRecipeButton.addActionListener(e -> {
            try {
                if (inRecipeTable.getRowCount() > 0) {
                    foodManagement = new FoodManagement();
                    int recipePrice = -1;
                    String recipeName = JOptionPane.showInputDialog(null, "Name of recipe: ");
                    if (!recipeName.isEmpty() && !recipeExists(recipeName)) {
                        String priceIn = JOptionPane.showInputDialog(null, "Salesprice for " + recipeName + ": ");
                        recipePrice = Integer.parseInt(priceIn);

                        ArrayList<Object[]> ingInfo = new ArrayList<>();
                        for(int i = 0; i < inRecipeTable.getRowCount(); i++) {
                            Object[] obj = new Object[2];
                            obj[0] = inRecipeTable.getValueAt(i, nameColumnNr);
                            obj[1] = inRecipeTable.getValueAt(i, quantityColumnNr);
                            ingInfo.add(obj);
                        }

                        if(foodManagement.addRecipe(recipeName, ingInfo, recipePrice) && !recipeName.isEmpty() && recipePrice > 0) {
                            JOptionPane.showMessageDialog(null, "Success!");
                            Recipes.updateRecipes();
                            setVisible(false);
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(null, "Error. Try again!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Input a recipe name, which also does not already exist.");
                    }

                }

            } catch(Exception e1){
                System.err.println("Error with adding recipe.");
            }

        });

        cancelButton.addActionListener(e -> {
            setVisible(false);
            dispose();
        });

        pack();
        setLocationRelativeTo(getParent());
        setModal(true);
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
                if (unitsInRecipe > 0 && existsInTable(inRecipeTable, inStorageTable.getValueAt(index, 0).toString()) == -1) {
                    inRecipeModel.addRow(ingredient);
                } else if (existsInTable(inRecipeTable, inStorageTable.getValueAt(index, 0).toString()) >= 0) {
                    int row = existsInTable(inRecipeTable, inStorageTable.getValueAt(index, 0).toString());
                    int currentPortions = (Integer)inRecipeTable.getValueAt(row, 1);
                    inRecipeModel.setValueAt((unitsInRecipe + currentPortions), row, 1);
                } else {
                    JOptionPane.showMessageDialog(null, "1. Units must be positive numbers.\n2. Two ingredients with the same name can\n not be used in a recipe.\n(Edit the quantity instead!)");
                }
            } else {
                inRecipeModel.removeRow(index);
            }
        } catch (Exception e){}
    }

    private int existsInTable(JTable table, String name) {
        // Get row count
        int rowCount = table.getRowCount();
        // Check against all entries
        for (int i = 0; i < rowCount; i++) {
            if (table.getValueAt(i, 0).toString().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    private boolean recipeExists(String name) {
        FoodManagement fm = new FoodManagement();
        ArrayList<Object[]> recipes = fm.getRecipes();
        for(int i = 0; i < recipes.size(); i++) {
            if(recipes.get(i)[1].equals(name)) {
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
