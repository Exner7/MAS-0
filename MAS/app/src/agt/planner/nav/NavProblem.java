package agt.planner.nav;

public class NavProblem {

    final static int ACTION_COST = 1;
    final static int COLLISION_COST = 100;

    final static int GRID_SIZE = 5;

    static final int[][] DS = new int[][] {
            { 0, 1 }, { -1, 0 },
            { 0, -1 }, { 1, 0 }
    };

    final boolean[][][] FCDs, BCDs;
    final double collisionProb;

    final NavState initState, goalState;

    public NavProblem(
            boolean[][][] FCDs, boolean[][][] BCDs,
            double collisionProb,
            NavState initState, NavState goalState) {

        this.FCDs = FCDs;
        this.BCDs = BCDs;

        this.collisionProb = collisionProb;

        this.initState = initState;
        this.goalState = goalState;

    }

    public NavState result(NavState state, int d) {

        if (BCDs[state.row][state.col][d]) {
            return null;
        }

        int row = state.row + DS[d][0];
        int col = state.col + DS[d][1];

        if (row < 0 || GRID_SIZE <= row || col < 0 || GRID_SIZE <= col) {
            return null;
        }

        return new NavState(row, col);

    }

    public int stepCost(NavState state, int d) {

        if (FCDs[state.row][state.col][d]) {
            return ACTION_COST;
        }

        if (BCDs[state.row][state.col][d]) {
            return ACTION_COST + COLLISION_COST;
        }

        return (int) (ACTION_COST + collisionProb * COLLISION_COST);

    }

    public boolean goalTest(NavState state) {
        return state.equals(goalState);
    }

    public int heuristic(NavState state) {
        return (Math.abs(goalState.row - state.row) +
                Math.abs(goalState.col - state.col));
    }

}
