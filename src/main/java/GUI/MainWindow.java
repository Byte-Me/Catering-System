package GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Created by olekristianaune on 07.03.2016.
 */
public class MainWindow extends JFrame {
    private JPanel mainPanel;
    private JTabbedPane tabbedPane1;
    private JPanel statistics;
    private JPanel driver;
    private JPanel chef;
    private JPanel administration;
    private JPanel sale;
    private JList userList;
    private JButton addUserButton;


    public MainWindow(int userType) {
        setContentPane(mainPanel);

        setupAdministration();

        // Create GUI for specific user type
        switch (userType) {
            case 0:
                // Admin have access to everything
                break;
            case 1:
                // Sale
                //tabbedPane1.remove(administration);
                tabbedPane1.remove(chef);
                tabbedPane1.remove(driver);
                break;
            case 2:
                // Chef
                tabbedPane1.remove(statistics);
                tabbedPane1.remove(driver);
                tabbedPane1.remove(administration);
                tabbedPane1.remove(sale);
                break;
            case 3:
                // Driver
                tabbedPane1.remove(statistics);
                tabbedPane1.remove(chef);
                tabbedPane1.remove(administration);
                tabbedPane1.remove(sale);
                break;
            default:
        }


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();



        setVisible(true);
    }

    private void setupAdministration() {

        addUserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AddUser addUser = new AddUser();
            }
        });

    }
}
