package com.pacman.item;

import com.pacman.util.Constants;

/**
 * 道具类型枚举
 */
public enum ItemType {
    
    /** 磁铁：自动吸取范围内豆子 */
    MAGNET("磁铁", Constants.COLOR_ITEM_MAGNET, Constants.MAGNET_DURATION),
    
    /** 护盾：抵挡一次敌人碰撞 */
    SHIELD("护盾", Constants.COLOR_ITEM_SHIELD, Constants.SHIELD_DURATION),
    
    /** 穿墙术：短暂时间可以穿越墙壁 */
    WALL_PASS("穿墙术", Constants.COLOR_ITEM_WALL_PASS, Constants.WALL_PASS_DURATION);
    
    /** 道具名称 */
    private final String name;
    
    /** 道具颜色 */
    private final String color;
    
    /** 持续时间（秒），0表示一次性使用 */
    private final double duration;
    
    /**
     * 构造函数
     * @param name 道具名称
     * @param color 道具颜色
     * @param duration 持续时间
     */
    ItemType(String name, String color, double duration) {
        this.name = name;
        this.color = color;
        this.duration = duration;
    }
    
    public String getName() {
        return name;
    }
    
    public String getColor() {
        return color;
    }
    
    public double getDuration() {
        return duration;
    }
}
