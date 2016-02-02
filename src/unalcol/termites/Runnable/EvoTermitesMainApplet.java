 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.Runnable;

import java.awt.BorderLayout;
import java.awt.Panel;

/**
 * very basic swarm applet
 */
public class EvoTermitesMainApplet extends java.applet.Applet implements Runnable {

    Thread t = null;
    WorldCanvas bc = null;
    int popul;
    float popS;
    private int failByTermite;
    private int vt;
    private int width; 
    private int height;
    
    EvoTermitesMainApplet(int population, float popSick, int failuresByTermite, int threshold, int w, int h) {
        this.popul = population;
        this.popS = popSick;
        this.failByTermite = failuresByTermite;
        this.vt = threshold;
        this.width = w;
        this.height = h;
    }

    public float getPopS() {
        return popS;
    }

    public int getPopul() {
        return popul;
    }

    public void init() {
        setLayout(new BorderLayout());
        bc = new WorldCanvas(this);

        Panel p = new Panel();
        //p.add(new Button("simulate"));
        //p.add(new Button("Hello World!"));

        add("Center", bc);
        //add("South",p);
        show();
    }

    @Override
    public void start() {
        if (t == null) {
            t = new Thread(this);
            t.start();
        }
    }

    @Override
    public void stop() {
        if (t != null) {
            t.stop();
            t = null;
        }
    }

    public void run() {
        try {
            while (true) {
                t.sleep(30);
                bc.repaint();
                repaint();
            }
        } catch (InterruptedException e) {

        }
    }

    /**
     * @return the failByTermite
     */
    public int getFailByTermite() {
        return failByTermite;
    }

    /**
     * @param failByTermite the failByTermite to set
     */
    public void setFailByTermite(int failByTermite) {
        this.failByTermite = failByTermite;
    }

    /**
     * @return the vt
     */
    public int getVt() {
        return vt;
    }

    /**
     * @param vt the vt to set
     */
    public void setVt(int vt) {
        this.vt = vt;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }
}

