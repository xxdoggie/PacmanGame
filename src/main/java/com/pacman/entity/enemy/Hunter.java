package com.pacman.entity.enemy;

import com.pacman.util.Constants;
import com.pacman.util.Direction;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * Hunter enemy extending Enemy (OOP: Inheritance).
 * AI: Slow random movement, rushes when player is in line of sight.
 */
public class Hunter extends Enemy {

    private boolean isRushing;
    private double rushCooldown;
    private double rushDuration;
    private static final double MAX_RUSH_DURATION = 2.0;
    private static final double RUSH_COOLDOWN_TIME = 1.5;

    public Hunter(double gridX, double gridY) {
        super(gridX, gridY, Constants.HUNTER_BASE_SPEED, Constants.COLOR_HUNTER);
        this.isRushing = false;
        this.rushCooldown = 0;
        this.rushDuration = 0;
        this.moveInterval = 0.15;
    }

    @Override
    public void update(double deltaTime) {
        if (rushCooldown > 0) rushCooldown -= deltaTime;

        if (!isRushing && rushCooldown <= 0 && canSeePlayer()) {
            startRush();
        }

        if (isRushing) {
            rushDuration += deltaTime;
            if (rushDuration >= MAX_RUSH_DURATION) {
                stopRush();
            }
        }
        super.update(deltaTime);
    }

    private void startRush() {
        isRushing = true;
        rushDuration = 0;
        speed = Constants.HUNTER_RUSH_SPEED;
    }

    private void stopRush() {
        isRushing = false;
        rushCooldown = RUSH_COOLDOWN_TIME;
        speed = Constants.HUNTER_BASE_SPEED;
    }

    /**
     * Checks if player is in hunter's line of sight (considering facing direction).
     */
    @Override
    protected boolean canSeePlayer() {
        if (player == null || gameMap == null) return false;

        int playerTileX = player.getTileX();
        int playerTileY = player.getTileY();
        int myTileX = getTileX();
        int myTileY = getTileY();

        if (myTileX != playerTileX && myTileY != playerTileY) return false;

        // Check if player is in facing direction
        if (direction != Direction.NONE) {
            if (myTileX == playerTileX) {
                if (direction == Direction.UP && playerTileY >= myTileY) return false;
                if (direction == Direction.DOWN && playerTileY <= myTileY) return false;
                if (direction == Direction.LEFT || direction == Direction.RIGHT) return false;
            } else {
                if (direction == Direction.LEFT && playerTileX >= myTileX) return false;
                if (direction == Direction.RIGHT && playerTileX <= myTileX) return false;
                if (direction == Direction.UP || direction == Direction.DOWN) return false;
            }
        }

        // Check for walls between hunter and player
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

    @Override
    protected void decideDirection() {
        List<Direction> validDirs = getValidDirectionsNoReverse();
        if (validDirs.isEmpty()) validDirs = getValidDirections();
        if (validDirs.isEmpty()) {
            direction = Direction.NONE;
            return;
        }

        if (isRushing && player != null) {
            Direction towardsPlayer = getDirectionTowardsPlayer();
            if (validDirs.contains(towardsPlayer)) {
                direction = towardsPlayer;
                return;
            }

            Direction bestDir = validDirs.get(0);
            double bestDist = Double.MAX_VALUE;
            for (Direction dir : validDirs) {
                int newX = getTileX() + dir.getDx();
                int newY = getTileY() + dir.getDy();
                double dist = Math.sqrt(Math.pow(newX - player.getGridX(), 2) + Math.pow(newY - player.getGridY(), 2));
                if (dist < bestDist) {
                    bestDist = dist;
                    bestDir = dir;
                }
            }
            direction = bestDir;
        } else {
            if (direction != Direction.NONE && validDirs.contains(direction) && random.nextDouble() < 0.6) {
                return;
            }
            direction = validDirs.get(random.nextInt(validDirs.size()));
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        double pixelX = getPixelX();
        double pixelY = getPixelY();

        if (frozen) {
            gc.setFill(Color.LIGHTBLUE);
        } else if (isRushing) {
            gc.setFill(Color.web("#FF6600"));
        } else {
            gc.setFill(Color.web(color));
        }

        renderGhostBody(gc, pixelX, pixelY);

        if (isRushing) {
            renderAngryEyes(gc, pixelX, pixelY);
        } else {
            renderEyes(gc, pixelX, pixelY);
        }

        // Rush speed lines effect
        if (isRushing && !frozen) {
            gc.setStroke(Color.ORANGE);
            gc.setLineWidth(2);
            double offsetX = -direction.getDx() * 10;
            double offsetY = -direction.getDy() * 10;
            for (int i = 0; i < 3; i++) {
                gc.strokeLine(pixelX + offsetX + random.nextInt(10) - 5, pixelY + offsetY + random.nextInt(10) - 5,
                        pixelX + offsetX * 1.5 + random.nextInt(10) - 5, pixelY + offsetY * 1.5 + random.nextInt(10) - 5);
            }
        }

        if (frozen) {
            gc.setStroke(Color.CYAN);
            gc.setLineWidth(2);
            gc.strokeOval(pixelX - ENEMY_RADIUS - 2, pixelY - ENEMY_RADIUS - 2, (ENEMY_RADIUS + 2) * 2, (ENEMY_RADIUS + 2) * 2);
        }
    }

    private void renderAngryEyes(GraphicsContext gc, double x, double y) {
        gc.setFill(Color.WHITE);
        gc.fillOval(x - ENEMY_RADIUS * 0.45, y - ENEMY_RADIUS * 0.35, ENEMY_RADIUS * 0.35, ENEMY_RADIUS * 0.4);
        gc.fillOval(x + ENEMY_RADIUS * 0.1, y - ENEMY_RADIUS * 0.35, ENEMY_RADIUS * 0.35, ENEMY_RADIUS * 0.4);

        gc.setFill(Color.RED);
        double pupilOffsetX = direction.getDx() * 2;
        double pupilOffsetY = direction.getDy() * 2;
        gc.fillOval(x - ENEMY_RADIUS * 0.35 + pupilOffsetX, y - ENEMY_RADIUS * 0.25 + pupilOffsetY, ENEMY_RADIUS * 0.2, ENEMY_RADIUS * 0.25);
        gc.fillOval(x + ENEMY_RADIUS * 0.2 + pupilOffsetX, y - ENEMY_RADIUS * 0.25 + pupilOffsetY, ENEMY_RADIUS * 0.2, ENEMY_RADIUS * 0.25);
    }

    public boolean isRushing() { return isRushing; }
}
