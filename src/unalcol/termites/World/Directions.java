/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.World;

/**
 *
 * @author arles.rodriguez
 */
public class Directions {
   /* public static final int NONE = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    public static final int UP = 4;
    public static final int ROTATE = 5;
    public static final int DOWNRIGHT = 6;
    public static final int DOWNLEFT = 7;
    public static final int UPLEFT = 8;
    public static final int UPRIGHT = 9;
*/
   // {"right", "downright", "down", "downleft", "left", "upleft", "up", "upright", "none"}

    /**
     *
     */
    public static final int RIGHT = 0;

    /**
     *
     */
    public static final int DOWNRIGHT = 1;

    /**
     *
     */
    public static final int DOWN = 2;

    /**
     *
     */
    public static final int DOWNLEFT = 3;

    /**
     *
     */
    public static final int LEFT = 4;

    /**
     *
     */
    public static final int UPLEFT = 5;

    /**
     *
     */
    public static final int UP = 6;

    /**
     *
     */
    public static final int UPRIGHT = 7;

    /**
     *
     */
    public static final int NONE = 8;

    /**
     *
     */
    public static final int ROTATE = -1;

    /**
     *
     */
    public static final String sNONE = "none";

    /**
     *
     */
    public static final String sDOWN = "down";

    /**
     *
     */
    public static final String sLEFT = "left";

    /**
     *
     */
    public static final String sRIGHT = "right";

    /**
     *
     */
    public static final String sUP = "up";

    /**
     *
     */
    public static final String sROTATE = "rotate";

    //take this in terms of vicinity dirs
     //Der|downDer|Down|DownIzq|Izq|UpIzq|Up|UpDer

    /**
     *
     */
    public static final int vicinityDirs[] = {RIGHT, DOWNRIGHT, DOWN, DOWNLEFT, LEFT, UPLEFT, UP, UPRIGHT, NONE};

    /**
     *
     */
    public static final String vicinitySDirs[] = {"RIGHT", "DOWNRIGHT", "DOWN", "DOWNLEFT", "LEFT", "UPLEFT", "UP", "UPRIGHT", "NONE"};
}
