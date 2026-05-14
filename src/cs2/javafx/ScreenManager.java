package cs2.javafx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ScreenManager {
    private static Stage primaryStage;

    public static void initialize(Stage stage) {
        primaryStage = stage;
    }

    public static void setScreen(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(ScreenManager.class.getResource(fxmlPath));
            Parent root = loader.load();
            if (primaryStage.getScene() == null) {
                Scene scene = new Scene(root, 800, 600);
                primaryStage.setScene(scene);
            } else {
                primaryStage.getScene().setRoot(root);
            }
        } catch (IOException e) {
            System.err.println("Failed to load screen: " + fxmlPath);
            e.printStackTrace();
        }
    }

    public static Parent loadSubPane(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(ScreenManager.class.getResource(fxmlPath));
            return loader.load();
        } catch (IOException e) {
            System.err.println("Failed to load sub-pane: " + fxmlPath);
            e.printStackTrace();
            return null;
        }
    }
}
