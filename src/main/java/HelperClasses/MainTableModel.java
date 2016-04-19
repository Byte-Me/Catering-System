package HelperClasses;

import javax.swing.table.DefaultTableModel;

/**
 * Created by olekristianaune on 11.04.2016.
 */
public class MainTableModel extends DefaultTableModel {
    @Override
    public boolean isCellEditable(int row, int column) {
        return false; // No cells are editable
    }

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
