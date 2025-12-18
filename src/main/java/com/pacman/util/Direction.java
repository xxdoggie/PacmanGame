package com.pacman.util;

/**
 * 方向枚举类
 * 定义游戏中实体移动的四个基本方向
 */
public enum Direction {
    
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0),
    NONE(0, 0);
    
    /** X方向增量 */
    private final int dx;
    
    /** Y方向增量 */
    private final int dy;
    
    /**
     * 构造函数
     * @param dx X方向增量
     * @param dy Y方向增量
     */
    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }
    
    /**
     * 获取X方向增量
     * @return X方向增量
     */
    public int getDx() {
        return dx;
    }
    
    /**
     * 获取Y方向增量
     * @return Y方向增量
     */
    public int getDy() {
        return dy;
    }
    
    /**
     * 获取相反方向
     * @return 相反的方向
     */
    public Direction getOpposite() {
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
            case NONE -> NONE;
        };
    }
    
    /**
     * 顺时针旋转90度
     * @return 旋转后的方向
     */
    public Direction rotateClockwise() {
        return switch (this) {
            case UP -> RIGHT;
            case RIGHT -> DOWN;
            case DOWN -> LEFT;
            case LEFT -> UP;
            case NONE -> NONE;
        };
    }
    
    /**
     * 逆时针旋转90度
     * @return 旋转后的方向
     */
    public Direction rotateCounterClockwise() {
        return switch (this) {
            case UP -> LEFT;
            case LEFT -> DOWN;
            case DOWN -> RIGHT;
            case RIGHT -> UP;
            case NONE -> NONE;
        };
    }
    
    /**
     * 判断是否为水平方向
     * @return 是否为水平方向
     */
    public boolean isHorizontal() {
        return this == LEFT || this == RIGHT;
    }
    
    /**
     * 判断是否为垂直方向
     * @return 是否为垂直方向
     */
    public boolean isVertical() {
        return this == UP || this == DOWN;
    }
    
    /**
     * 获取所有有效方向（不包含NONE）
     * @return 四个基本方向的数组
     */
    public static Direction[] validDirections() {
        return new Direction[]{UP, DOWN, LEFT, RIGHT};
    }
}
