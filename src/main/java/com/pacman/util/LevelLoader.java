package com.pacman.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pacman.game.Level;
import com.pacman.item.ItemType;
import com.pacman.map.GameMap;
import com.pacman.map.tile.TileType;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Level loader - loads level data from JSON and builds GameMap
 */
public class LevelLoader {
    private static final Gson gson = new GsonBuilder().create();

    public static Level loadLevel(int levelNumber) {
        String path = "/levels/level_" + levelNumber + ".json";

        try (InputStream is = LevelLoader.class.getResourceAsStream(path)) {
            if (is == null) {
                System.err.println("Level file not found: " + path);
                return createDefaultLevel(levelNumber);
            }

            InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            Level level = gson.fromJson(reader, Level.class);
            level.setLevelNumber(levelNumber);
            return level;
        } catch (Exception e) {
            System.err.println("Failed to load level: " + e.getMessage());
            e.printStackTrace();
            return createDefaultLevel(levelNumber);
        }
    }
    
    public static GameMap buildGameMap(Level level) {
        GameMap map = new GameMap();
        
        // 解析地图布局
        String[] layout = level.getMapLayout();
        if (layout != null) {
            for (int y = 0; y < layout.length && y < Constants.MAP_ROWS; y++) {
                String row = layout[y];
                for (int x = 0; x < row.length() && x < Constants.MAP_COLS; x++) {
                    char c = row.charAt(x);
                    TileType type = charToTileType(c);
                    map.setTile(x, y, type);
                }
            }
        }
        
        // 设置玩家出生点
        map.setSpawnPoint(level.getSpawnX(), level.getSpawnY());
        
        // 处理单向通道方向
        for (Level.OneWayConfig oneWay : level.getOneWays()) {
            map.setTile(oneWay.x, oneWay.y, TileType.ONE_WAY, oneWay.getDirection());
        }
        
        // 链接传送门
        for (Level.PortalPair portal : level.getPortals()) {
            map.linkPortals(portal.x1, portal.y1, portal.x2, portal.y2);
        }
        
        // 在空地上添加豆子
        map.addDotsOnAllFloors();
        
        // 添加道具
        for (Level.ItemConfig itemConfig : level.getItems()) {
            ItemType type = switch (itemConfig.type.toLowerCase()) {
                case "magnet" -> ItemType.MAGNET;
                case "shield" -> ItemType.SHIELD;
                case "wallpass", "wall_pass" -> ItemType.WALL_PASS;
                default -> null;
            };
            if (type != null) {
                map.addItem(itemConfig.x, itemConfig.y, type);
            }
        }
        
        // 添加敌人
        for (int i = 0; i < level.getEnemies().size(); i++) {
            Level.EnemyConfig enemyConfig = level.getEnemies().get(i);
            map.addEnemy(enemyConfig.x, enemyConfig.y, enemyConfig.type);
        }
        
        return map;
    }
    
    private static TileType charToTileType(char c) {
        return switch (c) {
            case '#', 'W' -> TileType.WALL;
            case 'P' -> TileType.PORTAL;
            case 'O' -> TileType.ONE_WAY;
            case 'I' -> TileType.ICE;
            case 'J' -> TileType.JUMP_PAD;
            case '+' -> TileType.SPEED_UP;
            case '-' -> TileType.SLOW_DOWN;
            case 'B' -> TileType.BLIND_TRAP;
            default -> TileType.FLOOR;
        };
    }

    public static Level createDefaultLevel(int levelNumber) {
        Level level = new Level();
        level.setLevelNumber(levelNumber);
        level.setName("Level " + levelNumber);
        level.setChapter((levelNumber - 1) / 6 + 1);
        
        // 根据关卡编号生成不同的默认地图
        level.setMapLayout(generateDefaultLayout(levelNumber));
        level.setSpawnX(1);
        level.setSpawnY(1);
        
        // 根据关卡难度添加敌人
        addDefaultEnemies(level, levelNumber);
        
        // 根据章节添加道具
        addDefaultItems(level, levelNumber);
        
        return level;
    }
    
