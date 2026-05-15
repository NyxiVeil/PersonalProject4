
---
# **MVP DOCUMENT — TURN‑BASED RPG SYSTEM**

## **1. Core Game Overview**
A single‑player, turn‑based RPG with:
- Four player classes
- Multiple enemy types + bosses
- Linear day‑based story progression
- Basic combat system (attack, block, dodge, flee)
- Inventory + items
- Gear score system
- Random events
- Difficulty selection
- Optional companions (low priority)
- UI layout similar to the provided mockup (battle screen, menus, logs)

---

# **2. Player Classes**

## **Class Definitions**
Each class has base stats, attack speed, and a unique passive.

| Class | Base HP | Base Damage | Attack Speed | Special |
|------|---------|-------------|--------------|---------|
| Mage | 100 | 20 | 1 | 10% chance after winning a battle to gain +5 damage |
| Archer | 100 | 10 | 1 | 15% chance to attack twice |
| Knight | 125 | 10 | 1 | Block chance becomes 75%, cannot dodge; 5% chance to parry and counterattack |
| Thief | 75 | 5 | 2 | Dodge chance becomes 50%, block chance becomes 0% |

**Total stats obtainable through story progression: +115 (health + attack combined).**

---

# **3. Player Stats**
- **Damage**
- **Health**
- **Attack Speed**
  - Higher speed attacks first.
  - If player speed ≥ 2 × enemy speed → player attacks twice per turn.

---

# **4. Combat System**

## **Player Actions**
- **Attack**  
  Deal damage equal to player damage stat.
- **Block**  
  Base: 50% chance to take half damage.  
  Knight: 75% chance, 5% chance to parry (extra attack).
- **Dodge**  
  Base: 15% chance to take no damage + counterattack.  
  Thief: 50% chance.
- **Flee**  
  Ends battle. No rewards.

## **Enemy Turn**
- Enemy attacks once per turn unless stunned.

## **Boss Stun Mechanic**
- After **5 turns**, bosses become **stunned** for 1 turn.

---

# **5. Enemy Types**

## **Normal Enemies**
| Enemy | HP | Damage |
|-------|----|--------|
| Slime | 10 | (default enemy damage) |
| Goblin | 20 | (default enemy damage) |
| Skeleton | 30 | (default enemy damage) |

## **Bosses**
| Boss | HP | Damage | Special |
|------|----|--------|---------|
| King Slime | 70 | 20 | Regenerates 2.5 HP per turn |
| Goblin King | 100 | 20 | — |
| Colossal Skeleton | 200 | 25 | — |
| Dragon | 500 | 50 | Final boss |

---

# **6. Gear Score System**
Formula:
```
gearScore * dmgMultiplier = finalDamage
```
Gear score is influenced by:
- Blacksmith upgrades
- Story rewards
- Random events
- Items (if applicable)

---

# **7. Items & Inventory**

## **Items**
| Item | Effect |
|------|--------|
| Apple | Heal 5 HP |
| Health Potion | Heal 25 HP |
| Life Relic | Grants +1 extra life (appears once) |

Inventory stores all items.  
Player can use items during combat.

---

# **8. Currency**
- **Gold**  
Used for shops and random events.

---

# **9. Random Events**
- **Item Shop** (max 3 items)
- **Side Quest**
- **Blood Moon**  
  - Higher chance of difficult enemies
- **Enemy Mutations** (optional extension)

---

# **10. Difficulty Selection**
Affects:
- Enemy HP
- Enemy damage
- Gold rewards
- Stat rewards

(Exact multipliers can be defined later.)

---

# **11. Story Flow (Day‑Based)**

## **Day 0 — Intro**
- Player wakes in Thornhollow.
- Name input:
  - If blank → player says they cannot recall name.
- Past selection determines class:
  - Intelligent → Mage  
  - Honed focus → Archer  
  - Helped those in need → Knight  
  - Delinquent → Thief  
  - Typical person → Ends game immediately

## **Day 1 — Slimes**
- Quest: Kill 10 slimes (4 battles × 2 slimes).
- Fail → death message.
- Flee → failure message.
- Reward: **+5 HP, +5 Attack**

## **Day 2 — King Slime**
- Boss fight.
- Fail → death message.
- Flee → survival message.
- Win reward: **+20 HP, +20 Attack**

## **Day 3 — Blacksmith Event**
- Free gear upgrade.
- Player chooses Y/N.
- If yes:
  - 50% chance +5 stats  
  - 45% chance +10 stats  
  - 5% chance +15 stats

## **Day 4 — Goblins**
- Quest: Kill 10 goblins (4 battles × 2 goblins).
- Fail → death.
- Flee → failure.
- Reward: **+5 HP, +5 Attack**

## **Day 5 — Goblin King**
- Boss fight.
- Fail → death.
- Flee → survival.
- Win reward: **+20 HP, +20 Attack**

## **Day 6 — Villager Quest**
- Villager asks for help with skeletons.
- If Y → go to Day 7  
- If N → skip to Day 9

## **Day 7 — Skeletons**
- Quest: Kill 10 skeletons (4 battles × 2 skeletons).
- Fail → death.
- Flee → failure.
- Reward: **+10 HP, +10 Attack**

## **Day 8 — Colossal Skeleton**
- Boss fight.
- Fail → death.
- Flee → survival.
- Win reward: **+20 HP, +20 Attack**

## **Day 9 — Guild Support Event**
- Guild offers gear upgrade for dragon fight.
- If Y → +20 HP, +20 Attack  
- If N → no reward

## **Day 10 — Final Day (Dragon)**
- Player chooses to fight or flee.
- If fight → dragon battle.
- If flee:
  - 50% chance death
  - 50% chance survival

---

# **12. Quest System**
After completing a story quest, a repeatable version unlocks in the quest menu:
- Slime quest
- Goblin quest
- Skeleton quest

Rewards:
- XP
- Gold

---

# **13. UI Requirements (Based on Provided Layout)**

## **Left Menu**
- Story Progress
- Battle
- Inventory
- Quest
- Save
- Load
- Exit

## **Center Battle Area**
- Player sprite + HP bar
- Enemy sprite + HP bar
- Turn indicator (“Your turn”)

## **Action Buttons**
- Attack
- Block
- Dodge
- Use Item
- Flee

## **Bottom Log**
- Combat log / chat log

---

# **14. AI Agent Implementation Notes**
The AI agent must be able to:
- Parse class selection and apply stats
- Run turn‑based combat logic
- Handle random chance (dodge, block, parry, double attack, mage bonus)
- Track story progression by day
- Manage inventory and items
- Apply stat rewards
- Handle branching choices (Y/N, flee, etc.)
- Manage boss stun mechanic
- Track gear score and apply multipliers
- Trigger random events
- Save/load game state

---

# **15. Non‑Essential (Low Priority)**
- Companions / NPC party members
- Enemy mutations
- Additional side quests
- Expanded gear system

---