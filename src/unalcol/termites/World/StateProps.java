/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.World;

/**
 *
 * @author arles.rodriguez
 */
public class StateProps {
    public float value;
    public int type;
    public float pherovalue;
    public float pherovalue2;
    public static final int NEUTRAL = 0;
    public static final int PIECE = 3;
    public static final int FREEZE = 3;
    public static final int TERMITE = 4;
    public static final int OBJECT = 5;
    public static final int WALL = 6;
    public float temp;
    public String msg;
    public boolean isHome;
    public boolean isTarget;
    public boolean globalInfo;
    
    public static final int LEADER = 9;
    public static final int SEEKER = 6;

    public StateProps(float v, float t) {
        pherovalue = v;
        temp = t;
        type = NEUTRAL;
        msg = "";
        isHome = false;
        isTarget = false;
    }

    public StateProps(float v, float t, float p2) {
        pherovalue = v;
        pherovalue2 = p2;
        temp = t;
        type = NEUTRAL;
        msg = "";
        isHome = false;
        isTarget = false;
    }

}
