package cs2.javafx;
// This package name organizes our code and matches the folder structure

import javafx.application.Application;
// The base class for all JavaFX applications

import javafx.scene.Scene;
// A Scene holds all visual content shown in the window

import javafx.scene.control.Label;
// A simple UI control that displays text

import javafx.stage.Stage;
// The main window of a JavaFX application

public class MainApp extends Application {
    // Our class must extend Application to use JavaFX

    @Override
    public void start(Stage stage) {
        // This method runs automatically when the JavaFX app starts

        Label label = new Label("Hill’s Lawn & Property Care — JavaFX is working!");
        // Create a text label to display in the window

        Scene scene = new Scene(label, 500, 250);
        // Create a scene with the label inside it
        // 500 = width, 250 = height (in pixels)

        stage.setTitle("Hill’s Lawn & Property Care");
        // Set the title text shown at the top of the window

        stage.setScene(scene);
        // Attach the scene to the window (stage)

        stage.show();
        // Display the window on the screen
    }

    public static void main(String[] args) {
        // This is the standard Java starting point

        launch();
        // Starts the JavaFX application and calls start()
    }
}