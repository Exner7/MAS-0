package model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;

import view.Panel;

public class Model {

    public static final int[][] DS = new int[][] {
            { 0, 1 }, { -1, 0 },
            { 0, -1 }, { 1, 0 }
    };

    public static final int[][] COLOR_CELLS = new int[][] {
            null,
            { 4, 0 }, { 4, 4 }, { 0, 3 }, { 0, 0 }
    };

    public static final int NUM_DIRECTIONS = DS.length;
    public static final int NUM_COLORS = COLOR_CELLS.length;

    public static final int MAX_NUM_CELL_CLIENTS = 4;
    public static final double SPAWN_PROB = 0.25;
    public static final Random RAND = new Random();

    public static final int COLLISION = -100;
    public static final int PENALTY = -10;
    public static final int ACTION = -1;
    public static final int REWARD = +20;

    public static final int GRID_SIZE = 5;
    public static final Cell[][] GRID = new Cell[GRID_SIZE][GRID_SIZE];
    private static final String GRID_TXT_FILE_PATH = "/model/grid.txt";
    private static final String GRID_TXT_SEPARATOR = " ";

    public static final int MAX_ROBOT_ENERGY = 1 * Panel.APS * 60;

    public Robot robot;

    public static int numServedClients;
    public static int numCollisions;
    public static int evaluation;

    public Model() {

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream(GRID_TXT_FILE_PATH)))) {

            for (int row = 0; row < GRID.length; row++) {
                String[] rowCellCodes = br.readLine().split(GRID_TXT_SEPARATOR);

                for (int col = 0; col < rowCellCodes.length; col++) {
                    GRID[row][col] = new Cell(row, col,
                            Integer.parseInt(rowCellCodes[col]) / 10,
                            Integer.parseInt(rowCellCodes[col]) % 10);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int c = 1; c < COLOR_CELLS.length; c++) {

            int row = COLOR_CELLS[c][0];
            int col = COLOR_CELLS[c][1];

            GRID[row][col].color = c;

        }

        int row = RAND.nextInt(1, GRID.length);
        int col = RAND.nextInt(1, GRID[row].length);

        robot = new Robot(MAX_ROBOT_ENERGY, GRID[row][col]);

    }

    public int[] spawn() {

        int c = RAND.nextInt(1, NUM_COLORS);

        int row = COLOR_CELLS[c][0];
        int col = COLOR_CELLS[c][1];

        return new int[] { c, GRID[row][col].spawn() };

    }

    @Override
    public String toString() {
        return String.format(
                "| Energy: %3d |  Served: %3d |  Collisions: %3d |  Evaluation: %3d |",
                robot.energy, numServedClients, numCollisions, evaluation);
    }

}
