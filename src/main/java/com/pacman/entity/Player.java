package com.pacman.entity;

import com.pacman.item.ItemType;
import com.pacman.map.GameMap;
import com.pacman.util.Constants;
import com.pacman.util.Direction;
import com.pacman.util.SkinManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * Player class extending Entity (OOP: Inheritance).
 * Handles player movement, item effects, and rendering.
 */
public class Player extends Entity {

    private Direction nextDirection;
    private Map<ItemType, Double> activeEffects;
    private boolean hasShield;
    private boolean isJumping;
    private double jumpTargetX;
    private double jumpTargetY;
    private double jumpProgress;
    private boolean isBlinded;
    private double blindTimer;
    private double speedModifier;
    private boolean onIce;
    private Direction iceDirection;
    private Direction lastFacingDirection;
    private double portalCooldown;
    private double invincibleTimer;
    private GameMap gameMap;

    public Player(double gridX, double gridY) {
        super(gridX, gridY);
        this.speed = Constants.PLAYER_BASE_SPEED;
        this.collisionRadius = Constants.PLAYER_RADIUS;
        this.nextDirection = Direction.NONE;
        this.activeEffects = new HashMap<>();
        this.hasShield = false;
        this.isJumping = false;
        this.isBlinded = false;
        this.blindTimer = 0;
        this.speedModifier = 1.0;
        this.onIce = false;
        this.iceDirection = Direction.NONE;
        this.lastFacingDirection = Direction.DOWN;
        this.portalCooldown = 0;
        this.invincibleTimer = 0;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    @Override
    public void update(double deltaTime) {
        updateEffects(deltaTime);

        if (portalCooldown > 0) portalCooldown -= deltaTime;
        if (invincibleTimer > 0) invincibleTimer -= deltaTime;

        if (isBlinded) {
            blindTimer -= deltaTime;
            if (blindTimer <= 0) {
                isBlinded = false;
                blindTimer = 0;
            }
        }

        if (isJumping) {
            updateJump(deltaTime);
            return;
        }

        double actualSpeed = speed * speedModifier;
        tryChangeDirection();

        if (direction != Direction.NONE || (onIce && iceDirection != Direction.NONE)) {
            Direction moveDir = onIce ? iceDirection : direction;
            lastFacingDirection = moveDir;

            boolean canWallPass = hasEffect(ItemType.WALL_PASS);
            int nextTileX = getTileX() + moveDir.getDx();
            int nextTileY = getTileY() + moveDir.getDy();

            double distToNextTileX = (moveDir.getDx() != 0) ?
                    (moveDir.getDx() > 0 ? Math.ceil(gridX) - gridX : gridX - Math.floor(gridX)) : 1.0;
            double distToNextTileY = (moveDir.getDy() != 0) ?
                    (moveDir.getDy() > 0 ? Math.ceil(gridY) - gridY : gridY - Math.floor(gridY)) : 1.0;
            double distToEdge = Math.min(distToNextTileX, distToNextTileY);

            Direction fromDirection = moveDir.getOpposite();

            if (distToEdge < 0.1 && gameMap != null && !gameMap.canMoveTo(nextTileX, nextTileY, fromDirection, canWallPass)) {
                alignToGrid();
                if (onIce) {
                    iceDirection = Direction.NONE;
                } else {
                    direction = Direction.NONE;
                }
                if (nextDirection == moveDir) {
                    nextDirection = Direction.NONE;
                }
            } else {
                double newX = gridX + moveDir.getDx() * actualSpeed * deltaTime;
                double newY = gridY + moveDir.getDy() * actualSpeed * deltaTime;

                if (gameMap != null && gameMap.canMoveTo(newX, newY, fromDirection, canWallPass)) {
                    gridX = newX;
                    gridY = newY;
                    handleMapBoundary();
                } else if (onIce) {
                    alignToGrid();
                    iceDirection = Direction.NONE;
                } else {
                    alignToGrid();
                    direction = Direction.NONE;
                    if (nextDirection == moveDir) {
                        nextDirection = Direction.NONE;
                    }
                }
            }
        }
        speedModifier = 1.0;
    }

    private void updateJump(double deltaTime) {
        jumpProgress += deltaTime * 3;
        if (jumpProgress >= 1.0) {
            gridX = jumpTargetX;
            gridY = jumpTargetY;
            isJumping = false;
            jumpProgress = 0;
        }
    }

    public void startJump(double targetX, double targetY) {
        isJumping = true;
        jumpTargetX = targetX;
        jumpTargetY = targetY;
        jumpProgress = 0;
    }

    /**
     * Attempts to change direction based on pre-input.
     */
    private void tryChangeDirection() {
        if (nextDirection == Direction.NONE || gameMap == null) return;
        if (onIce && iceDirection != Direction.NONE) return;
        if (nextDirection == direction) {
            nextDirection = Direction.NONE;
            return;
        }

        double centerDist = Math.abs(gridX - Math.round(gridX)) + Math.abs(gridY - Math.round(gridY));
        if (centerDist < 0.15) {
            int testX = getTileX() + nextDirection.getDx();
            int testY = getTileY() + nextDirection.getDy();

            boolean canWallPass = hasEffect(ItemType.WALL_PASS);
            Direction fromDirection = nextDirection.getOpposite();
            if (gameMap.canMoveTo(testX, testY, fromDirection, canWallPass)) {
                direction = nextDirection;
                if (onIce) iceDirection = nextDirection;
                nextDirection = Direction.NONE;
                alignToGrid();
            }
        }
    }

    private void alignToGrid() {
        gridX = Math.round(gridX);
        gridY = Math.round(gridY);
    }

    private void handleMapBoundary() {
        if (gridX < 0) gridX = Constants.MAP_COLS - 1;
        if (gridX >= Constants.MAP_COLS) gridX = 0;
        if (gridY < 0) gridY = Constants.MAP_ROWS - 1;
        if (gridY >= Constants.MAP_ROWS) gridY = 0;
    }

    private void updateEffects(double deltaTime) {
        activeEffects.entrySet().removeIf(entry -> {
            double newTime = entry.getValue() - deltaTime;
            if (newTime <= 0) {
                onEffectEnd(entry.getKey());
                return true;
            }
            entry.setValue(newTime);
            return false;
        });
    }

    /**
     * Handles item effect expiration.
     */
    private void onEffectEnd(ItemType type) {
        if (type == ItemType.WALL_PASS && gameMap != null) {
            int tileX = getTileX();
            int tileY = getTileY();
            if (!gameMap.canMoveTo(tileX, tileY, false)) {
                int[] safePos = findNearestSafePosition(tileX, tileY);
                if (safePos != null) {
                    gridX = safePos[0];
                    gridY = safePos[1];
                }
            }
        }
    }

    private int[] findNearestSafePosition(int startX, int startY) {
        for (int radius = 1; radius <= 5; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    if (Math.abs(dx) != radius && Math.abs(dy) != radius) continue;
                    int x = startX + dx;
                    int y = startY + dy;
                    if (gameMap.canMoveTo(x, y, false)) {
                        return new int[]{x, y};
                    }
                }
            }
        }
        return null;
    }

    public void addEffect(ItemType type, double duration) {
        if (type == ItemType.SHIELD) {
            hasShield = true;
        } else {
            activeEffects.put(type, duration);
        }
    }

    public boolean hasEffect(ItemType type) {
        if (type == ItemType.SHIELD) return hasShield;
        return activeEffects.containsKey(type);
    }

    public boolean consumeShield() {
        if (hasShield) {
            hasShield = false;
            invincibleTimer = 1.0;
            return true;
        }
        return false;
    }

    public boolean isInvincible() {
        return invincibleTimer > 0;
    }

    public void applyBlind(double duration) {
        isBlinded = true;
        blindTimer = duration;
    }

    public void applySpeedModifier(double modifier) {
        this.speedModifier = modifier;
    }

    public void setOnIce(boolean onIce) {
        boolean wasOnIce = this.onIce;
        this.onIce = onIce;
        if (onIce && !wasOnIce && direction != Direction.NONE) {
            iceDirection = direction;
        } else if (!onIce && wasOnIce) {
            iceDirection = Direction.NONE;
        }
    }

    public void setNextDirection(Direction direction) {
        this.nextDirection = direction;
        if (this.direction == Direction.NONE && !onIce) {
            this.direction = direction;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        double pixelX = getPixelX();
        double pixelY = getPixelY();

        // Jump animation
        if (isJumping) {
            double startX = (gridX + 0.5) * Constants.TILE_SIZE;
            double startY = (gridY + 0.5) * Constants.TILE_SIZE;
            double endX = (jumpTargetX + 0.5) * Constants.TILE_SIZE;
            double endY = (jumpTargetY + 0.5) * Constants.TILE_SIZE;
            pixelX = startX + (endX - startX) * jumpProgress;
            pixelY = startY + (endY - startY) * jumpProgress;
            double jumpHeight = Math.sin(jumpProgress * Math.PI) * Constants.TILE_SIZE;
            pixelY -= jumpHeight;
        }

        // Shield effect
        if (hasShield) {
            gc.setFill(Color.web(Constants.COLOR_ITEM_SHIELD, 0.3));
            double shieldSize = SkinManager.getDisplaySize() * 0.6;
            gc.fillOval(pixelX - shieldSize - 5, pixelY - shieldSize - 5, (shieldSize + 5) * 2, (shieldSize + 5) * 2);
        }

        Direction facingDir = (direction != Direction.NONE) ? direction : lastFacingDirection;
        Image playerImage = SkinManager.getInstance().getImage(facingDir);

        if (playerImage != null) {
            double imgWidth = playerImage.getWidth();
            double imgHeight = playerImage.getHeight();
            gc.drawImage(playerImage, pixelX - imgWidth / 2, pixelY - imgHeight / 2);
        } else {
            renderFallback(gc, pixelX, pixelY, facingDir);
        }

        // Wall pass effect
        if (hasEffect(ItemType.WALL_PASS)) {
            gc.setStroke(Color.web(Constants.COLOR_ITEM_WALL_PASS, 0.7));
            gc.setLineWidth(2);
            double effectSize = SkinManager.getDisplaySize() * 0.5;
            gc.strokeOval(pixelX - effectSize - 3, pixelY - effectSize - 3, (effectSize + 3) * 2, (effectSize + 3) * 2);
        }

        // Magnet range indicator
        if (hasEffect(ItemType.MAGNET)) {
            gc.setStroke(Color.web(Constants.COLOR_ITEM_MAGNET, 0.3));
            gc.setLineWidth(1);
            double magnetRadius = Constants.MAGNET_RANGE * Constants.TILE_SIZE;
            gc.strokeOval(pixelX - magnetRadius, pixelY - magnetRadius, magnetRadius * 2, magnetRadius * 2);
        }
    }

    /**
     * Fallback rendering when skin images are unavailable.
     */
    private void renderFallback(GraphicsContext gc, double pixelX, double pixelY, Direction facingDir) {
        gc.setFill(Color.web(Constants.COLOR_PLAYER));
        gc.fillOval(pixelX - Constants.PLAYER_RADIUS, pixelY - Constants.PLAYER_RADIUS,
                Constants.PLAYER_RADIUS * 2, Constants.PLAYER_RADIUS * 2);

        gc.setFill(Color.web(Constants.COLOR_FLOOR));
        double mouthAngle = switch (facingDir) {
            case RIGHT -> 0;
            case DOWN -> 90;
            case LEFT -> 180;
            case UP -> 270;
            default -> 0;
        };
        gc.fillArc(pixelX - Constants.PLAYER_RADIUS, pixelY - Constants.PLAYER_RADIUS,
                Constants.PLAYER_RADIUS * 2, Constants.PLAYER_RADIUS * 2, mouthAngle - 30, 60,
                javafx.scene.shape.ArcType.ROUND);
    }

    // Getters
    public boolean isJumping() { return isJumping; }
    public boolean isBlinded() { return isBlinded; }
    public boolean hasShield() { return hasShield; }
    public Direction getNextDirection() { return nextDirection; }
    public boolean canTeleport() { return portalCooldown <= 0; }
    public void setPortalCooldown(double cooldown) { this.portalCooldown = cooldown; }
}