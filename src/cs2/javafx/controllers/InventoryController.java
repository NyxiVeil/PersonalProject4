package cs2.javafx.controllers;

import cs2.javafx.model.GameManager;
import cs2.javafx.model.Item;
import cs2.javafx.model.PlayerState;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InventoryController {

    @FXML private Label capacityLabel;
    @FXML private Label goldLabel;
    @FXML private ListView<String> itemListView;
    @FXML private Button useBtn;
    @FXML private Button deleteBtn;
    @FXML private Button closeBtn;

    private PlayerState player;
    private boolean isCombat;
    private String usedItem = null;

    public void initData(PlayerState player, boolean isCombat) {
        this.player = player;
        this.isCombat = isCombat;
        
        if (isCombat) {
            deleteBtn.setDisable(true);
            deleteBtn.setText("Delete (In Combat)");
        }
        
        refreshUI();
    }

    private void refreshUI() {
        if (player == null) return;

        goldLabel.setText(player.getGold() + "g");
        capacityLabel.setText("(" + player.getInventory().size() + "/10 types)");

        List<String> displayList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : player.getInventory().entrySet()) {
            displayList.add(entry.getKey() + " x" + entry.getValue());
        }
        itemListView.setItems(FXCollections.observableArrayList(displayList));
    }

    @FXML
    private void handleUse(ActionEvent event) {
        String selected = itemListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        // Parse key name from "ItemName xQty"
        String itemName = selected.split(" x")[0];

        if (isCombat) {
            // In combat, we pass the decision back to BattleController to process it as a turn
            usedItem = itemName;
            closeWindow();
        } else {
            // Out of combat: apply immediately
            Item item = Item.fromDisplayName(itemName);
            if (item != null) {
                player.removeItem(itemName);
                switch (item) {
                    case APPLE:
                        player.heal(item.getHealAmount());
                        if (MainGameScreenController.getInstance() != null) {
                            MainGameScreenController.getInstance().logMessage("🍎 Ate an Apple outside of battle. Restored 5 HP.");
                        }
                        break;
                    case HEALTH_POTION:
                        player.heal(item.getHealAmount());
                        if (MainGameScreenController.getInstance() != null) {
                            MainGameScreenController.getInstance().logMessage("🧪 Drank a Health Potion outside of battle. Restored 25 HP.");
                        }
                        break;
                    case LIFE_RELIC:
                        player.addExtraLife();
                        if (MainGameScreenController.getInstance() != null) {
                            MainGameScreenController.getInstance().logMessage("💎 Gained 1 extra life outside of battle.");
                        }
                        break;
                }
                refreshUI();
            }
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        String selected = itemListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        String itemName = selected.split(" x")[0];
        player.removeItem(itemName);
        if (MainGameScreenController.getInstance() != null) {
            MainGameScreenController.getInstance().logMessage("🗑 Deleted " + itemName + " from inventory.");
        }
        refreshUI();
    }

    @FXML
    private void handleClose(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }

    public String getUsedItem() {
        return usedItem;
    }

    /**
     * Helper to show the inventory as a modal dialog and return any item used during combat.
     */
    public static String showInventoryDialog(Window owner, PlayerState player, boolean isCombat) {
        try {
            FXMLLoader loader = new FXMLLoader(InventoryController.class.getResource("/cs2/javafx/views/Inventory.fxml"));
            Parent root = loader.load();

            InventoryController ctrl = loader.getController();
            ctrl.initData(player, isCombat);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(owner);
            stage.setTitle(isCombat ? "Use Item (Combat)" : "Inventory");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            return ctrl.getUsedItem();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
