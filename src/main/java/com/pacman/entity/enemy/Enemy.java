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
 * 敌人基类
 * 所有敌人类型的父类，体现OOP的继承和多态
 */
public abstract class Enemy extends Entity {
    
    /** 随机数生成器 */
    protected static final Random random = new Random();
    
    /** 敌人颜色 */
    protected String color;
    
    /** 游戏地图引用 */
    protected GameMap gameMap;
    
    /** 玩家引用 */
    protected Player player;
    
    /** 敌人半径 */
    protected static final int ENEMY_RADIUS = Constants.TILE_SIZE / 2 - 2;
    
    /** 是否被冻结 */
    protected boolean frozen;
    
    /** 冻结剩余时间 */
    protected double frozenTimer;
    
    /** 移动计时器（用于控制移动频率） */
    protected double moveTimer;
    
    /** 移动间隔（秒） */
    protected double moveInterval;
    
    /**
     * 构造函数
     * @param gridX 初始X坐标
     * @param gridY 初始Y坐标
     * @param speed 移动速度
     * @param color 敌人颜色
     */
    public Enemy(double gridX, double gridY, double speed, String color) {
        super(gridX, gridY);
        this.speed = speed;
        this.color = color;
        this.collisionRadius = ENEMY_RADIUS;
        this.frozen = false;
        this.frozenTimer = 0;
        this.moveTimer = 0;
        this.moveInterval = 0.2; // 默认每0.2秒决定一次移动方向
    }
    
