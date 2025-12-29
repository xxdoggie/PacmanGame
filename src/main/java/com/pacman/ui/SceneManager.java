package com.pacman.ui;

import com.pacman.game.Game;
import com.pacman.ui.LevelIntroData.LevelIntro;
import com.pacman.ui.LevelIntroData.NewElement;
import com.pacman.util.Constants;
import com.pacman.util.Direction;
import com.pacman.util.SkinManager;
import com.pacman.util.SkinManager.SkinType;
import com.pacman.util.SoundManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Scene manager (OOP: Singleton pattern)
 * Manages and switches between game scenes
 */
public class SceneManager {
    private static SceneManager instance;
    private Stage primaryStage;
    private Game game;
    private int unlockedLevel = Constants.TOTAL_LEVELS; // TODO: Change to 1 for release

    private SceneManager() {}

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    public void initialize(Stage stage) {
        this.primaryStage = stage;
        SkinManager.getInstance();
    }

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

        Button singlePlayerBtn = createMenuButton("Single Player");
        singlePlayerBtn.setOnAction(e -> showLevelSelect());

        Button settingsBtn = createMenuButton("Settings");
        settingsBtn.setOnAction(e -> showSettings());

        Button exitBtn = createMenuButton("Exit Game");
        exitBtn.setOnAction(e -> primaryStage.close());

        menuLayout.getChildren().addAll(
                titleLabel,
                subtitleLabel,
                createSpacer(30),
                singlePlayerBtn,
                settingsBtn,
                exitBtn
        );

        Scene menuScene = new Scene(menuLayout, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        primaryStage.setScene(menuScene);
    }

