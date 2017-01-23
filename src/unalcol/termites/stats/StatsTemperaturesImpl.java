/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.stats;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import unalcol.termites.World.StateProps;
import unalcol.termites.World.Termite;
import unalcol.termites.World.World;

/**
 *
 * @author Arles Rodriguez <arles.rodriguez@gmail.com>
 */
public class StatsTemperaturesImpl {

    private String reportFile;

    /**
     *
     * @param f
     */
    public StatsTemperaturesImpl(String f) {
        this.reportFile = f;
    }

    Hashtable getStatisticsInteger(World w) {
        Hashtable Statistics = new Hashtable();
        int right = 0;
        int wrong = 0;

        int n = w.getAgents().size();

        StateProps[][] st = w.getStates();

        ArrayList<Double> data = new ArrayList<>();
        ArrayList<Double> msgin = new ArrayList<>();
        ArrayList<Double> msgout = new ArrayList<>();
        ArrayList<Double> explTerrain = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            int count = 0;
            Termite t = (Termite) w.getAgent(i);
            Hashtable inf = (Hashtable) t.getAttribute("inf_i");
            String tmp;
            /*for (int j = 0; j < w.width; j++) {
                for (int k = 0; k < w.height; k++) {
                    tmp = j + "-" + k;
                    if ((float)inf.get(tmp) == st[j][k].temp) {
                        count++;
                    }
                }
            }*/
            count = inf.size();
            if (w.completeInfo(count)) {
                right++;
            } else {
                wrong++;
            }
            data.add((double) count);
            msgin.add((double) t.getnMsgRecv());
            msgout.add((double) t.getnMsgSend());
            explTerrain.add((double) t.getExploredTerrain());
        }

        StatisticsNormalDist stnd = new StatisticsNormalDist(data, data.size());

        StatisticsNormalDist stsend = new StatisticsNormalDist(msgout, msgout.size());
        StatisticsNormalDist strecv = new StatisticsNormalDist(msgin, msgin.size());
        StatisticsNormalDist explT = new StatisticsNormalDist(explTerrain, explTerrain.size());
        Statistics.put("wsize", (w.width * w.height)-w.wallsnumber);
        Statistics.put("mean", stnd.getMean());
        Statistics.put("stddev", stnd.getStdDev());
        Statistics.put("right", right);
        Statistics.put("wrong", wrong);
        Statistics.put("round", w.getRoundGetInfo());
        Statistics.put("avgSend", stsend.getMean());
        Statistics.put("avgRecv", strecv.getMean());
        Statistics.put("stdDevSend", stsend.getStdDev());
        Statistics.put("stdDevRecv", strecv.getStdDev());
        Statistics.put("avgExplTerrain", explT.getMean());
        Statistics.put("stdExplTerrain", explT.getStdDev());
        System.out.println("stats: " + Statistics);
        return Statistics;
    }

    /**
     *
     * @param w
     */
    public void printStatistics(World w) {
        Hashtable st = getStatisticsInteger(w);
        try {
            int nr = w.getAgents().size() - ((Integer) st.get("right") + (Integer) st.get("wrong"));
            //filename = getFileName() + "ds.trace";
            PrintWriter escribir;
            escribir = new PrintWriter(new BufferedWriter(new FileWriter(getReportFile(), true)));
            escribir.println(st.get("right") + "," + st.get("wrong") + "," + nr + "," + st.get("wsize") + "," + st.get("mean") + "," + st.get("stddev") + "," + st.get("round") + "," + st.get("avgSend")
                    + "," + st.get("avgRecv") + "," + st.get("stdDevSend") + "," + st.get("stdDevRecv") + "," + st.get("avgExplTerrain") + "," +st.get("stdExplTerrain"));
            escribir.close();

            /*escribir = new PrintWriter(new BufferedWriter(new FileWriter("best" + getReportFile(), true)));

            Termite t = w.getBest();
            if (t != null) {
                Hashtable A = (Hashtable)t.getPhHistory().clone();
                Hashtable B = (Hashtable)t.getInfHistory().clone();
                for (Iterator<Integer> iterator = A.keySet().iterator(); iterator.hasNext();) {
                    int key = iterator.next();
                    escribir.println(key + "," + A.get(key) + "," + B.get(key));
                }
            }
            escribir.close();
            */
        } catch (IOException ex) {
            Logger.getLogger(StatsTemperaturesImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the reportFile
     */
    public String getReportFile() {
        return reportFile;
    }

    /**
     * @param reportFile the reportFile to set
     */
    public void setReportFile(String reportFile) {
        this.reportFile = reportFile;
    }

}
