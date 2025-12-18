package com.pacman.entity.enemy;

import com.pacman.util.Constants;
import com.pacman.util.Direction;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * 幻影敌人
 * AI行为：慢速、固定路线巡逻、间歇性隐身
 */
public class Phantom extends Enemy {
    
    /** 是否处于隐身状态 */
    private boolean invisible;
    
    /** 隐身/显形计时器 */
    private double visibilityTimer;
    
    /** 当前显形持续时间 */
    private double visibleDuration;
    
    /** 当前隐身持续时间 */
    private double invisibleDuration;
    
    /** 巡逻路径 */
    private List<int[]> patrolPath;
    
    /** 当前路径点索引 */
    private int currentPathIndex;
    
    /** 是否正向巡逻 */
    private boolean forwardPatrol;
    
    /** 透明度（用于渐变效果） */
    private double opacity;
    
    /**
     * 构造函数
     * @param gridX 初始X坐标
     * @param gridY 初始Y坐标
     */
    public Phantom(double gridX, double gridY) {
        super(gridX, gridY, Constants.PHANTOM_SPEED, Constants.COLOR_PHANTOM);
        this.invisible = false;
        this.visibilityTimer = 0;
        this.visibleDuration = Constants.PHANTOM_INVISIBLE_CYCLE - Constants.PHANTOM_INVISIBLE_DURATION;
        this.invisibleDuration = Constants.PHANTOM_INVISIBLE_DURATION;
        this.patrolPath = new ArrayList<>();
        this.currentPathIndex = 0;
        this.forwardPatrol = true;
        this.opacity = 1.0;
        this.moveInterval = 0.2;
    }
    
    @Override
    public void update(double deltaTime) {
        // 更新隐身状态
        updateVisibility(deltaTime);
        
        // 调用父类更新
        super.update(deltaTime);
    }
    
    /**
     * 更新隐身状态
     * @param deltaTime 时间间隔
     */
    private void updateVisibility(double deltaTime) {
        visibilityTimer += deltaTime;
        
        if (invisible) {
            // 隐身状态
            if (visibilityTimer >= invisibleDuration) {
                // 结束隐身
                invisible = false;
                visibilityTimer = 0;
            }
            // 更新透明度（渐变效果）
            if (visibilityTimer < 0.3) {
                opacity = 1.0 - visibilityTimer / 0.3 * 0.8; // 淡出
            } else if (visibilityTimer > invisibleDuration - 0.3) {
                opacity = 0.2 + (visibilityTimer - (invisibleDuration - 0.3)) / 0.3 * 0.8; // 淡入
            } else {
                opacity = 0.2; // 完全隐身时仍有微弱可见
            }
        } else {
            // 显形状态
            if (visibilityTimer >= visibleDuration) {
                // 开始隐身
                invisible = true;
                visibilityTimer = 0;
            }
            opacity = 1.0;
        }
    }
    
    /**
     * 设置巡逻路径
     * @param path 路径点列表
     */
    public void setPatrolPath(List<int[]> path) {
        this.patrolPath = path;
        if (!path.isEmpty()) {
            currentPathIndex = findNearestPathPoint();
        }
    }
    
    /**
     * 添加巡逻点
     * @param x X坐标
     * @param y Y坐标
     */
    public void addPatrolPoint(int x, int y) {
        patrolPath.add(new int[]{x, y});
    }
    
    /**
     * 查找最近的路径点
     */
    private int findNearestPathPoint() {
        double minDist = Double.MAX_VALUE;
        int nearestIndex = 0;
        
        for (int i = 0; i < patrolPath.size(); i++) {
            int[] point = patrolPath.get(i);
            double dist = Math.sqrt(
                    Math.pow(gridX - point[0], 2) +
                    Math.pow(gridY - point[1], 2)
            );
            if (dist < minDist) {
                minDist = dist;
                nearestIndex = i;
            }
        }
        
        return nearestIndex;
    }
    
    @Override
    protected void decideDirection() {
        if (patrolPath.isEmpty()) {
            // 没有路径时随机移动
            List<Direction> validDirs = getValidDirectionsNoReverse();
            if (validDirs.isEmpty()) {
                validDirs = getValidDirections();
            }
            if (!validDirs.isEmpty()) {
                if (direction != Direction.NONE && validDirs.contains(direction) && random.nextDouble() < 0.8) {
                    return;
                }
                direction = validDirs.get(random.nextInt(validDirs.size()));
            }
            return;
        }
        
        // 获取当前目标点
        int[] targetPoint = patrolPath.get(currentPathIndex);
        
        // 检查是否到达目标点
        double distToTarget = Math.sqrt(
                Math.pow(gridX - targetPoint[0], 2) +
                Math.pow(gridY - targetPoint[1], 2)
        );
        
        if (distToTarget < 0.2) {
            // 更新路径索引
            if (forwardPatrol) {
                currentPathIndex++;
                if (currentPathIndex >= patrolPath.size()) {
                    currentPathIndex = patrolPath.size() - 2;
                    forwardPatrol = false;
                    if (currentPathIndex < 0) currentPathIndex = 0;
                }
            } else {
                currentPathIndex--;
                if (currentPathIndex < 0) {
                    currentPathIndex = 1;
                    forwardPatrol = true;
                    if (currentPathIndex >= patrolPath.size()) currentPathIndex = 0;
                }
            }
            targetPoint = patrolPath.get(currentPathIndex);
        }
        
        // 朝目标移动
        double dx = targetPoint[0] - gridX;
        double dy = targetPoint[1] - gridY;
        
        List<Direction> validDirs = getValidDirections();
        if (validDirs.isEmpty()) {
            direction = Direction.NONE;
            return;
        }
        
        Direction bestDir = validDirs.get(0);
        double bestScore = Double.MIN_VALUE;
        
        for (Direction dir : validDirs) {
            double score = dir.getDx() * dx + dir.getDy() * dy;
            if (score > bestScore) {
                bestScore = score;
                bestDir = dir;
            }
        }
        
        direction = bestDir;
    }
    
