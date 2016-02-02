/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.VM;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * This code allows to run a binary program impl for termites
 * in form of table
 * Sensations | Actions
 * S1|S2|..|SN|AC1|AC2|ACN
 *  0|0 |0 | 1| 1 | 1 | 0
 * @author Arles
 */
public class TermitesVMBinaryImpl extends TermitesVM {

    public TermitesVMBinaryImpl(int iterationFailure, int iterator, float failureProb, int FailuresByTermite) {
        this.interpreter = new TermitesInterpreterBinaryImpl(iterationFailure, iterator, failureProb, FailuresByTermite);
    }

    @Override
    public int step() {
        return interpreter.runNextInstruction();
    }
}
