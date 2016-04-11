package Statistics.graph;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.util.*;

/**
 * Created by Evdal on 03.03.2016.
 *
 * Class contains static methods for creating ChartPanel
 * For now only linecharts are available, but this class can be expanded.
 *
 * To create a chart, write:
 * ChartCreator.createLineChart(enter parameters here)
 *
 * This will return a ChartPanel object, which can be
 * added directly to a JFrame using:
 * JFrame.add(ChartPanel)
 *
 * Remember to create a check wether the object returned
 * is NULL, as this means no chart was created.
 *
 */
public class ChartCreator extends JFrame {

    /* Constructor information:

        title - large title above chart.
        xTitle - description of xAxis.
        yTitle - desription of yAxis.
        xValues - Arraylist of strings, usually describes time. Eks. months, years.
        yValues - Arraylist of doubles, usually values changing over time. Eks. revenue, number of customers.
        dataInfo - Describes the meaning of the line.

        If Arraylists are not equal size, NULL is returned.

    */

    public static ChartPanel createLineChart(String title, String xTitle,
                                             String yTitle, ArrayList<String> xValues, ArrayList<Object> yValues, String dataInfo ) {

        if(xValues.size() != yValues.size()) {
            return null;
        }

        JFreeChart lineChart = ChartFactory.createLineChart(title, xTitle, yTitle,
                createDataset(xValues, yValues, dataInfo), PlotOrientation.VERTICAL, true, true, false);

        ChartPanel chartPanel = new ChartPanel( lineChart );


        chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
        // preffered size is set, might want variables for dimensions.

        return chartPanel;

    }

        /*
            Creates dataset from two ArrayLists and datainfo,
            returns NULL if not equal size.
         */

    private static DefaultCategoryDataset createDataset(ArrayList<String> xValues, //kan bare sende inn double eller int
                                                        ArrayList<Object> yValues, String dataInfo) {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if(yValues.get(0) instanceof Integer) {
            for (int i = 0; i < xValues.size(); i++) {
                dataset.addValue((Integer)yValues.get(i), dataInfo, xValues.get(i));
            }
        }
        else if(yValues.get(0) instanceof Double){
            for (int i = 0; i < xValues.size(); i++) {
                dataset.addValue((Double)yValues.get(i), dataInfo, xValues.get(i));
            }
        }

        return dataset;
    }
    public static ChartPanel createBarChart(String title, String xTitle,
                                             String yTitle, ArrayList<String> xValues, ArrayList<Object> yValues, String dataInfo ) {

        if(xValues.size() != yValues.size()) {
            return null;
        }
        JFreeChart barChart = ChartFactory.createBarChart(title, xTitle, yTitle,
                createDataset(xValues, yValues, dataInfo), PlotOrientation.VERTICAL, true, true, false);

        ChartPanel chartPanel = new ChartPanel(barChart);


        chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
        // preffered size is set, might want variables for dimensions.

        return chartPanel;

    }

}
