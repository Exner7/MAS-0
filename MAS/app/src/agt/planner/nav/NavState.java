package agt.planner.nav;

public class NavState {

    final int row, col;

    public NavState(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NavState)) {
            return false;
        }

        return (row == ((NavState) obj).row &&
                col == ((NavState) obj).col);
    }

}
