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
     * 在设置地图后调用，会生成一个矩形巡逻路线
     */
    public void generateDefaultPath() {
        patrolPath.clear();
        
        // 以当前位置为中心，生成一个简单的方形巡逻路径
        int centerX = (int) gridX;
        int centerY = (int) gridY;
        int range = 3; // 巡逻范围
        
        // 尝试生成一个方形路径
        int[][] candidates = {
                {centerX - range, centerY - range},
                {centerX + range, centerY - range},
                {centerX + range, centerY + range},
                {centerX - range, centerY + range}
        };
        
        for (int[] point : candidates) {
            // 检查点是否在地图范围内且可通行
            if (gameMap != null && 
                point[0] >= 0 && point[0] < Constants.MAP_COLS &&
                point[1] >= 0 && point[1] < Constants.MAP_ROWS &&
                gameMap.canMoveTo(point[0], point[1], false)) {
                patrolPath.add(point);
            }
        }
        
        // 如果没有有效路径点，至少添加当前位置
        if (patrolPath.isEmpty()) {
            patrolPath.add(new int[]{centerX, centerY});
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
