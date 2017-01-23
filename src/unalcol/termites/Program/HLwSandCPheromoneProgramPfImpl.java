/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.Program;

import unalcol.termites.TermitesLanguage.TermitesLanguage;
import unalcol.termites.World.Termite;
import unalcol.termites.World.WorldConsensusImpl;

/**
 * This class implements a Hybrid Levy Walk Seekers and Carriers algorithm.
 * Reward or punish a strategy among Levy Walks or Seekers and Carriers
 * Generate the next direction of movement for an agent.
 * This strategy just reward the action it does not punish a strategy
 *
 * @author Arles Rodriguez <arles.rodriguez@gmail.com>
 * @version 1.0
 */
public class HLwSandCPheromoneProgramPfImpl extends TermitesMovementProgram {

    int counter = 0;
    private WorldConsensusImpl world;
    boolean ret = false;
    int iterationFailure = 0;

    /**
     * Defines a failure probability in [0, 1]
     */
    public double pf = 0;

    /**
     * An array of strategies to adapt a probability of selection of a strategy
     */
    public float[] strategies;
    float alpha;
    float acumulator;
    int dirPoslw;
    float T;
    int lastStrategy = 0;

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
     * @param _language termite language of perceptions and actions
     * @param world the space where agents move
     * @param iterator used by compatibility
     * @param probFailure failure probability
     * @param failuresByTermite not used
     */
    public HLwSandCPheromoneProgramPfImpl(TermitesLanguage _language, WorldConsensusImpl world, int iterator, float probFailure, int failuresByTermite) {
        super(_language, 0);
        this.world = world;
        pf = probFailure;
        strategies = new float[2];
        strategies[0] = 0.5f;
        strategies[1] = 0.5f;

        alpha = (float) Math.random();
        acumulator = 0;
        dirPoslw = (int) (Math.random() * 8.0);
        T = 1;
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
     * This function take the array of strategies and normalize weights
     */
    private void normalizeStrategies() {
        float sum = 0;
        for (float st : strategies) {
            sum += st;
        }
        for (int i = 0; i < strategies.length; i++) {
            strategies[i] /= sum;
        }
    }

    /**
     * Rewards the lastStrategy selected
     */
    public void RewardStrategy() {
        float delta = (float) Math.random();
        strategies[lastStrategy] = strategies[lastStrategy] + delta * strategies[lastStrategy];
        normalizeStrategies();
    }

    /**
     *
     * @return true if strategy is Seekers and Carriers
     */
    public boolean isStrategySandc() {
        return lastStrategy == 0;
    }

    /**
     * Choose an action (movement or die) based on the perceptions of a termite
     *
     * @param temps not used
     * @param pheromone pheromone sensor in vicinity
     * @param termitesNeighbor neighbor sensor in vicinity
     * @param seekingStatus define status looking other termites Termite.SEEKING
     * or avoid them Termite.CARRYING
     * @param message not used
     * @param proximitySensor wall sensor
     * @param MT not used
     * @return
     */
    @Override
    public int accion(float[] temps, float[] pheromone, boolean[] termitesNeighbor, int seekingStatus, String message, boolean proximitySensor, boolean[] MT) {
        /* If termite has a message then react to this message */
        if (Math.random() < pf) {
            return 11; //Die
        }

        lastStrategy = Roulette(strategies);
        if (lastStrategy == 0) {
            System.out.println("phero st!");
            if (seekingStatus == Termite.SEEKING) {
                return MotionUtil.seek(pheromone);
            }
            if (seekingStatus == Termite.CARRYING) {
                return MotionUtil.carry(pheromone);
            }
        } else {
            System.out.println("Levy Walk!");
            return LevyWalk(proximitySensor, termitesNeighbor);
        }
        return 0;
    }

}
