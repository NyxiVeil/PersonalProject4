package cs2.javafx.model;

import java.util.List;

public class StoryDay {
    private final int dayNumber;
    private final String title;
    private final String description;
    private final List<String> choices;
    private final boolean isBattleDay;
    private final List<String> enemyNames; // For battles

    public StoryDay(int dayNumber, String title, String description, List<String> choices, boolean isBattleDay, List<String> enemyNames) {
        this.dayNumber = dayNumber;
        this.title = title;
        this.description = description;
        this.choices = choices;
        this.isBattleDay = isBattleDay;
        this.enemyNames = enemyNames;
    }

    public int getDayNumber() { return dayNumber; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public List<String> getChoices() { return choices; }
    public boolean isBattleDay() { return isBattleDay; }
    public List<String> getEnemyNames() { return enemyNames; }
}
