package com.pacman;

import com.pacman.ui.SceneManager;
import com.pacman.util.Constants;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * 游戏主入口类
 * 继承JavaFX Application，负责启动游戏
 */
public class Main extends Application {
    
    /**
     * 主方法，程序入口
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    /**
     * JavaFX应用启动方法
     * @param primaryStage 主舞台
     */
    @Override
    public void start(Stage primaryStage) {
        // 设置窗口标题
        primaryStage.setTitle(Constants.GAME_TITLE);
        
        // 设置窗口不可调整大小
        primaryStage.setResizable(false);
        
        // 初始化场景管理器
        SceneManager sceneManager = SceneManager.getInstance();
        sceneManager.initialize(primaryStage);
        
        // 显示主菜单
        sceneManager.showMenu();
        
        // 显示窗口
        primaryStage.show();
        
        System.out.println("Game started successfully!");
    }
    
    /**
     * 应用停止时的清理工作
     */
    @Override
    public void stop() {
        System.out.println("Game closed.");
        // 可以在这里添加保存游戏进度等清理逻辑
    }
}
