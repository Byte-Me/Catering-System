package GUI.WindowPanels;

import HelperClasses.DateLabelFormatter;
import Statistics.*;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.jfree.chart.ChartPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;

import static Statistics.FinanceStatistics.findFinanceStats;

/**
 * Created by olekristianaune on 13.03.2016.
 */
public class Statistics {
    private JPanel orderStatisticsPanel;
    private JPanel barChartPanel;
    private JPanel statsPanel;
    OrderStatistics os = new OrderStatistics();
    SubscriptionStatistics ss = new SubscriptionStatistics();
    JDatePickerImpl fromDate;
    JDatePickerImpl toDate;

    public Statistics(JPanel searchStatisticsPanel, JPanel orderStatisticsPanel, JPanel statsPanel,  JPanel barChartPanel) {

        this.orderStatisticsPanel = orderStatisticsPanel;
        this.barChartPanel = barChartPanel;
        this.statsPanel = statsPanel;

        // Labels
        JLabel fromLabel = new JLabel("From: ");
        JLabel toLabel = new JLabel("To: ");

        // Date Pickers start
        UtilDateModel fModel = new UtilDateModel();
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.DAY_OF_MONTH, -7);
        fModel.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
        fModel.setSelected(true);
        UtilDateModel tModel = new UtilDateModel();
        cal.add(Calendar.DAY_OF_MONTH, 7);
        tModel.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
        tModel.setSelected(true);
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");

        JDatePanelImpl fromPanel = new JDatePanelImpl(fModel, p);
        JDatePanelImpl toPanel = new JDatePanelImpl(tModel, p);

        fromDate = new JDatePickerImpl(fromPanel, new DateLabelFormatter());
        toDate = new JDatePickerImpl(toPanel, new DateLabelFormatter());


        // Get Statistics Button
        JButton getStatisticsButton = new JButton("Get Statistics");


        // Add components to JPanel
        searchStatisticsPanel.setLayout(new FlowLayout());
        searchStatisticsPanel.add(fromLabel);
        searchStatisticsPanel.add(fromDate);
        searchStatisticsPanel.add(toLabel);
        searchStatisticsPanel.add(toDate);
        searchStatisticsPanel.add(getStatisticsButton);


        getStatisticsButton.addActionListener(e -> {
            getStatistics();
        });

        getStatistics();


    }

    // TODO: This should be rewritten to only update components and not deleting and readding
    public void getStatistics() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fDate = dateFormat.format((Date)fromDate.getModel().getValue());
        String tDate = dateFormat.format((Date)toDate.getModel().getValue());

        orderStatisticsPanel.setLayout(new BorderLayout());
        barChartPanel.setLayout(new BorderLayout());

        Object[] orderStats = os.createLineChartFromOrder(fDate, tDate);

        ChartPanel lineChart = (ChartPanel)orderStats[0];
        if (lineChart != null) {
            orderStatisticsPanel.removeAll();
            orderStatisticsPanel.add(lineChart, BorderLayout.CENTER);
            orderStatisticsPanel.getRootPane().revalidate();
        }

        JPanel barChart = os.createBarChartFromOrder(fDate, tDate);
        if (barChart != null) {
            barChartPanel.removeAll();
            barChartPanel.add(barChart, BorderLayout.CENTER);
            barChartPanel.getRootPane().revalidate();
        }

        statsPanel.setBackground(Color.WHITE); // White background
        statsPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Padding
        statsPanel.setLayout(new GridLayout(8, 2));
        statsPanel.removeAll();

        // Amount of orders
        String sumOrder = orderStats[1].toString();
        statsPanel.add(new JLabel("Sum Orders:"));
        statsPanel.add(new JLabel(sumOrder, SwingConstants.RIGHT));

        // Canceled orders
        String canceledOrders = ((Integer)os.getCancelledOrders(fDate, tDate)).toString();
        statsPanel.add(new JLabel("Canceled Orders:"));
        statsPanel.add(new JLabel(canceledOrders, SwingConstants.RIGHT));

        // New Subscriptions
        String newSubs = ((Integer)ss.getSubCount(fDate, tDate)).toString();
        statsPanel.add(new JLabel("New Subscriptions:"));
        statsPanel.add(new JLabel(newSubs, SwingConstants.RIGHT));

        // Canceled subscriptions
        String canceledSubs = ((Integer)ss.getCancelledSubCount(fDate, tDate)).toString();
        statsPanel.add(new JLabel("Canceled Subscriptions:"));
        statsPanel.add(new JLabel(canceledSubs, SwingConstants.RIGHT));

        // Active subscriptions
        String activeSubs = ((Integer)ss.getActiveSubCount(fDate, tDate)).toString();
        statsPanel.add(new JLabel("Active Subscriptions:"));
        statsPanel.add(new JLabel(activeSubs, SwingConstants.RIGHT));

        // FINANSE
        long[] financeStats = findFinanceStats(fDate, tDate);
        // Income
        String income = ((Long)financeStats[0]).toString();
        statsPanel.add(new JLabel("Income:"));
        statsPanel.add(new JLabel(income, SwingConstants.RIGHT));

        // Expenses
        String expenses = ((Long)financeStats[1]).toString();
        statsPanel.add(new JLabel("Expenses:"));
        statsPanel.add(new JLabel(expenses, SwingConstants.RIGHT));

        // Net Profit
        String profit = ((Long)financeStats[2]).toString();
        statsPanel.add(new JLabel("Profit:"));
        statsPanel.add(new JLabel(profit, SwingConstants.RIGHT));
    }

}
