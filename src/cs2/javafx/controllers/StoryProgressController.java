package cs2.javafx.controllers;

import cs2.javafx.model.GameManager;
import cs2.javafx.model.StoryDay;
import cs2.javafx.model.StoryManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class StoryProgressController {

    @FXML private Label dayTitleLabel;
    @FXML private Label storyDescriptionLabel;
    @FXML private HBox choicesContainer;
    @FXML private VBox developerToolsContainer;

    private GameManager gm;

    @FXML
    public void initialize() {
        gm = GameManager.getInstance();
        refreshUI();
    }

    private void refreshUI() {
        if (gm.isGameOver()) {
            dayTitleLabel.setText("Game Over");
            storyDescriptionLabel.setText("You have fallen. Your journey ends here.");
            choicesContainer.getChildren().clear();
            developerToolsContainer.setVisible(false);
            developerToolsContainer.setManaged(false);
            return;
        }

        int currentDay = gm.getCurrentDay();
        StoryDay dayInfo = StoryManager.getDay(currentDay);

        dayTitleLabel.setText(dayInfo.getTitle());
        storyDescriptionLabel.setText(dayInfo.getDescription());

        // Update choices
        choicesContainer.getChildren().clear();
        for (String choice : dayInfo.getChoices()) {
            Button btn = new Button(choice);
            btn.setStyle("-fx-background-color: #D4A843; -fx-text-fill: #2B1200; -fx-font-family: 'Georgia'; -fx-cursor: hand; -fx-padding: 8 16;");
            btn.setOnAction(e -> handleChoice(choice, dayInfo));
            choicesContainer.getChildren().add(btn);
        }

        // Handle developer skip tool visibility
        if (gm.isTestMode() && dayInfo.isBattleDay()) {
            developerToolsContainer.setVisible(true);
            developerToolsContainer.setManaged(true);
        } else {
            developerToolsContainer.setVisible(false);
            developerToolsContainer.setManaged(false);
        }
    }

    private void handleChoice(String choice, StoryDay dayInfo) {
        if (choice.equals("Fight")) {
            if (MainGameScreenController.getInstance() != null) {
                MainGameScreenController.getInstance().startStoryBattle(dayInfo.getEnemyName());
            } else {
                System.out.println("Switching to Battle View for: " + dayInfo.getEnemyName());
            }
        } else {
            boolean shouldRefresh = StoryManager.processChoice(gm.getCurrentDay(), choice, gm);
            if (shouldRefresh) {
                gm.advanceDay();
            }
            refreshUI();
        }
    }

    @FXML
    private void handleAutoWin(ActionEvent event) {
        int day = gm.getCurrentDay();
        StoryManager.applyCombatResult(day, true, false, gm);
        if (MainGameScreenController.getInstance() != null) {
            MainGameScreenController.getInstance().logMessage("Auto Win applied! You skipped the battle and gained rewards for Day " + day + ".");
        }
        refreshUI();
    }

    @FXML
    private void handleAutoLose(ActionEvent event) {
        int day = gm.getCurrentDay();
        StoryManager.applyCombatResult(day, false, false, gm);
        if (MainGameScreenController.getInstance() != null) {
            MainGameScreenController.getInstance().logMessage("Auto Lose applied! You were defeated on Day " + day + ".");
        }
        refreshUI();
    }
}
