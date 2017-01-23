/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.World;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import unalcol.agents.Action;
import unalcol.agents.Agent;
import unalcol.agents.Percept;
import unalcol.agents.simulate.Environment;
import unalcol.termites.Runnable.AppMain;
import unalcol.termites.reports.DataCollectedLatexConsolidatorSASOMessagesSend1;
import unalcol.types.collection.vector.Vector;

/**
 * Defines the environment in which agents will act It can be defined as
 * toroidal or not given some parameters and is a bi-dimensional space of states
 * with a size width x height
 *
 * @author Arles Rodriguez <arles.rodriguez@gmail.com>
 */
public abstract class World extends Environment {

    private static boolean Finished;
    private static boolean isCalculating = false;

    /**
     *
     */
    public int wallsnumber;
    private Hashtable<Integer, String> failAgentsInformation;

    /**
     * @return the isFinished
     */
    public boolean isFinished() {
        return Finished;
    }

    /**
     * @param aIsFinished the isFinished to set
     */
    public void setFinished(boolean aIsFinished) {
        Finished = aIsFinished;
    }

    /**
     * width of the world
     */
    public int width;

    /**
     * height of world
     */
    public int height;

    /**
     * Structure that store all the world definition
     */
    public StateProps[][] states;
    int seekers = 0;
    int carriers = 0;

    /**
     * Used to estimate the world age
     */
    public int age;

    /**
     * Number of agents that failed
     */
    public static int agentsDie = 0;
    private int roundGetInfo = -1;
    private static int idBest = -1;

    /**
     *
     */
    public int amountGlobalInfo = 0;

    /**
     * Creates a world given one agent
     *
     * @param agent
     */
    public World(Agent agent) {
        super(agent);
        age = 0;
        wallsnumber = 0;
    }

    /**
     * Creates a world given a vector of agents and creates the execution thread
     * of each agent
     *
     * @param _agents a structure that contains the agents
     * @param w width
     * @param h height
     */
    public World(Vector<Agent> _agents, int w, int h) {
        super(_agents);
        width = w;
        height = h;
        age = 0;
        wallsnumber = 0;
        failAgentsInformation = new Hashtable<>();
    }

