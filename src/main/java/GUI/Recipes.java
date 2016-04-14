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
public class Recipes extends JDialog {
    private JPanel mainPane;
    private JTable recipesTable;
    private JScrollPane recipes;
    private JButton cancelButton;
    private JButton editRecipeButton;
    private JButton addRecipeButton;

    static DefaultTableModel recipesModel;

    FoodManagement foodManagement = new FoodManagement();

    public Recipes() {
        setTitle("Recipes");
        setContentPane(mainPane);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Images/icon32.png"));
        setIconImage(icon);

        String[] recipeHeader = {"ID", "Name"};

        recipesModel = new DefaultTableModel(); // Model of the table

        recipesModel.setColumnIdentifiers(recipeHeader);

        recipesTable.setModel(recipesModel); // Add model to table

        updateRecipes();

        addRecipeButton.addActionListener(e -> new AddRecipe());

        editRecipeButton.addActionListener(e -> new EditRecipe());

        cancelButton.addActionListener(e -> {
            setVisible(false);
            dispose();
        });

        pack();
        setLocationRelativeTo(getParent());
        setModal(true);
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
