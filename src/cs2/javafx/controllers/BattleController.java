package cs2.javafx.controllers;

import cs2.javafx.combat.CombatEngine;
import cs2.javafx.model.*;
import cs2.javafx.model.CombatResult.Entry;
import cs2.javafx.model.CombatResult.UIUpdate;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ─────────────────────────────────────────────────────────────────
 *  BattleController — Wires Battle.fxml to CombatEngine.
 * ─────────────────────────────────────────────────────────────────
 *
 * Flow:
 *   1. setupPanel is shown first with class + enemy dropdowns.
 *   2. onStartBattle() creates PlayerState and Enemy, starts CombatEngine.
 *   3. battlePanel becomes visible; setupPanel is hidden.
 *   4. Each action button calls the corresponding CombatEngine method.
 *   5. Results are forwarded to the parent MainGameScreenController log
 *      one line at a time with a short animated delay between each line
 *      (FIX 1 — action delays via SequentialTransition).
 *   6. HP bars and labels update after every action.
 *   7. When combat ends, action buttons are disabled and "New Battle" appears.
 *
 * The parent controller reference is injected by MainGameScreenController
 * via setParentController() after loading this sub-pane.
 *
 * // TEMPORARY TEST FEATURE — REMOVE LATER
 */
public class BattleController {

    // ── Setup panel ───────────────────────────────────────────────
    @FXML private VBox     setupPanel;
    @FXML private ComboBox<String> classSelector;
    @FXML private ComboBox<String> enemySelector;
    @FXML private Label    passiveLabel;
    @FXML private Button   startBattleBtn;

    // ── Battle panel ──────────────────────────────────────────────
    @FXML private VBox     battlePanel;
    @FXML private Label    turnLabel;
    @FXML private Label    playerNameLabel;
    @FXML private ProgressBar playerHPBar;
    @FXML private Label    playerHPLabel;
    @FXML private Label    enemyNameLabel;
    @FXML private ProgressBar enemyHPBar;
    @FXML private Label    enemyHPLabel;
    @FXML private Button   btnAttack;
    @FXML private Button   btnBlock;
    @FXML private Button   btnDodge;
    @FXML private Button   btnUseItem;
    @FXML private Button   btnFlee;
    @FXML private Label    actionTooltipLabel;
    @FXML private Label    inventoryLabel;
    @FXML private Button   btnRestart;

    // ── State ─────────────────────────────────────────────────────
    private final CombatEngine engine = new CombatEngine();
    private MainGameScreenController parentController;
    private boolean isStoryBattle = false;
    private CombatResult.Outcome lastOutcome = null;

    /**
     * FIX 1 — Delay between sequential combat actions.
     * Each log line is revealed after this many milliseconds so events do not
     * resolve instantly and the player can follow what happened step-by-step.
     */
    private static final double LOG_DELAY_MS = 600;

    /** Enemy name → factory lambda map (preserves insertion order for ComboBox). */
    private static final Map<String, java.util.function.Supplier<Enemy>> ENEMY_MAP;
    static {
        ENEMY_MAP = new LinkedHashMap<>();
        ENEMY_MAP.put("Slime (10 HP)",              EnemyFactory::createSlime);
        ENEMY_MAP.put("Goblin (20 HP)",             EnemyFactory::createGoblin);
        ENEMY_MAP.put("Skeleton (30 HP)",           EnemyFactory::createSkeleton);
        ENEMY_MAP.put("King Slime — BOSS (70 HP)",  EnemyFactory::createKingSlime);
        ENEMY_MAP.put("Goblin King — BOSS (100 HP)",EnemyFactory::createGoblinKing);
        ENEMY_MAP.put("Colossal Skeleton — BOSS (200 HP)", EnemyFactory::createColossalSkeleton);
        ENEMY_MAP.put("Dragon — FINAL BOSS (500 HP)", EnemyFactory::createDragon);
    }

    // ── FXML init ─────────────────────────────────────────────────

