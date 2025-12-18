package com.pacman.entity.enemy;

import com.pacman.util.Constants;
import com.pacman.util.Direction;

import java.util.List;

/**
 * 追踪者敌人
 * AI行为：直线向玩家方向移动（上下左右）
 */
public class Chaser extends Enemy {
    
    /**
     * 构造函数
     * @param gridX 初始X坐标
     * @param gridY 初始Y坐标
     */
    public Chaser(double gridX, double gridY) {
        super(gridX, gridY, Constants.CHASER_SPEED, Constants.COLOR_CHASER);
    }
    
    @Override
    protected void decideDirection() {
        if (player == null) {
            // 没有玩家时随机移动
            List<Direction> validDirs = getValidDirections();
            if (!validDirs.isEmpty()) {
                direction = validDirs.get(random.nextInt(validDirs.size()));
            }
            return;
        }
        
        // 获取朝向玩家的方向
        Direction towardsPlayer = getDirectionTowardsPlayer();
        
        // 获取可移动的方向（不走回头路）
        List<Direction> validDirs = getValidDirectionsNoReverse();
        
        if (validDirs.isEmpty()) {
            // 死路，只能掉头
            validDirs = getValidDirections();
        }
        
        if (validDirs.isEmpty()) {
            direction = Direction.NONE;
            return;
        }
        
        // 优先选择朝向玩家的方向
        if (validDirs.contains(towardsPlayer)) {
            direction = towardsPlayer;
            return;
        }
        
        // 如果不能直接朝向玩家，选择能缩短距离的方向
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
    }
}
