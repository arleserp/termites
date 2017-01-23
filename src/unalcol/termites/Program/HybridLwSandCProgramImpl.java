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
 * This class implements a Hybrid Levy Walk Seekers and Carriers algorithm.
 * Reward or punish a strategy among Levy Walks or Seekers and Carriers Generate
 * the next direction of movement for an agent. This class reward and punish a
 * strategy based on the result of the action stored in a history of boolean
 * values. Strategies are carriers|Levy|seekers
 *
 * @author Arles Rodriguez <arles.rodriguez@gmail.com>
 * @version 1.0
 */
public class HybridLwSandCProgramImpl extends TermitesMovementProgram {

    int counter = 0;
    boolean ret = false;
    int iterationFailure = 0;

    /**
     * failure probability in [0,1]
     */
    public double pf = 0;

    /**
     * array of strategies Seekers | Carriers | Levy Walks
     */
    public float[] strategies;

    /**
     * Threshold of success
     */
    public int rho;

    public int n;

    /**
     * history of ones and zeros based on success on collection task.
     */
    public boolean[] history;

    float alpha;
    float acumulator;
    int dirPoslw;
    float T;
    int lastStrategy = 0;
    int roundNumber;
    int lastRoundReward = 0;

    /**
     * This internal function evaluates if there are collisions with an agent or
     * border based on the boolean sensor of the agent
     *
     * @param termitesNeighbor sensor of vicinity true if there is another
     * termite or wall
     */
    private boolean proximitySensor(boolean[] termitesNeighbor) {
        for (int i = 0; i < termitesNeighbor.length; i++) {
            if (termitesNeighbor[i]) {
                return true;
            }
        }
        return false;
    }

    /**
     * LevyWalk implementation based on Beal paper: Beal, J. (2013).
     * Superdiffusive dispersion and mixing of swarms with reactive levy walks.
     * International Conference on Self-Adaptive and Self-Organizing Systems,
     * SASO, 141–148. http://doi.org/10.1109/SASO.2013.9
     *
     * @param proximitySensor represents if there is an obstacle in the way of
     * the agent
     * @param termitesNeighbor vicinity of agent true if another agent is close
     * @return direction of movement from 0-8 representing a direction
     */
    int LevyWalk(boolean proximitySensor, boolean[] termitesNeighbor) {
        acumulator += alpha;
        if (acumulator >= T || proximitySensor || proximitySensor(termitesNeighbor)) {
            alpha = (float) Math.random();
            dirPoslw = (int) (Math.random() * 8.0);
            acumulator = 0;
        }
        return dirPoslw;
    }

    /**
     *
     * @param _language termites language of sensors and actions
     * @param probFailure failure probability
     * @param failuresByTermite not used
     */
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

    /**
     * Select a direction based on the Roulette algorithm
     *
     * @param pheromone amount of pheromone in the vicinity of termite
     * @return a movement corresponding to a number in 8 directions
     */
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

    /**
     *
     * @return true if the strategy is carriers
     */
    public boolean isStrategyCarrier() {
        return lastStrategy == 0;
    }

    /**
     *
     * @return true is the strategy is seekers
     */
    public boolean isStrategySeeker() {
        return lastStrategy == 2;
    }

    /**
     *
     * @return true if strategy is Levy Walk
     */
    public boolean isStrategyLevy() {
        return lastStrategy == 1;
    }

    /**
     * This function take the array of strategies and normalize weights
     */
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

    /**
     * Rewards a Strategy based on a learning rule. Reward is a random number.
     */
    public void RewardStrategy() {
        float delta = (float) Math.random();
        strategies[lastStrategy] += delta * strategies[lastStrategy];
        normalizeStrategies();
    }

    /**
     * Punish a Strategy based on a learning rule. Punish is a random number.
     */
    private void PunishStrategy() {
        float delta = (float) Math.random();
        strategies[lastStrategy] -= delta * strategies[lastStrategy];
        normalizeStrategies();
    }

    /**
     * Carrier strategy is to find the direction with the minimum value of pheromone
     * @param pheromone pheromone perception in termite's vicinity
     * @param proximitySensor sensor of walls in environment 
     * @param termitesNeighbor sensor to detect another termite in termite's vicinity
     * @return
     */
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

    /**
     * Seekers strategy
     * @param pheromone
     * @return the direction with the higher amount of pheromone.
     */
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

    /**
     * Choose a direction of movement or the die action to simulate a failure
     * @param temps not used
     * @param pheromone pheromone sensor in vicinity
     * @param termitesNeighbor neighbors of termite
     * @param seekingStatus determine if agent is seeker or carrier
     * @param message not used
     * @param proximitySensor used to detect a wall in agents vicinity
     * @param MT not used
     * @return an action corresponding to a direction of movement or die action.
     */
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

        switch (lastStrategy) {
            case 0:
                return carry(pheromone, proximitySensor, termitesNeighbor);
            case 1:
                return LevyWalk(proximitySensor, termitesNeighbor);
            default:
                return seek(pheromone);
        }
    }

    /**
     * Add the result of an action to history
     * @param b
     */
    public void addHistory(boolean b) {
        history[roundNumber % n] = b;
    }

    /**
     * Count the number of success in movements
    */
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

    /**
     *  Removes agent history
     */
    private void deletehistory() {
        for (int i = 0; i < history.length; i++) {
            history[i] = false;
        }
    }

}
