package com.pacman.item;

import com.pacman.util.Constants;

/**
 * Item type enumeration
 */
public enum ItemType {
    MAGNET("Magnet", Constants.COLOR_ITEM_MAGNET, Constants.MAGNET_DURATION),
    SHIELD("Shield", Constants.COLOR_ITEM_SHIELD, Constants.SHIELD_DURATION),
    WALL_PASS("Wall Pass", Constants.COLOR_ITEM_WALL_PASS, Constants.WALL_PASS_DURATION);

    private final String name;
    private final String color;
    private final double duration; // 0 = one-time use

    ItemType(String name, String color, double duration) {
        this.name = name;
        this.color = color;
        this.duration = duration;
    }

    public String getName() { return name; }
    public String getColor() { return color; }
    public double getDuration() { return duration; }
}
