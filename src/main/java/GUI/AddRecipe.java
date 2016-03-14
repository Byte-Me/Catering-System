package GUI;

import Database.FoodManagement;

import javax.swing.*;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

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
        FoodManagement foodManagement = new FoodManagement();
        Object[] ingredient = new Object[3];
        int unitsInStorage = (Integer)inStorageTable.getValueAt(inStorageTable.getSelectedRow(), 1);
        if (left) {
            int unitsInRecipe = Integer.parseInt(JOptionPane.showInputDialog(null, "How many units?"));
            if (unitsInStorage >= unitsInRecipe) {
                ingredient[0] = inStorageTable.getValueAt(index, 0);
                ingredient[1] = unitsInRecipe;
                ingredient[2] = inStorageTable.getValueAt(index, 2);
                if (inRecipeTable.getValueAt(index, 0) != null || ingredient[0].equals(inRecipeTable.getValueAt(index, 0))) {
                    inRecipeTable.setValueAt(unitsInRecipe, index, 1);
                    inStorageTable.setValueAt((unitsInStorage - unitsInRecipe), index, 1);
                } else {
                    inRecipeModel.addRow(ingredient);
                }

            }
        } else {
            for(int i = 0; i < 3; i++) {
                ingredient[i] = inRecipeTable.getValueAt(index, i);
            }
            inStorageTable.setValueAt((unitsInStorage + ((Integer)inRecipeTable.getValueAt(index, 1))), index, 1);
            inRecipeModel.removeRow(inRecipeTable.getSelectedRow());

        }
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