    @FXML
    public void initialize() {
        // Populate class selector
        classSelector.setItems(FXCollections.observableArrayList(
                PlayerClass.MAGE.getDisplayName(),
                PlayerClass.ARCHER.getDisplayName(),
                PlayerClass.KNIGHT.getDisplayName(),
                PlayerClass.THIEF.getDisplayName()
        ));

        // Populate enemy selector
        enemySelector.setItems(
                FXCollections.observableArrayList(ENEMY_MAP.keySet()));
        enemySelector.getSelectionModel().selectFirst(); // default: Slime
    }

    // ── Setup panel handlers ──────────────────────────────────────

    /** Updates the passive description label when a class is picked. */
    @FXML
    private void onClassSelected() {
        String selected = classSelector.getValue();
        if (selected == null) return;
        for (PlayerClass pc : PlayerClass.values()) {
            if (pc.getDisplayName().equals(selected)) {
                passiveLabel.setText("Passive: " + pc.getPassiveDescription());
                break;
            }
        }
    }

    /**
     * Validates selections, builds PlayerState + Enemy, starts CombatEngine.
     * Pre-seeds inventory with test items so UseItem can be demonstrated.
     *
     * // TEMPORARY TEST FEATURE — REMOVE LATER
     */
    @FXML
    private void onStartBattle() {
        String className  = classSelector.getValue();
        String enemyLabel = enemySelector.getValue();

        if (className == null) {
            log("Please select a class before starting.");
            return;
        }
        if (enemyLabel == null) {
            log("Please select an enemy before starting.");
            return;
        }

        // Build player
        PlayerClass chosenClass = null;
        for (PlayerClass pc : PlayerClass.values()) {
            if (pc.getDisplayName().equals(className)) { chosenClass = pc; break; }
        }
        PlayerState player = new PlayerState("Hero", chosenClass);

        // Seed test inventory so UseItem can be tested
        // TEMPORARY TEST FEATURE — REMOVE LATER
        player.addItem(Item.APPLE.getDisplayName(), 2);
        player.addItem(Item.HEALTH_POTION.getDisplayName(), 1);

        // Build enemy
        Enemy enemy = ENEMY_MAP.get(enemyLabel).get();

        // Start engine
        isStoryBattle = false;
        engine.startCombat(player, List.of(enemy));

        // Show battle panel
        setupPanel.setVisible(false);
        setupPanel.setManaged(false);
        battlePanel.setVisible(true);
        battlePanel.setManaged(true);

        // Disable dodge for Knight
        btnDodge.setDisable(chosenClass == PlayerClass.KNIGHT);

        // Disable block for Thief (engine will auto-attack, but disable for clarity)
        btnBlock.setDisable(chosenClass == PlayerClass.THIEF);

        setupTooltips(chosenClass);
        updateUI();
        log("=== Battle started: " + player.getName()
                + " [" + chosenClass.getDisplayName() + "] vs " + enemy.getName() + " ===");
        log("Your passive: " + chosenClass.getPassiveDescription());
    }

    public void startStoryBattleWithEnemies(List<String> enemyNames) {
        PlayerState player = GameManager.getInstance().getPlayerState();
        if (player == null) {
            log("ERROR: No player state found!");
            return;
        }

        List<Enemy> enemies = new java.util.ArrayList<>();
        for (String name : enemyNames) {
            // Check the map for exact match or partial match (since map keys have HP)
            Enemy e = null;
            for (String key : ENEMY_MAP.keySet()) {
                if (key.startsWith(name)) {
                    e = ENEMY_MAP.get(key).get();
                    break;
                }
            }
            if (e != null) {
                enemies.add(e);
            }
        }

        if (enemies.isEmpty()) {
            log("ERROR: Could not create any enemies for: " + String.join(", ", enemyNames));
            return;
        }

        isStoryBattle = true;
        engine.startCombat(player, enemies);

        // Show battle panel directly
        setupPanel.setVisible(false);
        setupPanel.setManaged(false);
        battlePanel.setVisible(true);
        battlePanel.setManaged(true);

        PlayerClass chosenClass = player.getPlayerClass();
        btnDodge.setDisable(chosenClass == PlayerClass.KNIGHT);
        btnBlock.setDisable(chosenClass == PlayerClass.THIEF);

        setupTooltips(chosenClass);
        updateUI();
        log("=== Battle started: " + player.getName()
                + " [" + chosenClass.getDisplayName() + "] vs " + enemies.size() + " enemies ===");
        log("Your passive: " + chosenClass.getPassiveDescription());
    }

