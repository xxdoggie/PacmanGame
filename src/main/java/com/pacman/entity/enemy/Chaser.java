package com.pacman.entity.enemy;

import com.pacman.util.Constants;
import com.pacman.util.Direction;

import java.util.List;

/**
 * Chaser enemy extending Enemy (OOP: Inheritance).
 * AI: Directly pursues player position.
 */
public class Chaser extends Enemy {

    public Chaser(double gridX, double gridY) {
        super(gridX, gridY, Constants.CHASER_SPEED, Constants.COLOR_CHASER);
    }

    @Override
    protected void decideDirection() {
        if (player == null) {
            List<Direction> validDirs = getValidDirections();
            if (!validDirs.isEmpty()) {
                direction = validDirs.get(random.nextInt(validDirs.size()));
            }
            return;
        }

        Direction towardsPlayer = getDirectionTowardsPlayer();
        List<Direction> validDirs = getValidDirectionsNoReverse();

        if (validDirs.isEmpty()) validDirs = getValidDirections();
        if (validDirs.isEmpty()) {
            direction = Direction.NONE;
            return;
        }

        if (validDirs.contains(towardsPlayer)) {
            direction = towardsPlayer;
            return;
        }

        // Choose direction that minimizes distance to player
        Direction bestDir = validDirs.get(0);
        double bestDist = Double.MAX_VALUE;

        for (Direction dir : validDirs) {
            int newX = getTileX() + dir.getDx();
            int newY = getTileY() + dir.getDy();
            double dist = Math.sqrt(Math.pow(newX - player.getGridX(), 2) + Math.pow(newY - player.getGridY(), 2));
            if (dist < bestDist) {
                bestDist = dist;
                bestDir = dir;
            }
        }
        direction = bestDir;
    }
}
