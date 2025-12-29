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
        
        // Parse map layout
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

        // Set player spawn point
        map.setSpawnPoint(level.getSpawnX(), level.getSpawnY());

        // Configure one-way passage directions
        for (Level.OneWayConfig oneWay : level.getOneWays()) {
            map.setTile(oneWay.x, oneWay.y, TileType.ONE_WAY, oneWay.getDirection());
        }

        // Link portal pairs
        for (Level.PortalPair portal : level.getPortals()) {
            map.linkPortals(portal.x1, portal.y1, portal.x2, portal.y2);
        }

        // Add dots on all floor tiles
        map.addDotsOnAllFloors();

        // Add items
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

        // Add enemies
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
        
        // Generate different default maps based on level number
        level.setMapLayout(generateDefaultLayout(levelNumber));
        level.setSpawnX(1);
        level.setSpawnY(1);

        // Add enemies based on level difficulty
        addDefaultEnemies(level, levelNumber);

        // Add items based on chapter
        addDefaultItems(level, levelNumber);
        
        return level;
    }
    
    /**
     * Generate default map layout
     * @param levelNumber Level number
     * @return Map layout string array
     */
    private static String[] generateDefaultLayout(int levelNumber) {
        // Base maze template
        String[] layout = new String[Constants.MAP_ROWS];

        // Top and bottom rows are walls
        StringBuilder topBottom = new StringBuilder();
        for (int i = 0; i < Constants.MAP_COLS; i++) {
            topBottom.append('#');
        }
        layout[0] = topBottom.toString();
        layout[Constants.MAP_ROWS - 1] = topBottom.toString();

        // Middle rows
        for (int y = 1; y < Constants.MAP_ROWS - 1; y++) {
            StringBuilder row = new StringBuilder();
            for (int x = 0; x < Constants.MAP_COLS; x++) {
                if (x == 0 || x == Constants.MAP_COLS - 1) {
                    row.append('#'); // Left and right boundaries
                } else if (shouldBeWall(x, y, levelNumber)) {
                    row.append('#');
                } else if (shouldBeSpecialTile(x, y, levelNumber)) {
                    row.append(getSpecialTileChar(x, y, levelNumber));
                } else {
                    row.append('.'); // Empty floor
                }
            }
            layout[y] = row.toString();
        }

        return layout;
    }
    
    /**
     * Determine if a position should be a wall
     */
    private static boolean shouldBeWall(int x, int y, int levelNumber) {
        // Ensure spawn area is clear
        if (x <= 2 && y <= 2) return false;

        // Generate different wall layouts based on level
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
     * Determine if a position should be a special tile
     */
    private static boolean shouldBeSpecialTile(int x, int y, int levelNumber) {
        int chapter = (levelNumber - 1) / 6 + 1;

        // Chapter 1 has no special tiles (except portals)
        if (chapter == 1 && levelNumber >= 4) {
            // Level 4+ has portals
            return (x == 1 && y == Constants.MAP_ROWS - 2) || (x == Constants.MAP_COLS - 2 && y == 1);
        }

        // Chapter 2+ has terrain features
        if (chapter >= 2) {
            // Ice, speed up, slow down tiles
            if (x > 5 && x < 15 && y == 7) return true;
            if (x == 10 && y > 3 && y < 12) return true;
        }

        return false;
    }
    
    /**
     * Get the special tile character for a position
     */
    private static char getSpecialTileChar(int x, int y, int levelNumber) {
        int chapter = (levelNumber - 1) / 6 + 1;

        if (chapter == 1) {
            return 'P'; // Portal
        }

        // Return different special tiles based on position
        if (y == 7) {
            if (x < 8) return 'I'; // Ice
            if (x < 12) return '+'; // Speed up
            return '-'; // Slow down
        }

        if (x == 10) {
            return 'J'; // Jump pad
        }

        return '.';
    }
    
    /**
     * Add default enemies
     */
    private static void addDefaultEnemies(Level level, int levelNumber) {
        // Level 1 has no enemies
        if (levelNumber == 1) return;

        int chapter = (levelNumber - 1) / 6 + 1;
        int levelInChapter = (levelNumber - 1) % 6 + 1;

        // Chasers
        if (levelNumber >= 2) {
            level.getEnemies().add(new Level.EnemyConfig("chaser", Constants.MAP_COLS - 3, Constants.MAP_ROWS - 3));
        }
        if (levelNumber >= 4) {
            level.getEnemies().add(new Level.EnemyConfig("chaser", Constants.MAP_COLS / 2, Constants.MAP_ROWS / 2));
        }

        // Wanderers
        if (levelNumber >= 3) {
            level.getEnemies().add(new Level.EnemyConfig("wanderer", 5, Constants.MAP_ROWS - 3));
        }
        if (levelNumber >= 5) {
            level.getEnemies().add(new Level.EnemyConfig("wanderer", Constants.MAP_COLS - 5, 3));
        }

        // Patrollers (starting from chapter 2)
        if (chapter >= 2 && levelInChapter >= 2) {
            level.getEnemies().add(new Level.EnemyConfig("patroller", 8, 5));
        }
        if (chapter >= 2 && levelInChapter >= 4) {
            level.getEnemies().add(new Level.EnemyConfig("patroller", 12, 10));
        }

        // Hunters (starting from chapter 3)
        if (chapter >= 3 && levelInChapter >= 2) {
            level.getEnemies().add(new Level.EnemyConfig("hunter", Constants.MAP_COLS - 4, Constants.MAP_ROWS / 2));
        }
        if (chapter >= 3 && levelInChapter >= 5) {
            level.getEnemies().add(new Level.EnemyConfig("hunter", 4, Constants.MAP_ROWS / 2));
        }

        // Phantoms (starting from chapter 4)
        if (chapter >= 4) {
            level.getEnemies().add(new Level.EnemyConfig("phantom", Constants.MAP_COLS / 2, 3));
        }
        if (chapter >= 4 && levelInChapter >= 4) {
            level.getEnemies().add(new Level.EnemyConfig("phantom", Constants.MAP_COLS / 2, Constants.MAP_ROWS - 4));
        }

        // Chapter 5 adds more enemies
        if (chapter == 5) {
            level.getEnemies().add(new Level.EnemyConfig("chaser", 3, 3));
            level.getEnemies().add(new Level.EnemyConfig("hunter", Constants.MAP_COLS - 6, 6));
        }
    }
    
    /**
     * Add default items
     */
    private static void addDefaultItems(Level level, int levelNumber) {
        int chapter = (levelNumber - 1) / 6 + 1;

        // Items start appearing from chapter 3
        if (chapter < 3) return;

        int levelInChapter = (levelNumber - 1) % 6 + 1;

        // Magnet
        if (levelInChapter >= 1) {
            level.getItems().add(new Level.ItemConfig("magnet", Constants.MAP_COLS / 2, Constants.MAP_ROWS / 2));
        }

        // Shield
        if (levelInChapter >= 3) {
            level.getItems().add(new Level.ItemConfig("shield", 3, Constants.MAP_ROWS - 4));
        }

        // Wall pass
        if (levelInChapter >= 5) {
            level.getItems().add(new Level.ItemConfig("wallpass", Constants.MAP_COLS - 4, 4));
        }

        // Chapter 5 has more items
        if (chapter == 5) {
            level.getItems().add(new Level.ItemConfig("shield", Constants.MAP_COLS - 3, Constants.MAP_ROWS - 3));
            if (levelInChapter >= 4) {
                level.getItems().add(new Level.ItemConfig("magnet", 4, 4));
            }
        }
    }
}
