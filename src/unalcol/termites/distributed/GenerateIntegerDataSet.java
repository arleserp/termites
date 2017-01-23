/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.distributed;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Arles Rodriguez <arles.rodriguez@gmail.com>
 */
public class GenerateIntegerDataSet implements Serializable {

    private int n;
    private ArrayList<Integer> input;
    int i;
    private int channelNumber;
    private int eppsIter;

    /**
     *
     * @param datasize
     */
    public GenerateIntegerDataSet(int datasize) {
        n = datasize;
        input = new ArrayList();
        i = 0;
        for (int j = 0; j < n; j++) {
            int number = (int) (Math.random() * 1000);
            input.add(number);
        }
    }

    /**
     *
     * @param datasize
     * @param chann
     * @param eppstein
     * @param constant
     */
    public GenerateIntegerDataSet(int datasize, int chann, int eppstein, int constant) {
        n = datasize;
        input = new ArrayList();
        channelNumber = chann;
        eppsIter = eppstein;
        i = 0;
        for (int j = 0; j < n; j++) {
            input.add(constant);
        }
    }

    /**
     *
     * @param filename
     */
    public void saveFile(String filename) {
        try {
            OutputStream file = new FileOutputStream(filename);
            OutputStream buffer = new BufferedOutputStream(file);
            ObjectOutputStream oos;
            try {
                oos = new ObjectOutputStream(buffer);
                oos.writeObject(this);
                oos.close();
            } catch (IOException ex) {
                Logger.getLogger(GenerateIntegerDataSet.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenerateIntegerDataSet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param filename
     * @return
     */
    public static GenerateIntegerDataSet loadFile(String filename) {
        InputStream file;
        GenerateIntegerDataSet ds = null;
        try {
            file = new FileInputStream(filename);
            InputStream buffer = new BufferedInputStream(file);
            ObjectInput in = new ObjectInputStream(buffer);
            ds = (GenerateIntegerDataSet) in.readObject();

        } catch (IOException ex) {
            Logger.getLogger(GenerateIntegerDataSet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GenerateIntegerDataSet.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return ds;
    }

    /**
     *
     * @return
     */
    public int getNext() {
        if(i >= input.size()){
            //System.out.println("The End.");
            //System.exit(0);
            return -1;
        }
        int t = (int) input.get(i);
        i++;
        return t;
    }

    /**
     *
     * @return
     */
    public String toString() {
        return input.toString();
    }

    /**
     * @return the n
     */
    public int getAgentsNumber() {
        return n;
    }

    /**
     * @param n the n to set
     */
    public void setAgentsNumber(int n) {
        this.n = n;
    }

    /**
     * @return the channelNumber
     */
    public int getChannelNumber() {
        return channelNumber;
    }

    /**
     * @return the eppsIter
     */
    public int getEppsIter() {
        return eppsIter;
    }

    /**
     *
     * @return
     */
    public int Min() {
        return (int) Collections.min(input);
    }
    
    /**
     *
     * @return
     */
    public ArrayList Sort(){
        Object[] D = (input).toArray();
        Arrays.sort(D);
        //System.out.println("arr" + Arrays.toString(D));
        ArrayList<Integer> E = new ArrayList(Arrays.asList(D));
        return E;
    }
    
    /**
     *
     * @return
     */
    public double avg(){
        double sum = 0.0;
        for(Integer data: input){
            sum += data;
        }
        return sum/input.size();
    }

}


