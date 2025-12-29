package com.pacman.entity.enemy;

import com.pacman.util.Constants;
import com.pacman.util.Direction;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Phantom enemy extending Enemy (OOP: Inheritance).
 * AI: Slow patrol with periodic invisibility, chases player when invisible.
 */
public class Phantom extends Enemy {

    private boolean invisible;
    private double visibilityTimer;
    private double visibleDuration;
    private double invisibleDuration;
    private List<int[]> patrolPath;
    private int currentPathIndex;
    private boolean forwardPatrol;
    private double opacity;

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
        updateVisibility(deltaTime);
        speed = invisible ? 3.0 : Constants.PHANTOM_SPEED;
        super.update(deltaTime);
    }

    private void updateVisibility(double deltaTime) {
        visibilityTimer += deltaTime;

        if (invisible) {
            if (visibilityTimer >= invisibleDuration) {
                invisible = false;
                visibilityTimer = 0;
            }
            // Fade in/out effect
            if (visibilityTimer < 0.3) {
                opacity = 1.0 - visibilityTimer / 0.3;
            } else if (visibilityTimer > invisibleDuration - 0.3) {
                opacity = (visibilityTimer - (invisibleDuration - 0.3)) / 0.3;
            } else {
                opacity = 0.0;
            }
        } else {
            if (visibilityTimer >= visibleDuration) {
                invisible = true;
                visibilityTimer = 0;
            }
            opacity = 1.0;
        }
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
        // Chase player when invisible
        if (invisible && player != null) {
            List<Direction> validDirs = getValidDirectionsNoReverse();
            if (validDirs.isEmpty()) validDirs = getValidDirections();
            if (validDirs.isEmpty()) {
                direction = Direction.NONE;
                return;
            }

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
            List<Direction> validDirs = getValidDirectionsNoReverse();
            if (validDirs.isEmpty()) validDirs = getValidDirections();
            if (!validDirs.isEmpty()) {
                if (direction != Direction.NONE && validDirs.contains(direction) && random.nextDouble() < 0.8) return;
                direction = validDirs.get(random.nextInt(validDirs.size()));
            }
            return;
        }

        int[] targetPoint = patrolPath.get(currentPathIndex);
        double distToTarget = Math.sqrt(Math.pow(gridX - targetPoint[0], 2) + Math.pow(gridY - targetPoint[1], 2));

        if (distToTarget < 0.2) {
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

        gc.setGlobalAlpha(opacity);

        if (frozen) {
            gc.setFill(Color.LIGHTBLUE);
        } else {
            gc.setFill(Color.web(color));
        }

        renderGhostBody(gc, pixelX, pixelY);
        renderPhantomEyes(gc, pixelX, pixelY);

        // Wave effect when invisible
        if (invisible && opacity > 0.1) {
            gc.setStroke(Color.web(color, 0.3));
            gc.setLineWidth(1);
            double waveOffset = (System.currentTimeMillis() % 1000) / 1000.0 * Math.PI * 2;
            for (int i = 0; i < 3; i++) {
                double waveRadius = ENEMY_RADIUS + 3 + i * 4 + Math.sin(waveOffset + i) * 2;
                gc.strokeOval(pixelX - waveRadius, pixelY - waveRadius, waveRadius * 2, waveRadius * 2);
            }
        }

        if (frozen) {
            gc.setStroke(Color.CYAN);
            gc.setLineWidth(2);
            gc.strokeOval(pixelX - ENEMY_RADIUS - 2, pixelY - ENEMY_RADIUS - 2, (ENEMY_RADIUS + 2) * 2, (ENEMY_RADIUS + 2) * 2);
        }

        gc.setGlobalAlpha(1.0);
    }

    private void renderPhantomEyes(GraphicsContext gc, double x, double y) {
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeOval(x - ENEMY_RADIUS * 0.5, y - ENEMY_RADIUS * 0.4, ENEMY_RADIUS * 0.35, ENEMY_RADIUS * 0.45);
        gc.strokeOval(x + ENEMY_RADIUS * 0.15, y - ENEMY_RADIUS * 0.4, ENEMY_RADIUS * 0.35, ENEMY_RADIUS * 0.45);

        gc.setFill(Color.web("#FFFFFF", 0.6));
        double pupilOffsetX = direction.getDx() * 1.5;
        double pupilOffsetY = direction.getDy() * 1.5;
        gc.fillOval(x - ENEMY_RADIUS * 0.38 + pupilOffsetX, y - ENEMY_RADIUS * 0.25 + pupilOffsetY, ENEMY_RADIUS * 0.15, ENEMY_RADIUS * 0.2);
        gc.fillOval(x + ENEMY_RADIUS * 0.25 + pupilOffsetX, y - ENEMY_RADIUS * 0.25 + pupilOffsetY, ENEMY_RADIUS * 0.15, ENEMY_RADIUS * 0.2);
    }

    @Override
    public boolean collidesWithPlayer() {
        return super.collidesWithPlayer();
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
            int[][] directions = {{0, -4}, {4, 0}, {0, 4}, {-4, 0}};
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

    public boolean isInvisible() { return invisible; }
    public double getOpacity() { return opacity; }
}
