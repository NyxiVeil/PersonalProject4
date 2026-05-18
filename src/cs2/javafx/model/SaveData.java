package cs2.javafx.model;

import java.io.Serializable;

/**
 * Container for all data that needs to be saved to disk.
 */
public class SaveData implements Serializable {
    private static final long serialVersionUID = 1L;

    private PlayerState playerState;
    private int currentDay;
    private boolean villagerQuestAccepted;
    private long lastSavedTimestamp;

    public SaveData(PlayerState playerState, int currentDay, boolean villagerQuestAccepted) {
        this.playerState = playerState;
        this.currentDay = currentDay;
        this.villagerQuestAccepted = villagerQuestAccepted;
        this.lastSavedTimestamp = System.currentTimeMillis();
    }

    public PlayerState getPlayerState() {
        return playerState;
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public boolean isVillagerQuestAccepted() {
        return villagerQuestAccepted;
    }

    public long getLastSavedTimestamp() {
        return lastSavedTimestamp;
    }
}
