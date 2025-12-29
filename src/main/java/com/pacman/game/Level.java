package com.pacman.game;

import com.pacman.util.Direction;

import java.util.ArrayList;
import java.util.List;

/**
 * Level data class - stores level configuration loaded from JSON
 */
public class Level {
    private int levelNumber;
    private String name;
    private int chapter;
    private String[] mapLayout;
    private int spawnX;
    private int spawnY;
    private List<EnemyConfig> enemies;
    private List<ItemConfig> items;
    private List<PortalPair> portals;
    private List<OneWayConfig> oneWays;
    private List<PatrolConfig> patrols;

    public Level() {
        this.enemies = new ArrayList<>();
        this.items = new ArrayList<>();
        this.portals = new ArrayList<>();
        this.oneWays = new ArrayList<>();
        this.patrols = new ArrayList<>();
    }

    /** Enemy configuration inner class */
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

    /** Item configuration inner class */
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

    /** Portal pair configuration */
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

    /** One-way passage configuration */
    public static class OneWayConfig {
        public int x, y;
        public String direction;

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

    /** Patrol path configuration */
    public static class PatrolConfig {
        public int enemyIndex;
        public List<int[]> path;

        public PatrolConfig() {
            this.path = new ArrayList<>();
        }
    }
    
    // Getters and Setters
    public int getLevelNumber() { return levelNumber; }
    public void setLevelNumber(int levelNumber) { this.levelNumber = levelNumber; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getChapter() { return chapter; }
    public void setChapter(int chapter) { this.chapter = chapter; }
    public String[] getMapLayout() { return mapLayout; }
    public void setMapLayout(String[] mapLayout) { this.mapLayout = mapLayout; }
    public int getSpawnX() { return spawnX; }
    public void setSpawnX(int spawnX) { this.spawnX = spawnX; }
    public int getSpawnY() { return spawnY; }
    public void setSpawnY(int spawnY) { this.spawnY = spawnY; }
    public List<EnemyConfig> getEnemies() { return enemies; }
    public void setEnemies(List<EnemyConfig> enemies) { this.enemies = enemies; }
    public List<ItemConfig> getItems() { return items; }
    public void setItems(List<ItemConfig> items) { this.items = items; }
    public List<PortalPair> getPortals() { return portals; }
    public void setPortals(List<PortalPair> portals) { this.portals = portals; }
    public List<OneWayConfig> getOneWays() { return oneWays; }
    public void setOneWays(List<OneWayConfig> oneWays) { this.oneWays = oneWays; }
    public List<PatrolConfig> getPatrols() { return patrols; }
    public void setPatrols(List<PatrolConfig> patrols) { this.patrols = patrols; }
}
