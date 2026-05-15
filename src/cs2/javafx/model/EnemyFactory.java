package cs2.javafx.model;

/**
 * Static factory for creating all enemy instances.
 *
 * Normal enemy damage = 5 (defined as "default enemy damage" in design docs).
 * All enemies have attackSpeed = 1.
 *
 * Boss HP / Damage per design doc:
 *   King Slime       | 70  HP | 20 dmg | regen 2.5/turn
 *   Goblin King      | 100 HP | 20 dmg |
 *   Colossal Skeleton| 200 HP | 25 dmg |
 *   Dragon           | 500 HP | 50 dmg | final boss
 */
public class EnemyFactory {

    private static final int NORMAL_DAMAGE = 5;
    private static final int NORMAL_SPEED  = 1;
    private static final int BOSS_SPEED    = 1;

    // ── Normal Enemies ───────────────────────────────────────────

    public static Enemy createSlime() {
        return new Enemy("Slime", 10, NORMAL_DAMAGE, NORMAL_SPEED, false, 0);
    }

    public static Enemy createGoblin() {
        return new Enemy("Goblin", 20, NORMAL_DAMAGE, NORMAL_SPEED, false, 0);
    }

    public static Enemy createSkeleton() {
        return new Enemy("Skeleton", 30, NORMAL_DAMAGE, NORMAL_SPEED, false, 0);
    }

    // ── Bosses ───────────────────────────────────────────────────

    /** Regenerates 2.5 HP per turn. */
    public static Enemy createKingSlime() {
        return new Enemy("King Slime", 70, 20, BOSS_SPEED, true, 2.5);
    }

    public static Enemy createGoblinKing() {
        return new Enemy("Goblin King", 100, 20, BOSS_SPEED, true, 0);
    }

    public static Enemy createColossalSkeleton() {
        return new Enemy("Colossal Skeleton", 200, 25, BOSS_SPEED, true, 0);
    }

    /** Final boss. */
    public static Enemy createDragon() {
        return new Enemy("Dragon", 500, 50, BOSS_SPEED, true, 0);
    }
}
