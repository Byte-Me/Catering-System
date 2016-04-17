package GUI;

import Database.FinanceManagement;
import Database.FoodManagement;
import Food.CreateShoppingList;
import HelperClasses.DateLabelFormatter;
import org.jdatepicker.JDatePicker;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;

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
    private JPanel datePanel;
    JDatePickerImpl fromDate;
    JDatePickerImpl toDate;

    private DefaultTableModel shoppingListModel = new DefaultTableModel();
    private FinanceManagement financeManagement = new FinanceManagement();
    private final FoodManagement foodManagement = new FoodManagement();

    public GenerateShoppingList() {
        setTitle("Shopping List");
        setContentPane(mainPane);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainPane.getRootPane().setDefaultButton(okButton);
        // Labels
        JLabel fromLabel = new JLabel("From: ");
        JLabel toLabel = new JLabel("To: ");

        // Date Pickers start
        UtilDateModel fModel = new UtilDateModel();
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        fModel.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
        fModel.setSelected(true);
        UtilDateModel tModel = new UtilDateModel();

        tModel.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
        tModel.setSelected(true);
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");

        JDatePanelImpl fromPanel = new JDatePanelImpl(fModel, p);
        JDatePanelImpl toPanel = new JDatePanelImpl(tModel, p);

        fromDate = new JDatePickerImpl(fromPanel, new DateLabelFormatter());
        toDate = new JDatePickerImpl(toPanel, new DateLabelFormatter());

        JButton updateButton = new JButton("Update list");
        updateButton.addActionListener(e -> updateShoppingList());

        // Add components to JPanel
        datePanel.setLayout(new FlowLayout());
        datePanel.add(fromLabel);
        datePanel.add(fromDate);
        datePanel.add(toLabel);
        datePanel.add(toDate);
        datePanel.add(updateButton);

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

                    //adds to storage
                    foodManagement.addIngredientToStorage((String)shoppingTable.getValueAt(i,0),(Integer)shoppingTable.getValueAt(i,1));

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
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fDate = dateFormat.format((Date)fromDate.getModel().getValue());
        String tDate = dateFormat.format((Date)toDate.getModel().getValue());
        ArrayList<Object[]> shoppingItems = CreateShoppingList.withDates(fDate, tDate);
        System.out.println(fDate +" "+tDate);

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