    /**
     * 生成默认地图布局
     * @param levelNumber 关卡编号
     * @return 地图布局字符串数组
     */
    private static String[] generateDefaultLayout(int levelNumber) {
        // 基础迷宫模板
        String[] layout = new String[Constants.MAP_ROWS];
        
        // 第一行和最后一行是墙
        StringBuilder topBottom = new StringBuilder();
        for (int i = 0; i < Constants.MAP_COLS; i++) {
            topBottom.append('#');
        }
        layout[0] = topBottom.toString();
        layout[Constants.MAP_ROWS - 1] = topBottom.toString();
        
        // 中间行
        for (int y = 1; y < Constants.MAP_ROWS - 1; y++) {
            StringBuilder row = new StringBuilder();
            for (int x = 0; x < Constants.MAP_COLS; x++) {
                if (x == 0 || x == Constants.MAP_COLS - 1) {
                    row.append('#'); // 左右边界
                } else if (shouldBeWall(x, y, levelNumber)) {
                    row.append('#');
                } else if (shouldBeSpecialTile(x, y, levelNumber)) {
                    row.append(getSpecialTileChar(x, y, levelNumber));
                } else {
                    row.append('.'); // 空地
                }
            }
            layout[y] = row.toString();
        }
        
        return layout;
    }
    
    /**
     * 判断指定位置是否应该是墙
     */
    private static boolean shouldBeWall(int x, int y, int levelNumber) {
        // 确保出生点周围是空的
        if (x <= 2 && y <= 2) return false;
        
        // 根据关卡生成不同的墙壁布局
        int pattern = levelNumber % 5;
        
        return switch (pattern) {
            case 0 -> (x % 4 == 2 && y % 3 != 0) || (y % 4 == 2 && x % 3 != 0);
            case 1 -> (x + y) % 5 == 0 && x > 3 && y > 3;
            case 2 -> ((x % 3 == 0 && y % 2 == 0) || (x % 2 == 0 && y % 3 == 0)) && x > 2 && y > 2;
            case 3 -> (x % 4 == 0 || y % 4 == 0) && x > 2 && y > 2 && x < Constants.MAP_COLS - 2 && y < Constants.MAP_ROWS - 2;
            case 4 -> ((x - 5) * (x - 5) + (y - 7) * (y - 7) < 9) || ((x - 14) * (x - 14) + (y - 7) * (y - 7) < 9);
            default -> false;
        };
    }
    
    /**
     * 判断是否应该是特殊格子
     */
    private static boolean shouldBeSpecialTile(int x, int y, int levelNumber) {
        int chapter = (levelNumber - 1) / 6 + 1;
        
        // 第一章没有特殊格子（除了传送门）
        if (chapter == 1 && levelNumber >= 4) {
            // Level 4+ 有传送门
            return (x == 1 && y == Constants.MAP_ROWS - 2) || (x == Constants.MAP_COLS - 2 && y == 1);
        }
        
        // 第二章开始有地形
        if (chapter >= 2) {
            // 冰面、加速带、减速带
            if (x > 5 && x < 15 && y == 7) return true;
            if (x == 10 && y > 3 && y < 12) return true;
        }
        
        return false;
    }
    
    /**
     * 获取特殊格子字符
     */
    private static char getSpecialTileChar(int x, int y, int levelNumber) {
        int chapter = (levelNumber - 1) / 6 + 1;
        
        if (chapter == 1) {
            return 'P'; // 传送门
        }
        
        // 根据位置返回不同特殊格子
        if (y == 7) {
            if (x < 8) return 'I'; // 冰面
            if (x < 12) return '+'; // 加速带
            return '-'; // 减速带
        }
        
        if (x == 10) {
            return 'J'; // 跳板
        }
        
        return '.';
    }
    
