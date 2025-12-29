package com.pacman.entity.enemy;

import com.pacman.entity.Entity;
import com.pacman.entity.Player;
import com.pacman.map.GameMap;
import com.pacman.util.Constants;
import com.pacman.util.Direction;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Abstract base class for all enemies (OOP: Inheritance, Polymorphism).
 * Each enemy type implements different AI behavior through decideDirection().
 */
public abstract class Enemy extends Entity {

    protected static final Random random = new Random();
    protected static final int ENEMY_RADIUS = Constants.TILE_SIZE / 2 - 2;

    protected String color;
    protected GameMap gameMap;
    protected Player player;
    protected boolean frozen;
    protected double frozenTimer;
    protected double moveTimer;
    protected double moveInterval;

    public Enemy(double gridX, double gridY, double speed, String color) {
        super(gridX, gridY);
        this.speed = speed;
        this.color = color;
        this.collisionRadius = ENEMY_RADIUS;
        this.frozen = false;
        this.frozenTimer = 0;
        this.moveTimer = 0;
        this.moveInterval = 0.2;
    }

    public void setGameMap(GameMap gameMap) { this.gameMap = gameMap; }
    public void setPlayer(Player player) { this.player = player; }

    @Override
    public void update(double deltaTime) {
        if (frozen) {
            frozenTimer -= deltaTime;
            if (frozenTimer <= 0) {
                frozen = false;
                frozenTimer = 0;
            }
            return;
        }

        double centerDist = Math.abs(gridX - Math.round(gridX)) + Math.abs(gridY - Math.round(gridY));

        if (centerDist < 0.1) {
            moveTimer += deltaTime;
            if (moveTimer >= moveInterval) {
                moveTimer = 0;
                alignToGrid();
                decideDirection();
            }
        }

        if (direction != Direction.NONE) {
            int targetTileX = getTileX() + direction.getDx();
            int targetTileY = getTileY() + direction.getDy();

            if (gameMap != null && gameMap.canMoveTo(targetTileX, targetTileY, false)) {
                double newX = gridX + direction.getDx() * speed * deltaTime;
                double newY = gridY + direction.getDy() * speed * deltaTime;

                double targetX = Math.round(gridX) + direction.getDx();
                double targetY = Math.round(gridY) + direction.getDy();

                if (direction.getDx() > 0 && newX > targetX) newX = targetX;
                if (direction.getDx() < 0 && newX < targetX) newX = targetX;
                if (direction.getDy() > 0 && newY > targetY) newY = targetY;
                if (direction.getDy() < 0 && newY < targetY) newY = targetY;

                gridX = newX;
                gridY = newY;
            } else {
                alignToGrid();
                direction = Direction.NONE;
                decideDirection();
            }
        }
    }

    /**
     * Abstract method for AI behavior (OOP: Polymorphism).
     * Each subclass implements its own movement strategy.
     */
    protected abstract void decideDirection();

    protected void alignToGrid() {
        gridX = Math.round(gridX);
        gridY = Math.round(gridY);
    }

    protected List<Direction> getValidDirections() {
        List<Direction> valid = new ArrayList<>();
        for (Direction dir : Direction.validDirections()) {
            int newX = getTileX() + dir.getDx();
            int newY = getTileY() + dir.getDy();
            if (gameMap != null && gameMap.canMoveTo(newX, newY, false)) {
                valid.add(dir);
            }
        }
        return valid;
    }

    protected List<Direction> getValidDirectionsNoReverse() {
        List<Direction> valid = getValidDirections();
        Direction opposite = direction.getOpposite();
        if (valid.size() > 1) valid.remove(opposite);
        return valid;
    }

    protected Direction getDirectionTowardsPlayer() {
        if (player == null) return Direction.NONE;
        double dx = player.getGridX() - gridX;
        double dy = player.getGridY() - gridY;

        if (Math.abs(dx) > Math.abs(dy)) {
            return dx > 0 ? Direction.RIGHT : Direction.LEFT;
        } else if (Math.abs(dy) > 0) {
            return dy > 0 ? Direction.DOWN : Direction.UP;
        }
        return Direction.NONE;
    }

