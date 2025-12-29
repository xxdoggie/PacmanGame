package com.pacman.map.tile;

import com.pacman.util.Constants;

/**
 * Tile type enumeration
 */
public enum TileType {
    FLOOR("floor", Constants.COLOR_FLOOR, true),
    WALL("wall", Constants.COLOR_WALL, false),
    PORTAL("portal", Constants.COLOR_PORTAL, true),
    ONE_WAY("one_way", Constants.COLOR_ONE_WAY, true),
    ICE("ice", Constants.COLOR_ICE, true),
    JUMP_PAD("jump_pad", Constants.COLOR_JUMP_PAD, true),
    SPEED_UP("speed_up", Constants.COLOR_SPEED_UP, true),
    SLOW_DOWN("slow_down", Constants.COLOR_SLOW_DOWN, true),
    BLIND_TRAP("blind_trap", Constants.COLOR_BLIND_TRAP, true);

    private final String id;
    private final String color;
    private final boolean walkable;

    TileType(String id, String color, boolean walkable) {
        this.id = id;
        this.color = color;
        this.walkable = walkable;
    }

    public String getId() { return id; }
    public String getColor() { return color; }
    public boolean isWalkable() { return walkable; }

    public static TileType fromId(String id) {
        for (TileType type : values()) {
            if (type.id.equals(id)) return type;
        }
        return FLOOR;
    }
}
