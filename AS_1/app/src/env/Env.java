import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Structure;

import jason.environment.Environment;

import model.Model;

import view.Panel;
import view.View;

import java.util.logging.Logger;

public class Env extends Environment {

    static Logger logger = Logger.getLogger(Env.class.getName());

    Model model;
    View view;

    @Override
    public void init(String[] args) {

        model = new Model();
        view = new View(model);

        updatePercepts();

    }

    private void updatePercepts() {
        clearPercepts();

        addPercept(Literal.parseLiteral(
                String.format("energy(%d)", model.robot.energy)));

        addPercept(Literal.parseLiteral(
                String.format("carry(%d)", model.robot.carry)));

        addPercept(Literal.parseLiteral(
                String.format("robot([%d, %d])",
                        model.robot.cell.row, model.robot.cell.col)));

        String nodes = String.format("nodes([ [%d, %d]",
                model.robot.cell.row, model.robot.cell.col);

        for (int c = 1; c < Model.COLOR_CELLS.length; c++) {
            nodes += String.format(", [%d, %d]",
                    Model.COLOR_CELLS[c][0], Model.COLOR_CELLS[c][1]);
        }

        nodes += " ])";

        addPercept(Literal.parseLiteral(nodes));

        int[] route = model.spawn();

        if (0 < route[1]) {
            addPercept(Literal.parseLiteral(
                    String.format("route([%d, %d])",
                            route[0], route[1])));
        }

    }

    @Override
    public boolean executeAction(String agName, Structure act) {

        String log = String.format(
                "%s\nAgent '%s' executing action: %s",
                model, agName, act);

        logger.info(log);

        try {

            switch (act.getFunctor()) {
                case "move":
                    int d = (int) ((NumberTerm) act.getTerm(0)).solve();
                    model.robot.move(d);
                    break;

                case "unload":
                    model.robot.unload();
                    break;

                case "load":
                    int c = (int) ((NumberTerm) act.getTerm(0)).solve();
                    model.robot.load(c);
                    break;

                default:
                    model.robot.action = 9;
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        updatePercepts();

        try {
            Thread.sleep(1000 / Panel.APS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        informAgsEnvironmentChanged();

        return true;

    }

}
