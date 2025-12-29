package com.pacman.entity.enemy;

import com.pacman.util.Constants;
import com.pacman.util.Direction;

import java.util.List;

/**
 * Wanderer enemy extending Enemy (OOP: Inheritance).
 * AI: Moves randomly without a fixed patrol route.
 */
public class Wanderer extends Enemy {

    private static final double KEEP_DIRECTION_CHANCE = 0.7;

    public Wanderer(double gridX, double gridY) {
        super(gridX, gridY, Constants.WANDERER_SPEED, Constants.COLOR_WANDERER);
        this.moveInterval = 0.3;
    }

    @Override
    protected void decideDirection() {
        List<Direction> validDirs = getValidDirectionsNoReverse();
        if (validDirs.isEmpty()) validDirs = getValidDirections();
        if (validDirs.isEmpty()) {
            direction = Direction.NONE;
            return;
        }

        // Probability to keep current direction
        if (direction != Direction.NONE && validDirs.contains(direction) && random.nextDouble() < KEEP_DIRECTION_CHANCE) {
            return;
        }
        direction = validDirs.get(random.nextInt(validDirs.size()));
    }
}
