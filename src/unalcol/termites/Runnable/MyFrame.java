package unalcol.termites.Runnable;

import java.awt.*;
import java.awt.event.*;

public class MyFrame extends Frame implements ActionListener {

    int population;
    float popSick;
    int failuresByTermite;
    int threshold;

    
    /*public MyFrame() { // constructor
        super("Termites!"); // define frame title
        // define Menubar
        MenuBar mb = new MenuBar();
        setMenuBar(mb);
        // Define File menu and with Exit menu item
        Menu fileMenu = new Menu("File");
        mb.add(fileMenu);
        MenuItem exitMenuItem = new MenuItem("Exit");
        fileMenu.add(exitMenuItem);
        exitMenuItem.addActionListener(this);
        // define the applet and add to the frame
        EvoTermitesMainApplet myApplet = new EvoTermitesMainApplet(100, (float)0.1, 1, 20);
        add(myApplet, BorderLayout.CENTER);
        
        // call applet's init method (since it is not
        // automatically called in a Java application)
        //setBounds(10, 10, 350, 200); // this time use a predefined frame size/position

        setVisible(true);
        myApplet.init();
        myApplet.start();
    } // end constructor*/

    public MyFrame(int pop, float popS, int FailuresbyTermite, int threshold, String title, int width, int height) {
        //super("Pop:" + pop + " FP:" + popS + " Failures Termite: " + FailuresbyTermite + " vt:" + threshold); // define frame title
        super(title);
        this.population = pop;
        this.popSick = popS;
        this.failuresByTermite = FailuresbyTermite;
        this.threshold = threshold;
        // define Menubar
        MenuBar mb = new MenuBar();
        setMenuBar(mb);
        // Define File menu and with Exit menu item
        Menu fileMenu = new Menu("File");
        mb.add(fileMenu);
        MenuItem exitMenuItem = new MenuItem("Exit");
        fileMenu.add(exitMenuItem);
        exitMenuItem.addActionListener(this);
        // define the applet and add to the frame
        System.out.println("fbt" + failuresByTermite);
        EvoTermitesMainApplet myApplet = new EvoTermitesMainApplet(population, popSick, failuresByTermite, threshold, width, height);
        add(myApplet, BorderLayout.CENTER);
        // call applet's init method (since it is not
        // automatically called in a Java application)
        //setBounds(10, 10, 350, 200); // this time use a predefined frame size/position
        setVisible(true);

        myApplet.init();
        myApplet.start();
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

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
