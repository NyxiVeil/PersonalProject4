package cs2.javafx.model;

public class GameManager {
    private static GameManager instance;

    private PlayerState playerState;
    private int currentDay;
    private boolean isGameOver;
    private boolean isTestMode = true; // Enables developer skip buttons

    // Story flags
    private boolean villagerQuestAccepted = false;
    
    // Save state
    private int currentSaveSlot = -1;

    private GameManager() {
        // Private constructor for singleton
        resetGame();
    }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public void resetGame() {
        playerState = null;
        currentDay = 0;
        isGameOver = false;
        villagerQuestAccepted = false;
        // Do not reset currentSaveSlot here, it persists across deaths so the player can reload
    }

    public void startGame(String playerName, PlayerClass playerClass) {
        this.playerState = new PlayerState(playerName, playerClass);
        this.currentDay = 1; // Advance to day 1 after Day 0 choices
        this.isGameOver = false;
    }

    public PlayerState getPlayerState() {
        return playerState;
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public void advanceDay() {
        currentDay++;
    }

    public void setCurrentDay(int day) {
        this.currentDay = day;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
    }

    public boolean isTestMode() {
        return isTestMode;
    }

    public void setTestMode(boolean testMode) {
        isTestMode = testMode;
    }

    public boolean isVillagerQuestAccepted() {
        return villagerQuestAccepted;
    }

    public void setVillagerQuestAccepted(boolean accepted) {
        villagerQuestAccepted = accepted;
    }

    public int getCurrentSaveSlot() {
        return currentSaveSlot;
    }

    public void setCurrentSaveSlot(int slot) {
        this.currentSaveSlot = slot;
    }

    public void loadFromSaveData(SaveData data) {
        this.playerState = data.getPlayerState();
        this.currentDay = data.getCurrentDay();
        this.villagerQuestAccepted = data.isVillagerQuestAccepted();
        this.isGameOver = false; // reset game over flag just in case
    }
}
