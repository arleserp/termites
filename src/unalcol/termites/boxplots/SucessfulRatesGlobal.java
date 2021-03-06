/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.boxplots;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Paint;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.AbstractCategoryItemLabelGenerator;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import unalcol.termites.reports.DataCollectedLatexConsolidatorSASOMessagesSend1;
/**
 *
 * @author Arles Rodriguez <arles.rodriguez@gmail.com>
 */
public class SucessfulRatesGlobal extends ApplicationFrame {

    private static final long serialVersionUID = 1L;

    static class LabelGenerator extends AbstractCategoryItemLabelGenerator implements CategoryItemLabelGenerator {

        private static final long serialVersionUID = 1L;

        public String generateLabel(CategoryDataset categorydataset, int i, int j) {
            String s = null;
            double d = 0.0D;
            if (category != null) {
                Number number = categorydataset.getValue(i, category.intValue());
                d = number.doubleValue();
            } else {
                d = calculateSeriesTotal(categorydataset, i);
            }
            Number number1 = categorydataset.getValue(i, j);
            if (number1 != null) {
                double d1 = number1.doubleValue();
                //s = /*number1.toString() +*/ " (" + formatter.format(d1 / d) + ")";
            }
            return s;
        }

        private double calculateSeriesTotal(CategoryDataset categorydataset, int i) {
            double d = 0.0D;
            for (int j = 0; j < categorydataset.getColumnCount(); j++) {
                Number number = categorydataset.getValue(i, j);
                if (number != null) {
                    d += number.doubleValue();
                }
            }

            return d;
        }

        private Integer category;
        private NumberFormat formatter;

        public LabelGenerator(int i) {
            this(new Integer(i));
        }

        public LabelGenerator(Integer integer) {
            super("", NumberFormat.getInstance());
            formatter = NumberFormat.getPercentInstance();
            category = integer;
        }
    }

    /**
     *
     * @param s
     */
    public SucessfulRatesGlobal(String s) {
        super(s);
        ArrayList<Double> pf0 = new ArrayList<>();
        ArrayList<Double> pfg1 = new ArrayList<>();
        ArrayList<Double> pfg2 = new ArrayList<>();
        ArrayList<Double> pfg3 = new ArrayList<>();
        ArrayList<Double> pfg4 = new ArrayList<>();
        ArrayList<Double> pfg5 = new ArrayList<>();
        ArrayList<Double> pfg6 = new ArrayList<>();

        pf0.add(0.0);
        pfg1.add(1.0E-4);
        pfg2.add(3.0E-4);
        pfg3.add(5.0E-4);
        pfg4.add(7.0E-4);
        pfg5.add(9.0E-4);
        //pfg3.add(1.0E-3);

        CategoryDataset categorydataset = createDataset(pf0);
        JFreeChart jfreechart = createChart(categorydataset, pf0);

        categorydataset = createDataset(pfg1);
        jfreechart = createChart(categorydataset, pfg1);
        categorydataset = createDataset(pfg2);
        jfreechart = createChart(categorydataset, pfg2);
        categorydataset = createDataset(pfg3);
        jfreechart = createChart(categorydataset, pfg3);
        categorydataset = createDataset(pfg4);
        jfreechart = createChart(categorydataset, pfg4);
        categorydataset = createDataset(pfg5);
        jfreechart = createChart(categorydataset, pfg5);

        ChartPanel chartpanel = new ChartPanel(jfreechart);
        chartpanel.setPreferredSize(new Dimension(500, 270));
        setContentPane(chartpanel);
    }

