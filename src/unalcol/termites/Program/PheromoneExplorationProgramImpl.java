/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.Program;

import unalcol.termites.VM.TermitesVM;
import java.util.LinkedList;
import java.util.Queue;
import unalcol.termites.TermitesLanguage.TermitesLanguage;
import unalcol.termites.World.Directions;
import unalcol.termites.World.Termite;
import unalcol.termites.World.WorldConsensusImpl;
import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;

/**
 *
 * @author arles.rodriguez
 */
public class PheromoneExplorationProgramImpl implements AgentProgram {

    int counter = 0;
    private WorldConsensusImpl world;
    boolean ret = false;
    private int agentIterator;
    private Queue<Integer> movQueue;
//    TermitesVM termitesvm = new TermitesVMImpl();
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

    /**
     *
     * @param _language
     * @param world
     * @param iterator
     * @param probFailure
     * @param failuresByTermite
     */
    public PheromoneExplorationProgramImpl(TermitesLanguage _language, WorldConsensusImpl world, int iterator, float probFailure, int failuresByTermite) {
        this.world = world;
        this.agentIterator = iterator;
        this.movQueue = new LinkedList<Integer>();
        pf = probFailure;
//System.out.println("oooofff" + probFailure);
        // termitesvm = new TermitesVMBinaryImpl(iterationFailure, iterator, probFailure, failuresByTermite);
    }

    int seekRoulette(float[] pheromone) {
        float sum = 0;
        for (int k = 0; k < pheromone.length; k++) {
            sum += pheromone[k];
        }
        float rand = (int) (Math.random() * sum);
        sum = 0;
        int mov = 0;
        for (int k = 0; k < pheromone.length; k++) {
            sum += pheromone[k];
            if(rand < sum){
                mov = k;
                break;
            }
        }
        return mov;
    }

    /**
     *
     * @param pheromone
     * @return
     */
    public int seek(float[] pheromone) {
        int dirPos = 0;
        boolean useRandomdir = true;
        double q0 = 0.9;
        
        if(Math.random() >= q0){
            for (int k = 1; k < pheromone.length; k++) {
                if (pheromone[k] > pheromone[dirPos]) {
                    dirPos = k;
                    useRandomdir = false;
                }
            }
            if (useRandomdir) {
                dirPos = (int) (Math.random() * 8.0);
            }
        }else{
            return seekRoulette(pheromone);
        }
        return dirPos;
    }

    /**
     *
     * @param pheromone
     * @return
     */
    public int carry(float[] pheromone) {
        int dirPos = 0;
        boolean useRandomdir = true;
        for (int k = 1; k < pheromone.length; k++) {
            if ((pheromone[dirPos] > pheromone[k])) {
                dirPos = k;
                useRandomdir = false;
            }
        }
        if (useRandomdir) {
            dirPos = (int) (Math.random() * 8.0);
        }
        return dirPos;
    }

    /**
     *
     * @param temps
     * @param pheromone
     * @param idNeighbor
     * @param seekingStatus
     * @param message
     * @param PI
     * @param MT
     * @return
     */
    public int accion(float[] temps, float[] pheromone, String idNeighbor, int seekingStatus, String message, boolean PI, boolean MT) {
        /* If termite has a message then react to this message */
        if (Math.random() < pf) {
            return 11; //Die
        }
        if (seekingStatus == Termite.SEEKING) {
            System.out.println("sssel");
            return seek(pheromone);
        }
        if (seekingStatus == Termite.CARRYING) {
            return carry(pheromone);
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

    /**
     *
     * @param p
     * @return
     */
    @Override
    public Action compute(Percept p) {
        if ((String) p.getAttribute("idneighbor") != null) {
            return new Action("Send");
        }
        if ((int) p.getAttribute("seekingStatus") == Termite.SEEKING) {
            return new Action("");
        }
        return new Action("none");

    }

    /**
     *
     */
    @Override
    public void init() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
