package cs2.javafx.model;

/**
 * All usable items with their display names and heal amounts.
 *
 * Item         | Effect
 * Apple        | Heal 5 HP
 * Health Potion| Heal 25 HP
 * Life Relic   | Grants +1 extra life (special — no heal amount, handled separately)
 */
public enum Item {

    APPLE("Apple", 5),
    HEALTH_POTION("Health Potion", 25),
    LIFE_RELIC("Life Relic", 0); // Special: grants an extra life, not a direct heal

    private final String displayName;
    private final int healAmount;

    Item(String displayName, int healAmount) {
        this.displayName = displayName;
        this.healAmount  = healAmount;
    }

    public String getDisplayName() { return displayName; }
    public int    getHealAmount()  { return healAmount; }

    /** Look up an Item by its display name. Returns null if not found. */
    public static Item fromDisplayName(String name) {
        for (Item item : values()) {
            if (item.displayName.equalsIgnoreCase(name)) return item;
        }
        return null;
    }
}
