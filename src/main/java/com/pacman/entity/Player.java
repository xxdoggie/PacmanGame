package com.pacman.entity;

import com.pacman.item.ItemType;
import com.pacman.map.GameMap;
import com.pacman.util.Constants;
import com.pacman.util.Direction;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

/**
 * 玩家类
 * 继承自Entity，实现玩家的移动、道具效果等功能
 */
public class Player extends Entity {
    
    /** 玩家想要移动的方向（用于预输入） */
    private Direction nextDirection;
    
    /** 当前激活的道具效果及其剩余时间 */
    private Map<ItemType, Double> activeEffects;
    
    /** 是否有护盾 */
    private boolean hasShield;
    
    /** 是否处于跳跃状态 */
    private boolean isJumping;
    
    /** 跳跃目标位置 */
    private double jumpTargetX;
    private double jumpTargetY;
    
    /** 跳跃进度（0-1） */
    private double jumpProgress;
    
    /** 是否处于致盲状态 */
    private boolean isBlinded;
    
    /** 致盲剩余时间 */
    private double blindTimer;
    
    /** 当前速度修正系数 */
    private double speedModifier;
    
    /** 是否在冰面上滑行 */
    private boolean onIce;
    
    /** 冰面滑行时的惯性方向 */
    private Direction iceDirection;
    
    /** 游戏地图引用 */
    private GameMap gameMap;
    
    /**
     * 构造函数
     * @param gridX 初始X坐标
     * @param gridY 初始Y坐标
     */
    public Player(double gridX, double gridY) {
        super(gridX, gridY);
        this.speed = Constants.PLAYER_BASE_SPEED;
        this.collisionRadius = Constants.PLAYER_RADIUS;
        this.nextDirection = Direction.NONE;
        this.activeEffects = new HashMap<>();
        this.hasShield = false;
        this.isJumping = false;
        this.isBlinded = false;
        this.blindTimer = 0;
        this.speedModifier = 1.0;
        this.onIce = false;
        this.iceDirection = Direction.NONE;
    }
    
