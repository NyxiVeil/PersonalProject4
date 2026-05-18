package cs2.javafx.controllers;

import cs2.javafx.ScreenManager;
import cs2.javafx.model.GameManager;
import cs2.javafx.model.SaveData;
import cs2.javafx.model.SaveManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SaveSelectionController {

    @FXML private Label slot1Label;
    @FXML private Button btnSelect1;
    @FXML private Button btnDelete1;

    @FXML private Label slot2Label;
    @FXML private Button btnSelect2;
    @FXML private Button btnDelete2;

    @FXML private Label slot3Label;
    @FXML private Button btnSelect3;
    @FXML private Button btnDelete3;

    @FXML
    public void initialize() {
        refreshSlots();
    }

    private void refreshSlots() {
        updateSlotUI(1, slot1Label, btnDelete1);
        updateSlotUI(2, slot2Label, btnDelete2);
        updateSlotUI(3, slot3Label, btnDelete3);
    }

    private void updateSlotUI(int slot, Label label, Button btnDelete) {
        if (SaveManager.isSlotOccupied(slot)) {
            SaveData data = SaveManager.loadGame(slot);
            if (data != null && data.getPlayerState() != null) {
                String dateStr = new SimpleDateFormat("MM/dd HH:mm").format(new Date(data.getLastSavedTimestamp()));
                String info = String.format("Day %d | %s [%s] | %s",
                        data.getCurrentDay(),
                        data.getPlayerState().getName(),
                        data.getPlayerState().getPlayerClass().getDisplayName(),
                        dateStr);
                label.setText(info);
                btnDelete.setVisible(true);
            } else {
                label.setText("Corrupted Data");
                btnDelete.setVisible(true);
            }
        } else {
            label.setText("Empty Slot");
            btnDelete.setVisible(false);
        }
    }

    private void handleSelect(int slot) {
        GameManager gm = GameManager.getInstance();
        if (SaveManager.isSlotOccupied(slot)) {
            SaveData data = SaveManager.loadGame(slot);
            if (data != null) {
                gm.loadFromSaveData(data);
                gm.setCurrentSaveSlot(slot);
                ScreenManager.setScreen("/cs2/javafx/views/MainGameScreen.fxml");
                // The MainGameScreenController will load, and we can log loaded.
            }
        } else {
            // New game
            gm.resetGame();
            gm.setCurrentSaveSlot(slot);
            ScreenManager.setScreen("/cs2/javafx/views/MainGameScreen.fxml");
        }
    }

    private void handleDelete(int slot) {
        SaveManager.deleteGame(slot);
        refreshSlots();
    }

    @FXML private void onSelectSlot1(ActionEvent event) { handleSelect(1); }
    @FXML private void onDeleteSlot1(ActionEvent event) { handleDelete(1); }

    @FXML private void onSelectSlot2(ActionEvent event) { handleSelect(2); }
    @FXML private void onDeleteSlot2(ActionEvent event) { handleDelete(2); }

    @FXML private void onSelectSlot3(ActionEvent event) { handleSelect(3); }
    @FXML private void onDeleteSlot3(ActionEvent event) { handleDelete(3); }

    @FXML private void onBack(ActionEvent event) {
        ScreenManager.setScreen("/cs2/javafx/views/StartScreen.fxml");
    }
}
