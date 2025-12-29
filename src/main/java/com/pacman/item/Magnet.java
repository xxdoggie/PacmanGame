package com.pacman.item;

import com.pacman.entity.Player;
import com.pacman.util.Constants;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Magnet item (OOP: Inheritance from Item)
 * Effect: Automatically attracts nearby dots
 */
public class Magnet extends Item {
    public Magnet(int gridX, int gridY) {
        super(gridX, gridY, ItemType.MAGNET);
    }

    @Override
    protected void renderIcon(GraphicsContext gc, double x, double y) {
        gc.setLineWidth(3);
        gc.setStroke(Color.RED);
        gc.strokeLine(x - 6, y - 5, x - 6, y + 5);
        gc.strokeArc(x - 6, y + 2, 12, 10, 180, 90, javafx.scene.shape.ArcType.OPEN);

        gc.setStroke(Color.BLUE);
        gc.strokeLine(x + 6, y - 5, x + 6, y + 5);
        gc.strokeArc(x - 6, y + 2, 12, 10, 270, 90, javafx.scene.shape.ArcType.OPEN);
    }

    @Override
    public void applyEffect(Player player) {
        player.addEffect(ItemType.MAGNET, Constants.MAGNET_DURATION);
        System.out.println("Magnet obtained! Duration: " + Constants.MAGNET_DURATION + "s");
    }
}
