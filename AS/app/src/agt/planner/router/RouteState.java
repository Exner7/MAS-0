package planner.router;

import java.util.Arrays;

public class RouteState {

    final int color, depth, energy;

    final int[][] routes;

    public RouteState(int color, int depth, int energy, int[][] routes) {

        this.color = color;
        this.depth = depth;
        this.energy = energy;
        this.routes = routes;

    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RouteState)) {
            return false;
        }

        return (color == ((RouteState) obj).color &&
                depth == ((RouteState) obj).depth &&
                energy == ((RouteState) obj).energy &&
                Arrays.deepEquals(routes, ((RouteState) obj).routes));
    }

    public int[][] getRoutesCopy() {
        int[][] copy = new int[routes.length][routes.length];

        for (int i = 0; i < copy.length; i++) {
            for (int j = 0; j < copy.length; j++) {
                copy[i][j] = routes[i][j];
            }
        }

        return copy;
    }

}
