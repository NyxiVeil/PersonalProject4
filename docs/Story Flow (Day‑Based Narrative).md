General Structure
Each day is a linear event.
Player state must persist between days.

Agent Note:  
Implement as runDay(int dayNumber) with branching.

Day 0 — Intro
Player wakes in Thornhollow.

Name input: blank → “cannot recall name.”

Past selection determines class:

Intelligent → Mage

Honed focus → Archer

Helped those in need → Knight

Delinquent → Thief

Typical person → Ends game immediately

Day 1 — Slimes
Quest: Kill 10 slimes (4 battles × 2 slimes).
Fail → death.
Flee → failure.
Reward: +5 HP, +5 Attack

Day 2 — King Slime
Boss fight.
Fail → death.
Flee → survival.
Win reward: +20 HP, +20 Attack

Day 3 — Blacksmith Event
Free gear upgrade.
If yes:

50% → +5 stats

45% → +10 stats

5% → +15 stats

Day 4 — Goblins
Quest: Kill 10 goblins.
Fail → death.
Flee → failure.
Reward: +5 HP, +5 Attack

Day 5 — Goblin King
Boss fight.
Fail → death.
Flee → survival.
Reward: +20 HP, +20 Attack

Day 6 — Villager Quest
If yes → Day 7
If no → Day 9

Day 7 — Skeletons
Quest: Kill 10 skeletons.
Fail → death.
Flee → failure.
Reward: +10 HP, +10 Attack

Day 8 — Colossal Skeleton
Boss fight.
Fail → death.
Flee → survival.
Reward: +20 HP, +20 Attack

Day 9 — Guild Support
If yes → +20 HP, +20 Attack
If no → nothing

Day 10 — Final Day
Fight or flee.
If flee:

50% death

50% survival

If fight → dragon battle.

Agent Note:  
Each day should return a result object:

Code
class DayResult {
    boolean survived;
    boolean progressed;
    int nextDay;
}