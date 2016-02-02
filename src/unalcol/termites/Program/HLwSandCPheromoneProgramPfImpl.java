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
public class HLwSandCPheromoneProgramPfImpl extends TermitesMovementProgram {

    int counter = 0;
    private WorldConsensusImpl world;
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

    float alpha;
    float acumulator;
    int dirPoslw;
    float T;
    int lastStrategy = 0;

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

    public HLwSandCPheromoneProgramPfImpl(TermitesLanguage _language, WorldConsensusImpl world, int iterator, float probFailure, int failuresByTermite) {
        super(_language, 0);
        this.world = world;
        this.agentIterator = iterator;
        this.movQueue = new LinkedList<Integer>();
        pf = probFailure;
        strategies = new float[2];
        strategies[0] = 0.5f;
        strategies[1] = 0.5f;

        alpha = (float) Math.random();
        acumulator = 0;
        dirPoslw = (int) (Math.random() * 8.0);
        T = 1;
//((HybridPheromoneProgramPfImpl)(world.getAgent(counter)).getProgram()).strategies[0] = 0.1f;
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


    public void normalizeStrategies() {
        float sum = 0;
        //System.out.println("st[0]" + strategies[0] + ", st[1]" + strategies[1]);
        for (float st : strategies) {
            sum += st;
        }
        for (int i = 0; i < strategies.length; i++) {
            strategies[i] /= sum;
        }
        //System.out.println("st[0]" + strategies[0] + ", st[1]" + strategies[1]);
    }

    public void RewardStrategy() {
        float delta = (float) Math.random();
        strategies[lastStrategy] = strategies[lastStrategy] + delta * strategies[lastStrategy];
        normalizeStrategies();
    }

    public boolean isStrategySandc() {
        return lastStrategy == 0;
    }

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

    public TermitesVM getTermitesvm() {
        return termitesvm;
    }
}
