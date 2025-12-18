package com.pacman.ui;

import com.pacman.game.Game;
import com.pacman.game.GameState;
import com.pacman.util.Constants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * 场景管理器（单例模式）
 * 负责管理和切换游戏中的各个场景
 */
public class SceneManager {
    
    /** 单例实例 */
    private static SceneManager instance;
    
    /** 主舞台 */
    private Stage primaryStage;
    
    /** 游戏实例 */
    private Game game;
    
    /** 当前已解锁的最大关卡 */
    private int unlockedLevel = 1;
    
    /**
     * 私有构造函数（单例模式）
     */
    private SceneManager() {
    }
    
    /**
     * 获取单例实例
     * @return SceneManager实例
     */
    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }
    
    /**
     * 初始化场景管理器
     * @param stage 主舞台
     */
    public void initialize(Stage stage) {
        this.primaryStage = stage;
    }
    
    /**
     * 显示主菜单
     */
    public void showMenu() {
        VBox menuLayout = new VBox(30);
        menuLayout.setAlignment(Pos.CENTER);
        menuLayout.setBackground(new Background(new BackgroundFill(
                Color.web("#1A1A2E"), CornerRadii.EMPTY, Insets.EMPTY)));
        
        // 游戏标题
        Label titleLabel = new Label("PAC-MAN");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 72));
        titleLabel.setTextFill(Color.YELLOW);
        
        Label subtitleLabel = new Label("ADVENTURE");
        subtitleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        subtitleLabel.setTextFill(Color.web("#FFD700"));
        
        // 单人模式按钮
        Button singlePlayerBtn = createMenuButton("单人模式");
        singlePlayerBtn.setOnAction(e -> showLevelSelect());
        
        // 双人模式按钮（暂时禁用）
        Button multiPlayerBtn = createMenuButton("双人模式");
        multiPlayerBtn.setOnAction(e -> {
            // TODO: 实现双人模式
            System.out.println("双人模式开发中...");
        });
        multiPlayerBtn.setDisable(true);
        
        // 退出按钮
        Button exitBtn = createMenuButton("退出游戏");
        exitBtn.setOnAction(e -> primaryStage.close());
        
        menuLayout.getChildren().addAll(
                titleLabel, 
                subtitleLabel, 
                createSpacer(30),
                singlePlayerBtn, 
                multiPlayerBtn, 
                exitBtn
        );
        
        Scene menuScene = new Scene(menuLayout, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        primaryStage.setScene(menuScene);
    }
    
    /**
     * 显示关卡选择界面
     */
    public void showLevelSelect() {
        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setBackground(new Background(new BackgroundFill(
                Color.web("#1A1A2E"), CornerRadii.EMPTY, Insets.EMPTY)));
        
        // 标题
        Label titleLabel = new Label("选择关卡");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        titleLabel.setTextFill(Color.WHITE);
        
        // 关卡网格
        GridPane levelGrid = new GridPane();
        levelGrid.setAlignment(Pos.CENTER);
        levelGrid.setHgap(10);
        levelGrid.setVgap(10);
        
        // 创建30个关卡按钮（每行6个，共5行）
        int cols = 6;
        for (int i = 1; i <= Constants.TOTAL_LEVELS; i++) {
            Button levelBtn = createLevelButton(i);
            int row = (i - 1) / cols;
            int col = (i - 1) % cols;
            levelGrid.add(levelBtn, col, row);
        }
        
        // 返回按钮
        Button backBtn = createMenuButton("返回主菜单");
        backBtn.setOnAction(e -> showMenu());
        
        mainLayout.getChildren().addAll(titleLabel, createSpacer(20), levelGrid, createSpacer(20), backBtn);
        
        Scene levelSelectScene = new Scene(mainLayout, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        primaryStage.setScene(levelSelectScene);
    }
    
    /**
     * 开始指定关卡
     * @param level 关卡编号
     */
    public void startLevel(int level) {
        game = new Game(level);
        Scene gameScene = game.getScene();
        primaryStage.setScene(gameScene);
        game.start();
    }
    
    /**
     * 关卡通过处理
     * @param level 完成的关卡
     */
    public void onLevelComplete(int level) {
        // 解锁下一关
        if (level >= unlockedLevel && level < Constants.TOTAL_LEVELS) {
            unlockedLevel = level + 1;
        }
        
        if (level >= Constants.TOTAL_LEVELS) {
            // 通关所有关卡
            showVictoryScreen();
        } else {
            // 显示关卡完成界面
            showLevelCompleteScreen(level);
        }
    }
    
    /**
     * 游戏结束处理
     * @param level 失败的关卡
     */
    public void onGameOver(int level) {
        showGameOverScreen(level);
    }
    
    /**
     * 显示关卡完成界面
     * @param level 完成的关卡
     */
    private void showLevelCompleteScreen(int level) {
        VBox layout = new VBox(30);
        layout.setAlignment(Pos.CENTER);
        layout.setBackground(new Background(new BackgroundFill(
                Color.web("#1A1A2E"), CornerRadii.EMPTY, Insets.EMPTY)));
        
        Label titleLabel = new Label("关卡 " + level + " 通过！");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        titleLabel.setTextFill(Color.LIMEGREEN);
        
        // 获取章节剧情
        String storyText = getChapterStory(level);
        if (storyText != null && !storyText.isEmpty()) {
            Label storyLabel = new Label(storyText);
            storyLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
            storyLabel.setTextFill(Color.LIGHTGRAY);
            storyLabel.setWrapText(true);
            storyLabel.setMaxWidth(600);
            layout.getChildren().add(storyLabel);
        }
        
        Button nextLevelBtn = createMenuButton("下一关");
        nextLevelBtn.setOnAction(e -> startLevel(level + 1));
        
        Button selectBtn = createMenuButton("选择关卡");
        selectBtn.setOnAction(e -> showLevelSelect());
        
        Button menuBtn = createMenuButton("返回主菜单");
        menuBtn.setOnAction(e -> showMenu());
        
        layout.getChildren().addAll(titleLabel, createSpacer(20), nextLevelBtn, selectBtn, menuBtn);
        
        Scene scene = new Scene(layout, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        primaryStage.setScene(scene);
    }
    
    /**
     * 显示游戏结束界面
     * @param level 失败的关卡
     */
    private void showGameOverScreen(int level) {
        VBox layout = new VBox(30);
        layout.setAlignment(Pos.CENTER);
        layout.setBackground(new Background(new BackgroundFill(
                Color.web("#1A1A2E"), CornerRadii.EMPTY, Insets.EMPTY)));
        
        Label titleLabel = new Label("游戏结束");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        titleLabel.setTextFill(Color.RED);
        
        Label levelLabel = new Label("在第 " + level + " 关失败");
        levelLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 24));
        levelLabel.setTextFill(Color.LIGHTGRAY);
        
        Button retryBtn = createMenuButton("重新挑战");
        retryBtn.setOnAction(e -> startLevel(level));
        
        Button selectBtn = createMenuButton("选择关卡");
        selectBtn.setOnAction(e -> showLevelSelect());
        
        Button menuBtn = createMenuButton("返回主菜单");
        menuBtn.setOnAction(e -> showMenu());
        
        layout.getChildren().addAll(titleLabel, levelLabel, createSpacer(20), retryBtn, selectBtn, menuBtn);
        
        Scene scene = new Scene(layout, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        primaryStage.setScene(scene);
    }
    
    /**
     * 显示通关界面
     */
    private void showVictoryScreen() {
        VBox layout = new VBox(30);
        layout.setAlignment(Pos.CENTER);
        layout.setBackground(new Background(new BackgroundFill(
                Color.web("#1A1A2E"), CornerRadii.EMPTY, Insets.EMPTY)));
        
        Label titleLabel = new Label("🎉 恭喜通关！ 🎉");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        titleLabel.setTextFill(Color.GOLD);
        
        Label msgLabel = new Label("你已经完成了所有30个关卡！");
        msgLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 24));
        msgLabel.setTextFill(Color.WHITE);
        
        Button selectBtn = createMenuButton("再次挑战");
        selectBtn.setOnAction(e -> showLevelSelect());
        
        Button menuBtn = createMenuButton("返回主菜单");
        menuBtn.setOnAction(e -> showMenu());
        
        layout.getChildren().addAll(titleLabel, msgLabel, createSpacer(20), selectBtn, menuBtn);
        
        Scene scene = new Scene(layout, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        primaryStage.setScene(scene);
    }
    
    /**
     * 创建菜单按钮
     * @param text 按钮文字
     * @return 样式化的按钮
     */
    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setPrefSize(Constants.BUTTON_WIDTH, Constants.BUTTON_HEIGHT);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        button.setStyle(
                "-fx-background-color: #16213E; " +
                "-fx-text-fill: white; " +
                "-fx-border-color: #0F3460; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 10; " +
                "-fx-background-radius: 10; " +
                "-fx-cursor: hand;"
        );
        
        // 鼠标悬停效果
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #0F3460; " +
                "-fx-text-fill: #E94560; " +
                "-fx-border-color: #E94560; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 10; " +
                "-fx-background-radius: 10; " +
                "-fx-cursor: hand;"
        ));
        
        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: #16213E; " +
                "-fx-text-fill: white; " +
                "-fx-border-color: #0F3460; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 10; " +
                "-fx-background-radius: 10; " +
                "-fx-cursor: hand;"
        ));
        
        return button;
    }
    
    /**
     * 创建关卡选择按钮
     * @param level 关卡编号
     * @return 关卡按钮
     */
    private Button createLevelButton(int level) {
        Button button = new Button(String.valueOf(level));
        button.setPrefSize(60, 60);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        boolean isUnlocked = level <= unlockedLevel;
        boolean isFusionLevel = (level % 6 == 0); // 融合关卡
        
        if (isUnlocked) {
            String bgColor = isFusionLevel ? "#E94560" : "#16213E";
            String borderColor = isFusionLevel ? "#FF6B6B" : "#0F3460";
            
            button.setStyle(
                    "-fx-background-color: " + bgColor + "; " +
                    "-fx-text-fill: white; " +
                    "-fx-border-color: " + borderColor + "; " +
                    "-fx-border-width: 2; " +
                    "-fx-border-radius: 10; " +
                    "-fx-background-radius: 10; " +
                    "-fx-cursor: hand;"
            );
            button.setOnAction(e -> startLevel(level));
        } else {
            // 锁定状态
            button.setText("🔒");
            button.setStyle(
                    "-fx-background-color: #333333; " +
                    "-fx-text-fill: #666666; " +
                    "-fx-border-color: #444444; " +
                    "-fx-border-width: 2; " +
                    "-fx-border-radius: 10; " +
                    "-fx-background-radius: 10;"
            );
            button.setDisable(true);
        }
        
        return button;
    }
    
    /**
     * 创建空白间隔
     * @param height 间隔高度
     * @return Region对象
     */
    private Region createSpacer(double height) {
        Region spacer = new Region();
        spacer.setMinHeight(height);
        spacer.setPrefHeight(height);
        return spacer;
    }
    
    /**
     * 获取章节剧情文字
     * @param level 关卡编号
     * @return 剧情文字
     */
    private String getChapterStory(int level) {
        return switch (level) {
            case 1 -> "迷宫中似乎有什么东西在蠢蠢欲动...";
            case 6 -> "第一章完成！\n「一只饥饿的小精灵闯入了神秘迷宫，听说吃完所有金豆就能获得宝藏……但迷宫的守护者们不会让他轻易得逞。」";
            case 12 -> "第二章完成！\n「迷宫深处，地面开始变得诡异——有的地方寒冰刺骨，有的地方脚下生风……」";
            case 18 -> "第三章完成！\n「迷宫中散落着古老的魔法道具，善用它们，或许能扭转乾坤……」";
            case 24 -> "第四章完成！\n「暗影中潜伏着看不见的猎手，它们时隐时现，令人防不胜防……」";
            case 30 -> "最终章完成！\n「迷宫的最深处，所有危险汇聚于此。你已经证明了自己是真正的勇者！」";
            default -> null;
        };
    }
    
    /**
     * 获取当前游戏实例
     * @return Game实例
     */
    public Game getGame() {
        return game;
    }
    
    /**
     * 获取已解锁的最大关卡
     * @return 已解锁关卡数
     */
    public int getUnlockedLevel() {
        return unlockedLevel;
    }
    
    /**
     * 设置已解锁的最大关卡（用于存档读取）
     * @param level 关卡数
     */
    public void setUnlockedLevel(int level) {
        this.unlockedLevel = Math.min(level, Constants.TOTAL_LEVELS);
    }
}
