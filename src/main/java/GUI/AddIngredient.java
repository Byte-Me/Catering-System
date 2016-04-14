package GUI;

import Database.FoodManagement;
import GUI.WindowPanels.Chef;
import HelperClasses.MainTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by asdfLaptop on 15.03.2016.
 */
public class AddIngredient extends JDialog {
    private JPanel mainPane;
    private JButton cancelButton;
    private JButton addIngredientsButton;
    private JTextField ingName;
    private JTextField quantity;
    private JTextField unit;
    private JTable inStorageTable;
    private JTable toAddTable;
    private JScrollPane inStorage;
    private JScrollPane toAdd;
    private JButton addButton;
    private JTextField price;
    private JButton deleteButton;

    private static DefaultTableModel inStorageModel;
    private static DefaultTableModel toAddModel;

    FoodManagement foodManagement;

    public AddIngredient() {
        setTitle("New Ingredient");
        setContentPane(mainPane);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Images/icon32.png"));
        setIconImage(icon);

        foodManagement = new FoodManagement();

        String[] ingredientHeader1 = {"Ingredient", "Quantity", "Unit"};
        String[] ingredientHeader2 = {"Ingredient", "Quantity", "Unit", "Price"};

        inStorageModel = new MainTableModel(); // Model of the table
        toAddModel = new DefaultTableModel(); // Model of the table

        inStorageModel.setColumnIdentifiers(ingredientHeader1);
        toAddModel.setColumnIdentifiers(ingredientHeader2);

        inStorageTable.setModel(inStorageModel); // Add model to table
        toAddTable.setModel(toAddModel); // Add model to table

        updateIngredients();

        addButton.addActionListener(e -> {
            try {
                String nameText = ingName.getText();
                int quantityText = Integer.parseInt(quantity.getText());
                String unitText = unit.getText();
                int priceText = Integer.parseInt(price.getText());

                Object[] ingInfo = new Object[4];
                ingInfo[0] = nameText;
                ingInfo[1] = quantityText;
                ingInfo[2] = unitText;
                ingInfo[3] = priceText;

                if(nameText != null && unitText != null && quantityText > 0 && priceText > 0 && !existsInTable(toAddTable, nameText)) {
                    toAddModel.addRow(ingInfo);
                    ingName.setText(null);
                    quantity.setText(null);
                    unit.setText(null);
                    price.setText(null);
                } else {
                    wrongInputNumber(quantity);
                    wrongInputNumber(price);
                    JOptionPane.showMessageDialog(null, "Error!\n1. All fields must be filled. \n2. Units and price must be positive numbers.\n3. Two ingredients with the same name can\n not be added.\n(Edit the quantity instead!)");
                }
            } catch(Exception e1) {}
        });

        deleteButton.addActionListener(e -> {
            if (toAddTable.getSelectedRow() >= 0) {
                toAddModel.removeRow(toAddTable.getSelectedRow());
            }
        });

        addIngredientsButton.addActionListener(e -> {
            boolean ok = false;
            String ingredientsAdded = "";
            // ArrayList<Object[]> ingToBeAdded = new ArrayList<Object[]>();
            for(int i = 0; i < toAddTable.getRowCount(); i++) {
                Object[] obj = new Object[4];
                obj[0] = toAddTable.getValueAt(i, 0);
                obj[1] = toAddTable.getValueAt(i, 1);
                obj[2] = toAddTable.getValueAt(i, 2);
                obj[3] = toAddTable.getValueAt(i, 3);

                if(toAddTable.getRowCount() > 0 && foodManagement.addIngredient((String)obj[0], (Integer)obj[1], (String)obj[2], (Integer)obj[3])) {
                    ok = true;
                    ingredientsAdded += obj[0] + "\n";
                }
            }
            if (!ok) {
                JOptionPane.showMessageDialog(null, "Error, try again!");
            } else {
                JOptionPane.showMessageDialog(null,"Added successfully:\n" + ingredientsAdded);
                Chef.updateIngredients();
                setVisible(false);
                dispose();
            }
        });

        cancelButton.addActionListener(e -> {
            AddIngredient.this.setVisible(false);
            AddIngredient.this.dispose();
        });

        pack();
        setLocationRelativeTo(getParent());
        setModal(true);
        setVisible(true);
    }

    public static void updateIngredients() {
        FoodManagement foodManagement = new FoodManagement();
        ArrayList<Object[]> ingredients = foodManagement.getIngredients();

        inStorageModel.setRowCount(0);

        for (Object[] ingredient : ingredients) {
            inStorageModel.addRow(ingredient);
        }
    }

    private boolean existsInTable(JTable table, String name) {
        // Get row count
        int rowCount = table.getRowCount();

        // Check against all entries
        for (int i = 0; i < rowCount; i++) {
            if (table.getValueAt(i, 0).toString().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private void wrongInputNumber(JTextField tf) {
        int i = Integer.parseInt(tf.getText());
        if(i <= 0) {
            tf.setText(null);
        }
    }
}
