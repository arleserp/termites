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
import unalcol.termites.World.Directions;
import unalcol.termites.World.Termite;
import unalcol.termites.World.WorldConsensusImpl;
import unalcol.agents.Action;
import unalcol.random.RandomUtil;
import unalcol.random.integer.IntegerGenerator;

/**
 *
 * @author arles.rodriguez
 */
public class OnlySeekersLevyWalkProgramImpl extends TermitesMovementProgram {

    int counter = 0;
    private WorldConsensusImpl world;
    boolean ret = false;
    private int agentIterator;
    private Queue<Integer> movQueue;
    //TermitesVM termitesvm = new TermitesVMImpl();
    int iterationFailure = 0;

    /**
     *
     */
    public TermitesVM termitesvm = null;
    //defines an error vector
    //float myerror[] = new float[super.language.getActionsNumber()];

    /**
     *
     */
    public double pf = 0;

    float alpha;
    float acumulator;
    int dirPoslw;
    float T;

    /**
     *
     * @param _language
     * @param world
     * @param iterator
     * @param probFailure
     * @param failuresByTermite
     */
    public OnlySeekersLevyWalkProgramImpl(TermitesLanguage _language, WorldConsensusImpl world, int iterator, float probFailure, int failuresByTermite) {
        super(_language, 0);
        this.world = world;
        this.agentIterator = iterator;
        this.movQueue = new LinkedList<>();
        pf = probFailure;
        alpha = (float) Math.random();
        acumulator = 0;
        dirPoslw = (int) (Math.random() * 8.0);
        T = 1;
    }

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
     * @param pheromone
     * @param proximitySensor
     * @param termitesNeighbor
     * @return
     */
    public int seek(float[] pheromone, boolean proximitySensor, boolean[] termitesNeighbor) {
        int dirPos = 0;
        double q0 = 0.9;
        ArrayList<Integer> temp = new ArrayList();
        if (Math.random() <= q0) {
            for (int k = 0; k < pheromone.length; k++) {
                if (pheromone[k] != -1) {
                    dirPos = k;
                    break;
                }
            }
            //System.out.println("maximum");
            for (int k = 1; k < pheromone.length; k++) {
                if (pheromone[k] != -1 && pheromone[dirPos] < pheromone[k]) {
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
            dirPos = LevyWalk(proximitySensor, termitesNeighbor);
            if (!temp.contains(dirPos)) {
                dirPos = temp.get(RandomUtil.nextInt(temp.size()));
            }
        } else {
            //System.out.print("2");
            //dirPos = Roulette(pheromone);
            dirPos = LevyWalk(proximitySensor, termitesNeighbor);
        }
        //System.out.print("," + dirPos + "\n");
        return dirPos;
    }

    /**
     *
     * @param pheromone
     * @param proximitySensor
     * @param termitesNeighbor
     * @return
     */
    public int carry(float[] pheromone, boolean proximitySensor, boolean[] termitesNeighbor) {
        int dirPos = 0;
        double q0 = 0.9;
        ArrayList<Integer> temp = new ArrayList();
        if (Math.random() <= q0) {
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
            dirPos = LevyWalk(proximitySensor, termitesNeighbor);
            if (!temp.contains(dirPos)) {
                dirPos = temp.get(RandomUtil.nextInt(temp.size()));
            }
        } else {
            //Idea is choose direction with the less amount of pheromone
            /*float[] phinv = new float[pheromone.length];
             for (int i = 0; i < pheromone.length; i++) {
             phinv[i] = 1 - pheromone[i];
             }*/
            //dirPos = Roulette(pheromone);
            dirPos = LevyWalk(proximitySensor, termitesNeighbor);
        }
        return dirPos;
    }
    
    /**
     *
     * @param temps
     * @param pheromone
     * @param termitesNeighbor
     * @param seekingStatus
     * @param message
     * @param proximitySensor
     * @param MT
     * @return
     */
    @Override
    public int accion(float[] temps, float[] pheromone, boolean[] termitesNeighbor, int seekingStatus, String message, boolean proximitySensor, boolean[] MT) {
        /* If termite has a message then react to this message */
        if (Math.random() < pf) {
            return 11; //Die
        }
        if (seekingStatus == Termite.SEEKING) {
            return seek(pheromone, proximitySensor, termitesNeighbor);
        }
        if (seekingStatus == Termite.CARRYING) {
            return carry(pheromone, proximitySensor, termitesNeighbor);
        }
        return 0;
    }

    /**
     *
     * @return
     */
    public TermitesVM getTermitesvm() {
        return termitesvm;
    }
}