    /**
     * 设置游戏地图引用
     * @param gameMap 游戏地图
     */
    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }
    
    /**
     * 设置玩家引用
     * @param player 玩家
     */
    public void setPlayer(Player player) {
        this.player = player;
    }
    
    @Override
    public void update(double deltaTime) {
        // 处理冻结状态
        if (frozen) {
            frozenTimer -= deltaTime;
            if (frozenTimer <= 0) {
                frozen = false;
                frozenTimer = 0;
            }
            return;
        }

        // 检查是否接近格子中心
        double centerDist = Math.abs(gridX - Math.round(gridX)) + Math.abs(gridY - Math.round(gridY));

        // 只有接近格子中心时才决定新方向
        if (centerDist < 0.1) {
            // 更新移动计时器
            moveTimer += deltaTime;

            // 决定移动方向
            if (moveTimer >= moveInterval) {
                moveTimer = 0;
                alignToGrid(); // 确保对齐
                decideDirection();
            }
        }

        // 执行移动
        if (direction != Direction.NONE) {
            // 计算目标格子
            int targetTileX = getTileX() + direction.getDx();
            int targetTileY = getTileY() + direction.getDy();

            // 先检查目标格子是否可达
            if (gameMap != null && gameMap.canMoveTo(targetTileX, targetTileY, false)) {
                double newX = gridX + direction.getDx() * speed * deltaTime;
                double newY = gridY + direction.getDy() * speed * deltaTime;

                // 计算移动后是否超过了目标格子中心
                double targetX = Math.round(gridX) + direction.getDx();
                double targetY = Math.round(gridY) + direction.getDy();

                // 防止越过目标格子
                if (direction.getDx() > 0 && newX > targetX) newX = targetX;
                if (direction.getDx() < 0 && newX < targetX) newX = targetX;
                if (direction.getDy() > 0 && newY > targetY) newY = targetY;
                if (direction.getDy() < 0 && newY < targetY) newY = targetY;

                gridX = newX;
                gridY = newY;
            } else {
                // 前方不可通行，停止并重新决定方向
                alignToGrid();
                direction = Direction.NONE;
                decideDirection();
            }
        }
    }
    
    /**
     * 决定移动方向（抽象方法，由子类实现不同AI）
     * 这是多态的核心体现
     */
    protected abstract void decideDirection();
    
    /**
     * 对齐到格子中心
     */
    protected void alignToGrid() {
        gridX = Math.round(gridX);
        gridY = Math.round(gridY);
    }
    
    /**
     * 获取可移动的方向列表
     * @return 可移动方向列表
     */
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
    
    /**
     * 获取不包含当前反方向的可移动方向
     * @return 可移动方向列表（不走回头路）
     */
    protected List<Direction> getValidDirectionsNoReverse() {
        List<Direction> valid = getValidDirections();
        Direction opposite = direction.getOpposite();
        
        // 如果有其他选择，移除反方向
        if (valid.size() > 1) {
            valid.remove(opposite);
        }
        
        return valid;
    }
    
    /**
     * 计算到玩家的方向
     * @return 朝向玩家的最佳方向
     */
    protected Direction getDirectionTowardsPlayer() {
        if (player == null) {
            return Direction.NONE;
        }
        
        double dx = player.getGridX() - gridX;
        double dy = player.getGridY() - gridY;
        
        // 优先选择距离更远的轴向
        if (Math.abs(dx) > Math.abs(dy)) {
            return dx > 0 ? Direction.RIGHT : Direction.LEFT;
        } else if (Math.abs(dy) > 0) {
            return dy > 0 ? Direction.DOWN : Direction.UP;
        }
        
        return Direction.NONE;
    }
    
    /**
     * 检查是否能看到玩家（直线无障碍）
     * @return 是否能看到玩家
     */
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
    
    /**
     * 冻结敌人
     * @param duration 冻结时间
     */
    public void freeze(double duration) {
        frozen = true;
        frozenTimer = duration;
    }
    
    /**
     * 检查是否与玩家碰撞
     * @return 是否碰撞
     */
    public boolean collidesWithPlayer() {
        if (player == null || player.isJumping()) {
            return false; // 玩家跳跃时不会碰撞
        }
        return collidesWith(player);
    }
    
    @Override
    public void render(GraphicsContext gc) {
        double pixelX = getPixelX();
        double pixelY = getPixelY();
        
        // 冻结效果
        if (frozen) {
            gc.setFill(Color.LIGHTBLUE);
        } else {
            gc.setFill(Color.web(color));
        }
        
        // 绘制身体（幽灵形状）
        renderGhostBody(gc, pixelX, pixelY);
        
        // 绘制眼睛
        renderEyes(gc, pixelX, pixelY);
        
        // 冻结时绘制冰晶效果
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
     * 绘制幽灵身体
     * @param gc 图形上下文
     * @param x 中心X坐标
     * @param y 中心Y坐标
     */
    protected void renderGhostBody(GraphicsContext gc, double x, double y) {
        // 上半部分（半圆）
        gc.fillArc(
                x - ENEMY_RADIUS,
                y - ENEMY_RADIUS,
                ENEMY_RADIUS * 2,
                ENEMY_RADIUS * 2,
                0, 180,
                javafx.scene.shape.ArcType.ROUND
        );
        
        // 下半部分（矩形 + 波浪底部）
        gc.fillRect(
                x - ENEMY_RADIUS,
                y,
                ENEMY_RADIUS * 2,
                ENEMY_RADIUS * 0.7
        );
        
        // 波浪底部
        double waveY = y + ENEMY_RADIUS * 0.7;
        double waveWidth = ENEMY_RADIUS * 2 / 3.0;
        for (int i = 0; i < 3; i++) {
            gc.fillOval(
                    x - ENEMY_RADIUS + i * waveWidth,
                    waveY - waveWidth / 4,
                    waveWidth,
                    waveWidth / 2
            );
        }
    }
    
    /**
     * 绘制眼睛
     * @param gc 图形上下文
     * @param x 中心X坐标
     * @param y 中心Y坐标
     */
    protected void renderEyes(GraphicsContext gc, double x, double y) {
        // 眼白
        gc.setFill(Color.WHITE);
        gc.fillOval(x - ENEMY_RADIUS * 0.5, y - ENEMY_RADIUS * 0.4, ENEMY_RADIUS * 0.4, ENEMY_RADIUS * 0.5);
        gc.fillOval(x + ENEMY_RADIUS * 0.1, y - ENEMY_RADIUS * 0.4, ENEMY_RADIUS * 0.4, ENEMY_RADIUS * 0.5);
        
        // 眼球（根据移动方向）
        gc.setFill(Color.BLUE);
        double pupilOffsetX = direction.getDx() * 2;
        double pupilOffsetY = direction.getDy() * 2;
        gc.fillOval(x - ENEMY_RADIUS * 0.4 + pupilOffsetX, y - ENEMY_RADIUS * 0.3 + pupilOffsetY, ENEMY_RADIUS * 0.2, ENEMY_RADIUS * 0.3);
        gc.fillOval(x + ENEMY_RADIUS * 0.2 + pupilOffsetX, y - ENEMY_RADIUS * 0.3 + pupilOffsetY, ENEMY_RADIUS * 0.2, ENEMY_RADIUS * 0.3);
    }
    
    // ==================== Getter ====================
    
    public boolean isFrozen() {
        return frozen;
    }
    
    public String getColor() {
        return color;
    }
}
