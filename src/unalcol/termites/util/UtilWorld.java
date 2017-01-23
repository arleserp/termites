/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.util;

/**
 *
 * @author arles.rodriguez
 */
public class UtilWorld {

    /**
     *
     * @param direction
     * @return
     */
    public static String toString(int direction) {
        //String st = "";

        switch (direction) {
            case 0:
                return "none";
            case 1:
                return "down";
            case 2:
                return "left";
            case 3:
                return "right";
            case 4:
                return "up";
            case 5:
                return "rotate";
            case 6:
                return "end";
            default:
                return "none";
        }
        //return st;
    }

    /**
     *
     * @param direction
     * @return
     */
    public static boolean[] toBit(int direction) {
        boolean bits[] = new boolean[3];

        switch (direction) {
            case 0:
                bits[0] = bits[1] = bits[2] = false;
                break;
            case 1:
                bits[0] = bits[1] = false;
                bits[2] = true;
                break;
            case 2:
                bits[0] = false;
                bits[1] = true;
                bits[2] = false;
                break;
            case 3:
                bits[0] = false;
                bits[1] = bits[2] = true;               
                break;
            case 4:
                bits[0] = true;
                bits[1] = bits[2] = false;
                break;
            case 5:
                bits[0] = true;
                bits[1] = false;
                bits[2] = true;
                break;
            case 6:
                bits[0] = true;
                bits[1] = true;
                bits[2] = false;
                break;
            default:
                bits[0] = bits[1] = bits[2] = false;
                break;
        }
        return bits;
    }

}
