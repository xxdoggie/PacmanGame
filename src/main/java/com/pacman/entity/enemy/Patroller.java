package com.pacman.entity.enemy;

import com.pacman.util.Constants;
import com.pacman.util.Direction;

import java.util.ArrayList;
import java.util.List;

/**
 * 巡逻者敌人
 * AI行为：沿固定路线循环移动
 */
public class Patroller extends Enemy {
    
    /** 巡逻路径点列表 */
    private List<int[]> patrolPath;
    
    /** 当前目标路径点索引 */
    private int currentPathIndex;
    
    /** 是否正向巡逻 */
    private boolean forwardPatrol;
    
    /** 到达路径点的容差 */
    private static final double ARRIVAL_THRESHOLD = 0.2;
    
    /**
     * 构造函数
     * @param gridX 初始X坐标
     * @param gridY 初始Y坐标
     */
    public Patroller(double gridX, double gridY) {
        super(gridX, gridY, Constants.PATROLLER_SPEED, Constants.COLOR_PATROLLER);
        this.patrolPath = new ArrayList<>();
        this.currentPathIndex = 0;
        this.forwardPatrol = true;
        this.moveInterval = 0.1; // 巡逻者移动判定更频繁
    }
    
    /**
     * 设置巡逻路径
     * @param path 路径点列表，每个点是 int[]{x, y}
     */
    public void setPatrolPath(List<int[]> path) {
        this.patrolPath = path;
        if (!path.isEmpty()) {
            // 找到最近的路径点作为起点
            currentPathIndex = findNearestPathPoint();
        }
    }
    
    /**
     * 添加巡逻路径点
     * @param x 格子X坐标
     * @param y 格子Y坐标
     */
    public void addPatrolPoint(int x, int y) {
        patrolPath.add(new int[]{x, y});
    }
    
    /**
     * 查找最近的路径点
     * @return 最近路径点的索引
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
            // 没有巡逻路径时，像游荡者一样随机移动
            randomMove();
            return;
        }
        
        // 获取当前目标点
        int[] targetPoint = patrolPath.get(currentPathIndex);
        
        // 检查是否到达目标点
        double distToTarget = Math.sqrt(
                Math.pow(gridX - targetPoint[0], 2) +
                Math.pow(gridY - targetPoint[1], 2)
        );
        
        if (distToTarget < ARRIVAL_THRESHOLD) {
            // 到达目标点，切换到下一个
            updatePathIndex();
            targetPoint = patrolPath.get(currentPathIndex);
        }
        
        // 朝目标点移动
        moveTowardsPoint(targetPoint[0], targetPoint[1]);
    }
    
    /**
     * 更新路径索引
     */
    private void updatePathIndex() {
        if (forwardPatrol) {
            currentPathIndex++;
            if (currentPathIndex >= patrolPath.size()) {
                // 到达终点，反向巡逻
                currentPathIndex = patrolPath.size() - 2;
                forwardPatrol = false;
                
                // 如果只有一个点，保持不动
                if (currentPathIndex < 0) {
                    currentPathIndex = 0;
                }
            }
        } else {
            currentPathIndex--;
            if (currentPathIndex < 0) {
                // 到达起点，正向巡逻
                currentPathIndex = 1;
                forwardPatrol = true;
                
                // 如果只有一个点，保持不动
                if (currentPathIndex >= patrolPath.size()) {
                    currentPathIndex = 0;
                }
            }
        }
    }
    
    /**
     * 朝指定点移动
     * @param targetX 目标X坐标
     * @param targetY 目标Y坐标
     */
    private void moveTowardsPoint(int targetX, int targetY) {
        double dx = targetX - gridX;
        double dy = targetY - gridY;
        
        List<Direction> validDirs = getValidDirections();
        
        if (validDirs.isEmpty()) {
            direction = Direction.NONE;
            return;
        }
        
        // 选择最接近目标的方向
        Direction bestDir = validDirs.get(0);
        double bestScore = Double.MIN_VALUE;
        
        for (Direction dir : validDirs) {
            // 计算该方向与目标方向的匹配度
            double score = dir.getDx() * dx + dir.getDy() * dy;
            
            if (score > bestScore) {
                bestScore = score;
                bestDir = dir;
            }
        }
        
        direction = bestDir;
    }
    
    /**
     * 随机移动（无巡逻路径时的后备方案）
     */
    private void randomMove() {
        List<Direction> validDirs = getValidDirectionsNoReverse();
        
        if (validDirs.isEmpty()) {
            validDirs = getValidDirections();
        }
        
        if (!validDirs.isEmpty()) {
            if (direction != Direction.NONE && validDirs.contains(direction) && random.nextDouble() < 0.7) {
                return;
            }
            direction = validDirs.get(random.nextInt(validDirs.size()));
        } else {
            direction = Direction.NONE;
        }
    }
    
    /**
     * 根据地图自动生成巡逻路径
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

        // 使用探索算法生成路径：从起点开始，沿着可通行方向走
        int currentX = startX;
        int currentY = startY;
        Direction lastDir = Direction.NONE;
        int minPathLength = 12; // 最小路径长度，确保巡逻路径足够长

        // 尝试生成足够长的路径
        for (int step = 0; step < 30 && patrolPath.size() < minPathLength; step++) {
            Direction bestDir = null;
            int maxDistance = 0;

            // 尝试每个方向，找到能走最远的方向
            for (Direction dir : Direction.validDirections()) {
                // 避免立即折返
                if (dir == lastDir.getOpposite() && patrolPath.size() > 1) {
                    continue;
                }

                // 沿这个方向探索能走多远
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

            // 如果找到了可以走的方向
            if (bestDir != null && maxDistance >= 2) {
                // 走到这个方向的一半或至少2格
                int walkDistance = Math.max(2, maxDistance / 2);
                currentX += bestDir.getDx() * walkDistance;
                currentY += bestDir.getDy() * walkDistance;

                // 确保不重复添加相同的点
                int[] lastPoint = patrolPath.get(patrolPath.size() - 1);
                if (lastPoint[0] != currentX || lastPoint[1] != currentY) {
                    patrolPath.add(new int[]{currentX, currentY});
                }

                lastDir = bestDir;
            } else {
                // 无路可走，尝试换个方向
                lastDir = Direction.NONE;
            }
        }

        // 如果路径太短，尝试在起点周围找一些点
        if (patrolPath.size() < 3) {
            // 在起点周围找可通行的点
            int[][] directions = {{0, -3}, {3, 0}, {0, 3}, {-3, 0}};
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
    
    public List<int[]> getPatrolPath() {
        return patrolPath;
    }
    
    public int getCurrentPathIndex() {
        return currentPathIndex;
    }
}
