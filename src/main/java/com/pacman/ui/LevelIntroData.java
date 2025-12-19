package com.pacman.ui;

import com.pacman.util.Constants;
import java.util.ArrayList;
import java.util.List;

/**
 * 关卡介绍数据类
 * 存储每个关卡引入的新元素信息
 */
public class LevelIntroData {

    /**
     * 新元素类型枚举
     */
    public enum ElementType {
        ENEMY,      // 敌人
        ITEM,       // 道具
        TERRAIN,    // 地形
        MECHANIC    // 游戏机制
    }

    /**
     * 新元素数据记录
     */
    public record NewElement(
            ElementType type,
            String name,
            String description,
            String tips,
            String color,
            String iconType  // 用于绘制图标的类型标识
    ) {}

    /**
     * 关卡介绍数据记录
     */
    public record LevelIntro(
            int level,
            String title,
            String subtitle,
            List<NewElement> newElements
    ) {}

    /**
     * 获取指定关卡的介绍数据
     * @param level 关卡编号
     * @return 关卡介绍数据，如果该关卡没有新元素则返回null
     */
    public static LevelIntro getLevelIntro(int level) {
        return switch (level) {
            case 1 -> createLevel1Intro();
            case 2 -> createLevel2Intro();
            case 3 -> createLevel3Intro();
            case 4 -> createLevel4Intro();
            case 5 -> createLevel5Intro();
            case 7 -> createLevel7Intro();
            case 8 -> createLevel8Intro();
            case 9 -> createLevel9Intro();
            case 10 -> createLevel10Intro();
            case 11 -> createLevel11Intro();
            case 13 -> createLevel13Intro();
            case 14 -> createLevel14Intro();
            case 15 -> createLevel15Intro();
            case 16 -> createLevel16Intro();
            case 17 -> createLevel17Intro();
            case 19 -> createLevel19Intro();
            default -> null;
        };
    }

    /**
     * 判断关卡是否有新元素介绍
     * @param level 关卡编号
     * @return 是否有新元素介绍
     */
    public static boolean hasIntro(int level) {
        return getLevelIntro(level) != null;
    }

    // ==================== 各关卡介绍数据 ====================

    private static LevelIntro createLevel1Intro() {
        List<NewElement> elements = new ArrayList<>();
        elements.add(new NewElement(
                ElementType.MECHANIC,
                "Basic Controls",
                "Use WASD or arrow keys to move Pac-Man",
                "Collect all dots in the maze to clear the level!",
                Constants.COLOR_PLAYER,
                "player"
        ));
        return new LevelIntro(1, "Chapter 1: Getting Started", "Welcome to the Maze World!", elements);
    }

    private static LevelIntro createLevel2Intro() {
        List<NewElement> elements = new ArrayList<>();
        elements.add(new NewElement(
                ElementType.ENEMY,
                "Chaser",
                "Red ghost that directly pursues your position",
                "It takes the shortest path to you, but moves slightly slower. It only recalculates at intersections - use this to shake it off!",
                Constants.COLOR_CHASER,
                "chaser"
        ));
        return new LevelIntro(2, "New Enemy Appears", "The maze guardians have arrived...", elements);
    }

    private static LevelIntro createLevel3Intro() {
        List<NewElement> elements = new ArrayList<>();
        elements.add(new NewElement(
                ElementType.ENEMY,
                "Wanderer",
                "Orange ghost that moves randomly through the maze",
                "Its movement is unpredictable - watch its direction carefully!",
                Constants.COLOR_WANDERER,
                "wanderer"
        ));
        return new LevelIntro(3, "New Enemy Appears", "More guardians join the battle...", elements);
    }

    private static LevelIntro createLevel4Intro() {
        List<NewElement> elements = new ArrayList<>();
        elements.add(new NewElement(
                ElementType.TERRAIN,
                "Portal",
                "Purple entrance that teleports you to its paired exit",
                "Use portals to escape chasers - plan your escape route!",
                Constants.COLOR_PORTAL,
                "portal"
        ));
        return new LevelIntro(4, "Space Jump", "Mysterious portals have appeared...", elements);
    }

    private static LevelIntro createLevel5Intro() {
        List<NewElement> elements = new ArrayList<>();
        elements.add(new NewElement(
                ElementType.TERRAIN,
                "One-Way Passage",
                "Cyan arrow floor - can only pass in the arrow's direction",
                "Plan your route using one-way passages! Once through, there's no going back!",
                Constants.COLOR_ONE_WAY,
                "oneway"
        ));
        return new LevelIntro(5, "One-Way Path", "Plan your route with one-way passages...", elements);
    }

    private static LevelIntro createLevel7Intro() {
        List<NewElement> elements = new ArrayList<>();
        elements.add(new NewElement(
                ElementType.TERRAIN,
                "Ice Floor",
                "Light blue floor - you'll keep sliding when stepping on it",
                "Learn to control momentum on ice and plan your route ahead!",
                Constants.COLOR_ICE,
                "ice"
        ));
        return new LevelIntro(7, "Chapter 2: Dangerous Terrain", "Deep in the maze, the ground becomes treacherous...", elements);
    }

