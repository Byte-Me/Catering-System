package HelperClasses;

import javax.swing.table.DefaultTableModel;

/**
 * Created by olekristianaune on 11.04.2016.
 */
public class CustomTableModel extends DefaultTableModel {
    String[] headers;

    public CustomTableModel(String[] headers) {
        System.arraycopy(headers, 0, this.headers, 0, headers.length);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        //all cells false
        return false;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            default:
                return String.class;
        }
    }


}
