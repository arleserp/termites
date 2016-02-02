/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.WorldDesigner;

/**
 *
 * @author Arles Rodriguez
 */
public class MapCreator {

    /**
     * Frame to create Maps
     *
     * @param args
     */
    public static void main(String[] args) {
        // define frame, its size and make it visible
        MapFrame m;
        int w = 50;
        int h = 50;

        if (args.length >= 1) {
            w = Integer.valueOf(args[0]);
        }

        if (args.length >= 2) {
            h = Integer.valueOf(args[1]);
        }

        m = new MapFrame(w, h);
        m.update(w,h);
    } // end main method

} // end classs

