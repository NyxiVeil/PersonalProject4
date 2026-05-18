package cs2.javafx.model;

import java.util.ArrayList;
import java.util.List;

public class QuestManager {
    public static class Quest {
        public String name;
        public String enemyName;
        public int rewardGold;
        
        public Quest(String name, String enemyName, int rewardGold) {
            this.name = name;
            this.enemyName = enemyName;
            this.rewardGold = rewardGold;
        }
    }

    /**
     * Returns a list of quests available based on the current story progress.
     */
    public static List<Quest> getAvailableQuests(GameManager gm) {
        List<Quest> quests = new ArrayList<>();
        int day = gm.getCurrentDay();
        
        if (day > 1) {
            quests.add(new Quest("Slime Extermination", "Slime Group", 10));
        }
        if (day > 4) {
            quests.add(new Quest("Goblin Bounty", "Goblin Pack", 20));
        }
        if (day > 7 && gm.isVillagerQuestAccepted()) {
            quests.add(new Quest("Graveyard Patrol", "Skeleton Horde", 30));
        }
        
        return quests;
    }

    public static void completeQuest(Quest quest, GameManager gm) {
        PlayerState player = gm.getPlayerState();
        if (player != null) {
            player.addGold(quest.rewardGold);
        }
    }
}
