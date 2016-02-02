/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.Runnable;

import java.util.ArrayList;
import java.util.Hashtable;
import unalcol.termites.World.Termite;
import unalcol.termites.World.World;
import unalcol.termites.distributed.NetworkMessageBuffer;
import unalcol.termites.reports.GraphicReportHealingObserver;
import unalcol.agents.Agent;
import unalcol.agents.AgentProgram;
import unalcol.random.RandomUtil;
import unalcol.termites.World.WorldLwphCLwEvapImpl;
import unalcol.termites.World.WorldTemperaturesOneStepLWOnePheromoneEvaporationImpl;
import unalcol.termites.World.WorldTemperaturesOneStepOnePheromoneHybridLWEvaporationImpl;
import unalcol.termites.World.WorldTemperaturesOneStepOnePheromoneHybridLWEvaporationImpl2;
import unalcol.types.collection.vector.Vector;

/**
 * Creates a simulation without graphic interface
 *
 * @author arles.rodriguez
 */
public class WorldThread implements Runnable {

    private World world;
    public boolean renderAnts = true;
    public boolean renderSeeking = true;
    public boolean renderCarrying = true;
    int modo = 0;
    GraphicReportHealingObserver greport;
    int executions = 0;
    int population = 100;
    float probFailure = (float) 0.1;
    int failuresByTermite = 1;
    Hashtable<String, Object> positions;
    int width;
    int height;

    /**
     * Creates a simulation without graphic interface
     *
     * @param pop
     * @param pf
     * @param width
     * @param height
     * @return
     */
    WorldThread(int pop, float pf, int width, int height) {
        population = pop;
        probFailure = pf;
        positions = new Hashtable<>();
        this.width = width;
        this.height = height;

    }

    /**
     * Obtain a free location in the world for locating agents
     *
     * @param width
     * @param height
     * @return a free position to locate an agent
     */
    public int[] getFreePosition(int width, int height) {
        int pos[] = new int[2];
        do {
            pos[0] = RandomUtil.nextInt(width);
            pos[1] = RandomUtil.nextInt(height);
        } while (positions.containsKey(pos[0] + "," + pos[1]));

        positions.put(pos[0] + "," + pos[1], pos);
        return pos;
    }

    /**
     * Obtains a central location for the world
     *
     * @param width
     * @param height
     * @return
     */
    /**
     * return the center location in a world
     *
     * @param width
     * @param height
     * @return array [width / 2, height / 2]
     */
    public int[] getCentralPosition(int width, int height) {
        int pos[] = new int[2];
        //do {
        pos[0] = width / 2;//RandomUtil.nextInt(width);
        pos[1] = height / 2;//RandomUtil.nextInt(height);
        //} while (positions.contains(pos));
        positions.put(pos[0] + "," + pos[1], pos);
        return pos;
    }

    /**
     *
     * Initializes simulation.
     */
    public void init() {
        Vector<Agent> termites = new Vector();
        int size_w;
        int size_h;

        size_w = this.width;
        size_h = this.height;

        System.out.println("fp" + probFailure);

        //report = new reportHealingProgram(population, probFailure, this);
        greport = new GraphicReportHealingObserver(probFailure);

        if (AppMain.maze.equals("mazeon")) {
            reserveMazeLocations(size_w, size_h);
        }

        //Creates "Agents"
        for (int i = 0; i < population; i++) {
            AgentProgram program = ProgramWorldFactory.createProgram(i, probFailure, failuresByTermite);

            Termite t = new Termite(program, i);
            t.setAttribute("ID", String.valueOf(i));

            //int pos[] = getCentralPosition(size_w, size_h);
            int pos[] = getFreePosition(size_w, size_h);
            t.setX(pos[0]);
            t.setY(pos[1]);

            t.setProgram(program);
            t.setAttribute("infi", new ArrayList<Integer>());
            t.setAttribute("inf_i", new Hashtable());

            NetworkMessageBuffer.getInstance().createBuffer((String) t.getAttribute("ID"));
            termites.add(t);
        }

        world = ProgramWorldFactory.createWorld(termites, size_w, size_h);
        greport.addObserver(world);
        System.out.println("w: " + world.width + "h: " + world.height);
        if (AppMain.maze.equals("mazeon")) {
            world.createMazeWorld();
        }
        world.run();
        executions++;

        world.updateSandC();
        world.calculateGlobalInfo();
        world.nObservers();

    }

    /**
     * Runs a simulation.
     *
     */
    @Override
    public void run() {
        //try {
        while (!world.isFinished()) {
                //Thread.sleep(30);

            world.updateSandC();
            world.calculateGlobalInfo();

            if (world.getAge() % 2 == 0 || world.getAgentsDie() == world.getAgents().size() || world.getRoundGetInfo() != -1) {
                world.nObservers();
            }

            if (world instanceof WorldTemperaturesOneStepLWOnePheromoneEvaporationImpl) {
                ((WorldTemperaturesOneStepLWOnePheromoneEvaporationImpl) world).evaporatePheromone();
            }

            if (world instanceof WorldTemperaturesOneStepOnePheromoneHybridLWEvaporationImpl) {
                ((WorldTemperaturesOneStepOnePheromoneHybridLWEvaporationImpl) world).evaporatePheromone();
            }

            if (world instanceof WorldTemperaturesOneStepOnePheromoneHybridLWEvaporationImpl2) {
                ((WorldTemperaturesOneStepOnePheromoneHybridLWEvaporationImpl2) world).evaporatePheromone();
            }

            if (world instanceof WorldLwphCLwEvapImpl) {
                ((WorldLwphCLwEvapImpl) world).evaporatePheromone();
            }
        }
        /*} catch (InterruptedException e) {
         System.out.println("interrupted!");
         }
         System.out.println("End WorldThread");*/
    }

    public void createWallLine(int x, int y, int fx, int fy) {
        if (x == fx) {
            for (; y <= fy; y++) {
                positions.put(x + "," + y, x);
            }
        } else if (y == fy) {
            for (; x <= fx; x++) {
                positions.put(x + "," + y, x);
            }
        } else {
            for (; y < fy; y++) {
                for (; x <= fx; x++) {
                    if (x == y) {
                        positions.put(x + "," + y, x);
                    }
                }
            }
        }
    }

    private void reserveMazeLocations(int size_w, int size_h) {
        createWallLine((int) (0.2 * size_h), (int) (0.2 * size_w), (int) (0.7 * size_h), (int) (0.2 * size_w));
        createWallLine((int) (0.2 * size_h), (int) (0.2 * size_w), (int) (0.2 * size_h), (int) (0.8 * size_w));
        createWallLine((int) (0.2 * size_h), (int) (0.8 * size_w), (int) (0.7 * size_h), (int) (0.8 * size_w));
        createWallLine((int) (0.5 * size_h), 0, (int) (0.5 * size_h), (int) (0.2 * size_w));
        createWallLine((int) (0.7 * size_h), (int) (0.2 * size_w), (int) (0.7 * size_h), (int) (0.45 * size_w));
        createWallLine((int) (0.7 * size_h), (int) (0.55 * size_w), (int) (0.7 * size_h), (int) (0.8 * size_w));
        createWallLine((int) (0.8 * size_h), 0, (int) (0.8 * size_h), (int) (0.45 * size_w));
        createWallLine((int) (0.8 * size_h), (int) (0.55 * size_w), (int) (0.8 * size_h), (int) (size_w));
        //System.out.println("posi" + positions.size());
    }
}
