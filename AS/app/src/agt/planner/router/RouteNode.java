package planner.router;

public class RouteNode {

    final RouteState state;
    final RouteNode parent;

    final int action, pathCost;

    public RouteNode(RouteState state, RouteNode parent, int action, int pathCost) {

        this.state = state;
        this.parent = parent;

        this.action = action;
        this.pathCost = pathCost;

    }

}
