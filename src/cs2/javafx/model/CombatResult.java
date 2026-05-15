package cs2.javafx.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Value object returned by every CombatEngine action.
 * Contains the combat outcome state and a list of {@link Entry} objects,
 * each pairing a log line with an optional {@link UIUpdate} hint.
 *
 * The UI update hints let BattleController refresh specific widgets
 * (enemy HP bar, player HP bar, turn label) at exactly the right moment
 * in the sequential animation — not just once at the very end.
 */
public class CombatResult {

    // ── Outcome ───────────────────────────────────────────────────

    public enum Outcome {
        ONGOING,      // Combat continues
        PLAYER_WIN,   // Enemy HP reached 0
        PLAYER_LOSE,  // Player HP reached 0 and no extra lives remain
        FLED          // Player chose to flee
    }

    // ── UI update hints ───────────────────────────────────────────

    /**
     * Signals which part of the UI should refresh after a specific log line
     * is displayed. The controller checks this after each PauseTransition fires.
     */
    public enum UIUpdate {
        /** No UI refresh needed after this line. */
        NONE,
        /** Refresh the enemy HP bar and label. */
        REFRESH_ENEMY_HP,
        /** Refresh the player HP bar and label. */
        REFRESH_PLAYER_HP,
        /** Refresh the turn indicator label. */
        REFRESH_TURN_LABEL,
        /** Refresh both HP bars and the turn label (used at round boundaries). */
        REFRESH_ALL
    }

    // ── Log Entry ─────────────────────────────────────────────────

    /** A single log line paired with a UI update hint. */
    public static class Entry {
        private final String   text;
        private final UIUpdate uiUpdate;

        public Entry(String text, UIUpdate uiUpdate) {
            this.text     = text;
            this.uiUpdate = uiUpdate;
        }

        public String   getText()    { return text; }
        public UIUpdate getUIUpdate(){ return uiUpdate; }
    }

    // ── Fields ────────────────────────────────────────────────────

    private final Outcome     outcome;
    private final List<Entry> entries;

    public CombatResult(Outcome outcome, List<Entry> entries) {
        this.outcome = outcome;
        this.entries = Collections.unmodifiableList(new ArrayList<>(entries));
    }

    public Outcome     getOutcome() { return outcome; }
    public List<Entry> getEntries() { return entries; }

    /**
     * Convenience accessor — returns just the raw text of every entry.
     * Kept for compatibility with any code that only needs the lines.
     */
    public List<String> getLogLines() {
        List<String> lines = new ArrayList<>(entries.size());
        for (Entry e : entries) lines.add(e.getText());
        return lines;
    }

    // ── Builder ───────────────────────────────────────────────────

    public static class Builder {
        private Outcome outcome = Outcome.ONGOING;
        private final List<Entry> entries = new ArrayList<>();

        public Builder outcome(Outcome o) { this.outcome = o; return this; }

        /** Log a plain line with no UI update. */
        public Builder log(String line) {
            entries.add(new Entry(line, UIUpdate.NONE));
            return this;
        }

        /** Log a line that should trigger a specific UI refresh after it is shown. */
        public Builder log(String line, UIUpdate update) {
            entries.add(new Entry(line, update));
            return this;
        }

        public CombatResult build() {
            return new CombatResult(outcome, entries);
        }
    }
}
