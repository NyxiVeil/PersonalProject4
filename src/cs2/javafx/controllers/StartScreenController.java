package cs2.javafx.controllers;

import cs2.javafx.ScreenManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class StartScreenController {

    @FXML
    private void handleStartGame(ActionEvent event) {
        // Switch to the save selection screen
        ScreenManager.setScreen("/cs2/javafx/views/SaveSelectionScreen.fxml");
    }
}
