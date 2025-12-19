package com.pacman.ui;

import com.pacman.util.Constants;
import java.util.ArrayList;
import java.util.List;

/**
 * 关卡介绍数据类
 * 存储每个关卡引入的新元素信息
 */
public class LevelIntroData {

    /**
     * 新元素类型枚举
     */
    public enum ElementType {
        ENEMY,      // 敌人
        ITEM,       // 道具
        TERRAIN,    // 地形
        MECHANIC    // 游戏机制
    }

    /**
     * 新元素数据记录
     */
    public record NewElement(
            ElementType type,
            String name,
            String description,
            String tips,
            String color,
            String iconType  // 用于绘制图标的类型标识
    ) {}

    /**
     * 关卡介绍数据记录
     */
    public record LevelIntro(
            int level,
            String title,
            String subtitle,
            List<NewElement> newElements
    ) {}

    /**
     * 获取指定关卡的介绍数据
     * @param level 关卡编号
     * @return 关卡介绍数据，如果该关卡没有新元素则返回null
     */
    public static LevelIntro getLevelIntro(int level) {
        return switch (level) {
            case 1 -> createLevel1Intro();
            case 2 -> createLevel2Intro();
            case 3 -> createLevel3Intro();
            case 4 -> createLevel4Intro();
            case 5 -> createLevel5Intro();
            case 7 -> createLevel7Intro();
            case 8 -> createLevel8Intro();
            case 9 -> createLevel9Intro();
            case 10 -> createLevel10Intro();
            case 11 -> createLevel11Intro();
            case 13 -> createLevel13Intro();
            case 14 -> createLevel14Intro();
            case 15 -> createLevel15Intro();
            case 16 -> createLevel16Intro();
            case 17 -> createLevel17Intro();
            case 19 -> createLevel19Intro();
            default -> null;
        };
    }

    /**
     * 判断关卡是否有新元素介绍
     * @param level 关卡编号
     * @return 是否有新元素介绍
     */
    public static boolean hasIntro(int level) {
        return getLevelIntro(level) != null;
    }

    // ==================== 各关卡介绍数据 ====================

    private static LevelIntro createLevel1Intro() {
        List<NewElement> elements = new ArrayList<>();
        elements.add(new NewElement(
                ElementType.MECHANIC,
                "基础操作",
                "使用 WASD 或方向键移动吃豆人",
                "收集迷宫中所有的金豆即可通关！",
                Constants.COLOR_PLAYER,
                "player"
        ));
        return new LevelIntro(1, "第一章：入门", "欢迎来到迷宫世界！", elements);
    }

    private static LevelIntro createLevel2Intro() {
        List<NewElement> elements = new ArrayList<>();
        elements.add(new NewElement(
                ElementType.ENEMY,
                "追踪者",
                "红色幽灵，会直线追踪你的位置",
                "它会选择最短路径接近你，但它的移动速度比你稍慢些。记住，它只有在到达路口时才会进行下一次追踪，利用好这个机制甩开它吧！",
                Constants.COLOR_CHASER,
                "chaser"
        ));
        return new LevelIntro(2, "新敌人登场", "迷宫的守护者出现了...", elements);
    }

    private static LevelIntro createLevel3Intro() {
        List<NewElement> elements = new ArrayList<>();
        elements.add(new NewElement(
                ElementType.ENEMY,
                "游荡者",
                "橙色幽灵，在迷宫中随机移动",
                "它的行动不可预测，注意观察它的移动方向！",
                Constants.COLOR_WANDERER,
                "wanderer"
        ));
        return new LevelIntro(3, "新敌人登场", "更多守护者加入战斗...", elements);
    }

    private static LevelIntro createLevel4Intro() {
        List<NewElement> elements = new ArrayList<>();
        elements.add(new NewElement(
                ElementType.TERRAIN,
                "传送门",
                "紫色入口，进入后传送到配对的出口",
                "利用传送门甩开追踪者，规划好逃脱路线！",
                Constants.COLOR_PORTAL,
                "portal"
        ));
        return new LevelIntro(4, "空间跳跃", "神秘的传送门出现了...", elements);
    }

    private static LevelIntro createLevel5Intro() {
        List<NewElement> elements = new ArrayList<>();
        elements.add(new NewElement(
                ElementType.TERRAIN,
                "单向通道",
                "青色箭头地板，只能朝箭头方向通过",
                "规划路线，利用单向通道！一旦通过就无法原路返回！",
                Constants.COLOR_ONE_WAY,
                "oneway"
        ));
        return new LevelIntro(5, "单向之路", "规划路线，利用单向道...", elements);
    }

    private static LevelIntro createLevel7Intro() {
        List<NewElement> elements = new ArrayList<>();
        elements.add(new NewElement(
                ElementType.TERRAIN,
                "冰面",
                "浅蓝色地板，踏上后会持续滑行",
                "学会在冰面控制惯性，提前规划好路线！",
                Constants.COLOR_ICE,
                "ice"
        ));
        return new LevelIntro(7, "第二章：危险地形", "迷宫深处，地面变得诡异...", elements);
    }

