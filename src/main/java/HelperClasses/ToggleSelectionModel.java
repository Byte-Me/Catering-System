package HelperClasses;

import javax.swing.*;

/**
 * Created by olekristianaune on 01.04.2016.
 */
public class ToggleSelectionModel extends DefaultListSelectionModel {

    boolean gestureStarted = false;

    public void setSelectionInterval(int index0, int index1) {
        if (isSelectedIndex(index0) && !gestureStarted) {
            super.removeSelectionInterval(index0, index1);
        }
        else {
            super.setSelectionInterval(index0, index1);
        }
        gestureStarted = true;
    }

    @Override
    public void setValueIsAdjusting(boolean isAdjusting) {
        if (!isAdjusting) {
            gestureStarted = false;
        }
    }

}
