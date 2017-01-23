package unalcol.termites.World;

import java.util.Hashtable;
import unalcol.termites.VM.TermitesInterpreterBinaryImpl;
import unalcol.termites.VM.TermitesVMBinaryImpl;
import unalcol.agents.Agent;
import unalcol.agents.AgentProgram;
import unalcol.termites.Runnable.AppMain;

/**
 *
 * @author ARODRIGUEZ
 */
public class Termite extends Agent {

    private int type = 0;

    /**
     *
     */
    public static final int SEEKING = 0;

    /**
     *
     */
    public static final int CARRYING = 1;

    /**
     *
     */
    public static final int DIAGNOSE = 2;

    /**
     *
     */
    public static final int OVERDIAGNOSE = 3;

    /**
     *
     */
    public static final int STARTDIAGNOSE = 4;

    /**
     *
     */
    public static final int NOTDIAGNOSE = 5;
    private float pheromone;
    private int mystatus;
    private int diagnoseStatus;
    private int id;

    /**
     *
     */
    public int voteAboutProgram = 0;

    /**
     *
     */
    public int diagnoseNumber = 0;

    /**
     *
     */
    public boolean putPheromone = true;
    private int ImGoodCounter = 0;

    /**
     *
     */
    public double persProb = 0.5;

    /**
     *
     */
    public double diagProb = 0.5;

    /**
     *
     */
    public int votesThreshold = 20;

    /**
     *
     */
    public int foodIteration;

    /**
     *
     */
    public int lastTime;

    /**
     *
     */
    public int lostCounter;
    /* This two variables are for the voting threshold */
    private double lambda = 0.5;

    /**
     *
     */
    public int votesStatus;

    /**
     *
     */
    public int totalVotes;
    private int x;
    private int y;
    private int round;
    private int nMsgSend;
    private int nMsgRecv;
    private Hashtable phHistory;
    private Hashtable infHistory;
    private int roundsWithoutNewInfo;
    private int exploredTerrain;
    private Hashtable infi;

    //Relative coordinate system
    private int minx;
    private int miny;
    private int maxx;
    private int maxy;

    //DS Simulator

    /**
     *
     */
    protected Hashtable<String, Object> properties = new Hashtable<>();
    //LinkedBlockingQueue<Object> mbuffer = new LinkedBlockingQueue<Object>();

    /**
     *
     * @param program
     * @param id
     */
    public Termite(AgentProgram program, int id) {
        super(program);
        if (AppMain.getMode().equals("oneph") || AppMain.getMode().equals("onephevap") || AppMain.getMode().equals("lwphevap") || AppMain.getMode().equals("hybrid") || AppMain.getMode().equals("hybrid2") || AppMain.getMode().equals("hybrid3") || AppMain.getMode().equals("hybrid4") || AppMain.getMode().equals("lwphevap2") || AppMain.getMode().equals("turnoncontact") || AppMain.getMode().equals("lwphevapMap")) {
            this.mystatus = CARRYING;
            this.pheromone = (float) 1;
        } else {
            this.mystatus = SEEKING;
            if (AppMain.getMode().equals("lwphclwevap")) {
                this.pheromone = (float) 1;
            } else {
                this.pheromone = (float) 0;
            }
        }
        this.id = id;
        this.diagnoseStatus = NOTDIAGNOSE;
        //GaussianGenerator g = new GaussianGenerator(20.0, 3.0);
        this.votesThreshold = 20; // (int)g.next();
        this.foodIteration = 0;
        this.lastTime = 0;
        this.votesStatus = 0;
        this.totalVotes = 0;
        this.round = 0;
        this.nMsgRecv = 0;
        this.nMsgSend = 0;
        this.phHistory = new Hashtable();
        this.infHistory = new Hashtable();
        this.roundsWithoutNewInfo = 0;
        this.exploredTerrain = 0;
        this.minx = Integer.MAX_VALUE;
        this.miny = Integer.MAX_VALUE;
        this.maxx = Integer.MIN_VALUE;
        this.maxy = Integer.MIN_VALUE;
    }