    // ── Action button handlers ────────────────────────────────────

    @FXML private void onAttack()  { processResultAnimated(engine.playerAttack()); }
    @FXML private void onBlock()   { processResultAnimated(engine.playerBlock()); }
    @FXML private void onDodge()   { processResultAnimated(engine.playerDodge()); }
    @FXML private void onFlee()    { processResultAnimated(engine.playerFlee()); }

    /**
     * Simple item selection: if player has exactly one type of item, use it.
     * If multiple types are available, pops an Alert with a choice dialog.
     */
    @FXML
    private void onUseItem() {
        PlayerState player = engine.getPlayer();
        if (player.getInventory().isEmpty()) {
            log("Inventory is empty — no items to use.");
            return;
        }

        String chosenItem = InventoryController.showInventoryDialog(
                btnUseItem.getScene().getWindow(),
                player,
                true // isCombat
        );

        if (chosenItem != null) {
            processResultAnimated(engine.playerUseItem(chosenItem));
        }
    }

    /** Returns to the setup panel for a new test battle. */
    @FXML
    private void onRestart() {
        if (isStoryBattle) {
            GameManager gm = GameManager.getInstance();
            boolean isWin = (lastOutcome == CombatResult.Outcome.PLAYER_WIN);
            boolean isFlee = (lastOutcome == CombatResult.Outcome.FLED);

            StoryManager.applyCombatResult(gm.getCurrentDay(), isWin, isFlee, gm);

            if (parentController != null) {
                parentController.showStoryProgress(null);
            }
            return;
        }

        // Reset to setup panel
        battlePanel.setVisible(false);
        battlePanel.setManaged(false);
        setupPanel.setVisible(true);
        setupPanel.setManaged(true);
        btnRestart.setVisible(false);
        btnRestart.setManaged(false);
        setActionButtonsDisabled(false);
        log("─── Ready for next battle ───");
    }

    // ── Internal helpers ──────────────────────────────────────────


    /**
     * Animated combat log — now driven by {@link CombatResult.Entry} objects.
     *
     * Each entry carries a text line AND a {@link UIUpdate} hint.  After each
     * PauseTransition fires the line is logged AND the matching widget (enemy
     * HP bar, player HP bar, or turn label) is refreshed immediately, so the
     * player sees the bar change at the same moment they read the line that
     * caused it.
     *
     * Special handling for REFRESH_TURN_LABEL entries whose text starts with
     * "⚔ Enemy Turn": those lines are NOT printed to the log; they only flip
     * the turnLabel so the indicator changes silently between actions.
     */
    private void processResultAnimated(CombatResult result) {
        List<Entry> entries = result.getEntries();

        // Disable buttons immediately to prevent queuing actions during animation
        setActionButtonsDisabled(true);

        // Build a SequentialTransition: one PauseTransition per entry
        SequentialTransition sequence = new SequentialTransition();

        for (Entry entry : entries) {
            PauseTransition pause = new PauseTransition(Duration.millis(LOG_DELAY_MS));
            final Entry captured = entry;
            pause.setOnFinished(e -> {
                // Log the line (skip internal turn-marker entries)
                boolean isTurnMarker = captured.getUIUpdate() == UIUpdate.REFRESH_TURN_LABEL
                        && captured.getText().startsWith("⚔ Enemy Turn");
                if (!isTurnMarker) {
                    log(captured.getText());
                }
                // Apply the targeted UI update
                applyUIUpdate(captured.getUIUpdate());
            });
            sequence.getChildren().add(pause);
        }

        // After all entries: do a final full refresh and handle outcome
        sequence.setOnFinished(e -> {
            updateUI(); // ensure everything is in sync at round end
            lastOutcome = result.getOutcome();
            switch (lastOutcome) {
                case PLAYER_WIN:
                    log("✅ Victory! All enemies defeated.");
                    endCombat();
                    break;
                case PLAYER_LOSE:
                    log("❌ Defeated... Better luck next time.");
                    endCombat();
                    break;
                case FLED:
                    log("You fled. No rewards granted.");
                    endCombat();
                    break;
                case ONGOING:
                    // Re-enable action buttons so the player can take their next turn
                    setActionButtonsDisabled(false);
                    // Re-apply class-based button restrictions
                    restoreClassButtonRestrictions();
                    break;
            }
        });

        sequence.play();
    }

