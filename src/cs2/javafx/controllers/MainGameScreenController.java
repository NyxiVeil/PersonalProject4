package cs2.javafx.controllers;

import cs2.javafx.ScreenManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

public class MainGameScreenController {

    @FXML
    private BorderPane centerContainer;
    
    @FXML
    private TextArea combatLog;

    @FXML
    public void initialize() {
        // Load the initial sub-pane
        showStoryProgress(null);
        logMessage("Welcome to Thornhollow!");
    }

    @FXML
    private void showStoryProgress(ActionEvent event) {
        Parent view = ScreenManager.loadSubPane("/cs2/javafx/views/StoryProgress.fxml");
        if (view != null) {
            centerContainer.setCenter(view);
        }
    }

    @FXML
    private void showBattle(ActionEvent event) {
        logMessage("Battle screen not yet implemented — use ⚔ Test Battle to test combat.");
    }

    // TEMPORARY TEST FEATURE — REMOVE LATER
    @FXML
    private void showTestBattle(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/cs2/javafx/views/Battle.fxml"));
        try {
            javafx.scene.Parent view = loader.load();
            // Inject this controller so the battle log writes to the bottom TextArea
            cs2.javafx.controllers.BattleController battleCtrl = loader.getController();
            battleCtrl.setParentController(this);
            centerContainer.setCenter(view);
            logMessage("=== Test Battle loaded — choose your class and enemy, then click Start Battle ===");
        } catch (java.io.IOException e) {
            logMessage("ERROR: Could not load Battle.fxml — " + e.getMessage());
            e.printStackTrace();
        }
    }
    // END TEMPORARY TEST FEATURE

    @FXML
    private void showInventory(ActionEvent event) {
        logMessage("Inventory screen not implemented yet.");
    }

    @FXML
    private void showQuest(ActionEvent event) {
        logMessage("Quest screen not implemented yet.");
    }

    @FXML
    private void handleSave(ActionEvent event) {
        logMessage("Game Saved.");
    }

    @FXML
    private void handleLoad(ActionEvent event) {
        logMessage("Game Loaded.");
    }

    @FXML
    private void handleExit(ActionEvent event) {
        // Switch back to start screen
        ScreenManager.setScreen("/cs2/javafx/views/StartScreen.fxml");
    }
    
    public void logMessage(String message) {
        if (combatLog != null) {
            combatLog.appendText(message + "\n");
        }
    }
}
