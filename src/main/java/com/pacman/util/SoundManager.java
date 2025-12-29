package com.pacman.util;

import javafx.scene.media.AudioClip;

import java.net.URL;
import java.util.EnumMap;
import java.util.Map;

/**
 * Sound manager (OOP: Singleton pattern)
 * Manages and plays game sound effects
 */
public class SoundManager {
    private static SoundManager instance;

    public enum SoundType {
        EAT_DOT("eat_dot", 100),
        HURT("hurt", 500),
        JUMP("jump", 200),
        SPEED_UP("speed_up", 300),
        SLOW_DOWN("slow_down", 300),
        TELEPORT("teleport", 300),
        ITEM_PICKUP("item_pickup", 200),
        COUNTDOWN("countdown", 0),
        LEVEL_COMPLETE("level_complete", 0),
        GAME_OVER("game_over", 0);

        private final String fileName;
        private final long cooldownMs;

        SoundType(String fileName, long cooldownMs) {
            this.fileName = fileName;
            this.cooldownMs = cooldownMs;
        }

        public String getFileName() { return fileName; }
        public long getCooldownMs() { return cooldownMs; }
    }

    private Map<SoundType, AudioClip> sounds;
    private Map<SoundType, Long> lastPlayTime;
    private boolean soundEnabled;
    private double volume;

    private SoundManager() {
        this.sounds = new EnumMap<>(SoundType.class);
        this.lastPlayTime = new EnumMap<>(SoundType.class);
        this.soundEnabled = true;
        this.volume = 0.7;
        loadAllSounds();
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

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

    /** Play sound with cooldown control */
    public void play(SoundType type) {
        if (!soundEnabled) return;

        AudioClip clip = sounds.get(type);
        if (clip == null) return;

        long now = System.currentTimeMillis();
        long cooldown = type.getCooldownMs();
        if (cooldown > 0) {
            Long lastTime = lastPlayTime.get(type);
            if (lastTime != null && (now - lastTime) < cooldown) {
                return; // Still in cooldown
            }
        }

        clip.play(volume);
        lastPlayTime.put(type, now);
    }

    public void play(SoundType type, double customVolume) {
        if (!soundEnabled) {
            return;
        }

        AudioClip clip = sounds.get(type);
        if (clip == null) {
            return;
        }

        // 检查冷却时间
        long now = System.currentTimeMillis();
        long cooldown = type.getCooldownMs();
        if (cooldown > 0) {
            Long lastTime = lastPlayTime.get(type);
            if (lastTime != null && (now - lastTime) < cooldown) {
                return;
            }
        }

        clip.play(customVolume);
        lastPlayTime.put(type, now);
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
