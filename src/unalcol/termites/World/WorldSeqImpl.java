/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.World;

import java.util.Hashtable;
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
public class WorldSeqImpl extends World {

    public Vector<Agent> Pieces;
    TermitesLanguage language;
    public Vector<int[]> Targets;
    public int currAgent;
    public int seconds;
    Hashtable<String, ConcurrentLinkedQueue> mbuffer = new Hashtable<String, ConcurrentLinkedQueue>();
    GenerateIntegerDataSet ds;
    private int agentsDie = 0;

    /*constructor called by world canvas */
    public WorldSeqImpl(Vector<Agent> agents, int w, int h) {
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
        age = 0;
        seconds = 0;
        ds = new GenerateIntegerDataSet(100);
        int n = this.getAgents().size();
        seekers = 0;
        carriers = 0;
        for (int k = 0; k < n; k++) {
            ((Termite) this.getAgent(k)).setAttribute("infi", createMatrixTemps(width, height));
        }
    }

    double[][] createMatrixTemps(int wi, int he) {
        double[][] m = new double[wi][he];
        for (int i = 0; i < wi; i++) {
            for (int j = 0; j < he; j++) {
                m[i][j] = -1;
            }
        }
        return m;
    }

    public void printMatrix(double[][] m) {
        for (double[] m1 : m) {
            for (int j = 0; j < m[0].length; j++) {
                System.out.print(m1[j] + " ");
            }
            System.out.println(" ");
        }
    }

    public Vector getTargets() {
        return Targets;
    }

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
    @Override
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

    @Override
    public boolean act(Agent agent, Action action) {

        String act = action.getCode();
        Termite t = (Termite) agent;
        t.setRound(t.getRound() + 1);
        boolean executed = false;
        StateProps box = getVal(t.getX(), t.getY());
        t.sleep(40);
        //System.out.println("agent" + agent);

        if (act.equals("die")) {
            t.die();
            this.getVal((int) (t.getX()), (int) (t.getY())).type = StateProps.NEUTRAL;
            this.getVal((int) (t.getX()), (int) (t.getY())).value = (float) 0.5;
            agentsDie++;
            return false;
        }

        //Secuential Program
        //System.out.println("t get x" + t.getX());
        if (t.getX() != width - 1) {
            act = "right";
        } else {
            act = "downright";
        }

        //@TOFIX: this not would be here!
        String idOther = getldNeighborPercept(agent);
        double temp = getTemperaturePercept(agent);
        //System.out.println("temp"  + temp);
        //System.out.println("(" + t.getX() + ", " + t.getY() + ")");
        ((double[][]) t.getAttribute("infi"))[t.getX()][t.getY()] = temp;

        /*System.out.println("-----------------------------");
         printMatrix((double[][]) t.getAttribute("infi"));
         System.out.println("-----------------------------");        
         */
        executed = updatePiecesInWorld(t, this.getActionIndex(act));
        DrawPieceInWorld(t);

        // adjust ant's pheromone (via learning rule; goes down to 0.5)
        if (idOther != null) { //box.isTarget
            String[] msg = new String[3]; //msg: [from|msg|channel]
            msg[0] = (String) t.getAttribute("ID");
            msg[1] = StringSerializer.serialize((float[][]) t.getAttribute("infi"));
            msg[2] = null;
            NetworkMessageBuffer.getInstance().putMessage(idOther, msg);
        }

        String myID = (String) t.getAttribute("ID");
        String[] inbox = NetworkMessageBuffer.getInstance().getMessage(myID);
        //inbox: id | infi | chan(null)
        if (inbox != null) {
            //System.out.println("talk (" + myID + "," + inbox[0] + ")");
            String senderID = inbox[0];
            float[][] inf = (float[][]) t.getAttribute("infi");
            float[][] senderInf = (float[][]) StringSerializer.deserialize(inbox[1]);
            int newinf = 0;
            int senderinf = 0;
            //compare and merge information
            for (int i = 0; i < inf.length; i++) {
                for (int j = 0; j < inf[0].length; j++) {
                    if (inf[i][j] == -1 && senderInf[i][j] != -1) { //{inf[i][j]) {*/
                        inf[i][j] = senderInf[i][j];
                    }
                    if (senderInf[i][j] != -1) {
                        senderinf++;
                    }
                    if (inf[i][j] != -1) {
                        newinf++;
                    }
                }
            }
        }
        //} 

        //if (t.getType() == StateProps.LEADER) {
        //DrawPieceInWorld(t);
        //}
        return executed;
    }

    @Override
    public void init(Agent agent) {
        Termite sim_agent = (Termite) agent;
        sim_agent.run();
    }

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


    public void updateSandC() {
        seekers = 0;
        carriers = 0;
        int average = 0;
        for (int k = 0; k < this.getAgents().size(); k++) {
            if ((this.getAgent(k)).status != Action.DIE) {
                if (((Termite) this.getAgent(k)).getStatus() == Termite.SEEKING) {
                    seekers++;
                } else if (((Termite) this.getAgent(k)).getStatus() == Termite.CARRYING) {
                    carriers++;
                }
                average += ((Termite) this.getAgent(k)).getRound();
            }
        }
        average /= this.getAgents().size();
        this.setAge(average);
    }

    /**
     * @return the agentsDie
     */
    public int getAgentsDie() {
        return agentsDie;
    }

    /**
     * @param agentsDie the agentsDie to set
     */
    public void setAgentsDie(int agentsDie) {
        this.agentsDie = agentsDie;
    }

}
