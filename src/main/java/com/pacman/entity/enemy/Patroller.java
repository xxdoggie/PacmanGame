package com.pacman.entity.enemy;

import com.pacman.util.Constants;
import com.pacman.util.Direction;

import java.util.ArrayList;
import java.util.List;

/**
 * Patroller enemy extending Enemy (OOP: Inheritance).
 * AI: Follows a fixed patrol route in a back-and-forth pattern.
 */
public class Patroller extends Enemy {

    private List<int[]> patrolPath;
    private int currentPathIndex;
    private boolean forwardPatrol;
    private static final double ARRIVAL_THRESHOLD = 0.2;

    public Patroller(double gridX, double gridY) {
        super(gridX, gridY, Constants.PATROLLER_SPEED, Constants.COLOR_PATROLLER);
        this.patrolPath = new ArrayList<>();
        this.currentPathIndex = 0;
        this.forwardPatrol = true;
        this.moveInterval = 0.1;
    }

    public void setPatrolPath(List<int[]> path) {
        this.patrolPath = path;
        if (!path.isEmpty()) currentPathIndex = findNearestPathPoint();
    }

    public void addPatrolPoint(int x, int y) {
        patrolPath.add(new int[]{x, y});
    }

    private int findNearestPathPoint() {
        double minDist = Double.MAX_VALUE;
        int nearestIndex = 0;
        for (int i = 0; i < patrolPath.size(); i++) {
            int[] point = patrolPath.get(i);
            double dist = Math.sqrt(Math.pow(gridX - point[0], 2) + Math.pow(gridY - point[1], 2));
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
            randomMove();
            return;
        }

        int[] targetPoint = patrolPath.get(currentPathIndex);
        double distToTarget = Math.sqrt(Math.pow(gridX - targetPoint[0], 2) + Math.pow(gridY - targetPoint[1], 2));

        if (distToTarget < ARRIVAL_THRESHOLD) {
            updatePathIndex();
            targetPoint = patrolPath.get(currentPathIndex);
        }
        moveTowardsPoint(targetPoint[0], targetPoint[1]);
    }

    private void updatePathIndex() {
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
    }

    private void moveTowardsPoint(int targetX, int targetY) {
        double dx = targetX - gridX;
        double dy = targetY - gridY;
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

    private void randomMove() {
        List<Direction> validDirs = getValidDirectionsNoReverse();
        if (validDirs.isEmpty()) validDirs = getValidDirections();

        if (!validDirs.isEmpty()) {
            if (direction != Direction.NONE && validDirs.contains(direction) && random.nextDouble() < 0.7) return;
            direction = validDirs.get(random.nextInt(validDirs.size()));
        } else {
            direction = Direction.NONE;
        }
    }

    /**
     * Generates a default patrol path by exploring the map.
     */
    public void generateDefaultPath() {
        patrolPath.clear();
        if (gameMap == null) {
            patrolPath.add(new int[]{(int) gridX, (int) gridY});
            return;
        }

        int startX = (int) gridX;
        int startY = (int) gridY;
        patrolPath.add(new int[]{startX, startY});

        int currentX = startX;
        int currentY = startY;
        Direction lastDir = Direction.NONE;
        int minPathLength = 12;

        for (int step = 0; step < 30 && patrolPath.size() < minPathLength; step++) {
            Direction bestDir = null;
            int maxDistance = 0;

            for (Direction dir : Direction.validDirections()) {
                if (dir == lastDir.getOpposite() && patrolPath.size() > 1) continue;

                int distance = 0;
                int testX = currentX;
                int testY = currentY;

                while (distance < 8) {
                    testX += dir.getDx();
                    testY += dir.getDy();
                    if (testX < 1 || testX >= Constants.MAP_COLS - 1 ||
                            testY < 1 || testY >= Constants.MAP_ROWS - 1 ||
                            !gameMap.canMoveTo(testX, testY, false)) break;
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
            int[][] directions = {{0, -3}, {3, 0}, {0, 3}, {-3, 0}};
            for (int[] delta : directions) {
                int px = startX + delta[0];
                int py = startY + delta[1];
                if (px >= 1 && px < Constants.MAP_COLS - 1 && py >= 1 && py < Constants.MAP_ROWS - 1 &&
                        gameMap.canMoveTo(px, py, false)) {
                    patrolPath.add(new int[]{px, py});
                }
            }
        }
    }

    public List<int[]> getPatrolPath() { return patrolPath; }
    public int getCurrentPathIndex() { return currentPathIndex; }
}
