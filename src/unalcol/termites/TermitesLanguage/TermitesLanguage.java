/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.TermitesLanguage;
import unalcol.agents.simulate.util.Language;

/**
 *
 * @author arles.rodriguez
 */
public class TermitesLanguage implements Language {

    /**
     *
     */
    protected String[] percepts = null;

    /**
     *
     */
    protected String[] actions = null;

    /**
     *
     */
    public TermitesLanguage() {
        //Der|downDer|Down|DownIzq|Izq|UpIzq|Up|UpDer|hello
        //actions = new String[]{"none", "down", "left", "right", "up", "rotate", "downright", "downleft", "upleft", "upright"};
        actions = new String[] {"right", "downright", "down", "downleft", "left", "upleft", "up", "upright", "none", "diagnose", "responsemsg", "die", "seq"};
        percepts = new String[] {"temps", "pheromone", "neighborTermite", "seekingStatus", "msg" };
    }
    
    /**
     *
     * @param action
     * @return
     */
    public int getActionIndex(String action) {
        for(int i= 0; i < actions.length; i++)
            if(action.equals(actions[i]))
            {
                return i;
            }
        return 0;
    }

    /**
     *
     * @param percept
     * @return
     */
    public int getPerceptIndex(String percept) {
        /*if (percept.equals(SOLVED)) {
            return (getPerceptsNumber() - 1);
        } else {
            int[] values = new int[2];
            int k = 0;
            try {
                StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(
                        percept));
                while (k < 2 && tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
                    if (tokenizer.ttype == StreamTokenizer.TT_NUMBER) {

                        values[k] = (int) (tokenizer.nval);
                        k++;
                    }
                }
                return values[0] * DIGITS + values[1];
            } catch (Exception e) {
                return -1;
            }
        }*/
        return 0;
    }

    //TOFIX: aca hay algo raro.

    /**
     *
     * @param index
     * @return
     */
    public String getAction(int index) {
        if(index  < 0)
            return actions[8];

        if(index < actions.length)
        {
            return actions[index];
        }
        return actions[actions.length -1];
    }

    /**
     *
     * @param index
     * @return
     */
    public String getPercept(int index) {
        if (index < getPerceptsNumber() - 1) {
            return percepts[index];
        } else {
            return "PERCEPT ERROR";
        }
    }

    /**
     *
     * @return
     */
    public int getActionsNumber() {
        return actions.length;
    }

    /**
     *
     * @return
     */
    public int getPerceptsNumber() {
        //ROW*COLUMN +  SOLVED
        return  percepts.length;
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {

        TermitesLanguage language = new TermitesLanguage();

        int nActions = language.getActionsNumber();
        for (int i = 0; i < nActions; i++) {
            String action = language.getAction(i);
            System.out.println(i + ":" + action + ":" + language.getActionIndex(action));
        }

        /*int nPerceptions = language.getPerceptsNumber();
        for( int i=0; i<nPerceptions; i++ ){
        String perception = language.getPercept(i);
        System.out.println( i + ":" + perception );
        }*/

    }
}
