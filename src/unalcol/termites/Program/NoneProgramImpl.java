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

    public NoneProgramImpl(TermitesLanguage _language) {
        super(_language, 0);
    }

    @Override
    public int accion(float[] PF, float[] PD,  boolean[] termitesNeighbor,int PA,  String message, boolean PI, boolean[] MT) {
        return (int) (0);
    }
}
