package cs2.javafx;
// This package name organizes our code and matches the folder structure

import javafx.application.Application;
// The base class for all JavaFX applications

import javafx.stage.Stage;
// The main window of a JavaFX application

public class MainApp extends Application {
    // Our class must extend Application to use JavaFX

    @Override
    public void start(Stage stage) {
        // This method runs automatically when the JavaFX app starts

        stage.setTitle("Hill’s Lawn & Property Care");
        // Set the title text shown at the top of the window
        
        // Initialize the ScreenManager with the primary stage
        ScreenManager.initialize(stage);
        
        // Load the initial start screen
        ScreenManager.setScreen("/cs2/javafx/views/StartScreen.fxml");

        stage.show();
        // Display the window on the screen
    }

    public static void main(String[] args) {
        // This is the standard Java starting point

        launch();
        // Starts the JavaFX application and calls start()
    }
}