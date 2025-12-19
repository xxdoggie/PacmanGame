package com.pacman.game;

/**
 * 游戏状态枚举类
 * 管理游戏的各种状态
 */
public enum GameState {
    
    /** 主菜单状态 */
    MENU,
    
    /** 关卡选择状态 */
    LEVEL_SELECT,
    
    /** 游戏进行中 */
    PLAYING,
    
    /** 游戏暂停 */
    PAUSED,
    
    /** 关卡通过 */
    LEVEL_COMPLETE,
    
    /** 游戏结束（失败） */
    GAME_OVER,
    
    /** 游戏胜利（通关） */
    VICTORY,
    
    /** 显示剧情/对话 */
    STORY,

    /** 倒计时准备阶段 */
    COUNTDOWN,

    /** 关卡介绍页面 */
    LEVEL_INTRO;
    
    /**
     * 判断是否可以暂停
     * @return 是否可以暂停
     */
    public boolean canPause() {
        return this == PLAYING;
    }
    
    /**
     * 判断是否在游戏中（需要更新游戏逻辑）
     * @return 是否在游戏中
     */
    public boolean isInGame() {
        return this == PLAYING || this == COUNTDOWN;
    }
    
    /**
     * 判断是否需要显示UI覆盖层
     * @return 是否需要显示覆盖层
     */
    public boolean needsOverlay() {
        return this == PAUSED || this == LEVEL_COMPLETE ||
               this == GAME_OVER || this == VICTORY || this == STORY || this == LEVEL_INTRO;
    }
}
