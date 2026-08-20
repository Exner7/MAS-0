package view;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import model.Cell;
import model.Model;

public class CellSprite {

    final Cell cell;

    final int x, y, width, height;

    BufferedImage colorImage, wallsImage;

    static final BufferedImage[][] clientImages = new BufferedImage[Model.MAX_NUM_CELL_CLIENTS][Model.NUM_COLORS];

    static final String IMG_DIR_PATH = "/view/cell";

    public CellSprite(Cell cell) {

        this.cell = cell;

        x = cell.col * Panel.CELL_SIZE;
        y = cell.row * Panel.CELL_SIZE;

        height = width = Panel.CELL_SIZE;

        try {

            colorImage = ImageIO.read(getClass().getResourceAsStream(
                    String.format(
                            "%s/color/%d.png",
                            IMG_DIR_PATH,
                            cell.color)));

            wallsImage = ImageIO.read(getClass().getResourceAsStream(
                    String.format(
                            "%s/walls/%d%d.png",
                            IMG_DIR_PATH,
                            cell.numFreeDirections, cell.orientation)));

            if (null == clientImages[0][1]) {

                for (int i = 0; i < clientImages.length; i++) {
                    for (int j = 1; j < clientImages[i].length; j++) {

                        clientImages[i][j] = ImageIO.read(
                                getClass().getResourceAsStream(
                                        String.format(
                                                "%s/clients/%d%d.png",
                                                IMG_DIR_PATH, i, j)));

                    }
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void draw(Graphics2D g2d) {

        g2d.drawImage(colorImage, x, y, width, height, null);
        g2d.drawImage(wallsImage, x, y, width, height, null);

        for (int i = 0; i < cell.clients.size(); i++) {
            g2d.drawImage(clientImages[i][cell.clients.get(i)],
                    x, y, width, height, null);
        }

    }

}
