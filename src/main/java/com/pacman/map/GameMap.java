package com.pacman.map;

import com.pacman.entity.Dot;
import com.pacman.entity.Player;
import com.pacman.entity.enemy.*;
import com.pacman.item.*;
import com.pacman.map.tile.Tile;
import com.pacman.map.tile.TileType;
import com.pacman.util.Constants;
import com.pacman.util.Direction;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * 游戏地图类
 * 管理地图格子、豆子、道具和敌人
 */
public class GameMap {
    
    /** 地图格子二维数组 */
    private Tile[][] tiles;
    
    /** 豆子列表 */
    private List<Dot> dots;
    
    /** 道具列表 */
    private List<Item> items;
    
    /** 敌人列表 */
    private List<Enemy> enemies;
    
    /** 玩家出生点 */
    private int spawnX;
    private int spawnY;
    
    /** 地图宽度（格子数） */
    private int width;
    
    /** 地图高度（格子数） */
    private int height;
    
    /**
     * 构造函数
     */
    public GameMap() {
        this.width = Constants.MAP_COLS;
        this.height = Constants.MAP_ROWS;
        this.tiles = new Tile[height][width];
        this.dots = new ArrayList<>();
        this.items = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.spawnX = 1;
        this.spawnY = 1;
        
        // 初始化为空地图
        initEmptyMap();
    }
    
