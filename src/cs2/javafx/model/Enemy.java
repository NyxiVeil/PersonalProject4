package cs2.javafx.model;

/**
 * Represents a single enemy instance during combat.
 *
 * Boss stun mechanic:
 *   - Call tickTurn() once per round.
 *   - After 5 turns, isStunned() becomes true for exactly 1 turn.
 *   - On the next tick after being stunned, stun clears and counter resets.
 *
 * King Slime regen:
 *   - regenPerTurn = 2.5; stored as double to support fractional regen.
 *   - getCurrentHP() floors the double for display/damage checks.
 */
public class Enemy {

    private final String name;
    private final int    maxHP;
    private double       currentHP;      // double supports 2.5 regen
    private final int    damage;
    private final int    attackSpeed;
    private final boolean isBoss;
    private final double regenPerTurn;  // 0 for non-regen enemies

    // Boss stun tracking
    private int     turnCounter; // rounds elapsed in this boss fight
    private boolean stunned;

    public Enemy(String name, int maxHP, int damage, int attackSpeed,
                 boolean isBoss, double regenPerTurn) {
        this.name         = name;
        this.maxHP        = maxHP;
        this.currentHP    = maxHP;
        this.damage       = damage;
        this.attackSpeed  = attackSpeed;
        this.isBoss       = isBoss;
        this.regenPerTurn = regenPerTurn;
        this.turnCounter  = 0;
        this.stunned      = false;
    }

    // ── HP ──────────────────────────────────────────────────────
    public int    getCurrentHP()    { return (int) currentHP; }
    public double getCurrentHPRaw() { return currentHP; }
    public int    getMaxHP()        { return maxHP; }

    public void takeDamage(int amount) {
        currentHP = Math.max(0, currentHP - amount);
    }

    /** King Slime regenerates 2.5 HP per turn, capped at maxHP. */
    public void regen() {
        if (regenPerTurn > 0) {
            currentHP = Math.min(maxHP, currentHP + regenPerTurn);
        }
    }

    public boolean isAlive() { return currentHP > 0; }

    // ── Stats ────────────────────────────────────────────────────
    public String  getName()         { return name; }
    public int     getDamage()       { return damage; }
    public int     getAttackSpeed()  { return attackSpeed; }
    public boolean isBoss()          { return isBoss; }
    public double  getRegenPerTurn() { return regenPerTurn; }

    // ── Boss stun mechanic ───────────────────────────────────────
    /**
     * Call once per combat round (after the player acts).
     * Manages the 5-turn stun cycle for bosses.
     */
    public void tickTurn() {
        if (!isBoss) return;

        if (stunned) {
            // Was stunned last turn — now recover and reset counter
            stunned      = false;
            turnCounter  = 0;
        } else {
            turnCounter++;
            if (turnCounter >= 5) {
                stunned = true;
            }
        }
    }

    public boolean isStunned()    { return stunned; }
    public int     getTurnCounter() { return turnCounter; }
}
