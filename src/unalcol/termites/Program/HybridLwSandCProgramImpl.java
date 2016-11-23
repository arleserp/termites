/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.Program;

import java.util.ArrayList;
import unalcol.termites.VM.TermitesVM;
import unalcol.termites.TermitesLanguage.TermitesLanguage;
import unalcol.random.RandomUtil;
import unalcol.termites.Runnable.AppMain;

/**
 *
 * @author arles.rodriguez
 */
public class HybridLwSandCProgramImpl extends TermitesMovementProgram {

    int counter = 0;
    boolean ret = false;
    int iterationFailure = 0;
    public TermitesVM termitesvm = null;
    public double pf = 0;
    public float[] strategies;
    public int rho;
    public int n;
    public boolean[] history;

    float alpha;
    float acumulator;
    int dirPoslw;
    float T;
    int lastStrategy = 0;
    int roundNumber;
    int lastRoundReward = 0;

    private boolean proximitySensor(boolean[] termitesNeighbor) {
        for (int i = 0; i < termitesNeighbor.length; i++) {
            if (termitesNeighbor[i]) {
                return true;
            }
        }
        return false;
    }

    int LevyWalk(boolean proximitySensor, boolean[] termitesNeighbor) {
        acumulator += alpha;
        if (acumulator >= T || proximitySensor || proximitySensor(termitesNeighbor)) {
            alpha = (float) Math.random();
            dirPoslw = (int) (Math.random() * 8.0);
            acumulator = 0;
        }
        return dirPoslw;
    }

    public HybridLwSandCProgramImpl(TermitesLanguage _language, float probFailure, int failuresByTermite) {
        super(_language, 0);
        pf = probFailure;
        strategies = new float[3];
        strategies[0] = (float) Math.random();
        strategies[1] = (float) Math.random();
        strategies[2] = (float) Math.random();
        normalizeStrategies();
        alpha = (float) Math.random();
        acumulator = 0;
        dirPoslw = (int) (Math.random() * 8.0);
        T = 1;
        n = AppMain.historySize;
        rho = AppMain.hybridThreshold;
        history = new boolean[n];
        roundNumber = 0;
    }

    int Roulette(float[] pheromone) {
        //System.out.println("roulette");
        float sum = 0;
        for (int k = 0; k < pheromone.length; k++) {
            sum += pheromone[k];
        }
        double rand = (double) (Math.random() * sum);
        sum = 0;
        int mov = 0;
        for (int k = 0; k < pheromone.length; k++) {
            sum += pheromone[k];
            if (rand < sum) {
                mov = k;
                break;
            }
        }
        return mov;
    }

    public boolean isStrategyCarrier() {
        return lastStrategy == 0;
    }
    
    public boolean isStrategySeeker() {
        return lastStrategy == 2;
    }
    
    public boolean isStrategyLevy() {
        return lastStrategy == 1;
    }



    public void normalizeStrategies() {
        float sum = 0;
        // System.out.println("carriers:[0]" + strategies[0] + ", LevyWalk[1]" + strategies[1]);
        for (float st : strategies) {
            sum += st;
        }
        //System.out.println("sum" + sum);
        for (int i = 0; i < strategies.length; i++) {
            strategies[i] /= sum;
        }
        // System.out.println("after: st[0]" + strategies[0] + ", st[1]" + strategies[1]);
    }

    public void RewardStrategy() {
        float delta = (float) Math.random();
        strategies[lastStrategy] += delta * strategies[lastStrategy];
        normalizeStrategies();
    }

    private void PunishStrategy() {
        float delta = (float) Math.random();
        strategies[lastStrategy] -= delta * strategies[lastStrategy];
        normalizeStrategies();
    }

    public int carry(float[] pheromone, boolean proximitySensor, boolean[] termitesNeighbor) {
        int dirPos = 0;
        ArrayList<Integer> temp = new ArrayList();
        for (int k = 0; k < pheromone.length; k++) {
            if (pheromone[k] != -1) {
                dirPos = k;
                break;
            }
        }

        for (int k = dirPos + 1; k < pheromone.length; k++) {
            if (pheromone[k] != -1 && pheromone[dirPos] > pheromone[k]) {
                dirPos = k;
            }
        }

        //store location with the min amount of pheromone
        float min = pheromone[dirPos];
        for (int k = 0; k < pheromone.length; k++) {
            if (pheromone[k] == min) {
                temp.add(k);
            }
        }
        dirPos = temp.get(RandomUtil.nextInt(temp.size()));
        return dirPos;
    }

    public static int seek(float[] pheromone) {
        ArrayList<Integer> temp = new ArrayList();
        int dirPos = 0;
        for (int k = 0; k < pheromone.length; k++) {
            if (pheromone[k] != -1.0) {
                dirPos = k;
                break;
            }
        }
        //System.out.println("maximum");
        for (int k = 1; k < pheromone.length; k++) {
            if (pheromone[k] != -1.0 && pheromone[dirPos] < pheromone[k]) {
                dirPos = k;
            }
        }
        //store location with the max amount of pheromone
        float max = pheromone[dirPos];
        for (int k = 0; k < pheromone.length; k++) {
            if (pheromone[k] == max) {
                temp.add(k);
            }
        }
        dirPos = temp.get(RandomUtil.nextInt(temp.size()));
        return dirPos;
    }

    @Override
    public int accion(float[] temps, float[] pheromone, boolean[] termitesNeighbor, int seekingStatus, String message, boolean proximitySensor, boolean[] MT) {
        /* If termite has a message then react to this message */
        if (Math.random() < pf) {
            return 11; //Die
        }

        if (roundNumber % n == 0) {
            if (countOnes()) {
                RewardStrategy();
            } else {
                PunishStrategy();
            }
            lastStrategy = Roulette(strategies);
            deletehistory();
        }
        roundNumber++;

        if (lastStrategy == 0) {
            return carry(pheromone, proximitySensor, termitesNeighbor);
        } else if (lastStrategy == 1) {
            //System.out.println("Levy Walk!");
            return LevyWalk(proximitySensor, termitesNeighbor);
        } else {
            return seek(pheromone);
        }
    }

    public TermitesVM getTermitesvm() {
        return termitesvm;
    }

    public void addHistory(boolean b) {
        history[roundNumber % n] = b;
    }

    private boolean countOnes() {
        int count = 0;
        boolean r = false;
        for (boolean x : history) {
            if (x) {
                count++;
            }
        }
        if (count >= rho) {
            r = true;
        }
        if (count > 0) {
            rho = count;
        }
        return r;
    }

    private void deletehistory() {
        for (int i = 0; i < history.length; i++) {
            history[i] = false;
        }
    }

}
