package com.pacman.map.tile;

import com.pacman.entity.Entity;
import com.pacman.entity.Player;
import com.pacman.util.Constants;
import com.pacman.util.Direction;
import com.pacman.util.SoundManager;
import com.pacman.util.SoundManager.SoundType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Tile class representing a single map cell (OOP: Encapsulation).
 * Different tile types produce different gameplay effects.
 */
public class Tile {
    protected int gridX, gridY;
    protected TileType type;
    protected Direction direction;
    protected Tile linkedTile;
    protected com.pacman.map.GameMap gameMap;

    public Tile(int gridX, int gridY, TileType type) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.type = type;
        this.direction = Direction.NONE;
        this.linkedTile = null;
    }

    /** Render tile to canvas */
    public void render(GraphicsContext gc) {
        double pixelX = gridX * Constants.TILE_SIZE;
        double pixelY = gridY * Constants.TILE_SIZE;

        gc.setFill(Color.web(type.getColor()));
        gc.fillRect(pixelX, pixelY, Constants.TILE_SIZE, Constants.TILE_SIZE);
        renderSpecialEffect(gc, pixelX, pixelY);
    }

    /** Render special visual effects based on tile type */
    protected void renderSpecialEffect(GraphicsContext gc, double x, double y) {
        switch (type) {
            case WALL -> renderWall(gc, x, y);
            case PORTAL -> renderPortal(gc, x, y);
            case ONE_WAY -> renderOneWay(gc, x, y);
            case ICE -> renderIce(gc, x, y);
            case JUMP_PAD -> renderJumpPad(gc, x, y);
            case SPEED_UP -> renderSpeedUp(gc, x, y);
            case SLOW_DOWN -> renderSlowDown(gc, x, y);
            case BLIND_TRAP -> renderBlindTrap(gc, x, y);
            default -> {}
        }
    }

    private void renderWall(GraphicsContext gc, double x, double y) {
        gc.setStroke(Color.web("#0066CC"));
        gc.setLineWidth(2);
        gc.strokeRect(x + 1, y + 1, Constants.TILE_SIZE - 2, Constants.TILE_SIZE - 2);
        // Highlight effect
        gc.setStroke(Color.web("#3399FF", 0.5));
        gc.strokeLine(x + 3, y + 3, x + Constants.TILE_SIZE - 3, y + 3);
        gc.strokeLine(x + 3, y + 3, x + 3, y + Constants.TILE_SIZE - 3);
    }

    private void renderPortal(GraphicsContext gc, double x, double y) {
        double centerX = x + Constants.TILE_SIZE / 2.0;
        double centerY = y + Constants.TILE_SIZE / 2.0;
        double radius = Constants.TILE_SIZE / 3.0;

        // Rotation animation
        double rotation = (System.currentTimeMillis() % 3000) / 3000.0 * Math.PI * 2;
        gc.setStroke(Color.web("#FF00FF", 0.8));
        gc.setLineWidth(2);

        for (int i = 0; i < 4; i++) {
            double angle = rotation + i * Math.PI / 2;
            double startX = centerX + Math.cos(angle) * radius * 0.5;
            double startY = centerY + Math.sin(angle) * radius * 0.5;
            double endX = centerX + Math.cos(angle) * radius;
            double endY = centerY + Math.sin(angle) * radius;
            gc.strokeLine(startX, startY, endX, endY);
        }

        // Center circle
        gc.setFill(Color.web("#FF00FF", 0.6));
        gc.fillOval(centerX - radius * 0.4, centerY - radius * 0.4, radius * 0.8, radius * 0.8);
    }

    private void renderOneWay(GraphicsContext gc, double x, double y) {
        double centerX = x + Constants.TILE_SIZE / 2.0;
        double centerY = y + Constants.TILE_SIZE / 2.0;

        gc.setFill(Color.WHITE);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);

        double arrowSize = Constants.TILE_SIZE / 3.0;
        double[] xPoints, yPoints;

        switch (direction) {
            case UP -> {
                xPoints = new double[]{centerX, centerX - arrowSize/2, centerX + arrowSize/2};
                yPoints = new double[]{centerY - arrowSize/2, centerY + arrowSize/2, centerY + arrowSize/2};
            }
            case DOWN -> {
                xPoints = new double[]{centerX, centerX - arrowSize/2, centerX + arrowSize/2};
                yPoints = new double[]{centerY + arrowSize/2, centerY - arrowSize/2, centerY - arrowSize/2};
            }
            case LEFT -> {
                xPoints = new double[]{centerX - arrowSize/2, centerX + arrowSize/2, centerX + arrowSize/2};
                yPoints = new double[]{centerY, centerY - arrowSize/2, centerY + arrowSize/2};
            }
            case RIGHT -> {
                xPoints = new double[]{centerX + arrowSize/2, centerX - arrowSize/2, centerX - arrowSize/2};
                yPoints = new double[]{centerY, centerY - arrowSize/2, centerY + arrowSize/2};
            }
            default -> {
                xPoints = new double[]{centerX, centerX - arrowSize/2, centerX + arrowSize/2};
                yPoints = new double[]{centerY - arrowSize/2, centerY + arrowSize/2, centerY + arrowSize/2};
            }
        }

        gc.fillPolygon(xPoints, yPoints, 3);
    }

    private void renderIce(GraphicsContext gc, double x, double y) {
        // Ice crystal effect
        gc.setStroke(Color.web("#FFFFFF", 0.5));
        gc.setLineWidth(1);
        gc.strokeLine(x + 5, y + 5, x + Constants.TILE_SIZE - 5, y + Constants.TILE_SIZE - 5);
        gc.strokeLine(x + Constants.TILE_SIZE - 5, y + 5, x + 5, y + Constants.TILE_SIZE - 5);
        gc.strokeLine(x + Constants.TILE_SIZE / 2, y + 3, x + Constants.TILE_SIZE / 2, y + Constants.TILE_SIZE - 3);
        gc.strokeLine(x + 3, y + Constants.TILE_SIZE / 2, x + Constants.TILE_SIZE - 3, y + Constants.TILE_SIZE / 2);
    }

    private void renderJumpPad(GraphicsContext gc, double x, double y) {
        double centerX = x + Constants.TILE_SIZE / 2.0;
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);

        // Base
        gc.strokeLine(x + 5, y + Constants.TILE_SIZE - 5, x + Constants.TILE_SIZE - 5, y + Constants.TILE_SIZE - 5);

        // Spring
        double springY = y + Constants.TILE_SIZE - 8;
        for (int i = 0; i < 3; i++) {
            gc.strokeLine(x + 10 + i * 6, springY - i * 6, x + 16 + i * 6, springY - i * 6 - 4);
            gc.strokeLine(x + 16 + i * 6, springY - i * 6 - 4, x + 10 + (i + 1) * 6, springY - (i + 1) * 6);
        }

        // Arrow indicating jump direction
        gc.setFill(Color.YELLOW);
        gc.fillPolygon(
                new double[]{centerX, centerX - 6, centerX + 6},
                new double[]{y + 8, y + 16, y + 16},
                3
        );
    }

    private void renderSpeedUp(GraphicsContext gc, double x, double y) {
        gc.setFill(Color.YELLOW);
        double centerX = x + Constants.TILE_SIZE / 2.0;
        double centerY = y + Constants.TILE_SIZE / 2.0;

        // Draw two forward arrows
        for (int i = 0; i < 2; i++) {
            double offsetX = (i - 0.5) * 10;
            gc.fillPolygon(
                    new double[]{centerX + offsetX + 8, centerX + offsetX - 4, centerX + offsetX - 4},
                    new double[]{centerY, centerY - 6, centerY + 6},
                    3
            );
        }
    }

    private void renderSlowDown(GraphicsContext gc, double x, double y) {
        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(3);

        // Draw horizontal lines for slow down effect
        for (int i = 0; i < 4; i++) {
            double lineY = y + 6 + i * 8;
            gc.strokeLine(x + 4, lineY, x + Constants.TILE_SIZE - 4, lineY);
        }
    }

    private void renderBlindTrap(GraphicsContext gc, double x, double y) {
        double centerX = x + Constants.TILE_SIZE / 2.0;
        double centerY = y + Constants.TILE_SIZE / 2.0;

        // Draw crossed-out eye icon
        gc.setStroke(Color.web("#9932CC"));
        gc.setLineWidth(2);
        gc.strokeOval(centerX - 10, centerY - 5, 20, 10);
        gc.fillOval(centerX - 4, centerY - 3, 8, 6);

        // Cross-out line
        gc.setStroke(Color.RED);
        gc.strokeLine(x + 8, y + 8, x + Constants.TILE_SIZE - 8, y + Constants.TILE_SIZE - 8);
    }

    /** Called when an entity steps on this tile */
    public void onStep(Entity entity) {
        if (!(entity instanceof Player player)) {
            handleNonPlayerStep(entity);
            return;
        }

        // Reset ice state if not on ice tile
        if (type != TileType.ICE) {
            player.setOnIce(false);
        }

        switch (type) {
            case ICE -> player.setOnIce(true);
            case SPEED_UP -> {
                player.applySpeedModifier(Constants.SPEED_UP_MULTIPLIER);
                SoundManager.getInstance().play(SoundType.SPEED_UP);
            }
            case SLOW_DOWN -> {
                player.applySpeedModifier(Constants.SLOW_DOWN_MULTIPLIER);
                SoundManager.getInstance().play(SoundType.SLOW_DOWN);
            }
            case BLIND_TRAP -> player.applyBlind(Constants.BLIND_DURATION);
            case JUMP_PAD -> handleJumpPad(player);
            case PORTAL -> handlePortal(player);
            default -> {}
        }
    }

    /** Called when an entity leaves this tile */
    public void onLeave(Entity entity) {
        if (entity instanceof Player player) {
            if (type == TileType.ICE) {
                player.setOnIce(false);
            }
        }
    }

    /** Handle non-player entity stepping on tile */
    private void handleNonPlayerStep(Entity entity) {
        switch (type) {
            case SPEED_UP -> entity.setSpeed(entity.getSpeed() * Constants.SPEED_UP_MULTIPLIER);
            case SLOW_DOWN -> entity.setSpeed(entity.getSpeed() * Constants.SLOW_DOWN_MULTIPLIER);
            case PORTAL -> {
                if (linkedTile != null) {
                    entity.setGridX(linkedTile.getGridX());
                    entity.setGridY(linkedTile.getGridY());
                }
            }
            default -> {}
        }
    }

    /** Handle jump pad effect - search for nearest landing spot (can cross walls) */
    private void handleJumpPad(Player player) {
        if (player.isJumping()) return;

        Direction jumpDir = (direction != Direction.NONE) ? direction : player.getDirection();
        if (jumpDir == Direction.NONE) return;

        int targetX = -1;
        int targetY = -1;
        int maxSearchDistance = Math.max(Constants.MAP_COLS, Constants.MAP_ROWS);

        for (int dist = 1; dist <= maxSearchDistance; dist++) {
            int testX = gridX + jumpDir.getDx() * dist;
            int testY = gridY + jumpDir.getDy() * dist;

            // Boundary check
            if (testX < 0 || testX >= Constants.MAP_COLS || testY < 0 || testY >= Constants.MAP_ROWS) {
                break;
            }

            // Check for valid landing spot (not a wall)
            if (gameMap != null && gameMap.canMoveTo(testX, testY, false)) {
                targetX = testX;
                targetY = testY;
                break;
            }
        }

        if (targetX != -1 && targetY != -1) {
            player.startJump(targetX, targetY);
            SoundManager.getInstance().play(SoundType.JUMP);
        }
    }

    /** Handle portal teleportation */
    private void handlePortal(Player player) {
        if (linkedTile != null && player.canTeleport()) {
            player.setGridX(linkedTile.getGridX());
            player.setGridY(linkedTile.getGridY());
            player.setPortalCooldown(0.5);
            SoundManager.getInstance().play(SoundType.TELEPORT);
        }
    }

    /** Check if entry from given direction is allowed */
    public boolean canEnterFrom(Direction fromDirection) {
        if (!type.isWalkable()) return false;

        // One-way tile check
        if (type == TileType.ONE_WAY) {
            return fromDirection == direction.getOpposite();
        }
        return true;
    }

    // Getters and Setters
    public int getGridX() { return gridX; }
    public int getGridY() { return gridY; }
    public TileType getType() { return type; }
    public Direction getDirection() { return direction; }
    public void setDirection(Direction direction) { this.direction = direction; }
    public Tile getLinkedTile() { return linkedTile; }
    public void setLinkedTile(Tile linkedTile) { this.linkedTile = linkedTile; }
    public void setGameMap(com.pacman.map.GameMap gameMap) { this.gameMap = gameMap; }
    public boolean isWalkable() { return type.isWalkable(); }
}
