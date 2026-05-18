package cs2.javafx.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class StoryManager {
    private static final Map<Integer, StoryDay> days = new HashMap<>();
    private static final Random random = new Random();

    static {
        // Day 0
        days.put(0, new StoryDay(0, "Day 0: The Awakening",
                "You wake up in Thornhollow. You cannot recall your name.\n\nWho were you in your past life?",
                List.of("Intelligent (Mage)", "Honed focus (Archer)", "Helped those in need (Knight)", "Delinquent (Thief)", "Typical person (Exit)"),
                false, null));

        // Day 1
        days.put(1, new StoryDay(1, "Day 1: The Slime Menace",
                "The village elder asks you to clear out the slimes that have been ruining the crops.\nQuest: Defeat the slimes.",
                List.of("Fight", "Flee"),
                true, "Slime Group"));

        // Day 2
        days.put(2, new StoryDay(2, "Day 2: The King of Slimes",
                "The slimes have formed a massive entity! The King Slime blocks the path to the neighboring town.",
                List.of("Fight", "Flee"),
                true, "King Slime"));

        // Day 3
        days.put(3, new StoryDay(3, "Day 3: The Blacksmith's Offer",
                "A wandering blacksmith offers to upgrade your gear for free. Do you accept?",
                List.of("Yes", "No"),
                false, null));

        // Day 4
        days.put(4, new StoryDay(4, "Day 4: Goblin Ambush",
                "A pack of goblins ambushes you on the road!",
                List.of("Fight", "Flee"),
                true, "Goblin Pack"));

        // Day 5
        days.put(5, new StoryDay(5, "Day 5: The Goblin King",
                "You've tracked the goblins back to their camp. Their leader, the Goblin King, challenges you.",
                List.of("Fight", "Flee"),
                true, "Goblin King"));

        // Day 6
        days.put(6, new StoryDay(6, "Day 6: A Villager's Plea",
                "A villager desperately begs for your help. The graveyard has been overrun by skeletons. Will you help?",
                List.of("Yes", "No"),
                false, null));

        // Day 7
        days.put(7, new StoryDay(7, "Day 7: The Graveyard",
                "You enter the graveyard and are immediately attacked by the undead.",
                List.of("Fight", "Flee"),
                true, "Skeleton Horde"));

        // Day 8
        days.put(8, new StoryDay(8, "Day 8: Colossal Bones",
                "The bones in the graveyard assemble into a Colossal Skeleton!",
                List.of("Fight", "Flee"),
                true, "Colossal Skeleton"));

        // Day 9
        days.put(9, new StoryDay(9, "Day 9: Guild Support",
                "The Adventurer's Guild has heard of your deeds and offers you supplies for the final battle ahead. Accept their help?",
                List.of("Yes", "No"),
                false, null));

        // Day 10
        days.put(10, new StoryDay(10, "Day 10: The Dragon",
                "The sky turns dark as a massive Dragon descends upon the realm. This is your final challenge.",
                List.of("Fight", "Flee"),
                true, "Dragon"));
        
        // Post-game
        days.put(11, new StoryDay(11, "Epilogue",
                "Your journey has come to an end. The realm is safe, and you are a hero.\nYou can now repeat quests from the Quest menu.",
                List.of("Continue"),
                false, null));
    }

    public static StoryDay getDay(int day) {
        return days.getOrDefault(day, new StoryDay(day, "Unknown Day", "Nothing happens.", List.of("Next"), false, null));
    }

    /**
     * Processes non-combat choices and applies rewards/consequences.
     * Returns true if the day should advance immediately.
     */
    public static boolean processChoice(int day, String choice, GameManager gm) {
        PlayerState player = gm.getPlayerState();

        if (day == 0) {
            String name = player == null ? "Unknown" : player.getName();
            if (choice.contains("Mage")) {
                gm.startGame(name, PlayerClass.MAGE);
            } else if (choice.contains("Archer")) {
                gm.startGame(name, PlayerClass.ARCHER);
            } else if (choice.contains("Knight")) {
                gm.startGame(name, PlayerClass.KNIGHT);
            } else if (choice.contains("Thief")) {
                gm.startGame(name, PlayerClass.THIEF);
            } else if (choice.contains("Exit")) {
                System.exit(0);
            }
            return false; // startGame advances the day to 1
        }
        else if (day == 3) {
            if ("Yes".equals(choice)) {
                int roll = random.nextInt(100);
                if (roll < 50) {
                    player.addMaxHP(5);
                    player.addDamage(5);
                } else if (roll < 95) {
                    player.addMaxHP(10);
                    player.addDamage(10);
                } else {
                    player.addMaxHP(15);
                    player.addDamage(15);
                }
            }
            return true;
        }
        else if (day == 6) {
            if ("Yes".equals(choice)) {
                gm.setVillagerQuestAccepted(true);
                return true; // proceed to Day 7
            } else {
                gm.setCurrentDay(9); // Skip to Day 9
                return false;
            }
        }
        else if (day == 9) {
            if ("Yes".equals(choice)) {
                player.addMaxHP(20);
                player.addDamage(20);
            }
            return true;
        }
        else if (day == 10 && "Flee".equals(choice)) {
            int roll = random.nextInt(100);
            if (roll < 50) {
                gm.setGameOver(true);
            } else {
                gm.setCurrentDay(11);
            }
            return false;
        }
        
        return true;
    }

    /**
     * Applies combat results: wins give stats, flees might be death or survival.
     */
    public static void applyCombatResult(int day, boolean isWin, boolean isFlee, GameManager gm) {
        PlayerState player = gm.getPlayerState();
        if (player == null) return;

        if (isWin) {
            switch (day) {
                case 1:
                case 4:
                    player.addMaxHP(5);
                    player.addDamage(5);
                    break;
                case 2:
                case 5:
                case 8:
                    player.addMaxHP(20);
                    player.addDamage(20);
                    break;
                case 7:
                    player.addMaxHP(10);
                    player.addDamage(10);
                    break;
                case 10:
                    // Final boss beaten
                    break;
            }
            gm.advanceDay();
        } else if (isFlee) {
            // Check flee conditions
            switch (day) {
                case 1:
                case 4:
                case 7:
                    // Flee -> failure (maybe retry or game over?)
                    // The doc says "Flee -> failure", "Fail -> death".
                    gm.setGameOver(true);
                    break;
                case 2:
                case 5:
                case 8:
                    // Flee -> survival (just skip the boss)
                    gm.advanceDay();
                    break;
            }
        } else {
            // Loss -> Death
            gm.setGameOver(true);
        }
    }
}
