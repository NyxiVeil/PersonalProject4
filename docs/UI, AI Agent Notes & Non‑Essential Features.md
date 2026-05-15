1. UI Requirements
Left Menu
Story Progress

Battle

Inventory

Quest

Save

Load

Exit

Center Battle Area
Player sprite + HP bar

Enemy sprite + HP bar

Turn indicator

Action Buttons
Attack

Block

Dodge

Use Item

Flee

Bottom Log
Combat log / chat log

Agent Note:  
UI must call backend functions, not contain logic.

2. AI Agent Implementation Notes
The AI agent must:

Parse class selection

Run turn‑based combat

Handle random chance

Track story progression

Manage inventory

Apply stat rewards

Handle branching choices

Manage boss stun

Track gear score

Trigger random events

Save/load game state

Additional Agent‑Friendly Clarifications Added:

Use a single GameState object to store all persistent data.

All combat actions should be pure functions returning results.

Story days must be deterministic except where randomness is explicitly defined.

Logs should be appended as strings for UI display.

3. Non‑Essential Features
Companions

Enemy mutations

Additional side quests

Expanded gear system