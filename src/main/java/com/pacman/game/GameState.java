package com.pacman.game;

/**
 * Game state enumeration
 */
public enum GameState {
    MENU,
    LEVEL_SELECT,
    PLAYING,
    PAUSED,
    LEVEL_COMPLETE,
    GAME_OVER,
    VICTORY,
    STORY,
    COUNTDOWN,
    LEVEL_INTRO;

    public boolean canPause() {
        return this == PLAYING;
    }

    public boolean isInGame() {
        return this == PLAYING || this == COUNTDOWN;
    }

    public boolean needsOverlay() {
        return this == PAUSED || this == LEVEL_COMPLETE ||
               this == GAME_OVER || this == VICTORY || this == STORY || this == LEVEL_INTRO;
    }
}
