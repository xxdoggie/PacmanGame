package com.pacman.util;

import javafx.scene.media.AudioClip;

import java.net.URL;
import java.util.EnumMap;
import java.util.Map;

/**
 * 音效管理器（单例模式）
 * 负责加载和播放游戏音效
 */
public class SoundManager {

    /** 单例实例 */
    private static SoundManager instance;

    /** 音效类型枚举 */
    public enum SoundType {
        EAT_DOT("eat_dot", true),         // 吃豆（高频，单实例模式）
        HURT("hurt", true),                // 受伤
        JUMP("jump", true),                // 跳板
        SPEED_UP("speed_up", true),        // 加速
        SLOW_DOWN("slow_down", true),      // 减速
        TELEPORT("teleport", true),        // 传送
        ITEM_PICKUP("item_pickup", true),  // 吃到道具
        COUNTDOWN("countdown", false),     // 倒计时（允许叠加）
        LEVEL_COMPLETE("level_complete", false), // 过关
        GAME_OVER("game_over", false);     // 失败

        private final String fileName;
        private final boolean singleInstance;  // 是否单实例播放（防止叠加）

        SoundType(String fileName, boolean singleInstance) {
            this.fileName = fileName;
            this.singleInstance = singleInstance;
        }

        public String getFileName() {
            return fileName;
        }

        public boolean isSingleInstance() {
            return singleInstance;
        }
    }

    /** 音效缓存 */
    private Map<SoundType, AudioClip> sounds;

    /** 音效是否启用 */
    private boolean soundEnabled;

    /** 音量（0.0 - 1.0） */
    private double volume;

    /**
     * 私有构造函数
     */
    private SoundManager() {
        this.sounds = new EnumMap<>(SoundType.class);
        this.soundEnabled = true;
        this.volume = 0.7;
        loadAllSounds();
    }

    /**
     * 获取单例实例
     */
    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    /**
     * 加载所有音效
     */
    private void loadAllSounds() {
        for (SoundType type : SoundType.values()) {
            String soundPath = "/sounds/" + type.getFileName() + ".wav";
            try {
                URL resource = getClass().getResource(soundPath);
                if (resource == null) {
                    System.out.println("Sound file not found (optional): " + soundPath);
                    continue;
                }

                AudioClip clip = new AudioClip(resource.toExternalForm());
                sounds.put(type, clip);
                System.out.println("Loaded sound: " + soundPath);
            } catch (Exception e) {
                System.err.println("Failed to load sound: " + soundPath);
                e.printStackTrace();
            }
        }
    }

    /**
     * 播放音效
     * @param type 音效类型
     */
    public void play(SoundType type) {
        if (!soundEnabled) {
            return;
        }

        AudioClip clip = sounds.get(type);
        if (clip != null) {
            // 单实例模式：先停止之前的播放，防止音频叠加耗尽资源
            if (type.isSingleInstance()) {
                clip.stop();
            }
            clip.play(volume);
        }
    }

    /**
     * 播放音效（自定义音量）
     * @param type 音效类型
     * @param customVolume 自定义音量（0.0 - 1.0）
     */
    public void play(SoundType type, double customVolume) {
        if (!soundEnabled) {
            return;
        }

        AudioClip clip = sounds.get(type);
        if (clip != null) {
            if (type.isSingleInstance()) {
                clip.stop();
            }
            clip.play(customVolume);
        }
    }

    /**
     * 停止指定音效
     * @param type 音效类型
     */
    public void stop(SoundType type) {
        AudioClip clip = sounds.get(type);
        if (clip != null) {
            clip.stop();
        }
    }

    /**
     * 停止所有音效
     */
    public void stopAll() {
        for (AudioClip clip : sounds.values()) {
            if (clip != null) {
                clip.stop();
            }
        }
    }

    /**
     * 设置音效是否启用
     * @param enabled 是否启用
     */
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        if (!enabled) {
            stopAll();
        }
    }

    /**
     * 获取音效是否启用
     * @return 是否启用
     */
    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    /**
     * 设置音量
     * @param volume 音量（0.0 - 1.0）
     */
    public void setVolume(double volume) {
        this.volume = Math.max(0.0, Math.min(1.0, volume));
    }

    /**
     * 获取音量
     * @return 音量
     */
    public double getVolume() {
        return volume;
    }

    /**
     * 检查音效是否已加载
     * @param type 音效类型
     * @return 是否已加载
     */
    public boolean isLoaded(SoundType type) {
        return sounds.containsKey(type);
    }
}
