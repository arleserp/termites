/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.VM;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 *
 * @author arles.rodriguez
 */
public abstract class TermitesVM {

    /* Sensor information */
    private float[] temps;
    private float[] pheromone;
    private boolean[] termitesNeighbor;
    private int seekingStatus;
    private String message;
    TermitesInterpreter interpreter;

    /* VM information */
    private int destinydir = -1;

    /**
     *
     */
    public TermitesVM() {
        interpreter = new TermitesInterpreterImpl();
    }

    /**
     *
     * @param temps
     * @param pheromone
     * @param termitesNeighbor
     * @param seekingStatus
     * @param message
     * @param PI
     * @param MT
     */
    public void updatePercepts(float[] temps, float[] pheromone, boolean[] termitesNeighbor, int seekingStatus, String message, boolean PI, boolean MT) {
        this.temps = temps;
        this.pheromone = pheromone;
        this.termitesNeighbor = termitesNeighbor;
        this.seekingStatus = seekingStatus;
        this.message = message;
        interpreter.updatePercepts(temps, pheromone, termitesNeighbor, seekingStatus, message, PI, MT);
    }

    /**
     * @return the destinydir
     */
    public int getDestinydir() {
        return destinydir;
    }

    /**
     * @param destinydir the destinydir to set
     */
    public void setDestinydir(int destinydir) {
        this.destinydir = destinydir;
    }

    //Loads the program of a termite

    /**
     *
     * @param filename
     * @return
     */
    public int loadTermiteProgram(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String code = reader.toString();
            interpreter.interpret(code);
//            while (linea != null) {
//                jTextArea1.append(linea + "\n");
//                linea = reader.readLine();
//            }
            reader.close();
        } catch (Exception ex) {
            System.out.println("Error loading file " + filename);
            return -1;
        }
        return 0;
    }

    /**
     *
     * @param filename
     * @return
     */
    public int step(String filename) {
        int returnValue = -1;
        String code = "";
        String linea = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            code = reader.readLine() + "\n";
            while ((linea = reader.readLine()) != null) {
                code += linea + "\n";
            }
            returnValue = interpreter.runNextInstruction(code);
        } catch (Exception ex) {
            System.out.println("Error loading file " + filename);
            return returnValue;
        }
        return returnValue;
    }

    /**
     *
     * @return
     */
    public TermitesInterpreter getInterpreter() {
        return interpreter;
    }

    /**
     *
     * @return
     */
    public int step() {
        return -1;
    }
}
