package com.pacman.item;

import com.pacman.entity.Player;
import com.pacman.util.Constants;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * 磁铁道具
 * 效果：自动吸取一定范围内的豆子
 */
public class Magnet extends Item {
    
    /**
     * 构造函数
     * @param gridX 格子X坐标
     * @param gridY 格子Y坐标
     */
    public Magnet(int gridX, int gridY) {
        super(gridX, gridY, ItemType.MAGNET);
    }
    
    @Override
    protected void renderIcon(GraphicsContext gc, double x, double y) {
        // 绘制U形磁铁图标
        gc.setStroke(Color.RED);
        gc.setLineWidth(3);
        
        // 左边红色部分
        gc.setStroke(Color.RED);
        gc.strokeLine(x - 6, y - 5, x - 6, y + 5);
        gc.strokeArc(x - 6, y + 2, 12, 10, 180, 90, javafx.scene.shape.ArcType.OPEN);
        
        // 右边蓝色部分
        gc.setStroke(Color.BLUE);
        gc.strokeLine(x + 6, y - 5, x + 6, y + 5);
        gc.strokeArc(x - 6, y + 2, 12, 10, 270, 90, javafx.scene.shape.ArcType.OPEN);
    }
    
    @Override
    public void applyEffect(Player player) {
        player.addEffect(ItemType.MAGNET, Constants.MAGNET_DURATION);
        System.out.println("获得磁铁效果！持续 " + Constants.MAGNET_DURATION + " 秒");
    }
}
