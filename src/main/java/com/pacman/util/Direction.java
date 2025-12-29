package com.pacman.util;

/**
 * Direction enumeration - defines four basic movement directions
 */
public enum Direction {
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0),
    NONE(0, 0);

    private final int dx;
    private final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int getDx() { return dx; }
    public int getDy() { return dy; }

    public Direction getOpposite() {
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
            case NONE -> NONE;
        };
    }
    
    public Direction rotateClockwise() {
        return switch (this) {
            case UP -> RIGHT;
            case RIGHT -> DOWN;
            case DOWN -> LEFT;
            case LEFT -> UP;
            case NONE -> NONE;
        };
    }
    
    public Direction rotateCounterClockwise() {
        return switch (this) {
            case UP -> LEFT;
            case LEFT -> DOWN;
            case DOWN -> RIGHT;
            case RIGHT -> UP;
            case NONE -> NONE;
        };
    }
    
    public boolean isHorizontal() { return this == LEFT || this == RIGHT; }
    public boolean isVertical() { return this == UP || this == DOWN; }

    public static Direction[] validDirections() {
        return new Direction[]{UP, DOWN, LEFT, RIGHT};
    }
}
