package view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import model.Model;

public class Panel extends JPanel implements Runnable {

    private static final int OG_CELL_SIZE = 16;
    static final int SCALE_FACTOR = 4;

    public static final int CELL_SIZE = OG_CELL_SIZE * SCALE_FACTOR;

    private static final int PANEL_SIZE = CELL_SIZE * Model.GRID_SIZE;

    public static final int FPS = 32;
    public static final int APS = 1;

    Model model;

    private static final CellSprite[][] GRID_SPRITE = new CellSprite[Model.GRID_SIZE][Model.GRID_SIZE];

    RobotSprite robotSprite;

    Thread thread;

    public Panel(Model model) {

        this.model = model;

        for (int row = 0; row < Model.GRID.length; row++) {
            for (int col = 0; col < Model.GRID[row].length; col++) {

                GRID_SPRITE[row][col] = new CellSprite(Model.GRID[row][col]);

            }
        }

        robotSprite = new RobotSprite(model.robot);

        this.setPreferredSize(new Dimension(PANEL_SIZE, PANEL_SIZE));
        this.setDoubleBuffered(true);
        this.setFocusable(true);

    }

    void startThread() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {

        long previous = System.nanoTime();
        long current;

        double interval = (double) 1_000_000_000 / FPS;

        double delta = 0.0;

        while (null != thread) {
            current = System.nanoTime();

            delta += (current - previous) / interval;

            if (1.0 / APS <= delta) {
                update();
                repaint();

                delta = 0.0;
            }

            previous = current;
        }

    }

    private void update() {
        robotSprite.update();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        for (int row = 0; row < Model.GRID.length; row++) {
            for (int col = 0; col < Model.GRID[row].length; col++) {
                GRID_SPRITE[row][col].draw(g2d);
            }
        }

        robotSprite.draw(g2d);

        g2d.dispose();
    }

}
