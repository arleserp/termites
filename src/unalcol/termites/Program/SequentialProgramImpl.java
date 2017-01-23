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

/**
 *
 * @author arles.rodriguez
 */
public class SequentialProgramImpl extends TermitesMovementProgram {

    int counter = 0;
    boolean isFw;
    boolean isLateral;
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
    int currentDir;

    /**
     *
     * @param _language
     * @param world
     * @param iterator
     * @param probFailure
     * @param failuresByTermite
     */
    public SequentialProgramImpl(TermitesLanguage _language, WorldConsensusImpl world, int iterator, float probFailure, int failuresByTermite) {
        super(_language, 0);
        this.world = world;
        this.agentIterator = iterator;
        this.movQueue = new LinkedList<Integer>();
        pf = probFailure;
        
        isLateral = Math.random() > 0.5;
        if(isLateral){
            currentDir = 0;
        }else{
            currentDir = 6;
        }
        isFw = Math.random() > 0.5;

        //System.out.println("oooofff" + probFailure);
        // termitesvm = new TermitesVMBinaryImpl(iterationFailure, iterator, probFailure, failuresByTermite);
    }

    private boolean proximitySensor(boolean[] termitesNeighbor) {
        for (int i = 0; i < termitesNeighbor.length; i++) {
            if (termitesNeighbor[i]) {
                return true;
            }
        }
        return false;
    }

    int lateralMovement(boolean[] termitesNeighbor, boolean[] proximityPerception) {
        if (isFw) {
            currentDir = modeForward(proximityPerception);
        } else {
            currentDir = modeBackward(proximityPerception);
        }

        if (isFw && (termitesNeighbor[currentDir] || (proximityPerception[currentDir] && proximityPerception[2]))) {
            //System.out.println("back!");
            isFw = false;
            //currentDir = modeBackward(proximityPerception);
        } else if (termitesNeighbor[currentDir] || (proximityPerception[currentDir] && proximityPerception[6])) {
            //System.out.println("fw!");
            isFw = true;
            //currentDir = modeForward(proximityPerception);
        }
        return currentDir;
    }

    int verticalMovement(boolean[] termitesNeighbor, boolean[] proximityPerception) {
        {
            if (isFw) {
                currentDir = modeUp(proximityPerception);
            } else {
                currentDir = modeDown(proximityPerception);
            }

            if (isFw && (termitesNeighbor[currentDir] || (proximityPerception[0] && proximityPerception[6]))) {
                //System.out.println("back!");
                isFw = false;
                //currentDir = modeBackward(proximityPerception);
            } else if (termitesNeighbor[currentDir] || (proximityPerception[4] && proximityPerception[2])) {
                //System.out.println("fwsssssssssssssss!");
                isFw = true;
                //currentDir = modeForward(proximityPerception);
            }
        }
        return currentDir;
    }

    /**
     *
     * @param temps
     * @param pheromone
     * @param termitesNeighbor
     * @param seekingStatus
     * @param message
     * @param proximitySensor
     * @param proximityPerception
     * @return
     */
    @Override
    //
    public int accion(float[] temps, float[] pheromone, boolean[] termitesNeighbor, int seekingStatus, String message, boolean proximitySensor, boolean[] proximityPerception) {
        /* If termite has a message then react to this message */

        if(isLateral){
            currentDir = lateralMovement(termitesNeighbor, proximityPerception);
        }else{
            currentDir = verticalMovement(termitesNeighbor, proximityPerception);
        }
//Der|downDer|Down|DownIzq|Izq|UpIzq|Up|UpDer|hello
        if (Math.random() < pf) {
            return 11; //Die
        }

        return currentDir;
    }

    /**
     *
     * @return
     */
    public TermitesVM getTermitesvm() {
        return termitesvm;
    }

