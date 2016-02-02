/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.WorldDesigner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class TestGrid01 {

    public static void main(String[] args) {
        new TestGrid01();
    }

    public TestGrid01() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                }

                JFrame frame = new JFrame("Testing");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout());
                frame.add(new TestPane());
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    public class TestPane extends JPanel {

        private int columnCount = 50;
        private int rowCount = 50;
        private List<Rectangle> cells;
        private Point selectedCell;

        private ArrayList<Point> selectedCells;

        public TestPane() {
            cells = new ArrayList<>(columnCount * rowCount);
            selectedCells = new ArrayList<>();
            MouseAdapter mouseHandler;
            mouseHandler = new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    Point point = e.getPoint();

                    int width = getWidth();
                    int height = getHeight();

                    int cellWidth = width / columnCount;
                    int cellHeight = height / rowCount;

                    int column = e.getX() / cellWidth;
                    int row = e.getY() / cellHeight;
                    System.out.println("click" + column + " row:" + row);
                    
                    selectedCell = new Point(column, row);
                    repaint();

                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    System.out.println("click");
                    Point point = e.getPoint();

                    int width = getWidth();
                    int height = getHeight();

                    int cellWidth = width / columnCount;
                    int cellHeight = height / rowCount;

                    int column = e.getX() / cellWidth;
                    int row = e.getY() / cellHeight;

                    Point selCell = new Point(column, row);
                    
                    //if(!selectedCells.contains(selCell))
                    //{   
                        System.out.println("add");
                        selectedCells.add(selCell);
                    //}else{
                    //    System.out.println("del");
                    //    selectedCells.remove(selCell);
                    //}
                    System.out.println("click" + column + " row:" + row);
                    repaint();

                }

                @Override
                public void mousePressed(MouseEvent e) {
                    System.out.println("click");
                    Point point = e.getPoint();

                    int width = getWidth();
                    int height = getHeight();

                    int cellWidth = width / columnCount;
                    int cellHeight = height / rowCount;

                    int column = e.getX() / cellWidth;
                    int row = e.getY() / cellHeight;

                    selectedCell = new Point(column, row);
                    selectedCells.add(selectedCell);
                    System.out.println("click" + column + " row:" + row);
                    repaint();

                }

            };
            addMouseMotionListener(mouseHandler);
            addMouseListener(mouseHandler);

        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(200, 200);
        }

        @Override
        public void invalidate() {
            cells.clear();
            selectedCell = null;
            super.invalidate();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();

            int width = getWidth();
            int height = getHeight();

            int cellWidth = width / columnCount;
            int cellHeight = height / rowCount;

            int xOffset = (width - (columnCount * cellWidth)) / 2;
            int yOffset = (height - (rowCount * cellHeight)) / 2;

            if (cells.isEmpty()) {
                for (int row = 0; row < rowCount; row++) {
                    for (int col = 0; col < columnCount; col++) {
                        Rectangle cell = new Rectangle(
                                xOffset + (col * cellWidth),
                                yOffset + (row * cellHeight),
                                cellWidth,
                                cellHeight);
                        cells.add(cell);
                    }
                }
            }

            if (selectedCell != null) {

                int index = selectedCell.x + (selectedCell.y * columnCount);
                Rectangle cell = cells.get(index);
                g2d.setColor(Color.BLUE);
                g2d.fill(cell);

            }

            g2d.setColor(Color.GRAY);

            for (Rectangle cell : cells) {
                g2d.draw(cell);
            }

            for (Point p : selectedCells) {
                int index = p.x + (p.y * columnCount);
                Rectangle cell = cells.get(index);
                g2d.setColor(Color.BLUE);
                g2d.fill(cell);
            }

            g2d.dispose();
        }
    }
}
