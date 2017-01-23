/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.VM;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import unalcol.termites.World.Termite;

/**
 *
 * @author Arles
 * Interpreter manages execution of programs.
 */
public final class TermitesInterpreterBinaryImpl extends TermitesInterpreter {
    int iteration;
    int iterationFailure;
    int FailuresByTermite;
    String programCode;
    ArrayList<Integer> votesAboutInstruction = new ArrayList<Integer>();
    ArrayList<String> votesBinaryProgram = new ArrayList<String>();
    /* 2012.12.17 Added for control codeline */
    ArrayList<Integer> votesNumberLine = new ArrayList<Integer>();
    HashMap rightCode; //this is just for validate healing!
    float probFailure = 0;
    
    /**
     *
     */
    public HashMap program = new HashMap();

    /**
     *
     */
    public String[] perceptions = {"100","010","101","011"};

    /**
     *
     * @return
     */
    public int getIteration() {
        return iteration;
    }

    /**
     *
     * @return
     */
    public int getLastIdHealed() {
        return lastIdHealed;
    }

    /**
     *
     * @return
     */
    public int getLastIdInduceEvoMutation() {
        return lastIdInduceEvoMutation;
    }

    /**
     *
     * @return
     */
    public int getLastIdInduceFailure() {
        return lastIdInduceFailure;
    }

    /**
     *
     * @return
     */
    public int getLastIdSick() {
        return lastIdSick;
    }

    //ids
    int lastIdInduceFailure = -1;
    int lastIdInduceEvoMutation = -1;
    int lastIdHealed = -1;
    int lastIdSick=-1;

    /**
     *
     * @param filename
     * @return
     */
    public final HashMap loadProgram(String filename) {
        String linea;
        HashMap<String, String> h = new HashMap();
        try{
            BufferedReader reader = new BufferedReader(new FileReader(filename)); 
            while ((linea = reader.readLine()) != null) {
                h.put(linea.substring(0, 3), linea.substring(3,linea.length()));                
            }
            reader.close();
        }catch (Exception ex) {
            System.out.println("Error loading file " + filename);
            return null;
        }
        return h;
    }
    
    /**
     *
     * @param changes
     */
    public void induceFailureToCode(int changes) {
        int pos;
        int posStr;
        String line;
        for (int i = 0; i < changes; i++) {
            pos = (int)Math.floor(Math.random()*perceptions.length);
            line = (String) program.get(perceptions[pos]);
            StringBuilder output = new StringBuilder(line);   
            posStr = (int)Math.floor(Math.random()*line.length());
            if(line.charAt(posStr) == '0'){
                output.setCharAt(posStr, '1');
            }else{
                output.setCharAt(posStr, '0');
            }
            program.put(perceptions[pos], output.toString());
        }
    }
     
    /**
     *
     * @param iterationFailure
     * @param iterator
     * @param probFailure
     * @param FailuresByTermite
     */
    public TermitesInterpreterBinaryImpl(int iterationFailure, int iterator, float probFailure, int FailuresByTermite) {
        this.id = iterator;
        this.programCode = null;
        this.iteration = 0;
        this.iterationFailure = iterationFailure;
        this.probFailure = probFailure;
        this.FailuresByTermite = FailuresByTermite;
        this.program = loadProgram("program-b0.txt");   
        this.rightCode = loadProgram("program-b0.txt");
        
    }

