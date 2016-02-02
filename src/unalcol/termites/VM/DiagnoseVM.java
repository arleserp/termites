/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.VM;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import unalcol.termites.TermitesLanguage.TermitesLanguage;
import unalcol.termites.World.Directions;
import unalcol.termites.World.Termite;
import unalcol.termites.World.WorldConsensusImpl;

/**
 *
 * @author arles.rodriguez
 */
public abstract class DiagnoseVM {

    private int destinydir = -1;
    private int timeout = 3;
    TermitesLanguage tLanguage = new TermitesLanguage();
    float diagnoseWeights[];
    int lastposX;
    int lastposY;
    String lastACT;
    int worldHeight;
    int worldWidth;
    int diagnoseNumber = 0;
    float alpha = (float) 0.01;
    float gamma = (float) 0.06;
    protected static String[] language = {
        "HELLO", "HASPROGRAM", "HAVEPROGRAM", "NOHAVEPROGRAM", "END", "ACT", "DONE", "DIAGNOSE", "RUNINSTR", "INSTRRES"
    };
    boolean canDiagnose;

    public DiagnoseVM() {
        this.destinydir = 0;
        diagnoseWeights = new float[language.length];
        InitializeDiagnoseWeights();
        canDiagnose = true;
    }

    public DiagnoseVM(int destdir, int height, int width) {
        this.destinydir = destdir;
        this.worldHeight = height;
        this.worldWidth = width;
        InitializeDiagnoseWeights();
        canDiagnose = true;
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

    /**
     * This function now have a diagnose matrix for asking questions
     * @param piece
     * @return
     */
    public String startDiagnosis(Termite piece, int w, int h) {
        //Send hello if Termite is starting diagnosi
        //Diagnosis code must be here
       // if (canDiagnose) {
            worldHeight = h;
            worldWidth = w;
            piece.setDiagnoseStatus(Termite.DIAGNOSE);
            String res;
            lastACT = getDiagnosequestion();
            res = language[5] + "-" + lastACT + "," + piece.getX() + "," + piece.getY();
            //System.out.println("start Diagnose " + res + "id:" + piece.getId());
            //piece.setVelocity(0);
            calculatePos(piece);
            return res;
      //  }
      //  return null;
    }

    public String responseMessage(Termite piece, String msg, boolean acted) {
        String res = "";
        boolean comp = true;

        if (diagnoseNumber != 0 && diagnoseNumber % 1000 == 0) {
            try {
                PrintWriter escribir = new PrintWriter(new BufferedWriter(new FileWriter("diagnose stats.csv", true)));
                escribir.println(piece.getId() + printDiagnoseWeigths());
                escribir.close();
            } catch (IOException ex) {
                Logger.getLogger(WorldConsensusImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        //System.out.println("piece:" + piece.getId() + ", msg received:" + msg);

        if (msg.equals("")) {
            endDialog(piece);
            //piece.setDiagnoseStatus(Termite.NOTDIAGNOSE);
            //piece.setVelocity(6);
            return language[4] + "," + piece.getX() + "," + piece.getY();
        }
        if (msg.contains(language[1])) {
            piece.setDiagnoseStatus(Termite.OVERDIAGNOSE);
            boolean exists = (new File("D:\\UN - 2010-III\\tesis\\sources\\untermites~termitesrepo\\MyTermites\\programs\\program-" + piece.getId() + ".txt")).exists();
            if (exists) {
                return language[2] + "," + piece.getX() + "," + piece.getY();
                // File or directory exists
            } else {
                return language[3] + "-" + piece.getId() + "," + piece.getX() + "," + piece.getY();
            }
            // File or directory does not exist
        }
        if (msg.contains(language[2]) && !msg.contains(language[3])) {
            // piece.setStatus(Termite.OVERDIAGNOSE);
            return language[4] + "," + piece.getX() + "," + piece.getY();
            // File or directory does not exist
        }
        if (msg.contains(language[3])) {
            System.out.println("---COPY FILE---!" + msg);
            String[] smsg = msg.split(",");
            String[] smsg2 = smsg[0].split("-");
            //piece.setStatus(Termite.OVERDIAGNOSE);
            unalcol.termites.util.CopyFile.copyfile("D:\\UN - 2010-III\\tesis\\sources\\untermites~termitesrepo\\MyTermites\\programs\\program-" + piece.getId() + ".txt", "D:\\UN - 2010-III\\tesis\\sources\\untermites~termitesrepo\\MyTermites\\programs\\program-" + smsg2[1] + ".txt");
            return language[1] + "," + piece.getX() + "," + piece.getY();
            // File or directory does not exist
        }
        if (msg.contains(language[4])) {
            //System.out.println("END");
            endDialog(piece);
            return language[4] + "," + piece.getX() + "," + piece.getY();
        }
        if (msg.contains(language[5])) {
            piece.setDiagnoseStatus(Termite.OVERDIAGNOSE);
            System.out.println("R:" + language[6] + "-" + acted + "," + piece.getX() + "," + piece.getY());
            return language[6] + "-" + acted + "," + piece.getX() + "," + piece.getY();
        }
        if (msg.contains(language[6])) {
            //computes here if executed was true
            System.out.println("entra!");
            String[] smsg = msg.split(",");
            String[] smsg2 = smsg[0].split("-");
            diagnoseNumber++;

            System.out.println("entra1");
            if (smsg2[1].equals("true")) {
                comp = compute(smsg[1], smsg[2], lastACT, lastposX, lastposY);
                if (comp) {

                    System.out.println("piece is well moving to - " + lastACT);
                    diagnoseWeights[tLanguage.getActionIndex(lastACT)] = diagnoseWeights[tLanguage.getPerceptIndex(lastACT)] - alpha * (1 + gamma * getMaxQ() - diagnoseWeights[tLanguage.getPerceptIndex(lastACT)]);
                } else {
                    System.out.println("piece is bad moving to - " + lastACT);
                    diagnoseWeights[tLanguage.getActionIndex(lastACT)] = diagnoseWeights[tLanguage.getPerceptIndex(lastACT)] + alpha * (1 + gamma * getMaxQ() - diagnoseWeights[tLanguage.getPerceptIndex(lastACT)]);
                    //diagnoseWeights[tLanguage.getActionIndex(lastACT)] +=1; // diagnoseWeights[tLanguage.getPerceptIndex(lastACT)] + diagnoseWeights[tLanguage.getPerceptIndex(lastACT)] / diagnoseNumber + getLearningRate();
                }
            }

            System.out.println("DONE-" + comp + "-" + lastACT);
            endDialog(piece);
            System.out.println("eval end dialog");
            return language[7] + "-" + comp + "-" + lastACT + "," + piece.getX() + "," + piece.getY();
        }
        if (msg.contains(language[7])) {
            endDialog(piece);
            return language[4] + "," + piece.getX() + "," + piece.getY();
        }

        return res;
    }

    public void endDialog(Termite piece) {
        piece.setDiagnoseStatus(Termite.NOTDIAGNOSE);
        //piece.setVelocity(6);
    }

    protected String getDiagnosequestion() {
        int tmp = 0;
        boolean useRandomdir = true;
        boolean useRandomdirArray = false;
        ArrayList<Integer> tmp2 = new ArrayList<Integer>();

        for (int i = 1; i < diagnoseWeights.length; i++) {
            if (diagnoseWeights[i] > diagnoseWeights[tmp]) {
                tmp = i;
                tmp2.clear();
                tmp2.add(tmp);
                useRandomdir = false;
            }

            //si hay otro igual reactiva el random
            if ((i != tmp) && (diagnoseWeights[tmp] == diagnoseWeights[i])) {
                System.out.println("diagnose weights: " + printDiagnoseWeights());
                useRandomdirArray = true;
                useRandomdir = true;
                tmp2.add(i);
            }
        }

        if (useRandomdir) {
            if (useRandomdirArray) {
                Random random = new Random();
                tmp = tmp2.get(random.nextInt(tmp2.size()));
                //tmp = (int) ((Math.random() * 1000000) % diagnoseWeights.length);
                System.out.println("use random array!!!!!! " + diagnoseWeights[tmp]);
            } else {
                Random random = new Random();
                tmp = random.nextInt(diagnoseWeights.length);
                //tmp = (int) ((Math.random() * 1000000) % diagnoseWeights.length);
                System.out.println("use random!!!!!! " + tmp);
            }
        } else {
            System.out.println("no usó random y escogió:" + tmp);
        }
        return tLanguage.getAction(tmp);
    }

    //computes movement execution
    private boolean compute(String posX, String posY, String lastACT, int lastposX, int lastposY) {
        int direction = tLanguage.getActionIndex(lastACT);
        int i = 0;
        int j = 0;
        int resX = lastposX, resY = lastposY;

        if (direction == Directions.UP) {
            if (lastposY > 0) { //-1
                resY = lastposY - 1;
            }
        }
        if (direction == Directions.DOWN) {
            resY = lastposY + 1;
        }
        if (direction == Directions.LEFT) {
            resX = lastposX - 1;
        }
        if (direction == Directions.RIGHT) { //TODO +1
            resX = lastposX + 1;
        }

        if (direction == Directions.DOWNLEFT) {
            resX = lastposX - 1;
            resY = lastposY + 1;
        }

        if (direction == Directions.DOWNRIGHT) {
            resY = lastposY + 1;
            resX = lastposX + 1;
        }

        if (direction == Directions.UPLEFT) {
            resX = lastposX - 1;
            resY = lastposY - 1;
        }

        if (direction == Directions.UPRIGHT) {
            resY = lastposY - 1;
            resX = lastposX + 1;
        }

        if (resY >= worldHeight) {
            //System.out.println("w: "+ width + "h: "+ height);
            resY = resY % worldHeight;
        }

        if (resX >= worldWidth) {
            //System.out.println("w: "+ width + "h: "+ height);
            resX = resX % worldWidth;
        }
        if (resY < 0) {
            resY = resY + worldHeight;
        }
        if (resX < 0) {
            resX = resX + worldWidth;
        }

        if (Integer.valueOf(posX) == resX && Integer.valueOf(posY) == resY) {
            return true;
        }
        return false;
    }

    protected void calculatePos(Termite piece) {
        int[] xs = {1, 1, 0, -1, -1, -1, 0, 1};
        int[] ys = {0, 1, 1, 1, 0, -1, -1, -1};
        //StateProps[] vals = new StateProps[xs.length];
        lastposX = piece.getX() + xs[destinydir];
        lastposY = piece.getY() + ys[destinydir];
    }

    private void InitializeDiagnoseWeights() {
        for (int i = 0; i < diagnoseWeights.length; i++) {
            diagnoseWeights[i] = (float) 0.12;
        }
    }

    /*gets learning rate */
    private float getLearningRate() {
        float suma = 0;
        for (int i = 0; i < diagnoseWeights.length; i++) {
            suma += (float) diagnoseWeights[i];
        }
        suma = suma / diagnoseWeights.length;
        return suma;
    }

    private String printDiagnoseWeigths() {
        String sDiagnose = "";
        for (int i = 0; i < diagnoseWeights.length; i++) {
            sDiagnose += ";" + diagnoseWeights[i];
        }
        return sDiagnose;
    }

    //obtains maxQ
    public float getMaxQ() {
        int tmp = 0;
        boolean useRandomdir = true;
        for (int i = 1; i < diagnoseWeights.length; i++) {
            if (diagnoseWeights[i] > diagnoseWeights[tmp]) {
                tmp = i;
                useRandomdir = false;
            }
            if ((i != tmp) && (diagnoseWeights[tmp] == diagnoseWeights[i])) {
                useRandomdir = true;
            }
        }
        if (useRandomdir) {
            Random random = new Random();
            tmp = random.nextInt(diagnoseWeights.length);
            //System.out.println("rnd");
            //tmp = (int) (Math.random() * diagnoseWeights.length);
        }
        return diagnoseWeights[tmp];
    }

    private String printDiagnoseWeights() {
        String output = "";
        for (int i = 0; i < diagnoseWeights.length; i++) {
            output += diagnoseWeights[i] + ", ";
        }
        return output;
    }

    public void setCanDiagnose(boolean canDiagnose) {
        this.canDiagnose = canDiagnose;
    }

    public boolean isCanDiagnose() {
        return canDiagnose;
    }
}
