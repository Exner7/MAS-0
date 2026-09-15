package agt.planner;

import java.util.ArrayList;

import jason.NoValueException;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ListTerm;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Term;

import agt.planner.router.RouteGraph;
import agt.planner.router.RouteProblem;

public class plan extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {

        boolean[][][] FCDs = getCDs((ListTerm) args[0]);
        boolean[][][] BCDs = getCDs((ListTerm) args[1]);

        int[][] NDs = getNodes((ListTerm) args[2]);
        int[][] RTs = getRoutes((ListTerm) args[3], NDs.length);

        int C = (int) ((NumberTerm) args[4]).solve();
        int E = (int) ((NumberTerm) args[5]).solve();

        int NoFMs = (int) ((NumberTerm) args[6]).solve();
        int NoCs = (int) ((NumberTerm) args[7]).solve();

        RouteProblem problem = new RouteProblem(
                FCDs, BCDs,
                NDs, RTs,
                C, E,
                NoFMs, NoCs);

        RouteGraph graph = new RouteGraph();

        ArrayList<Integer> plan = graph.search(problem);

        ListTermImpl NewPL = new ListTermImpl();

        if (null == plan || plan.size() <= 0) {
            un.unifies(args[8], new NumberTermImpl(0));
            un.unifies(args[9], NewPL);
            return true;
        }

        if (0 < plan.get(0)) {
            un.unifies(args[8], new NumberTermImpl(plan.get(0)));
            NewPL.add(new NumberTermImpl(9));
            un.unifies(args[9], NewPL);
            return true;
        }

        for (int i = 1; i < plan.size(); i++) {
            NewPL.add(new NumberTermImpl(plan.get(i)));
        }

        un.unifies(args[8], new NumberTermImpl(plan.get(0)));
        un.unifies(args[9], NewPL);
        return true;

    }

    private int[][] getRoutes(ListTerm RTs, int numNodes) {

        int[][] result = new int[numNodes][numNodes];

        try {

            for (Term term : RTs) { // [ [Src, Dst], ... ]

                ListTerm routeTerm = (ListTerm) term; // [Src, Dst]

                int src = (int) ((NumberTerm) routeTerm.get(0)).solve(); // Src
                int dst = (int) ((NumberTerm) routeTerm.get(1)).solve(); // Dst

                result[src][dst]++;

            }

        } catch (NoValueException e) {
            e.printStackTrace();
        }

        return result;

    }

    private int[][] getNodes(ListTerm NDs) {

        int[][] result = new int[NDs.size()][2];

        try {

            for (int i = 0; i < result.length; i++) { // [ [Row, Col], ... ]

                ListTerm cellTerm = (ListTerm) NDs.get(i); // [Row, Col]

                int row = (int) ((NumberTerm) cellTerm.get(0)).solve(); // Row
                int col = (int) ((NumberTerm) cellTerm.get(1)).solve(); // Col

                result[i] = new int[] { row, col };

            }

        } catch (NoValueException e) {
            e.printStackTrace();
        }

        return result;

    }

    private boolean[][][] getCDs(ListTerm CDs) {

        final int GRID_SIZE = 5;
        final int[][] DS = new int[][] {
                { 0, 1 }, { -1, 0 },
                { 0, -1 }, { 1, 0 }
        };

        boolean[][][] result = new boolean[GRID_SIZE][GRID_SIZE][DS.length];

        try {

            for (Term term : CDs) { // [ [[Row, Col], Dir], ... ]

                ListTerm listTerm = (ListTerm) term; // [[Row, Col], Dir]

                ListTerm cellTerm = (ListTerm) listTerm.get(0); // [Row, Col]

                int dir = (int) ((NumberTerm) listTerm.get(1)).solve(); // Dir

                int row = (int) ((NumberTerm) cellTerm.get(0)).solve(); // Row
                int col = (int) ((NumberTerm) cellTerm.get(1)).solve(); // Col

                int row2 = row + DS[dir][0];
                int col2 = col + DS[dir][1];

                result[row][col][dir] = true;

                if (row2 < 0 || GRID_SIZE <= row2 || col2 < 0 || GRID_SIZE <= col2) {
                    continue;
                }

                int dir2 = (dir + 2) % DS.length;

                result[row2][col2][dir2] = true;

            }

        } catch (NoValueException e) {
            e.printStackTrace();
        }

        return result;

    }

}
