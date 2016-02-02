/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.Runnable;

import java.awt.*;
import java.awt.event.WindowEvent;
import javax.swing.WindowConstants;

/**
 *
 * @author Arles Rodriguez
 */
public class AppMain {

    /**
     * Determines the maximum number of iteration
     */
    public static int maxIter = 3000;

    /**
     * mode: defines the motion algorithm
     */
    public static String mode = "random";

    /**
     * graph: enables or disable graphic interface
     */
    public static String graph = "graphson";

    /**
     * walls: defines a world with walls or not
     */
    public static String walls = "wallsoff";

    /**
     * evap: defines a constant of evaporation
     */
    public static float evap = 0.01f;

    /**
     * show intersection of information
     */
    public static String showInfoIntersect = "off";

    /*
     * defines a size of history for hybrid algorithm
     */
    public static int historySize = 20;

    /*
     * defines a round to execute roulette hybrid algorithm
     */
    public static int hybridThreshold = 5;

    /*
     * /stop simulation 
     */
    public static boolean stop = false;

    /*
     * Defines if create maze of not
     * [mazeoff|mazeon]   
     */
    public static String maze = "mazeoff";

    /*
     *  Defines a success rate to stop experiments
     * 
     */
    public static double stopSuccessRate = 1.0;

    /*
     *
     *  Defines if agents would start from a central location or not
     */
    public static String startCentralLocation = "randomLocation";

    /*
     *
     *  Defines if agents would start from a central location or not
     */
    public static String saveLocation = "nosaveLocation";

    /**
     * Performs simulation
     *
     * @param args
     */
    public static void main(String[] args) {
        // define frame, its size and make it visible
        Frame myFrame;

        if (args.length > 1) {
            //Pop Size
            System.out.println("popSize:" + args[0]);
            //Failure Probability
            System.out.println("pf:" + args[1]);
            // width
            System.out.println("w:" + args[2]);
            // height
            System.out.println("h:" + args[3]);
            // maxIter: default 3000
            System.out.println("maxIter:" + args[4]);
            // mode: random, sandc, swapph, oneph, onephevap, levywalk, lwphevap, lwsandc
            System.out.println("mode:" + args[5]);
            // nograph: graphicson|graphicsoff
            System.out.println("graph:" + args[6]);
            // Walls or not wall
            System.out.println("walls:" + args[7]);

            int population = Integer.valueOf(args[0]);
            float pf = Float.valueOf(args[1]);
            int width = Integer.valueOf(args[2]); //208, 408,  
            int height = Integer.valueOf(args[3]); //248, 446

            //maxIter parameter
            maxIter = Integer.valueOf(args[4]);

            /* modes are: random, sandc, swapph, oneph, onephevap, levywalk, lwphevap, lwsandc, 
             hybrid, sandclw, hlwsandc, hybrid2, lwphevap2, lwsandc2, lwphclwevap */
            mode = String.valueOf(args[5]);

            //Graph mode [graphson|graphsoff]
            graph = String.valueOf(args[6]);

            //walls [wallson|wallsoff]
            walls = String.valueOf(args[7]);

            //evap [a real number]
            if (args.length >= 9) {
                evap = Float.valueOf(args[8]);
                System.out.println("evap:" + evap);
            }

            //show information intersection [on|off]
            if (args.length >= 10) {
                showInfoIntersect = String.valueOf(args[9]);
            }

            if (mode.equals("hybrid") || mode.equals("hybrid3") || mode.equals("hybrid4")) {
                //used in hybrid algorithms
                if (args.length >= 11) {
                    historySize = Integer.valueOf(args[10]);
                }

                if (args.length >= 12) {
                    hybridThreshold = Integer.valueOf(args[11]);
                }

                if (args.length >= 13) {
                    maze = String.valueOf(args[12]);
                }

                if (args.length >= 14) {
                    stopSuccessRate = Double.valueOf(args[13]);
                }

                if (args.length >= 15) {
                    startCentralLocation = String.valueOf(args[14]);
                }

                if (args.length >= 16) {
                    saveLocation = String.valueOf(args[15]);
                }
            } else {
                System.out.println("args[10]" + args[10]);
                if (args.length >= 11) {
                    maze = String.valueOf(args[10]);
                }
                if (args.length >= 12) {
                    stopSuccessRate = Double.valueOf(args[11]);
                }
                if (args.length >= 13) {
                    startCentralLocation =  String.valueOf(args[12]);
                }
                if (args.length >= 14) {
                    saveLocation = String.valueOf(args[13]);
                }
            }
            if (graph.equals("graphson")) {
                width = width * 4 + 8;
                height = height * 4 + 48;
                //int FailuresbyTermite = Integer.valueOf(args[2]);
                //int threshold = Integer.valueOf(args[3]);
                String title = "popSize:" + args[0] + "pf:" + args[1] + "w:" + args[2] + "maxIter:" + args[4] + "mode:" + args[5];
                //population = 50;
                //myFrame = new MyFrame(population, pf, FailuresbyTermite, threshold);
                myFrame = new MyFrame(population, pf, 0, 0, title, width, height);
                myFrame.setBounds(10, 10, width, height); // this time use a predefined frame size/position
                myFrame.dispatchEvent(new WindowEvent(myFrame, WindowEvent.WINDOW_CLOSING));

                //myFrame.setBounds(10, 10, 208, 248); // this time use a predefined frame size/position
                //myFrame.setBounds(10, 10, 408, 446); // this time use a predefined frame size/position
                myFrame.setVisible(true);

                //myFrame2 = new MyFrame(population, pf, width, width, title, width, width);
                //myFrame2.setBounds(10, 10, width, height); // this time use a predefined frame size/position
                //myFrame2.dispatchEvent(new WindowEvent(myFrame, WindowEvent.WINDOW_CLOSING));
                //myFrame.setBounds(10, 10, 208, 248); // this time use a predefined frame size/position
                //myFrame.setBounds(10, 10, 408, 446); // this time use a predefined frame size/position
                //mFrame2.setVisible(true);
            } else {
                WorldThread w = new WorldThread(population, pf, width, height);
                w.init();
                w.run();
                //myFrame.setVisible(true);
                //myFrame.setState(Frame.ICONIFIED);
                System.out.println("running without graphics...");
            }
        } else {
            System.out.println("Usage:");
            System.out.println("java -Xmx4200m -classpath TermitesSimulator.jar unalcol.termites.Runnable.AppMain population pf world_width world_height stop_cond mode [graphson|graphsoff] [wallson|wallsoff] ");
        }
    } // end main method

    /**
     * @return the mode
     */
    public static String getMode() {
        return mode;
    }
} // end classs

