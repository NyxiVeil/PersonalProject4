package cs2.javafx.controllers;

import cs2.javafx.model.GameManager;
import cs2.javafx.model.QuestManager;
import cs2.javafx.model.QuestManager.RepeatableQuest;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class QuestController {

    @FXML private ListView<String> questListView;
    @FXML private VBox progressContainer;
    @FXML private Label activeQuestNameLabel;
    @FXML private ProgressBar questProgressBar;
    @FXML private Button startQuestBtn;

    private Timeline progressTimeline;

    @FXML
    public void initialize() {
        refreshUI();

        // If a quest is already running in the background when opening this tab, track it!
        if (QuestManager.isQuestRunning()) {
            startProgressTracker();
        }
    }

    private void refreshUI() {
        GameManager gm = GameManager.getInstance();
        int currentDay = gm.getCurrentDay();

        List<RepeatableQuest> unlocked = QuestManager.getUnlockedQuests(currentDay);
        List<String> displayList = new ArrayList<>();
        
        if (unlocked.isEmpty()) {
            displayList.add("No quests unlocked yet. Beat Day 1 to unlock the first quest!");
            startQuestBtn.setDisable(true);
        } else {
            for (RepeatableQuest q : unlocked) {
                displayList.add(q.getName() + " — " + q.getDescription());
            }
            startQuestBtn.setDisable(QuestManager.isQuestRunning());
        }

        questListView.setItems(FXCollections.observableArrayList(displayList));
    }

    @FXML
    private void handleStartQuest(ActionEvent event) {
        String selected = questListView.getSelectionModel().getSelectedItem();
        if (selected == null || selected.startsWith("No quests")) return;

        // Extract name
        String questName = selected.split(" — ")[0];

        QuestManager.startQuest(questName);
        startProgressTracker();
    }

    private void startProgressTracker() {
        if (progressTimeline != null) {
            progressTimeline.stop();
        }

        progressContainer.setVisible(true);
        progressContainer.setManaged(true);
        startQuestBtn.setDisable(true);

        progressTimeline = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            if (QuestManager.isQuestRunning()) {
                activeQuestNameLabel.setText("Quest Running: " + QuestManager.getActiveQuestName());
                questProgressBar.setProgress(QuestManager.getQuestProgress());
            } else {
                progressContainer.setVisible(false);
                progressContainer.setManaged(false);
                startQuestBtn.setDisable(false);
                progressTimeline.stop();
                refreshUI();
            }
        }));
        progressTimeline.setCycleCount(Timeline.INDEFINITE);
        progressTimeline.play();
    }
}
