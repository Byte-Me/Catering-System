package GUI;

import Database.FoodManagement;

import javax.swing.*;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.html.ObjectView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import static GUI.MainWindow.updateIngredients;

/**
 * Created by asdfLaptop on 15.03.2016.
 */
public class AddIngredient extends JFrame {
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

    private static DefaultTableModel inStorageModel;
    private static DefaultTableModel toAddModel;

    FoodManagement foodManagement;

    public AddIngredient(Container parent) {
        setContentPane(mainPane);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainPane.getRootPane().setDefaultButton(addButton);
        pack();
        setLocationRelativeTo(parent);

        foodManagement = new FoodManagement();

        String[] ingredientHeader = {"Ingredient", "Quantity", "Unit"};
        String[] ingredientAddHeader = {"Ingredient", "Quantity", "Unit", "Price"};

        inStorageModel = new DefaultTableModel(); // Model of the table
        toAddModel = new DefaultTableModel(); // Model of the table

        inStorageModel.setColumnIdentifiers(ingredientHeader);
        toAddModel.setColumnIdentifiers(ingredientAddHeader);

        inStorageTable.setModel(inStorageModel); // Add model to table
        toAddTable.setModel(toAddModel); // Add model to table

        updateIngredients();

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
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
            }
        });

        addIngredientsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean ok = false;
                // ArrayList<Object[]> ingToBeAdded = new ArrayList<Object[]>();
                for(int i = 0; i < toAddTable.getRowCount(); i++) {
                    Object[] obj = new Object[4];
                    obj[0] = toAddTable.getValueAt(i, 0);
                    obj[1] = toAddTable.getValueAt(i, 1);
                    obj[2] = toAddTable.getValueAt(i, 2);
                    obj[3] = toAddTable.getValueAt(i, 3);

                    if(toAddTable.getRowCount() > 0 && foodManagement.addIngredient((String)obj[0], (Integer)obj[1], (String)obj[2], (Integer)obj[3])) {
                        JOptionPane.showMessageDialog(null, "Success!");
                        ok = true;
                    }
                }
                if (!ok) {
                    JOptionPane.showMessageDialog(null, "Error!\n1. All fields must be filled. \n2. Units and price must be positive numbers.\n3. Two ingredients with the same name can\n not be added.\n(Edit the quantity instead!)");
                } else {
                    MainWindow.updateIngredients();
                    setVisible(false);
                    dispose();
                }
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
