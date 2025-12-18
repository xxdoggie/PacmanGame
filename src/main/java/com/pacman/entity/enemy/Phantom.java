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

        // 隐身时速度加快
        if (invisible) {
            speed = Constants.PHANTOM_SPEED * 2.0;
        } else {
            speed = Constants.PHANTOM_SPEED;
        }

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
            // 更新透明度（完全隐身）
            if (visibilityTimer < 0.3) {
                opacity = 1.0 - visibilityTimer / 0.3; // 快速淡出
            } else if (visibilityTimer > invisibleDuration - 0.3) {
                opacity = (visibilityTimer - (invisibleDuration - 0.3)) / 0.3; // 淡入
            } else {
                opacity = 0.0; // 完全隐身
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
        // 隐身时追逐玩家，更具威胁性
        if (invisible && player != null) {
            List<Direction> validDirs = getValidDirectionsNoReverse();
            if (validDirs.isEmpty()) {
                validDirs = getValidDirections();
            }
            if (validDirs.isEmpty()) {
                direction = Direction.NONE;
                return;
            }

            // 追逐玩家
            double dx = player.getGridX() - gridX;
            double dy = player.getGridY() - gridY;

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
            return;
        }

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
     * 沿着可通行的路径探索，生成一条较长的巡逻路线
     */
    public void generateDefaultPath() {
        patrolPath.clear();

        if (gameMap == null) {
            patrolPath.add(new int[]{(int) gridX, (int) gridY});
            return;
        }

        int startX = (int) gridX;
        int startY = (int) gridY;

        // 添加起点
        patrolPath.add(new int[]{startX, startY});

        // 使用探索算法生成路径
        int currentX = startX;
        int currentY = startY;
        Direction lastDir = Direction.NONE;
        int minPathLength = 12; // 最小路径长度，确保巡逻路径足够长

        for (int step = 0; step < 30 && patrolPath.size() < minPathLength; step++) {
            Direction bestDir = null;
            int maxDistance = 0;

            for (Direction dir : Direction.validDirections()) {
                if (dir == lastDir.getOpposite() && patrolPath.size() > 1) {
                    continue;
                }

                int distance = 0;
                int testX = currentX;
                int testY = currentY;

                while (distance < 8) {
                    testX += dir.getDx();
                    testY += dir.getDy();

                    if (testX < 1 || testX >= Constants.MAP_COLS - 1 ||
                        testY < 1 || testY >= Constants.MAP_ROWS - 1 ||
                        !gameMap.canMoveTo(testX, testY, false)) {
                        break;
                    }
                    distance++;
                }

                if (distance > maxDistance) {
                    maxDistance = distance;
                    bestDir = dir;
                }
            }

            if (bestDir != null && maxDistance >= 2) {
                int walkDistance = Math.max(2, maxDistance / 2);
                currentX += bestDir.getDx() * walkDistance;
                currentY += bestDir.getDy() * walkDistance;

                int[] lastPoint = patrolPath.get(patrolPath.size() - 1);
                if (lastPoint[0] != currentX || lastPoint[1] != currentY) {
                    patrolPath.add(new int[]{currentX, currentY});
                }

                lastDir = bestDir;
            } else {
                lastDir = Direction.NONE;
            }
        }

        if (patrolPath.size() < 3) {
            int[][] directions = {{0, -4}, {4, 0}, {0, 4}, {-4, 0}};
            for (int[] delta : directions) {
                int px = startX + delta[0];
                int py = startY + delta[1];
                if (px >= 1 && px < Constants.MAP_COLS - 1 &&
                    py >= 1 && py < Constants.MAP_ROWS - 1 &&
                    gameMap.canMoveTo(px, py, false)) {
                    patrolPath.add(new int[]{px, py});
                }
            }
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
