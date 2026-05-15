1. Enemy Types
Normal Enemies
Enemy	HP	Damage
Slime	10	default enemy damage
Goblin	20	default enemy damage
Skeleton	30	default enemy damage


Bosses
Boss	HP	Damage	Special
King Slime	70	20	Regenerates 2.5 HP/turn
Goblin King	100	20	—
Colossal Skeleton	200	25	—
Dragon	500	50	Final boss


Agent Note:  
Bosses must track:

Turn counter

Stun state

Special abilities

2. Random Events
Item Shop (max 3 items)

Side Quest

Blood Moon → higher chance of difficult enemies

Enemy Mutations (optional)

Agent Note:  
Random events should be triggered by:

Story days

Exploration

Random chance

3. Difficulty Selection
Affects:

Enemy HP

Enemy damage

Gold rewards

Stat rewards

Multipliers can be added later.

Agent Note:  
Store difficulty as an enum with multipliers.

4. Quest System
After completing a story quest, a repeatable version unlocks:

Slime quest

Goblin quest

Skeleton quest

Rewards:

XP

Gold

Agent Note:  
Quests should be stored as objects with:

Requirements

Enemy type

Reward function