    /**
     * Function used to calculate the intersection of all the information that
     * agent have collected in a determined time.
     */
    public void calculateGlobalInfo() {
        if (!isCalculating) {
            isCalculating = true;
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    states[i][j].globalInfo = false;
                }
            }
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    for (int k = 0; k < this.getAgents().size(); k++) {
                        Termite t = (Termite) this.getAgent(k);
                        if (t.status != Action.DIE) {
                            String loc = i + "-" + j;
                            if (((Hashtable) t.getAttribute("inf_i")).containsKey(loc)) {
                                states[i][j].globalInfo = true;
                                break;
                            }
                        }
                    }
                }
            }
            isCalculating = false;
        } else {
            System.out.println("entra!");
        }
    }

    /**
     * obtains information of a determined location
     *
     * @param x x is associated with width
     * @param y y is associated with height
     * @return
     */
    public StateProps getVal(int x, int y) {
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

    /**
     *
     * @return representation of the world as a matrix of states
     */
    public StateProps[][] getStates() {
        return this.states;
    }

    /**
     *
     * @return number of seekers in a determined world
     */
    public int getSeekers() {
        return seekers; //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @return number of carriers in a determined world
     */
    public int getCarriers() {
        return carriers; //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @return age of the world
     */
    public int getAge() {
        return age;
    }

    /**
     * Set an age to the world
     *
     * @param age the age to set
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * increases age of the world
     */
    public void increaseAge() {
        synchronized (World.class) {
            this.age++;
        }
    }

    /**
     * Calculates and updates the amount of seekers and carriers in a determined
     * world.
     *
     */
    public void updateSandC() {
        seekers = 0;
        carriers = 0;
        int average = 0;
        int agentslive = 0;
        for (int k = 0; k < this.getAgents().size(); k++) {

            if ((this.getAgent(k)).status != Action.DIE) {
                if (((Termite) this.getAgent(k)).getStatus() == Termite.SEEKING) {
                    seekers++;
                } else if (((Termite) this.getAgent(k)).getStatus() == Termite.CARRYING) {
                    carriers++;
                }
                average += ((Termite) this.getAgent(k)).getRound();
                agentslive++;
            }

        }

        if (agentslive != 0) {
            average /= agentslive;
            //System.out.println("age:" + average);
            this.setAge(average);
        }
    }

    /**
     *
     */
    public void nObservers() {
        if (!isFinished()) {
            setChanged();
            notifyObservers();
        }
    }

    /**
     * @return the number of agents with failures
     */
    public int getAgentsDie() {
        synchronized (World.class) {
            return agentsDie;
        }
    }

    /**
     * @param agentsDie set the number of agents with failures
     */
    public void setAgentsDie(int agentsDie) {
        World.agentsDie = agentsDie;
    }

    /**
     * increases the number of agents with failures
     */
    public void increaseAgentsDie() {
        synchronized (World.class) {
            World.agentsDie++;
        }
    }

    /**
     * @return obtains the round number of a determined world
     */
    public int getRoundGetInfo() {
        return roundGetInfo;
    }

    /**
     * Set a round number for the world
     *
     * @param roundGetInfo the roundGetInfo to set
     */
    public void setRoundGetInfo(int roundGetInfo) {
        synchronized (World.class) {
            this.roundGetInfo = roundGetInfo;
        }
    }

    /**
     * return the id of the agent who collects all the information first
     *
     * @return the idBest
     */
    public int getIdBest() {
        return idBest;
    }

    /**
     * sets the id of the best agent
     *
     * @param idBest the idBest to set
     */
    public void setIdBest(int idBest) {
        World.idBest = idBest;
    }

    /**
     * Obtains the id of the best agent
     *
     * @return
     */
    public Termite getBest() {
        if (this.getIdBest() != -1) {
            return (Termite) this.getAgent(this.getIdBest());
        } else {
            return (Termite) this.getAgent(calculateBest());
        }
    }

    /**
     * Calculates the agent with the biggest amount of information
     *
     * @return
     */
    public int calculateBest() {
        int n = this.agents.size();
        int id = 0;
        int aminfo = ((Termite) this.getAgent(0)).getAmountInfo();
        int tinfo;
        Termite t;
        for (int i = 1; i < n; i++) {
            t = ((Termite) this.getAgent(i));
            tinfo = t.getAmountInfo();
            if (tinfo > aminfo) {
                id = t.getId();
                aminfo = t.getAmountInfo();
            }
        }
        return id;
    }

    /**
     * Moves an agent in a direction given by direction for a not toroidal world
     *
     * @param piece
     * @param direction
     * @return true if it is possible to move the agent otherwise false in case
     * of collisions
     */
    protected boolean updatePiecesInWorldNotToroidal(Termite piece, int direction) {
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
            if (piece.getY() > 0) {
                piece.setY(piece.getY() - 1);
            }
        }
        if (direction == Directions.DOWN) {
            if (piece.getY() < this.height - 1) {
                piece.setY(piece.getY() + 1);
            }
        }
        if (direction == Directions.LEFT) {
            if (piece.getX() > 0) {
                piece.setX(piece.getX() - 1);
            }
        }
        if (direction == Directions.RIGHT) {
            if (piece.getX() < this.width - 1) {
                piece.setX(piece.getX() + 1);
            }
        }

        if (direction == Directions.DOWNLEFT) {
            if (piece.getX() > 0 && piece.getY() < this.height - 1) {
                piece.setX(piece.getX() - 1);
                piece.setY(piece.getY() + 1);
            }
        }

        if (direction == Directions.DOWNRIGHT) {
            if (piece.getX() < this.width - 1 && piece.getY() < this.height - 1) {
                piece.setX(piece.getX() + 1);
                piece.setY(piece.getY() + 1);
            }
        }
        if (direction == Directions.UPLEFT) {
            if (piece.getX() > 0 && piece.getY() > 0) {
                piece.setX(piece.getX() - 1);
                piece.setY(piece.getY() - 1);
            }
        }
        if (direction == Directions.UPRIGHT) {
            if (piece.getX() < this.width - 1 && piece.getY() > 0) {
                piece.setX(piece.getX() + 1);
                piece.setY(piece.getY() - 1);
            }
        }
        /*if (piece.getY() < 0) {
         if (piece.getY() > 0) {
         piece.setY(piece.getY() - 1);
         }
         }
         if (piece.getX() < 0) {
         if (piece.getX() > 0) {
         piece.setX(piece.getX() - 1);
         }
         }*/
        return true;
    }

    /**
     * evaluate collisions among agents
     *
     * @param piece
     * @param direction
     * @return
     */
    protected boolean hasCollisions(Termite piece, int direction) {
        boolean[] neighbors = getcollidePercept(piece);
        return (neighbors[direction]);
    }

    /**
     * moves the agent in a not toroidal world
     *
     * @param piece the agent to move
     * @param direction direction of movement
     * @return
     */
    protected boolean updatePiecesInWorld(Termite piece, int direction) {
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
     * Senses the walls of a determined world
     *
     * @param t the agent to move
     * @return true if there is a wall in the Moore neighbourhood of agent t
     */
    protected boolean getProximitySensor(Agent t) {
        Termite piece = (Termite) t;
        int[] xs = {1, 1, 0, -1, -1, -1, 0, 1};
        int[] ys = {0, 1, 1, 1, 0, -1, -1, -1};
        //StateProps[] vals = new StateProps[xs.length];
        boolean[] agentPerceptVector = new boolean[xs.length];
        for (int j = 0; j < agentPerceptVector.length; j++) {
            if (piece.getX() + xs[j] < 0 || piece.getY() + ys[j] < 0 || piece.getX() + xs[j] == width || piece.getY() + ys[j] == height || (getVal(piece.getX() + xs[j], piece.getY() + ys[j]).type == StateProps.WALL)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets data in current location of an agent it is call temperature for
     * historic reasons, it will be changed.
     *
     * @param agent
     * @return current data in location of agent
     */
    protected float getTemperaturePercept(Agent agent) {
        Termite t = (Termite) agent;
        return this.getVal(t.getX(), t.getY()).temp;
    }

    /**
     * Get pheromone percept in a Moore neighboorhood with center in the agent
     * location
     *
     * @param agent
     * @return float array with pheromone values in vicinity
     */
    protected float[] getPheromonePercept(Agent agent) {
        Termite piece = (Termite) agent;
        //get the vecinity
        int[] xs = {1, 1, 0, -1, -1, -1, 0, 1};
        int[] ys = {0, 1, 1, 1, 0, -1, -1, -1};

        float[] pheromone = new float[xs.length];
        for (int j = 0; j < pheromone.length; j++) {
            if (AppMain.walls.equals("wallsoff")) {
                pheromone[j] = this.getVal(piece.getX() + xs[j], piece.getY() + ys[j]).pherovalue;
            } else {
                if (piece.getX() + xs[j] < 0 || piece.getY() + ys[j] < 0 || piece.getX() + xs[j] == width || piece.getY() + ys[j] == height) {
                    pheromone[j] = -1;
                } else {
                    pheromone[j] = this.getVal(piece.getX() + xs[j], piece.getY() + ys[j]).pherovalue;
                }
            }
        }
        return pheromone;
    }

    /**
     * Obtain social status of agent
     *
     * @param agent
     * @return return 0 if agent is seeker, 1 if agent is carrier;
     */
    protected int getSeekingPercept(Agent agent) {
        Termite piece = (Termite) agent;
        return piece.getStatus();
    }

    /**
     * Returns if there are other agents in current locations
     *
     * @param agent
     * @return an array of boolean values indicating the presence of agents in
     * an agent's Moore neighbourhood
     */
    protected boolean[] getneiborTermitePercept(Agent agent) {
        Termite piece = (Termite) agent;

        //get the vicinity
        int[] xs = {1, 1, 0, -1, -1, -1, 0, 1, 0};
        int[] ys = {0, 1, 1, 1, 0, -1, -1, -1, 0};
        //StateProps[] vals = new StateProps[xs.length];
        boolean[] agentPerceptVector = new boolean[xs.length];

        //if we are in home we are in the target
        if (this.getVal(piece.getX(), piece.getY()).isHome || this.getVal(piece.getX(), piece.getY()).isTarget) {
            for (int j = 0; j < agentPerceptVector.length; j++) {
                agentPerceptVector[j] = false;
            }
        }
        for (int j = 0; j < agentPerceptVector.length; j++) {
            agentPerceptVector[j] = (this.getVal(piece.getX() + xs[j], piece.getY() + ys[j]).type == StateProps.TERMITE);
        }
        return agentPerceptVector;
    }

    /**
     *
     * @param agent
     * @return
     */
    public boolean[] getProximityDirSensorPercept(Agent agent) {
        Termite piece = (Termite) agent;

        //Der|downDer|Down|DownIzq|Izq|UpIzq|Up|UpDer|nothing
        //get the vicinity
        int[] xs = {1, 1, 0, -1, -1, -1, 0, 1, 0};
        int[] ys = {0, 1, 1, 1, 0, -1, -1, -1, 0};
        //StateProps[] vals = new StateProps[xs.length];
        boolean[] agentPerceptVector = new boolean[xs.length];

        for (int j = 0; j < agentPerceptVector.length; j++) {
            agentPerceptVector[j] = (piece.getX() + xs[j] < 0 || piece.getY() + ys[j] < 0 || piece.getX() + xs[j] == width || piece.getY() + ys[j] == height || (getVal(piece.getX() + xs[j], piece.getY() + ys[j]).type == StateProps.WALL));
        }
        return agentPerceptVector;
    }

    /**
     * This is used for stablish communication among agents returns the id's of
     * the first agent in the agent's vicinity
     *
     * @param agent
     * @return string with the id of the first agent found in the agent's
     * vicinity
     */
    protected String getIdNeighborPercept(Agent agent) {
        Termite t = (Termite) agent;
        //get the vecinity
        int[] xs = {1, 1, 0, -1, -1, -1, 0, 1};
        int[] ys = {0, 1, 1, 1, 0, -1, -1, -1};
        boolean[] agentPerceptVector = new boolean[xs.length];

        for (int j = 0; j < agentPerceptVector.length; j++) {
            int n = agents.size();
            int nposx = t.getX() + xs[j];
            int nposy = t.getY() + ys[j];

            for (int i = 0; i < n; i++) {
                Termite l = (Termite) agents.get(i);
                if (l.getX() == nposx && l.getY() == nposy) {
                    return (String) l.getAttribute("ID");
                }
            }

        }
        return null;
    }

    /**
     * Returns array of boolean values indicating the presence of another agent
     *
     * @param piece
     * @return array of boolean values. true values in case of collide another
     * agent, false otherwise.
     */
    protected boolean[] getcollidePercept(Termite piece) {
        //Der|downDer|Down|DownIzq|Izq|UpIzq|Up|UpDer|hello
        //get the vecinity
        int[] xs = {1, 1, 0, -1, -1, -1, 0, 1};
        int[] ys = {0, 1, 1, 1, 0, -1, -1, -1};
        //StateProps[] vals = new StateProps[xs.length];
        boolean[] agentPerceptVector = new boolean[xs.length];
        for (int j = 0; j < agentPerceptVector.length; j++) {

            //if (this.getVal(piece.getX() + xs[j], piece.getY() + ys[j]).isHome || this.getVal(piece.getX() + xs[j], piece.getY() + ys[j]).type != StateProps.LEADER) {
            //     agentPerceptVector[j] = false;
            // } else {
            agentPerceptVector[j] = ((this.getVal(piece.getX() + xs[j], piece.getY() + ys[j]).type == StateProps.TERMITE) || (this.getVal(piece.getX() + xs[j], piece.getY() + ys[j]).type == StateProps.WALL));
            // }
        }
        return agentPerceptVector;
    }

    /**
     *
     * @return
     */
    public int getAmountGlobalInfo() {
        amountGlobalInfo = 0;
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {

                StateProps box = this.getVal(x, y);
                if (box.globalInfo) {
                    this.amountGlobalInfo++;
                }
            }
        }
        return amountGlobalInfo;
    }

    /**
     *
     */
    public void evaporatePheromone() {
        //System.out.println("evap");
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                StateProps box = getVal(i, j);
                if (box.pherovalue > 0) {
                    box.pherovalue -= AppMain.evap * box.pherovalue;
                }
            }
        }
    }

    /**
     *
     * @return
     */
    public int calculateWallsNumber() {
        //System.out.println("evap");
        int wallsNumber = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                StateProps box = getVal(i, j);
                if (box.type == StateProps.WALL) {
                    wallsNumber++;
                }
            }
        }
        return wallsNumber;
    }

    /**
     *
     * @param x
     * @param y
     * @param fx
     * @param fy
     */
    public void createWallLine(int x, int y, int fx, int fy) {
        if (x == fx) {
            for (; y <= fy; y++) {
                this.getVal(x, y).type = StateProps.WALL;
                this.getVal(x, y).value = (float) 0.0;
            }
        } else if (y == fy) {
            for (; x <= fx; x++) {
                this.getVal(x, y).type = StateProps.WALL;
                this.getVal(x, y).value = (float) 0.0;
            }
        } else {
            for (; y < fy; y++) {
                for (; x <= fx; x++) {
                    if (x == y) {
                        this.getVal(x, y).type = StateProps.WALL;
                        this.getVal(x, y).value = (float) -1.0;
                    }
                }
            }
        }
    }

    /**
     * Function that creates a maze by now is just one so it is static
     *
     */
    /*    public void createMazeWorld() {
     createWallLine((int) (0.2 * height), (int) (0.2 * width), (int) (0.7 * height), (int) (0.2 * width));
     createWallLine((int) (0.2 * height), (int) (0.2 * width), (int) (0.2 * height), (int) (0.8 * width));
     createWallLine((int) (0.2 * height), (int) (0.8 * width), (int) (0.7 * height), (int) (0.8 * width));
     createWallLine((int) (0.5 * height), 0, (int) (0.5 * height), (int) (0.2 * width));
     createWallLine((int) (0.7 * height), (int) (0.2 * width), (int) (0.7 * height), (int) (0.45 * width));
     createWallLine((int) (0.7 * height), (int) (0.55 * width), (int) (0.7 * height), (int) (0.8 * width));
     createWallLine((int) (0.8 * height), (int) (0.2 * width), (int) (0.8 * height), (int) (0.45 * width));
     createWallLine((int) (0.8 * height), (int) (0.55 * width), (int) (0.8 * height), (int) (width) - 1);
     wallsnumber = calculateWallsNumber();
     }
     */
    public void createMazeWorld() {
        Scanner sc = null;
        try {
            File file = new File(AppMain.maze + ".csv");
            sc = new Scanner(file);

        } catch (FileNotFoundException ex) {
            createWallLine((int) (0.2 * height), (int) (0.2 * width), (int) (0.7 * height), (int) (0.2 * width));
            createWallLine((int) (0.2 * height), (int) (0.2 * width), (int) (0.2 * height), (int) (0.8 * width));
            createWallLine((int) (0.2 * height), (int) (0.8 * width), (int) (0.7 * height), (int) (0.8 * width));
            createWallLine((int) (0.5 * height), 0, (int) (0.5 * height), (int) (0.2 * width));
            createWallLine((int) (0.7 * height), (int) (0.2 * width), (int) (0.7 * height), (int) (0.45 * width));
            createWallLine((int) (0.7 * height), (int) (0.55 * width), (int) (0.7 * height), (int) (0.8 * width));
            createWallLine((int) (0.8 * height), (int) (0.2 * width), (int) (0.8 * height), (int) (0.45 * width));
            createWallLine((int) (0.8 * height), (int) (0.55 * width), (int) (0.8 * height), (int) (width) - 1);
            Logger.getLogger(World.class.getName()).log(Level.WARNING, "Using Default J. Beal Maze", ex);
            return;
        }

        String[] data = null;
        while (sc.hasNext()) {
            String line = sc.nextLine();
            //System.out.println("line:" + line);
            data = line.split(",");
            Double Xo = Double.valueOf(data[0]);
            Double Yo = Double.valueOf(data[1]);
            Double Xi = Double.valueOf(data[2]);
            Double Yi = Double.valueOf(data[3]);
            
            int twi = (Yi==1)?(width-1):width; 
            int thi = (Xi==1)?(height-1):height; 
            createWallLine((int) (Xo * height), (int) (Yo * width), (int) (Xi * thi), (int) (Yi*twi));
        }

        wallsnumber = calculateWallsNumber();
    }

    /**
     *
     * @param id
     * @param iter
     * @param info
     */
    public void setLastAgentFail(int id, int iter, int info) {
        String inf = id + "," + iter + "," + info;
        getFailAgentsInformation().put(id, inf);
    }

    /**
     * @return the failAgentsInformation
     */
    public Hashtable<Integer, String> getFailAgentsInformation() {
        return failAgentsInformation;
    }

    /**
     * @param failAgentsInformation the failAgentsInformation to set
     */
    public void setFailAgentsInformation(Hashtable<Integer, String> failAgentsInformation) {
        this.failAgentsInformation = failAgentsInformation;
    }

    /**
     * Agents sense from the environment so World provide the perceptions of an
     * agent in a form of perception
     *
     * @param agent
     * @return perception of Agent agent
     */
    public Percept sense(Agent agent) {
        Percept p = new Percept();

        float[] pheromones = getPheromonePercept(agent);
        p.setAttribute("pheromone", pheromones);

        Integer seekingStatus = getSeekingPercept(agent);
        p.setAttribute("seekingStatus", seekingStatus);

        boolean[] neighborTermite = getneiborTermitePercept(agent);
        p.setAttribute("neighborTermite", neighborTermite);

        String leaderPercept = getIdNeighborPercept(agent);
        if (leaderPercept != null) {
            p.setAttribute("idneighbor", leaderPercept);
        }

        double tempPercept = getTemperaturePercept(agent);
        p.setAttribute("tempPercept", tempPercept);

        boolean proximitySensor = getProximitySensor(agent);
        p.setAttribute("proximitySensor", proximitySensor);

        boolean[] proximityDirSensor = getProximityDirSensorPercept(agent);
        p.setAttribute("proximityDirSensor", proximityDirSensor);

        return p;
    }

    /**
     *
     * @param aminfo
     * @return
     */
    public boolean completeInfo(int aminfo) {
        return aminfo >= (width * height - wallsnumber) * AppMain.stopSuccessRate;
    }
}