    /**
     * 初始化空地图
     */
    private void initEmptyMap() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tiles[y][x] = new Tile(x, y, TileType.FLOOR);
            }
        }
    }
    
    /**
     * 设置格子类型
     * @param x X坐标
     * @param y Y坐标
     * @param type 格子类型
     */
    public void setTile(int x, int y, TileType type) {
        if (isValidPosition(x, y)) {
            tiles[y][x] = new Tile(x, y, type);
        }
    }
    
    /**
     * 设置格子类型和方向
     * @param x X坐标
     * @param y Y坐标
     * @param type 格子类型
     * @param direction 方向
     */
    public void setTile(int x, int y, TileType type, Direction direction) {
        if (isValidPosition(x, y)) {
            Tile tile = new Tile(x, y, type);
            tile.setDirection(direction);
            tiles[y][x] = tile;
        }
    }
    
    /**
     * 获取格子
     * @param x X坐标
     * @param y Y坐标
     * @return 格子对象，越界返回null
     */
    public Tile getTile(int x, int y) {
        if (isValidPosition(x, y)) {
            return tiles[y][x];
        }
        return null;
    }
    
    /**
     * 链接两个传送门
     * @param x1 传送门1的X坐标
     * @param y1 传送门1的Y坐标
     * @param x2 传送门2的X坐标
     * @param y2 传送门2的Y坐标
     */
    public void linkPortals(int x1, int y1, int x2, int y2) {
        Tile tile1 = getTile(x1, y1);
        Tile tile2 = getTile(x2, y2);
        
        if (tile1 != null && tile2 != null) {
            tile1.setLinkedTile(tile2);
            tile2.setLinkedTile(tile1);
        }
    }
    
    /**
     * 添加豆子
     * @param x X坐标
     * @param y Y坐标
     */
    public void addDot(int x, int y) {
        if (isValidPosition(x, y) && tiles[y][x].isWalkable()) {
            dots.add(new Dot(x, y));
        }
    }
    
    /**
     * 在所有空地上添加豆子
     */
    public void addDotsOnAllFloors() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (tiles[y][x].isWalkable()) {
                    // 不在玩家出生点放豆子
                    if (x != spawnX || y != spawnY) {
                        addDot(x, y);
                    }
                }
            }
        }
    }
    
    /**
     * 添加道具
     * @param x X坐标
     * @param y Y坐标
     * @param type 道具类型
     */
    public void addItem(int x, int y, ItemType type) {
        if (!isValidPosition(x, y)) return;
        
        Item item = switch (type) {
            case MAGNET -> new Magnet(x, y);
            case SHIELD -> new Shield(x, y);
            case WALL_PASS -> new WallPass(x, y);
        };
        
        items.add(item);
        
        // 移除该位置的豆子（如果有的话）
        dots.removeIf(dot -> dot.getTileX() == x && dot.getTileY() == y);
    }
    
    /**
     * 添加敌人
     * @param x X坐标
     * @param y Y坐标
     * @param enemyType 敌人类型字符串
     * @return 添加的敌人对象
     */
    public Enemy addEnemy(int x, int y, String enemyType) {
        // 验证生成位置是否有效，如果无效则寻找最近的有效位置
        int spawnX = x;
        int spawnY = y;
        if (!isValidPosition(x, y) || !tiles[y][x].isWalkable()) {
            // 寻找最近的有效位置
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

        // 创建 final 变量供 lambda 使用
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

        // 移除该位置的豆子
        dots.removeIf(dot -> dot.getTileX() == finalSpawnX && dot.getTileY() == finalSpawnY);

        return enemy;
    }
    
    /**
     * 设置玩家出生点
     * @param x X坐标
     * @param y Y坐标
     */
    public void setSpawnPoint(int x, int y) {
        this.spawnX = x;
        this.spawnY = y;
    }
    
    /**
     * 检查位置是否有效
     * @param x X坐标
     * @param y Y坐标
     * @return 是否有效
     */
    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
    
    /**
     * 检查是否可以移动到指定位置
     * @param x X坐标（可以是小数）
     * @param y Y坐标（可以是小数）
     * @param canWallPass 是否可以穿墙
     * @return 是否可以移动
     */
    public boolean canMoveTo(double x, double y, boolean canWallPass) {
        int tileX = (int) Math.round(x);
        int tileY = (int) Math.round(y);

        if (!isValidPosition(tileX, tileY)) {
            return false;
        }

        Tile tile = tiles[tileY][tileX];

        // 穿墙效果
        if (canWallPass) {
            return true;
        }

        return tile.isWalkable();
    }

    /**
     * 检查是否可以从指定方向移动到目标位置
     * @param x X坐标（可以是小数）
     * @param y Y坐标（可以是小数）
     * @param fromDirection 移动的来源方向（玩家是从哪个方向过来的）
     * @param canWallPass 是否可以穿墙
     * @return 是否可以移动
     */
    public boolean canMoveTo(double x, double y, Direction fromDirection, boolean canWallPass) {
        int tileX = (int) Math.round(x);
        int tileY = (int) Math.round(y);

        if (!isValidPosition(tileX, tileY)) {
            return false;
        }

        Tile tile = tiles[tileY][tileX];

        // 穿墙效果
        if (canWallPass) {
            return true;
        }

        // 检查单向通道
        if (tile.getType() == TileType.ONE_WAY) {
            return tile.canEnterFrom(fromDirection);
        }

        return tile.isWalkable();
    }
    
    /**
     * 检查是否可以从某个方向进入指定位置
     * @param x 目标X坐标
     * @param y 目标Y坐标
     * @param fromDirection 来源方向
     * @param canWallPass 是否可以穿墙
     * @return 是否可以进入
     */
    public boolean canEnterFrom(int x, int y, Direction fromDirection, boolean canWallPass) {
        if (!isValidPosition(x, y)) {
            return false;
        }
        
        if (canWallPass) {
            return true;
        }
        
        Tile tile = tiles[y][x];
        return tile.canEnterFrom(fromDirection);
    }
    
    /**
     * 更新地图（豆子收集、道具效果等）
     * @param player 玩家
     * @param deltaTime 时间间隔
     */
    public void update(Player player, double deltaTime) {
        int playerTileX = player.getTileX();
        int playerTileY = player.getTileY();
        
        // 处理地图格子效果
        if (isValidPosition(playerTileX, playerTileY)) {
            Tile currentTile = tiles[playerTileY][playerTileX];
            currentTile.onStep(player);
        }
        
        // 收集豆子
        for (Dot dot : dots) {
            if (!dot.isCollected() && dot.canBeCollectedBy(player)) {
                // 磁铁效果：扩大收集范围
                if (player.hasEffect(ItemType.MAGNET)) {
                    double dist = Math.sqrt(
                            Math.pow(dot.getGridX() - player.getGridX(), 2) +
                            Math.pow(dot.getGridY() - player.getGridY(), 2)
                    );
                    if (dist <= Constants.MAGNET_RANGE) {
                        dot.collect();
                    }
                } else {
                    dot.collect();
                }
            }
        }
        
        // 磁铁效果：吸引范围内的豆子
        if (player.hasEffect(ItemType.MAGNET)) {
            for (Dot dot : dots) {
                if (!dot.isCollected()) {
                    double dist = Math.sqrt(
                            Math.pow(dot.getGridX() - player.getGridX(), 2) +
                            Math.pow(dot.getGridY() - player.getGridY(), 2)
                    );
                    if (dist <= Constants.MAGNET_RANGE) {
                        dot.collect();
                    }
                }
            }
        }
        
        // 收集道具
        for (Item item : items) {
            if (!item.isCollected() && item.canBeCollectedBy(player)) {
                item.collect(player);
            }
        }
        
        // 更新道具动画
        for (Item item : items) {
            item.update(deltaTime);
        }
        
        // 更新敌人
        for (Enemy enemy : enemies) {
            enemy.setPlayer(player);
            enemy.update(deltaTime);
        }
    }
    
    /**
     * 检查敌人碰撞
     * @param player 玩家
     * @return 是否发生碰撞（游戏结束）
     */
    public boolean checkEnemyCollision(Player player) {
        if (player.isJumping()) {
            return false; // 跳跃中不会碰撞
        }
        
        for (Enemy enemy : enemies) {
            if (enemy.collidesWithPlayer()) {
                // 检查护盾
                if (player.consumeShield()) {
                    System.out.println("护盾抵挡了攻击！");
                    // 将敌人推开一点
                    continue;
                }
                return true; // 游戏结束
            }
        }
        return false;
    }
    
    /**
     * 检查是否所有豆子都被收集
     * @return 是否全部收集
     */
    public boolean allDotsCollected() {
        for (Dot dot : dots) {
            if (!dot.isCollected()) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 获取剩余豆子数量
     * @return 剩余豆子数
     */
    public int getRemainingDots() {
        int count = 0;
        for (Dot dot : dots) {
            if (!dot.isCollected()) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * 渲染地图
     * @param gc 图形上下文
     * @param player 玩家（用于致盲效果）
     */
    public void render(GraphicsContext gc, Player player) {
        // 渲染地图格子
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // 致盲效果检查
                if (player != null && player.isBlinded()) {
                    double dist = Math.sqrt(
                            Math.pow(x - player.getGridX(), 2) +
                            Math.pow(y - player.getGridY(), 2)
                    );
                    if (dist > Constants.BLIND_VISIBLE_RANGE) {
                        // 超出可见范围，绘制黑色
                        gc.setFill(Color.BLACK);
                        gc.fillRect(x * Constants.TILE_SIZE, y * Constants.TILE_SIZE,
                                Constants.TILE_SIZE, Constants.TILE_SIZE);
                        continue;
                    }
                }
                
                tiles[y][x].render(gc);
            }
        }
        
        // 渲染豆子
        for (Dot dot : dots) {
            if (!player.isBlinded() || isInVisibleRange(dot, player)) {
                dot.render(gc);
            }
        }
        
        // 渲染道具
        for (Item item : items) {
            if (!player.isBlinded() || isInVisibleRange(item, player)) {
                item.render(gc);
            }
        }
        
        // 渲染敌人
        for (Enemy enemy : enemies) {
            if (!player.isBlinded() || isInVisibleRange(enemy, player)) {
                enemy.render(gc);
            }
        }
    }
    
    /**
     * 检查实体是否在玩家可见范围内（致盲效果）
     */
    private boolean isInVisibleRange(com.pacman.entity.Entity entity, Player player) {
        if (player == null || !player.isBlinded()) {
            return true;
        }
        
        double dist = Math.sqrt(
                Math.pow(entity.getGridX() - player.getGridX(), 2) +
                Math.pow(entity.getGridY() - player.getGridY(), 2)
        );
        
        return dist <= Constants.BLIND_VISIBLE_RANGE;
    }
    
    // ==================== Getter ====================
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public int getSpawnX() {
        return spawnX;
    }
    
    public int getSpawnY() {
        return spawnY;
    }
    
    public List<Dot> getDots() {
        return dots;
    }
    
    public List<Item> getItems() {
        return items;
    }
    
    public List<Enemy> getEnemies() {
        return enemies;
    }
}
