package com.pacman.entity.enemy;

import com.pacman.util.Constants;
import com.pacman.util.Direction;

import java.util.List;

/**
 * 游荡者敌人
 * AI行为：随机方向移动，没有固定的巡航路线
 */
public class Wanderer extends Enemy {
    
    /** 保持当前方向的概率（减少频繁转向） */
    private static final double KEEP_DIRECTION_CHANCE = 0.7;
    
    /**
     * 构造函数
     * @param gridX 初始X坐标
     * @param gridY 初始Y坐标
     */
    public Wanderer(double gridX, double gridY) {
        super(gridX, gridY, Constants.WANDERER_SPEED, Constants.COLOR_WANDERER);
        this.moveInterval = 0.3; // 游荡者移动间隔稍长
    }
    
    @Override
    protected void decideDirection() {
        List<Direction> validDirs = getValidDirectionsNoReverse();
        
        if (validDirs.isEmpty()) {
            // 死路，只能掉头
            validDirs = getValidDirections();
        }
        
        if (validDirs.isEmpty()) {
            direction = Direction.NONE;
            return;
        }
        
        // 有一定概率保持当前方向
        if (direction != Direction.NONE && 
            validDirs.contains(direction) && 
            random.nextDouble() < KEEP_DIRECTION_CHANCE) {
            return; // 保持当前方向
        }
        
        // 随机选择一个方向
        direction = validDirs.get(random.nextInt(validDirs.size()));
    }
}
