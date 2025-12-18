package com.pacman.item;

import com.pacman.entity.Entity;
import com.pacman.entity.Player;
import com.pacman.util.Constants;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * 道具基类
 * 所有道具的父类
 */
public abstract class Item extends Entity {
    
    /** 道具类型 */
    protected ItemType type;
    
    /** 是否已被收集 */
    protected boolean collected;
    
    /** 道具半径 */
    protected static final int ITEM_RADIUS = Constants.TILE_SIZE / 3;
    
    /** 动画计时器（用于浮动效果） */
    protected double animationTimer;
    
    /**
     * 构造函数
     * @param gridX 格子X坐标
     * @param gridY 格子Y坐标
     * @param type 道具类型
     */
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
        if (collected) {
            return;
        }
        
        double pixelX = getPixelX();
        double pixelY = getPixelY();
        
        // 浮动效果
        double floatOffset = Math.sin(animationTimer * 3) * 3;
        pixelY += floatOffset;
        
        // 绘制光晕
        gc.setFill(Color.web(type.getColor(), 0.3));
        gc.fillOval(
                pixelX - ITEM_RADIUS - 4,
                pixelY - ITEM_RADIUS - 4,
                (ITEM_RADIUS + 4) * 2,
                (ITEM_RADIUS + 4) * 2
        );
        
        // 绘制道具主体
        gc.setFill(Color.web(type.getColor()));
        gc.fillOval(
                pixelX - ITEM_RADIUS,
                pixelY - ITEM_RADIUS,
                ITEM_RADIUS * 2,
                ITEM_RADIUS * 2
        );
        
        // 绘制道具图标
        renderIcon(gc, pixelX, pixelY);
    }
    
    /**
     * 绘制道具图标（由子类实现）
     * @param gc 图形上下文
     * @param x 中心X坐标
     * @param y 中心Y坐标
     */
    protected abstract void renderIcon(GraphicsContext gc, double x, double y);
    
    /**
     * 应用道具效果（由子类实现）
     * @param player 玩家
     */
    public abstract void applyEffect(Player player);
    
    /**
     * 收集道具
     * @param player 收集的玩家
     */
    public void collect(Player player) {
        if (!collected) {
            collected = true;
            active = false;
            applyEffect(player);
        }
    }
    
    /**
     * 检查是否可被玩家收集
     * @param player 玩家
     * @return 是否可收集
     */
    public boolean canBeCollectedBy(Player player) {
        if (collected) {
            return false;
        }
        
        double dx = this.gridX - player.getGridX();
        double dy = this.gridY - player.getGridY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        return distance < 0.6;
    }
    
    // ==================== Getter ====================
    
    public ItemType getType() {
        return type;
    }
    
    public boolean isCollected() {
        return collected;
    }
}
