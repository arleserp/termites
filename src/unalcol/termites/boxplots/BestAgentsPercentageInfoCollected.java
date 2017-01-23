/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.boxplots;

/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * ----------------------
 * MessagesSent2.java
 * ----------------------
 * (C) Copyright 2003, 2004, by David Browning and Contributors.
 *
 * Original Author:  David Browning (for the Australian Institute of Marine Science);
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: MessagesSent2.java,v 1.12 2004/06/02 14:35:42 mungady Exp $
 *
 * Changes
 * -------
 * 21-Aug-2003 : Version 1, contributed by David Browning (for the Australian Institute of 
 *               Marine Science);
 * 27-Aug-2003 : Renamed BoxAndWhiskerCategoryDemo --> MessagesSent2, moved dataset creation
 *               into the demo (DG);
 *
 */
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
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
public class BestAgentsPercentageInfoCollected extends ApplicationFrame {

    private static String experimentsDir = ".";
    private static String mazeMode = "mazeon";
    private static String[] aMode;
    /**
     * Access to logging facilities.
     */
    private static final LogContext LOGGER = Log.createContext(BestAgentsPercentageInfoCollected.class);

    /**
     * Creates a new demo.
     *
     * @param title the frame title.
     * @param pf
     */
    public BestAgentsPercentageInfoCollected(final String title, ArrayList<Double> pf) {

        super(title);
        String sDirectorio = experimentsDir;

        Hashtable<String, List> info = new Hashtable();

        //String[] aMode = {"levywalk", "lwphevap", "hybrid", "hybrid3", "hybrid4"};
        /*for (String mode : aMode) {
         info.put(mode, new ArrayList());
         }*/
        createSampleDataset(pf, info);
        AddDataFailingExperiments(sDirectorio, pf, info);
        final BoxAndWhiskerCategoryDataset dataset = addDataSet(info);

        final CategoryAxis xAxis = new CategoryAxis("");
        //final NumberAxis yAxis = new NumberAxis("Information Collected");
        final NumberAxis yAxis = new NumberAxis("");

        yAxis.setAutoRangeIncludesZero(false);
        final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
        renderer.setFillBox(false);
        renderer.setToolTipGenerator(new BoxAndWhiskerToolTipGenerator());
        final CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);

        Font font = new Font("Dialog", Font.PLAIN, 13);
        xAxis.setTickLabelFont(font);
        yAxis.setTickLabelFont(font);
        yAxis.setLabelFont(font);

