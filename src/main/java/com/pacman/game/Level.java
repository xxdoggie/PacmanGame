package com.pacman.game;

import com.pacman.util.Direction;

import java.util.ArrayList;
import java.util.List;

/**
 * 关卡数据类
 * 用于存储从JSON加载的关卡信息
 */
public class Level {
    
    /** 关卡编号 */
    private int levelNumber;
    
    /** 关卡名称 */
    private String name;
    
    /** 章节编号 */
    private int chapter;
    
    /** 地图布局（二维字符数组） */
    private String[] mapLayout;
    
    /** 玩家出生点X */
    private int spawnX;
    
    /** 玩家出生点Y */
    private int spawnY;
    
    /** 敌人配置列表 */
    private List<EnemyConfig> enemies;
    
    /** 道具配置列表 */
    private List<ItemConfig> items;
    
    /** 传送门配对列表 */
    private List<PortalPair> portals;
    
    /** 单向通道列表 */
    private List<OneWayConfig> oneWays;
    
    /** 巡逻路径配置 */
    private List<PatrolConfig> patrols;
    
    /**
     * 默认构造函数
     */
    public Level() {
        this.enemies = new ArrayList<>();
        this.items = new ArrayList<>();
        this.portals = new ArrayList<>();
        this.oneWays = new ArrayList<>();
        this.patrols = new ArrayList<>();
    }
    
    /**
     * 敌人配置内部类
     */
    public static class EnemyConfig {
        public String type;
        public int x;
        public int y;
        
        public EnemyConfig() {}
        
        public EnemyConfig(String type, int x, int y) {
            this.type = type;
            this.x = x;
            this.y = y;
        }
    }
    
    /**
     * 道具配置内部类
     */
    public static class ItemConfig {
        public String type;
        public int x;
        public int y;
        
        public ItemConfig() {}
        
        public ItemConfig(String type, int x, int y) {
            this.type = type;
            this.x = x;
            this.y = y;
        }
    }
    
    /**
     * 传送门配对内部类
     */
    public static class PortalPair {
        public int x1, y1;
        public int x2, y2;
        
        public PortalPair() {}
        
        public PortalPair(int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
    }
    
    /**
     * 单向通道配置内部类
     */
    public static class OneWayConfig {
        public int x;
        public int y;
        public String direction; // "up", "down", "left", "right"
        
        public OneWayConfig() {}
        
        public OneWayConfig(int x, int y, String direction) {
            this.x = x;
            this.y = y;
            this.direction = direction;
        }
        
        public Direction getDirection() {
            return switch (direction.toLowerCase()) {
                case "up" -> Direction.UP;
                case "down" -> Direction.DOWN;
                case "left" -> Direction.LEFT;
                case "right" -> Direction.RIGHT;
                default -> Direction.NONE;
            };
        }
    }
    
    /**
     * 巡逻路径配置内部类
     */
    public static class PatrolConfig {
        public int enemyIndex; // 对应enemies列表中的索引
        public List<int[]> path; // 路径点列表
        
        public PatrolConfig() {
            this.path = new ArrayList<>();
        }
    }
    
    // ==================== Getter 和 Setter ====================
    
    public int getLevelNumber() {
        return levelNumber;
    }
    
    public void setLevelNumber(int levelNumber) {
        this.levelNumber = levelNumber;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getChapter() {
        return chapter;
    }
    
    public void setChapter(int chapter) {
        this.chapter = chapter;
    }
    
    public String[] getMapLayout() {
        return mapLayout;
    }
    
    public void setMapLayout(String[] mapLayout) {
        this.mapLayout = mapLayout;
    }
    
    public int getSpawnX() {
        return spawnX;
    }
    
    public void setSpawnX(int spawnX) {
        this.spawnX = spawnX;
    }
    
    public int getSpawnY() {
        return spawnY;
    }
    
    public void setSpawnY(int spawnY) {
        this.spawnY = spawnY;
    }
    
    public List<EnemyConfig> getEnemies() {
        return enemies;
    }
    
    public void setEnemies(List<EnemyConfig> enemies) {
        this.enemies = enemies;
    }
    
    public List<ItemConfig> getItems() {
        return items;
    }
    
    public void setItems(List<ItemConfig> items) {
        this.items = items;
    }
    
    public List<PortalPair> getPortals() {
        return portals;
    }
    
    public void setPortals(List<PortalPair> portals) {
        this.portals = portals;
    }
    
    public List<OneWayConfig> getOneWays() {
        return oneWays;
    }
    
    public void setOneWays(List<OneWayConfig> oneWays) {
        this.oneWays = oneWays;
    }
    
    public List<PatrolConfig> getPatrols() {
        return patrols;
    }
    
    public void setPatrols(List<PatrolConfig> patrols) {
        this.patrols = patrols;
    }
}
