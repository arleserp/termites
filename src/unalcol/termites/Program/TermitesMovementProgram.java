package unalcol.termites.Program;

import unalcol.termites.TermitesLanguage.TermitesLanguage;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.types.collection.vector.*;
import unalcol.agents.Action;

/**
 * <p>
 * Title: </p>
 *
 * <p>
 * Description: </p>
 *
 * <p>
 * Copyright: Copyright (c) 2007</p>
 *
 * <p>
 * Company: Kunsamu</p>
 *
 * @author Jonatan Gómez
 * @version 1.0
 */
public abstract class TermitesMovementProgram implements AgentProgram {

    protected TermitesLanguage language;
    protected Vector<String> cmd = new Vector<String>();
    protected int agentPos;
    public double pf;

    public TermitesMovementProgram() {
    }

    public TermitesMovementProgram(TermitesLanguage _language, int agent) {
        language = _language;
        agentPos = agent;
    }

    public void setLanguage(TermitesLanguage _language) {
        language = _language;
    }

    public void init() {
        cmd.clear();
    }

    public abstract int accion(float[] temps, float[] pheromone, boolean[] termitesNeighbor, int seekingStatus, String message, boolean proximitySensor, boolean[] proximityDirSensor);


    /**
     * execute
     *
     * @param perception Perception
     * @return Action[]
     */
    public Action compute(Percept p) {
        int d = accion((float[]) p.getAttribute("temps"), (float[]) p.getAttribute("pheromone"), (boolean[]) p.getAttribute("neighborTermite"), (Integer) p.getAttribute("seekingStatus"), (String) p.getAttribute("msg"),(boolean)p.getAttribute("proximitySensor"), (boolean[])p.getAttribute("proximityDirSensor"));
        return new Action(language.getAction(d));
      //if( cmd.size() == 0 ){
 /*     boolean PF = ( (Boolean) p.getAttribute(language.getPercept(0))).
         booleanValue();
         boolean PD = ( (Boolean) p.getAttribute(language.getPercept(1))).
         booleanValue();
         boolean PA = ( (Boolean) p.getAttribute(language.getPercept(2))).
         booleanValue();
         boolean PI = ( (Boolean) p.getAttribute(language.getPercept(3))).
         booleanValue();
         boolean MT = ( (Boolean) p.getAttribute(language.getPercept(4))).
         booleanValue();
         */
      //int d = accion(PF, PD, PA, PI, MT);
        //int d = accion((float[])p.getAttribute("temps"), (float[])p.getAttribute("pheromone"), (boolean[])p.getAttribute("neighborTermite"),  (Integer)p.getAttribute("seekingStatus"), (String)p.getAttribute("msg"), false, false);
        //cmd.add(language.getAction(d));
        //}
        //String x = cmd.get(0);
        //cmd.remove(0);
        //return new Action(x);
    }

    /**
     * goalAchieved
     *
     * @param perception Perception
     * @return boolean
     */
    public boolean goalAchieved(Percept p) {
        return (((Boolean) p.getAttribute(language.getPercept(4))).booleanValue());
    }
}
