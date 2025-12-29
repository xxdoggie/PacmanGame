package com.pacman;

import com.pacman.ui.SceneManager;
import com.pacman.util.Constants;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main entry point class extending JavaFX Application.
 */
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle(Constants.GAME_TITLE);
        primaryStage.setResizable(false);

        SceneManager sceneManager = SceneManager.getInstance();
        sceneManager.initialize(primaryStage);
        sceneManager.showMenu();

        primaryStage.show();
        System.out.println("Game started successfully!");
    }

    @Override
    public void stop() {
        System.out.println("Game closed.");
    }
}
