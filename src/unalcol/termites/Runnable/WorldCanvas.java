/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.Runnable;

import java.applet.Applet;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import unalcol.termites.World.StateProps;
import unalcol.termites.World.Termite;
import unalcol.termites.World.World;
import unalcol.termites.distributed.NetworkMessageBuffer;
import unalcol.termites.reports.GraphicReportHealingObserver;
import unalcol.agents.Agent;
import unalcol.agents.AgentProgram;
import unalcol.random.RandomUtil;
import unalcol.random.real.GaussianGenerator;
import static unalcol.termites.Runnable.AppMain.graph;
import unalcol.termites.World.WorldHybridLWSandCImpl;
import unalcol.termites.World.WorldHybridLwSandLwCImpl;
import unalcol.termites.World.WorldLwphCLwEvapImpl;
import unalcol.termites.World.WorldTemperaturesOneStepLWOnePheromoneEvaporationImpl;
import unalcol.termites.World.WorldTemperaturesOneStepOnePheromoneEvaporationMapImpl;
import unalcol.termites.World.WorldTemperaturesOneStepOnePheromoneHybridLWEvaporationImpl;
import unalcol.termites.World.WorldTemperaturesOneStepOnePheromoneHybridLWEvaporationImpl2;
import unalcol.termites.World.WorldTemperaturesTocOneStepLWOnePheromoneEvaporationImpl;
import unalcol.termites.stats.StatsTemperaturesImpl;
import unalcol.types.collection.vector.Vector;

/**
 * This class is a facade that creates the world and agents and render the
 * graphic interface by each iteration.
 *
 * @author arles.rodriguez
 */
public class WorldCanvas extends Canvas {

    private Applet parent = null;
    private Image image;
    private Image image2;
    private Graphics buffer = null;
    private Graphics buffer2 = null;
    private Rectangle r = new Rectangle(0, 0, 0, 0);
    private World world;

    /**
     *
     */
    public boolean renderAnts = true;

    /**
     *
     */
    public boolean renderSeeking = true;

    /**
     *
     */
    public boolean renderCarrying = true;
    int modo = 0;
    GraphicReportHealingObserver greport;
    int population = 100;
    float probFailure = (float) 0.1;
    int failuresByTermite = 1;
    int vt = 20;
    Hashtable<String, Object> positions;
    int width;
    int height;
    int lastWorldAge;

    Frame frame2;

    /**
     * Obtain a free location in the world for locating agents
     *
     * @param width
     * @param height
     * @param sc Scanner to load locations from file
     * @return a free position to locate an agent
     */
    public int[] getFreePosition(int width, int height, Scanner sc) {
        int pos[] = new int[2];
        GaussianGenerator gw = new GaussianGenerator(0.5, 0.05);
        GaussianGenerator gh = new GaussianGenerator(0.5, 0.05);

        do {
            if (AppMain.startCentralLocation.equals("CentralLocation")) {
                pos[0] = (int) (gw.next() * width);
                pos[1] = (int) (gh.next() * height);
            } else if (AppMain.startCentralLocation.equals("randomLocation")) {
                pos[0] = RandomUtil.nextInt(width);
                pos[1] = RandomUtil.nextInt(height);
            } else {
                String line = sc.nextLine();
                String[] data = line.split(",");
                pos[0] = Integer.valueOf(data[0]);
                pos[1] = Integer.valueOf(data[1]);
            }
        } while(positions.containsKey(pos[0] + "," + pos[1]));
        positions.put(pos[0] + "," + pos[1], pos);
        return pos;
    }

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
     * Creates Canvas
     *
     * @param p extends Applet by historic reasons.
     */
    public WorldCanvas(Applet p) {
        parent = p;
        population = ((EvoTermitesMainApplet) p).getPopul();
        probFailure = ((EvoTermitesMainApplet) p).getPopS();
        failuresByTermite = ((EvoTermitesMainApplet) p).getFailByTermite();
        vt = ((EvoTermitesMainApplet) p).getVt();
        positions = new Hashtable<>();
        this.width = p.getWidth();
        this.height = p.getHeight();
        System.out.println("crea frame");
        frame2 = new Frame("Global Info. Collected");
        frame2.setBounds(50, 50, width, height);
        frame2.setLocation(10, 255);
        frame2.setVisible(true);
        lastWorldAge = 0;
    }

