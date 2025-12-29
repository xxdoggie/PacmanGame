package com.pacman.map.tile;

import com.pacman.entity.Entity;
import com.pacman.entity.Player;
import com.pacman.util.Constants;
import com.pacman.util.Direction;
import com.pacman.util.SoundManager;
import com.pacman.util.SoundManager.SoundType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Base tile class for all map elements
 */
public class Tile {
    protected int gridX, gridY;
    protected TileType type;
    protected Direction direction;
    protected Tile linkedTile;
    protected com.pacman.map.GameMap gameMap;

    /**
     * 构造函数
     * @param gridX X坐标
     * @param gridY Y坐标
     * @param type 格子类型
     */
    public Tile(int gridX, int gridY, TileType type) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.type = type;
        this.direction = Direction.NONE;
        this.linkedTile = null;
    }
    
    /**
     * 渲染格子
     * @param gc 图形上下文
     */
    public void render(GraphicsContext gc) {
        double pixelX = gridX * Constants.TILE_SIZE;
        double pixelY = gridY * Constants.TILE_SIZE;
        
        // 绘制基础背景
        gc.setFill(Color.web(type.getColor()));
        gc.fillRect(pixelX, pixelY, Constants.TILE_SIZE, Constants.TILE_SIZE);
        
        // 根据类型绘制特殊效果
        renderSpecialEffect(gc, pixelX, pixelY);
    }
    
    /**
     * 绘制特殊效果（由子类重写）
     * @param gc 图形上下文
     * @param x 像素X坐标
     * @param y 像素Y坐标
     */
    protected void renderSpecialEffect(GraphicsContext gc, double x, double y) {
        switch (type) {
            case WALL -> renderWall(gc, x, y);
            case PORTAL -> renderPortal(gc, x, y);
            case ONE_WAY -> renderOneWay(gc, x, y);
            case ICE -> renderIce(gc, x, y);
            case JUMP_PAD -> renderJumpPad(gc, x, y);
            case SPEED_UP -> renderSpeedUp(gc, x, y);
            case SLOW_DOWN -> renderSlowDown(gc, x, y);
            case BLIND_TRAP -> renderBlindTrap(gc, x, y);
            default -> {}
        }
    }
    
    /**
     * 绘制墙壁
     */
    private void renderWall(GraphicsContext gc, double x, double y) {
        // 添加边框效果
        gc.setStroke(Color.web("#0066CC"));
        gc.setLineWidth(2);
        gc.strokeRect(x + 1, y + 1, Constants.TILE_SIZE - 2, Constants.TILE_SIZE - 2);
        
        // 添加高光
        gc.setStroke(Color.web("#3399FF", 0.5));
        gc.strokeLine(x + 3, y + 3, x + Constants.TILE_SIZE - 3, y + 3);
        gc.strokeLine(x + 3, y + 3, x + 3, y + Constants.TILE_SIZE - 3);
    }
    
    /**
     * 绘制传送门
     */
    private void renderPortal(GraphicsContext gc, double x, double y) {
        double centerX = x + Constants.TILE_SIZE / 2.0;
        double centerY = y + Constants.TILE_SIZE / 2.0;
        double radius = Constants.TILE_SIZE / 3.0;
        
        // 绘制旋转效果
        double rotation = (System.currentTimeMillis() % 3000) / 3000.0 * Math.PI * 2;
        
        gc.setStroke(Color.web("#FF00FF", 0.8));
        gc.setLineWidth(2);
        
        for (int i = 0; i < 4; i++) {
            double angle = rotation + i * Math.PI / 2;
            double startX = centerX + Math.cos(angle) * radius * 0.5;
            double startY = centerY + Math.sin(angle) * radius * 0.5;
            double endX = centerX + Math.cos(angle) * radius;
            double endY = centerY + Math.sin(angle) * radius;
            gc.strokeLine(startX, startY, endX, endY);
        }
        
        // 中心圆
        gc.setFill(Color.web("#FF00FF", 0.6));
        gc.fillOval(centerX - radius * 0.4, centerY - radius * 0.4, radius * 0.8, radius * 0.8);
    }
    
    /**
     * 绘制单向通道
     */
    private void renderOneWay(GraphicsContext gc, double x, double y) {
        double centerX = x + Constants.TILE_SIZE / 2.0;
        double centerY = y + Constants.TILE_SIZE / 2.0;
        
        // 绘制箭头
        gc.setFill(Color.WHITE);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        
        double arrowSize = Constants.TILE_SIZE / 3.0;
        double[] xPoints, yPoints;
        
        switch (direction) {
            case UP -> {
                xPoints = new double[]{centerX, centerX - arrowSize/2, centerX + arrowSize/2};
                yPoints = new double[]{centerY - arrowSize/2, centerY + arrowSize/2, centerY + arrowSize/2};
            }
            case DOWN -> {
                xPoints = new double[]{centerX, centerX - arrowSize/2, centerX + arrowSize/2};
                yPoints = new double[]{centerY + arrowSize/2, centerY - arrowSize/2, centerY - arrowSize/2};
            }
            case LEFT -> {
                xPoints = new double[]{centerX - arrowSize/2, centerX + arrowSize/2, centerX + arrowSize/2};
                yPoints = new double[]{centerY, centerY - arrowSize/2, centerY + arrowSize/2};
            }
            case RIGHT -> {
                xPoints = new double[]{centerX + arrowSize/2, centerX - arrowSize/2, centerX - arrowSize/2};
                yPoints = new double[]{centerY, centerY - arrowSize/2, centerY + arrowSize/2};
            }
            default -> {
                xPoints = new double[]{centerX, centerX - arrowSize/2, centerX + arrowSize/2};
                yPoints = new double[]{centerY - arrowSize/2, centerY + arrowSize/2, centerY + arrowSize/2};
            }
        }
        
        gc.fillPolygon(xPoints, yPoints, 3);
    }
    
    /**
     * 绘制冰面
     */
    private void renderIce(GraphicsContext gc, double x, double y) {
        // 添加冰晶效果
        gc.setStroke(Color.web("#FFFFFF", 0.5));
        gc.setLineWidth(1);
        
        // 绘制几条交叉线模拟冰晶
        gc.strokeLine(x + 5, y + 5, x + Constants.TILE_SIZE - 5, y + Constants.TILE_SIZE - 5);
        gc.strokeLine(x + Constants.TILE_SIZE - 5, y + 5, x + 5, y + Constants.TILE_SIZE - 5);
        gc.strokeLine(x + Constants.TILE_SIZE / 2, y + 3, x + Constants.TILE_SIZE / 2, y + Constants.TILE_SIZE - 3);
        gc.strokeLine(x + 3, y + Constants.TILE_SIZE / 2, x + Constants.TILE_SIZE - 3, y + Constants.TILE_SIZE / 2);
    }
    
    /**
     * 绘制跳板
     */
    private void renderJumpPad(GraphicsContext gc, double x, double y) {
        double centerX = x + Constants.TILE_SIZE / 2.0;
        double centerY = y + Constants.TILE_SIZE / 2.0;
        
        // 绘制弹簧效果
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        
        // 底座
        gc.strokeLine(x + 5, y + Constants.TILE_SIZE - 5, x + Constants.TILE_SIZE - 5, y + Constants.TILE_SIZE - 5);
        
        // 弹簧
        double springY = y + Constants.TILE_SIZE - 8;
        for (int i = 0; i < 3; i++) {
            gc.strokeLine(x + 10 + i * 6, springY - i * 6, x + 16 + i * 6, springY - i * 6 - 4);
            gc.strokeLine(x + 16 + i * 6, springY - i * 6 - 4, x + 10 + (i + 1) * 6, springY - (i + 1) * 6);
        }
        
        // 箭头指示跳跃方向
        gc.setFill(Color.YELLOW);
        gc.fillPolygon(
                new double[]{centerX, centerX - 6, centerX + 6},
                new double[]{y + 8, y + 16, y + 16},
                3
        );
    }
    
    /**
     * 绘制加速带
     */
    private void renderSpeedUp(GraphicsContext gc, double x, double y) {
        gc.setFill(Color.YELLOW);
        double centerX = x + Constants.TILE_SIZE / 2.0;
        double centerY = y + Constants.TILE_SIZE / 2.0;
        
        // 绘制两个向前的箭头
        for (int i = 0; i < 2; i++) {
            double offsetX = (i - 0.5) * 10;
            gc.fillPolygon(
                    new double[]{centerX + offsetX + 8, centerX + offsetX - 4, centerX + offsetX - 4},
                    new double[]{centerY, centerY - 6, centerY + 6},
                    3
            );
        }
    }
    
    /**
     * 绘制减速带
     */
    private void renderSlowDown(GraphicsContext gc, double x, double y) {
        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(3);
        
        // 绘制几条横线模拟减速带
        for (int i = 0; i < 4; i++) {
            double lineY = y + 6 + i * 8;
            gc.strokeLine(x + 4, lineY, x + Constants.TILE_SIZE - 4, lineY);
        }
    }
    
    /**
     * 绘制致盲陷阱
     */
    private void renderBlindTrap(GraphicsContext gc, double x, double y) {
        double centerX = x + Constants.TILE_SIZE / 2.0;
        double centerY = y + Constants.TILE_SIZE / 2.0;
        
        // 绘制眼睛图标（被划掉）
        gc.setStroke(Color.web("#9932CC"));
        gc.setLineWidth(2);
        
        // 眼睛形状
        gc.strokeOval(centerX - 10, centerY - 5, 20, 10);
        gc.fillOval(centerX - 4, centerY - 3, 8, 6);
        
        // 划掉线
        gc.setStroke(Color.RED);
        gc.strokeLine(x + 8, y + 8, x + Constants.TILE_SIZE - 8, y + Constants.TILE_SIZE - 8);
    }
    
    /**
     * 当实体踩到格子时的效果
     * @param entity 踩到的实体
     */
    public void onStep(Entity entity) {
        if (!(entity instanceof Player player)) {
            // 非玩家实体的处理
            handleNonPlayerStep(entity);
            return;
        }

        // 如果不是冰面，重置冰面状态
        if (type != TileType.ICE) {
            player.setOnIce(false);
        }

        switch (type) {
            case ICE -> player.setOnIce(true);
            case SPEED_UP -> {
                player.applySpeedModifier(Constants.SPEED_UP_MULTIPLIER);
                SoundManager.getInstance().play(SoundType.SPEED_UP);
            }
            case SLOW_DOWN -> {
                player.applySpeedModifier(Constants.SLOW_DOWN_MULTIPLIER);
                SoundManager.getInstance().play(SoundType.SLOW_DOWN);
            }
            case BLIND_TRAP -> {
                player.applyBlind(Constants.BLIND_DURATION);
                // 陷阱触发后可以选择消失或保留
            }
            case JUMP_PAD -> handleJumpPad(player);
            case PORTAL -> handlePortal(player);
            default -> {}
        }
    }
    
    /**
     * 当实体离开格子时的效果
     * @param entity 离开的实体
     */
    public void onLeave(Entity entity) {
        if (entity instanceof Player player) {
            if (type == TileType.ICE) {
                player.setOnIce(false);
            }
        }
    }
    
    /**
     * 处理非玩家实体踩到格子
     * @param entity 实体
     */
    private void handleNonPlayerStep(Entity entity) {
        // 敌人也会受到部分地图效果影响
        switch (type) {
            case SPEED_UP -> {
                // 敌人加速
                double originalSpeed = entity.getSpeed();
                entity.setSpeed(originalSpeed * Constants.SPEED_UP_MULTIPLIER);
            }
            case SLOW_DOWN -> {
                double originalSpeed = entity.getSpeed();
                entity.setSpeed(originalSpeed * Constants.SLOW_DOWN_MULTIPLIER);
            }
            case PORTAL -> {
                if (linkedTile != null) {
                    entity.setGridX(linkedTile.getGridX());
                    entity.setGridY(linkedTile.getGridY());
                }
            }
            default -> {}
        }
    }
    
    /**
     * 处理跳板效果
     * 向移动方向搜索最近的可落脚点（可以穿越墙壁）
     * @param player 玩家
     */
    private void handleJumpPad(Player player) {
        if (player.isJumping()) {
            return; // 已经在跳跃中
        }

        // 根据跳板方向或玩家移动方向确定跳跃方向
        Direction jumpDir = (direction != Direction.NONE) ? direction : player.getDirection();
        if (jumpDir == Direction.NONE) {
            return; // 没有方向，不跳跃
        }

        // 向移动方向搜索最近的可落脚点（可以穿越墙壁）
        int targetX = -1;
        int targetY = -1;

        // 确定搜索范围（取地图较大的维度）
        int maxSearchDistance = Math.max(Constants.MAP_COLS, Constants.MAP_ROWS);

        for (int dist = 1; dist <= maxSearchDistance; dist++) {
            int testX = gridX + jumpDir.getDx() * dist;
            int testY = gridY + jumpDir.getDy() * dist;

            // 边界检查
            if (testX < 0 || testX >= Constants.MAP_COLS || testY < 0 || testY >= Constants.MAP_ROWS) {
                break; // 超出地图边界，停止搜索
            }

            // 检查是否是可落脚点（不是墙）
            if (gameMap != null && gameMap.canMoveTo(testX, testY, false)) {
                // 找到第一个可落脚点（最近的）
                targetX = testX;
                targetY = testY;
                break;
            }
            // 如果是墙，继续搜索（跳板可以穿越墙壁）
        }

        // 只有当找到有效落脚点时才跳跃
        if (targetX != -1 && targetY != -1) {
            player.startJump(targetX, targetY);
            SoundManager.getInstance().play(SoundType.JUMP);
        }
    }
    
    /**
     * 处理传送门效果
     * @param player 玩家
     */
    private void handlePortal(Player player) {
        if (linkedTile != null && player.canTeleport()) {
            // 传送到关联的传送门
            player.setGridX(linkedTile.getGridX());
            player.setGridY(linkedTile.getGridY());
            // 设置传送冷却，防止立即传送回来
            player.setPortalCooldown(0.5);
            SoundManager.getInstance().play(SoundType.TELEPORT);
        }
    }
    
    /**
     * 检查是否可以从指定方向进入
     * @param fromDirection 来源方向
     * @return 是否可以进入
     */
    public boolean canEnterFrom(Direction fromDirection) {
        if (!type.isWalkable()) {
            return false;
        }
        
        // 单向通道检查
        if (type == TileType.ONE_WAY) {
            // 只能从指定方向的对面进入
            return fromDirection == direction.getOpposite();
        }
        
        return true;
    }
    
    // ==================== Getter 和 Setter ====================
    
    public int getGridX() {
        return gridX;
    }
    
    public int getGridY() {
        return gridY;
    }
    
    public TileType getType() {
        return type;
    }
    
    public Direction getDirection() {
        return direction;
    }
    
    public void setDirection(Direction direction) {
        this.direction = direction;
    }
    
    public Tile getLinkedTile() {
        return linkedTile;
    }
    
    public void setLinkedTile(Tile linkedTile) {
        this.linkedTile = linkedTile;
    }

    public void setGameMap(com.pacman.map.GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public boolean isWalkable() {
        return type.isWalkable();
    }
}
