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
import java.util.Collections;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import unalcol.termites.stats.StatisticsNormalDist;

/**
 *
 * @author Arles Rodriguez
 */
public class DataCollectedLatexConsolidatorSASOBestRound {

    public static void main(String[] args) {
        String sDirectorio = ".";
        File f = new File(sDirectorio);
        String extension;
        File[] files = f.listFiles();
        Hashtable<String, String> Pop = new Hashtable<>();
        PrintWriter escribir;
        Scanner sc = null;
        ArrayList<Integer> aPops = new ArrayList<>();
        ArrayList<Double> aPf = new ArrayList<>();
        ArrayList<String> aTech = new ArrayList<>();
        try {
            escribir = new PrintWriter(new BufferedWriter(new FileWriter("bestround.tex", true)));
            escribir.println("\\begin{tabular}{|r|r|r|r|r|r|}");
            escribir.println("\\hline\n"
                    + " & & \\multicolumn{4}{ |c| }{Round Number} \\\\ \n"
                    + " \\cline{2-6}");
            escribir.println("\\multicolumn{1}{|c|}{Pop} & \\multicolumn{1}{|c|}{$pf$} & \\multicolumn{1}{ |c| }{Random Expl.} & \\multicolumn{1}{ |c| }{Levy Walk} & \\multicolumn{1}{ |c| }{Seekers and Carriers}& \\multicolumn{1}{ |c| }{Seeks and Carriers with Levy Walks}\\\\ ");
            escribir.println("\\hline");
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
                        Logger.getLogger(DataCollectedLatexConsolidatorSASOBestRound.class.getName()).log(Level.SEVERE, null, ex);
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

                        if (bestRoundNumber != 0 && bestRoundNumber != -1) {
                            bestR.add((double) bestRoundNumber);
                        }
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
                    if (!aPops.contains(popsize)) {
                        aPops.add(popsize);
                    }
                    if (!aPf.contains(pf)) {
                        aPf.add(pf);
                    }
                    if (!aTech.contains(mode)) {
                        aTech.add(mode);
                    }
                    String key = popsize + "-" + mode + "-" + pf;
                    //Hashtable<Double, Object> probf = new Hashtable<>();
                    //Hashtable<String, String> tech = new Hashtable<>();
                    //System.out.println("add" + mode);
                    Pop.put(key, String.format("$%.2f \\pm %.2f$", brst.getMean(), brst.getStdDev()));
                    /*escribir.println(popsize + "," + worldSize + "," + pf + "," + mode + "," + maxIter + ","
                     + acst.getMean() + "," + acst.getStdDev() + ","
                     + expst.getMean() + "," + expst.getStdDev() + ","
                     + brst.getMean() + "," + brst.getStdDev() + ","
                     + avsd.getMean() + "," + avsd.getStdDev() + ","
                     + avrc.getMean() + "," + avrc.getStdDev() + ","
                     + avde.getMean() + "," + avde.getStdDev());*/
                }
            }

            String[] aMode = {"random", "levywalk", "sandc", "sandclw"};
            Collections.sort(aPops);
            Collections.sort(aPf);
            Collections.reverse(aPf);
            boolean firstline = true;
            for (int ipop : aPops) {

                System.out.println("i" + ipop);
                int cpf = 0;
                for (double fpf : aPf) {
                    int c = 0;
                    for (String smode : aMode) {
                        String hKey = ipop + "-" + smode + "-" + fpf;
                        System.out.println("hKey" + hKey);
                        if (Pop.containsKey(hKey)) {
                            System.out.println("contain!");
                            if (firstline) {
                                cpf++;
                                System.out.println("cpf" + cpf);
                                if (cpf == (int) (aPf.size() / 2)) {
                                    escribir.print(ipop + " & " + fpf + " & ");
                                } else {
                                    escribir.print(" & " + fpf + " & ");
                                }
                                firstline = false;
                            }
                            escribir.print(Pop.get(hKey));
                            c++;
                            if (aMode.length != c) {
                                escribir.print(" & ");
                            }

                        }
                    }
                    escribir.println("\\\\");
                    escribir.println("\\cline{2-6}");
                    firstline = true;
                }
                escribir.println("\\hline");
                escribir.println("\\hline");

            }
            System.out.println("Pop" + Pop);
            escribir.println("\\end{tabular}");

            escribir.close();
        } catch (IOException ex) {
            Logger.getLogger(DataCollectedLatexConsolidatorSASOBestRound.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
