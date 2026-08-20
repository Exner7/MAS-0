package model;

public class Robot {

    public int energy, carry;

    public Cell cell;

    public int action = 9;
    public int direction = Model.NUM_DIRECTIONS - 1;

    public Robot(int energy, Cell cell) {

        this.energy = energy;
        this.cell = cell;

    }

    public void move(int d) {

        if (energy-- <= 0) { return; }

        Model.evaluation += Model.ACTION;
        System.out.printf(
                "ACTION (%d), Evaluation: %d\n",
                Model.ACTION, Model.evaluation);

        action = direction = d;

        if (!cell.isDirectionFree[d]) {

            Model.numCollisions++;

            Model.evaluation += Model.COLLISION;
            System.out.printf(
                    "COLLISION (%d), Evaluation: %d\n",
                    Model.COLLISION, Model.evaluation);

            return;

        }

        int row = cell.row + Model.DS[d][0];
        int col = cell.col + Model.DS[d][1];

        cell = Model.GRID[row][col];

    }

    public void unload() {

        if (energy-- <= 0) { return; }

        Model.evaluation += Model.ACTION;
        System.out.printf(
                "ACTION (%d), Evaluation: %d\n",
                Model.ACTION, Model.evaluation);

        action = 4;

        cell.insert(carry);
        carry = 0;

    }

    public void load(int c) {

        if (energy-- <= 0 || 0 < carry) { return; }

        Model.evaluation += Model.ACTION;
        System.out.printf(
                "ACTION (%d), Evaluation: %d\n",
                Model.ACTION, Model.evaluation);

        action = 4 + c;

        carry = cell.extract(c);

        System.out.println("Carrying: " + carry);

    }

}
