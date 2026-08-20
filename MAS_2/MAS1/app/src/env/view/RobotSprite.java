package view;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import model.Model;
import model.Robot;

public class RobotSprite {

    final Robot robot;

    int x, y;
    
    final int width, height;

    final int speed = Panel.APS * Panel.CELL_SIZE / Panel.FPS;

    static final String IMG_DIR_PATH = "/view/robot";

    static final BufferedImage[] robotImages = new BufferedImage[Model.NUM_DIRECTIONS + 1];
    static final BufferedImage[] clientImages = new BufferedImage[Model.NUM_COLORS];
    static final BufferedImage[] energyImages = new BufferedImage[5];

    public RobotSprite(Robot robot) {

        this.robot = robot;

        x = robot.cell.col * Panel.CELL_SIZE;
        y = robot.cell.row * Panel.CELL_SIZE;

        height = width = Panel.CELL_SIZE;

        try {

            if (null == robotImages[0]) {

                for (int d = 0; d < robotImages.length; d++) {

                    robotImages[d] = ImageIO.read(
                            getClass().getResourceAsStream(
                                    String.format(
                                            "%s/%d.png",
                                            IMG_DIR_PATH, d)));

                }

                for (int c = 1; c < clientImages.length; c++) {

                    clientImages[c] = ImageIO.read(
                            getClass().getResourceAsStream(
                                    String.format(
                                            "%s/client/%d.png",
                                            IMG_DIR_PATH, c)));

                }

                for (int l = 0; l < energyImages.length; l++) {

                    energyImages[l] = ImageIO.read(
                            getClass().getResourceAsStream(
                                    String.format(
                                            "%s/energy/%d.png",
                                            IMG_DIR_PATH, l)));

                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void draw(Graphics2D g2d) {

        if (0 < robot.carry) {
            g2d.drawImage(clientImages[robot.carry],
                    x, y, width, height, null);
        }

        int robotImageIndex = (0 < robot.energy)
                ? robot.direction
                : robotImages.length - 1;

        int energyLevel = (int) Math.ceil(
                4 * (double) robot.energy / Model.MAX_ROBOT_ENERGY);

        g2d.drawImage(robotImages[robotImageIndex],
                x, y, width, height, null);

        g2d.drawImage(energyImages[energyLevel],
                x, y, width, height, null);

    }

    public void update() {

        if (Model.NUM_DIRECTIONS <= robot.action || robot.energy <= 0) {
            return;
        }

        if (robot.cell.isDirectionFree[robot.direction]) {
            updateMove();
            return;
        }

        updateCollision();

    }

    private void updateCollision() {
        switch (robot.direction) {

            case 0: if (x < robot.cell.col * Panel.CELL_SIZE + 3 * Panel.SCALE_FACTOR) { x += speed; } else { resetCoordinates(); } break;
            case 1: if (robot.cell.row * Panel.CELL_SIZE - 3 * Panel.SCALE_FACTOR < y) { y -= speed; } else { resetCoordinates(); } break;
            case 2: if (robot.cell.col * Panel.CELL_SIZE - 3 * Panel.SCALE_FACTOR < x) { x -= speed; } else { resetCoordinates(); } break;
            case 3: if (y < robot.cell.row * Panel.CELL_SIZE + 3 * Panel.SCALE_FACTOR) { y += speed; } else { resetCoordinates(); } break;

            default: resetCoordinates(); break;

        }
    }

    private void updateMove() {
        switch (robot.direction) {

            case 0: if (x < robot.cell.col * Panel.CELL_SIZE) { x += speed; } else { resetCoordinates(); } break;
            case 1: if (robot.cell.row * Panel.CELL_SIZE < y) { y -= speed; } else { resetCoordinates(); } break;
            case 2: if (robot.cell.col * Panel.CELL_SIZE < x) { x -= speed; } else { resetCoordinates(); } break;
            case 3: if (y < robot.cell.row * Panel.CELL_SIZE) { y += speed; } else { resetCoordinates(); } break;

            default: resetCoordinates(); break;

        }
    }

    public void resetCoordinates() {
        x = robot.cell.col * Panel.CELL_SIZE;
        y = robot.cell.row * Panel.CELL_SIZE;
    }

}