    /**
     * Applies a targeted UI refresh based on the UIUpdate hint attached to a
     * log entry.  Called inside the animation loop so the UI stays in sync
     * with each individual action as it is revealed.
     */
    private void applyUIUpdate(UIUpdate update) {
        if (update == null || update == UIUpdate.NONE) return;
        switch (update) {
            case REFRESH_ENEMY_HP:
                refreshEnemyHP();
                break;
            case REFRESH_PLAYER_HP:
                refreshPlayerHP();
                break;
            case REFRESH_TURN_LABEL:
                refreshTurnLabel(true); // true = enemy's turn
                break;
            case REFRESH_ALL:
                updateUI();
                break;
            default:
                break;
        }
    }

    /**
     * Re-applies the class-specific button restrictions (Knight no dodge,
     * Thief no block) after re-enabling action buttons post-animation.
     * Called only when combat is ONGOING.
     */
    private void restoreClassButtonRestrictions() {
        if (engine.getPlayer() == null) return;
        PlayerClass cls = engine.getPlayer().getPlayerClass();
        btnDodge.setDisable(cls == PlayerClass.KNIGHT);
        btnBlock.setDisable(cls == PlayerClass.THIEF);
    }

    /** Refreshes all HP bars, labels, inventory, and the turn indicator. */
    private void updateUI() {
        refreshEnemyHP();
        refreshPlayerHP();
        refreshTurnLabel(false); // false = player's turn (round just ended)

        // Inventory
        PlayerState player = engine.getPlayer();
        Map<String, Integer> inv = player.getInventory();
        if (inv.isEmpty()) {
            inventoryLabel.setText("Inventory: (empty)");
        } else {
            StringBuilder sb = new StringBuilder("Inventory: ");
            inv.forEach((k, v) -> sb.append(k).append(" ×").append(v).append("  "));
            inventoryLabel.setText(sb.toString().trim());
        }
    }

    /** Refreshes only the enemy HP bar, label, and colour. */
    private void refreshEnemyHP() {
        Enemy enemy = engine.getEnemy();
        if (enemy == null) {
            enemyNameLabel.setText("Cleared");
            enemyHPBar.setProgress(0);
            enemyHPLabel.setText("HP: 0 / 0");
            enemyHPBar.setStyle(hpBarStyle(0));
            return;
        }
        
        int aliveCount = 0;
        for (Enemy e : engine.getEnemies()) {
            if (e.isAlive()) aliveCount++;
        }
        String multiSuffix = aliveCount > 1 ? (" (+" + (aliveCount - 1) + " more)") : "";

        enemyNameLabel.setText(enemy.getName() + (enemy.isBoss() ? " ★" : "") + multiSuffix);
        double pct = (double) enemy.getCurrentHP() / enemy.getMaxHP();
        enemyHPBar.setProgress(Math.max(0, pct));
        enemyHPLabel.setText("HP: " + enemy.getCurrentHP() + " / " + enemy.getMaxHP());
        enemyHPBar.setStyle(hpBarStyle(pct));
    }

    /** Refreshes only the player HP bar, label, and colour. */
    private void refreshPlayerHP() {
        PlayerState player = engine.getPlayer();
        playerNameLabel.setText(player.getName()
                + " [" + player.getPlayerClass().getDisplayName() + "]");
        double pct = (double) player.getCurrentHP() / player.getMaxHP();
        playerHPBar.setProgress(Math.max(0, pct));
        playerHPLabel.setText("HP: " + player.getCurrentHP() + " / " + player.getMaxHP());
        playerHPBar.setStyle(hpBarStyle(pct));
    }