    /**
     * @return the status
     */
    public int getStatus() {
        return mystatus;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(int status) {
        this.mystatus = status;
    }

    /**
     * @return the pheromone
     */
    public float getPheromone() {
        return pheromone;
    }

    /**
     * @param pheromone the pheromone to set
     */
    public void setPheromone(float pheromone) {
        this.pheromone = pheromone;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the diagnoseStatus
     */
    public int getDiagnoseStatus() {
        return diagnoseStatus;
    }

    /**
     * @param diagnoseStatus the diagnoseStatus to set
     */
    public void setDiagnoseStatus(int diagnoseStatus) {
        this.diagnoseStatus = diagnoseStatus;
    }

    /**
     */
    public void increaseVoteProgram() {
        voteAboutProgram++;

    }

    /**
     *
     */
    public void decreaseVoteProgram() {
        voteAboutProgram--;
    }

    /**
     *
     * @return
     */
    public String printVotes() {
        String sVotes = "";
        sVotes = id + ";" + voteAboutProgram;
        return sVotes;
    }

    /**
     *
     * @return
     */
    public int getVoteAboutProgram() {
        return voteAboutProgram;
    }

    /**
     *
     * @param voteAboutProgram
     */
    public void setVoteAboutProgram(int voteAboutProgram) {
        this.voteAboutProgram = voteAboutProgram;
    }
    
    /**
     *
     */
    public void increaseImGoodCounter() {
        this.ImGoodCounter++;
    }

    /**
     *
     * @param ImGoodCounter
     */
    public void setImGoodCounter(int ImGoodCounter) {
        this.ImGoodCounter = ImGoodCounter;
    }

    /**
     *
     * @return
     */
    public int getImGoodCounter() {
        return ImGoodCounter;
    }

    /**
     * @return the lambda
     */
    public double getLambda() {
        return lambda;
    }

    /**
     * @param lambda the lambda to set
     */
    public void setLambda(double lambda) {
        this.lambda = lambda;
    }

    /**
     *
     */
    public void votesplus() {
        this.votesStatus++;
    }

    /**
     *
     */
    public void votesneg() {
        this.votesStatus--;
    }

    /**
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     *
     * @param t
     */
    public void setType(int t) {
        this.type = t;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    //DS Part

    /**
     *
     * @param key
     * @param value
     */
    public void setAttribute(String key, Object value) {
        properties.put(key, value);
    }

    /**
     *
     * @param key
     * @return
     */
    public Object getAttribute(String key) {
        return properties.get(key);
    }

    /**
     *
     * @param key
     * @return
     */
    public boolean removeAttribute(String key) {
        if (properties.remove(key) != null) {
            return true;
        } else {
            return (false);
        }
    }

    //  public void putMessage(Object message) {
    //       mbuffer.add(message);
    //   }
    //   public Object getMessage() {
    //       return mbuffer.poll();
    //   }
    /**
     * @return the round round is defined as the number of iteration by
     * distributed Asynchronous Round-Based Computation Differently from
     * synchronous sys -tems, the rounds are not given for free in an
     * asynchronous system. Each process pi has to handle a local variable ri
     * which denotes its current round number. We first consider that, in each
     * round r it executes, a process sends a message to each of its neighbors,
     * and receives a message from each of them.
     */
    public int getRound() {
        return round;
    }

    /**
     * @param round the round to set
     */
    public void setRound(int round) {
        this.round = round;
    }

    /**
     *
     * @param msg
     */
    public void log(String msg) {
        System.out.println("Agent: " + this.getAttribute("ID") + " : " + msg);
    }

    /**
     * @return the nMsgSend
     */
    public int getnMsgSend() {
        return nMsgSend;
    }

    /**
     * @param nMsgSend the nMsgSend to set
     */
    public void setnMsgSend(int nMsgSend) {
        this.nMsgSend = nMsgSend;
    }

    /**
     * @return the nMsgRecv
     */
    public int getnMsgRecv() {
        return nMsgRecv;
    }

    /**
     * @param nMsgRecv the nMsgRecv to set
     */
    public void setnMsgRecv(int nMsgRecv) {
        this.nMsgRecv = nMsgRecv;
    }

    /**
     *
     */
    public void incMsgSend() {
        synchronized (Termite.class) {
            nMsgSend++;
        }
    }

    /**
     *
     */
    public void incMsgRecv() {
        synchronized (Termite.class) {
            nMsgRecv++;
        }
    }

    /**
     * @return the phHistory
     */
    public Hashtable getPhHistory() {
        return phHistory;
    }

    /**
     * @param phHistory the phHistory to set
     */
    public void setPhHistory(Hashtable phHistory) {
        this.phHistory = phHistory;
    }

    /**
     * @return the infHistory
     */
    public Hashtable getInfHistory() {
        return infHistory;
    }

    /**
     * @param infHistory the infHistory to set
     */
    public void setInfHistory(Hashtable infHistory) {
        this.infHistory = infHistory;
    }

    /**
     *
     * @return
     */
    public int getAmountInfo() {
        int aminfo = 0;
        float[][] inf = (float[][]) this.getAttribute("infi");
        for (float[] inf1 : inf) {
            for (int j = 0; j < inf[0].length; j++) {
                if (inf1[j] != -1) {
                    aminfo++;
                }
            }
        }
        return aminfo;
    }

    /**
     * @return the roundsWithoutNewInfo
     */
    public int getRoundsWithoutNewInfo() {
        return roundsWithoutNewInfo;
    }

    /**
     * @param infDelta the roundsWithoutNewInfo to set
     */
    public void setRoundsWithoutNewInfo(int infDelta) {
        this.roundsWithoutNewInfo = infDelta;
    }

    /**
     */
    public void addRoundsWithoutNewInfo() {
        this.roundsWithoutNewInfo++;
    }

    /**
     * @return the exploredTerrain
     */
    public int getExploredTerrain() {
        return exploredTerrain;
    }

    /**
     * @param exploredTerrain the exploredTerrain to set
     */
    public void setExploredTerrain(int exploredTerrain) {
        this.exploredTerrain = exploredTerrain;
    }

    /**
     */
    public void incExploredTerrain() {
        exploredTerrain++;
    }

    /**
     * @return the infi
     */
    public Hashtable getInfi() {
        return infi;
    }

    /**
     * @param infi the infi to set
     */
    public void setInfi(Hashtable infi) {
        this.infi = infi;
    }

    /**
     * @return the minx
     */
    public int getMinx() {
        return minx;
    }

    /**
     * @param minx the minx to set
     */
    public void setMinx(int minx) {
        this.minx = minx;
    }

    /**
     * @return the miny
     */
    public int getMiny() {
        return miny;
    }

    /**
     * @param miny the miny to set
     */
    public void setMiny(int miny) {
        this.miny = miny;
    }

    /**
     * @return the maxx
     */
    public int getMaxx() {
        return maxx;
    }

    /**
     * @param maxx the maxx to set
     */
    public void setMaxx(int maxx) {
        this.maxx = maxx;
    }

    /**
     * @return the maxy
     */
    public int getMaxy() {
        return maxy;
    }

    /**
     * @param maxy the maxy to set
     */
    public void setMaxy(int maxy) {
        this.maxy = maxy;
    }

}
