package com.pacman.entity;

import com.pacman.util.Constants;
import com.pacman.util.Direction;
import javafx.scene.canvas.GraphicsContext;

/**
 * Abstract base class for all game entities (OOP: Inheritance, Encapsulation).
 * All movable or interactive objects in the game inherit from this class.
 */
public abstract class Entity {

    protected double gridX;
    protected double gridY;
    protected double speed;
    protected Direction direction;
    protected boolean active;
    protected double collisionRadius;

    public Entity(double gridX, double gridY) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.speed = 0;
        this.direction = Direction.NONE;
        this.active = true;
        this.collisionRadius = Constants.TILE_SIZE / 2.0 - 2;
    }

    /**
     * Abstract method for updating entity state (OOP: Polymorphism).
     */
    public abstract void update(double deltaTime);

    /**
     * Abstract method for rendering entity (OOP: Polymorphism).
     */
    public abstract void render(GraphicsContext gc);

    public double getPixelX() {
        return (gridX + 0.5) * Constants.TILE_SIZE;
    }

    public double getPixelY() {
        return (gridY + 0.5) * Constants.TILE_SIZE;
    }

    public int getTileX() {
        return (int) Math.round(gridX);
    }

    public int getTileY() {
        return (int) Math.round(gridY);
    }

    /**
     * Collision detection with another entity.
     */
    public boolean collidesWith(Entity other) {
        if (!this.active || !other.active) {
            return false;
        }
        double dx = this.getPixelX() - other.getPixelX();
        double dy = this.getPixelY() - other.getPixelY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < (this.collisionRadius + other.collisionRadius);
    }

    public boolean isOnTile(int tileX, int tileY) {
        return getTileX() == tileX && getTileY() == tileY;
    }

    public double distanceTo(Entity other) {
        double dx = this.gridX - other.gridX;
        double dy = this.gridY - other.gridY;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double distanceToTile(int tileX, int tileY) {
        double dx = this.gridX - tileX;
        double dy = this.gridY - tileY;
        return Math.sqrt(dx * dx + dy * dy);
    }

    // Getters and Setters (OOP: Encapsulation)

    public double getGridX() { return gridX; }
    public void setGridX(double gridX) { this.gridX = gridX; }
    public double getGridY() { return gridY; }
    public void setGridY(double gridY) { this.gridY = gridY; }
    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }
    public Direction getDirection() { return direction; }
    public void setDirection(Direction direction) { this.direction = direction; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public double getCollisionRadius() { return collisionRadius; }
    public void setCollisionRadius(double collisionRadius) { this.collisionRadius = collisionRadius; }
}