    /**
     * it is call in each iteration, renders the graphic interface and call
     * routines in the world evaporates pheromone
     *
     * @param g
     */
    @Override
    public void update(Graphics g) {

        if (r.width != bounds().width || r.height != bounds().height) {
            image = parent.createImage(bounds().width, bounds().height);
            image2 = frame2.createImage(image.getWidth(parent), image.getHeight(parent));

            buffer = image.getGraphics();
            buffer2 = image2.getGraphics();
            r = bounds();
            System.out.println("rh:" + r.height + "rw:" + r.width);
            init();
        }

        if (graph.equals("graphson")) {
            //render(buffer);
            if (image != null) {
                g.drawImage(image, 0, 0, this);
            }
            //render(buffer2);
            frame2.getGraphics().drawImage(image2, 5, 30, frame2);
            render(buffer, buffer2);
        }

        world.updateSandC();
        world.calculateGlobalInfo();

        if (world.getAge() - lastWorldAge > 20 || world.getAgentsDie() == world.getAgents().size() || world.getRoundGetInfo() != -1) {
            //System.out.println("wa:" + world.getAge());
            world.nObservers();
            lastWorldAge = world.getAge();
        }
        //System.out.println("world.ge" + world.getAge());

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
        if (world instanceof WorldTemperaturesTocOneStepLWOnePheromoneEvaporationImpl) {
            ((WorldTemperaturesTocOneStepLWOnePheromoneEvaporationImpl) world).evaporatePheromone();
        }

        if (world instanceof WorldTemperaturesOneStepOnePheromoneEvaporationMapImpl) {
            ((WorldTemperaturesOneStepOnePheromoneEvaporationMapImpl) world).evaporatePheromone();
        }

        if (world instanceof WorldHybridLWSandCImpl) {
            ((WorldHybridLWSandCImpl) world).evaporatePheromone();
        }

        if (world instanceof WorldLwphCLwEvapImpl) {
            ((WorldLwphCLwEvapImpl) world).evaporatePheromone();
        }

        if (world instanceof WorldHybridLwSandLwCImpl) {
            ((WorldHybridLwSandLwCImpl) world).evaporatePheromone();
        }
    }

