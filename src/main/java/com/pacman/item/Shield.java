package com.pacman.item;

import com.pacman.entity.Player;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Shield item (OOP: Inheritance from Item)
 * Effect: Blocks one enemy collision
 */
public class Shield extends Item {
    public Shield(int gridX, int gridY) {
        super(gridX, gridY, ItemType.SHIELD);
    }

    @Override
    protected void renderIcon(GraphicsContext gc, double x, double y) {
        gc.setFill(Color.WHITE);
        double[] xPoints = {x, x - 7, x - 6, x, x + 6, x + 7};
        double[] yPoints = {y - 7, y - 3, y + 5, y + 8, y + 5, y - 3};
        gc.fillPolygon(xPoints, yPoints, 6);

        gc.setStroke(Color.web(type.getColor()));
        gc.setLineWidth(2);
        gc.strokeLine(x, y - 4, x, y + 4);
        gc.strokeLine(x - 3, y, x + 3, y);
    }

    @Override
    public void applyEffect(Player player) {
        player.addEffect(ItemType.SHIELD, 0);
        System.out.println("Shield obtained! Blocks one attack");
    }
}
