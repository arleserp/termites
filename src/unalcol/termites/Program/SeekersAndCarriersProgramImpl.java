/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.Program;

import unalcol.termites.VM.TermitesVM;
import java.util.LinkedList;
import java.util.Queue;
import unalcol.termites.TermitesLanguage.TermitesLanguage;
import unalcol.termites.World.Termite;
import unalcol.termites.World.WorldConsensusImpl;

/**
 *
 * @author arles.rodriguez
 */
public class SeekersAndCarriersProgramImpl extends TermitesMovementProgram {
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

    public SeekersAndCarriersProgramImpl(TermitesLanguage _language, WorldConsensusImpl world, int iterator, float probFailure, int failuresByTermite) {
        super(_language, 0);
        this.world = world;
        this.agentIterator = iterator;
        this.movQueue = new LinkedList<Integer>();
        pf = probFailure;
//System.out.println("oooofff" + probFailure);
        // termitesvm = new TermitesVMBinaryImpl(iterationFailure, iterator, probFailure, failuresByTermite);
    }

 
    @Override
    public int accion(float[] temps, float[] pheromone, boolean[] termitesNeighbor, int seekingStatus, String message, boolean PI, boolean[] MT) {
        /* If termite has a message then react to this message */
        if (Math.random() < pf) {
            return 11; //Die
        }
        if (seekingStatus == Termite.SEEKING) {
            return MotionUtil.seek(pheromone);
        }
        if (seekingStatus == Termite.CARRYING) {
            return MotionUtil.carry(pheromone);
        }
        return 0;
    }

    public TermitesVM getTermitesvm() {
        return termitesvm;
    }
}