    private int modeBackward(boolean[] proximityPerception) {
        //Der|downDer|Down|DownIzq|Izq|UpIzq|Up|UpDer|hello
        if (!proximityPerception[currentDir]) {
            //currentDir = 0;
            if (!proximityPerception[4] && currentDir == 6) {
                currentDir = 4;
            }
            if (!proximityPerception[0] && currentDir == 6) {
                currentDir = 0;
            }
            // System.out.println("hey");
        } else {
            if (proximityPerception[currentDir] && currentDir == 0) {
                if (!proximityPerception[6]) {
                    currentDir = 6;
                }
            }
            if (proximityPerception[currentDir] && currentDir == 6) {
                if (proximityPerception[0]) {
                    currentDir = 4;
                } else if (proximityPerception[4]) {
                    currentDir = 0;
                }
            }
            if (proximityPerception[currentDir] && currentDir == 4) { //up after move left
                currentDir = 6;
            }
            if (proximityPerception[currentDir] && proximityPerception[2] && currentDir == 0) {
                currentDir = 6;
            }
            if (currentDir == 2) {
                currentDir = 6;
            }

        }
        //System.out.println("curre" + currentDir);
        return currentDir;
    }

    private int modeForward(boolean[] proximityPerception) {
        //Der|downDer|Down|DownIzq|Izq|UpIzq|Up|UpDer|hello
        if (!proximityPerception[currentDir]) {
            //currentDir = 0;
            if (!proximityPerception[4] && currentDir == 2) {
                currentDir = 4;
            }
            if (!proximityPerception[0] && currentDir == 2) {
                currentDir = 0;
            }
        } else {
            if (proximityPerception[currentDir] && currentDir == 0) {
                currentDir = 2;
            }
            if (proximityPerception[currentDir] && currentDir == 2) {
                currentDir = 0;
            }
            if (currentDir == 4) {
                currentDir = 2;
            }
            if (proximityPerception[currentDir] && proximityPerception[2] && currentDir == 0) {
                currentDir = 4;
            }
            if (currentDir == 6) {
                currentDir = 2;
            }
        }
        //System.out.println("curre" + currentDir);
        return currentDir;
    }

    private int modeUp(boolean[] proximityPerception) {
        //Der|downDer|Down|DownIzq|Izq|UpIzq|Up|UpDer|hello
        if (!proximityPerception[currentDir]) {
            //currentDir = 0;
            if (!proximityPerception[6] && currentDir == 0) {
                currentDir = 6;
            }
            if (!proximityPerception[2] && currentDir == 0) {
                currentDir = 2;
            }
        } else {
            if (proximityPerception[currentDir] && currentDir == 6) {
                currentDir = 0;
            }
            if (proximityPerception[currentDir] && currentDir == 2) {
                currentDir = 0;
            }
            if (proximityPerception[currentDir] && currentDir == 0) {
                if (proximityPerception[6]) {
                    currentDir = 2;
                }
                if (proximityPerception[2]) {
                    currentDir = 6;
                }
            }
        }
        //System.out.println("curre up" + currentDir);
        return currentDir;
    }

    private int modeDown(boolean[] proximityPerception) {
        //Der|downDer|Down|DownIzq|Izq|UpIzq|Up|UpDer|hello
        if (!proximityPerception[currentDir]) {
            //currentDir = 0;
            if (!proximityPerception[6] && currentDir == 0) {
                currentDir = 6;
            }
            if (!proximityPerception[2] && currentDir == 0) {
                currentDir = 2;
            }
            if (!proximityPerception[6] && currentDir == 4) {
                currentDir = 6;
            }
            if (!proximityPerception[2] && currentDir == 4) {
                currentDir = 2;
            }
        } else {
            if (proximityPerception[currentDir] && currentDir == 6) {
                currentDir = 4;
            }
            if (proximityPerception[currentDir] && currentDir == 2) {
                currentDir = 4;
            }
            if (proximityPerception[currentDir] && currentDir == 4) {
                if (proximityPerception[6]) {
                    currentDir = 2;
                }
                if (proximityPerception[2]) {
                    currentDir = 6;
                }
            }
        }
        //System.out.println("curre down" + currentDir);
        return currentDir;
    }

}
