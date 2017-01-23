/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.Program;

import unalcol.termites.VM.TermitesVM;
import unalcol.termites.TermitesLanguage.TermitesLanguage;

/**
 *
 * @author arles.rodriguez
 */
public class LevyWalkProgramImpl extends TermitesMovementProgram {

    int counter = 0;
    boolean ret = false;
    int iterationFailure = 0;

    /**
     *
     */
    public TermitesVM termitesvm = null;
    //defines an error vector

    /**
     *
     */
    public double pf = 0;
    float alpha;
    float acumulator;
    int dirPos;
    float T;

    /**
     *
     * @param _language
     * @param probFailure
     */
    public LevyWalkProgramImpl(TermitesLanguage _language, double probFailure) {
        super(_language, 0);
        pf = probFailure;
        alpha = (float) Math.random();
        acumulator = 0;
        dirPos = (int) (Math.random() * 8.0);
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
    public int accion(float[] temps, float[] pheromone, boolean[] termitesNeighbor, int seekingStatus, String message,  boolean proximitySensor, boolean[] MT) {
        /* If termite has a message then react to this message */

        if (Math.random() < pf) {
            return 11; //Die
        }

        acumulator += alpha;
        if (acumulator >= T || proximitySensor || proximitySensor(termitesNeighbor)) {
            alpha = (float) Math.random();
            dirPos = (int) (Math.random() * 8.0);
            acumulator = 0;
        }

        return dirPos;
    }

    /**
     *
     * @return
     */
    public TermitesVM getTermitesvm() {
        return termitesvm;
    }


}
