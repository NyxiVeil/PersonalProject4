package cs2.javafx.combat;

import cs2.javafx.model.*;
import cs2.javafx.model.CombatResult.UIUpdate;

import java.util.Random;

/**
 * ─────────────────────────────────────────────────────────────────
 *  CombatEngine — Pure turn-based combat logic. No JavaFX imports.
 * ─────────────────────────────────────────────────────────────────
 *
 * Usage:
 *   CombatEngine engine = new CombatEngine();
 *   engine.startCombat(player, enemy);
 *   CombatResult result = engine.playerAttack(); // or block, dodge, etc.
 *   // check result.getOutcome() and result.getEntries()
 *
 * Turn structure per round:
 *   1. Player action executes (via performOneAttack for attack actions).
 *   2. If player speed >= 2× enemy speed, player performs a second full action.
 *   3. Enemy death is checked → PLAYER_WIN if dead.
 *   4. Enemy acts (unless stunned).
 *   5. Boss stun counter ticks.
 *   6. King Slime regen applies.
 *   7. Player death is checked → PLAYER_LOSE or extra life revive.
 *
 * Speed / double-attack rule:
 *   player.attackSpeed >= 2 * enemy.attackSpeed → player attacks twice (full action each).
 *
 * Block:
 *   Base 50% → half damage. Knight 75% → half damage + 5% parry.
 *   Thief block chance = 0%.
 *
 * Dodge:
 *   Base 15% → no damage + counterattack. Thief 50%.
 *   Knight cannot dodge (engine enforces, UI disables button).
 *
 * UI update hints (UIUpdate enum on each log Entry):
 *   REFRESH_ENEMY_HP   — after player deals damage to enemy
 *   REFRESH_PLAYER_HP  — after enemy deals damage to player
 *   REFRESH_TURN_LABEL — after the turn switches to the enemy phase
 *   REFRESH_ALL        — at round end / revive events
 */
public class CombatEngine {

    private PlayerState player;
    private Enemy       enemy;
    private final Random random = new Random();

    // ── Setup ─────────────────────────────────────────────────────

    /** Call before the first player action of a new encounter. */
    public void startCombat(PlayerState player, Enemy enemy) {
        this.player = player;
        this.enemy  = enemy;
    }

    // ── Public player action methods ─────────────────────────────

    /**
     * Player attacks the enemy.
     *
     * FIX 3 — Speed as extra turn:
     *   Each attack is now a full independent action via performOneAttack().
     *   If player.attackSpeed >= 2 * enemy.attackSpeed, performOneAttack() is
     *   called a second time, giving the player a true second action (with its
     *   own Archer passive roll) rather than just adding raw damage.
     *
     * FIX 2 — Archer passive guard:
     *   The passive check inside performOneAttack() now guards with
     *   enemy.isAlive(), preventing a strike on a corpse.
     */
    public CombatResult playerAttack() {
        CombatResult.Builder result = new CombatResult.Builder();

        // First action
        performOneAttack(result);

        // Speed double-attack: player speed >= 2× enemy speed gets a full second action
        if (player.getAttackSpeed() >= 2 * enemy.getAttackSpeed()) {
            result.log("⚡ Double speed — " + player.getName() + " acts again!");
            performOneAttack(result);
        }

        if (!enemy.isAlive()) return resolveEnemyDeath(result);
        return resolveEnemyTurn(result);
    }

