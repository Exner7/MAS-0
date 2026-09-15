package planner.router;

import java.util.ArrayList;
import java.util.HashMap;

import planner.nav.NavGraph;
import planner.nav.NavProblem;
import planner.nav.NavState;

public class RouteProblem {

    final int REWARD = 20;

    final int[][] nodes;

    final int carry;

    final double collisionProb;

    final RouteState initState;

    HashMap<Integer, ArrayList<Integer>> navs = new HashMap<>();

    public RouteProblem(
            boolean[][][] FCDs, boolean[][][] BCDs,
            int[][] nodes, int[][] routes,
            int carry, int energy,
            int NoFMs, int NoCs) {

        this.nodes = nodes;
        this.carry = carry;

        initState = new RouteState(0, 0, energy, routes);

        if (0 < carry) {

            initState.routes[0][carry]++;

        } else {

            for (int src = 0; src < routes.length; src++) {
                if (hasDestination(initState, src)) {
                    initState.routes[0][src]++;
                }
            }

        }

        collisionProb = (NoFMs <= 0)
                ? 0.5
                : (double) NoCs / (NoFMs + NoCs);

        System.out.printf("\nNew-Move Risk: %f\n", collisionProb * 100);

        System.out.printf("\ndst: %9s", "");

        for (int src = 0; src < routes.length; src++) {
            System.out.printf(" %03d ", src);
        }

        for (int src = 0; src < routes.length; src++) {

            System.out.printf("\nsrc=%03d: ", src);

            for (int dst = 0; dst < routes[src].length; dst++) {

                if (initState.routes[src][dst] <= 0) {
                    System.out.printf(" %3s ", "###");

                    continue;
                }

                int initRow = nodes[src][0];
                int initCol = nodes[src][1];

                int goalRow = nodes[dst][0];
                int goalCol = nodes[dst][1];

                NavState initNavState = new NavState(initRow, initCol);
                NavState goalNavState = new NavState(goalRow, goalCol);

                NavProblem navProblem = new NavProblem(
                        FCDs, BCDs, collisionProb,
                        initNavState, goalNavState);

                NavGraph navGraph = new NavGraph();

                ArrayList<Integer> nav = navGraph.search(navProblem);

                navs.put(10 * src + dst, nav);

                System.out.printf(" %03d ", nav.get(0));

            }

        }

        System.out.println();

    }

    public RouteState result(RouteState state, int t) {

        if (state.routes[state.color][t] <= 0) {
            return null;
        }

        int navSteps = navs.get(10 * state.color + t).size() - 1;

        int energyLoss;

        if (state.depth == 0 && carry <= 0) {
            energyLoss = navSteps;
        } else if (state.depth == 0) {
            energyLoss = navSteps + 1;
        } else {
            energyLoss = 1 + navSteps + 1;
        }

        int energy = state.energy - energyLoss;

        if (energy < 0) {
            return null;
        }

        int[][] routes = state.getRoutesCopy();
        routes[state.color][t]--;

        return new RouteState(t, state.depth + 1, energy, routes);

    }

    public int stepCost(RouteState state, int t) {

        int navCost = navs.get(10 * state.color + t).get(0);

        if (state.depth == 0 && carry <= 0) {
            return navCost;
        }

        if (state.depth == 0) {
            return navCost + 1 - REWARD;
        }

        return 1 + navCost + 1 - REWARD;

    }

    private boolean hasDestination(RouteState state, int src) {

        for (int dst = 0; dst < nodes.length; dst++) {
            if (0 < state.routes[src][dst]) {
                return true;
            }
        }

        return false;

    }

}
