package GUI;

import javax.swing.*;

/**
 * Created by olekristianaune on 07.03.2016.
 */
public class MainWindow extends JFrame {
    private JPanel mainPanel;
    private JTabbedPane tabbedPane1;
    private JPanel panel3;
    private JPanel panel1;
    private JPanel panel2;


    public MainWindow() {
        setContentPane(mainPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();



        setVisible(true);
    }


    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
