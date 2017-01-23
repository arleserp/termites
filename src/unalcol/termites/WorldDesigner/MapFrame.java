/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.WorldDesigner;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static unalcol.termites.Runnable.AppMain.graph;

/**
 *
 * @author Arles Rodriguez <arles.rodriguez@gmail.com>
 */
public class MapFrame extends Frame implements ActionListener {

    private Graphics buffer = null;
    private int[][] map;
    private Rectangle r = new Rectangle(0, 0, 0, 0);
    private Image image;

    MapFrame(int w, int h) {
        initMap(w, h);
        int width = w * 4 + 8;
        int height = h * 4 + 48;
        setBounds(50, 50, width, height);
        MenuBar mb = new MenuBar();
        setMenuBar(mb);
        // Define File menu and with Exit menu item
        Menu fileMenu = new Menu("File");
        mb.add(fileMenu);
        MenuItem exitMenuItem = new MenuItem("Exit");
        fileMenu.add(exitMenuItem);
        exitMenuItem.addActionListener(this);

        setLocation(10, 255);
        setVisible(true);

    }

    /**
     *
     * @param w
     * @param h
     */
    public void update(int w, int h) {
        if (r.width != bounds().width || r.height != bounds().height) {
            image = createImage(bounds().width, bounds().height);
            r = bounds();
            System.out.println("rh:" + r.height + "rw:" + r.width);
        }
       
        render(getGraphics(), w, h);
        //getGraphics().drawImage(image, w, h, this);
        
        revalidate();
        repaint();
    }

    /**
     *
     * @param w
     * @param h
     */
    public void initMap(int w, int h) {
        map = new int[w][h];

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                map[i][j] = 0;
            }
        }
    }

    /**
     *
     * @param g
     * @param w
     * @param h
     */
    public void render(Graphics g, int w, int h) {

        float xr = r.width / w;
        float yr = r.height / h;

        System.out.println("rh:" + r.height + "rw:" + r.width);

        Color phColor;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int tx = (int) (x * xr);
                int ty = (int) (y * yr);
                System.out.println("entra");
                /*switch (x%2) {
                 case 0:
                 phColor = new Color(176, 196, 222);
                 g.setColor(phColor);
                 g.fillRect(tx, ty, (int) xr, (int) yr);
                 break;
                 case 1:*/
                g.setColor(Color.BLACK);
                g.fillRect(tx, ty, (int) xr, (int) yr);
                /*break;
                 default:
                 System.out.println("je");
                 break;
                 }*/

            }
        }
    }

    /**
     *
     * @param evt
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() instanceof MenuItem) {
            String menuLabel = ((MenuItem) evt.getSource()).getLabel();
            if (menuLabel.equals("Exit")) {
                // close application, when exit is selected
                dispose();
                System.exit(0);
            }
        }
    }

}
