package agt.planner.nav;

public class NavNode {

    final NavState state;
    final NavNode parent;

    final int action, pathCost, evalCost;

    public NavNode(
            NavState state, NavNode parent,
            int action,
            int pathCost, int evalCost) {

        this.state = state;
        this.parent = parent;

        this.action = action;

        this.pathCost = pathCost;
        this.evalCost = evalCost;

    }

}
