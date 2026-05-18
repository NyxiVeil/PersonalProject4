package cs2.javafx.controllers;

import cs2.javafx.ScreenManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import cs2.javafx.model.GameManager;
import cs2.javafx.model.SaveData;
import cs2.javafx.model.SaveManager;

public class MainGameScreenController {

    private static MainGameScreenController instance;

    @FXML
    private BorderPane centerContainer;
    
    @FXML
    private TextArea combatLog;

    @FXML
    public void initialize() {
        instance = this;
        // Load the initial sub-pane
        showStoryProgress(null);
        logMessage("Welcome to Thornhollow!");
    }

    public static MainGameScreenController getInstance() {
        return instance;
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

    public void startStoryBattle(String enemyName) {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/cs2/javafx/views/Battle.fxml"));
        try {
            javafx.scene.Parent view = loader.load();
            cs2.javafx.controllers.BattleController battleCtrl = loader.getController();
            battleCtrl.setParentController(this);
            centerContainer.setCenter(view);
            logMessage("=== Story Battle: " + enemyName + " ===");
        } catch (java.io.IOException e) {
            logMessage("ERROR: Could not load Battle.fxml — " + e.getMessage());
            e.printStackTrace();
        }
    }

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
        GameManager gm = GameManager.getInstance();
        if (gm.getCurrentSaveSlot() == -1) {
            logMessage("Cannot save — no slot selected!");
            return;
        }
        SaveData data = new SaveData(
                gm.getPlayerState(),
                gm.getCurrentDay(),
                gm.isVillagerQuestAccepted()
        );
        SaveManager.saveGame(gm.getCurrentSaveSlot(), data);
        logMessage("Game Saved to Slot " + gm.getCurrentSaveSlot() + ".");
    }

    @FXML
    private void handleLoad(ActionEvent event) {
        // Switch back to save selection screen to load a different save
        ScreenManager.setScreen("/cs2/javafx/views/SaveSelectionScreen.fxml");
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
