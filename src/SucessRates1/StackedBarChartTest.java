/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SucessRates1;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.SubCategoryAxis;
import org.jfree.chart.plot.CategoryPlot;  
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.GroupedStackedBarRenderer;
import org.jfree.data.KeyToGroupMap;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class StackedBarChartTest {

    private static CategoryDataset getData() {
        DefaultCategoryDataset dataSet = new DefaultCategoryDataset();

        dataSet.addValue(100, "Series1 G1", "Jan 04");
        dataSet.addValue(150, "Series1 G2", "Jan 04");

        dataSet.addValue(60, "Series2 G1", "Feb 04");
        dataSet.addValue(90, "Series2 G2", "Feb 04");

        dataSet.addValue(70, "Series3 G2", "Feb 04");
        dataSet.addValue(99, "Series3 G1", "Feb 04");

        dataSet.addValue(100, "Series4 G1", "Feb 04");
        dataSet.addValue(80, "Series4 G2", "Feb 04");

        dataSet.addValue(20, "Series1 G1", "Feb 04");
        dataSet.addValue(50, "Series1 G2", "Feb 04");

        dataSet.addValue(10, "Series2 G1", "Jan 04");
        dataSet.addValue(40, "Series2 G2", "Jan 04");

        dataSet.addValue(10, "Series3 G2", "Jan 04");
        dataSet.addValue(50, "Series3 G1", "Jan 04");

        dataSet.addValue(10, "Series4 G1", "Jan 04");
        dataSet.addValue(60, "Series4 G2", "Jan 04");

  // dataSet.addValue(100, "Series1 G1", "Jan 04");
        // dataSet.addValue(20, "Series1 G1", "Feb 04");
        // dataSet.addValue(10, "Series2 G1", "Jan 04");
        // dataSet.addValue(60, "Series2 G1", "Feb 04");
        // dataSet.addValue(99, "Series3 G1", "Feb 04");
        // dataSet.addValue(50, "Series3 G1", "Jan 04");
        // dataSet.addValue(100, "Series4 G1", "Feb 04");
        // dataSet.addValue(10, "Series4 G1", "Jan 04");
        //
        //  
        // dataSet.addValue(150, "Series1 G2", "Jan 04");
        // dataSet.addValue(50, "Series1 G2", "Feb 04");
        // dataSet.addValue(40, "Series2 G2", "Jan 04");
        // dataSet.addValue(90, "Series2 G2", "Feb 04");
        // dataSet.addValue(10, "Series3 G2", "Jan 04");
        // dataSet.addValue(70, "Series3 G2", "Feb 04");
        // dataSet.addValue(60, "Series4 G2", "Jan 04");
        // dataSet.addValue(80, "Series4 G2", "Feb 04");
        return dataSet;
    }

    private static JFreeChart createChart(CategoryDataset categorydataset) {
        JFreeChart jfreechart = ChartFactory.createStackedBarChart("Sample", null, "value", categorydataset, PlotOrientation.VERTICAL, true, true, false);
        GroupedStackedBarRenderer renderer = new GroupedStackedBarRenderer();
        renderer.setItemMargin(0.01);
        renderer.setDrawBarOutline(false);

        renderer.setSeriesPaint(0, Color.blue);
        renderer.setSeriesPaint(1, Color.blue);

        renderer.setSeriesPaint(2, Color.cyan);
        renderer.setSeriesPaint(3, Color.cyan);

        renderer.setSeriesPaint(4, Color.green);
        renderer.setSeriesPaint(5, Color.green);

        renderer.setSeriesPaint(6, Color.yellow);
        renderer.setSeriesPaint(7, Color.yellow);

  // renderer.setSeriesPaint(0, Color.blue);
        // renderer.setSeriesPaint(4, Color.blue);
        //
        // renderer.setSeriesPaint(1, Color.cyan);
        // renderer.setSeriesPaint(5, Color.cyan);
        //
        // renderer.setSeriesPaint(2, Color.green);
        // renderer.setSeriesPaint(6, Color.green);
        //
        // renderer.setSeriesPaint(3, Color.yellow);
        // renderer.setSeriesPaint(7, Color.yellow);
        KeyToGroupMap keytogroupmap = new KeyToGroupMap("G1");
        keytogroupmap.mapKeyToGroup("Series1 G1", "G1");
        keytogroupmap.mapKeyToGroup("Series2 G1", "G1");
        keytogroupmap.mapKeyToGroup("Series3 G1", "G1");
        keytogroupmap.mapKeyToGroup("Series4 G1", "G1");

        keytogroupmap.mapKeyToGroup("Series1 G2", "G2");
        keytogroupmap.mapKeyToGroup("Series2 G2", "G2");
        keytogroupmap.mapKeyToGroup("Series3 G2", "G2");
        keytogroupmap.mapKeyToGroup("Series4 G2", "G2");

        renderer.setSeriesToGroupMap(keytogroupmap);
        SubCategoryAxis subcategoryaxis = new SubCategoryAxis("Series/Month");
        subcategoryaxis.setCategoryMargin(0.03);
        subcategoryaxis.addSubCategory("G1");
        subcategoryaxis.addSubCategory("G2");

        CategoryPlot categoryplot = (CategoryPlot) jfreechart.getPlot();
        categoryplot.setDomainAxis(subcategoryaxis);
        categoryplot.setRenderer(renderer);
        //categoryplot.setFixedLegendItems(createLegendItems());

        return jfreechart;
    }

    private static LegendItemCollection createLegendItems() {
        LegendItemCollection legenditemcollection = new LegendItemCollection();
        LegendItem legenditem = new LegendItem("Series1", "-", null, null, Plot.DEFAULT_LEGEND_ITEM_BOX, Color.blue);
        LegendItem legenditem1 = new LegendItem("Series2", "-", null, null, Plot.DEFAULT_LEGEND_ITEM_BOX, Color.cyan);
        LegendItem legenditem2 = new LegendItem("Series3", "-", null, null, Plot.DEFAULT_LEGEND_ITEM_BOX, Color.green);
        LegendItem legenditem3 = new LegendItem("Series4", "-", null, null, Plot.DEFAULT_LEGEND_ITEM_BOX, Color.yellow);
        legenditemcollection.add(legenditem);
        legenditemcollection.add(legenditem1);
    
        legenditemcollection.add(legenditem3);
            legenditemcollection.add(legenditem2);
        return legenditemcollection;
    }

    public static void main(String args[]) {

        StackedBarChartTest demo = new StackedBarChartTest();

        ChartFrame frame = new ChartFrame("First", demo.createChart(demo.getData()));
        frame.pack();
        frame.setVisible(true);
    }
}
