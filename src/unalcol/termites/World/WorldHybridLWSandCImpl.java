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
import unalcol.termites.Program.HybridLwSandCProgramImpl;
import unalcol.termites.Program.HybridPheromoneLevyWalkProgramImpl;
import unalcol.termites.Runnable.AppMain;
import unalcol.termites.distributed.HashtableOperations;
import unalcol.types.collection.vector.Vector;

/**
 *
 * @author arles.rodriguez
 */
public class WorldHybridLWSandCImpl extends World {

    public Vector<Agent> Pieces;
    TermitesLanguage language;
    public Vector<int[]> Targets;
    public int currAgent;
    public int seconds;
    public int LevyWalkers;
    Hashtable<String, ConcurrentLinkedQueue> mbuffer = new Hashtable<String, ConcurrentLinkedQueue>();
    GenerateIntegerDataSet ds;

    /*constructor called by world canvas */
    public WorldHybridLWSandCImpl(Vector<Agent> agents, int w, int h) {
        super(agents, w, h);
        width = w;
        System.out.println("w" + w);
        height = h;
        GaussianGenerator g = new GaussianGenerator(0.5, 0.4);
        System.out.println("h" + h);
        states = new StateProps[w][h];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                states[i][j] = new StateProps((float) 0, (float) Math.abs(g.next()));
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

    @Override
    public void updateSandC() {
        seekers = 0;
        carriers = 0;
        LevyWalkers = 0;
        int average = 0;
        int agentslive = 0;
        for (int k = 0; k < this.getAgents().size(); k++) {

            if ((this.getAgent(k)).status != Action.DIE) {
                Termite t = (Termite) this.getAgent(k);
                if (((HybridLwSandCProgramImpl) t.getProgram()).isStrategyCarrier()) {
                    carriers++;
                } else if (((HybridLwSandCProgramImpl) t.getProgram()).isStrategySeeker()) {
                    seekers++;
                } else {
                    LevyWalkers++;
                }
                average += ((Termite) this.getAgent(k)).getRound();
                agentslive++;
            }

        }
        if (agentslive != 0) {
            average /= agentslive;
            //System.out.println("age" + this.getAge() + ", average" + average );
            if (average - this.getAge() > 1) {
                //System.out.println("h");
                //System.out.println(average);
                //evaporatePheromone();
            }
            this.setAge(average);
        }
    }

    @Override
    public boolean act(Agent agent, Action action) {
        String act = action.getCode();
        Termite t = (Termite) agent;
        boolean gotNewInfo = false;

        t.setRound(t.getRound() + 1);
        StateProps box = getVal(t.getX(), t.getY());
        t.sleep(15);

        //Agent Die and dissapears from the world
        if (act.equals("die")) {
            t.die();
            this.getVal((int) (t.getX()), (int) (t.getY())).type = StateProps.NEUTRAL;
            this.getVal((int) (t.getX()), (int) (t.getY())).value = (float) 0.5;
            agentsDie++;
            setLastAgentFail(t.getId(), t.getRound(), ((Hashtable)t.getAttribute("inf_i")).size());
            return false;
        }
        
        //Read data in current location
        float temp = getTemperaturePercept(agent);
        String loc = t.getX() + "-" + t.getY();

        if (!((Hashtable) t.getAttribute("inf_i")).containsKey(loc)) {
            t.incExploredTerrain();
            if (t.getStatus() == Termite.CARRYING) {
                t.setPheromone((float) 1);
            }
            ((Hashtable) t.getAttribute("inf_i")).put(loc, temp);
            gotNewInfo = true;
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

            if (gotNewInf) {
                t.setPheromone((float) 1.0);
                gotNewInfo = true;
            }
        }

        //Verifies its information is complete
        boolean complete = false;
        int aminfo = ((Hashtable) t.getAttribute("inf_i")).size();

        if (completeInfo(aminfo)) {
            complete = true;
        }

        if (getRoundGetInfo() == -1 && complete) {
            System.out.println("complete! round" + t.getRound());
            setRoundGetInfo(t.getRound());
            setIdBest(t.getId());
        }

        //by now I store the pheromone in all until get the best.
        // I know it's hardcoded but is experimental.
        if (getRoundGetInfo() == -1) {
            t.getPhHistory().put(t.getRound(), t.getPheromone());
            t.getInfHistory().put(t.getRound(), aminfo);
        }

        // adjust ant's pheromone (via learning rule; goes down to 0.5)
        //if (t.getStatus() == Termite.CARRYING) {
        t.setPheromone((float) (t.getPheromone() + 0.01f * (0.5f - t.getPheromone())));
        box.pherovalue = (float) (box.pherovalue + 0.01 * (((Termite) t).getPheromone() - box.pherovalue));
        //}

        boolean executed;
        if (AppMain.walls.equals("wallsoff")) {
            executed = updatePiecesInWorld(t, this.getActionIndex(act));
        } else {
            executed = updatePiecesInWorldNotToroidal(t, this.getActionIndex(act));
        }
        DrawPieceInWorld(t);
        //showMap();
        //System.out.println("new info:" + gotNewInfo);
        ((HybridLwSandCProgramImpl) t.getProgram()).addHistory(gotNewInfo);
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
