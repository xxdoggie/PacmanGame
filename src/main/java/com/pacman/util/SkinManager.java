package com.pacman.util;

import javafx.scene.image.Image;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Skin manager (OOP: Singleton pattern)
 * Manages player skin images
 */
public class SkinManager {
    private static SkinManager instance;

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

    private SkinType currentSkin;
    private Map<SkinType, Map<Direction, Image>> skinImages;
    private static final double DISPLAY_SIZE = Constants.TILE_SIZE * 1.2;

    private SkinManager() {
        this.currentSkin = SkinType.SKIN1;
        this.skinImages = new HashMap<>();
        loadAllSkins();
    }

    public static SkinManager getInstance() {
        if (instance == null) {
            instance = new SkinManager();
        }
        return instance;
    }

    private void loadAllSkins() {
        for (SkinType skin : SkinType.values()) {
            Map<Direction, Image> directionImages = new EnumMap<>(Direction.class);
            String skinPrefix = skin.name().toLowerCase();

            for (Direction dir : Direction.validDirections()) {
                String imagePath = "/images/player/" + skinPrefix + "_" + dir.name().toLowerCase() + ".png";
                try {
                    java.io.InputStream is = getClass().getResourceAsStream(imagePath);
                    if (is == null) {
                        System.err.println("Resource not found: " + imagePath);
                        continue;
                    }

                    Image image = new Image(
                            is,
                            DISPLAY_SIZE,  // requested width
                            DISPLAY_SIZE,  // requested height
                            true,          // preserve ratio
                            true           // smooth scaling
                    );

                    // Check if image loaded successfully
                    if (image.isError()) {
                        System.err.println("Failed to load skin image (error): " + imagePath);
                        if (image.getException() != null) {
                            image.getException().printStackTrace();
                        }
                        continue;
                    }

                    if (image.getWidth() <= 0 || image.getHeight() <= 0) {
                        System.err.println("Invalid image dimensions for: " + imagePath);
                        continue;
                    }

                    directionImages.put(dir, image);
                    System.out.println("Loaded: " + imagePath);
                } catch (Exception e) {
                    System.err.println("Failed to load: " + imagePath);
                    e.printStackTrace();
                }
            }

            if (directionImages.containsKey(Direction.RIGHT)) {
                directionImages.put(Direction.NONE, directionImages.get(Direction.RIGHT));
            }

            skinImages.put(skin, directionImages);
        }
    }

    public Image getImage(Direction direction) {
        Map<Direction, Image> images = skinImages.get(currentSkin);
        if (images != null) {
            return images.get(direction);
        }
        return null;
    }

    public Image getImage(SkinType skin, Direction direction) {
        Map<Direction, Image> images = skinImages.get(skin);
        if (images != null) {
            return images.get(direction);
        }
        return null;
    }

    public void setCurrentSkin(SkinType skin) {
        this.currentSkin = skin;
        System.out.println("Skin: " + skin.getDisplayName());
    }

    public SkinType getCurrentSkin() { return currentSkin; }

    public void nextSkin() {
        SkinType[] skins = SkinType.values();
        int currentIndex = currentSkin.ordinal();
        int nextIndex = (currentIndex + 1) % skins.length;
        setCurrentSkin(skins[nextIndex]);
    }

    public void previousSkin() {
        SkinType[] skins = SkinType.values();
        int currentIndex = currentSkin.ordinal();
        int prevIndex = (currentIndex - 1 + skins.length) % skins.length;
        setCurrentSkin(skins[prevIndex]);
    }

    public static double getDisplaySize() { return DISPLAY_SIZE; }
}