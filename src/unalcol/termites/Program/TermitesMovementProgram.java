package unalcol.termites.Program;

import unalcol.termites.TermitesLanguage.TermitesLanguage;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.types.collection.vector.*;
import unalcol.agents.Action;

/**
 * @author Arles Rodriguez <arles.rodriguez@gmail.com>
 * Based on unalcol agents by  
 */
public abstract class TermitesMovementProgram implements AgentProgram {

    /**
     *
     */
    protected TermitesLanguage language;

    /**
     *
     */
    protected Vector<String> cmd = new Vector<String>();

    /**
     *
     */
    protected int agentPos;

    /**
     *
     */
    public double pf;

    /**
     *
     */
    public TermitesMovementProgram() {
    }

    /**
     *
     * @param _language
     * @param agent
     */
    public TermitesMovementProgram(TermitesLanguage _language, int agent) {
        language = _language;
        agentPos = agent;
    }

    /**
     *
     * @param _language
     */
    public void setLanguage(TermitesLanguage _language) {
        language = _language;
    }

    /**
     *
     */
    public void init() {
        cmd.clear();
    }

    /**
     *
     * @param temps
     * @param pheromone
     * @param termitesNeighbor
     * @param seekingStatus
     * @param message
     * @param proximitySensor
     * @param proximityDirSensor
     * @return
     */
    public abstract int accion(float[] temps, float[] pheromone, boolean[] termitesNeighbor, int seekingStatus, String message, boolean proximitySensor, boolean[] proximityDirSensor);


    /**
     * execute
     *
     * @param p
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
     * @param p
     * @return boolean
     */
    public boolean goalAchieved(Percept p) {
        return (((Boolean) p.getAttribute(language.getPercept(4))).booleanValue());
    }
}
