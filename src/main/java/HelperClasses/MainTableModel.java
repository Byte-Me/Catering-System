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
        switch (column) {
            default:
                return String.class; // All columns are by default Strings
        }
    }


}
