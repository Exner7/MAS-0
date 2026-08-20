import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Structure;

import jason.environment.Environment;

import model.Model;
import model.Robot;
import view.Panel;
import view.View;

import java.util.logging.Logger;

public class Env extends Environment {

    static Logger logger = Logger.getLogger(Env.class.getName());

    Model model;
    View view;

    final String agNamePrefix = "hedge";

    int lock;

    @Override
    public void init(String[] args) {

        model = new Model();
        view = new View(model);

        for (int agentNum = 1; agentNum <= Model.robots.size(); agentNum++) {
            updateAgPercepts(agentNum);
        }

    }

    private void updateAgPercepts(int agentNum) {

        String agentName = agNamePrefix + agentNum;

        clearPercepts(agentName);

        int robotIdx = agentNum - 1;
        Robot robot = Model.robots.get(robotIdx);

        clearPercepts();

        addPercept(agentName, Literal.parseLiteral(
                String.format("energy(%d)", robot.energy)));

        addPercept(agentName, Literal.parseLiteral(
                String.format("carry(%d)", robot.carry)));

        addPercept(agentName, Literal.parseLiteral(
                String.format("robot([%d, %d])",
                        robot.cell.row, robot.cell.col)));

        String nodes = String.format("nodes([ [%d, %d]",
                robot.cell.row, robot.cell.col);

        for (int c = 1; c < Model.COLOR_CELLS.length; c++) {
            nodes += String.format(", [%d, %d]",
                    Model.COLOR_CELLS[c][0], Model.COLOR_CELLS[c][1]);
        }

        nodes += " ])";

        addPercept(agentName, Literal.parseLiteral(nodes));

        lock += agentNum; if (lock % 3 == 0) { lock = 0; return; }

        int[] route = model.spawn();

        if (0 < route[1]) {

            String asAgentName = agNamePrefix + (Model.RAND.nextInt(2) + 1);
            addPercept(asAgentName, Literal.parseLiteral(
                    String.format("route([%d, %d])",
                            route[0], route[1])));

        }

    }

    @Override
    public boolean executeAction(String agName, Structure act) {

        int agentNum = Integer.parseInt(agName.substring(agName.length() - 1));

        int robotIdx = agentNum - 1;
        Robot robot = Model.robots.get(robotIdx);

        String log = String.format(
                "%s\nAgent '%s' executing action: %s",
                model, agName, act);

        logger.info(log);

        try {

            switch (act.getFunctor()) {
                case "move":
                    int d = (int) ((NumberTerm) act.getTerm(0)).solve();
                    robot.move(d);
                    break;

                case "unload":
                    robot.unload();
                    break;

                case "load":
                    int c = (int) ((NumberTerm) act.getTerm(0)).solve();
                    robot.load(c);
                    break;

                default:
                    robot.action = 9;
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        updateAgPercepts(agentNum);

        try {
            Thread.sleep(1000 / Panel.APS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        informAgsEnvironmentChanged(agName);

        return true;

    }

}
