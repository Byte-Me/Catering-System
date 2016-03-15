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
public class AddRecipe extends JFrame {
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

    public AddRecipe(Container parent) {
        setContentPane(mainPane);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainPane.getRootPane().setDefaultButton(addRecipeButton);
        pack();
        setLocationRelativeTo(parent);

        String[] ingredientHeader = {"Ingredient", "Quantity", "Unit"};

        inStorageModel = new DefaultTableModel(); // Model of the table
        inRecipeModel = new DefaultTableModel(); // Model of the table

        inStorageModel.setColumnIdentifiers(ingredientHeader);
        inRecipeModel.setColumnIdentifiers(ingredientHeader);

        inStorageTable.setModel(inStorageModel); // Add model to table
        inRecipeTable.setModel(inRecipeModel); // Add model to table

        inStorageTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        updateIngredients();

        moveLeft.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                copyPasteData(inStorageTable.getSelectedRow(), true);

            }
        });

        moveRight.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                copyPasteData(inRecipeTable.getSelectedRow(), false);
            }
        });

        addRecipeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                foodManagement = new FoodManagement();
                String recipeName = JOptionPane.showInputDialog(null, "Name of recipe: ");
                ArrayList<Object[]> ingInfo = new ArrayList<Object[]>();
                for(int i = 0; i < inRecipeTable.getRowCount(); i++) {
                    Object[] obj = new Object[2];
                    obj[0] = inRecipeTable.getValueAt(i, 0);
                    obj[1] = inRecipeTable.getValueAt(i, 1);
                    System.out.println(Arrays.toString(obj));
                    ingInfo.add(obj);
                }
                if(foodManagement.addRecipe(recipeName, ingInfo)) {
                    JOptionPane.showMessageDialog(null, "Success!");
                } else {
                    JOptionPane.showMessageDialog(null, "Error. Try again!");
                }
                setVisible(false);
                dispose();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });

        setVisible(true);
    }

    //FIXME
    private void copyPasteData(int index, boolean left) {
        try {
            Object[] ingredient = new Object[3];
            if (left) {
                int unitsInRecipe = Integer.parseInt(JOptionPane.showInputDialog(null, "How many units?"));
                ingredient[0] = inStorageTable.getValueAt(index, 0);
                ingredient[1] = unitsInRecipe;
                ingredient[2] = inStorageTable.getValueAt(index, 2);
                inRecipeModel.addRow(ingredient);
            } else {
                inRecipeModel.removeRow(index);
            }
        } catch (Exception e){}
    }

    private boolean existsInRecipe(JTable table, Object[] entry) {
        // Get row and column count
        int rowCount = table.getRowCount();
        int colCount = table.getColumnCount();

        // Get Current Table Entry
        String curEntry = "";
        for (Object o : entry) {
            String e = o.toString();
            curEntry = curEntry + " " + e;
        }

        // Check against all entries
        for (int i = 0; i < rowCount; i++) {
            String rowEntry = "";
            for (int j = 0; j < colCount; j++)
                rowEntry = rowEntry + " " + table.getValueAt(i, 0).toString();
            if (rowEntry.equalsIgnoreCase(curEntry)) {
                return true;
            }
        }
        return false;
    }

    public static void updateIngredients() {
        FoodManagement foodManagement = new FoodManagement();
        ArrayList<Object[]> ingredients = foodManagement.getIngredients();

        for(int i = 0; i < inStorageModel.getRowCount(); i++) {
            inStorageModel.removeRow(i);
        }

        for (Object[] ingredient : ingredients) {
            inStorageModel.addRow(ingredient);
        }
    }
}
