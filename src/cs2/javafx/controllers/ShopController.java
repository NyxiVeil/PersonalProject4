package cs2.javafx.controllers;

import cs2.javafx.model.GameManager;
import cs2.javafx.model.Item;
import cs2.javafx.model.PlayerState;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ShopController {

    @FXML private Label goldLabel;

    private PlayerState player;

    @FXML
    public void initialize() {
        refreshGoldLabel();
    }

    private void refreshGoldLabel() {
        player = GameManager.getInstance().getPlayerState();
        if (player != null) {
            goldLabel.setText(player.getGold() + " Gold 💰");
        } else {
            goldLabel.setText("N/A");
        }
    }

    @FXML
    private void handleBuyApple(ActionEvent event) {
        buyItem(Item.APPLE.getDisplayName(), 3);
    }

    @FXML
    private void handleBuyPotion(ActionEvent event) {
        buyItem(Item.HEALTH_POTION.getDisplayName(), 12);
    }

    private void buyItem(String itemName, int price) {
        if (player == null) {
            log("ERROR: No active hero found to make a purchase!");
            return;
        }

        // 1. Check Gold
        if (player.getGold() < price) {
            log("❌ Town Merchant: You do not have enough gold for a " + itemName + "!");
            return;
        }

        // 2. Check Inventory Capacity & Stack Limit using addItem
        boolean added = player.addItem(itemName, 1);
        if (added) {
            // Deduct gold
            player.addGold(-price);
            refreshGoldLabel();
            log("🛒 Purchased 1x " + itemName + " for " + price + " gold. Current Gold: " + player.getGold() + ".");
        } else {
            log("🎒 Town Merchant: Your bags are full! Cannot buy " + itemName + " (Max 10 item types, max 10 stack size).");
        }
    }

    private void log(String msg) {
        if (MainGameScreenController.getInstance() != null) {
            MainGameScreenController.getInstance().logMessage(msg);
        } else {
            System.out.println(msg);
        }
    }
}
