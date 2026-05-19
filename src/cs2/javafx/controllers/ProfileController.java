package cs2.javafx.controllers;

import cs2.javafx.model.GameManager;
import cs2.javafx.model.PlayerState;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ProfileController {

    @FXML private Label nameLabel;
    @FXML private Label classLabel;
    @FXML private Label dayLabel;
    @FXML private Label goldLabel;
    @FXML private Label attackLabel;
    @FXML private Label healthLabel;
    @FXML private Label speedLabel;
    @FXML private Label livesLabel;

    @FXML
    public void initialize() {
        GameManager gm = GameManager.getInstance();
        PlayerState player = gm.getPlayerState();

        if (player == null) {
            nameLabel.setText("No active hero found.");
            classLabel.setText("Please start a new game or load a save.");
            dayLabel.setText("Day: N/A");
            goldLabel.setText("Gold: N/A");
            attackLabel.setText("N/A");
            healthLabel.setText("N/A");
            speedLabel.setText("N/A");
            livesLabel.setText("N/A");
            return;
        }

        nameLabel.setText("Hero Name: " + player.getName());
        classLabel.setText("Class: " + player.getPlayerClass().getDisplayName());
        dayLabel.setText("Current Progress: Day " + gm.getCurrentDay());
        goldLabel.setText("Gold: " + player.getGold() + " 💰");
        
        // Attack: base and final (accounting for gear score if applicable)
        int baseAttack = player.getDamage();
        int finalAttack = player.getFinalDamage();
        if (player.getGearScore() != 1.0) {
            attackLabel.setText(baseAttack + " (Base)  [Final: " + finalAttack + " (Gear Score: " + player.getGearScore() + ")]");
        } else {
            attackLabel.setText(String.valueOf(baseAttack));
        }

        healthLabel.setText(player.getCurrentHP() + " / " + player.getMaxHP() + " HP");
        speedLabel.setText(String.valueOf(player.getAttackSpeed()));
        livesLabel.setText(String.valueOf(player.getExtraLives()));
    }
}
