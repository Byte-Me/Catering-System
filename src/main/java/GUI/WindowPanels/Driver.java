package GUI.WindowPanels;

import javax.swing.*;

/**
 * Created by olekristianaune on 13.03.2016.
 */
public class Driver {

    DefaultListModel<String> driverModel;

    public Driver(JList<String> drivingList, JPanel mapPanel) {

        driverModel = new DefaultListModel<String>(); // Model of the list
        drivingList.setModel(driverModel); // Add model to jList

        // TODO - testdata (remove)
        driverModel.addElement("Some Curporation LTD    Adresseveien 4, Sted, Land  91482099");

    }
}
