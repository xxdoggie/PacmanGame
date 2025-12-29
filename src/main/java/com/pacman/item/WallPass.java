package com.pacman.item;

import com.pacman.entity.Player;
import com.pacman.util.Constants;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Wall Pass item (OOP: Inheritance from Item)
 * Effect: Temporarily allows passing through walls
 */
public class WallPass extends Item {
    public WallPass(int gridX, int gridY) {
        super(gridX, gridY, ItemType.WALL_PASS);
    }

    @Override
    protected void renderIcon(GraphicsContext gc, double x, double y) {
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);

        // Dashed wall
        gc.setLineDashes(3);
        gc.strokeLine(x - 2, y - 7, x - 2, y + 7);
        gc.setLineDashes(0);

        // Arrow through wall
        gc.setFill(Color.WHITE);
        double[] xPoints = {x + 6, x, x, x + 6};
        double[] yPoints = {y, y - 4, y + 4, y};
        gc.fillPolygon(xPoints, yPoints, 4);
        gc.strokeLine(x - 6, y, x, y);
    }

    @Override
    public void applyEffect(Player player) {
        player.addEffect(ItemType.WALL_PASS, Constants.WALL_PASS_DURATION);
        System.out.println("Wall Pass obtained! Duration: " + Constants.WALL_PASS_DURATION + "s");
    }
}
