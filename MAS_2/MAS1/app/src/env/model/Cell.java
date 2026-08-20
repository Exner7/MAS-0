package model;

import java.util.ArrayList;
import java.util.Arrays;

public class Cell {

    public final int row, col;

    public int color;

    public final int numFreeDirections, orientation;
    public final boolean[] isDirectionFree = new boolean[Model.NUM_DIRECTIONS];

    public final ArrayList<Integer> clients = new ArrayList<>();

    public Cell(int row, int col,
            int numFreeDirections, int orientation) {

        this.row = row;
        this.col = col;

        this.numFreeDirections = numFreeDirections;
        this.orientation = orientation;

        final int ND = Model.NUM_DIRECTIONS;

        switch (numFreeDirections) {
            case 1:
                isDirectionFree[orientation % ND] = true;
                break;

            case 2:
                isDirectionFree[orientation % ND] = true;
                if (orientation < ND) {
                    isDirectionFree[(orientation + 1) % ND] = true;
                } else {
                    isDirectionFree[(orientation + 2) % ND] = true;
                }
                break;

            case 3:
                isDirectionFree[orientation % ND] = true;
                isDirectionFree[(orientation + 1) % ND] = true;
                isDirectionFree[(orientation + 2) % ND] = true;
                break;

            case 4:
                Arrays.fill(isDirectionFree, true);
                break;

            default:
                break;
        }

    }

    public int spawn() {

        if (Model.MAX_NUM_CELL_CLIENTS <= clients.size() ||
                Model.SPAWN_PROB < Model.RAND.nextDouble()) {
            return 0;
        }

        int c = Model.RAND.nextInt(1, Model.NUM_COLORS);
        clients.add(c);

        return c;

    }

    public int extract(int c) {
        return ((clients.remove((Integer) c)) ? c : 0);
    }

    public void insert(int c) {

        if (0 == c) {
            return;
        }

        if (color == c) {

            Model.numServedClients++;

            Model.evaluation += Model.REWARD;
            System.out.printf(
                    "REWARD (%d), Evaluation: %d\n",
                    Model.REWARD, Model.evaluation);

            return;

        }

        Model.evaluation += Model.PENALTY;
        System.out.printf(
                "PENALTY (%d), Evaluation: %d\n",
                Model.PENALTY, Model.evaluation);

        if (clients.size() < Model.MAX_NUM_CELL_CLIENTS) {
            clients.add(c);
        }

    }

}
