/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.boxplots;
/**
 *
 * @author Arles Rodriguez <arles.rodriguez@gmail.com>
 */
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.Log;
import org.jfree.util.LogContext;
import unalcol.termites.reports.DataCollectedLatexConsolidatorSASOMessagesSend1;

/**
 * Demonstration of a box-and-whisker chart using a {@link CategoryPlot}.
 *
 * @author David Browning
 */
public class HybridInformationCollected extends ApplicationFrame {

    private static String experimentsDir = ".";
    private static String mazeMode = "mazeon";
    private static String[] aMode;

    /**
     * Access to logging facilities.
     */
    private static final LogContext LOGGER = Log.createContext(HybridInformationCollected.class);

    /**
     * Creates a new demo.
     *
     * @param title the frame title.
     * @param pf
     */
    public HybridInformationCollected(final String title, ArrayList<Double> pf) {
        super(title);
        final BoxAndWhiskerCategoryDataset dataset = createSampleDataset(pf);
        final CategoryAxis xAxis = new CategoryAxis("");
        final NumberAxis yAxis = new NumberAxis("");
        //final NumberAxis yAxis = new NumberAxis("Information Collected");
        yAxis.setAutoRangeIncludesZero(false);
        final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
        renderer.setFillBox(false);
        renderer.setToolTipGenerator(new BoxAndWhiskerToolTipGenerator());
        final CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);

        Font font = new Font("Dialog", Font.PLAIN, 12);
        xAxis.setTickLabelFont(font);
        yAxis.setTickLabelFont(font);
        yAxis.setLabelFont(font);

