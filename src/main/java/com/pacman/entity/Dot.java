package com.pacman.entity;

import com.pacman.util.Constants;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * 豆子类
 * 玩家需要收集的基本目标
 */
public class Dot extends Entity {
    
    /** 豆子半径 */
    private static final int DOT_RADIUS = 4;
    
    /** 是否已被收集 */
    private boolean collected;
    
    /**
     * 构造函数
     * @param gridX 格子X坐标
     * @param gridY 格子Y坐标
     */
    public Dot(int gridX, int gridY) {
        super(gridX, gridY);
        this.collected = false;
        this.collisionRadius = DOT_RADIUS;
    }
    
    @Override
    public void update(double deltaTime) {
        // 豆子不需要更新逻辑
    }
    
    @Override
    public void render(GraphicsContext gc) {
        if (collected) {
            return;
        }
        
        double pixelX = getPixelX();
        double pixelY = getPixelY();
        
        // 绘制豆子
        gc.setFill(Color.web(Constants.COLOR_DOT));
        gc.fillOval(
                pixelX - DOT_RADIUS,
                pixelY - DOT_RADIUS,
                DOT_RADIUS * 2,
                DOT_RADIUS * 2
        );
    }
    
    /**
     * 收集豆子
     */
    public void collect() {
        this.collected = true;
        this.active = false;
    }
    
    /**
     * 检查是否已被收集
     * @return 是否已被收集
     */
    public boolean isCollected() {
        return collected;
    }
    
    /**
     * 检查玩家是否可以收集该豆子
     * @param player 玩家
     * @return 是否可以收集
     */
    public boolean canBeCollectedBy(Player player) {
        if (collected) {
            return false;
        }
        
        // 计算距离
        double dx = this.gridX - player.getGridX();
        double dy = this.gridY - player.getGridY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        return distance < 0.5; // 当玩家足够接近时收集
    }
}