    /**
     * 添加默认敌人
     */
    private static void addDefaultEnemies(Level level, int levelNumber) {
        // Level 1 没有敌人
        if (levelNumber == 1) return;
        
        int chapter = (levelNumber - 1) / 6 + 1;
        int levelInChapter = (levelNumber - 1) % 6 + 1;
        
        // 追踪者
        if (levelNumber >= 2) {
            level.getEnemies().add(new Level.EnemyConfig("chaser", Constants.MAP_COLS - 3, Constants.MAP_ROWS - 3));
        }
        if (levelNumber >= 4) {
            level.getEnemies().add(new Level.EnemyConfig("chaser", Constants.MAP_COLS / 2, Constants.MAP_ROWS / 2));
        }
        
        // 游荡者
        if (levelNumber >= 3) {
            level.getEnemies().add(new Level.EnemyConfig("wanderer", 5, Constants.MAP_ROWS - 3));
        }
        if (levelNumber >= 5) {
            level.getEnemies().add(new Level.EnemyConfig("wanderer", Constants.MAP_COLS - 5, 3));
        }
        
        // 巡逻者（第二章开始）
        if (chapter >= 2 && levelInChapter >= 2) {
            level.getEnemies().add(new Level.EnemyConfig("patroller", 8, 5));
        }
        if (chapter >= 2 && levelInChapter >= 4) {
            level.getEnemies().add(new Level.EnemyConfig("patroller", 12, 10));
        }
        
        // 猎手（第三章开始）
        if (chapter >= 3 && levelInChapter >= 2) {
            level.getEnemies().add(new Level.EnemyConfig("hunter", Constants.MAP_COLS - 4, Constants.MAP_ROWS / 2));
        }
        if (chapter >= 3 && levelInChapter >= 5) {
            level.getEnemies().add(new Level.EnemyConfig("hunter", 4, Constants.MAP_ROWS / 2));
        }
        
        // 幻影（第四章开始）
        if (chapter >= 4) {
            level.getEnemies().add(new Level.EnemyConfig("phantom", Constants.MAP_COLS / 2, 3));
        }
        if (chapter >= 4 && levelInChapter >= 4) {
            level.getEnemies().add(new Level.EnemyConfig("phantom", Constants.MAP_COLS / 2, Constants.MAP_ROWS - 4));
        }
        
        // 第五章增加更多敌人
        if (chapter == 5) {
            level.getEnemies().add(new Level.EnemyConfig("chaser", 3, 3));
            level.getEnemies().add(new Level.EnemyConfig("hunter", Constants.MAP_COLS - 6, 6));
        }
    }
    
    /**
     * 添加默认道具
     */
    private static void addDefaultItems(Level level, int levelNumber) {
        int chapter = (levelNumber - 1) / 6 + 1;
        
        // 第三章开始有道具
        if (chapter < 3) return;
        
        int levelInChapter = (levelNumber - 1) % 6 + 1;
        
        // 磁铁
        if (levelInChapter >= 1) {
            level.getItems().add(new Level.ItemConfig("magnet", Constants.MAP_COLS / 2, Constants.MAP_ROWS / 2));
        }
        
        // 护盾
        if (levelInChapter >= 3) {
            level.getItems().add(new Level.ItemConfig("shield", 3, Constants.MAP_ROWS - 4));
        }
        
        // 穿墙术
        if (levelInChapter >= 5) {
            level.getItems().add(new Level.ItemConfig("wallpass", Constants.MAP_COLS - 4, 4));
        }
        
        // 第五章有更多道具
        if (chapter == 5) {
            level.getItems().add(new Level.ItemConfig("shield", Constants.MAP_COLS - 3, Constants.MAP_ROWS - 3));
            if (levelInChapter >= 4) {
                level.getItems().add(new Level.ItemConfig("magnet", 4, 4));
            }
        }
    }
}