    private static CategoryDataset createDataset(ArrayList<Double> Pf) {
        DefaultCategoryDataset defaultcategorydataset = new DefaultCategoryDataset();
        String sDirectorio = "..\\results\\";
        File f = new File(sDirectorio);
        String extension;
        File[] files = f.listFiles();
        Hashtable<String, String> Pop = new Hashtable<>();
        PrintWriter escribir;
        Scanner sc = null;
        double sucessfulExp = 0.0;

        Hashtable<String, List> info = new Hashtable();

        //String[] aMode = {"levywalk", "lwphevap", "hybrid", "hybrid3", "hybrid4"};
        info.put("levywalk", new ArrayList());
        info.put("lwphevap", new ArrayList());
        info.put("hybrid", new ArrayList());
        //info.put("hybrid3", new ArrayList());
        //info.put("hybrid4", new ArrayList());
        info.put("sequential", new ArrayList());

        for (File file : files) {
            extension = "";
            int i = file.getName().lastIndexOf('.');
            int p = Math.max(file.getName().lastIndexOf('/'), file.getName().lastIndexOf('\\'));
            if (i > p) {
                extension = file.getName().substring(i + 1);
            }

            // System.out.println(file.getName() + "extension" + extension);
            if (file.isFile() && extension.equals("csv") && file.getName().startsWith("dataCollected") && file.getName().contains("mazeon")) {
                System.out.println(file.getName());
                System.out.println("get: " + file.getName());
                String[] filenamep = file.getName().split(Pattern.quote("+"));

                System.out.println("file" + filenamep[8]);

                int popsize = Integer.valueOf(filenamep[3]);
                double pf = Double.valueOf(filenamep[5]);
                String mode = filenamep[7];

                int maxIter = -1;
                //if (!filenamep[8].isEmpty()) {
                maxIter = Integer.valueOf(filenamep[9]);
                //}

                System.out.println("psize:" + popsize);
                System.out.println("pf:" + pf);
                System.out.println("mode:" + mode);
                System.out.println("maxIter:" + maxIter);

                //String[] aMode = {"random", "levywalk", "sandc", "sandclw"};
                //String[] aMode = {"lwphclwevap", "lwsandc2", "lwsandc", "lwphevap2", "lwphevap"};
                // String[] aMode = {"levywalk", "lwphevap", "hybrid"};
                //String[] aMode = {"levywalk", "lwphevap", "hybrid", "hybrid3", "hybrid4", "sequential"};
                String[] aMode = {"levywalk", "lwphevap", "hybrid", "sequential"};

                
                if (isInMode(aMode, mode)) {
                    final List list = new ArrayList();
                    try {
                        sc = new Scanner(file);

                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(DataCollectedLatexConsolidatorSASOMessagesSend1.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }

                    int roundNumber = 0;
                    double globalInfoCollected = 0;

                    String[] data = null;
                    while (sc.hasNext()) {
                        String line = sc.nextLine();
                        data = line.split(",");
                        //System.out.println("data");
                        roundNumber = Integer.valueOf(data[0]);
                        globalInfoCollected = Double.valueOf(data[4]);

                        if (globalInfoCollected >= 90 && Pf.contains(pf)) {
                            info.get(mode).add(roundNumber);
                            break;
                        }

                    }

                }
            }

        }

        for (String key : info.keySet()) {
            System.out.println(key + ":" + info.get(key).size()/30*100.0);
            defaultcategorydataset.addValue( info.get(key).size()/30.0*100.0, "", getTechniqueName(key));
        }

        return defaultcategorydataset;
    }

    private static JFreeChart createChart(CategoryDataset categorydataset, ArrayList<Double> pf) {
        JFreeChart jfreechart = ChartFactory.createBarChart("Success Rates - " + getTitle(pf), "", "", categorydataset, PlotOrientation.VERTICAL, true, true, false);
        jfreechart.getTitle().setFont(new Font("Sans-Serif", Font.PLAIN, 18));
        jfreechart.setBackgroundPaint(new Color(221, 223, 238));
        CategoryPlot categoryplot = (CategoryPlot) jfreechart.getPlot();
        categoryplot.setBackgroundPaint(Color.white);
        categoryplot.setDomainGridlinePaint(Color.white);
        categoryplot.setRangeGridlinePaint(Color.gray);
        categoryplot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);

        BarRenderer renderer = (BarRenderer) categoryplot.getRenderer();
        //categoryplot.setBackgroundPaint(new Color(221, 223, 238));

        renderer.setSeriesPaint(0, new Color(130, 165, 70));
        renderer.setSeriesPaint(1, new Color(220, 165, 70));
        renderer.setSeriesPaint(4, new Color(255, 165, 70));
        renderer.setDrawBarOutline(false);
        renderer.setShadowVisible(false);
        // renderer.setMaximumBarWidth(1);
        renderer.setGradientPaintTransformer(null);
        renderer.setDefaultBarPainter(new StandardBarPainter());

        categoryplot.setRenderer(renderer);

        NumberAxis numberaxis = (NumberAxis) categoryplot.getRangeAxis();
        numberaxis.setUpperMargin(0.25D);
        CategoryItemRenderer categoryitemrenderer = categoryplot.getRenderer();
        categoryitemrenderer.setBaseItemLabelsVisible(true);
        categoryitemrenderer.setBaseItemLabelGenerator(new LabelGenerator(null));
        numberaxis.setRange(0, 100);
        //numberaxis.setNumberFormatOverride(NumberFormat.getPercentInstance());

        Font font = new Font("SansSerif", Font.ROMAN_BASELINE, 12);
        numberaxis.setTickLabelFont(font);
        CategoryAxis axisd = categoryplot.getDomainAxis();
        ValueAxis axisr = categoryplot.getRangeAxis();
        axisd.setTickLabelFont(font);
        axisr.setTickLabelFont(font);

        final ChartPanel chartPanel = new ChartPanel(jfreechart);
        chartPanel.setPreferredSize(new java.awt.Dimension(650, 370));

        FileOutputStream output;
        try {
            output = new FileOutputStream("successGlobalRates" + pf + ".jpg");
            ChartUtilities.writeChartAsJPEG(output, 1.0f, jfreechart, 650, 370, null);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MessagesSent1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MessagesSent1.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jfreechart;
    }

    /*public static JPanel createDemoPanel() {
     JFreeChart jfreechart = createChart(createDataset());
     return new ChartPanel(jfreechart);
     }*/
    private static String getTitle(ArrayList<Double> pf) {
        String s = "pf=";

        for (int i = 0; i < pf.size(); i++) {
            s += pf.get(i);
            if (i != pf.size() - 1) {
                s += " and ";
            }
        }
        return s;
    }

    private static boolean isInMode(String[] aMode, String mode) {
        for (String temp : aMode) {
            if (temp.equals(mode)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param args
     */
    public static void main(String args[]) {
        SucessfulRatesGlobal itemlabeldemo2 = new SucessfulRatesGlobal("Sucess Rates");
        itemlabeldemo2.pack();
        RefineryUtilities.centerFrameOnScreen(itemlabeldemo2);
        itemlabeldemo2.setVisible(true);
    }

    private static String getTechniqueName(String mode) {
        switch (mode) {
            case "sandclw":
                return "SandC with Lw";
            case "lwsandc2":
                return "Lw and C";
            case "lwsandc":
                return "Lw and C-Lw";
            case "lwphevap2":
                return "C and Evap";
            case "lwphevap":
                return "C-Lw and Evap";
            default:
                return mode;
        }
    }

    private static class CustomRenderer extends BarRenderer {

        /**
         * The colors.
         */
        private Paint[] colors;

        /**
         * Creates a new renderer.
         *
         * @param colors the colors.
         */
        public CustomRenderer(final Paint[] colors) {
            this.colors = colors;
        }

        /**
         * Returns the paint for an item. Overrides the default behaviour
         * inherited from AbstractSeriesRenderer.
         *
         * @param row the series.
         * @param column the category.
         *
         * @return The item color.
         */
        public Paint getItemPaint(final int row, final int column) {
            return this.colors[column % this.colors.length];
        }
    }

}
