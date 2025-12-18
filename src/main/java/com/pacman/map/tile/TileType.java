package com.pacman.map.tile;

import com.pacman.util.Constants;

/**
 * 地图格子类型枚举
 */
public enum TileType {
    
    /** 空地（可通行） */
    FLOOR("floor", Constants.COLOR_FLOOR, true),
    
    /** 墙壁（不可通行） */
    WALL("wall", Constants.COLOR_WALL, false),
    
    /** 传送门 */
    PORTAL("portal", Constants.COLOR_PORTAL, true),
    
    /** 单向通道 */
    ONE_WAY("one_way", Constants.COLOR_ONE_WAY, true),
    
    /** 冰面 */
    ICE("ice", Constants.COLOR_ICE, true),
    
    /** 跳板 */
    JUMP_PAD("jump_pad", Constants.COLOR_JUMP_PAD, true),
    
    /** 加速带 */
    SPEED_UP("speed_up", Constants.COLOR_SPEED_UP, true),
    
    /** 减速带 */
    SLOW_DOWN("slow_down", Constants.COLOR_SLOW_DOWN, true),
    
    /** 致盲陷阱 */
    BLIND_TRAP("blind_trap", Constants.COLOR_BLIND_TRAP, true);
    
    /** 类型标识（用于JSON） */
    private final String id;
    
    /** 颜色 */
    private final String color;
    
    /** 是否可通行 */
    private final boolean walkable;
    
    /**
     * 构造函数
     * @param id 类型标识
     * @param color 颜色
     * @param walkable 是否可通行
     */
    TileType(String id, String color, boolean walkable) {
        this.id = id;
        this.color = color;
        this.walkable = walkable;
    }
    
    public String getId() {
        return id;
    }
    
    public String getColor() {
        return color;
    }
    
    public boolean isWalkable() {
        return walkable;
    }
    
    /**
     * 根据ID获取类型
     * @param id 类型ID
     * @return TileType，未找到返回FLOOR
     */
    public static TileType fromId(String id) {
        for (TileType type : values()) {
            if (type.id.equals(id)) {
                return type;
            }
        }
        return FLOOR;
    }
}
