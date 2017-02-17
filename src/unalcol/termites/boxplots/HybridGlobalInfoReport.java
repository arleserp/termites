/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.boxplots;

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
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.AbstractCategoryItemLabelGenerator;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import unalcol.termites.reports.DataCollectedLatexConsolidatorSASOMessagesSend1;

/**
 *
 * @author Arles Rodriguez <arles.rodriguez@gmail.com>
 */
public class HybridGlobalInfoReport extends ApplicationFrame {

    private static final long serialVersionUID = 1L;
    private static String experimentsDir = ".";
    private static String mazeMode = "mazeon";
    private static String[] aMode;

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
    public HybridGlobalInfoReport(String s) {
        super(s);
        createSuperGraph();

        /*
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
         setContentPane(chartpanel);*/
    }

    private static void createSuperGraph() {
        XYSeriesCollection juegoDatos = null;
        Hashtable<String, XYSeriesCollection> dataCollected = new Hashtable();
        DefaultCategoryDataset defaultcategorydataset = new DefaultCategoryDataset();
        String sDirectorio = experimentsDir;
        File f = new File(sDirectorio);
        String extension;
        File[] files = f.listFiles();
        Hashtable<String, String> Pop = new Hashtable<>();
        PrintWriter escribir;
        Scanner sc = null;
        double sucessfulExp = 0.0;
        for (File file : files) {
            extension = "";
            int i = file.getName().lastIndexOf('.');
            int p = Math.max(file.getName().lastIndexOf('/'), file.getName().lastIndexOf('\\'));
            if (i > p) {
                extension = file.getName().substring(i + 1);
            }
            // System.out.println(file.getName() + "extension" + extension);
            if (file.isFile() && extension.equals("csv") && file.getName().startsWith("dataCollected") && file.getName().contains(mazeMode)) {
                System.out.println(file.getName());
                System.out.println("get: " + file.getName());
                String[] filenamep = file.getName().split(Pattern.quote("+"));
                XYSeries globalInfo;

                System.out.println("file" + filenamep[9]);

                int popsize = Integer.valueOf(filenamep[3]);
                double pf = Double.valueOf(filenamep[5]);
                String mode = filenamep[7];

                int maxIter = -1;
                //if (!filenamep[8].isEmpty()) {
                maxIter = Integer.valueOf(filenamep[9]);
                //}
                int n = 0;
                int rho = 0;
                double evapRate = 0.0;
                //14, 16, 16
                if (mode.equals("hybrid")) {
                    n = Integer.valueOf(filenamep[15]);
                    rho = Integer.valueOf(filenamep[17]);
                    String[] tmp = filenamep[19].split(Pattern.quote("."));

                    System.out.println("history size:" + n);
                    System.out.println("rho:" + rho);
                    String sEvap = tmp[0] + "." + tmp[1];
                    evapRate = Double.valueOf(sEvap);
                }

                System.out.println("psize:" + popsize);
                System.out.println("pf:" + pf);
                System.out.println("mode:" + mode);
                System.out.println("maxIter:" + maxIter);

                //String[] aMode = {"random", "levywalk", "sandc", "sandclw"};
                //String[] aMode = {"lwphclwevap", "lwsandc2", "lwsandc", "lwphevap2", "lwphevap"};
                //String[] aMode = {"hybrid", "lwphevap", "levywalk"};
                //String[] aMode = {"levywalk", "lwphevap", "hybrid", "hybrid3", "hybrid4", "sequential"};
                String key;
                if (mode.equals("hybrid")) {
                    key = getTechniqueName(mode) + "+" + pf + "+" + rho + "+" + n + "+" + popsize + "+" + evapRate;
                } else {
                    key = getTechniqueName(mode) + "+" + pf + "+" + popsize + "+" + evapRate;
                }

                System.out.println("key" + key);
                if (isInMode(aMode, mode)) {
                    final List list = new ArrayList();
                    try {
                        sc = new Scanner(file);

                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(DataCollectedLatexConsolidatorSASOMessagesSend1.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                    int roundNumber = 0;
                    double globalInfoCollected = 0.0;

                    //ArrayList<Double> acSt = new ArrayList<>();
                    //ArrayList<Double> avgExp = new ArrayList<>();
                    String[] data = null;
                    globalInfo = new XYSeries("");
                    while (sc.hasNext()) {
                        String line = sc.nextLine();
                        //System.out.println("line:" + line);
                        data = line.split(",");
                        roundNumber = Integer.valueOf(data[0]);
                        globalInfoCollected = Double.valueOf(data[4]);

                        if (globalInfoCollected > 100) {
                            System.out.println("more than 100:" + file.getName());
                        } else {
                            globalInfo.add(roundNumber, globalInfoCollected);
                        }
                        //System.out.println("r" + roundNumber + "dc:" + globalInfoCollected);
                        //Add Data and generate statistics 
                        //acSt.add((double) agentsCorrect);
                        //avgExp.add(averageExplored);
                    }

                    if (!dataCollected.containsKey(key)) {
                        juegoDatos = new XYSeriesCollection();
                        juegoDatos.addSeries(globalInfo);
                        dataCollected.put(key, juegoDatos);
                    } else {
                        ((XYSeriesCollection) dataCollected.get(key)).addSeries(globalInfo);
                    }
                    //if (Pf.contains(pf)) {
                        /*if (mode.equals("hybrid")) {
                     String nameSeries = n + "-" + rho + "-" + evapRate;
                     defaultcategorydataset.addValue(((double) sucessfulExp) / acSt.size() * 100.0, "" + popsize, nameSeries);
                     } else {
                     defaultcategorydataset.addValue(((double) sucessfulExp) / acSt.size() * 100.0, "" + popsize, getTechniqueName(mode) + "\nPf:" + pf);
                     }*/
                    /*pf == 1.0E-4 || pf == 3.0E-4*/
                    //}
                }
            }
        }
        createChart(dataCollected);
    }

    private static void createChart(Hashtable<String, XYSeriesCollection> dataCollected) {
        System.out.println("dc" + dataCollected);
        for (String key : dataCollected.keySet()) {
            JFreeChart chart = ChartFactory.createXYLineChart(
                    key, "Round number", "GlobalInfo",
                    dataCollected.get(key), PlotOrientation.VERTICAL,
                    true, true, false);

            XYPlot xyPlot = (XYPlot) chart.getPlot();
            NumberAxis domain = (NumberAxis) xyPlot.getDomainAxis();
            domain.setRange(0.0, 10000.0);

            FileOutputStream output;
            try {
                System.out.println("Key: " + key);
                output = new FileOutputStream(key + mazeMode + ".jpg");
                ChartUtilities.writeChartAsJPEG(output, 1.0f, chart, 400, 400, null);

            } catch (FileNotFoundException ex) {
                Logger.getLogger(ECALAgentsRight.class
                        .getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ECALAgentsRight.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

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
        if (args.length > 0) {
            experimentsDir = args[0];
        }

        if (args.length > 1) {
            mazeMode = args[1];
        }

        aMode = new String[args.length - 2];

        for (int i = 2; i < args.length; i++) {
            aMode[i - 2] = args[i];
        }

        HybridGlobalInfoReport itemlabeldemo2 = new HybridGlobalInfoReport("Sucess Rates");
        //itemlabeldemo2.pack();
        //RefineryUtilities.centerFrameOnScreen(itemlabeldemo2);
        //itemlabeldemo2.setVisible(true);
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