    protected boolean canSeePlayer() {
        if (player == null || gameMap == null) return false;

        int playerTileX = player.getTileX();
        int playerTileY = player.getTileY();
        int myTileX = getTileX();
        int myTileY = getTileY();

        if (myTileX != playerTileX && myTileY != playerTileY) return false;

        if (myTileX == playerTileX) {
            int minY = Math.min(myTileY, playerTileY);
            int maxY = Math.max(myTileY, playerTileY);
            for (int y = minY + 1; y < maxY; y++) {
                if (!gameMap.canMoveTo(myTileX, y, false)) return false;
            }
        } else {
            int minX = Math.min(myTileX, playerTileX);
            int maxX = Math.max(myTileX, playerTileX);
            for (int x = minX + 1; x < maxX; x++) {
                if (!gameMap.canMoveTo(x, myTileY, false)) return false;
            }
        }
        return true;
    }

    public void freeze(double duration) {
        frozen = true;
        frozenTimer = duration;
    }

    public boolean collidesWithPlayer() {
        if (player == null || player.isJumping()) return false;
        return collidesWith(player);
    }

    @Override
    public void render(GraphicsContext gc) {
        double pixelX = getPixelX();
        double pixelY = getPixelY();

        if (frozen) {
            gc.setFill(Color.LIGHTBLUE);
        } else {
            gc.setFill(Color.web(color));
        }

        renderGhostBody(gc, pixelX, pixelY);
        renderEyes(gc, pixelX, pixelY);

        if (frozen) {
            gc.setStroke(Color.CYAN);
            gc.setLineWidth(2);
            gc.strokeOval(pixelX - ENEMY_RADIUS - 2, pixelY - ENEMY_RADIUS - 2,
                    (ENEMY_RADIUS + 2) * 2, (ENEMY_RADIUS + 2) * 2);
        }
    }

    protected void renderGhostBody(GraphicsContext gc, double x, double y) {
        gc.fillArc(x - ENEMY_RADIUS, y - ENEMY_RADIUS, ENEMY_RADIUS * 2, ENEMY_RADIUS * 2,
                0, 180, javafx.scene.shape.ArcType.ROUND);
        gc.fillRect(x - ENEMY_RADIUS, y, ENEMY_RADIUS * 2, ENEMY_RADIUS * 0.7);

        double waveY = y + ENEMY_RADIUS * 0.7;
        double waveWidth = ENEMY_RADIUS * 2 / 3.0;
        for (int i = 0; i < 3; i++) {
            gc.fillOval(x - ENEMY_RADIUS + i * waveWidth, waveY - waveWidth / 4, waveWidth, waveWidth / 2);
        }
    }

    protected void renderEyes(GraphicsContext gc, double x, double y) {
        gc.setFill(Color.WHITE);
        gc.fillOval(x - ENEMY_RADIUS * 0.5, y - ENEMY_RADIUS * 0.4, ENEMY_RADIUS * 0.4, ENEMY_RADIUS * 0.5);
        gc.fillOval(x + ENEMY_RADIUS * 0.1, y - ENEMY_RADIUS * 0.4, ENEMY_RADIUS * 0.4, ENEMY_RADIUS * 0.5);

        gc.setFill(Color.BLUE);
        double pupilOffsetX = direction.getDx() * 2;
        double pupilOffsetY = direction.getDy() * 2;
        gc.fillOval(x - ENEMY_RADIUS * 0.4 + pupilOffsetX, y - ENEMY_RADIUS * 0.3 + pupilOffsetY,
                ENEMY_RADIUS * 0.2, ENEMY_RADIUS * 0.3);
        gc.fillOval(x + ENEMY_RADIUS * 0.2 + pupilOffsetX, y - ENEMY_RADIUS * 0.3 + pupilOffsetY,
                ENEMY_RADIUS * 0.2, ENEMY_RADIUS * 0.3);
    }

    public boolean isFrozen() { return frozen; }
    public String getColor() { return color; }
}
