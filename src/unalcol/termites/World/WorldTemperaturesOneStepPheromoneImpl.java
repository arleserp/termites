/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.World;

import java.util.Hashtable;
import unalcol.termites.TermitesLanguage.TermitesLanguage;
import unalcol.termites.distributed.StringSerializer;
import unalcol.termites.distributed.GenerateIntegerDataSet;
import unalcol.termites.distributed.NetworkMessageBuffer;
import unalcol.agents.*;
import unalcol.random.real.GaussianGenerator;
import unalcol.termites.Runnable.AppMain;
import unalcol.termites.distributed.HashtableOperations;
import unalcol.types.collection.vector.Vector;

/**
 *
 * @author arles.rodriguez
 */
public class WorldTemperaturesOneStepPheromoneImpl extends World {

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
    GenerateIntegerDataSet ds;

    /*constructor called by world canvas */

    /**
     *
     * @param agents
     * @param w
     * @param h
     */

    public WorldTemperaturesOneStepPheromoneImpl(Vector<Agent> agents, int w, int h) {
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

    /**
     *
     * @param agent
     * @param action
     * @return
     */
    @Override
    public boolean act(Agent agent, Action action) {
        String act = action.getCode();
        Termite t = (Termite) agent;
        t.setRound(t.getRound() + 1);
        StateProps box = getVal(t.getX(), t.getY());
        t.sleep(15);

        //Agent Die and dissapears from the world
        if (act.equals("die")) {
            t.die();
            this.getVal((int) (t.getX()), (int) (t.getY())).type = StateProps.NEUTRAL;
            this.getVal((int) (t.getX()), (int) (t.getY())).value = (float) 0.5;
            increaseAgentsDie();
            updateSandC();
            return false;
        }

        //Read data in current location
        float temp = getTemperaturePercept(agent);
        String loc = t.getX() + "-" + t.getY();

        if (!((Hashtable) t.getAttribute("inf_i")).containsKey(loc)) {
            t.incExploredTerrain();
            if (t.getStatus() == Termite.SEEKING) {
                t.setPheromone((float) 0);
            }
            if (t.getStatus() == Termite.CARRYING) {
                t.setPheromone((float) 1);
            }
            ((Hashtable) t.getAttribute("inf_i")).put(loc, temp);
        }

        //sense other and send its information
        String idOther = getIdNeighborPercept(agent);

        // If there is another neighbor send its current information
        if (idOther != null) {
            if (!((Hashtable) t.getAttribute("inf_i")).isEmpty()) {
                String[] msg = new String[2]; //msg: [from|msg]
                msg[0] = (String) t.getAttribute("ID");
                msg[1] = StringSerializer.serialize(((Hashtable) t.getAttribute("inf_i")));
                NetworkMessageBuffer.getInstance().putMessage(idOther, msg);
            }
            t.incMsgSend();
        }

        String myID = (String) t.getAttribute("ID");
        String[] inbox = NetworkMessageBuffer.getInstance().getMessage(myID);

        //inbox: id | infi 
        if (inbox != null) {
            t.incMsgRecv();
            boolean gotNewInf = false;
            Hashtable senderInf = (Hashtable) StringSerializer.deserialize(inbox[1]);
            Hashtable inf_i = ((Hashtable) t.getAttribute("inf_i"));

            int oldsize = inf_i.size();
            inf_i = HashtableOperations.JoinSets(inf_i, senderInf);

            t.setAttribute("inf_i", inf_i);
            if (inf_i.size() > oldsize) {
                gotNewInf = true;
            }

            //Evaluates Differences
            if (!gotNewInf) {
                t.setPheromone((float) 1.0);
                t.setStatus(Termite.CARRYING);
            } else {
                t.setPheromone((float) 0);
                t.setStatus(Termite.SEEKING);
            }
        }

        //Verifies its information is complete
        boolean complete = false;
        int aminfo = ((Hashtable) t.getAttribute("inf_i")).size();
        //System.out.println("aminfo:" + aminfo + ", size:" + width * height);
        if (completeInfo(aminfo)) {
            complete = true;
        }

        if (getRoundGetInfo() == -1 && complete) {
            System.out.println("complete! round" + t.getRound());
            setRoundGetInfo(t.getRound());
            setIdBest(t.getId());
            updateSandC();
        }

        // adjust ant's pheromone (via learning rule; goes down to 0.5)
        t.setPheromone((float) (t.getPheromone() + 0.01f * (0.5f - t.getPheromone())));

        //by now I store the pheromone in all until get the best.
        // I know it's hardcoded but is experimental.
        /*if (getRoundGetInfo() == -1) {
         t.getPhHistory().put(t.getRound(), t.getPheromone());
         t.getInfHistory().put(t.getRound(), aminfo);
         }*/
        box.pherovalue = (float) (box.pherovalue + 0.01 * (((Termite) t).getPheromone() - box.pherovalue));
        boolean executed;
        if (AppMain.walls.equals("wallsoff")) {
            executed = updatePiecesInWorld(t, this.getActionIndex(act));
        } else {
            executed = updatePiecesInWorldNotToroidal(t, this.getActionIndex(act));
        }
        DrawPieceInWorld(t);
        return executed;

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

    private void showMap() {
        int l05 = 0;
        int e05 = 0;
        int g05 = 0;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (states[i][j].pherovalue > 0.5) {
                    g05++;
                } else if (states[i][j].pherovalue < 0.5) {
                    l05++;
                } else {
                    e05++;
                }
            }
        }
        System.out.println(l05 + "," + e05 + "," + g05);
    }

}