    /**
     * initialize agents and world
     */
    public void init() {
        Vector<Agent> termites = new Vector();
        int size_w;
        int size_h;
        Scanner sc = null;

        size_w = r.width / 4;
        size_h = r.height / 4;

        System.out.println("fp" + probFailure);

        //report = new reportHealingProgram(population, probFailure, this);
        greport = new GraphicReportHealingObserver(probFailure);

        if (!AppMain.maze.equals("mazeoff")) {
            reserveMazeLocations(size_w, size_h);
        }

        if (!AppMain.startCentralLocation.equals("CentralLocation") && !AppMain.startCentralLocation.equals("randomLocation")) {
            try {
                File file = new File(AppMain.startCentralLocation + ".csv");
                sc = new Scanner(file);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(World.class.getName()).log(Level.SEVERE, "Location file not found:" + AppMain.startCentralLocation + ".csv", ex);
            }
        }

        //Creates "Agents"
        for (int i = 0; i < population; i++) {
            AgentProgram program = ProgramWorldFactory.createProgram(i, probFailure, failuresByTermite);

            Termite t = new Termite(program, i);
            t.setAttribute("ID", String.valueOf(i));

            int pos[] = getFreePosition(size_w, size_h, sc);

            if (!AppMain.saveLocation.equals("nosaveLocation")) {
                addLocation(pos);
            }

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
        //System.out.println("a+"+AppMain.maze);
        if (!AppMain.maze.equals("mazeoff")) {
            world.createMazeWorld();
        }
        
              
        world.run();
        world.calculateGlobalInfo();
        world.updateSandC();
        world.nObservers();
    }

    /**
     * does the rendering (except for background)
     *
     * @param g
     */
    public void render(Graphics g) {

        float xr = r.width / world.width;
        float yr = r.height / world.height;
        Color phColor;
        for (int y = 0; y < world.height; y++) {
            for (int x = 0; x < world.width; x++) {
                int tx = (int) (x * xr);
                int ty = (int) (y * yr);
                StateProps box = world.getVal(x, y);

                if (modo == 0) {
                    if (box.type == StateProps.WALL) {
                        g.setColor(Color.BLACK);
                        g.fillRect(tx, ty, (int) xr, (int) yr);
                    } else {
                        switch ((int) box.value) {
                            case 0:
                                if (AppMain.showInfoIntersect.equals("on")) {
                                    if (!box.globalInfo) {
                                        phColor = new Color(176, 196, 222);

                                    } else {
                                        phColor = Color.ORANGE;
                                        //g.setColor(new Color(125, 125, (int) (10000 * (box.pherovalue)) % 255));
                                    }
                                    g.setColor(phColor);
                                } else {
                                    if (box.pherovalue == 0.5 || box.pherovalue == 0) {
                                        phColor = new Color(176, 196, 222);
                                        g.setColor(phColor);

                                    } else {
                                        phColor = new Color(255, 0, (int) (10000 * (box.pherovalue)) % 255);
                                        g.setColor(phColor);
                                        //g.setColor(new Color(125, 125, (int) (10000 * (box.pherovalue)) % 255));
                                    }
                                }
                                g.fillRect(tx, ty, (int) xr, (int) yr);
                                break;
                            case 1:
                                g.setColor(Color.white);
                                g.fillRect(tx, ty, (int) xr, (int) yr);
                                break;
                            case 2:
                                g.setColor(Color.RED);
                                g.fillRect(tx, ty, (int) xr, (int) yr);
                                break;
                            case 3:
                                g.setColor(Color.ORANGE);
                                g.fillRect(tx, ty, (int) xr, (int) yr);
                                break;
                            case 4:
                                g.setColor(Color.BLUE);
                                g.fillRect(tx, ty, (int) xr, (int) yr);
                                break;
                            case 5:
                                g.setColor(Color.cyan);
                                g.fillRect(tx, ty, (int) xr, (int) yr);
                                break;
                            case 6:
                                g.setColor(Color.WHITE);
                                g.fillRect(tx, ty, (int) xr, (int) yr);
                                break;
                            case 7:
                                g.setColor(Color.YELLOW);
                                g.fillRect(tx, ty, (int) xr, (int) yr);
                                break;
                            case 9:
                                g.setColor(Color.BLACK);
                                g.fillRect(tx, ty, (int) xr, (int) yr);
                                break;
                            default:
                                break;
                        }
                    }
                } else if (modo == 1) {
                    switch ((int) box.value) {
                        case 0:
                            g.setColor(Color.BLACK);
                            g.fillRect(tx, ty, (int) xr, (int) yr);
                            break;
                        case 1:
                            g.setColor(Color.GREEN);
                            g.fillRect(tx, ty, (int) xr, (int) yr);
                            break;
                        case 2:
                            g.setColor(Color.RED);
                            g.fillRect(tx, ty, (int) xr, (int) yr);
                            break;
                        case 3:
                            g.setColor(Color.ORANGE);
                            g.fillRect(tx, ty, (int) xr, (int) yr);
                            break;
                        case 4:
                            g.setColor(Color.BLUE);
                            g.fillRect(tx, ty, (int) xr, (int) yr);
                            break;
                        case 5:
                            g.setColor(Color.cyan);
                            g.fillRect(tx, ty, (int) xr, (int) yr);
                            break;
                        case 6:
                            g.setColor(Color.BLACK);
                            g.fillRect(tx, ty, (int) xr, (int) yr);
                            break;
                        case 7:
                            g.setColor(Color.YELLOW);
                            g.fillRect(tx, ty, (int) xr, (int) yr);
                            break;
                        case 9:
                            g.setColor(Color.BLACK);
                            g.fillRect(tx, ty, (int) xr, (int) yr);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        phColor = null;
    }

    /**
     *
     * @param g
     * @param g2
     */
    public void render(Graphics g, Graphics g2) {

        float xr = r.width / world.width;
        float yr = r.height / world.height;
        Color phColor;
        for (int y = 0; y < world.height; y++) {
            for (int x = 0; x < world.width; x++) {
                int tx = (int) (x * xr);
                int ty = (int) (y * yr);
                StateProps box = world.getVal(x, y);

                if (modo == 0) {
                    if (box.type == StateProps.WALL) {
                        g.setColor(Color.BLACK);
                        g.fillRect(tx, ty, (int) xr, (int) yr);
                    } else {
                        switch ((int) box.value) {
                            case 0:
                                //if (AppMain.showInfoIntersect.equals("on")) {
                                if (!box.globalInfo) {
                                    phColor = new Color(176, 196, 222);

                                } else {
                                    phColor = Color.ORANGE;
                                    //g.setColor(new Color(125, 125, (int) (10000 * (box.pherovalue)) % 255));
                                }
                                g2.setColor(phColor);
                                //} else {
                                if (box.pherovalue == 0.5 || box.pherovalue == 0) {
                                    phColor = new Color(176, 196, 222);
                                    g.setColor(phColor);

                                } else {
                                    phColor = new Color(255, 0, (int) (10000 * (box.pherovalue)) % 255);
                                    g.setColor(phColor);
                                    //g.setColor(new Color(125, 125, (int) (10000 * (box.pherovalue)) % 255));
                                }
                                //}
                                g2.fillRect(tx, ty, (int) xr, (int) yr);
                                g.fillRect(tx, ty, (int) xr, (int) yr);
                                break;
                            case 1:
                                g.setColor(Color.white);
                                g.fillRect(tx, ty, (int) xr, (int) yr);
                                break;
                            case 2:
                                g.setColor(Color.RED);
                                g.fillRect(tx, ty, (int) xr, (int) yr);
                                break;
                            case 3:
                                g.setColor(Color.ORANGE);
                                g.fillRect(tx, ty, (int) xr, (int) yr);
                                break;
                            case 4:
                                g.setColor(Color.BLUE);
                                g.fillRect(tx, ty, (int) xr, (int) yr);
                                break;
                            case 5:
                                g.setColor(Color.cyan);
                                g.fillRect(tx, ty, (int) xr, (int) yr);
                                break;
                            case 6:
                                g.setColor(Color.WHITE);
                                g.fillRect(tx, ty, (int) xr, (int) yr);
                                break;
                            case 7:
                                g.setColor(Color.YELLOW);
                                g.fillRect(tx, ty, (int) xr, (int) yr);
                                break;
                            case 9:
                                g.setColor(Color.BLACK);
                                g.fillRect(tx, ty, (int) xr, (int) yr);
                                break;
                            default:
                                break;
                        }
                    }
                } else if (modo == 1) {
                    switch ((int) box.value) {
                        case 0:
                            g.setColor(Color.BLACK);
                            g.fillRect(tx, ty, (int) xr, (int) yr);
                            break;
                        case 1:
                            g.setColor(Color.GREEN);
                            g.fillRect(tx, ty, (int) xr, (int) yr);
                            break;
                        case 2:
                            g.setColor(Color.RED);
                            g.fillRect(tx, ty, (int) xr, (int) yr);
                            break;
                        case 3:
                            g.setColor(Color.ORANGE);
                            g.fillRect(tx, ty, (int) xr, (int) yr);
                            break;
                        case 4:
                            g.setColor(Color.BLUE);
                            g.fillRect(tx, ty, (int) xr, (int) yr);
                            break;
                        case 5:
                            g.setColor(Color.cyan);
                            g.fillRect(tx, ty, (int) xr, (int) yr);
                            break;
                        case 6:
                            g.setColor(Color.BLACK);
                            g.fillRect(tx, ty, (int) xr, (int) yr);
                            break;
                        case 7:
                            g.setColor(Color.YELLOW);
                            g.fillRect(tx, ty, (int) xr, (int) yr);
                            break;
                        case 9:
                            g.setColor(Color.BLACK);
                            g.fillRect(tx, ty, (int) xr, (int) yr);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        phColor = null;
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
                positions.put(y + "," + x, x);
                positions.put(x + "," + y, x);
            }
        } else if (y == fy) {
            for (; x <= fx; x++) {
                positions.put(y + "," + x, x);
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
    }

    private void addLocation(int[] pos) {
        try {
            PrintWriter escribir;
            escribir = new PrintWriter(new BufferedWriter(new FileWriter(AppMain.saveLocation + ".csv", true)));
            escribir.println(pos[0] + "," + pos[1]);
            escribir.close();
        } catch (IOException ex) {
            Logger.getLogger(StatsTemperaturesImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