    /**
     * Player blocks. Enemy attacks this turn with a chance to reduce damage.
     *
     * Block chances:  Thief = 0%,  Knight = 75%,  others = 50%.
     * Parry (Knight): 5% chance on top of a successful block → counterattack for 0 incoming damage.
     */
    public CombatResult playerBlock() {
        CombatResult.Builder result = new CombatResult.Builder();
        PlayerClass cls = player.getPlayerClass();

        // Thief cannot block
        if (cls == PlayerClass.THIEF) {
            result.log("Thieves cannot block! Attacking instead...");
            return playerAttack();
        }

        double blockChance = (cls == PlayerClass.KNIGHT) ? 0.75 : 0.50;
        boolean blocked    = random.nextDouble() < blockChance;
        boolean parried    = (cls == PlayerClass.KNIGHT) && blocked && (random.nextDouble() < 0.05);

        result.log("🛡 " + player.getName() + " braces for impact...");

        // Signal the UI that the enemy phase is starting
        result.log("⚔ Enemy Turn", UIUpdate.REFRESH_TURN_LABEL);

        // --- Enemy attacks ---
        tickBossCounter(result); // tick before resolving the attack

        if (enemy.isStunned()) {
            result.log("💫 " + enemy.getName() + " is stunned and cannot attack!");
        } else {
            int rawDmg    = enemy.getDamage();
            int actualDmg;

            if (parried) {
                actualDmg = 0;
                result.log("⚔ Parry! Knight deflects and counterattacks for "
                        + player.getFinalDamage() + " damage!");
                dealDamageToEnemy(player.getFinalDamage(), result);
            } else if (blocked) {
                actualDmg = rawDmg / 2;
                result.log("Block! Damage reduced: " + rawDmg + " → " + actualDmg + ".");
            } else {
                actualDmg = rawDmg;
                result.log("Block failed — taking full " + rawDmg + " damage.");
            }

            if (actualDmg > 0) {
                player.takeDamage(actualDmg);
                result.log(player.getName() + " HP: " + player.getCurrentHP()
                        + "/" + player.getMaxHP(), UIUpdate.REFRESH_PLAYER_HP);
            }
        }

        applyBossRegen(result);

        if (parried && !enemy.isAlive()) return resolveEnemyDeath(result);
        if (!player.isAlive())          return resolvePlayerDeath(result);
        return result.outcome(CombatResult.Outcome.ONGOING).build();
    }

    /**
     * Player dodges.
     * Dodge chances:  Knight = 0% (cannot dodge),  Thief = 50%,  others = 15%.
     * On success:     full damage avoided + counterattack.
     * On failure:     full enemy damage taken — UNLESS the boss is stunned.
     *
     * FIX 4 — Stun ordering:
     *   tickBossCounter() is now called BEFORE the enemy attack block so that
     *   the stun flag is evaluated correctly on every dodge outcome, including
     *   dodge-fail. Previously the tick happened after the attack, meaning a
     *   stunned boss could still land a hit on a dodge-fail turn.
     */
    public CombatResult playerDodge() {
        CombatResult.Builder result = new CombatResult.Builder();
        PlayerClass cls = player.getPlayerClass();

        if (cls == PlayerClass.KNIGHT) {
            result.log("Knights cannot dodge!");
            return result.outcome(CombatResult.Outcome.ONGOING).build();
        }

        double dodgeChance = (cls == PlayerClass.THIEF) ? 0.50 : 0.15;
        boolean dodged     = random.nextDouble() < dodgeChance;

        result.log("💨 " + player.getName() + " attempts to dodge...");

        if (dodged) {
            result.log("Dodge successful! Counterattacking for " + player.getFinalDamage() + "!");
            dealDamageToEnemy(player.getFinalDamage(), result);
            if (!enemy.isAlive()) return resolveEnemyDeath(result);
        }

        // Signal the UI that the enemy phase is starting
        result.log("⚔ Enemy Turn", UIUpdate.REFRESH_TURN_LABEL);

        // FIX 4: Tick the boss stun counter BEFORE applying the enemy attack.
        tickBossCounter(result);

        if (!dodged) {
            // Dodge failed — only let the enemy attack if it is NOT stunned
            if (enemy.isStunned()) {
                result.log("💫 " + enemy.getName() + " is stunned and cannot attack!");
            } else {
                result.log("Dodge failed!");
                int dmg = enemy.getDamage();
                player.takeDamage(dmg);
                result.log(enemy.getName() + " deals " + dmg + " damage to " + player.getName() + ".");
                result.log(player.getName() + " HP: " + player.getCurrentHP() + "/" + player.getMaxHP(),
                        UIUpdate.REFRESH_PLAYER_HP);
                if (!player.isAlive()) return resolvePlayerDeath(result);
            }
        }

        applyBossRegen(result);
        return result.outcome(CombatResult.Outcome.ONGOING).build();
    }

