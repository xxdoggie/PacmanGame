package com.pacman.util;

/**
 * 游戏常量配置类
 * 集中管理所有游戏参数，便于调整和维护
 */
public final class Constants {
    
    // ==================== 窗口设置 ====================
    /** 游戏标题 */
    public static final String GAME_TITLE = "Pac-Man Adventure";
    
    /** 窗口宽度（像素） */
    public static final int WINDOW_WIDTH = 800;
    
    /** 窗口高度（像素） */
    public static final int WINDOW_HEIGHT = 700;
    
    // ==================== 地图设置 ====================
    /** 地图列数（格子数） */
    public static final int MAP_COLS = 20;
    
    /** 地图行数（格子数） */
    public static final int MAP_ROWS = 15;
    
    /** 每个格子的大小（像素） */
    public static final int TILE_SIZE = 36;
    
    /** 地图宽度（像素） */
    public static final int MAP_WIDTH = MAP_COLS * TILE_SIZE;
    
    /** 地图高度（像素） */
    public static final int MAP_HEIGHT = MAP_ROWS * TILE_SIZE;
    
    // ==================== 游戏设置 ====================
    /** 游戏帧率（每秒帧数） */
    public static final int FPS = 60;
    
    /** 每帧时间间隔（纳秒） */
    public static final long FRAME_TIME = 1_000_000_000L / FPS;
    
    /** 默认生命数 */
    public static final int DEFAULT_LIVES = 1;
    
    /** 总关卡数 */
    public static final int TOTAL_LEVELS = 30;
    
    // ==================== 玩家设置 ====================
    /** 玩家基础移动速度（格/秒） */
    public static final double PLAYER_BASE_SPEED = 5.0;
    
    /** 玩家半径（像素） */
    public static final int PLAYER_RADIUS = TILE_SIZE / 2 - 2;
    
    // ==================== 敌人设置 ====================
    /** 追踪者速度 */
    public static final double CHASER_SPEED = 3.5;
    
    /** 游荡者速度 */
    public static final double WANDERER_SPEED = 3.0;
    
    /** 猎手基础速度（慢速状态） */
    public static final double HUNTER_BASE_SPEED = 2.0;
    
    /** 猎手加速速度（看到玩家时） */
    public static final double HUNTER_RUSH_SPEED = 8.0;
    
    /** 巡逻者速度 */
    public static final double PATROLLER_SPEED = 3.0;
    
    /** 幻影速度 */
    public static final double PHANTOM_SPEED = 2.0;
    
    /** 幻影隐身周期（秒） */
    public static final double PHANTOM_INVISIBLE_CYCLE = 3.0;
    
    /** 幻影隐身持续时间（秒） */
    public static final double PHANTOM_INVISIBLE_DURATION = 1.5;
    
    // ==================== 道具效果设置 ====================
    /** 磁铁吸取范围（格子数） */
    public static final int MAGNET_RANGE = 3;
    
    /** 磁铁持续时间（秒） */
    public static final double MAGNET_DURATION = 5.0;
    
    /** 护盾持续时间（秒），0表示一次性使用 */
    public static final double SHIELD_DURATION = 0;
    
    /** 穿墙持续时间（秒） */
    public static final double WALL_PASS_DURATION = 3.0;
    
    // ==================== 地图元素效果设置 ====================
    /** 加速带加速倍率 */
    public static final double SPEED_UP_MULTIPLIER = 1.8;
    
    /** 减速带减速倍率 */
    public static final double SLOW_DOWN_MULTIPLIER = 0.5;
    
    /** 冰面滑行惯性系数（0-1，越大惯性越强） */
    public static final double ICE_FRICTION = 0.92;
    
    /** 跳板跳跃距离（格子数） */
    public static final int JUMP_PAD_DISTANCE = 2;
    
    /** 致盲效果持续时间（秒） */
    public static final double BLIND_DURATION = 2.0;

    /** 致盲效果可见范围（格子数） */
    public static final int BLIND_VISIBLE_RANGE = 3;
    
    // ==================== 颜色设置（临时用简单图形） ====================
    /** 玩家颜色 */
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
    
    // ==================== UI设置 ====================
    /** UI区域高度（显示分数、生命等） */
    public static final int UI_HEIGHT = WINDOW_HEIGHT - MAP_HEIGHT;
    
    /** 按钮宽度 */
    public static final int BUTTON_WIDTH = 200;
    
    /** 按钮高度 */
    public static final int BUTTON_HEIGHT = 50;
    
    /** 私有构造函数，防止实例化 */
    private Constants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }
}
