package com.pacman.item;

import com.pacman.entity.Entity;
import com.pacman.entity.Player;
import com.pacman.util.Constants;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Abstract base class for all items (OOP: Abstraction & Inheritance)
 * Provides common behavior for collectible power-ups
 */
public abstract class Item extends Entity {
    protected ItemType type;
    protected boolean collected;
    protected static final int ITEM_RADIUS = Constants.TILE_SIZE / 3;
    protected double animationTimer;

    public Item(int gridX, int gridY, ItemType type) {
        super(gridX, gridY);
        this.type = type;
        this.collected = false;
        this.collisionRadius = ITEM_RADIUS;
        this.animationTimer = 0;
    }
    
    @Override
    public void update(double deltaTime) {
        animationTimer += deltaTime;
    }
    
    @Override
    public void render(GraphicsContext gc) {
        if (collected) return;

        double pixelX = getPixelX();
        double pixelY = getPixelY();
        double floatOffset = Math.sin(animationTimer * 3) * 3;
        pixelY += floatOffset;

        // Glow effect
        gc.setFill(Color.web(type.getColor(), 0.3));
        gc.fillOval(pixelX - ITEM_RADIUS - 4, pixelY - ITEM_RADIUS - 4,
                (ITEM_RADIUS + 4) * 2, (ITEM_RADIUS + 4) * 2);

        // Item body
        gc.setFill(Color.web(type.getColor()));
        gc.fillOval(pixelX - ITEM_RADIUS, pixelY - ITEM_RADIUS,
                ITEM_RADIUS * 2, ITEM_RADIUS * 2);

        renderIcon(gc, pixelX, pixelY);
    }

    /** OOP: Abstract method - each subclass implements its own icon */
    protected abstract void renderIcon(GraphicsContext gc, double x, double y);

    /** OOP: Abstract method - polymorphism for different item effects */
    public abstract void applyEffect(Player player);

    public void collect(Player player) {
        if (!collected) {
            collected = true;
            active = false;
            applyEffect(player);
        }
    }

    public boolean canBeCollectedBy(Player player) {
        if (collected) return false;
        double dx = this.gridX - player.getGridX();
        double dy = this.gridY - player.getGridY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < 0.6;
    }

    public ItemType getType() { return type; }
    public boolean isCollected() { return collected; }
}
