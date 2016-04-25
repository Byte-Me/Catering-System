package HelperClasses;

import javax.swing.*;

/**
 * Created by olekristianaune on 01.04.2016.
 */
public class ToggleSelectionModel extends DefaultListSelectionModel {

    boolean gestureStarted = false;

    /**
     * If a selected value gets selected again, it gets deselected.
     *
     * @param index0    List start index
     * @param index1    List end index
     */
    public void setSelectionInterval(int index0, int index1) {
        if (isSelectedIndex(index0) && !gestureStarted) {
            super.removeSelectionInterval(index0, index1);
        }
        else {
            super.setSelectionInterval(index0, index1);
        }
        gestureStarted = true;
    }

    /**
     * When value is changed, make sure to set gesture started to false.
     *
     * @param isAdjusting
     */
    @Override
    public void setValueIsAdjusting(boolean isAdjusting) {
        if (!isAdjusting) {
            gestureStarted = false;
        }
    }

}
