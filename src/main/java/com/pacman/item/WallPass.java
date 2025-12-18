package com.pacman.item;

import com.pacman.entity.Player;
import com.pacman.util.Constants;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * 穿墙术道具
 * 效果：短暂时间可以穿越墙壁
 */
public class WallPass extends Item {
    
    /**
     * 构造函数
     * @param gridX 格子X坐标
     * @param gridY 格子Y坐标
     */
    public WallPass(int gridX, int gridY) {
        super(gridX, gridY, ItemType.WALL_PASS);
    }
    
    @Override
    protected void renderIcon(GraphicsContext gc, double x, double y) {
        // 绘制穿越图标（虚线墙+箭头）
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        
        // 虚线墙
        gc.setLineDashes(3);
        gc.strokeLine(x - 2, y - 7, x - 2, y + 7);
        gc.setLineDashes(0);
        
        // 穿越箭头
        gc.setFill(Color.WHITE);
        double[] xPoints = {x + 6, x, x, x + 6};
        double[] yPoints = {y, y - 4, y + 4, y};
        gc.fillPolygon(xPoints, yPoints, 4);
        
        // 箭头尾部
        gc.strokeLine(x - 6, y, x, y);
    }
    
    @Override
    public void applyEffect(Player player) {
        player.addEffect(ItemType.WALL_PASS, Constants.WALL_PASS_DURATION);
        System.out.println("获得穿墙效果！持续 " + Constants.WALL_PASS_DURATION + " 秒");
    }
}
