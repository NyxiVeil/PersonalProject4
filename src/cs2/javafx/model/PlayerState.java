package cs2.javafx.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Persistent container for all player data that must survive between combats.
 *
 * Damage formula: finalDamage = (int)(damage * gearScore)
 * gearScore defaults to 1.0 (no multiplier until upgrades are added).
 *
 * Inventory is stored as: item display name → quantity.
 */
public class PlayerState {

    private String      name;
    private PlayerClass playerClass;
    private int         maxHP;
    private int         currentHP;
    private int         damage;
    private int         attackSpeed;
    private double      gearScore;   // default 1.0
    private int         gold;
    private int         extraLives;

    /** item display name → quantity */
    private final Map<String, Integer> inventory;

    // ── Constructor ──────────────────────────────────────────────
    public PlayerState(String name, PlayerClass playerClass) {
        this.name        = name;
        this.playerClass = playerClass;
        this.maxHP       = playerClass.getBaseHP();
        this.currentHP   = maxHP;
        this.damage      = playerClass.getBaseDamage();
        this.attackSpeed = playerClass.getAttackSpeed();
        this.gearScore   = 1.0;
        this.gold        = 0;
        this.extraLives  = 0;
        this.inventory   = new HashMap<>();
    }

    // ── HP ───────────────────────────────────────────────────────
    public int  getCurrentHP() { return currentHP; }
    public int  getMaxHP()     { return maxHP; }
    public boolean isAlive()   { return currentHP > 0; }

    public void heal(int amount)       { currentHP = Math.min(currentHP + amount, maxHP); }
    public void takeDamage(int amount) { currentHP = Math.max(0, currentHP - amount); }
    public void addMaxHP(int amount)   { maxHP += amount; heal(0); } // keep current in bounds

    // ── Damage / Gear ────────────────────────────────────────────
    public int    getDamage()      { return damage; }
    public void   addDamage(int n) { damage += n; }
    public double getGearScore()   { return gearScore; }
    public void   setGearScore(double gs) { gearScore = gs; }

    /** Applies the gear score multiplier: damage * gearScore. */
    public int getFinalDamage() { return (int)(damage * gearScore); }

    // ── Speed / Class ────────────────────────────────────────────
    public int         getAttackSpeed() { return attackSpeed; }
    public PlayerClass getPlayerClass() { return playerClass; }
    public String      getName()        { return name; }
    public void        setName(String n){ name = n; }

    // ── Gold ─────────────────────────────────────────────────────
    public int  getGold()          { return gold; }
    public void addGold(int amount){ gold += amount; }

    // ── Extra Lives (Life Relic) ─────────────────────────────────
    public int  getExtraLives() { return extraLives; }
    public void addExtraLife()  { extraLives++; }

    /**
     * Consumes one extra life and fully restores HP.
     * @return true if a life was available and used
     */
    public boolean useExtraLife() {
        if (extraLives <= 0) return false;
        extraLives--;
        currentHP = maxHP;
        return true;
    }

    // ── Inventory ────────────────────────────────────────────────
    public Map<String, Integer> getInventory() { return inventory; }

    public void addItem(String itemName, int qty) {
        inventory.merge(itemName, qty, Integer::sum);
    }

    public boolean hasItem(String itemName) {
        return inventory.getOrDefault(itemName, 0) > 0;
    }

    /** Removes one unit of the item. Returns false if not present. */
    public boolean removeItem(String itemName) {
        int qty = inventory.getOrDefault(itemName, 0);
        if (qty <= 0) return false;
        if (qty == 1) inventory.remove(itemName);
        else          inventory.put(itemName, qty - 1);
        return true;
    }
}
