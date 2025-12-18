package com.pacman.entity;

import com.pacman.util.Constants;
import com.pacman.util.Direction;
import javafx.scene.canvas.GraphicsContext;

/**
 * 游戏实体基类
 * 所有游戏中可移动或可交互的对象都继承此类
 * 体现OOP的继承和封装原则
 */
public abstract class Entity {
    
    /** 实体在地图中的X坐标（格子坐标） */
    protected double gridX;
    
    /** 实体在地图中的Y坐标（格子坐标） */
    protected double gridY;
    
    /** 实体的移动速度（格/秒） */
    protected double speed;
    
    /** 实体当前的移动方向 */
    protected Direction direction;
    
    /** 实体是否存活/有效 */
    protected boolean active;
    
    /** 实体的碰撞半径（像素） */
    protected double collisionRadius;
    
    /**
     * 构造函数
     * @param gridX 初始X坐标（格子）
     * @param gridY 初始Y坐标（格子）
     */
    public Entity(double gridX, double gridY) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.speed = 0;
        this.direction = Direction.NONE;
        this.active = true;
        this.collisionRadius = Constants.TILE_SIZE / 2.0 - 2;
    }
    
    /**
     * 更新实体状态（抽象方法，由子类实现）
     * @param deltaTime 距离上一帧的时间（秒）
     */
    public abstract void update(double deltaTime);
    
    /**
     * 渲染实体（抽象方法，由子类实现）
     * @param gc 图形上下文
     */
    public abstract void render(GraphicsContext gc);
    
    /**
     * 获取实体的像素X坐标（中心点）
     * @return 像素X坐标
     */
    public double getPixelX() {
        return (gridX + 0.5) * Constants.TILE_SIZE;
    }
    
    /**
     * 获取实体的像素Y坐标（中心点）
     * @return 像素Y坐标
     */
    public double getPixelY() {
        return (gridY + 0.5) * Constants.TILE_SIZE;
    }
    
    /**
     * 获取实体所在的格子X坐标（整数）
     * @return 格子X坐标
     */
    public int getTileX() {
        return (int) Math.round(gridX);
    }
    
    /**
     * 获取实体所在的格子Y坐标（整数）
     * @return 格子Y坐标
     */
    public int getTileY() {
        return (int) Math.round(gridY);
    }
    
    /**
     * 检测与另一个实体的碰撞
     * @param other 另一个实体
     * @return 是否发生碰撞
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
    
    /**
     * 检测与指定格子的碰撞
     * @param tileX 格子X坐标
     * @param tileY 格子Y坐标
     * @return 是否在该格子范围内
     */
    public boolean isOnTile(int tileX, int tileY) {
        return getTileX() == tileX && getTileY() == tileY;
    }
    
    /**
     * 计算到另一个实体的距离（格子距离）
     * @param other 另一个实体
     * @return 距离
     */
    public double distanceTo(Entity other) {
        double dx = this.gridX - other.gridX;
        double dy = this.gridY - other.gridY;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    /**
     * 计算到指定格子的距离
     * @param tileX 目标格子X
     * @param tileY 目标格子Y
     * @return 距离
     */
    public double distanceToTile(int tileX, int tileY) {
        double dx = this.gridX - tileX;
        double dy = this.gridY - tileY;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    // ==================== Getter 和 Setter ====================
    
    public double getGridX() {
        return gridX;
    }
    
    public void setGridX(double gridX) {
        this.gridX = gridX;
    }
    
    public double getGridY() {
        return gridY;
    }
    
    public void setGridY(double gridY) {
        this.gridY = gridY;
    }
    
    public double getSpeed() {
        return speed;
    }
    
    public void setSpeed(double speed) {
        this.speed = speed;
    }
    
    public Direction getDirection() {
        return direction;
    }
    
    public void setDirection(Direction direction) {
        this.direction = direction;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public double getCollisionRadius() {
        return collisionRadius;
    }
    
    public void setCollisionRadius(double collisionRadius) {
        this.collisionRadius = collisionRadius;
    }
}
