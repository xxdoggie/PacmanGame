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
        EAT_DOT("eat_dot", 0.08),        // 吃豆（高频，设置冷却时间）
        HURT("hurt", 0.5),                // 受伤
        JUMP("jump", 0.2),                // 跳板
        SPEED_UP("speed_up", 0.3),        // 加速
        SLOW_DOWN("slow_down", 0.3),      // 减速
        TELEPORT("teleport", 0.3),        // 传送
        ITEM_PICKUP("item_pickup", 0.2),  // 吃到道具
        COUNTDOWN("countdown", 0.5),      // 倒计时
        LEVEL_COMPLETE("level_complete", 0.0), // 过关
        GAME_OVER("game_over", 0.0);      // 失败

        private final String fileName;
        private final double cooldown;  // 冷却时间（秒）

        SoundType(String fileName, double cooldown) {
            this.fileName = fileName;
            this.cooldown = cooldown;
        }

        public String getFileName() {
            return fileName;
        }

        public double getCooldown() {
            return cooldown;
        }
    }

    /** 音效缓存 */
    private Map<SoundType, AudioClip> sounds;

    /** 上次播放时间（用于冷却控制） */
    private Map<SoundType, Long> lastPlayTime;

    /** 音效是否启用 */
    private boolean soundEnabled;

    /** 音量（0.0 - 1.0） */
    private double volume;

    /**
     * 私有构造函数
     */
    private SoundManager() {
        this.sounds = new EnumMap<>(SoundType.class);
        this.lastPlayTime = new EnumMap<>(SoundType.class);
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
     * 播放音效（带冷却控制）
     * @param type 音效类型
     */
    public void play(SoundType type) {
        if (!soundEnabled) {
            return;
        }

        // 检查冷却时间
        long now = System.currentTimeMillis();
        Long lastTime = lastPlayTime.get(type);
        double cooldownMs = type.getCooldown() * 1000;

        if (lastTime != null && cooldownMs > 0 && (now - lastTime) < cooldownMs) {
            return; // 还在冷却中，跳过播放
        }

        AudioClip clip = sounds.get(type);
        if (clip != null) {
            // 对于高频音效，先停止之前的播放
            if (type.getCooldown() > 0) {
                clip.stop();
            }
            clip.play(volume);
            lastPlayTime.put(type, now);
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

        // 检查冷却时间
        long now = System.currentTimeMillis();
        Long lastTime = lastPlayTime.get(type);
        double cooldownMs = type.getCooldown() * 1000;

        if (lastTime != null && cooldownMs > 0 && (now - lastTime) < cooldownMs) {
            return;
        }

        AudioClip clip = sounds.get(type);
        if (clip != null) {
            if (type.getCooldown() > 0) {
                clip.stop();
            }
            clip.play(customVolume);
            lastPlayTime.put(type, now);
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
