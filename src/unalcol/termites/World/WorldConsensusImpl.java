/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.World;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentLinkedQueue;
import unalcol.termites.TermitesLanguage.TermitesLanguage;
import unalcol.termites.distributed.StringSerializer;
import unalcol.termites.distributed.GenerateIntegerDataSet;
import unalcol.termites.distributed.NetworkMessageBuffer;
import unalcol.agents.*;
import unalcol.types.collection.vector.Vector;

/**
 *
 * @author arles.rodriguez
 */
public class WorldConsensusImpl extends World {

    /**
     *
     */
    public Vector<Agent> Pieces;
    private int age;
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

    /**
     *
     */
    public int food;
    Hashtable<String, ConcurrentLinkedQueue> mbuffer = new Hashtable<String, ConcurrentLinkedQueue>();
    GenerateIntegerDataSet ds;

    Hashtable<String, Percept> p; 

    /*constructor called by world canvas */

    /**
     *
     * @param agents
     * @param w
     * @param h
     */

    public WorldConsensusImpl(Vector<Agent> agents, int w, int h) {
        super(agents, w, h);
        width = w;
        System.out.println("w" + w);
        height = h;
        System.out.println("h" + h);
        states = new StateProps[w][h];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                states[i][j] = new StateProps((float) 0.5, 0);
            }
        }
        Pieces = new Vector<Agent>();
        Targets = new Vector<int[]>();
        age = 0;
        seconds = 0;
        food = 0;
        ds = new GenerateIntegerDataSet(100);
        int n = this.getAgents().size();
        seekers = 0;
        carriers = 0;
        for (int k = 0; k < n; k++) {
            if (((Termite) this.getAgent(k)).getStatus() == Termite.SEEKING) {
                seekers++;
            } else if (((Termite) this.getAgent(k)).getStatus() == Termite.CARRYING) {
                carriers++;
            }
        }
        p = new Hashtable<String, Percept>();
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

    /**
     *
     * @return
     */
    public int getFood() {
        return food;
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
            //} else if (!piece.diagnoseVM.isCanDiagnose()) {
              //  this.getVal(x, y).value = (float) 5;
            } else if (((Termite) piece).getStatus() == StateProps.LEADER) {
                this.getVal(x, y).value = StateProps.LEADER;
            } else {
                this.getVal(x, y).value = (float) 1;
            }
        }
    }

    /**
     * @return the age
     */
    public int getAge() {
        return age;
    }

    /**
     * @param age the age to set
     */
    public void setAge(int age) {
        this.age = age;
    }

    public void increaseAge() {
        this.age++;
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

        String leaderPercept = getleaderPercept(agent);
        p.setAttribute("leaderPercept", neighborTermite);

        return p;
    }

    /**
     *
     * @param agent
     * @param action
     * @return
     */
    public boolean act(Agent agent, Action action) {
        this.increaseAge();
        String act = action.getCode();
        Termite t = (Termite) agent;
        boolean executed = false;
        StateProps box = getVal(t.getX(), t.getY());
        t.sleep(10);
        //System.out.println("agent" + agent);

        
        //@TOFIX: this not would be here!
        String idLeader = getleaderPercept(agent);

        //if (t.getType() == StateProps.SEEKER) {
        t.setPheromone((float) (t.getPheromone() + 0.01f * (0.5f - t.getPheromone())));
        // adjust ant's pheromone (via learning rule; goes down to 0.5)
        if (idLeader != null) { //box.isTarget
            // t.setPheromone((float) 1.0);
            //piece.setStatus(Termite.CARRYING);
            //piece.foodIteration = this.age;
            //piece.lostCounter = 0;
            String[] msg = new String[3]; //msg: [from|msg|channel]
            msg[0] = (String) t.getAttribute("ID");
            msg[1] = StringSerializer.serialize((ArrayList) t.getAttribute("infi"));
            msg[2] = null;
            NetworkMessageBuffer.getInstance().putMessage(idLeader, msg);
            //piece.setType(StateProps.LEADER);
        }/* else if (box.isHome) {
         if (t.getStatus() == Termite.CARRYING) {
         t.lostCounter = 0;
         food++;
         t.lastTime = this.age - t.foodIteration;
         }
         t.setPheromone((float) 0);
         t.setStatus(Termite.SEEKING);
         } else {*/

        box.pherovalue = (float) (box.pherovalue + 0.01 * (((Termite) t).getPheromone() - box.pherovalue));
        t.lostCounter++;
        //}
        executed = updatePiecesInWorld(t, this.getActionIndex(act));
        DrawPieceInWorld(t);
        //} 

        if (t.getType() == StateProps.LEADER) {
            if (Math.random() > 0.005) {
            //if (this.getAge() % 1000 == 0) {
                giveInfoToLeaders(t);
            }
            DrawPieceInWorld(t);
        }

        String myID = (String) t.getAttribute("ID");
        String[] inbox = NetworkMessageBuffer.getInstance().getMessage(myID);

        //inbox: id | infi | chan(null)
        if (inbox != null) {
            String senderID = inbox[0];
            ArrayList<Integer> inf = (ArrayList<Integer>) t.getAttribute("infi");
            ArrayList<Integer> senderInf = (ArrayList)StringSerializer.deserialize(inbox[1]);
            if (inf.size() > senderInf.size()) {
                //System.out.println("inf:" + inf);
                //System.out.println("id" +  senderID + "senderInf:" + senderInf);
                String[] msg = new String[3]; //msg: [from|msg|channel]
                msg[0] = (String) t.getAttribute("ID");
                msg[1] = StringSerializer.serialize((ArrayList) t.getAttribute("infi"));
                msg[2] = null;
                NetworkMessageBuffer.getInstance().putMessage(senderID, msg);
                //piece.setStatus(StateProps.LEADER);
                t.setPheromone((float) 1.0);

                if (t.getStatus() == Termite.SEEKING) {
                    seekers--;
                    carriers++;
                    //System.out.println("s:" + seekers + ", c:" + carriers);
                }
                t.setStatus(Termite.CARRYING);
                t.foodIteration = this.age;
                //piece.lostCounter = 0;
            } else {
                //System.out.println("inf:" + t.getId() + ":" + inf);
                //System.out.println("senderInf:" + senderInf);
                t.setPheromone((float) 0);
                if (t.getStatus() == Termite.CARRYING) {
                    //  t.lostCounter = 0;
                    t.lastTime = this.age - t.foodIteration;
                    carriers--;
                    seekers++;
                }
                t.setStatus(Termite.SEEKING);
                for (int i = inf.size(); i < senderInf.size(); i++) {
                    ((ArrayList<Integer>) t.getAttribute("infi")).add(senderInf.get(i));
                }
                //System.out.println("inf" + inf);
                //piece.setStatus(StateProps.SEEKER);
            }
        }
        return executed;
    }

    /**
     *
     * @param agent
     */
    public void init(Agent agent) {
        Termite sim_agent = (Termite) agent;
        sim_agent.run();
    }

    /**
     *
     * @return
     */
    public Vector<Action> actions() {
        Vector<Action> acts = new Vector<Action>();
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




 

    private String getleaderPercept(Agent agent) {
        Termite t = (Termite) agent;
        //get the vecinity
        int[] xs = {1, 1, 0, -1, -1, -1, 0, 1, 0};
        int[] ys = {0, 1, 1, 1, 0, -1, -1, -1, 0};
        boolean[] agentPerceptVector = new boolean[xs.length];

        //if we are in home we are in the target
        //if (this.getVal(t.getX(), t.getY()).isHome || this.getVal(t.getX(), t.getY()).isTarget) {
        //    for (int j = 0; j < agentPerceptVector.length; j++) {
        //        agentPerceptVector[j] = false;
        //    }
        //}
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



    private void giveInfoToLeaders(Termite t) {
        int newdata = ds.getNext();
        if (newdata != -1) {
            ((ArrayList) t.getAttribute("infi")).add(newdata);
            System.out.println("new inf" + newdata);
        }
        updateSandC();
        setChanged();
        notifyObservers();
    }

    public int getSeekers() {
        return seekers; //To change body of generated methods, choose Tools | Templates.
    }

    public int getCarriers() {
        return carriers; //To change body of generated methods, choose Tools | Templates.
    }

    public void updateSandC() {
        seekers = 0;
        carriers = 0;
        for (int k = 0; k < this.getAgents().size(); k++) {
            if (((Termite) this.getAgent(k)).getStatus() == Termite.SEEKING) {
                seekers++;
            } else if (((Termite) this.getAgent(k)).getStatus() == Termite.CARRYING) {
                carriers++;
            }
        }
    }

}
