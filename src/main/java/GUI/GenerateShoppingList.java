package GUI;

import Database.FinanceManagement;
import Database.FoodManagement;
import Food.CreateShoppingList;
import HelperClasses.DateLabelFormatter;
import HelperClasses.MainTableModel;
import org.jdatepicker.JDatePicker;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    private JDatePickerImpl fromDate;
    private JDatePickerImpl toDate;
    private JButton updateButton;
    private DefaultTableModel shoppingListModel;

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
        shoppingListModel = new MainTableModel();

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

        updateButton = new JButton("Update list");
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
        shoppingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        shoppingTable.setAutoCreateRowSorter(true);

        updateShoppingList();

        JPopupMenu popupMenu = new JPopupMenu("Shopping List");
        JMenuItem add = popupMenu.add(new AbstractAction("Edit Shopping List") {
            @Override
            public void actionPerformed(ActionEvent e) {
                editShoppingList();
            }
        });

        shoppingTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {

                int r = shoppingTable.rowAtPoint(e.getPoint());
                if (r >= 0 && r < shoppingTable.getRowCount()) {
                    shoppingTable.setRowSelectionInterval(r, r);
                } else {
                    shoppingTable.clearSelection();
                }

                int rowindex = shoppingTable.getSelectedRow();
                if (rowindex < 0)
                    return;

                if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {

                int r = shoppingTable.rowAtPoint(e.getPoint());
                if (r >= 0 && r < shoppingTable.getRowCount()) {
                    shoppingTable.setRowSelectionInterval(r, r);
                } else {
                    shoppingTable.clearSelection();
                }

                int rowindex = shoppingTable.getSelectedRow();
                if (rowindex < 0)
                    return;

                if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    if (shoppingTable.getSelectedRow() != -1) {
                        editShoppingList();
                    }
                }
            }

        });


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


        shoppingListModel.setRowCount(0);
        double totalPrice = 0;
        for (Object[] recipe : shoppingItems) {
            totalPrice += Double.valueOf((Integer)recipe[3]);
            shoppingListModel.addRow(recipe);
        }
        priceLabel.setText(Double.toString(totalPrice));
        updateButton.setVisible(true);

    }

    private void editShoppingList(){
        try{
            String name = (String)shoppingTable.getValueAt(shoppingTable.getSelectedRow(),0);
            int oldQuant = (Integer) shoppingTable.getValueAt(shoppingTable.getSelectedRow(),1);
            String newQuantIn = JOptionPane.showInputDialog(null, "New quantity for "+name+".");
            int newQuant = Integer.parseInt(newQuantIn);
            if(newQuant>=0) {
                //calculating new prices
                int ingPrice = (Integer) shoppingTable.getValueAt(shoppingTable.getSelectedRow(), 3) / (Integer) shoppingTable.getValueAt(shoppingTable.getSelectedRow(), 1);
                shoppingTable.setValueAt(newQuant, shoppingTable.getSelectedRow(), 1);
                int priceDiff = ingPrice * (newQuant - oldQuant);
                int newPrice = ingPrice*newQuant;
                shoppingTable.setValueAt(newPrice, shoppingTable.getSelectedRow(), 3);
                double newTotal = Double.parseDouble(priceLabel.getText())+priceDiff;
                priceLabel.setText(Double.toString(newTotal));
            }



        }
        catch (NumberFormatException ne){
            //Cancel pressed or not an int
            JOptionPane.showMessageDialog(null, "Issues with input.");
        }
    }
}
