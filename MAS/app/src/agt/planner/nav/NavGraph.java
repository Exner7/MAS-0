package agt.planner.nav;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;

public class NavGraph {

    public ArrayList<Integer> search(NavProblem problem) {

        NavNode node = new NavNode(
                problem.initState,
                null,
                9,
                0,
                problem.heuristic(problem.initState));

        PriorityQueue<NavNode> frontier = new PriorityQueue<>(
                (a, b) -> (a.evalCost - b.evalCost));

        frontier.add(node);

        HashSet<Integer> explored = new HashSet<>();

        while (true) {

            if (frontier.isEmpty()) {
                return null;
            }

            node = frontier.poll();

            if (problem.goalTest(node.state)) {
                return solution(node);
            }

            explored.add(10 * node.state.row + node.state.col);

            for (int dirAct = 0; dirAct < NavProblem.DS.length; dirAct++) {

                NavNode child = childNode(problem, node, dirAct);

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

    private boolean hasBeenExplored(
            HashSet<Integer> explored, NavNode node) {

        return explored.contains(
                10 * node.state.row + node.state.col);

    }

    private void replaceRedundantNode(
            PriorityQueue<NavNode> frontier, NavNode node) {

        for (NavNode frontNode : frontier) {

            if (node.state.equals(frontNode.state) &&
                    node.evalCost < frontNode.evalCost) {

                frontier.remove(frontNode);
                frontier.add(node);

                return;

            }

        }

    }

    private NavNode childNode(
            NavProblem problem, NavNode parent, int action) {

        NavState state = problem.result(parent.state, action);

        if (null == state) {
            return null;
        }

        int pathCost = parent.pathCost +
                problem.stepCost(parent.state, action);

        int evalCost = pathCost + problem.heuristic(state);

        return new NavNode(state, parent, action, pathCost, evalCost);

    }

    private ArrayList<Integer> solution(NavNode node) {

        ArrayList<Integer> solution = new ArrayList<>();

        solution.add(node.pathCost);

        while (null != node.parent) {

            solution.add(1, node.action);
            node = node.parent;

        }

        return solution;

    }

}
