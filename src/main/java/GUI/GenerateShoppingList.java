package GUI;

import Database.FinanceManagement;
import Food.CreateShoppingList;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

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
        setTitle("Shopping List");
        setContentPane(mainPane);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainPane.getRootPane().setDefaultButton(okButton);

        Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Images/icon32.png"));
        setIconImage(icon);

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

        pack();
        setLocationRelativeTo(getParent());
        setModal(true);
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
