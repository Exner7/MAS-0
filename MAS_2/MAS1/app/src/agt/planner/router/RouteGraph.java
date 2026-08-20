package planner.router;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;

public class RouteGraph {

    public ArrayList<Integer> search(RouteProblem problem) {

        RouteNode node = new RouteNode(
                problem.initState,
                null,
                0,
                0);

        RouteNode bestNode = node;

        PriorityQueue<RouteNode> frontier = new PriorityQueue<>(
                (a, b) -> (a.pathCost - b.pathCost));

        frontier.add(node);

        HashSet<RouteState> explored = new HashSet<>();

        while (true) {

            if (frontier.isEmpty()) {
                return solution(problem, bestNode);
            }

            node = frontier.poll();

            if (node.pathCost < bestNode.pathCost) {
                bestNode = node;
            }

            explored.add(node.state);

            for (int t = 1; t < problem.nodes.length; t++) {

                RouteNode child = childNode(problem, node, t);

                if (null == child) {
                    continue;
                }

                if (hasBeenExplored(explored, child)) {

                    replaceRedundantNode(frontier, child);
                    continue;

                }

                frontier.add(child);

            }

        }

    }

    private boolean hasBeenExplored(HashSet<RouteState> explored, RouteNode node) {

        for (RouteState exploredState : explored) {
            if (exploredState.equals(node.state)) {
                return true;
            }
        }

        return false;

    }

    private void replaceRedundantNode(
            PriorityQueue<RouteNode> frontier, RouteNode node) {

        for (RouteNode frontNode : frontier) {

            if (node.state.equals(frontNode.state) &&
                    node.pathCost < frontNode.pathCost) {

                frontier.remove(frontNode);
                frontier.add(node);

                return;

            }

        }

    }

    private RouteNode childNode(
            RouteProblem problem, RouteNode parent, int action) {

        RouteState state = problem.result(parent.state, action);

        if (null == state) {
            return null;
        }

        int pathCost = parent.pathCost
                + problem.stepCost(parent.state, action);

        return new RouteNode(state, parent, action, pathCost);

    }

    private ArrayList<Integer> solution(
            RouteProblem problem, RouteNode node) {

        ArrayList<Integer> solution = new ArrayList<>();

        while (null != node.parent) {

            solution.add(0, node.action);
            node = node.parent;

        }

        System.out.println("\nRouting Solution: " + solution);

        ArrayList<Integer> plan = new ArrayList<>();

        if (solution.size() <= 0) {
            System.out.println();
            plan.add(9);
            return plan;
        }

        int initSrc = 0;
        int initDst = solution.get(0);

        ArrayList<Integer> initNav = problem.navs.get(10 * initSrc + initDst);
        if (0 < initNav.size()) {
            initNav.remove(0);
            plan.addAll(initNav);
        }

        if (0 < problem.carry) {
            plan.add(4);
        }

        for (int i = 0; i < solution.size() - 1; i++) {

            int src = solution.get(i);
            int dst = solution.get(i + 1);

            plan.add(4 + dst);

            ArrayList<Integer> nav = problem.navs.get(10 * src + dst);
            if (0 < nav.size()) {
                nav.remove(0);
                plan.addAll(nav);
            }

            plan.add(4);

        }

        System.out.println("Plan: " + plan);
        System.out.println();

        return plan;

    }

}
