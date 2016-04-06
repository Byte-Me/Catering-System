package GUI;

import Database.FoodManagement;
import Food.CreateShoppingList;

import javax.swing.*;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by asdfLaptop on 15.03.2016.
 */
public class GenerateShoppingList extends JFrame {
    private JPanel mainPane;
    private JTable shoppingTable;
    private JButton okButton;

    private static DefaultTableModel shoppingListModel;

    private FoodManagement foodManagement;

    public GenerateShoppingList(Container parent) {
        setContentPane(mainPane);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainPane.getRootPane().setDefaultButton(okButton);
        pack();
        setLocationRelativeTo(parent);

        String[] shoppingListHeader = {"Ingredient", "Quantity", "Unit", "Price"};

        shoppingListModel = new DefaultTableModel();

        shoppingListModel.setColumnIdentifiers(shoppingListHeader);

        shoppingTable.setModel(shoppingListModel);
        updateShoppingList();

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });

        setVisible(true);
    }
    private void updateShoppingList() {
        ArrayList<Object[]> shoppingItems = CreateShoppingList.useOrdersToday();

        for(Object[] shoppingItem : shoppingItems){
            System.out.println(Arrays.toString(shoppingItem));
        }
        if(shoppingListModel.getRowCount() > 0) {
            for (int i = shoppingListModel.getRowCount() -1; i < -1; i--) {
                shoppingListModel.removeRow(i);
            }
        }

        for (Object[] recipe : shoppingItems) {
            shoppingListModel.addRow(recipe);
        }
    }
}
