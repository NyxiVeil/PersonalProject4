package cs2.javafx.model;

import cs2.javafx.controllers.MainGameScreenController;
import java.util.ArrayList;
import java.util.List;

public class QuestManager {
    public static class RepeatableQuest {
        private final String name;
        private final String description;
        private final int unlockedAtDay;
        private final int goldReward;
        private final double dropChance;

        public RepeatableQuest(String name, String description, int unlockedAtDay, int goldReward, double dropChance) {
            this.name = name;
            this.description = description;
            this.unlockedAtDay = unlockedAtDay;
            this.goldReward = goldReward;
            this.dropChance = dropChance;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
        public int getUnlockedAtDay() { return unlockedAtDay; }
        public int getGoldReward() { return goldReward; }
        public double getDropChance() { return dropChance; }
    }

    private static final List<RepeatableQuest> QUESTS = new ArrayList<>();

    static {
        QUESTS.add(new RepeatableQuest("Slime Quest", "Meadow slimes patrol.", 2, 1, 0.05)); // Quest 1: 1 Gold, 5% drop
        QUESTS.add(new RepeatableQuest("Slime Swarm Quest", "Clear slime blockade.", 3, 2, 0.07)); // Quest 2: 2 Gold, 7% drop
        QUESTS.add(new RepeatableQuest("Goblin Quest", "Patrol roads.", 4, 3, 0.10)); // Quest 3: 3 Gold, 10% drop
        QUESTS.add(new RepeatableQuest("Goblin King Quest", "Raid camps.", 6, 4, 0.12)); // Quest 4: 4 Gold, 12% drop
        QUESTS.add(new RepeatableQuest("Skeleton Quest", "Graveyard clean up.", 8, 5, 0.15)); // Quest 5: 5 Gold, 15% drop
        QUESTS.add(new RepeatableQuest("Colossal Skeleton Quest", "Remains containment.", 9, 6, 0.17)); // Quest 6: 6 Gold, 17% drop
        QUESTS.add(new RepeatableQuest("Dragon Quest", "Watch the skies.", 11, 7, 0.20)); // Quest 7: 7 Gold, 20% drop
    }

    private static RepeatableQuest activeQuest = null;
    private static long questStartTime = 0;

    public static List<RepeatableQuest> getUnlockedQuests(int currentDay) {
        List<RepeatableQuest> unlocked = new ArrayList<>();
        for (RepeatableQuest q : QUESTS) {
            if (currentDay >= q.getUnlockedAtDay()) {
                unlocked.add(q);
            }
        }
        return unlocked;
    }

    public static boolean isQuestRunning() {
        if (activeQuest == null) return false;
        // Safe check to auto-complete in case the PauseTransition was lost or interrupted
        if (System.currentTimeMillis() - questStartTime >= 10000) {
            completeActiveQuest();
            return false;
        }
        return true;
    }

    public static String getActiveQuestName() {
        return activeQuest != null ? activeQuest.getName() : null;
    }

    public static double getQuestProgress() {
        if (activeQuest == null) return 0.0;
        long elapsed = System.currentTimeMillis() - questStartTime;
        return Math.min(1.0, (double) elapsed / 10000.0);
    }

    public static void startQuest(String questName) {
        if (activeQuest != null) return;

        for (RepeatableQuest q : QUESTS) {
            if (q.getName().equals(questName)) {
                activeQuest = q;
                break;
            }
        }

        if (activeQuest == null) return;
        questStartTime = System.currentTimeMillis();

        if (MainGameScreenController.getInstance() != null) {
            MainGameScreenController.getInstance().logMessage("⏳ Started Repeatable Quest: " + activeQuest.getName() 
                + " (Reward: " + activeQuest.getGoldReward() + " Gold, Drop Chance: " + (int)(activeQuest.getDropChance() * 100) + "%). Will complete in 10s.");
        }

        // Run background task
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(10));
        pause.setOnFinished(e -> completeActiveQuest());
        pause.play();
    }

    public static void completeActiveQuest() {
        if (activeQuest == null) return;

        RepeatableQuest quest = activeQuest;
        activeQuest = null;
        questStartTime = 0;

        GameManager gm = GameManager.getInstance();
        PlayerState player = gm.getPlayerState();
        if (player == null) return;

        // Gold reward (scaled)
        int goldRewarded = quest.getGoldReward();
        player.addGold(goldRewarded);
        String msg = "💰 Quest Complete [" + quest.getName() + "]! Gained " + goldRewarded + " Gold.";

        // Drop chance (scaled)
        java.util.Random rand = new java.util.Random();
        if (rand.nextDouble() < quest.getDropChance()) {
            String itemDropped = rand.nextBoolean() ? Item.APPLE.getDisplayName() : Item.HEALTH_POTION.getDisplayName();
            boolean added = player.addItem(itemDropped, 1);
            if (added) {
                msg += " Found " + itemDropped + "!";
            } else {
                msg += " Found " + itemDropped + ", but inventory was full!";
            }
        } else {
            msg += " No item dropped.";
        }

        if (MainGameScreenController.getInstance() != null) {
            MainGameScreenController.getInstance().logMessage(msg);
        }
    }
}
