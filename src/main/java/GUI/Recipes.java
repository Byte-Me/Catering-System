package GUI;

import Database.FoodManagement;
import Util.HelperClasses.MainTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import static javax.swing.JOptionPane.showMessageDialog;

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

    static FoodManagement foodManagement = new FoodManagement();

    /**
     * Constructor for Recipes.
     */
    public Recipes() {
        setTitle("Recipes");
        setContentPane(mainPane);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Images/icon32.png"));
        setIconImage(icon);

        String[] recipeHeader = {"ID", "Name"};

        recipesModel = new MainTableModel();

        recipesModel.setColumnIdentifiers(recipeHeader);

        recipesTable.setModel(recipesModel); // Add model to table
        recipesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        updateRecipes();

        recipesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    int recipeId = (Integer)recipesModel.getValueAt(recipesTable.getSelectedRow(), 0);
                    new EditRecipe(recipeId);
                }
            }
        });

        addRecipeButton.addActionListener(e -> new AddRecipe());

        editRecipeButton.addActionListener(e ->{
            try {
                if (recipesTable.getSelectedRows().length == 1 ) { //TODO: sjekker ikke om flere columns er selected, velger øverste.
                    int recipeId = (Integer)recipesModel.getValueAt(recipesTable.getSelectedRow(), 0);
                    new EditRecipe(recipeId);
                } else if(recipesTable.getSelectedRows().length < 1){
                    showMessageDialog(null, "A recipe needs to be selected.");
                }
                else{
                    showMessageDialog(null, "Only one recipe can be selected.");
                }
            }
            catch (IndexOutOfBoundsException iobe){
                showMessageDialog(null, "A recipe needs to be selected.");
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

    /**
     * Util.Updates recipes.
     */
    public static void updateRecipes() {
        ArrayList<Object[]> recipes = foodManagement.getRecipes();

        recipesModel.setRowCount(0);

        for (Object[] recipe : recipes) {
            recipesModel.addRow(recipe);
        }
    }
}