    /**
     * Refreshes the turn indicator label.
     * @param enemyTurn {@code true} while the enemy phase is playing;
     *                  {@code false} once it is back to the player's turn.
     */
    private void refreshTurnLabel(boolean enemyTurn) {
        Enemy enemy = engine.getEnemy();
        if (enemy == null) {
            turnLabel.setText("Victory!");
            return;
        }

        if (enemyTurn) {
            if (enemy.isStunned()) {
                turnLabel.setText("Enemy Turn  [Boss is STUNNED — cannot act]");
            } else {
                turnLabel.setText("Enemy Turn");
            }
        } else {
            // Player's turn — show boss stun info
            if (enemy.isBoss() && !enemy.isStunned()) {
                int turnsToStun = 5 - enemy.getTurnCounter();
                turnLabel.setText("Your Turn  [Boss stuns in " + turnsToStun + " turn(s)]");
            } else if (enemy.isStunned()) {
                turnLabel.setText("Your Turn  [Boss is STUNNED!]");
            } else {
                turnLabel.setText("Your Turn");
            }
        }
    }

    /** Disables action buttons and shows the restart button. */
    private void endCombat() {
        setActionButtonsDisabled(true);
        if (isStoryBattle) {
            btnRestart.setText("Continue Story");
        } else {
            btnRestart.setText("New Battle");
        }
        btnRestart.setVisible(true);
        btnRestart.setManaged(true);
    }

    private void setActionButtonsDisabled(boolean disabled) {
        btnAttack.setDisable(disabled);
        btnBlock.setDisable(disabled);
        btnDodge.setDisable(disabled);
        btnUseItem.setDisable(disabled);
        btnFlee.setDisable(disabled);
    }

    /**
     * Wires up hover listeners for the action buttons to show descriptions
     * in the tooltip label, including class-specific passives.
     */
    private void setupTooltips(PlayerClass cls) {
        // Attack
        String attackDesc = "Basic attack.";
        if (cls == PlayerClass.ARCHER) attackDesc += " Passive: 15% chance to strike twice.";
        setHoverText(btnAttack, attackDesc);

        // Block
        String blockDesc;
        if (cls == PlayerClass.THIEF) blockDesc = "Thieves cannot block.";
        else if (cls == PlayerClass.KNIGHT) blockDesc = "Brace for impact. Reduces incoming damage by 75%. 5% chance to parry.";
        else blockDesc = "Brace for impact. Reduces incoming damage by 50%.";
        setHoverText(btnBlock, blockDesc);

        // Dodge
        String dodgeDesc;
        if (cls == PlayerClass.KNIGHT) dodgeDesc = "Knights cannot dodge.";
        else if (cls == PlayerClass.THIEF) dodgeDesc = "Attempt to dodge. 50% chance to avoid damage and counterattack.";
        else dodgeDesc = "Attempt to dodge. 15% chance to avoid damage and counterattack.";
        setHoverText(btnDodge, dodgeDesc);

        // Use Item
        setHoverText(btnUseItem, "Use a consumable item from your inventory.");

        // Flee
        setHoverText(btnFlee, "Run away from the battle. No rewards granted.");
    }

    private void setHoverText(Button btn, String text) {
        btn.setOnMouseEntered(e -> actionTooltipLabel.setText(text));
        btn.setOnMouseExited(e -> actionTooltipLabel.setText(" "));
    }

    /**
     * HP bar colour for the RPG parchment theme.
     * Always red (matching the reference art), darkening further at critical HP.
     */
    private String hpBarStyle(double pct) {
        if (pct > 0.4)       return "-fx-accent: #CC0000; -fx-control-inner-background: #550000;";
        else if (pct > 0.15) return "-fx-accent: #991100; -fx-control-inner-background: #440000;";
        else                 return "-fx-accent: #660000; -fx-control-inner-background: #330000;";
    }

    /** Forwards a log line to the parent MainGameScreen log area. */
    private void log(String message) {
        if (parentController != null) {
            parentController.logMessage(message);
        }
    }

    /**
     * Called by MainGameScreenController after loading this sub-pane
     * so log output can be forwarded to the bottom TextArea.
     */
    public void setParentController(MainGameScreenController parent) {
        this.parentController = parent;
    }
}
