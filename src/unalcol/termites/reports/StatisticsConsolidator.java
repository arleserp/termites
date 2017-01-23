/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.reports;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import unalcol.termites.stats.StatisticsNormalDist;

/**
 *
 * @author Arles Rodriguez <arles.rodriguez@gmail.com>
 */
public class StatisticsConsolidator {

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        String sDirectorio = ".";
        File f = new File(sDirectorio);
        String extension;
        File[] files = f.listFiles();

        PrintWriter escribir;
        Scanner sc = null;
        try {
            escribir = new PrintWriter(new BufferedWriter(new FileWriter("consolidated.csv", true)));
            escribir.println("PopSize,worldsize,pf,mode,maxIter,Correct Avg,Correct StdDev,Info Collected Avg,Info Collected StdDev,Best Round Avg,BestRound StdDev,MsgSend Avg,MsgSend StdDev,MsgRecv Avg,MsgRecv StdDev,Info by Agent Avg,Info by Agent StdDev");

            for (File file : files) {
                extension = "";
                int i = file.getName().lastIndexOf('.');
                int p = Math.max(file.getName().lastIndexOf('/'), file.getName().lastIndexOf('\\'));
                if (i > p) {
                    extension = file.getName().substring(i + 1);
                }

                // System.out.println(file.getName() + "extension" + extension);
                if (file.isFile() && extension.equals("csv") && file.getName().startsWith("experiment")) {
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

                    try {
                        sc = new Scanner(file);
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(StatisticsConsolidator.class.getName()).log(Level.SEVERE, null, ex);
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

                        /*System.out.println("ac" + agentsCorrect);
                        //agentsIncorrect = Integer.valueOf(data[1]); // not used
                        System.out.println("ws" + worldSize);
                        System.out.println("acgExpl: " + averageExplored);
                        // data[3] stdavgExplored - not used
                        System.out.println("Best Round Number:" + bestRoundNumber);
                        System.out.println("Avg Send: " + avgSend);
                        System.out.println("Avg Recv:" + avgRecv);
                        System.out.println("Avg Ind Explored:" + avgdataExplInd);
                        */
                        //Add Data and generate statistics 
                        acSt.add((double) agentsCorrect);
                        avgExp.add(averageExplored);
                        bestR.add((double) bestRoundNumber);
                        avSnd.add(avgSend);
                        avRecv.add(avgRecv);
                        avIndExpl.add(avgdataExplInd);
                    }

                    //Write statistics in file;
                    StatisticsNormalDist acst = new StatisticsNormalDist(acSt, acSt.size());
                    StatisticsNormalDist expst = new StatisticsNormalDist(avgExp, avgExp.size());
                    StatisticsNormalDist brst = new StatisticsNormalDist(bestR, bestR.size());
                    StatisticsNormalDist avsd = new StatisticsNormalDist(avSnd, avSnd.size());
                    StatisticsNormalDist avrc = new StatisticsNormalDist(avRecv, avRecv.size());
                    StatisticsNormalDist avde = new StatisticsNormalDist(avIndExpl, avIndExpl.size());


                    /*Format PopSize|worldsize|pf|mode|maxIter|
                     Correct Avg|Correct StdDev|
                     Info Collected Avg|Info Collected StdDev|
                     Best Round Avg|BestRound StdDev|
                     MsgSend Avg|MsgSend StdDev|
                     MsgRecv Avg|MsgRecv StdDev|
                     Info by Agent Avg|Info by Agent StdDev
                     */
                    escribir.println(popsize + "," + worldSize + "," + pf + "," + mode + "," + maxIter + ","
                            + acst.getMean() + "," + acst.getStdDev() + ","
                            + expst.getMean() + "," + expst.getStdDev() + ","
                            + brst.getMean() + "," + brst.getStdDev() + ","
                            + avsd.getMean() + "," + avsd.getStdDev() + ","
                            + avrc.getMean() + "," + avrc.getStdDev() + ","
                            + avde.getMean() + "," + avde.getStdDev());
                    
                }
            }
            escribir.close();
        } catch (IOException ex) {
            Logger.getLogger(StatisticsConsolidator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