    /**
     *
     * @return
     */
    @Override
    public int runNextInstruction() {
        try {
            if(iteration == 0){
                if (Math.random() < probFailure) {
                lastIdInduceFailure = this.id;
                induceFailureToCode(FailuresByTermite);
                setChanged();
                notifyObservers();
            }
            }
            return operate();
        } catch (InterruptedException ex) {
            Logger.getLogger(TermitesInterpreterBinaryImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }
      
    /**
     *
     * @param str
     * @return
     */
    public static int roulette(String str){
        ArrayList<Integer> pos = new ArrayList<Integer>();
        for(int i = 0; i < str.length(); i++){
            if(str.charAt(i) == '1'){
                pos.add(i);
            }
        }
        if(pos.isEmpty()){
            return -1;
        }else if(pos.size() == 1){
            return pos.get(0);
        }else{
            return pos.get((int)Math.floor(Math.random()*pos.size()));
        }
    }

    private int operate() throws InterruptedException {
        String seekPercept;
        String carryPercept;
        String neighborPercept;
        String keyPercepts;
        iteration++;
       
        if(getSeekingStatus() == Termite.SEEKING){
            seekPercept = "1";
        }else{
            seekPercept = "0";
        }
        
        if(getSeekingStatus() == Termite.CARRYING){
            carryPercept = "1";
        }else{
            carryPercept = "0";
        }
        
        if(hasNeighbors()){
            neighborPercept = "1";
        }else{
            neighborPercept = "0";
        }
        
        keyPercepts = seekPercept + carryPercept + neighborPercept;
        
        String action = (String) program.get(keyPercepts);
        Thread.sleep(1);
        if(action != null){
            int act = roulette(action);
            //System.out.println("act"+ act);
            if (act == 0) {
                return seek();
            }
            if (act == 1) {
                return carry();
            }
            if (act == 2) {
                return diagnose();
            }
        }
        //System.out.println(this.id + "program" + program) ;
        return -1;
    }

 
    private boolean hasNeighbors() {
        int neighbors = 0;
        for (int k = 0; k < getTermitesNeighbor().length; k++) {
            if (getTermitesNeighbor()[k]) {
                neighbors++;
            }
        }
        if (neighbors == 1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @return
     */
    public String printProgram() {
        String res = "";
        System.out.println("Program:" + program);
        return res;
    }

    /**
     *
     * @param line
     * @param pos
     */
    public void updateVotesAboutIns(String line, int pos) {
        boolean hasLine = false;
        
        for (int i = 0; i < votesBinaryProgram.size(); i++) {
            if (line.equals(votesBinaryProgram.get(i)) && (pos == votesNumberLine.get(i))) {
                votesAboutInstruction.set(i, (votesAboutInstruction.get(i) + 1));
                hasLine = true;
                break;
            }
        }
        if (!hasLine) {
            votesBinaryProgram.add(line);
            votesAboutInstruction.add(1);
            votesNumberLine.add(pos);
        }
    }
        
    /**
     *
     * @return
     */
    public String getInsMoreVoted() {
        String line = null;
        int votes = 0;
        for (int i = 0; i < votesBinaryProgram.size(); i++) {
            if (votesAboutInstruction.get(i) > votes) {
                line = votesBinaryProgram.get(i);
            }
        }
        return line;
    }

    /**
     *
     * @return
     */
    public int getPosMoreVoted() {
        int pos = 0;
        for (int i = 1; i < votesBinaryProgram.size(); i++) {
            if (votesAboutInstruction.get(i) > votesAboutInstruction.get(pos)) {
               pos = i;
            }
        }
        return pos;
    }
    
    /**
     *
     */
    public void evolutionaryMutation() {
        int pos = getPosMoreVoted();
        int posline = votesNumberLine.get(pos);
        String line = votesBinaryProgram.get(pos);
        
        program.put(perceptions[posline], line);
        
        votesAboutInstruction.remove(pos);
        votesBinaryProgram.remove(pos);
        votesNumberLine.remove(pos);
        
        if(!validateHealing())
        {
            lastIdSick = this.id;
        }else{
            System.out.println("healed");
        }
        lastIdInduceEvoMutation = this.id;
        setChanged();
        notifyObservers();
    }

    /**
     *
     * @param trheshold
     * @return
     */
    public boolean reachThreshold(int trheshold) {
        for(int i = 0; i < votesAboutInstruction.size(); i++) {
            if (votesAboutInstruction.get(i) > trheshold) {
                return true;
            }
        }
        return false;
    }
       
    /**
     *
     * @return
     */
    public boolean validateHealingEx() {
        return rightCode.equals(program);
    }
    
    /**
     *
     * @return
     */
    public boolean validateHealing() {
        boolean hasLine = false;
        if (rightCode.equals(program)) {
            lastIdHealed = this.id;
            setChanged();
            notifyObservers();
            return hasLine;
        }
        setChanged();
        notifyObservers();
        
        return hasLine;
    }

    /**
     *
     * @return
     */
    public int getTotalVotes() {
        int TotalVotes = 0;
        for (int i = 0; i < votesAboutInstruction.size(); i++) {
            TotalVotes = votesAboutInstruction.get(i); 
        }
        return TotalVotes;
    }
}
