/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.Program;

import unalcol.termites.TermitesLanguage.TermitesLanguage;

/**
 *
 * @author arles.rodriguez
 */
public class NoneProgramImpl extends TermitesMovementProgram {

    /**
     *
     * @param _language
     */
    public NoneProgramImpl(TermitesLanguage _language) {
        super(_language, 0);
    }

    /**
     *
     * @param PF
     * @param PD
     * @param termitesNeighbor
     * @param PA
     * @param message
     * @param PI
     * @param MT
     * @return
     */
    @Override
    public int accion(float[] PF, float[] PD,  boolean[] termitesNeighbor,int PA,  String message, boolean PI, boolean[] MT) {
        return (int) (0);
    }
}
