package com.pacman.entity.enemy;

import com.pacman.util.Constants;
import com.pacman.util.Direction;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * 猎手敌人
 * AI行为：平时慢速随机移动，当与玩家之间没有视觉障碍时会突然加速追击
 */
public class Hunter extends Enemy {
    
    /** 是否处于追击状态 */
    private boolean isRushing;
    
    /** 追击冷却时间 */
    private double rushCooldown;
    
    /** 追击持续时间 */
    private double rushDuration;
    
    /** 追击最大持续时间 */
    private static final double MAX_RUSH_DURATION = 2.0;
    
    /** 追击冷却时间 */
    private static final double RUSH_COOLDOWN_TIME = 1.5;
    
    /**
     * 构造函数
     * @param gridX 初始X坐标
     * @param gridY 初始Y坐标
     */
    public Hunter(double gridX, double gridY) {
        super(gridX, gridY, Constants.HUNTER_BASE_SPEED, Constants.COLOR_HUNTER);
        this.isRushing = false;
        this.rushCooldown = 0;
        this.rushDuration = 0;
        this.moveInterval = 0.15; // 猎手反应更快
    }
    
    @Override
    public void update(double deltaTime) {
        // 更新冷却计时器
        if (rushCooldown > 0) {
            rushCooldown -= deltaTime;
        }
        
        // 检查是否应该开始追击
        if (!isRushing && rushCooldown <= 0 && canSeePlayer()) {
            startRush();
        }
        
        // 更新追击状态
        if (isRushing) {
            rushDuration += deltaTime;
            // 只有超时才停止追击，不再因为看不到玩家就停止
            // 这样猎手一旦发现玩家就会全力追击直到超时
            if (rushDuration >= MAX_RUSH_DURATION) {
                stopRush();
            }
        }
        
        // 调用父类更新
        super.update(deltaTime);
    }
    
    /**
     * 开始追击
     */
    private void startRush() {
        isRushing = true;
        rushDuration = 0;
        speed = Constants.HUNTER_RUSH_SPEED;
    }
    
    /**
     * 停止追击
     */
    private void stopRush() {
        isRushing = false;
        rushCooldown = RUSH_COOLDOWN_TIME;
        speed = Constants.HUNTER_BASE_SPEED;
    }
    
