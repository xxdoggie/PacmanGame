package com.pacman.entity;

import com.pacman.util.Constants;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Dot class extending Entity (OOP: Inheritance).
 * Represents collectible dots that player needs to gather.
 */
public class Dot extends Entity {

    private static final int DOT_RADIUS = 4;
    private boolean collected;

    public Dot(int gridX, int gridY) {
        super(gridX, gridY);
        this.collected = false;
        this.collisionRadius = DOT_RADIUS;
    }

    @Override
    public void update(double deltaTime) {
        // Dots are static, no update needed
    }

    @Override
    public void render(GraphicsContext gc) {
        if (collected) return;

        double pixelX = getPixelX();
        double pixelY = getPixelY();
        gc.setFill(Color.web(Constants.COLOR_DOT));
        gc.fillOval(pixelX - DOT_RADIUS, pixelY - DOT_RADIUS, DOT_RADIUS * 2, DOT_RADIUS * 2);
    }

    public void collect() {
        this.collected = true;
        this.active = false;
    }

    public boolean isCollected() {
        return collected;
    }

    public boolean canBeCollectedBy(Player player) {
        if (collected) return false;
        double dx = this.gridX - player.getGridX();
        double dy = this.gridY - player.getGridY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < 0.5;
    }
}