    /**
     * Player uses a named item from inventory.
     * Enemy still acts after item use.
     * @param itemName display name of the item (e.g. "Apple")
     */
    public CombatResult playerUseItem(String itemName) {
        CombatResult.Builder result = new CombatResult.Builder();

        if (!player.hasItem(itemName)) {
            result.log("You have no " + itemName + " in your inventory.");
            return result.outcome(CombatResult.Outcome.ONGOING).build();
        }

        Item item = Item.fromDisplayName(itemName);
        if (item == null) {
            result.log("Unknown item: " + itemName);
            return result.outcome(CombatResult.Outcome.ONGOING).build();
        }

        player.removeItem(itemName);

        switch (item) {
            case APPLE:
                player.heal(item.getHealAmount());
                result.log("🍎 Ate an Apple — restored 5 HP. HP: "
                        + player.getCurrentHP() + "/" + player.getMaxHP(),
                        UIUpdate.REFRESH_PLAYER_HP);
                break;
            case HEALTH_POTION:
                player.heal(item.getHealAmount());
                result.log("🧪 Drank a Health Potion — restored 25 HP. HP: "
                        + player.getCurrentHP() + "/" + player.getMaxHP(),
                        UIUpdate.REFRESH_PLAYER_HP);
                break;
            case LIFE_RELIC:
                player.addExtraLife();
                result.log("💎 Life Relic absorbed — gained 1 extra life!");
                break;
        }

        // Enemy still gets their turn after item use
        return resolveEnemyTurn(result);
    }

    /**
     * Player flees the battle. Combat ends immediately. No rewards granted.
     */
    public CombatResult playerFlee() {
        CombatResult.Builder result = new CombatResult.Builder();
        result.log("🚪 " + player.getName() + " flees the battle. No rewards.");
        return result.outcome(CombatResult.Outcome.FLED).build();
    }

    // ── Accessors for UI binding ──────────────────────────────────

    public PlayerState getPlayer() { return player; }
    public Enemy       getEnemy()  { return enemy; }

    // ── Internal helpers ──────────────────────────────────────────

    /**
     * Executes one full attack action for the player.
     *
     * FIX 2 — Archer passive guard:
     *   The 15% second-strike check now includes enemy.isAlive() so the passive
     *   never prints or deals damage after the enemy has already been killed by
     *   the first hit.
     *
     * FIX 3 — Called by playerAttack() twice when speed qualifies.
     *   This makes the speed bonus a true second action (with its own passive
     *   roll) rather than a bare extra damage call.
     */
    private void performOneAttack(CombatResult.Builder result) {
        int dmg = player.getFinalDamage();
        dealDamageToEnemy(dmg, result);

        // Archer passive: 15% chance to strike again — only if the enemy survived
        if (player.getPlayerClass() == PlayerClass.ARCHER
                && enemy.isAlive()                        // FIX 2: guard against dead enemy
                && random.nextDouble() < 0.15) {
            result.log("✦ Archer passive — strikes again!");
            dealDamageToEnemy(dmg, result);
        }
    }

    /**
     * Applies the player's finalDamage to the enemy and logs the result.
     * Tags the line with REFRESH_ENEMY_HP so the enemy bar updates immediately.
     */
    private void dealDamageToEnemy(int dmg, CombatResult.Builder result) {
        enemy.takeDamage(dmg);
        result.log(player.getName() + " deals " + dmg + " to " + enemy.getName()
                + ". [" + enemy.getCurrentHP() + "/" + enemy.getMaxHP() + " HP]",
                UIUpdate.REFRESH_ENEMY_HP);
    }