    private static LevelIntro createLevel8Intro() {
        List<NewElement> elements = new ArrayList<>();
        elements.add(new NewElement(
                ElementType.ENEMY,
                "Patroller",
                "Green ghost that patrols back and forth along a fixed route",
                "Watch for gaps in the patrol route - memorize its pattern!",
                Constants.COLOR_PATROLLER,
                "patroller"
        ));
        return new LevelIntro(8, "Patroller Appears", "Guards with fixed routes have appeared...", elements);
    }

    private static LevelIntro createLevel9Intro() {
        List<NewElement> elements = new ArrayList<>();
        elements.add(new NewElement(
                ElementType.TERRAIN,
                "Speed Boost",
                "Orange floor - 80% speed increase when passing through",
                "Use speed boosts to quickly pass through dangerous areas!",
                Constants.COLOR_SPEED_UP,
                "speedup"
        ));
        return new LevelIntro(9, "Speed Zone", "A place where the wind moves your feet...", elements);
    }

    private static LevelIntro createLevel10Intro() {
        List<NewElement> elements = new ArrayList<>();
        elements.add(new NewElement(
                ElementType.TERRAIN,
                "Slowdown Zone",
                "Brown floor - 50% speed reduction when passing through",
                "Avoid slowdown zones, or lure enemies into them!",
                Constants.COLOR_SLOW_DOWN,
                "slowdown"
        ));
        return new LevelIntro(10, "Slowdown Trap", "A bone-chilling area...", elements);
    }

    private static LevelIntro createLevel11Intro() {
        List<NewElement> elements = new ArrayList<>();
        elements.add(new NewElement(
                ElementType.TERRAIN,
                "Jump Pad",
                "Green floor - jump 2 tiles forward when stepping on it",
                "Use jump pads to shake off chasers and quickly cross danger zones!",
                Constants.COLOR_JUMP_PAD,
                "jumppad"
        ));
        return new LevelIntro(11, "Jump Escape", "Bouncy mechanisms have appeared...", elements);
    }

    private static LevelIntro createLevel13Intro() {
        List<NewElement> elements = new ArrayList<>();
        elements.add(new NewElement(
                ElementType.ITEM,
                "Magnet",
                "Silver item - automatically attracts dots within 3 tiles",
                "Lasts 5 seconds - quickly collect lots of dots!",
                Constants.COLOR_ITEM_MAGNET,
                "magnet"
        ));
        return new LevelIntro(13, "Chapter 3: Item Master", "Ancient magical items have appeared...", elements);
    }

    private static LevelIntro createLevel14Intro() {
        List<NewElement> elements = new ArrayList<>();
        elements.add(new NewElement(
                ElementType.ENEMY,
                "Hunter",
                "Orange-red ghost - slow normally but charges for 2 seconds when it spots you",
                "Learn to avoid the hunter's line of sight - its speed skyrockets when you're spotted!",
                Constants.COLOR_HUNTER,
                "hunter"
        ));
        return new LevelIntro(14, "Hunter Attacks", "A dangerous line-of-sight tracker has appeared...", elements);
    }

    private static LevelIntro createLevel15Intro() {
        List<NewElement> elements = new ArrayList<>();
        elements.add(new NewElement(
                ElementType.ITEM,
                "Shield",
                "Blue item - blocks one enemy attack",
                "Shields don't stack - picking up another while shielded won't give extra protection.",
                Constants.COLOR_ITEM_SHIELD,
                "shield"
        ));
        return new LevelIntro(15, "Protective Power", "Protect yourself from harm...", elements);
    }

    private static LevelIntro createLevel16Intro() {
        List<NewElement> elements = new ArrayList<>();
        elements.add(new NewElement(
                ElementType.TERRAIN,
                "Blind Trap",
                "Dark purple floor - vision becomes limited when stepped on",
                "Memorize the map and avoid traps! Vision shrinks to 3-tile range for 2 seconds!",
                Constants.COLOR_BLIND_TRAP,
                "blindtrap"
        ));
        return new LevelIntro(16, "Blind Zone Crisis", "Darkness shrouds the maze...", elements);
    }

    private static LevelIntro createLevel17Intro() {
        List<NewElement> elements = new ArrayList<>();
        elements.add(new NewElement(
                ElementType.ITEM,
                "Wall Pass",
                "Gold item - temporarily allows passing through walls",
                "Create escape routes through walls - lasts 3 seconds!",
                Constants.COLOR_ITEM_WALL_PASS,
                "wallpass"
        ));
        return new LevelIntro(17, "Wall Pass Escape", "Magic to cross obstacles...", elements);
    }

    private static LevelIntro createLevel19Intro() {
        List<NewElement> elements = new ArrayList<>();
        elements.add(new NewElement(
                ElementType.ENEMY,
                "Phantom",
                "Purple ghost that periodically becomes invisible",
                "Goes invisible for 1.5s every 3s - moves faster and tracks you while invisible",
                Constants.COLOR_PHANTOM,
                "phantom"
        ));
        return new LevelIntro(19, "Chapter 4: Shadow Pursuit", "Invisible hunters lurk in the darkness...", elements);
    }


}