    /**
     * 设置游戏地图引用
     * @param gameMap 游戏地图
     */
    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }
    
    @Override
    public void update(double deltaTime) {
        // 更新道具效果计时器
        updateEffects(deltaTime);
        
        // 更新致盲效果
        if (isBlinded) {
            blindTimer -= deltaTime;
            if (blindTimer <= 0) {
                isBlinded = false;
                blindTimer = 0;
            }
        }
        
        // 处理跳跃状态
        if (isJumping) {
            updateJump(deltaTime);
            return;
        }
        
        // 计算实际速度
        double actualSpeed = speed * speedModifier;
        
        // 处理移动
        if (direction != Direction.NONE || (onIce && iceDirection != Direction.NONE)) {
            Direction moveDir = onIce ? iceDirection : direction;
            
            double newX = gridX + moveDir.getDx() * actualSpeed * deltaTime;
            double newY = gridY + moveDir.getDy() * actualSpeed * deltaTime;
            
            // 检查是否可以移动（考虑穿墙效果）
            boolean canWallPass = hasEffect(ItemType.WALL_PASS);
            
            if (gameMap != null && gameMap.canMoveTo(newX, newY, canWallPass)) {
                gridX = newX;
                gridY = newY;

                // 处理地图边界传送
                handleMapBoundary();
            } else if (!onIce) {
                // 撞墙后对齐到格子中心并停止移动
                alignToGrid();
                direction = Direction.NONE;
            }
        }
        
        // 尝试切换到预输入的方向
        tryChangeDirection();
        
        // 重置速度修正
        speedModifier = 1.0;
    }
    
    /**
     * 更新跳跃状态
     * @param deltaTime 时间间隔
     */
    private void updateJump(double deltaTime) {
        jumpProgress += deltaTime * 3; // 跳跃速度
        
        if (jumpProgress >= 1.0) {
            // 跳跃完成
            gridX = jumpTargetX;
            gridY = jumpTargetY;
            isJumping = false;
            jumpProgress = 0;
        }
    }
    
    /**
     * 开始跳跃
     * @param targetX 目标X坐标
     * @param targetY 目标Y坐标
     */
    public void startJump(double targetX, double targetY) {
        isJumping = true;
        jumpTargetX = targetX;
        jumpTargetY = targetY;
        jumpProgress = 0;
    }
    
    /**
     * 尝试切换到预输入的方向
     */
    private void tryChangeDirection() {
        if (nextDirection == Direction.NONE || gameMap == null) {
            return;
        }
        
        // 检查是否接近格子中心
        double centerDist = Math.abs(gridX - Math.round(gridX)) + Math.abs(gridY - Math.round(gridY));
        if (centerDist < 0.15) {
            // 检查新方向是否可行
            int testX = getTileX() + nextDirection.getDx();
            int testY = getTileY() + nextDirection.getDy();
            
            boolean canWallPass = hasEffect(ItemType.WALL_PASS);
            if (gameMap.canMoveTo(testX, testY, canWallPass)) {
                direction = nextDirection;
                nextDirection = Direction.NONE;
                alignToGrid();
                
                // 如果在冰面上改变方向，更新惯性方向
                if (onIce) {
                    iceDirection = direction;
                }
            }
        }
    }
    
    /**
     * 对齐到格子中心
     */
    private void alignToGrid() {
        gridX = Math.round(gridX);
        gridY = Math.round(gridY);
    }
    
    /**
     * 处理地图边界（用于传送门等）
     */
    private void handleMapBoundary() {
        if (gridX < 0) gridX = Constants.MAP_COLS - 1;
        if (gridX >= Constants.MAP_COLS) gridX = 0;
        if (gridY < 0) gridY = Constants.MAP_ROWS - 1;
        if (gridY >= Constants.MAP_ROWS) gridY = 0;
    }
    
    /**
     * 更新道具效果计时器
     * @param deltaTime 时间间隔
     */
    private void updateEffects(double deltaTime) {
        activeEffects.entrySet().removeIf(entry -> {
            double newTime = entry.getValue() - deltaTime;
            if (newTime <= 0) {
                onEffectEnd(entry.getKey());
                return true;
            }
            entry.setValue(newTime);
            return false;
        });
    }
    
    /**
     * 道具效果结束时的处理
     * @param type 道具类型
     */
    private void onEffectEnd(ItemType type) {
        switch (type) {
            case WALL_PASS -> {
                // 穿墙结束，如果在墙里面需要处理
                System.out.println("穿墙效果结束");
            }
            case MAGNET -> {
                System.out.println("磁铁效果结束");
            }
            default -> {}
        }
    }
    
    /**
     * 添加道具效果
     * @param type 道具类型
     * @param duration 持续时间
     */
    public void addEffect(ItemType type, double duration) {
        if (type == ItemType.SHIELD) {
            hasShield = true;
        } else {
            activeEffects.put(type, duration);
        }
    }
    
    /**
     * 检查是否有某个道具效果
     * @param type 道具类型
     * @return 是否有该效果
     */
    public boolean hasEffect(ItemType type) {
        if (type == ItemType.SHIELD) {
            return hasShield;
        }
        return activeEffects.containsKey(type);
    }
    
    /**
     * 消耗护盾
     * @return 是否成功消耗护盾
     */
    public boolean consumeShield() {
        if (hasShield) {
            hasShield = false;
            return true;
        }
        return false;
    }
    
    /**
     * 应用致盲效果
     * @param duration 持续时间
     */
    public void applyBlind(double duration) {
        isBlinded = true;
        blindTimer = duration;
    }
    
    /**
     * 应用速度修正
     * @param modifier 速度修正系数
     */
    public void applySpeedModifier(double modifier) {
        this.speedModifier = modifier;
    }
    
    /**
     * 设置冰面状态
     * @param onIce 是否在冰面上
     */
    public void setOnIce(boolean onIce) {
        this.onIce = onIce;
        if (onIce && direction != Direction.NONE) {
            iceDirection = direction;
        } else if (!onIce) {
            iceDirection = Direction.NONE;
        }
    }
    
    /**
     * 设置下一个移动方向（预输入）
     * @param direction 方向
     */
    public void setNextDirection(Direction direction) {
        this.nextDirection = direction;
        // 如果当前没有在移动，直接设置当前方向
        if (this.direction == Direction.NONE) {
            this.direction = direction;
        }
    }
    
    @Override
    public void render(GraphicsContext gc) {
        double pixelX = getPixelX();
        double pixelY = getPixelY();
        
        // 如果在跳跃，绘制跳跃动画
        if (isJumping) {
            double startX = (gridX + 0.5) * Constants.TILE_SIZE;
            double startY = (gridY + 0.5) * Constants.TILE_SIZE;
            double endX = (jumpTargetX + 0.5) * Constants.TILE_SIZE;
            double endY = (jumpTargetY + 0.5) * Constants.TILE_SIZE;
            
            // 插值位置
            pixelX = startX + (endX - startX) * jumpProgress;
            pixelY = startY + (endY - startY) * jumpProgress;
            
            // 跳跃高度（抛物线）
            double jumpHeight = Math.sin(jumpProgress * Math.PI) * Constants.TILE_SIZE;
            pixelY -= jumpHeight;
        }
        
        // 绘制护盾效果
        if (hasShield) {
            gc.setFill(Color.web(Constants.COLOR_ITEM_SHIELD, 0.3));
            gc.fillOval(
                    pixelX - Constants.PLAYER_RADIUS - 5,
                    pixelY - Constants.PLAYER_RADIUS - 5,
                    (Constants.PLAYER_RADIUS + 5) * 2,
                    (Constants.PLAYER_RADIUS + 5) * 2
            );
        }
        
        // 绘制玩家主体
        gc.setFill(Color.web(Constants.COLOR_PLAYER));
        gc.fillOval(
                pixelX - Constants.PLAYER_RADIUS,
                pixelY - Constants.PLAYER_RADIUS,
                Constants.PLAYER_RADIUS * 2,
                Constants.PLAYER_RADIUS * 2
        );
        
        // 绘制嘴巴（根据方向）
        gc.setFill(Color.web(Constants.COLOR_FLOOR));
        double mouthAngle = switch (direction) {
            case RIGHT -> 0;
            case DOWN -> 90;
            case LEFT -> 180;
            case UP -> 270;
            default -> 0;
        };
        gc.fillArc(
                pixelX - Constants.PLAYER_RADIUS,
                pixelY - Constants.PLAYER_RADIUS,
                Constants.PLAYER_RADIUS * 2,
                Constants.PLAYER_RADIUS * 2,
                mouthAngle - 30,
                60,
                javafx.scene.shape.ArcType.ROUND
        );
        
        // 如果有穿墙效果，添加特效
        if (hasEffect(ItemType.WALL_PASS)) {
            gc.setStroke(Color.web(Constants.COLOR_ITEM_WALL_PASS, 0.7));
            gc.setLineWidth(2);
            gc.strokeOval(
                    pixelX - Constants.PLAYER_RADIUS - 3,
                    pixelY - Constants.PLAYER_RADIUS - 3,
                    (Constants.PLAYER_RADIUS + 3) * 2,
                    (Constants.PLAYER_RADIUS + 3) * 2
            );
        }
        
        // 如果有磁铁效果，显示吸引范围
        if (hasEffect(ItemType.MAGNET)) {
            gc.setStroke(Color.web(Constants.COLOR_ITEM_MAGNET, 0.3));
            gc.setLineWidth(1);
            double magnetRadius = Constants.MAGNET_RANGE * Constants.TILE_SIZE;
            gc.strokeOval(
                    pixelX - magnetRadius,
                    pixelY - magnetRadius,
                    magnetRadius * 2,
                    magnetRadius * 2
            );
        }
    }
    
    // ==================== Getter ====================
    
    public boolean isJumping() {
        return isJumping;
    }
    
    public boolean isBlinded() {
        return isBlinded;
    }
    
    public boolean hasShield() {
        return hasShield;
    }
    
    public Direction getNextDirection() {
        return nextDirection;
    }
}