        final JFreeChart chart = new JFreeChart(
                "Total of Information Collected " + getTitle(pf),
                new Font("SansSerif", Font.BOLD, 12),
                plot,
                true
        );

        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(1300, 370));
        setContentPane(chartPanel);
        TextTitle legendText = null;
        if (pf.size() == 1) {
            legendText = new TextTitle("Population Size");
        } else {
            legendText = new TextTitle("Population Size - Probability of Failure");
        }

        legendText.setFont(font);
        legendText.setPosition(RectangleEdge.BOTTOM);
        chart.addSubtitle(legendText);
        chart.getLegend().setItemFont(font);
        FileOutputStream output;
        try {
            output = new FileOutputStream("totalInformationCollected" + pf + mazeMode + ".jpg");
            ChartUtilities.writeChartAsJPEG(output, 1.0f, chart, 1300, 400, null);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HybridInformationCollected.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HybridInformationCollected.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Creates a sample dataset.
     *
     * @return A sample dataset.
     */
    private BoxAndWhiskerCategoryDataset createSampleDataset(ArrayList<Double> Pf) {

        final int seriesCount = 5;
        final int categoryCount = 4;
        final int entityCount = 22;
        String sDirectorio = experimentsDir;
        File f = new File(sDirectorio);
        String extension;
        File[] files = f.listFiles();
        Hashtable<String, String> Pop = new Hashtable<>();
        PrintWriter escribir;
        Scanner sc = null;
        ArrayList<Integer> aPops = new ArrayList<>();
        ArrayList<Double> aPf = new ArrayList<>();
        ArrayList<String> aTech = new ArrayList<>();

        final DefaultBoxAndWhiskerCategoryDataset dataset
                = new DefaultBoxAndWhiskerCategoryDataset();

        for (File file : files) {
            extension = "";
            int i = file.getName().lastIndexOf('.');
            int p = Math.max(file.getName().lastIndexOf('/'), file.getName().lastIndexOf('\\'));
            if (i > p) {
                extension = file.getName().substring(i + 1);
            }

            // System.out.println(file.getName() + "extension" + extension);
            if (file.isFile() && extension.equals("csv") && file.getName().startsWith("experiment") && file.getName().contains(mazeMode)) {
                System.out.println(file.getName());
                System.out.println("get: " + file.getName());
                String[] filenamep = file.getName().split(Pattern.quote("+"));

                System.out.println("file" + filenamep[8]);

                int popsize = Integer.valueOf(filenamep[2]);
                double pf = Double.valueOf(filenamep[4]);
                String mode = filenamep[6];

                int maxIter = -1;
                //if (!filenamep[8].isEmpty()) {
                int n = 0;
                int rho = 0;
                double evapRate = 0.0;
                //14, 16, 16
                if (mode.equals("hybrid")) {
                    n = Integer.valueOf(filenamep[14]);
                    rho = Integer.valueOf(filenamep[16]);
                    String[] tmp = filenamep[18].split(Pattern.quote("."));

                    System.out.println("history size:" + n);
                    System.out.println("rho:" + rho);
                    String sEvap = tmp[0] + "." + tmp[1];
                    evapRate = Double.valueOf(sEvap);
                }

                System.out.println("psize:" + popsize);
                System.out.println("pf:" + pf);
                System.out.println("mode:" + mode);
                System.out.println("maxIter:" + maxIter);
                System.out.println("evap:" + evapRate);
                //String[] aMode = {"hybrid", "lwphevap"};

                if (/*Pf == pf && */isInMode(aMode, mode)) {
                    final List list = new ArrayList();
                    try {
                        sc = new Scanner(file);

                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(DataCollectedLatexConsolidatorSASOMessagesSend1.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                    int agentsCorrect = 0;
                    int worldSize = 0;
                    double averageExplored = 0.0;
                    int bestRoundNumber = 0;
                    double avgSend = 0;
                    double avgRecv = 0;
                    double avgdataExplInd = 0;
                    ArrayList<Double> acSt = new ArrayList<>();
                    ArrayList<Double> avgExp = new ArrayList<>();
                    ArrayList<Double> bestR = new ArrayList<>();
                    ArrayList<Double> avSnd = new ArrayList<>();
                    ArrayList<Double> avRecv = new ArrayList<>();
                    ArrayList<Double> avIndExpl = new ArrayList<>();

                    String[] data = null;
                    while (sc.hasNext()) {
                        String line = sc.nextLine();
                        //System.out.println("line:" + line);
                        data = line.split(",");
                        agentsCorrect = Integer.valueOf(data[0]);
                        //agentsIncorrect = Integer.valueOf(data[1]); // not used
                        worldSize = Integer.valueOf(data[3]);
                        averageExplored = Double.valueOf(data[4]);
                        // data[3] stdavgExplored - not used
                        bestRoundNumber = Integer.valueOf(data[6]);
                        avgSend = Double.valueOf(data[7]);
                        avgRecv = Double.valueOf(data[8]);
                        avgdataExplInd = Double.valueOf(data[11]);

                        //Add Data and generate statistics 
                        acSt.add((double) agentsCorrect);
                        avgExp.add(averageExplored);

                        avSnd.add(avgSend);
                        avRecv.add(avgRecv);
                        avIndExpl.add(avgdataExplInd);

                        list.add(new Double(averageExplored / (double) worldSize * 100));
                    }
                    LOGGER.debug("Adding series " + i);
                    LOGGER.debug(list.toString());
                    if (Pf.contains(pf)) {
                        /*pf == 1.0E-4 || pf == 3.0E-4*/
                        if (Pf.size() == 1) {
                            if (mode.equals("hybrid")) {
                                dataset.add(list, popsize, /*getTechniqueName(mode) + */ rho + "-" + evapRate + "-" + n);
                            } else {
                                dataset.add(list, popsize, getTechniqueName(mode));
                            }
                        } else {
                            if (mode.equals("hybrid")) {
                                dataset.add(list, String.valueOf(popsize) + "-" + pf, /*getTechniqueName(mode) +*/ rho + "-" + evapRate + "-" + n);
                            } else {
                                dataset.add(list, String.valueOf(popsize) + "-" + pf, getTechniqueName(mode));
                            }
                        }
                    }
                }
            }

        }

        /*for (int i = 0; i < seriesCount; i++) {
         for (int j = 0; j < categoryCount; j++) {
         final List list = new ArrayList();
         // add some values...
         for (int k = 0; k < entityCount; k++) {
         final double value1 = 10.0 + Math.random() * 3;
         list.add(new Double(value1));
         final double value2 = 11.25 + Math.random(); // concentrate values in the middle
         list.add(new Double(value2));
         }
         LOGGER.debug("Adding series " + i);
         LOGGER.debug(list.toString());
         dataset.add(list, "Series " + i, " Type " + j);
         }

         }*/
        return dataset;
    }

    // ****************************************************************************
    // * JFREECHART DEVELOPER GUIDE                                               *
    // * The JFreeChart Developer Guide, written by David Gilbert, is available   *
    // * to purchase from Object Refinery Limited:                                *
    // *                                                                          *
    // * http://www.object-refinery.com/jfreechart/guide.html                     *
    // *                                                                          *
    // * Sales are used to provide funding for the JFreeChart project - please    * 
    // * support us so that we can continue developing free software.             *
    // ****************************************************************************
    /**
     * For testing from the command line.
     *
     * @param args ignored.
     */
    public static void main(final String[] args) {
        //double pf = Double.valueOf(args[0]);
        //       System.out.println("pf:" + args[0]);
        //double pf = 0;
        /* For the Paper */

        /*
         ArrayList<Double> pf0 = new ArrayList<>();
         ArrayList<Double> pfg1 = new ArrayList<>();
         ArrayList<Double> pfg2 = new ArrayList<>();
         ArrayList<Double> pfg3 = new ArrayList<>();

        
         pf0.add(0.0);
         pfg1.add(1.0E-4);
         pfg1.add(3.0E-4);
         pfg2.add(5.0E-4);
         pfg2.add(7.0E-4);
         pfg3.add(9.0E-4);

         //pfg3.add(1.0E-3);
         //Log.getInstance().addTarget(new PrintStreamLogTarget(System.out));
         final InformationCollected1 demo = new InformationCollected1("Information Collected", pf0);
         final InformationCollected1 demo1 = new InformationCollected1("Information Collected", pfg1);
         final InformationCollected1 demo2 = new InformationCollected1("Information Collected", pfg2);
         final InformationCollected1 demo3 = new InformationCollected1("Information Collected", pfg3);
         //demo.pack();
         //RefineryUtilities.centerFrameOnScreen(demo);
         //demo.setVisible(true);
         **/
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

        /* Main with all Pictures by pf */
        ArrayList<Double> pf0 = new ArrayList<>();
        ArrayList<Double> pf1 = new ArrayList<>();
        ArrayList<Double> pf3 = new ArrayList<>();
        ArrayList<Double> pf5 = new ArrayList<>();
        ArrayList<Double> pf7 = new ArrayList<>();
        ArrayList<Double> pf9 = new ArrayList<>();
        ArrayList<Double> pf01 = new ArrayList<>();

        /* For the Paper */
        pf0.add(0.0);
        pf1.add(1.0E-4);
        pf3.add(3.0E-4);
        pf5.add(5.0E-4);
        pf7.add(7.0E-4);
        pf9.add(9.0E-4);
        pf01.add(1.0E-3);

        //pfg3.add(1.0E-3);
        //Log.getInstance().addTarget(new PrintStreamLogTarget(System.out));
        final HybridInformationCollected demo = new HybridInformationCollected("Information Collected", pf0);
        final HybridInformationCollected demo1 = new HybridInformationCollected("Information Collected", pf1);
        final HybridInformationCollected demo2 = new HybridInformationCollected("Information Collected", pf3);
        final HybridInformationCollected demo3 = new HybridInformationCollected("Information Collected", pf5);
        final HybridInformationCollected demo4 = new HybridInformationCollected("Information Collected", pf7);
        final HybridInformationCollected demo5 = new HybridInformationCollected("Information Collected", pf9);
        final HybridInformationCollected demo6 = new HybridInformationCollected("Information Collected", pf01);

    }

    private boolean isInMode(String[] aMode, String mode) {
        for (String temp : aMode) {
            if (temp.equals(mode)) {
                return true;
            }
        }
        return false;
    }

    private String getTechniqueName(String mode) {
        return mode;
    }

    private String getTitle(ArrayList<Double> pf) {
        String s = "with a pf=";

        for (int i = 0; i < pf.size(); i++) {
            s += pf.get(i);
            if (i != pf.size() - 1) {
                s += " and pf=";
            }
        }
        return s;
    }

}
