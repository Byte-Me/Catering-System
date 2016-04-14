package GUI;

import Database.FinanceManagement;
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
import java.util.DoubleSummaryStatistics;

/**
 * Created by asdfLaptop on 15.03.2016.
 */
public class GenerateShoppingList extends JDialog {
    private JPanel mainPane;
    private JTable shoppingTable;
    private JButton okButton;
    private JRadioButton addToStorageRButton;
    private JLabel priceLabel;
    private JLabel totalPriceLabel;

    private DefaultTableModel shoppingListModel = new DefaultTableModel();
    private FinanceManagement financeManagement = new FinanceManagement();

    public GenerateShoppingList() {
        setContentPane(mainPane);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainPane.getRootPane().setDefaultButton(okButton);
        pack();
        setLocationRelativeTo(getParent());
        setModal(true);

        String[] shoppingListHeader = {"Ingredient", "Quantity", "Unit", "Price"};

        shoppingListModel.setColumnIdentifiers(shoppingListHeader);

        shoppingTable.setModel(shoppingListModel);
        updateShoppingList();

        okButton.addActionListener(e -> {
            if(addToStorageRButton.isSelected()){
                double total = 0;
                for(int i= 0;i<shoppingTable.getRowCount();i++){
                    total += Double.valueOf((Integer)shoppingTable.getValueAt(i,3));
                }
                if(total > 0){
                    financeManagement.addOutcomeToDatabase(total);
                }
            }
            setVisible(false);
            dispose();
        });

        setVisible(true);
    }
    private void updateShoppingList() {
        ArrayList<Object[]> shoppingItems = CreateShoppingList.useOrdersToday();

        for(Object[] shoppingItem : shoppingItems){
            System.out.println(Arrays.toString(shoppingItem));
        }
        shoppingListModel.setRowCount(0);
        double totalPrice = 0;
        for (Object[] recipe : shoppingItems) {
            totalPrice += Double.valueOf((Integer)recipe[3]);
            shoppingListModel.addRow(recipe);
        }
        priceLabel.setText(Double.toString(totalPrice));
    }
}
