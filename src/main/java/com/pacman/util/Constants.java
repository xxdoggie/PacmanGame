package com.pacman.util;

/**
 * Game constants configuration
 */
public final class Constants {
    // Window settings
    public static final String GAME_TITLE = "Pac-Man Adventure";
    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 700;
    
    // Map settings
    public static final int MAP_COLS = 20;
    public static final int MAP_ROWS = 15;
    public static final int TILE_SIZE = 36;
    public static final int MAP_WIDTH = MAP_COLS * TILE_SIZE;
    public static final int MAP_HEIGHT = MAP_ROWS * TILE_SIZE;
    
    // Game settings
    public static final int FPS = 120;
    public static final long FRAME_TIME = 1_000_000_000L / FPS;
    public static final int DEFAULT_LIVES = 100;
    public static final int TOTAL_LEVELS = 30;
    
    // Player settings
    public static final double PLAYER_BASE_SPEED = 5.0;
    public static final int PLAYER_RADIUS = TILE_SIZE / 2 - 2;

    // Enemy settings
    public static final double CHASER_SPEED = 3.5;
    public static final double WANDERER_SPEED = 3.0;
    public static final double HUNTER_BASE_SPEED = 2.0;
    public static final double HUNTER_RUSH_SPEED = 7.5;
    public static final double PATROLLER_SPEED = 3.0;
    public static final double PHANTOM_SPEED = 2.0;
    public static final double PHANTOM_INVISIBLE_CYCLE = 3.0;
    public static final double PHANTOM_INVISIBLE_DURATION = 1.5;
    
    // Item effects
    public static final int MAGNET_RANGE = 3;
    public static final double MAGNET_DURATION = 5.0;
    public static final double SHIELD_DURATION = 0; // 0 = one-time use
    public static final double WALL_PASS_DURATION = 3.0;

    // Map tile effects
    public static final double SPEED_UP_MULTIPLIER = 1.8;
    public static final double SLOW_DOWN_MULTIPLIER = 0.5;
    public static final double ICE_FRICTION = 0.92;
    public static final int JUMP_PAD_DISTANCE = 2;
    public static final double BLIND_DURATION = 2.0;
    public static final int BLIND_VISIBLE_RANGE = 3;
    
    // Colors
    public static final String COLOR_PLAYER = "#FFFF00";
    
    /** 豆子颜色 */
    public static final String COLOR_DOT = "#FFFFFF";
    
    /** 墙壁颜色 */
    public static final String COLOR_WALL = "#1E90FF";
    
    /** 地板颜色 */
    public static final String COLOR_FLOOR = "#1A1A2E";
    
    /** 传送门颜色 */
    public static final String COLOR_PORTAL = "#FF00FF";
    
    /** 单向通道颜色 */
    public static final String COLOR_ONE_WAY = "#00CED1";
    
    /** 冰面颜色 */
    public static final String COLOR_ICE = "#87CEEB";
    
    /** 跳板颜色 */
    public static final String COLOR_JUMP_PAD = "#32CD32";
    
    /** 加速带颜色 */
    public static final String COLOR_SPEED_UP = "#FFA500";
    
    /** 减速带颜色 */
    public static final String COLOR_SLOW_DOWN = "#8B4513";
    
    /** 致盲陷阱颜色 */
    public static final String COLOR_BLIND_TRAP = "#4B0082";
    
    /** 追踪者颜色 */
    public static final String COLOR_CHASER = "#FF0000";
    
    /** 游荡者颜色 */
    public static final String COLOR_WANDERER = "#FFA07A";
    
    /** 猎手颜色 */
    public static final String COLOR_HUNTER = "#FF4500";
    
    /** 巡逻者颜色 */
    public static final String COLOR_PATROLLER = "#00FF00";
    
    /** 幻影颜色 */
    public static final String COLOR_PHANTOM = "#9370DB";
    
    /** 磁铁道具颜色 */
    public static final String COLOR_ITEM_MAGNET = "#C0C0C0";
    
    /** 护盾道具颜色 */
    public static final String COLOR_ITEM_SHIELD = "#00BFFF";
    
    /** 穿墙道具颜色 */
    public static final String COLOR_ITEM_WALL_PASS = "#FFD700";
    
    // UI settings
    public static final int UI_HEIGHT = WINDOW_HEIGHT - MAP_HEIGHT;
    public static final int BUTTON_WIDTH = 200;
    public static final int BUTTON_HEIGHT = 50;

    private Constants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }
}
