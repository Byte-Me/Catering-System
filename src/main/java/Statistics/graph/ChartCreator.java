package Statistics.graph;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleInsets;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

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

    /**
     *
     * @param title
     * @param xTitle
     * @param yTitle
     * @param xValues
     * @param yValues
     * @param dataInfo
     * @return
     */
    public static ChartPanel createLineChart(String title, String xTitle,
                                             String yTitle, ArrayList<String> xValues, ArrayList<Object> yValues, String dataInfo ) {

        if(xValues.size() != yValues.size()) {
            return null;
        }

        JFreeChart lineChart = ChartFactory.createLineChart(title, xTitle, yTitle,
                createDataset(xValues, yValues, dataInfo), PlotOrientation.VERTICAL, false, false, false);
        StandardChartTheme theme = (StandardChartTheme)org.jfree.chart.StandardChartTheme.createJFreeTheme();
        String fontName = "Helvetica";
        theme.setExtraLargeFont( new Font(fontName,Font.PLAIN, 16) ); //title
        theme.setLargeFont( new Font(fontName,Font.BOLD, 15)); //axis-title
        theme.setRegularFont( new Font(fontName,Font.PLAIN, 11));
        theme.setRangeGridlinePaint( Color.decode("#C0C0C0"));
        theme.setPlotBackgroundPaint( Color.white );
        theme.setChartBackgroundPaint( Color.white );
        theme.setGridBandPaint( Color.red );
        theme.setAxisOffset( new RectangleInsets(0,0,0,0) );
        theme.setBarPainter(new StandardBarPainter());
        theme.setAxisLabelPaint( Color.decode("#666666")  );
        theme.apply( lineChart );
        lineChart.getCategoryPlot().setOutlineVisible( false );
        lineChart.getCategoryPlot().getRangeAxis().setAxisLineVisible( false );
        lineChart.getCategoryPlot().getRangeAxis().setTickMarksVisible( false );
        lineChart.getCategoryPlot().setRangeGridlineStroke( new BasicStroke() );
        lineChart.getCategoryPlot().getRangeAxis().setTickLabelPaint( Color.decode("#666666") );
        lineChart.getCategoryPlot().getDomainAxis().setTickLabelPaint( Color.decode("#666666") );
        lineChart.setTextAntiAlias( true );
        lineChart.setAntiAlias( true );
        lineChart.getCategoryPlot().getRenderer().setSeriesPaint( 0, Color.decode( "#4572a7" ));

        ChartPanel chartPanel = new ChartPanel( lineChart );

        chartPanel.setPreferredSize( new java.awt.Dimension( 1000 , 367 ) );
        // preffered size is set, might want variables for dimensions.

        chartPanel.setDomainZoomable(false); // Remove zooming
        chartPanel.setRangeZoomable(false); // Remove zooming
        chartPanel.setPopupMenu(null); // Remove Popup menu
        return chartPanel;

    }

        /*
            Creates dataset from two ArrayLists and datainfo,
            returns NULL if not equal size.
         */

    /**
     *
     * @param xValues
     * @param yValues
     * @param dataInfo
     * @return
     */
    private static DefaultCategoryDataset createDataset(ArrayList<String> xValues, //kan bare sende inn double eller int
                                                        ArrayList<Object> yValues, String dataInfo) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();


        if(xValues.isEmpty()){
            dataset.addValue(0,"No data found","");
        }
        else {

            if (yValues.get(0) instanceof Integer) {
                for (int i = 0; i < xValues.size(); i++) {
                    dataset.addValue((Integer) yValues.get(i), dataInfo, xValues.get(i));
                }
            } else if (yValues.get(0) instanceof Double) {
                for (int i = 0; i < xValues.size(); i++) {
                    dataset.addValue((Double) yValues.get(i), dataInfo, xValues.get(i));
                }
            }
        }

        return dataset;
    }

    /**
     *
     * @param title
     * @param xTitle
     * @param yTitle
     * @param xValues
     * @param yValues
     * @param dataInfo
     * @return
     */
    public static ChartPanel createBarChart(String title, String xTitle,
                                             String yTitle, ArrayList<String> xValues, ArrayList<Object> yValues, String dataInfo ) {

        if(xValues.size() != yValues.size()) {
            return null;
        }
        JFreeChart barChart = ChartFactory.createBarChart(title, xTitle, yTitle,
                createDataset(xValues, yValues, dataInfo), PlotOrientation.VERTICAL, false, false, false);

        StandardChartTheme theme = (StandardChartTheme)org.jfree.chart.StandardChartTheme.createJFreeTheme();
        String fontName = "Lucida Sans";
        theme.setExtraLargeFont( new Font(fontName,Font.PLAIN, 16) ); //title
        theme.setLargeFont( new Font(fontName,Font.BOLD, 15)); //axis-title
        theme.setRegularFont( new Font(fontName,Font.PLAIN, 11));
        theme.setRangeGridlinePaint( Color.decode("#C0C0C0"));
        theme.setPlotBackgroundPaint( Color.white );
        theme.setChartBackgroundPaint( Color.white );
        theme.setGridBandPaint( Color.red );
        theme.setAxisOffset( new RectangleInsets(0,0,0,0) );
        theme.setBarPainter(new StandardBarPainter());
        theme.setAxisLabelPaint( Color.decode("#666666")  );
        theme.apply( barChart );
        barChart.getCategoryPlot().setOutlineVisible( false );
        barChart.getCategoryPlot().getRangeAxis().setAxisLineVisible( false );
        barChart.getCategoryPlot().getRangeAxis().setTickMarksVisible( false );
        barChart.getCategoryPlot().setRangeGridlineStroke( new BasicStroke() );
        barChart.getCategoryPlot().getRangeAxis().setTickLabelPaint( Color.decode("#666666") );
        barChart.getCategoryPlot().getDomainAxis().setTickLabelPaint( Color.decode("#666666") );
        barChart.setTextAntiAlias( true );
        barChart.setAntiAlias( true );
        barChart.getCategoryPlot().getRenderer().setSeriesPaint( 0, Color.decode( "#4572a7" ));
        BarRenderer rend = (BarRenderer) barChart.getCategoryPlot().getRenderer();
        rend.setShadowVisible( true );
        rend.setShadowXOffset( 2 );
        rend.setShadowYOffset( 0 );
        rend.setShadowPaint( Color.decode( "#C0C0C0"));
        rend.setMaximumBarWidth( 0.1);
        ChartPanel chartPanel = new ChartPanel(barChart);


        chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
        // preffered size is set, might want variables for dimensions.

        return chartPanel;
    }

}