    private static LevelIntro createLevel8Intro() {
        List<NewElement> elements = new ArrayList<>();
        elements.add(new NewElement(
                ElementType.ENEMY,
                "巡逻者",
                "绿色幽灵，沿固定路线来回巡逻",
                "观察巡逻路线找空隙，记住它的巡逻路线！",
                Constants.COLOR_PATROLLER,
                "patroller"
        ));
        return new LevelIntro(8, "巡逻者登场", "固定路线的守卫出现了...", elements);
    }

    private static LevelIntro createLevel9Intro() {
        List<NewElement> elements = new ArrayList<>();
        elements.add(new NewElement(
                ElementType.TERRAIN,
                "加速带",
                "橙色地板，经过时速度提升80%",
                "利用加速带快速通过危险区域！",
                Constants.COLOR_SPEED_UP,
                "speedup"
        ));
        return new LevelIntro(9, "加速通道", "脚下生风的地带...", elements);
    }

    private static LevelIntro createLevel10Intro() {
        List<NewElement> elements = new ArrayList<>();
        elements.add(new NewElement(
                ElementType.TERRAIN,
                "减速带",
                "棕色地板，经过时速度降低50%",
                "避开减速带，或引敌人进入减速带！",
                Constants.COLOR_SLOW_DOWN,
                "slowdown"
        ));
        return new LevelIntro(10, "减速陷阱", "寒冰刺骨的地带...", elements);
    }

    private static LevelIntro createLevel11Intro() {
        List<NewElement> elements = new ArrayList<>();
        elements.add(new NewElement(
                ElementType.TERRAIN,
                "跳板",
                "绿色地板，踏上后会跳跃前进2格",
                "用跳板甩掉追踪者，快速穿越危险区域！",
                Constants.COLOR_JUMP_PAD,
                "jumppad"
        ));
        return new LevelIntro(11, "跳跃逃脱", "弹跳的机关出现了...", elements);
    }

    private static LevelIntro createLevel13Intro() {
        List<NewElement> elements = new ArrayList<>();
        elements.add(new NewElement(
                ElementType.ITEM,
                "磁铁",
                "银色道具，自动吸收3格范围内的豆子",
                "持续5秒，可以快速收集大量豆子！",
                Constants.COLOR_ITEM_MAGNET,
                "magnet"
        ));
        return new LevelIntro(13, "第三章：道具大师", "古老的魔法道具出现了...", elements);
    }

    private static LevelIntro createLevel14Intro() {
        List<NewElement> elements = new ArrayList<>();
        elements.add(new NewElement(
                ElementType.ENEMY,
                "猎手",
                "橙红色幽灵，平时移动速度缓慢但看到你时会发起长达2秒的冲刺",
                "学会躲避猎手的视线，被发现时它的速度会暴增！",
                Constants.COLOR_HUNTER,
                "hunter"
        ));
        return new LevelIntro(14, "猎手来袭", "危险的视线追踪者出现了...", elements);
    }

    private static LevelIntro createLevel15Intro() {
        List<NewElement> elements = new ArrayList<>();
        elements.add(new NewElement(
                ElementType.ITEM,
                "护盾",
                "蓝色道具，可抵挡一次敌人攻击",
                "护盾不可叠加，拥有护盾时再次拾取护盾不能抵挡多次攻击。",
                Constants.COLOR_ITEM_SHIELD,
                "shield"
        ));
        return new LevelIntro(15, "防护之力", "保护自己免受伤害...", elements);
    }

    private static LevelIntro createLevel16Intro() {
        List<NewElement> elements = new ArrayList<>();
        elements.add(new NewElement(
                ElementType.TERRAIN,
                "致盲陷阱",
                "深紫色地板，踏入后视野受限",
                "记住地图，避开陷阱！视野缩小到3格范围，持续2秒！",
                Constants.COLOR_BLIND_TRAP,
                "blindtrap"
        ));
        return new LevelIntro(16, "盲区危机", "黑暗笼罩着迷宫...", elements);
    }

    private static LevelIntro createLevel17Intro() {
        List<NewElement> elements = new ArrayList<>();
        elements.add(new NewElement(
                ElementType.ITEM,
                "穿墙术",
                "金色道具，短暂时间内可穿越墙壁",
                "用穿墙创造逃生路线，持续3秒！",
                Constants.COLOR_ITEM_WALL_PASS,
                "wallpass"
        ));
        return new LevelIntro(17, "穿墙逃脱", "穿越障碍的魔法...", elements);
    }

    private static LevelIntro createLevel19Intro() {
        List<NewElement> elements = new ArrayList<>();
        elements.add(new NewElement(
                ElementType.ENEMY,
                "幻影",
                "紫色幽灵，会周期性隐身",
                "它每3秒隐身1.5秒，隐身时移动速度稍微加快并追踪玩家",
                Constants.COLOR_PHANTOM,
                "phantom"
        ));
        return new LevelIntro(19, "第四章：暗影追踪", "看不见的猎手潜伏在暗处...", elements);
    }


}