    /**
     * Resolves the enemy's standard attack turn.
     * Called at the end of Attack, Dodge-success, and UseItem flows.
     * Emits a REFRESH_TURN_LABEL hint before the enemy acts so the turn
     * indicator switches as soon as the enemy phase begins.
     */
    private CombatResult resolveEnemyTurn(CombatResult.Builder result) {
        // Signal the UI: switch turn indicator to "Enemy Turn" before enemy acts
        result.log("⚔ Enemy Turn", UIUpdate.REFRESH_TURN_LABEL);

        tickBossCounter(result);

        if (enemy.isStunned()) {
            result.log("💫 " + enemy.getName() + " is stunned — cannot act!");
        } else {
            int dmg = enemy.getDamage();
            player.takeDamage(dmg);
            result.log(enemy.getName() + " attacks " + player.getName()
                    + " for " + dmg + " damage!");
            result.log(player.getName() + " HP: " + player.getCurrentHP()
                    + "/" + player.getMaxHP(), UIUpdate.REFRESH_PLAYER_HP);
        }

        applyBossRegen(result);

        if (!player.isAlive()) return resolvePlayerDeath(result);
        return result.outcome(CombatResult.Outcome.ONGOING).build();
    }

    /**
     * Ticks the boss stun counter once per round.
     * Logs transitions: "stunned!" and "recovers from stun".
     */
    private void tickBossCounter(CombatResult.Builder result) {
        if (!enemy.isBoss()) return;
        boolean wasStunned = enemy.isStunned();
        enemy.tickTurn();
        if (!wasStunned && enemy.isStunned()) {
            result.log("⚡ " + enemy.getName() + " is overwhelmed — stunned for 1 turn!",
                    UIUpdate.REFRESH_TURN_LABEL);
        } else if (wasStunned && !enemy.isStunned()) {
            result.log(enemy.getName() + " shakes off the stun.", UIUpdate.REFRESH_TURN_LABEL);
        }
    }

    /** Applies King Slime regen (and any future regen enemies). */
    private void applyBossRegen(CombatResult.Builder result) {
        if (enemy.getRegenPerTurn() > 0 && enemy.isAlive()) {
            enemy.regen();
            result.log("♻ " + enemy.getName() + " regenerates — HP: "
                    + enemy.getCurrentHP() + "/" + enemy.getMaxHP(),
                    UIUpdate.REFRESH_ENEMY_HP);
        }
    }

    /** Called when enemy HP drops to 0. Applies Mage's post-victory passive. */
    private CombatResult resolveEnemyDeath(CombatResult.Builder result) {
        result.log("💀 " + enemy.getName() + " has been defeated!", UIUpdate.REFRESH_ENEMY_HP);

        // Mage passive: 10% chance to gain +5 damage after a win
        if (player.getPlayerClass() == PlayerClass.MAGE && random.nextDouble() < 0.10) {
            player.addDamage(5);
            result.log("✦ Mage passive — damage increased by 5! (Now: " + player.getDamage() + ")");
        }

        return result.outcome(CombatResult.Outcome.PLAYER_WIN).build();
    }

    /**
     * Called when player HP drops to 0.
     * If the player has a Life Relic extra life, consume it and revive.
     */
    private CombatResult resolvePlayerDeath(CombatResult.Builder result) {
        if (player.getExtraLives() > 0) {
            player.useExtraLife();
            result.log("💎 Extra life triggered! " + player.getName()
                    + " is revived at full HP. HP: "
                    + player.getCurrentHP() + "/" + player.getMaxHP(),
                    UIUpdate.REFRESH_ALL);
            return result.outcome(CombatResult.Outcome.ONGOING).build();
        }
        result.log("💀 " + player.getName() + " has fallen in battle...", UIUpdate.REFRESH_PLAYER_HP);
        return result.outcome(CombatResult.Outcome.PLAYER_LOSE).build();
    }
}
