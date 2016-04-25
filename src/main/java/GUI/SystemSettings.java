package GUI;

import Database.SettingsManagement;

import javax.swing.*;

/**
 * Created by olekristianaune on 20.04.2016.
 */
public class SystemSettings extends JDialog {

    private JPanel mainPanel;
    private JTextField address;
    private JButton cancelButton;
    private JButton saveButton;
    private JTextField city;
    private JTextField country;

    /**
     * Constructor for SystemSettings.
     */
    public SystemSettings() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(mainPanel);
        setTitle("System Settings");

        SettingsManagement sm = new SettingsManagement();

        address.setText(sm.getSystemAddress());
        city.setText(sm.getSystemCity());
        country.setText(sm.getSystemCountry());

        cancelButton.addActionListener(e -> {
            setVisible(false);
            dispose();
        });

        saveButton.addActionListener(e -> { // TODO: Add some failsafe!
            String addressString = address.getText();
            String cityString = city.getText();
            String countryString = country.getText();
            if( sm.setSystemAddress(addressString) && sm.setSystemCity(cityString) && sm.setSystemCountry(countryString) ) {
                setVisible(false);
                dispose();
            }
        });

        pack();
        setLocationRelativeTo(getParent());
        setModal(true);
        setVisible(true);
    }
}
