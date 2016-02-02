/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.Program;

import java.util.ArrayList;
import unalcol.termites.VM.TermitesVM;
import java.util.LinkedList;
import java.util.Queue;
import unalcol.termites.TermitesLanguage.TermitesLanguage;
import unalcol.termites.World.Termite;
import unalcol.random.RandomUtil;
import static unalcol.termites.Program.MotionUtil.Roulette;
import unalcol.termites.Runnable.AppMain;

/**
 *
 * @author arles.rodriguez
 */
public class HybridPheromoneLevyWalkProgramImpl extends TermitesMovementProgram {

    int counter = 0;
    boolean ret = false;
    private int agentIterator;
    private Queue<Integer> movQueue;
//    TermitesVM termitesvm = new TermitesVMImpl();
    int iterationFailure = 0;
    public TermitesVM termitesvm = null;
    //defines an error vector
    //float myerror[] = new float[super.language.getActionsNumber()];
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

    public HybridPheromoneLevyWalkProgramImpl(TermitesLanguage _language, int iterator, float probFailure, int failuresByTermite) {
        super(_language, 0);
        this.agentIterator = iterator;
        this.movQueue = new LinkedList<Integer>();
        pf = probFailure;
        strategies = new float[2];
        strategies[0] = (float) Math.random();
        strategies[1] = (float) Math.random();
        //strategies[2] = (float) Math.random();
        normalizeStrategies();
        alpha = (float) Math.random();
        acumulator = 0;
        dirPoslw = (int) (Math.random() * 8.0);
        T = 1;
        n = AppMain.historySize;
        rho = AppMain.hybridThreshold;
        history = new boolean[n];
        roundNumber = 0;
//((HybridPheromoneLevyWalkProgramImpl)(world.getAgent(counter)).getProgram()).strategies[0] = 0.1f;
//System.out.println("oooofff" + probFailure);
        // termitesvm = new TermitesVMBinaryImpl(iterationFailure, iterator, probFailure, failuresByTermite);
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
        //System.out.println("dirpos" + dirPos);
        for (int k = 0; k < pheromone.length; k++) {
            if (pheromone[k] == max) {
                temp.add(k);
            }
        }
        //System.out.println("temp" + temp);
        //System.out.print("1");
        dirPos = temp.get(RandomUtil.nextInt(temp.size()));
        //System.out.print("," + dirPos + "\n");
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
                //lastRoundReward = roundNumber;
            } else {
                PunishStrategy();
            }
            lastStrategy = Roulette(strategies);
            deletehistory();
        }
        roundNumber++;

        if (lastStrategy == 0) {
            //System.out.println("phero st!");
            //if (seekingStatus == Termite.CARRYING) {
            return carry(pheromone, proximitySensor, termitesNeighbor);
            //}
        } else {
            //System.out.println("Levy Walk!");
            return LevyWalk(proximitySensor, termitesNeighbor);
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
