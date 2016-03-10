package Testing;

import GUI.graph.ChartCreator;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by Evdal on 09.03.2016.
 */
public class TestJUnitGraph {
    private ArrayList<String> xValues = new ArrayList<String>();
    private ArrayList<Double> yValues = new ArrayList<Double>();

    @Test
    public void lineGraphTest(){


        xValues.add("January");
        xValues.add("February");
        xValues.add("March");
        xValues.add("April");
        xValues.add("Mai");

        yValues.add(100.0);
        yValues.add(200.0);
        yValues.add(300.0);
        yValues.add(345.0);
        yValues.add(785.0);

        JPanel panel = ChartCreator.createLineChart("Title", "Time", "Values", xValues, yValues, "Inntekter");
        assertNotNull(panel);
    }


}