    @Override
    public void render(GraphicsContext gc) {
        double pixelX = getPixelX();
        double pixelY = getPixelY();
        
        // 设置透明度
        gc.setGlobalAlpha(opacity);
        
        // 冻结效果优先
        if (frozen) {
            gc.setFill(Color.LIGHTBLUE);
        } else {
            gc.setFill(Color.web(color));
        }
        
        // 绘制身体
        renderGhostBody(gc, pixelX, pixelY);
        
        // 绘制眼睛（幻影的眼睛更空洞）
        renderPhantomEyes(gc, pixelX, pixelY);
        
        // 隐身时添加波纹效果
        if (invisible && opacity > 0.1) {
            gc.setStroke(Color.web(color, 0.3));
            gc.setLineWidth(1);
            double waveOffset = (System.currentTimeMillis() % 1000) / 1000.0 * Math.PI * 2;
            for (int i = 0; i < 3; i++) {
                double waveRadius = ENEMY_RADIUS + 3 + i * 4 + Math.sin(waveOffset + i) * 2;
                gc.strokeOval(
                        pixelX - waveRadius,
                        pixelY - waveRadius,
                        waveRadius * 2,
                        waveRadius * 2
                );
            }
        }
        
        // 冻结效果
        if (frozen) {
            gc.setStroke(Color.CYAN);
            gc.setLineWidth(2);
            gc.strokeOval(
                    pixelX - ENEMY_RADIUS - 2,
                    pixelY - ENEMY_RADIUS - 2,
                    (ENEMY_RADIUS + 2) * 2,
                    (ENEMY_RADIUS + 2) * 2
            );
        }
        
        // 恢复透明度
        gc.setGlobalAlpha(1.0);
    }
    
    /**
     * 绘制幻影的眼睛
     */
    private void renderPhantomEyes(GraphicsContext gc, double x, double y) {
        // 空洞的眼睛（只有眼眶）
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeOval(x - ENEMY_RADIUS * 0.5, y - ENEMY_RADIUS * 0.4, ENEMY_RADIUS * 0.35, ENEMY_RADIUS * 0.45);
        gc.strokeOval(x + ENEMY_RADIUS * 0.15, y - ENEMY_RADIUS * 0.4, ENEMY_RADIUS * 0.35, ENEMY_RADIUS * 0.45);
        
        // 微弱的光点
        gc.setFill(Color.web("#FFFFFF", 0.6));
        double pupilOffsetX = direction.getDx() * 1.5;
        double pupilOffsetY = direction.getDy() * 1.5;
        gc.fillOval(x - ENEMY_RADIUS * 0.38 + pupilOffsetX, y - ENEMY_RADIUS * 0.25 + pupilOffsetY, ENEMY_RADIUS * 0.15, ENEMY_RADIUS * 0.2);
        gc.fillOval(x + ENEMY_RADIUS * 0.25 + pupilOffsetX, y - ENEMY_RADIUS * 0.25 + pupilOffsetY, ENEMY_RADIUS * 0.15, ENEMY_RADIUS * 0.2);
    }
    
    /**
     * 检查幻影是否可以碰撞玩家
     * 隐身时也可以碰撞，但可以考虑让玩家更难发现
     */
    @Override
    public boolean collidesWithPlayer() {
        // 幻影在隐身时仍然可以碰撞玩家
        return super.collidesWithPlayer();
    }
    
    /**
     * 生成默认巡逻路径
     */
    public void generateDefaultPath() {
        patrolPath.clear();
        
        int centerX = (int) gridX;
        int centerY = (int) gridY;
        int range = 4;
        
        // 生成一个较大的方形路径
        int[][] candidates = {
                {centerX, centerY - range},
                {centerX + range, centerY},
                {centerX, centerY + range},
                {centerX - range, centerY}
        };
        
        for (int[] point : candidates) {
            if (gameMap != null &&
                point[0] >= 0 && point[0] < Constants.MAP_COLS &&
                point[1] >= 0 && point[1] < Constants.MAP_ROWS &&
                gameMap.canMoveTo(point[0], point[1], false)) {
                patrolPath.add(point);
            }
        }
        
        if (patrolPath.isEmpty()) {
            patrolPath.add(new int[]{centerX, centerY});
        }
    }
    
    // ==================== Getter ====================
    
    public boolean isInvisible() {
        return invisible;
    }
    
    public double getOpacity() {
        return opacity;
    }
}
