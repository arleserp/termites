/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.World;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import unalcol.termites.TermitesLanguage.TermitesLanguage;
import unalcol.termites.distributed.StringSerializer;
import unalcol.termites.distributed.GenerateIntegerDataSet;
import unalcol.termites.distributed.NetworkMessageBuffer;
import unalcol.agents.*;
import unalcol.random.real.GaussianGenerator;
import unalcol.types.collection.vector.Vector;

/**
 *
 * @author arles.rodriguez
 */
public class WorldTemperaturesImpl extends World {

    /**
     *
     */
    public Vector<Agent> Pieces;
    TermitesLanguage language;

    /**
     *
     */
    public Vector<int[]> Targets;

    /**
     *
     */
    public int currAgent;

    /**
     *
     */
    public int seconds;
    Hashtable<String, ConcurrentLinkedQueue> mbuffer = new Hashtable<String, ConcurrentLinkedQueue>();
    GenerateIntegerDataSet ds;

    /*constructor called by world canvas */

    /**
     *
     * @param agents
     * @param w
     * @param h
     */

    public WorldTemperaturesImpl(Vector<Agent> agents, int w, int h) {
        super(agents, w, h);
        width = w;
        System.out.println("w" + w);
        height = h;
        GaussianGenerator g = new GaussianGenerator(0.5, 0.4);
        System.out.println("h" + h);
        states = new StateProps[w][h];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                states[i][j] = new StateProps((float) 0.5, (float) Math.abs(g.next()));
            }
        }
        seconds = 0;
        ds = new GenerateIntegerDataSet(100);
        int n = this.getAgents().size();
        seekers = 0;
        carriers = 0;
        for (int k = 0; k < n; k++) {
            ((Termite) this.getAgent(k)).setAttribute("infi", createMatrixTemps(width, height));
        }
    }

    float[][] createMatrixTemps(int wi, int he) {
        float[][] m = new float[wi][he];
        for (int i = 0; i < wi; i++) {
            for (int j = 0; j < he; j++) {
                m[i][j] = -1;
            }
        }
        return m;
    }

    /**
     *
     * @param m
     */
    public void printMatrix(double[][] m) {
        for (double[] m1 : m) {
            for (int j = 0; j < m[0].length; j++) {
                System.out.print(m1[j] + " ");
            }
            System.out.println(" ");
        }
    }

    /**
     *
     * @return
     */
    public Vector getTargets() {
        return Targets;
    }

    /**
     *
     * @param pTargets
     */
    public void setTargets(Vector pTargets) {
        for (int i = 0; i < pTargets.size(); i++) {
            int input2[] = (int[]) pTargets.get(i);
            //System.out.println("setT(x,y) = (" + input2[0] + "," + input2[1] + ")");
            states[input2[0]][input2[1]].isTarget = true;
            getVal(input2[0], input2[1]).value = 6;
            getVal(input2[0], input2[1]).pherovalue = (float) 1.0;
        }
        this.Targets = pTargets;
    }

    public StateProps getVal(int x, int y) {
        //to fix: states could be stay out of matrix! xD
        //System.out.println("(x,y) = (" + x + "," + y + ")");
        if (x < 0) {
            x = width - 1;
        }
        if (y < 0) {
            y = height - 1;
        }
        if (x < 0 || y < 0) {
            System.out.println("(x,y) = (" + x % width + "," + y % height + ")");
        }
        return states[x % width][y % height];
    }

    public boolean updatePiecesInWorld(Termite piece, int direction) {
        if (Directions.NONE == direction) {
            return true;
        }
        //2011.04.06 review collitions in a fast way
        if (hasCollisions(piece, direction)) {
            //    direction = (int) (Math.random() * 8.0);
            return false;
        }

        if (!this.getVal((int) (piece.getX()), (int) (piece.getY())).isHome && !this.getVal((int) (piece.getX()), (int) (piece.getY())).isTarget) {
            this.getVal((int) (piece.getX()), (int) (piece.getY())).type = StateProps.NEUTRAL;
            this.getVal((int) (piece.getX()), (int) (piece.getY())).value = (float) 0.5;
        }

        if (direction == Directions.UP) {
            piece.setY(piece.getY() - 1);
        }
        if (direction == Directions.DOWN) {
            piece.setY(piece.getY() + 1);
        }
        if (direction == Directions.LEFT) {
            piece.setX(piece.getX() - 1);
        }
        if (direction == Directions.RIGHT) {
            piece.setX(piece.getX() + 1);
            piece.setX(piece.getX() % this.width);
        }

        if (direction == Directions.DOWNLEFT) {
            piece.setX(piece.getX() - 1);
            piece.setY(piece.getY() + 1);
        }
        if (direction == Directions.DOWNRIGHT) {
            piece.setY(piece.getY() + 1);
            piece.setX(piece.getX() + 1);
        }
        if (direction == Directions.UPLEFT) {
            piece.setX(piece.getX() - 1);
            piece.setY(piece.getY() - 1);
        }
        if (direction == Directions.UPRIGHT) {
            piece.setY(piece.getY() - 1);
            piece.setX(piece.getX() + 1);
        }
        if (piece.getY() < 0) {
            piece.setY(this.height - 1);
        }
        if (piece.getX() < 0) {
            piece.setX(this.width - 1);
        }
        piece.setY(piece.getY() % this.height);
        piece.setX(piece.getX() % this.width);
        return true;
    }

    /**
     *
     * @param piece
     */
    public void DrawPieceInWorld(Termite piece) {

        int x = piece.getX();
        int y = piece.getY();
        this.getVal(x, y).type = piece.getType();

        if (!this.getVal(x, y).isHome && !this.getVal(x, y).isTarget) {
            if (((Termite) piece).getDiagnoseStatus() != Termite.NOTDIAGNOSE) {
                this.getVal(x, y).value = (float) 3;
            } else if (((Termite) piece).getStatus() == Termite.CARRYING) {
                this.getVal(x, y).value = (float) 4;
                // } else if (!piece.diagnoseVM.isCanDiagnose()) {
                //     this.getVal(x, y).value = (float) 5;
            } else if (((Termite) piece).getStatus() == StateProps.LEADER) {
                this.getVal(x, y).value = StateProps.LEADER;
            } else {
                this.getVal(x, y).value = (float) 1;
            }
        }
    }

    public boolean hasCollisions(Termite piece, int direction) {
        boolean[] neighbors = getcollidePercept(piece);
        return (direction < neighbors.length && neighbors[direction]);
    }

    //obtains a free position for paint a termite
    public Percept sense(Agent agent) {
        Percept p = new Percept();

        float[] pheromones = getPheromonePercept(agent);
        p.setAttribute("pheromone", pheromones);

        Integer seekingStatus = getSeekingPercept(agent);
        p.setAttribute("seekingStatus", seekingStatus);

        boolean[] neighborTermite = getneiborTermitePercept(agent);
        p.setAttribute("neighborTermite", neighborTermite);

        String leaderPercept = getldNeighborPercept(agent);
        p.setAttribute("leaderPercept", leaderPercept);

        double tempPercept = getTemperaturePercept(agent);
        p.setAttribute("tempPercept", tempPercept);
        return p;
    }

    /**
     *
     * @param agent
     * @param action
     * @return
     */
    public boolean act(Agent agent, Action action) {
        String act = action.getCode();
        Termite t = (Termite) agent;
        t.setRound(t.getRound() + 1);
        boolean executed = false;
        StateProps box = getVal(t.getX(), t.getY());
        t.sleep(20);
        //System.out.println("agent" + agent);

        if (act.equals("die")) {
            t.die();
            this.getVal((int) (t.getX()), (int) (t.getY())).type = StateProps.NEUTRAL;
            this.getVal((int) (t.getX()), (int) (t.getY())).value = (float) 0.5;
            agentsDie++;
            updateSandC();
            return false;
        }

        //read temp
        float temp = getTemperaturePercept(agent);
        ((float[][]) t.getAttribute("infi"))[t.getX()][t.getY()] = temp;

        /*System.out.println("-----------------------------");
         printMatrix((double[][]) t.getAttribute("infi"));
         System.out.println("-----------------------------");        
         */
        //@TOFIX: this not would be here!
        String idOther = getldNeighborPercept(agent);
        // adjust ant's pheromone (via learning rule; goes down to 0.5)
        if (idOther != null) { //box.isTarget
            String[] msg = new String[3]; //msg: [from|msg|channel]
            msg[0] = (String) t.getAttribute("ID");
            msg[1] = StringSerializer.serialize((float[][]) t.getAttribute("infi"));
            msg[2] = "N";
            NetworkMessageBuffer.getInstance().putMessage(idOther, msg);
        }

        // adjust ant's pheromone (via learning rule; goes down to 0.5)
        t.setPheromone((float) (t.getPheromone() + 0.01f * (0.5f - t.getPheromone())));
        box.pherovalue = (float) (box.pherovalue + 0.01 * (((Termite) t).getPheromone() - box.pherovalue));
        t.lostCounter++;

        executed = updatePiecesInWorld(t, this.getActionIndex(act));
        DrawPieceInWorld(t);

        String myID = (String) t.getAttribute("ID");
        String[] inbox = NetworkMessageBuffer.getInstance().getMessage(myID);
        //inbox: id | infi | chan(null)
        if (inbox != null) {
            //Update current information
            String senderID = inbox[0];
            float[][] inf = (float[][]) t.getAttribute("infi");
            boolean complete = true;

            //D represents diff information
            if (inbox[2].equals("D")) {
                Hashtable senderInf = (Hashtable) StringSerializer.deserialize(inbox[1]);
                for (Iterator<int[]> iterator = senderInf.keySet().iterator(); iterator.hasNext();) {
                    int[] key = (int[]) iterator.next();
                    inf[key[0]][key[1]] = (float) senderInf.get(key);
                  //  t.setPheromone((float) 0);
                   // t.setStatus(Termite.SEEKING);
                }
            } else { //Gets and Evaluates what is new
                Hashtable Diff = null;
                float[][] senderInf = (float[][]) StringSerializer.deserialize(inbox[1]);
                for (int i = 0; i < inf.length; i++) {
                    for (int j = 0; j < inf[0].length; j++) {
                        if (inf[i][j] != -1 && senderInf[i][j] == -1) { //{inf[i][j]) {*/
                            if (Diff == null) {
                                Diff = new Hashtable();
                            }
                            int[] loc = new int[2];
                            loc[0] = i;
                            loc[1] = j;
                            Diff.put(loc, inf[i][j]);
                        }
                        if (inf[i][j] == -1 && senderInf[i][j] != -1) { //{inf[i][j]) {*/

                            inf[i][j] = senderInf[i][j];
                        }
                        if (inf[i][j] == -1) {
                            complete = false;
                        }
                    }
                }
                //Send the difference between the information only
                if (Diff != null) {
                    String[] msg = new String[3]; //msg: [from|msg|channel]
                    msg[0] = (String) t.getAttribute("ID");
                    msg[1] = StringSerializer.serialize(Diff);
                    msg[2] = "D";
                    NetworkMessageBuffer.getInstance().putMessage(senderID, msg);
                    t.setPheromone((float) 1.0);
                    t.setStatus(Termite.CARRYING);
                } else {
                    t.setPheromone((float) 0);
                    t.setStatus(Termite.SEEKING);
                }

                if (getRoundGetInfo() == -1 && complete) {
                    setRoundGetInfo(t.getRound());
                }
            }
        }
        return true;
    }

    /**
     *
     * @param agent
     */
    @Override
    public void init(Agent agent) {
        Termite sim_agent = (Termite) agent;
        sim_agent.run();
    }

    /**
     *
     * @return
     */
    @Override
    public Vector<Action> actions() {
        Vector<Action> acts = new Vector<>();
        int n = language.getActionsNumber();
        for (int i = 0; i < n; i++) {
            acts.add(new Action(language.getAction(i)));
        }
        return acts;
    }

    private int getActionIndex(String action) {
        String[] actions = new String[]{"right", "downright", "down", "downleft", "left", "upleft", "up", "upright", "none", "diagnose", "responsemsg"};
        for (int i = 0; i < actions.length; i++) {
            if (action.equals(actions[i])) {
                return i;
            }
        }
        return 0;
    }


    private String getldNeighborPercept(Agent agent) {
        Termite t = (Termite) agent;
        //get the vecinity
        int[] xs = {1, 1, 0, -1, -1, -1, 0, 1, 0};
        int[] ys = {0, 1, 1, 1, 0, -1, -1, -1, 0};
        boolean[] agentPerceptVector = new boolean[xs.length];

        for (int j = 0; j < agentPerceptVector.length; j++) {
            int n = agents.size();
            int nposx = t.getX() + xs[j];
            int nposy = t.getY() + ys[j];

            for (int i = 0; i < n; i++) {
                Termite l = (Termite) agents.get(i);
                if (l.getX() == nposx && l.getY() == nposy) {
                    return (String) l.getAttribute("ID");
                    //}
                }
            }

        }
        return null;
    }



}
