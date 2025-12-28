package com.pacman.util;

import javafx.scene.image.Image;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * 皮肤管理器（单例模式）
 * 负责加载和管理玩家皮肤图片
 */
public class SkinManager {

    /** 单例实例 */
    private static SkinManager instance;

    /** 可用皮肤列表 */
    public enum SkinType {
        SKIN1("Skin 1"),
        SKIN2("Skin 2");

        private final String displayName;

        SkinType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /** 当前选中的皮肤 */
    private SkinType currentSkin;

    /** 所有皮肤图片缓存：SkinType -> Direction -> Image */
    private Map<SkinType, Map<Direction, Image>> skinImages;

    /** 图片显示尺寸（根据TILE_SIZE调整） */
    private static final double DISPLAY_SIZE = Constants.TILE_SIZE * 1.2;

    /**
     * 私有构造函数
     */
    private SkinManager() {
        this.currentSkin = SkinType.SKIN1;
        this.skinImages = new HashMap<>();
        loadAllSkins();
    }

    /**
     * 获取单例实例
     */
    public static SkinManager getInstance() {
        if (instance == null) {
            instance = new SkinManager();
        }
        return instance;
    }

    /**
     * 加载所有皮肤图片
     */
    private void loadAllSkins() {
        for (SkinType skin : SkinType.values()) {
            Map<Direction, Image> directionImages = new EnumMap<>(Direction.class);
            String skinPrefix = skin.name().toLowerCase();

            for (Direction dir : Direction.validDirections()) {
                String imagePath = "/images/player/" + skinPrefix + "_" + dir.name().toLowerCase() + ".png";
                try {
                    Image image = new Image(
                            getClass().getResourceAsStream(imagePath),
                            DISPLAY_SIZE,  // 请求宽度
                            DISPLAY_SIZE,  // 请求高度
                            true,          // 保持比例
                            true           // 平滑缩放
                    );
                    directionImages.put(dir, image);
                    System.out.println("Loaded skin image: " + imagePath);
                } catch (Exception e) {
                    System.err.println("Failed to load skin image: " + imagePath);
                    e.printStackTrace();
                }
            }

            // 为NONE方向使用RIGHT方向的图片
            if (directionImages.containsKey(Direction.RIGHT)) {
                directionImages.put(Direction.NONE, directionImages.get(Direction.RIGHT));
            }

            skinImages.put(skin, directionImages);
        }
    }

    /**
     * 获取当前皮肤指定方向的图片
     * @param direction 方向
     * @return 对应的图片，如果没有则返回null
     */
    public Image getImage(Direction direction) {
        Map<Direction, Image> images = skinImages.get(currentSkin);
        if (images != null) {
            return images.get(direction);
        }
        return null;
    }

    /**
     * 获取指定皮肤指定方向的图片
     * @param skin 皮肤类型
     * @param direction 方向
     * @return 对应的图片
     */
    public Image getImage(SkinType skin, Direction direction) {
        Map<Direction, Image> images = skinImages.get(skin);
        if (images != null) {
            return images.get(direction);
        }
        return null;
    }

    /**
     * 设置当前皮肤
     * @param skin 皮肤类型
     */
    public void setCurrentSkin(SkinType skin) {
        this.currentSkin = skin;
        System.out.println("Skin changed to: " + skin.getDisplayName());
    }

    /**
     * 获取当前皮肤
     * @return 当前皮肤类型
     */
    public SkinType getCurrentSkin() {
        return currentSkin;
    }

    /**
     * 切换到下一个皮肤
     */
    public void nextSkin() {
        SkinType[] skins = SkinType.values();
        int currentIndex = currentSkin.ordinal();
        int nextIndex = (currentIndex + 1) % skins.length;
        setCurrentSkin(skins[nextIndex]);
    }

    /**
     * 切换到上一个皮肤
     */
    public void previousSkin() {
        SkinType[] skins = SkinType.values();
        int currentIndex = currentSkin.ordinal();
        int prevIndex = (currentIndex - 1 + skins.length) % skins.length;
        setCurrentSkin(skins[prevIndex]);
    }

    /**
     * 获取图片显示尺寸
     */
    public static double getDisplaySize() {
        return DISPLAY_SIZE;
    }
}