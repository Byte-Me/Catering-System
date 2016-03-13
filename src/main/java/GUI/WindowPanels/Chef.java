package GUI.WindowPanels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Created by olekristianaune on 13.03.2016.
 */
public class Chef {

    DefaultTableModel prepareModel;
    DefaultTableModel ingredientModel;

    public Chef(JTable prepareTable, JTable ingredientTable) {

        String[] prepareHeader = {"Quantity", "Recipe", "Notes", "Ready for delivery"}; // Header titles
        String[] ingredientHeader = {"Ingredient", "Quantity", "Unit"}; // Header titles

        prepareModel = new DefaultTableModel() {
            public Class<?> getColumnClass(int columnIndex) {
                return getValueAt(0, columnIndex).getClass();
            }
        }; // Model of the table
        ingredientModel = new DefaultTableModel(); // Model of the table

        prepareModel.setColumnIdentifiers(prepareHeader); // Add header to columns
        ingredientModel.setColumnIdentifiers(ingredientHeader); // Add header to columns

        prepareTable.setModel(prepareModel); // Add model to table
        ingredientTable.setModel(ingredientModel); // Add model to table



        // TODO - testdata (remove)
        prepareModel.addRow(new Object[]{3, "Paella", "Without seafood", false});

        // TODO - testdata (remove)
        ingredientModel.addRow(new Object[]{"Suagr", 200, "grams"});
        ingredientModel.addRow(new Object[]{"Meatballs", 3000, "pieces"});

    }
}
