package HelperClasses;

import javax.swing.table.DefaultTableModel;

/**
 * Created by olekristianaune on 11.04.2016.
 */
public class MainTableModel extends DefaultTableModel {

    /**
     * Defines that no cells are editable
     *
     * @param row
     * @param column
     * @return
     */
    @Override
    public boolean isCellEditable(int row, int column) {
        return false; // No cells are editable
    }

    /**
     * Returns the class type of the column. Helps with sorting correctly for different data types
     *
     * @param column
     * @return
     */
    @Override
    public Class getColumnClass(int column) {
        for (int row = 0; row < getRowCount(); row++) {
            Object o = getValueAt(row, column);
            if (o != null) {
                return o.getClass();
            }
        }
        return Object.class;
    }

}
