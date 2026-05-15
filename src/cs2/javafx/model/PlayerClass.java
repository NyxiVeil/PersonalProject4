package cs2.javafx.model;

/**
 * The four player classes with their base stats.
 *
 * Class  | HP  | Dmg | Speed | Passive
 * -------|-----|-----|-------|--------------------------------------------------
 * Mage   | 100 | 20  |  1    | 10% chance to gain +5 damage after each victory
 * Archer | 100 | 10  |  1    | 15% chance to attack twice on Attack action
 * Knight | 125 | 10  |  1    | Block = 75%, 5% parry counterattack; cannot dodge
 * Thief  |  75 |  5  |  2    | Dodge = 50%; block chance = 0%
 */
public enum PlayerClass {

    MAGE("Mage", 100, 20, 1,
            "10% chance to gain +5 damage after winning a battle"),

    ARCHER("Archer", 100, 10, 1,
            "15% chance to attack twice per Attack action"),

    KNIGHT("Knight", 125, 10, 1,
            "Block chance 75%, 5% parry counterattack; cannot dodge"),

    THIEF("Thief", 75, 5, 2,
            "Dodge chance 50%; block chance 0%");

    private final String displayName;
    private final int    baseHP;
    private final int    baseDamage;
    private final int    attackSpeed;
    private final String passiveDescription;

    PlayerClass(String displayName, int baseHP, int baseDamage,
                int attackSpeed, String passiveDescription) {
        this.displayName        = displayName;
        this.baseHP             = baseHP;
        this.baseDamage         = baseDamage;
        this.attackSpeed        = attackSpeed;
        this.passiveDescription = passiveDescription;
    }

    public String getDisplayName()        { return displayName; }
    public int    getBaseHP()             { return baseHP; }
    public int    getBaseDamage()         { return baseDamage; }
    public int    getAttackSpeed()        { return attackSpeed; }
    public String getPassiveDescription() { return passiveDescription; }
}
