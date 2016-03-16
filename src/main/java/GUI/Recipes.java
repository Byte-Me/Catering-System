package GUI;

import Database.FoodManagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by asdfLaptop on 16.03.2016.
 */
public class Recipes extends JFrame {
    private JPanel mainPane;
    private JTable recipesTable;
    private JScrollPane recipes;
    private JButton cancelButton;
    private JButton editRecipeButton;
    private JButton addRecipeButton;

    static DefaultTableModel recipesModel;

    FoodManagement foodManagement = new FoodManagement();

    public Recipes(Container parent) {
        setContentPane(mainPane);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainPane.getRootPane().setDefaultButton(addRecipeButton);
        pack();
        setLocationRelativeTo(parent);

        String[] recipeHeader = {"ID", "Name"};

        recipesModel = new DefaultTableModel(); // Model of the table

        recipesModel.setColumnIdentifiers(recipeHeader);

        recipesTable.setModel(recipesModel); // Add model to table

        updateRecipes();

        addRecipeButton.addActionListener(new ActionListener() { // Button action listener
            public void actionPerformed(ActionEvent e) {
                new AddRecipe(mainPane.getParent());
            }
        });

        editRecipeButton.addActionListener(new ActionListener() { // Button action listener
            public void actionPerformed(ActionEvent e) {
                new EditRecipe(mainPane.getParent());
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

    public void updateRecipes() {
        ArrayList<Object[]> recipes = foodManagement.getRecipes();

        for(int i = 0; i < recipesModel.getRowCount(); i++) {
            recipesModel.removeRow(i);
        }

        for (Object[] recipe : recipes) {
            recipesModel.addRow(recipe);
        }
    }
}