    /**
     * 检查玩家是否在猎手的视线内（考虑朝向）
     * 只有玩家在猎手面前（朝向方向）时才算在视线内
     */
    @Override
    protected boolean canSeePlayer() {
        if (player == null || gameMap == null) {
            return false;
        }

        int playerTileX = player.getTileX();
        int playerTileY = player.getTileY();
        int myTileX = getTileX();
        int myTileY = getTileY();

        // 必须在同一行或同一列
        if (myTileX != playerTileX && myTileY != playerTileY) {
            return false;
        }

        // 检查玩家是否在猎手的朝向方向上
        // 如果猎手没有方向（静止），则检查所有方向
        if (direction != Direction.NONE) {
            if (myTileX == playerTileX) {
                // 同一列，检查Y方向
                if (direction == Direction.UP && playerTileY >= myTileY) {
                    return false; // 猎手朝上，但玩家在下方或同位置
                }
                if (direction == Direction.DOWN && playerTileY <= myTileY) {
                    return false; // 猎手朝下，但玩家在上方或同位置
                }
                // 如果猎手朝左或右，但玩家在同一列的上下方，不算视线内
                if (direction == Direction.LEFT || direction == Direction.RIGHT) {
                    return false;
                }
            } else {
                // 同一行，检查X方向
                if (direction == Direction.LEFT && playerTileX >= myTileX) {
                    return false; // 猎手朝左，但玩家在右边或同位置
                }
                if (direction == Direction.RIGHT && playerTileX <= myTileX) {
                    return false; // 猎手朝右，但玩家在左边或同位置
                }
                // 如果猎手朝上或下，但玩家在同一行的左右方，不算视线内
                if (direction == Direction.UP || direction == Direction.DOWN) {
                    return false;
                }
            }
        }

        // 检查中间是否有墙
        if (myTileX == playerTileX) {
            int minY = Math.min(myTileY, playerTileY);
            int maxY = Math.max(myTileY, playerTileY);
            for (int y = minY + 1; y < maxY; y++) {
                if (!gameMap.canMoveTo(myTileX, y, false)) {
                    return false;
                }
            }
        } else {
            int minX = Math.min(myTileX, playerTileX);
            int maxX = Math.max(myTileX, playerTileX);
            for (int x = minX + 1; x < maxX; x++) {
                if (!gameMap.canMoveTo(x, myTileY, false)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    protected void decideDirection() {
        List<Direction> validDirs = getValidDirectionsNoReverse();
        
        if (validDirs.isEmpty()) {
            validDirs = getValidDirections();
        }
        
        if (validDirs.isEmpty()) {
            direction = Direction.NONE;
            return;
        }
        
        if (isRushing && player != null) {
            // 追击模式：直接朝玩家方向移动
            Direction towardsPlayer = getDirectionTowardsPlayer();
            if (validDirs.contains(towardsPlayer)) {
                direction = towardsPlayer;
                return;
            }
            
            // 选择能缩短距离的方向
            Direction bestDir = validDirs.get(0);
            double bestDist = Double.MAX_VALUE;
            
            for (Direction dir : validDirs) {
                int newX = getTileX() + dir.getDx();
                int newY = getTileY() + dir.getDy();
                double dist = Math.sqrt(
                        Math.pow(newX - player.getGridX(), 2) +
                        Math.pow(newY - player.getGridY(), 2)
                );
                
                if (dist < bestDist) {
                    bestDist = dist;
                    bestDir = dir;
                }
            }
            
            direction = bestDir;
        } else {
            // 非追击模式：类似游荡者的随机移动
            if (direction != Direction.NONE && 
                validDirs.contains(direction) && 
                random.nextDouble() < 0.6) {
                return; // 保持当前方向
            }
            direction = validDirs.get(random.nextInt(validDirs.size()));
        }
    }
    
    @Override
    public void render(GraphicsContext gc) {
        double pixelX = getPixelX();
        double pixelY = getPixelY();
        
        // 冻结效果
        if (frozen) {
            gc.setFill(Color.LIGHTBLUE);
        } else if (isRushing) {
            // 追击时颜色更亮
            gc.setFill(Color.web("#FF6600"));
        } else {
            gc.setFill(Color.web(color));
        }
        
        // 绘制身体
        renderGhostBody(gc, pixelX, pixelY);
        
        // 追击时眼睛变红
        if (isRushing) {
            renderAngryEyes(gc, pixelX, pixelY);
        } else {
            renderEyes(gc, pixelX, pixelY);
        }
        
        // 追击时添加速度线效果
        if (isRushing && !frozen) {
            gc.setStroke(Color.ORANGE);
            gc.setLineWidth(2);
            double offsetX = -direction.getDx() * 10;
            double offsetY = -direction.getDy() * 10;
            for (int i = 0; i < 3; i++) {
                gc.strokeLine(
                        pixelX + offsetX + random.nextInt(10) - 5,
                        pixelY + offsetY + random.nextInt(10) - 5,
                        pixelX + offsetX * 1.5 + random.nextInt(10) - 5,
                        pixelY + offsetY * 1.5 + random.nextInt(10) - 5
                );
            }
        }
        
        // 冻结效果
        if (frozen) {
            gc.setStroke(Color.CYAN);
            gc.setLineWidth(2);
            gc.strokeOval(
                    pixelX - ENEMY_RADIUS - 2,
                    pixelY - ENEMY_RADIUS - 2,
                    (ENEMY_RADIUS + 2) * 2,
                    (ENEMY_RADIUS + 2) * 2
            );
        }
    }
    
    /**
     * 绘制愤怒的眼睛（追击状态）
     * @param gc 图形上下文
     * @param x 中心X坐标
     * @param y 中心Y坐标
     */
    private void renderAngryEyes(GraphicsContext gc, double x, double y) {
        // 眼白（变小）
        gc.setFill(Color.WHITE);
        gc.fillOval(x - ENEMY_RADIUS * 0.45, y - ENEMY_RADIUS * 0.35, ENEMY_RADIUS * 0.35, ENEMY_RADIUS * 0.4);
        gc.fillOval(x + ENEMY_RADIUS * 0.1, y - ENEMY_RADIUS * 0.35, ENEMY_RADIUS * 0.35, ENEMY_RADIUS * 0.4);
        
        // 眼球（红色）
        gc.setFill(Color.RED);
        double pupilOffsetX = direction.getDx() * 2;
        double pupilOffsetY = direction.getDy() * 2;
        gc.fillOval(x - ENEMY_RADIUS * 0.35 + pupilOffsetX, y - ENEMY_RADIUS * 0.25 + pupilOffsetY, ENEMY_RADIUS * 0.2, ENEMY_RADIUS * 0.25);
        gc.fillOval(x + ENEMY_RADIUS * 0.2 + pupilOffsetX, y - ENEMY_RADIUS * 0.25 + pupilOffsetY, ENEMY_RADIUS * 0.2, ENEMY_RADIUS * 0.25);
    }
    
    // ==================== Getter ====================
    
    public boolean isRushing() {
        return isRushing;
    }
}
