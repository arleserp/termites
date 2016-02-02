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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

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
import org.jfree.ui.RefineryUtilities;
import org.jfree.util.Log;
import org.jfree.util.LogContext;
import unalcol.termites.reports.DataCollectedLatexConsolidatorSASOMessagesSend1;

/**
 * Demonstration of a box-and-whisker chart using a {@link CategoryPlot}.
 *
 * @author David Browning
 */
public class RoundNumberGlobal extends ApplicationFrame {

    private static String experimentsDir = ".";
    private static String[] aMode;
    
    /**
     * Access to logging facilities.
     */
    private static final LogContext LOGGER = Log.createContext(RoundNumberGlobal.class);

    /**
     * Creates a new demo.
     *
     * @param title the frame title.
     */
    public RoundNumberGlobal(final String title, ArrayList<Double> pf) {
      
        super(title);

        final BoxAndWhiskerCategoryDataset dataset = createSampleDataset(pf);

        final CategoryAxis xAxis = new CategoryAxis("");
        //final NumberAxis yAxis = new NumberAxis("Round number");
        final NumberAxis yAxis = new NumberAxis("");
        yAxis.setAutoRangeIncludesZero(false);
        final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
        renderer.setFillBox(false);
        renderer.setToolTipGenerator(new BoxAndWhiskerToolTipGenerator());
        final CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);

        Font font = new Font("Dialog", Font.PLAIN, 16);
        xAxis.setTickLabelFont(font);
        yAxis.setTickLabelFont(font);
        yAxis.setLabelFont(font);

        final JFreeChart chart = new JFreeChart(
                "Round Number" + getTitle(pf),
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
            output = new FileOutputStream("roundGlobalNumber" + pf + ".jpg");
            ChartUtilities.writeChartAsJPEG(output, 1.0f, chart, 400, 400, null);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RoundNumberGlobal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RoundNumberGlobal.class.getName()).log(Level.SEVERE, null, ex);
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
        //String sDirectorio = "experiments\\2015-10-30-mazeoff";
        File f = new File(experimentsDir);
        String extension;
        File[] files = f.listFiles();
        Hashtable<String, String> Pop = new Hashtable<>();
        PrintWriter escribir;
        Scanner sc = null;
        ArrayList<Integer> aPops = new ArrayList<>();
        ArrayList<Double> aPf = new ArrayList<>();
        ArrayList<String> aTech = new ArrayList<>();

        Hashtable<String, List> info = new Hashtable();

        //String[] aMode = {"levywalk", "lwphevap", "hybrid", "hybrid3", "hybrid4"};
        for(String mode: aMode){
            info.put(mode, new ArrayList());
        }
        
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
            if (file.isFile() && extension.equals("csv") && file.getName().startsWith("dataCollected")) {
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
                //String[] aMode = {"levywalk", "lwphevap", "hybrid", "hybrid3", "hybrid4", "sequential"};
                //String[] aMode = {"levywalk", "lwphevap", "hybrid", "sequential"};

                if (/*Pf == pf && */isInMode(aMode, mode)) {
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

                    LOGGER.debug("Adding series " + i);
                    LOGGER.debug(list.toString());
                    if (Pf.contains(pf)) {
                        /*pf == 1.0E-4 || pf == 3.0E-4*/
                        if (Pf.size() == 1) {
                            dataset.add(list, popsize, getTechniqueName(mode));
                        } else {
                            dataset.add(list, String.valueOf(popsize) + "-" + pf, getTechniqueName(mode));
                        }
                    }
                }
            }

        }

        for (String key : info.keySet()) {
            System.out.println(key + ":" + info.get(key).size() / 30 * 100.0);
            dataset.add(info.get(key), 10, getTechniqueName(key));
        }

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
        if(args.length > 0){
            experimentsDir = args[0];
        }
        
        aMode = new String[args.length-1];
        
        for(int i=1; i < args.length; i++){
            aMode[i-1] = args[i];
        }
            
//        double pf = Double.valueOf(args[0]);
        //       System.out.println("pf:" + args[0]);
        /* SASO Paper */
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
         //pfg3.add(1.0E-3);

         //Log.getInstance().addTarget(new PrintStreamLogTarget(System.out));
         final RoundNumber1 demo = new RoundNumber1("Round Number", pf0);
         final RoundNumber1 demo1 = new RoundNumber1("Round Number", pfg1);
         final RoundNumber1 demo2 = new RoundNumber1("Round Number", pfg2);
         final RoundNumber1 demo3 = new RoundNumber1("Round Number", pfg3);
         //demo.pack();
         //RefineryUtilities.centerFrameOnScreen(demo);
         //demo.setVisible(true);
         */
        double pf = 0;
        ArrayList<Double> pf0 = new ArrayList<>();
        ArrayList<Double> pf1 = new ArrayList<>();
        ArrayList<Double> pf3 = new ArrayList<>();
        ArrayList<Double> pf5 = new ArrayList<>();
        ArrayList<Double> pf7 = new ArrayList<>();
        ArrayList<Double> pf9 = new ArrayList<>();
        ArrayList<Double> pf01 = new ArrayList<>();

        pf0.add(0.0);
        pf1.add(1.0E-4);
        pf3.add(3.0E-4);
        pf5.add(5.0E-4);
        pf7.add(7.0E-4);
        pf9.add(9.0E-4);
        pf01.add(1.0E-3);

        //pfg3.add(1.0E-3);
        //Log.getInstance().addTarget(new PrintStreamLogTarget(System.out));
        final RoundNumberGlobal demo = new RoundNumberGlobal("Round Number", pf0);
        final RoundNumberGlobal demo1 = new RoundNumberGlobal("Round Number", pf1);
        final RoundNumberGlobal demo2 = new RoundNumberGlobal("Round Number", pf3);
        final RoundNumberGlobal demo3 = new RoundNumberGlobal("Round Number", pf5);
        final RoundNumberGlobal demo4 = new RoundNumberGlobal("Round Number", pf7);
        final RoundNumberGlobal demo5 = new RoundNumberGlobal("Round Number", pf9);
        final RoundNumberGlobal demo6 = new RoundNumberGlobal("Round Number", pf01);
        //demo.pack();
        //RefineryUtilities.centerFrameOnScreen(demo);
        //demo.setVisible(true);
    }

    private boolean isInMode(String[] aMode, String mode) {
        for (String temp : aMode) {
            if (temp.equals(mode)) {
                return true;
            }
        }
        return false;
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


    private String getTitle(ArrayList<Double> pf) {
        String s = " with a pf=";

        for (int i = 0; i < pf.size(); i++) {
            s += pf.get(i);
            if (i != pf.size() - 1) {
                s += " and pf=";
            }
        }
        return s;
    }

}
