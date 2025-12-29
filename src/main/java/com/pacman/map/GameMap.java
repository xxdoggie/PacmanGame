package com.pacman.map;

import com.pacman.entity.Dot;
import com.pacman.entity.Player;
import com.pacman.entity.enemy.*;
import com.pacman.item.*;
import com.pacman.map.tile.Tile;
import com.pacman.map.tile.TileType;
import com.pacman.util.Constants;
import com.pacman.util.Direction;
import com.pacman.util.SoundManager;
import com.pacman.util.SoundManager.SoundType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Game map class - manages tiles, dots, items, and enemies
 */
public class GameMap {
    private Tile[][] tiles;
    private List<Dot> dots;
    private List<Item> items;
    private List<Enemy> enemies;
    private int spawnX, spawnY;
    private int width, height;

    public GameMap() {
        this.width = Constants.MAP_COLS;
        this.height = Constants.MAP_ROWS;
        this.tiles = new Tile[height][width];
        this.dots = new ArrayList<>();
        this.items = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.spawnX = 1;
        this.spawnY = 1;
        initEmptyMap();
    }

    private void initEmptyMap() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile tile = new Tile(x, y, TileType.FLOOR);
                tile.setGameMap(this);
                tiles[y][x] = tile;
            }
        }
    }
    
    public void setTile(int x, int y, TileType type) {
        if (isValidPosition(x, y)) {
            Tile tile = new Tile(x, y, type);
            tile.setGameMap(this);
            tiles[y][x] = tile;
        }
    }

    public void setTile(int x, int y, TileType type, Direction direction) {
        if (isValidPosition(x, y)) {
            Tile tile = new Tile(x, y, type);
            tile.setDirection(direction);
            tile.setGameMap(this);
            tiles[y][x] = tile;
        }
    }
    
    public Tile getTile(int x, int y) {
        if (isValidPosition(x, y)) {
            return tiles[y][x];
        }
        return null;
    }
    
    public void linkPortals(int x1, int y1, int x2, int y2) {
        Tile tile1 = getTile(x1, y1);
        Tile tile2 = getTile(x2, y2);
        
        if (tile1 != null && tile2 != null) {
            tile1.setLinkedTile(tile2);
            tile2.setLinkedTile(tile1);
        }
    }
    
    public void addDot(int x, int y) {
        if (isValidPosition(x, y) && tiles[y][x].isWalkable()) {
            dots.add(new Dot(x, y));
        }
    }
    
    /** Add dots only on FLOOR tiles, excluding spawn and special tiles */
    public void addDotsOnAllFloors() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (tiles[y][x].getType() == TileType.FLOOR) {
                    if (x != spawnX || y != spawnY) {
                        addDot(x, y);
                    }
                }
            }
        }
    }
    
    public void addItem(int x, int y, ItemType type) {
        if (!isValidPosition(x, y)) return;

        Item item = switch (type) {
            case MAGNET -> new Magnet(x, y);
            case SHIELD -> new Shield(x, y);
            case WALL_PASS -> new WallPass(x, y);
        };

        items.add(item);
        dots.removeIf(dot -> dot.getTileX() == x && dot.getTileY() == y);
    }
    
    public Enemy addEnemy(int x, int y, String enemyType) {
        // Find valid spawn position if invalid
        int spawnX = x;
        int spawnY = y;
        if (!isValidPosition(x, y) || !tiles[y][x].isWalkable()) {
            outerLoop:
            for (int radius = 1; radius < Math.max(width, height); radius++) {
                for (int dx = -radius; dx <= radius; dx++) {
                    for (int dy = -radius; dy <= radius; dy++) {
                        int newX = x + dx;
                        int newY = y + dy;
                        if (isValidPosition(newX, newY) && tiles[newY][newX].isWalkable()) {
                            spawnX = newX;
                            spawnY = newY;
                            break outerLoop;
                        }
                    }
                }
            }
        }

        final int finalSpawnX = spawnX;
        final int finalSpawnY = spawnY;

        Enemy enemy = switch (enemyType.toLowerCase()) {
            case "chaser" -> new Chaser(finalSpawnX, finalSpawnY);
            case "wanderer" -> new Wanderer(finalSpawnX, finalSpawnY);
            case "hunter" -> new Hunter(finalSpawnX, finalSpawnY);
            case "patroller" -> {
                Patroller p = new Patroller(finalSpawnX, finalSpawnY);
                p.setGameMap(this);
                p.generateDefaultPath();
                yield p;
            }
            case "phantom" -> {
                Phantom ph = new Phantom(finalSpawnX, finalSpawnY);
                ph.setGameMap(this);
                ph.generateDefaultPath();
                yield ph;
            }
            default -> new Wanderer(finalSpawnX, finalSpawnY);
        };

        enemy.setGameMap(this);
        enemies.add(enemy);
        dots.removeIf(dot -> dot.getTileX() == finalSpawnX && dot.getTileY() == finalSpawnY);
        return enemy;
    }

    public void setSpawnPoint(int x, int y) {
        this.spawnX = x;
        this.spawnY = y;
    }
    
    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
    
    public boolean canMoveTo(double x, double y, boolean canWallPass) {
        int tileX = (int) Math.round(x);
        int tileY = (int) Math.round(y);
        if (!isValidPosition(tileX, tileY)) return false;
        if (canWallPass) return true;
        return tiles[tileY][tileX].isWalkable();
    }

    public boolean canMoveTo(double x, double y, Direction fromDirection, boolean canWallPass) {
        int tileX = (int) Math.round(x);
        int tileY = (int) Math.round(y);
        if (!isValidPosition(tileX, tileY)) return false;

        Tile tile = tiles[tileY][tileX];
        if (canWallPass) return true;

        if (tile.getType() == TileType.ONE_WAY) {
            return tile.canEnterFrom(fromDirection);
        }
        return tile.isWalkable();
    }

    public boolean canEnterFrom(int x, int y, Direction fromDirection, boolean canWallPass) {
        if (!isValidPosition(x, y)) return false;
        if (canWallPass) return true;
        return tiles[y][x].canEnterFrom(fromDirection);
    }
    
    public void update(Player player, double deltaTime) {
        int playerTileX = player.getTileX();
        int playerTileY = player.getTileY();

        // Process tile effects
        if (isValidPosition(playerTileX, playerTileY)) {
            tiles[playerTileY][playerTileX].onStep(player);
        }

        // Collect dots
        for (Dot dot : dots) {
            if (!dot.isCollected() && dot.canBeCollectedBy(player)) {
                if (player.hasEffect(ItemType.MAGNET)) {
                    double dist = Math.sqrt(
                            Math.pow(dot.getGridX() - player.getGridX(), 2) +
                            Math.pow(dot.getGridY() - player.getGridY(), 2)
                    );
                    if (dist <= Constants.MAGNET_RANGE) {
                        dot.collect();
                        SoundManager.getInstance().play(SoundType.EAT_DOT);
                    }
                } else {
                    dot.collect();
                    SoundManager.getInstance().play(SoundType.EAT_DOT);
                }
            }
        }

        // Magnet effect: attract nearby dots
        if (player.hasEffect(ItemType.MAGNET)) {
            for (Dot dot : dots) {
                if (!dot.isCollected()) {
                    double dist = Math.sqrt(
                            Math.pow(dot.getGridX() - player.getGridX(), 2) +
                            Math.pow(dot.getGridY() - player.getGridY(), 2)
                    );
                    if (dist <= Constants.MAGNET_RANGE) {
                        dot.collect();
                        SoundManager.getInstance().play(SoundType.EAT_DOT);
                    }
                }
            }
        }

        // Collect items
        for (Item item : items) {
            if (!item.isCollected() && item.canBeCollectedBy(player)) {
                item.collect(player);
                SoundManager.getInstance().play(SoundType.ITEM_PICKUP);
            }
        }

        for (Item item : items) item.update(deltaTime);
        for (Enemy enemy : enemies) {
            enemy.setPlayer(player);
            enemy.update(deltaTime);
        }
    }
    
    public boolean checkEnemyCollision(Player player) {
        if (player.isJumping() || player.isInvincible()) return false;

        for (Enemy enemy : enemies) {
            if (enemy.collidesWithPlayer()) {
                if (player.consumeShield()) continue;
                return true;
            }
        }
        return false;
    }

    public boolean allDotsCollected() {
        for (Dot dot : dots) {
            if (!dot.isCollected()) return false;
        }
        return true;
    }

    public int getRemainingDots() {
        int count = 0;
        for (Dot dot : dots) {
            if (!dot.isCollected()) count++;
        }
        return count;
    }
    
    public void render(GraphicsContext gc, Player player) {
        // Render tiles
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (player != null && player.isBlinded()) {
                    double dist = Math.sqrt(
                            Math.pow(x - player.getGridX(), 2) +
                            Math.pow(y - player.getGridY(), 2)
                    );
                    if (dist > Constants.BLIND_VISIBLE_RANGE) {
                        gc.setFill(Color.BLACK);
                        gc.fillRect(x * Constants.TILE_SIZE, y * Constants.TILE_SIZE,
                                Constants.TILE_SIZE, Constants.TILE_SIZE);
                        continue;
                    }
                }
                tiles[y][x].render(gc);
            }
        }

        for (Dot dot : dots) {
            if (!player.isBlinded() || isInVisibleRange(dot, player)) {
                dot.render(gc);
            }
        }

        for (Item item : items) {
            if (!player.isBlinded() || isInVisibleRange(item, player)) {
                item.render(gc);
            }
        }

        for (Enemy enemy : enemies) {
            if (!player.isBlinded() || isInVisibleRange(enemy, player)) {
                enemy.render(gc);
            }
        }
    }

    private boolean isInVisibleRange(com.pacman.entity.Entity entity, Player player) {
        if (player == null || !player.isBlinded()) return true;
        double dist = Math.sqrt(
                Math.pow(entity.getGridX() - player.getGridX(), 2) +
                Math.pow(entity.getGridY() - player.getGridY(), 2)
        );
        return dist <= Constants.BLIND_VISIBLE_RANGE;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getSpawnX() { return spawnX; }
    public int getSpawnY() { return spawnY; }
    public List<Dot> getDots() { return dots; }
    public List<Item> getItems() { return items; }
    public List<Enemy> getEnemies() { return enemies; }
}
