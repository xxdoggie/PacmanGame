package com.pacman.game;

import com.pacman.entity.Player;
import com.pacman.map.GameMap;
import com.pacman.ui.SceneManager;
import com.pacman.util.Constants;
import com.pacman.util.Direction;
import com.pacman.util.LevelLoader;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * 游戏主控制器类
 * 负责游戏的主循环、渲染、输入处理等
 */
public class Game {
    
    private int currentLevel;
    private GameState state;
    private GameMap gameMap;
    private Player player;
    private Level level;
    private Scene scene;
    private BorderPane mainLayout;
    private Canvas gameCanvas;
    private GraphicsContext gc;
    private AnimationTimer gameLoop;
    private long lastFrameTime;
    private int lives;
    private double gameTime;
    private int countdown;
    private double countdownTimer;
    
    private Label levelLabel;
    private Label dotsLabel;
    private Label timeLabel;
    private Label livesLabel;
    private VBox pauseOverlay;

    /** 碰撞冷却时间（防止连续扣血） */
    private double collisionCooldown;
    
    public Game(int levelNumber) {
        this.currentLevel = levelNumber;
        this.state = GameState.COUNTDOWN;
        this.lives = Constants.DEFAULT_LIVES;
        this.gameTime = 0;
        this.countdown = 3;
        this.countdownTimer = 0;
        this.collisionCooldown = 0;

        initializeGame();
        createScene();
    }
    
    private void initializeGame() {
        level = LevelLoader.loadLevel(currentLevel);
        gameMap = LevelLoader.buildGameMap(level);
        player = new Player(gameMap.getSpawnX(), gameMap.getSpawnY());
        player.setGameMap(gameMap);
    }
    
    private void createScene() {
        mainLayout = new BorderPane();
        mainLayout.setBackground(new Background(new BackgroundFill(
                Color.web("#0D0D1A"), CornerRadii.EMPTY, Insets.EMPTY)));
        
        HBox topUI = createTopUI();
        mainLayout.setTop(topUI);
        
        StackPane canvasContainer = new StackPane();
        canvasContainer.setAlignment(Pos.CENTER);
        
        gameCanvas = new Canvas(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
        gc = gameCanvas.getGraphicsContext2D();
        canvasContainer.getChildren().add(gameCanvas);
        
        pauseOverlay = createPauseOverlay();
        pauseOverlay.setVisible(false);
        canvasContainer.getChildren().add(pauseOverlay);
        
        mainLayout.setCenter(canvasContainer);
        
        scene = new Scene(mainLayout, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        setupKeyboardInput();
    }
    
    private HBox createTopUI() {
        HBox topUI = new HBox(40);
        topUI.setAlignment(Pos.CENTER);
        topUI.setPadding(new Insets(15));
        topUI.setBackground(new Background(new BackgroundFill(
                Color.web("#16213E"), CornerRadii.EMPTY, Insets.EMPTY)));
        
        levelLabel = createUILabel("关卡: " + currentLevel);
        dotsLabel = createUILabel("豆子: " + gameMap.getRemainingDots());
        timeLabel = createUILabel("时间: 0.0s");
        livesLabel = createUILabel("生命: " + lives);
        
        Button pauseBtn = new Button("暂停 (ESC)");
        pauseBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        pauseBtn.setStyle("-fx-background-color: #E94560; -fx-text-fill: white; -fx-padding: 8 16; -fx-background-radius: 5;");
        pauseBtn.setOnAction(e -> togglePause());
        
        topUI.getChildren().addAll(levelLabel, dotsLabel, timeLabel, livesLabel, pauseBtn);
        return topUI;
    }
    
    private Label createUILabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        label.setTextFill(Color.WHITE);
        return label;
    }
    
    private VBox createPauseOverlay() {
        VBox overlay = new VBox(20);
        overlay.setAlignment(Pos.CENTER);
        overlay.setBackground(new Background(new BackgroundFill(
                Color.web("#000000", 0.7), CornerRadii.EMPTY, Insets.EMPTY)));
        overlay.setPrefSize(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
        
        Label pauseLabel = new Label("游戏暂停");
        pauseLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        pauseLabel.setTextFill(Color.WHITE);
        
        Button resumeBtn = createOverlayButton("继续游戏");
        resumeBtn.setOnAction(e -> togglePause());
        
        Button restartBtn = createOverlayButton("重新开始");
        restartBtn.setOnAction(e -> restartLevel());
        
        Button menuBtn = createOverlayButton("返回菜单");
        menuBtn.setOnAction(e -> {
            stop();
            SceneManager.getInstance().showMenu();
        });
        
        overlay.getChildren().addAll(pauseLabel, resumeBtn, restartBtn, menuBtn);
        return overlay;
    }
    
    private Button createOverlayButton(String text) {
        Button button = new Button(text);
        button.setPrefSize(180, 45);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        button.setStyle("-fx-background-color: #16213E; -fx-text-fill: white; -fx-border-color: #0F3460; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8;");
        return button;
    }
    
    private void setupKeyboardInput() {
        // 让mainLayout可以获得焦点
        mainLayout.setFocusTraversable(true);

        // 使用addEventFilter在事件到达子节点之前捕获，确保能接收到所有键盘事件
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            KeyCode code = event.getCode();

            // 日志：记录所有按键事件
            System.out.println("[键盘输入] 按键: " + code + ", 游戏状态: " + state);

            // 消费方向键和WASD事件，防止被用于焦点遍历
            if (code == KeyCode.UP || code == KeyCode.DOWN ||
                code == KeyCode.LEFT || code == KeyCode.RIGHT ||
                code == KeyCode.W || code == KeyCode.A ||
                code == KeyCode.S || code == KeyCode.D) {
                event.consume();
            }

            if (code == KeyCode.ESCAPE || code == KeyCode.P) {
                togglePause();
                return;
            }

            if (state == GameState.PLAYING || state == GameState.COUNTDOWN) {
                Direction dir = null;
                if (code == KeyCode.W || code == KeyCode.UP) {
                    dir = Direction.UP;
                    System.out.println("[键盘输入] 识别为方向: UP (W或↑)");
                } else if (code == KeyCode.S || code == KeyCode.DOWN) {
                    dir = Direction.DOWN;
                    System.out.println("[键盘输入] 识别为方向: DOWN (S或↓)");
                } else if (code == KeyCode.A || code == KeyCode.LEFT) {
                    dir = Direction.LEFT;
                    System.out.println("[键盘输入] 识别为方向: LEFT (A或←)");
                } else if (code == KeyCode.D || code == KeyCode.RIGHT) {
                    dir = Direction.RIGHT;
                    System.out.println("[键盘输入] 识别为方向: RIGHT (D或→)");
                }

                if (dir != null) {
                    System.out.println("[键盘输入] 设置玩家方向: " + dir);
                    player.setNextDirection(dir);
                }
            } else {
                System.out.println("[键盘输入] 游戏状态不是PLAYING或COUNTDOWN，忽略方向键");
            }
        });
    }
    
