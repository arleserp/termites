/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.Program;

import java.util.LinkedList;
import java.util.Queue;
import unalcol.agents.AgentProgram;
import unalcol.termites.TermitesLanguage.TermitesLanguage;
import unalcol.termites.World.Directions;
import unalcol.termites.World.Termite;
import unalcol.termites.World.WorldConsensusImpl;
//import un.movements.TestProgram;
import unalcol.random.real.GaussianGenerator;

/**
 *
 * @author arles.rodriguez
 */
public class ACOProgramImpl2 extends TermitesMovementProgram {

    int counter = 0;
    private WorldConsensusImpl world;
    boolean ret = false;
    private int agentIterator;
    private Queue<Integer> movQueue;

    public ACOProgramImpl2(TermitesLanguage _language, WorldConsensusImpl world, int iterator) {
        //super(_language, 0);
        this.world = world;
        this.agentIterator = iterator;
        this.movQueue = new LinkedList<Integer>();
    }


    public int accion(float[] temps, float[] pheromone,  boolean[] termitesNeighbor, int seekingStatus,  String message, boolean PI, boolean[] MT) {
        //        int direction = 0;
//        System.out.println("Agent"+ agent);
//        if (world.myAgents.get(agentIterator).getVelocity() == 0) {
//            return direction;
//        }
//        for (int j = 0; j < temps.length; j++) {
//            System.out.println("Agent Temps[" + j + "] -xxx-" + temps[j]);
//        }
        boolean useRandomdir = true;
        int dirPos = 0;


        //System.out.println("seeki" + seekingStatus);

        if (seekingStatus == Termite.SEEKING) {
            for (int k = 0; k < pheromone.length; k++) {
                //             System.out.println("Seeking - PH" + pheromone[k]);
                //choose the route with most pheromone
                if (pheromone[dirPos] < pheromone[k]) {
                    dirPos = k;
                }
                if (pheromone[dirPos] != pheromone[k]) {
                    //System.out.println("oooh false");
                    useRandomdir = false;
                }
            }
//
//            GaussianGenerator g = new GaussianGenerator(0.3, 0.3);
//
//            if ((float) g.next() > pheromone[dirPos]) {
//                System.out.println("rnd");
//                dirPos = (int) (Math.random() * 8.0);
//            }
        } else if (seekingStatus == Termite.CARRYING) {
            //choose the route with most pheromone
            for (int k = 0; k < pheromone.length; k++) {
                //System.out.println("Carrying - PH" + pheromone[k]);
                if (pheromone[dirPos] > pheromone[k]) {
                    dirPos = k;
                }

                if (pheromone[dirPos] != pheromone[k]) {
                    useRandomdir = false;
                }
            }
//            GaussianGenerator g = new GaussianGenerator(0.4, 0.2);
//
//            if ((float) g.next() > pheromone[dirPos]) {
//                System.out.println("rnd");
//                dirPos = (int) (Math.random() * 8.0);
//            }
        }

        //System.out.println("more"+ dirPos);
//        if (useRandomdir){
//            System.out.println("rand!");
//            return (int) (Math.random() * 8.0);
//        }
        //System.out.println("dir:" + Directions.vicinityDirs[dirPos] + "pos:" + dirPos);
        return Directions.vicinityDirs[dirPos];
    }
}
