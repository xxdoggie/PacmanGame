package com.pacman.game;

import com.pacman.entity.Player;
import com.pacman.map.GameMap;
import com.pacman.ui.SceneManager;
import com.pacman.util.Constants;
import com.pacman.util.Direction;
import com.pacman.util.LevelLoader;
import com.pacman.util.SoundManager;
import com.pacman.util.SoundManager.SoundType;
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
 * Main game controller - handles game loop, rendering, and input
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

    /** Collision cooldown to prevent repeated damage */
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
        
        levelLabel = createUILabel("Level: " + currentLevel);
        dotsLabel = createUILabel("Dots: " + gameMap.getRemainingDots());
        timeLabel = createUILabel("Time: 0.0s");
        livesLabel = createUILabel("Lives: " + lives);

        Button pauseBtn = new Button("Pause (ESC)");
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
        
        Label pauseLabel = new Label("Game Paused");
        pauseLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        pauseLabel.setTextFill(Color.WHITE);

        Button resumeBtn = createOverlayButton("Resume");
        resumeBtn.setOnAction(e -> togglePause());

        Button restartBtn = createOverlayButton("Restart");
        restartBtn.setOnAction(e -> restartLevel());

        Button menuBtn = createOverlayButton("Back to Menu");
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
        mainLayout.setFocusTraversable(true);

        // Capture keyboard events before they reach child nodes
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            KeyCode code = event.getCode();

            // Consume arrow and WASD keys to prevent focus traversal
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
                } else if (code == KeyCode.S || code == KeyCode.DOWN) {
                    dir = Direction.DOWN;
                } else if (code == KeyCode.A || code == KeyCode.LEFT) {
                    dir = Direction.LEFT;
                } else if (code == KeyCode.D || code == KeyCode.RIGHT) {
                    dir = Direction.RIGHT;
                }

                if (dir != null) {
                    player.setNextDirection(dir);
                }
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
        mainLayout.requestFocus();

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
            SoundManager.getInstance().play(SoundType.COUNTDOWN);

            if (countdown <= 0) {
                state = GameState.PLAYING;
            }
        }
    }
    
    private void updatePlaying(double deltaTime) {
        gameTime += deltaTime;

        // Update collision cooldown
        if (collisionCooldown > 0) {
            collisionCooldown -= deltaTime;
        }

        player.update(deltaTime);
        gameMap.update(player, deltaTime);

        // Check collision only after cooldown expires
        if (collisionCooldown <= 0 && gameMap.checkEnemyCollision(player)) {
            lives--;
            livesLabel.setText("Lives: " + lives);
            collisionCooldown = 1.5; // 1.5s cooldown to prevent rapid damage
            SoundManager.getInstance().play(SoundType.HURT);

            if (lives <= 0) {
                onGameOver();
            }
            // Continue game without respawning
        }

        if (gameMap.allDotsCollected()) {
            onLevelComplete();
        }

        dotsLabel.setText("Dots: " + gameMap.getRemainingDots());
        timeLabel.setText(String.format("Time: %.1fs", gameTime));
    }
    
    private void resetPlayerPosition() {
        player.setGridX(gameMap.getSpawnX());
        player.setGridY(gameMap.getSpawnY());
        player.setDirection(Direction.NONE);
        // No pause on respawn - only COUNTDOWN at game start
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
        livesLabel.setText("Lives: " + lives);
        dotsLabel.setText("Dots: " + gameMap.getRemainingDots());
        timeLabel.setText("Time: 0.0s");
        
        start();
    }
    
    private void onGameOver() {
        stop();
        SoundManager.getInstance().play(SoundType.GAME_OVER);
        SceneManager.getInstance().onGameOver(currentLevel);
    }

    private void onLevelComplete() {
        stop();
        SoundManager.getInstance().play(SoundType.LEVEL_COMPLETE);
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