        final JFreeChart chart = new JFreeChart(
                "Information Collected " + getTitle(pf),
                new Font("SansSerif", Font.BOLD, 18),
                plot,
                true
        );
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(650, 370));
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
            output = new FileOutputStream("BestAgentsPercentageInfoCollected" + pf + mazeMode + ".jpg");
            ChartUtilities.writeChartAsJPEG(output, 1.0f, chart, 350, 350, null);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BestAgentsPercentageInfoCollected.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BestAgentsPercentageInfoCollected.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Creates a sample dataset.
     *
     * @return A sample dataset.
     */
    private void createSampleDataset(ArrayList<Double> Pf, Hashtable<String, List> info) {
        String sDirectorio = experimentsDir;
        File f = new File(sDirectorio);
        String extension;
        File[] files = f.listFiles();
        Scanner sc = null;

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
                maxIter = Integer.valueOf(filenamep[8]);
                //}

                /*System.out.println("psize:" + popsize);
                 System.out.println("pf:" + pf);
                 System.out.println("mode:" + mode);
                 System.out.println("maxIter:" + maxIter);
                 */
                //String[] aMode = {"sandclw", "lwsandc2", "lwsandc", "lwphevap2", "lwphevap"};
                //String[] aMode = {"levywalk", "lwphevap", "hybrid", "hybrid3", "hybrid4", "sequential"};
//                String[] aMode = {"turnoncontact", "lwsandc2", "lwsandc", "lwphevap2", "lwphevap"};
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

                        if (agentsCorrect > 0 && Pf.contains(pf)) {
                            //list.add(100.0);
                            if (info.get(mode + "+" + popsize) == null) {
                                info.put((mode + "+" + popsize), new ArrayList<Integer>());
                            }
                            info.get(mode + "+" + popsize).add(100.0);
                            //System.out.println(mode + "-entra!");
                            //list.add(new Double(averageExplored / (double) worldSize * 100));
                        }

                    }

                    LOGGER.debug("Adding series " + i);
                    LOGGER.debug(list.toString());
                    //if (Pf.contains(pf)) {
                        /*pf == 1.0E-4 || pf == 3.0E-4*/
                    /*if (Pf.size() == 1) {
                     dataset.add(list, popsize, getTechniqueName(mode));
                     } else {
                     dataset.add(list, String.valueOf(popsize) + "-" + pf, getTechniqueName(mode));
                     }*/

                    //}
                }
            }

        }
    }

    private static ArrayList<Double> getFailureProbs() {
        ArrayList<Double> pfs = new ArrayList<>();
        String sDirectorio = experimentsDir;
        System.out.println("experiments dir" + sDirectorio);
        File f = new File(sDirectorio);
        String extension;
        File[] files = f.listFiles();

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
                maxIter = Integer.valueOf(filenamep[8]);
                //}
                System.out.println("psize:" + popsize);
                System.out.println("pf:" + pf);
                System.out.println("mode:" + mode);
                System.out.println("maxIter:" + maxIter);

                System.out.println("pf:" + pf);
                if (!pfs.contains(pf)) {
                    pfs.add(pf);
                }
            }
        }
        System.out.println("pfs" + pfs);
        return pfs;
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

    private void AddDataFailingExperiments(String sDirectorio, ArrayList<Double> Pf, Hashtable<String, List> info) {
        File f = new File(sDirectorio);
        String extension;
        File[] files = f.listFiles();
        Hashtable<String, String> Pop = new Hashtable<>();
        Scanner sc = null;
        ArrayList<Integer> aPops = new ArrayList<>();
        ArrayList<Double> aPf = new ArrayList<>();
        ArrayList<String> aTech = new ArrayList<>();

        for (File file : files) {
            List list = new ArrayList();
            extension = "";
            int i = file.getName().lastIndexOf('.');
            int p = Math.max(file.getName().lastIndexOf('/'), file.getName().lastIndexOf('\\'));
            if (i > p) {
                extension = file.getName().substring(i + 1);
            }

            // System.out.println(file.getName() + "extension" + extension);
            if (file.isFile() && extension.equals("csv") && file.getName().startsWith("failInfo") && file.getName().contains(mazeMode)) {
                //System.out.println(file.getName());
                //System.out.println("get: " + file.getName());
                String[] filenamep = file.getName().split(Pattern.quote("+"));

                //System.out.println("file" + filenamep[8]);
                int popsizef = Integer.valueOf(filenamep[3]);
                double pff = Double.valueOf(filenamep[5]);
                String modef = filenamep[7];

                int worldSize = Integer.valueOf(filenamep[11]) * Integer.valueOf(filenamep[13]);
                /*System.out.println("psizef:" + popsizef);
                 System.out.println("pff:" + pff);
                 System.out.println("modef:" + modef);
                 */
//              String[] aMode = {"turnoncontact", "lwsandc2", "lwsandc", "lwphevap2", "lwphevap"};
                try {
                    sc = new Scanner(file);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(DataCollectedLatexConsolidatorSASOMessagesSend1.class
                            .getName()).log(Level.SEVERE, null, ex);
                }

                int agentId = -1;
                int countLines = 0;
                int roundNumber = -1;
                int infoCollected = -1;

                String[] data = null;
                while (sc.hasNext()) {
                    String line = sc.nextLine();
                    //System.out.println("line:" + line);
                    data = line.split(",");
                    agentId = Integer.valueOf(data[0]);
                    roundNumber = Integer.valueOf(data[1]);

                    if (infoCollected < Integer.valueOf(data[2])) {
                        infoCollected = Integer.valueOf(data[2]);
                    }
                    countLines++;
                }
                //System.out.println("lines" + countLines);
                if (countLines == popsizef && infoCollected != -1 && (Pf.contains(pff))) {
                    double datas = ((infoCollected * 1.0) / worldSize) * 100.0;
                    if (info.get(modef + "+" + popsizef) == null) {
                        info.put((modef + "+" + popsizef), new ArrayList<Integer>());
                    }
                    info.get(modef + "+" + popsizef).add(datas);
                }

                LOGGER.debug("Adding series " + i);
                LOGGER.debug(list.toString());
            }
        }

        System.out.println("info" + info);
    }

    private BoxAndWhiskerCategoryDataset addDataSet(Hashtable<String, List> info) {
        DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
        for (String key : info.keySet()) {
            System.out.println(key + ":" + info.get(key).size());
            String[] keyData = key.split(Pattern.quote("+"));
            dataset.add(info.get(key), keyData[1], getTechniqueName(keyData[0]));
        }
        //System.out.println("dataset" + dataset);
        return dataset;
    }

    //***************************************************************************
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
//        double pf = Double.valueOf(args[0]);
        //       System.out.println("pf:" + args[0]);

        /**
         * For saso paper
         */
        /*
         double pf = 0;
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
         // pfg3.add(1.0E-3);

         //Log.getInstance().addTarget(new PrintStreamLogTarget(System.out));
         final InformationCollected2 demo = new InformationCollected2("Information Collected", pf0);
         final InformationCollected2 demo1 = new InformationCollected2("Information Collected", pfg1);
         final InformationCollected2 demo2 = new InformationCollected2("Information Collected", pfg2);
         final InformationCollected2 demo3 = new InformationCollected2("Information Collected", pfg3);
         //demo.pack();
         //RefineryUtilities.centerFrameOnScreen(demo);
         //demo.setVisible(true);
         */
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

        ArrayList<Double> failureProbs = getFailureProbs();

        for (Double pf : failureProbs) {
            ArrayList<Double> pfi = new ArrayList<>();
            pfi.add(pf);
            final BestAgentsPercentageInfoCollected demo = new BestAgentsPercentageInfoCollected("Information Collected", pfi);
        }

        /* Main with all Pictures by pf */
        /*ArrayList<Double> pf0 = new ArrayList<>();
         ArrayList<Double> pf1 = new ArrayList<>();
         ArrayList<Double> pf3 = new ArrayList<>();
         ArrayList<Double> pf5 = new ArrayList<>();
         ArrayList<Double> pf7 = new ArrayList<>();
         ArrayList<Double> pf9 = new ArrayList<>();
         ArrayList<Double> pf01 = new ArrayList<>();

         /* For the web site */
        /*pf0.add(0.0);
         pf1.add(1.0E-4);
         pf3.add(3.0E-4);
         pf5.add(5.0E-4);
         pf7.add(7.0E-4);
         pf9.add(9.0E-4);
         pf01.add(1.0E-3);

         //pfg3.add(1.0E-3);
         //Log.getInstance().addTarget(new PrintStreamLogTarget(System.out));
         final BestAgentsPercentageInfoCollected demo = new BestAgentsPercentageInfoCollected("Information Collected", pf0);
         final BestAgentsPercentageInfoCollected demo1 = new BestAgentsPercentageInfoCollected("Information Collected", pf1);
         final BestAgentsPercentageInfoCollected demo2 = new BestAgentsPercentageInfoCollected("Information Collected", pf3);
         final BestAgentsPercentageInfoCollected demo3 = new BestAgentsPercentageInfoCollected("Information Collected", pf5);
         final BestAgentsPercentageInfoCollected demo4 = new BestAgentsPercentageInfoCollected("Information Collected", pf7);
         final BestAgentsPercentageInfoCollected demo5 = new BestAgentsPercentageInfoCollected("Information Collected", pf9);
         //final BestAgentsPercentageInfoCollected demo6 = new BestAgentsPercentageInfoCollected("Information Collected", pf01);
         */
    }
}