    private void togglePause() {
        if (state == GameState.PLAYING) {
            state = GameState.PAUSED;
            pauseOverlay.setVisible(true);
        } else if (state == GameState.PAUSED) {
            state = GameState.PLAYING;
            pauseOverlay.setVisible(false);
        }
    }
    
    public void start() {
        lastFrameTime = System.nanoTime();

        // 请求焦点，确保键盘事件能被接收
        mainLayout.requestFocus();
        System.out.println("[游戏] 游戏开始，已请求键盘焦点");

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double deltaTime = (now - lastFrameTime) / 1_000_000_000.0;
                lastFrameTime = now;

                deltaTime = Math.min(deltaTime, 0.05);

                update(deltaTime);
                render();
            }
        };

        gameLoop.start();
    }
    
    public void stop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }
    
    private void update(double deltaTime) {
        switch (state) {
            case COUNTDOWN -> updateCountdown(deltaTime);
            case PLAYING -> updatePlaying(deltaTime);
            default -> {}
        }
    }
    
    private void updateCountdown(double deltaTime) {
        countdownTimer += deltaTime;
        
        if (countdownTimer >= 1.0) {
            countdownTimer = 0;
            countdown--;
            
            if (countdown <= 0) {
                state = GameState.PLAYING;
            }
        }
    }
    
    private void updatePlaying(double deltaTime) {
        gameTime += deltaTime;

        // 更新碰撞冷却时间
        if (collisionCooldown > 0) {
            collisionCooldown -= deltaTime;
        }

        player.update(deltaTime);
        gameMap.update(player, deltaTime);

        // 只有在冷却时间结束后才检测碰撞
        if (collisionCooldown <= 0 && gameMap.checkEnemyCollision(player)) {
            lives--;
            livesLabel.setText("生命: " + lives);
            // 设置1.5秒的碰撞冷却时间，防止连续扣血
            collisionCooldown = 1.5;

            if (lives <= 0) {
                onGameOver();
            }
            // 不再回到出生点，继续游戏
        }

        if (gameMap.allDotsCollected()) {
            onLevelComplete();
        }

        dotsLabel.setText("豆子: " + gameMap.getRemainingDots());
        timeLabel.setText(String.format("时间: %.1fs", gameTime));
    }
    
    private void resetPlayerPosition() {
        player.setGridX(gameMap.getSpawnX());
        player.setGridY(gameMap.getSpawnY());
        player.setDirection(Direction.NONE);

        // 复活时不暂停游戏，直接继续（保持PLAYING状态）
        // 只有游戏开始时才进入COUNTDOWN状态
    }
    
    private void restartLevel() {
        stop();
        initializeGame();
        
        lives = Constants.DEFAULT_LIVES;
        gameTime = 0;
        countdown = 3;
        countdownTimer = 0;
        state = GameState.COUNTDOWN;
        
        pauseOverlay.setVisible(false);
        livesLabel.setText("生命: " + lives);
        dotsLabel.setText("豆子: " + gameMap.getRemainingDots());
        timeLabel.setText("时间: 0.0s");
        
        start();
    }
    
    private void onGameOver() {
        stop();
        SceneManager.getInstance().onGameOver(currentLevel);
    }
    
    private void onLevelComplete() {
        stop();
        SceneManager.getInstance().onLevelComplete(currentLevel);
    }
    
    private void render() {
        gc.setFill(Color.web(Constants.COLOR_FLOOR));
        gc.fillRect(0, 0, Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
        
        gameMap.render(gc, player);
        player.render(gc);
        
        if (state == GameState.COUNTDOWN) {
            renderCountdown();
        }
    }
    
    private void renderCountdown() {
        gc.setFill(Color.web("#000000", 0.5));
        gc.fillRect(0, 0, Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
        
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 72));
        
        String text = countdown > 0 ? String.valueOf(countdown) : "GO!";
        double textWidth = text.length() * 36;
        gc.fillText(text, (Constants.MAP_WIDTH - textWidth) / 2 + 20, Constants.MAP_HEIGHT / 2 + 20);
    }
    
    public Scene getScene() {
        return scene;
    }
    
    public GameState getState() {
        return state;
    }
    
    public int getCurrentLevel() {
        return currentLevel;
    }
    
    public double getGameTime() {
        return gameTime;
    }
}
