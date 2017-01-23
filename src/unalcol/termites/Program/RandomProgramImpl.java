/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.Program;

import unalcol.random.RandomUtil;
import unalcol.termites.TermitesLanguage.TermitesLanguage;

/**
 *
 * @author arles.rodriguez
 */
public class RandomProgramImpl extends TermitesMovementProgram {

    float pf;

    /**
     *
     * @param _language
     * @param fp
     */
    public RandomProgramImpl(TermitesLanguage _language, float fp) {
        super(_language, 0);
        pf = fp;
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
    public int accion(float[] PF, float[] PD, boolean[] termitesNeighbor, int PA, String message, boolean PI, boolean[] MT) {
        if (Math.random() < pf) {
            return 11; //Die
        }
        int dirPos = RandomUtil.nextInt(8);
        return dirPos;
    }
}
