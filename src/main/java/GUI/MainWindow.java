package GUI;

import Statistics.OrderStatistics;

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
        OrderStatistics ord = new OrderStatistics();
        setContentPane(mainPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();

        JFrame noe = new JFrame("Hei");
        noe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        noe.add(ord.createGraphFromOrders("2016-03-11", "2016-12-16"));
        noe.pack();
        noe.setVisible(true);


        setVisible(true);
    }


    private void createUIComponents() {
        // TODO: place custom component creation code here

    }
}