    public void showSettings() {
        VBox settingsContent = new VBox(20);
        settingsContent.setAlignment(Pos.CENTER);
        settingsContent.setPadding(new Insets(30));

        // 标题
        Label titleLabel = new Label("Settings");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 42));
        titleLabel.setTextFill(Color.WHITE);

        // 皮肤选择区域
        VBox skinSection = createSkinSelector();

        // 音效设置区域
        VBox soundSection = createSoundSettings();

        // 返回按钮
        Button backBtn = createMenuButton("Back to Menu");
        backBtn.setOnAction(e -> showMenu());

        settingsContent.getChildren().addAll(
                titleLabel,
                createSpacer(10),
                skinSection,
                createSpacer(10),
                soundSection,
                createSpacer(15),
                backBtn
        );

        // 使用 ScrollPane 包裹内容，确保可以滚动
        ScrollPane scrollPane = new ScrollPane(settingsContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: #1A1A2E; -fx-background-color: #1A1A2E;");

        // 外层容器
        StackPane root = new StackPane(scrollPane);
        root.setBackground(new Background(new BackgroundFill(
                Color.web("#1A1A2E"), CornerRadii.EMPTY, Insets.EMPTY)));

        Scene settingsScene = new Scene(root, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        primaryStage.setScene(settingsScene);
    }

    private VBox createSkinSelector() {
        VBox skinSection = new VBox(20);
        skinSection.setAlignment(Pos.CENTER);
        skinSection.setPadding(new Insets(20));
        skinSection.setStyle(
                "-fx-background-color: #16213E; " +
                        "-fx-background-radius: 15; " +
                        "-fx-border-color: #0F3460; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 15;"
        );
        skinSection.setMaxWidth(500);

        // 皮肤选择标题
        Label skinLabel = new Label("Player Skin");
        skinLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        skinLabel.setTextFill(Color.WHITE);

        // 皮肤预览和切换区域
        HBox skinPreviewBox = new HBox(30);
        skinPreviewBox.setAlignment(Pos.CENTER);

        // 左箭头按钮
        Button leftArrow = createArrowButton("<");
        leftArrow.setOnAction(e -> {
            SkinManager.getInstance().previousSkin();
            showSettings(); // 刷新页面
        });

        // 皮肤预览
        VBox previewBox = createSkinPreview();

        // 右箭头按钮
        Button rightArrow = createArrowButton(">");
        rightArrow.setOnAction(e -> {
            SkinManager.getInstance().nextSkin();
            showSettings(); // 刷新页面
        });

        skinPreviewBox.getChildren().addAll(leftArrow, previewBox, rightArrow);

        skinSection.getChildren().addAll(skinLabel, skinPreviewBox);

        return skinSection;
    }

    private VBox createSoundSettings() {
        VBox soundSection = new VBox(15);
        soundSection.setAlignment(Pos.CENTER);
        soundSection.setPadding(new Insets(20));
        soundSection.setStyle(
                "-fx-background-color: #16213E; " +
                        "-fx-background-radius: 15; " +
                        "-fx-border-color: #0F3460; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 15;"
        );
        soundSection.setMaxWidth(500);

        // 音效设置标题
        Label soundLabel = new Label("Sound");
        soundLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        soundLabel.setTextFill(Color.WHITE);

        SoundManager soundManager = SoundManager.getInstance();

        // 音效开关
        HBox toggleBox = new HBox(15);
        toggleBox.setAlignment(Pos.CENTER);

        Label toggleLabel = new Label("Sound Effects:");
        toggleLabel.setFont(Font.font("Arial", 16));
        toggleLabel.setTextFill(Color.WHITE);

        Button toggleBtn = new Button(soundManager.isSoundEnabled() ? "ON" : "OFF");
        toggleBtn.setPrefWidth(80);
        toggleBtn.setStyle(soundManager.isSoundEnabled() ?
                "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;" :
                "-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
        toggleBtn.setOnAction(e -> {
            boolean newState = !soundManager.isSoundEnabled();
            soundManager.setSoundEnabled(newState);
            showSettings(); // 刷新页面
        });

        toggleBox.getChildren().addAll(toggleLabel, toggleBtn);

        // 音量滑块
        HBox volumeBox = new HBox(15);
        volumeBox.setAlignment(Pos.CENTER);

        Label volumeLabel = new Label("Volume:");
        volumeLabel.setFont(Font.font("Arial", 16));
        volumeLabel.setTextFill(Color.WHITE);

        Slider volumeSlider = new Slider(0, 100, soundManager.getVolume() * 100);
        volumeSlider.setPrefWidth(200);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setMajorTickUnit(25);
        volumeSlider.setMinorTickCount(4);
        volumeSlider.setBlockIncrement(5);
        volumeSlider.setStyle("-fx-control-inner-background: #0F3460;");

        Label volumeValue = new Label(String.format("%.0f%%", soundManager.getVolume() * 100));
        volumeValue.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        volumeValue.setTextFill(Color.GOLD);
        volumeValue.setMinWidth(50);

        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double volume = newVal.doubleValue() / 100.0;
            soundManager.setVolume(volume);
            volumeValue.setText(String.format("%.0f%%", newVal.doubleValue()));
        });

        volumeBox.getChildren().addAll(volumeLabel, volumeSlider, volumeValue);

        soundSection.getChildren().addAll(soundLabel, toggleBox, volumeBox);

        return soundSection;
    }

    private VBox createSkinPreview() {
        VBox previewBox = new VBox(15);
        previewBox.setAlignment(Pos.CENTER);
        previewBox.setMinWidth(200);

        SkinManager skinManager = SkinManager.getInstance();
        SkinType currentSkin = skinManager.getCurrentSkin();

        // 皮肤名称
        Label skinNameLabel = new Label(currentSkin.getDisplayName());
        skinNameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        skinNameLabel.setTextFill(Color.GOLD);

        // 四方向预览
        GridPane directionGrid = new GridPane();
        directionGrid.setAlignment(Pos.CENTER);
        directionGrid.setHgap(10);
        directionGrid.setVgap(10);

        // 上
        Image upImage = skinManager.getImage(Direction.UP);
        if (upImage != null) {
            ImageView upView = new ImageView(upImage);
            upView.setFitWidth(50);
            upView.setFitHeight(50);
            upView.setPreserveRatio(true);
            directionGrid.add(upView, 1, 0);
        }

        // 左
        Image leftImage = skinManager.getImage(Direction.LEFT);
        if (leftImage != null) {
            ImageView leftView = new ImageView(leftImage);
            leftView.setFitWidth(50);
            leftView.setFitHeight(50);
            leftView.setPreserveRatio(true);
            directionGrid.add(leftView, 0, 1);
        }

        // 下
        Image downImage = skinManager.getImage(Direction.DOWN);
        if (downImage != null) {
            ImageView downView = new ImageView(downImage);
            downView.setFitWidth(50);
            downView.setFitHeight(50);
            downView.setPreserveRatio(true);
            directionGrid.add(downView, 1, 1);
        }

        // 右
        Image rightImage = skinManager.getImage(Direction.RIGHT);
        if (rightImage != null) {
            ImageView rightView = new ImageView(rightImage);
            rightView.setFitWidth(50);
            rightView.setFitHeight(50);
            rightView.setPreserveRatio(true);
            directionGrid.add(rightView, 2, 1);
        }

        previewBox.getChildren().addAll(directionGrid, skinNameLabel);

        return previewBox;
    }

    private Button createArrowButton(String text) {
        Button button = new Button(text);
        button.setPrefSize(50, 50);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        button.setStyle(
                "-fx-background-color: #0F3460; " +
                        "-fx-text-fill: white; " +
                        "-fx-border-color: #E94560; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 25; " +
                        "-fx-background-radius: 25; " +
                        "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #E94560; " +
                        "-fx-text-fill: white; " +
                        "-fx-border-color: #FF6B6B; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 25; " +
                        "-fx-background-radius: 25; " +
                        "-fx-cursor: hand;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: #0F3460; " +
                        "-fx-text-fill: white; " +
                        "-fx-border-color: #E94560; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 25; " +
                        "-fx-background-radius: 25; " +
                        "-fx-cursor: hand;"
        ));

        return button;
    }

    public void showLevelSelect() {
        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setBackground(new Background(new BackgroundFill(
                Color.web("#1A1A2E"), CornerRadii.EMPTY, Insets.EMPTY)));

        // 标题
        Label titleLabel = new Label("Select Level");
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
        Button backBtn = createMenuButton("Back to Menu");
        backBtn.setOnAction(e -> showMenu());

        mainLayout.getChildren().addAll(titleLabel, createSpacer(20), levelGrid, createSpacer(20), backBtn);

        Scene levelSelectScene = new Scene(mainLayout, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        primaryStage.setScene(levelSelectScene);
    }

    public void startLevel(int level) {
        // 检查是否有新元素介绍
        if (LevelIntroData.hasIntro(level)) {
            showLevelIntro(level);
        } else {
            startLevelDirectly(level);
        }
    }

    public void startLevelDirectly(int level) {
        game = new Game(level);
        Scene gameScene = game.getScene();
        primaryStage.setScene(gameScene);
        game.start();
    }

    public void showLevelIntro(int level) {
        LevelIntro intro = LevelIntroData.getLevelIntro(level);
        if (intro == null) {
            startLevelDirectly(level);
            return;
        }

        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setBackground(new Background(new BackgroundFill(
                Color.web("#1A1A2E"), CornerRadii.EMPTY, Insets.EMPTY)));

        // 关卡标题
        Label levelLabel = new Label("Level " + level);
        levelLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        levelLabel.setTextFill(Color.LIGHTGRAY);

        // 章节标题
        Label titleLabel = new Label(intro.title());
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 42));
        titleLabel.setTextFill(Color.GOLD);

        // 副标题
        Label subtitleLabel = new Label(intro.subtitle());
        subtitleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        subtitleLabel.setTextFill(Color.LIGHTGRAY);

        // 新元素容器
        VBox elementsContainer = new VBox(25);
        elementsContainer.setAlignment(Pos.CENTER);
        elementsContainer.setPadding(new Insets(20, 0, 20, 0));

        // 新元素标题
        Label newElementsTitle = new Label("— New Elements —");
        newElementsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        newElementsTitle.setTextFill(Color.WHITE);
        elementsContainer.getChildren().add(newElementsTitle);

        // 添加每个新元素的介绍卡片
        for (NewElement element : intro.newElements()) {
            HBox elementCard = createElementCard(element);
            elementsContainer.getChildren().add(elementCard);
        }

        // 如果元素太多，使用滚动面板
        ScrollPane scrollPane = new ScrollPane(elementsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setMaxHeight(350);
        scrollPane.setStyle("-fx-background: #1A1A2E; -fx-background-color: #1A1A2E;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // 按钮区域
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        Button startBtn = createMenuButton("Start Game");
        startBtn.setOnAction(e -> startLevelDirectly(level));
        startBtn.setStyle(
                "-fx-background-color: #E94560; " +
                        "-fx-text-fill: white; " +
                        "-fx-border-color: #FF6B6B; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-cursor: hand;"
        );

        Button backBtn = createMenuButton("Back");
        backBtn.setOnAction(e -> showLevelSelect());

        buttonBox.getChildren().addAll(startBtn, backBtn);

        // 提示文字
        Label tipLabel = new Label("Press Enter to start quickly");
        tipLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        tipLabel.setTextFill(Color.GRAY);

        mainLayout.getChildren().addAll(
                levelLabel,
                titleLabel,
                subtitleLabel,
                createSpacer(10),
                scrollPane,
                createSpacer(10),
                buttonBox,
                tipLabel
        );

        Scene introScene = new Scene(mainLayout, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        // 添加键盘快捷键
        introScene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case ENTER, SPACE -> startLevelDirectly(level);
                case ESCAPE -> showLevelSelect();
                default -> {}
            }
        });

        primaryStage.setScene(introScene);
    }

    private HBox createElementCard(NewElement element) {
        HBox card = new HBox(20);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15));
        card.setMaxWidth(650);
        card.setStyle(
                "-fx-background-color: #16213E; " +
                        "-fx-background-radius: 15; " +
                        "-fx-border-color: " + element.color() + "; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 15;"
        );

        // 图标区域
        Canvas iconCanvas = new Canvas(80, 80);
        GraphicsContext gc = iconCanvas.getGraphicsContext2D();
        drawElementIcon(gc, element, 40, 40, 30);

        // 文字区域
        VBox textBox = new VBox(8);
        textBox.setAlignment(Pos.CENTER_LEFT);

        // 元素类型标签
        String typeText = switch (element.type()) {
            case ENEMY -> "[Enemy]";
            case ITEM -> "[Item]";
            case TERRAIN -> "[Terrain]";
            case MECHANIC -> "[Mechanic]";
        };
        Label typeLabel = new Label(typeText);
        typeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        typeLabel.setTextFill(Color.web(element.color()));

        // 名称
        Label nameLabel = new Label(element.name());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        nameLabel.setTextFill(Color.WHITE);

        // 描述
        Label descLabel = new Label(element.description());
        descLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        descLabel.setTextFill(Color.LIGHTGRAY);
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(450);

        // 提示（Tips）
        Label tipsLabel = new Label("💡 " + element.tips());
        tipsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        tipsLabel.setTextFill(Color.GOLD);
        tipsLabel.setWrapText(true);
        tipsLabel.setMaxWidth(450);

        textBox.getChildren().addAll(typeLabel, nameLabel, descLabel, tipsLabel);
        card.getChildren().addAll(iconCanvas, textBox);

        return card;
    }

    private void drawElementIcon(GraphicsContext gc, NewElement element, double centerX, double centerY, double size) {
        gc.setFill(Color.web(element.color()));

        switch (element.iconType()) {
            case "player" -> {
                // 绘制吃豆人
                gc.setFill(Color.YELLOW);
                gc.fillArc(centerX - size, centerY - size, size * 2, size * 2, 35, 290, ArcType.ROUND);
            }
            case "chaser", "wanderer", "hunter", "patroller", "phantom" -> {
                // 绘制幽灵形状
                drawGhostIcon(gc, centerX, centerY, size, element.color());
            }
            case "ice" -> {
                // 绘制冰面方块
                gc.fillRoundRect(centerX - size, centerY - size, size * 2, size * 2, 8, 8);
                gc.setStroke(Color.WHITE);
                gc.setLineWidth(2);
                gc.strokeLine(centerX - size * 0.5, centerY - size * 0.3, centerX + size * 0.3, centerY + size * 0.5);
                gc.strokeLine(centerX - size * 0.3, centerY + size * 0.2, centerX + size * 0.5, centerY - size * 0.4);
            }
            case "jumppad" -> {
                // 绘制跳板
                gc.fillRoundRect(centerX - size, centerY - size * 0.5, size * 2, size, 5, 5);
                gc.setFill(Color.WHITE);
                // 向上箭头
                gc.fillPolygon(
                        new double[]{centerX - size * 0.4, centerX, centerX + size * 0.4},
                        new double[]{centerY + size * 0.8, centerY - size * 0.8, centerY + size * 0.8},
                        3
                );
            }
            case "speedup" -> {
                // 绘制加速带
                gc.fillRoundRect(centerX - size, centerY - size * 0.5, size * 2, size, 5, 5);
                gc.setFill(Color.WHITE);
                // 双箭头
                double arrowY = centerY;
                gc.fillPolygon(
                        new double[]{centerX - size * 0.6, centerX - size * 0.2, centerX - size * 0.6},
                        new double[]{arrowY - size * 0.3, arrowY, arrowY + size * 0.3},
                        3
                );
                gc.fillPolygon(
                        new double[]{centerX + size * 0.1, centerX + size * 0.5, centerX + size * 0.1},
                        new double[]{arrowY - size * 0.3, arrowY, arrowY + size * 0.3},
                        3
                );
            }
            case "slowdown" -> {
                // 绘制减速带
                gc.fillRoundRect(centerX - size, centerY - size * 0.5, size * 2, size, 5, 5);
                gc.setStroke(Color.WHITE);
                gc.setLineWidth(3);
                // 横条纹
                for (int i = -2; i <= 2; i++) {
                    gc.strokeLine(centerX + i * size * 0.3, centerY - size * 0.3,
                            centerX + i * size * 0.3, centerY + size * 0.3);
                }
            }
            case "portal" -> {
                // 绘制传送门（旋涡效果）
                gc.setStroke(Color.web(element.color()));
                gc.setLineWidth(3);
                for (int i = 0; i < 3; i++) {
                    double r = size * (0.4 + i * 0.25);
                    gc.strokeOval(centerX - r, centerY - r, r * 2, r * 2);
                }
                gc.setFill(Color.web(element.color()));
                gc.fillOval(centerX - size * 0.2, centerY - size * 0.2, size * 0.4, size * 0.4);
            }
            case "oneway" -> {
                // 绘制单向通道
                gc.fillRoundRect(centerX - size, centerY - size * 0.5, size * 2, size, 5, 5);
                gc.setFill(Color.WHITE);
                // 箭头
                gc.fillPolygon(
                        new double[]{centerX - size * 0.5, centerX + size * 0.5, centerX - size * 0.5},
                        new double[]{centerY - size * 0.3, centerY, centerY + size * 0.3},
                        3
                );
            }
            case "blindtrap" -> {
                // 绘制致盲陷阱
                gc.fillRoundRect(centerX - size, centerY - size, size * 2, size * 2, 8, 8);
                gc.setFill(Color.BLACK);
                gc.fillOval(centerX - size * 0.5, centerY - size * 0.5, size, size);
                gc.setStroke(Color.WHITE);
                gc.setLineWidth(2);
                gc.strokeOval(centerX - size * 0.5, centerY - size * 0.5, size, size);
            }
            case "magnet" -> {
                // 绘制磁铁（U形）
                gc.setStroke(Color.web(element.color()));
                gc.setLineWidth(6);
                gc.strokeArc(centerX - size * 0.6, centerY - size * 0.6,
                        size * 1.2, size * 1.2, 180, 180, ArcType.OPEN);
                gc.setFill(Color.RED);
                gc.fillRect(centerX - size * 0.7, centerY - size * 0.1, size * 0.3, size * 0.6);
                gc.setFill(Color.BLUE);
                gc.fillRect(centerX + size * 0.4, centerY - size * 0.1, size * 0.3, size * 0.6);
            }
            case "shield" -> {
                // 绘制护盾
                gc.setFill(Color.web(element.color()));
                gc.fillOval(centerX - size * 0.8, centerY - size * 0.8, size * 1.6, size * 1.6);
                gc.setFill(Color.web("#1A1A2E"));
                gc.fillOval(centerX - size * 0.5, centerY - size * 0.5, size, size);
                gc.setFill(Color.web(element.color()));
                gc.fillOval(centerX - size * 0.3, centerY - size * 0.3, size * 0.6, size * 0.6);
            }
            case "wallpass" -> {
                // 绘制穿墙术（半透明墙+穿越效果）
                gc.setGlobalAlpha(0.5);
                gc.setFill(Color.GRAY);
                gc.fillRect(centerX - size * 0.8, centerY - size * 0.8, size * 1.6, size * 1.6);
                gc.setGlobalAlpha(1.0);
                gc.setFill(Color.web(element.color()));
                gc.fillOval(centerX - size * 0.4, centerY - size * 0.4, size * 0.8, size * 0.8);
                // 穿越线条
                gc.setStroke(Color.web(element.color()));
                gc.setLineWidth(2);
                gc.strokeLine(centerX - size, centerY, centerX + size, centerY);
            }
            default -> {
                // 默认：圆形
                gc.fillOval(centerX - size * 0.8, centerY - size * 0.8, size * 1.6, size * 1.6);
            }
        }
    }

    private void drawGhostIcon(GraphicsContext gc, double x, double y, double size, String color) {
        gc.setFill(Color.web(color));

        // 上半部分（半圆）
        gc.fillArc(x - size, y - size, size * 2, size * 2, 0, 180, ArcType.ROUND);

        // 下半部分（矩形）
        gc.fillRect(x - size, y, size * 2, size * 0.7);

        // 波浪底部
        double waveY = y + size * 0.7;
        double waveWidth = size * 2 / 3.0;
        for (int i = 0; i < 3; i++) {
            gc.fillOval(x - size + i * waveWidth, waveY - waveWidth / 4, waveWidth, waveWidth / 2);
        }

        // 眼睛
        gc.setFill(Color.WHITE);
        gc.fillOval(x - size * 0.5, y - size * 0.4, size * 0.4, size * 0.5);
        gc.fillOval(x + size * 0.1, y - size * 0.4, size * 0.4, size * 0.5);

        gc.setFill(Color.BLUE);
        gc.fillOval(x - size * 0.4, y - size * 0.3, size * 0.2, size * 0.3);
        gc.fillOval(x + size * 0.2, y - size * 0.3, size * 0.2, size * 0.3);
    }

    public void onLevelComplete(int level) {
        if (level >= unlockedLevel && level < Constants.TOTAL_LEVELS) {
            unlockedLevel = level + 1;
        }
        if (level >= Constants.TOTAL_LEVELS) {
            showVictoryScreen();
        } else {
            showLevelCompleteScreen(level);
        }
    }

    public void onGameOver(int level) {
        showGameOverScreen(level);
    }

    private void showLevelCompleteScreen(int level) {
        VBox layout = new VBox(30);
        layout.setAlignment(Pos.CENTER);
        layout.setBackground(new Background(new BackgroundFill(
                Color.web("#1A1A2E"), CornerRadii.EMPTY, Insets.EMPTY)));

        Label titleLabel = new Label("Level " + level + " Complete!");
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

        Button nextLevelBtn = createMenuButton("Next Level");
        nextLevelBtn.setOnAction(e -> startLevel(level + 1));

        Button selectBtn = createMenuButton("Select Level");
        selectBtn.setOnAction(e -> showLevelSelect());

        Button menuBtn = createMenuButton("Back to Menu");
        menuBtn.setOnAction(e -> showMenu());

        layout.getChildren().addAll(titleLabel, createSpacer(20), nextLevelBtn, selectBtn, menuBtn);

        Scene scene = new Scene(layout, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        primaryStage.setScene(scene);
    }

    private void showGameOverScreen(int level) {
        VBox layout = new VBox(30);
        layout.setAlignment(Pos.CENTER);
        layout.setBackground(new Background(new BackgroundFill(
                Color.web("#1A1A2E"), CornerRadii.EMPTY, Insets.EMPTY)));

        Label titleLabel = new Label("Game Over");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        titleLabel.setTextFill(Color.RED);

        Label levelLabel = new Label("Failed at Level " + level);
        levelLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 24));
        levelLabel.setTextFill(Color.LIGHTGRAY);

        Button retryBtn = createMenuButton("Retry");
        retryBtn.setOnAction(e -> startLevel(level));

        Button selectBtn = createMenuButton("Select Level");
        selectBtn.setOnAction(e -> showLevelSelect());

        Button menuBtn = createMenuButton("Back to Menu");
        menuBtn.setOnAction(e -> showMenu());

        layout.getChildren().addAll(titleLabel, levelLabel, createSpacer(20), retryBtn, selectBtn, menuBtn);

        Scene scene = new Scene(layout, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        primaryStage.setScene(scene);
    }

    private void showVictoryScreen() {
        VBox layout = new VBox(30);
        layout.setAlignment(Pos.CENTER);
        layout.setBackground(new Background(new BackgroundFill(
                Color.web("#1A1A2E"), CornerRadii.EMPTY, Insets.EMPTY)));

        Label titleLabel = new Label("Congratulations!");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        titleLabel.setTextFill(Color.GOLD);

        Label msgLabel = new Label("You've completed all 30 levels!");
        msgLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 24));
        msgLabel.setTextFill(Color.WHITE);

        Button selectBtn = createMenuButton("Play Again");
        selectBtn.setOnAction(e -> showLevelSelect());

        Button menuBtn = createMenuButton("Back to Menu");
        menuBtn.setOnAction(e -> showMenu());

        layout.getChildren().addAll(titleLabel, msgLabel, createSpacer(20), selectBtn, menuBtn);

        Scene scene = new Scene(layout, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        primaryStage.setScene(scene);
    }

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

    private Button createLevelButton(int level) {
        Button button = new Button(String.valueOf(level));
        button.setPrefSize(60, 60);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        boolean isUnlocked = level <= unlockedLevel;
        boolean isFusionLevel = (level % 6 == 0);

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

    private Region createSpacer(double height) {
        Region spacer = new Region();
        spacer.setMinHeight(height);
        spacer.setPrefHeight(height);
        return spacer;
    }

    private String getChapterStory(int level) {
        return switch (level) {
            case 1 -> "Something stirs in the maze...";
            case 6 -> "Chapter 1 Complete!\n\"A hungry little spirit stumbled into the mysterious maze. Collect all the dots to find the treasure... but the guardians won't make it easy.\"";
            case 12 -> "Chapter 2 Complete!\n\"Deep in the maze, the ground becomes treacherous - icy floors and speed zones await...\"";
            case 18 -> "Chapter 3 Complete!\n\"Ancient magical items are scattered throughout the maze. Use them wisely to turn the tide...\"";
            case 24 -> "Chapter 4 Complete!\n\"Invisible hunters lurk in the shadows, appearing and disappearing without warning...\"";
            case 30 -> "Final Chapter Complete!\n\"In the depths of the maze, all dangers converge. You have proven yourself a true hero!\"";
            default -> null;
        };
    }

    public Game getGame() { return game; }
    public int getUnlockedLevel() { return unlockedLevel; }
    public void setUnlockedLevel(int level) {
        this.unlockedLevel = Math.min(level, Constants.TOTAL_LEVELS);
    }
}