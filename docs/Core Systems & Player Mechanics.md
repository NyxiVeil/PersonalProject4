1. Core Game Overview
A single‑player, turn‑based RPG with:

Four player classes

Multiple enemy types + bosses

Linear day‑based story progression

Combat actions: attack, block, dodge, flee

Inventory + items

Gear score system

Random events

Difficulty selection

Optional companions (low priority)

UI layout similar to provided mockup

Agent Note:  
All systems must be modular. Each mechanic should be callable independently (e.g., combatEngine(), applyReward(), runStoryDay()).

2. Player Classes
Class Definitions
Each class has base stats, attack speed, and a unique passive.

Class	Base HP	Base Damage	Attack Speed	Special
Mage	100	20	1	10% chance after winning a battle to gain +5 damage
Archer	100	10	1	15% chance to attack twice
Knight	125	10	1	Block chance = 75%, cannot dodge; 5% parry counterattack
Thief	75	5	2	Dodge = 50%, block = 0%


Total stats obtainable through story progression: +115 (HP + Attack).

Agent Note:  
Store class data in a structured object, e.g.:

Code
class PlayerClass {
    int baseHP;
    int baseDamage;
    int attackSpeed;
    Passive passive;
}
3. Player Stats
Damage

Health

Attack Speed

Higher speed attacks first.

If player speed ≥ 2 × enemy speed → player attacks twice.

Agent Note:  
Track stats in a persistent PlayerState object.

4. Combat System
Player Actions
Attack → Deal damage equal to player damage stat.

Block

Base: 50% chance to take half damage

Knight: 75% chance, 5% parry

Dodge

Base: 15% chance to avoid + counterattack

Thief: 50%

Flee → Ends battle, no rewards.

Enemy Turn
Enemy attacks once unless stunned.

Boss Stun Mechanic
After 5 turns, bosses are stunned for 1 turn.

Agent Note:  
Combat loop should follow strict order:

Determine turn owner

Execute action

Apply passives

Check stun

Check death

5. Gear Score System
Formula:

Code
gearScore * dmgMultiplier = finalDamage
Gear score increases from:

Blacksmith upgrades

Story rewards

Random events

Items (optional)

Agent Note:  
Store gearScore separately from base stats.

6. Items & Inventory
Items
Item	Effect
Apple	Heal 5 HP
Health Potion	Heal 25 HP
Life Relic	+1 extra life (appears once)


Inventory stores all items.
Items can be used in combat.

Agent Note:  
Use a simple list or map:
Map<String, Integer> inventory.

7. Currency
Gold used for shops and events.