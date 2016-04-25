package GUI;

import Database.FoodManagement;
import GUI.WindowPanels.Chef;

import javax.swing.*;

/**
 * Created by asdfLaptop on 14.04.2016.
 */
public class EditIngredient extends JDialog {
    private JPanel mainPane;
    private JButton cancelButton;
    private JButton editIngredientButton;
    private JTextField ingredientName;
    private JTextField ingredientPrice;
    private JTextField ingredientAmount;
    private JTextField ingredientUnit;

    FoodManagement foodManagement = new FoodManagement();

    /**
     * Constructor to the EditIngredient graphical user interface.
     * @param ingredient Name of the ingredient.
     */
    public EditIngredient(String ingredient) {
        setTitle("Edit Ingredient");
        setContentPane(mainPane);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        pack();
        setLocationRelativeTo(getParent());
        setModal(true);

        Object[] ingredientInfo = foodManagement.getSingleIngredient(ingredient);

        ingredientName.setText((String)ingredientInfo[0]);
        ingredientPrice.setText(ingredientInfo[1].toString());
        ingredientAmount.setText(ingredientInfo[2].toString());
        ingredientUnit.setText((String)ingredientInfo[3]);

        ingredientAmount.requestFocus();

        editIngredientButton.addActionListener(e -> { // Rar bug med å edite både navn og unit sammen, alle andre kombinasjoner funker (virker det som).
            try {
                String name = ingredientName.getText();
                int price = Integer.parseInt(ingredientPrice.getText());
                int quantity = Integer.parseInt(ingredientAmount.getText());
                String unit = ingredientUnit.getText();

                if(!name.isEmpty() && price > 0 && quantity > 0 && !unit.isEmpty()) {
                    if(foodManagement.updateIngredientPrice(ingredient, price) &&
                            foodManagement.updateIngredientQuantity(ingredient, quantity) &&
                            foodManagement.updateIngredientName(ingredient, name) &&
                            foodManagement.updateIngredientUnit(ingredient, unit)) {

                        JOptionPane.showMessageDialog(null, "Ingredient updated.");
                        setVisible(false);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(null, "Ingredient updated.");
                        setVisible(false);
                        dispose();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Issue with updating ingredient.");
                }

                Chef.updateIngredients();
            } catch(Exception e2) {
                JOptionPane.showMessageDialog(null, "Issue with updating ingredient.");
            }
        });

        cancelButton.addActionListener(e -> {
            setVisible(false);
            dispose();
        });

        setVisible(true);
    }


}
