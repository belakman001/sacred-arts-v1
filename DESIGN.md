# Sacred Arts — Design Document (v1 draft, 2026-10-21)

A Cradle-inspired cultivation mod for Minecraft 1.20.1 (Forge). Format modeled on
**The Awakening** mod: crisis/trigger enrollment → themed power kit → stamina-gated
abilities → dual progression (power mastery + character stats) → in-game goals menu.

> **IP posture:** Unofficial fan work. "Cradle" appears nowhere in the project title,
> logo, or textures. Description carries: "Unofficial fan work inspired by the
> Cradle series by Will Wight. Not affiliated with or endorsed by Will Wight or
> Hidden Gnome Publishing." No book text, no official art — all original assets.

## Core loop

1. **Enroll:** At Foundation, use a **Foundation Manual** (craftable: book + copper).
   The Path chooses you — one is assigned at random from the launch pool, weighted
   evenly. (Mirrors The Awakening's "the power chooses you.")
2. **Cycle & fight:** Abilities cost **madra** (no cooldowns — if you can pay, you can
   cast). Hold the **Cycle key** to draw in vital aura and refill madra faster.
3. **Master your Path:** Fighting with techniques raises **Path Mastery**, unlocking
   and strengthening techniques.
4. **Advance:** Hit the mastery threshold, then complete an **advancement trial**
   (mob-kill quota or mini-boss) to rise to the next stage.
5. **Reroll:** A craftable **Remnant Core** lets you abandon your Path and be
   re-assigned (The Awakening's Catalyst equivalent).

## Madra

- Every player has a **madra pool** (max grows each stage) shown as a HUD bar.
- Passive regen is slow; **cycling** (sneak + Cycle key, standing still) refills fast
  but leaves you vulnerable — the core risk/reward beat.
- All numbers data-driven via server config.

## Advancement ladder

| Stage | Trial to advance | Unlock |
|---|---|---|
| Foundation | — (start) | Madra pool, Path assignment |
| Copper | Cycle 500 madra total | **Copper Sight** — living entities glow through walls (short range) |
| Iron | Mastery 25 + defeat 20 hostile mobs | **Choose an Iron Body** (permanent, 1 of 3): Bloodforged (regen), Raindrop (speed/dodge), Oakheart (+max health) |
| Jade | Mastery 50 + cycle 2,500 madra | Perfect cycling (2× regen), spiritual sense (longer radar) |
| Lowgold | Defeat a Gold-rank Remnant | Adopt the Remnant → **Goldsign** (Path-themed cosmetic + small perk) |
| Highgold | Mastery 100 + advancement trial | Technique power tier II |
| Truegold | Defeat the Remnant Tyrant | Technique power tier III, prestige title |
| Underlord → Monarch | **v2** | Soulfire, revelations, Icons/Authority (Sage), body-spirit merge (Herald), Monarch |

v1 MVP caps playable advancement at **Truegold**; the Lord realm and beyond ship in v2
(data structures built to support them from day one).

## Launch Paths (6)

Each Path grants 4 techniques (one per type) + an ultimate at high mastery.

1. **Path of Black Flame** — fire + destruction. Goldsign: draconic traits.
   - Striker: *Dragon's Breath* — piercing beam that withers as much as burns
   - Enforcer: *Burning Cloak* — speed + fire aura that ignites attackers
   - Ruler: *Void Dragon's Dance* — ring of hungry flame around the caster
   - Forger: *Cinder Claws* — forged claws, bonus melee damage
   - Ultimate: *The Dragon Descends* — colossal forged dragon strike
2. **Path of Twin Stars** — pure. Goldsign: starfield eyes.
   - Striker: *Empty Palm* — palm strike that weakens/slows (disrupts madra)
   - Enforcer: *Soul Cloak* — heightened reflexes, brief dodge chance
   - Ruler: *Hollow Domain* — field that suppresses enemy abilities
   - Forger: *Twin Barrier* — orbiting pure-madra shields
   - Ultimate: *Twin Stars Collision* — dual-core detonation
3. **Path of the Endless Sword** — sword. Goldsign: silver-streaked hair.
   - Striker: *Rippling Sword* — crescent wave of sword madra
   - Enforcer: *Flowing Sword* — flowing footwork, attack speed up
   - Ruler: *Sword Aura Field* — zone of cutting intent (damage over time)
   - Forger: *Hidden Sword* — invisible blade, next hits crit
   - Ultimate: *Final Sword* — single devastating slash
4. **Path of the Hollow King** — pure. Goldsign: faint crown shimmer.
   - Striker: *Hollow Bolt* — pure madra projectile, bonus vs. "magical" mobs
   - Enforcer: *Hollow King's Armor* — nullifies incoming ability damage
   - Ruler: *Still Air* — calms a zone, slowing projectiles/mobs
   - Forger: *Hollow King's Crown* — forged spirit-stars, homing projectiles
   - Ultimate: *Hollow King Manifest* — full nullification field + starfall
5. **Path of the White Fox** — light + dream (illusion). Goldsign: fox ears/tail shimmer.
   - Striker: *Fox Fire* — illusory flames that terrify (fear effect, low damage)
   - Enforcer: *Fox Tail* — desync: brief untargetability
   - Ruler: *Fox Dream* — confuses mobs in an area (they fight each other)
   - Forger: *Fox Mirror* — illusory double that taunts enemies
   - Ultimate: *Grand Illusion* — battlefield-wide phantasm
6. **Path of the Sandviper** — poison. Goldsign: serpent-marked arm.
   - Striker: *Venom Spit* — arcing poison glob
   - Enforcer: *Serpent's Swiftness* — speed + poison immunity
   - Ruler: *Miasma Cloud* — lingering toxic zone
   - Forger: *Venom Needles* — fan of forged poison darts
   - Ultimate: *Venom Tide* — wave of corrosive venom

Future Paths (v2+): Grasping Sky, Stellar Spear, Dawn Oath, Frozen Blade, Heart of
Twin Stars as an advanced dual-core mechanic.

## Iconic weapons & items

| Item | Acquisition | Effect |
|---|---|---|
| **Penance** | Dreadgod drop (v2), single use | One strike kills *anything* (config-toggleable) |
| **Folding Scissors** | Mid-game craft/quest | Shapeshifting dagger, damage scales with stage |
| **Suriel's Razor** | Late-game | Transforming blade: sealed (melee) / unsealed (extended reach + healing on kill) |
| **Ozriel's Scythe** | Endgame (v2) | Corruption-cleaving, massive AoE |
| **Eclipse** | Quest chain | Cane ↔ shadow bow; bow fires forged shadow arrows; sneak-use glides |
| **Wintersteel Blade** | Smithing | High-tier sword, bonus vs. armored |
| **Hunger Arm** | Remnant Tyrant drop (offhand) | Melee hits drain life + madra |
| **Dross** | Quest companion | Floating companion: highlights ores/danger, small combat buffs |
| **Thousand-Mile Cloud** | Late craft | Flying mount (slow, fragile) |
| **Eight-Man Empire Armor** | Dungeon loot | 4-piece set bonus: technique cost reduction |
| **Scripted Warhammer** | Craft | Huge slow hammer with rune shockwave |

## Mobs & bosses

- **Wild Remnants** (tiered: Copper/Iron/Jade/Gold) — ambient madra spirits; Gold-rank
  Remnant required for the Lowgold trial.
- **Remnant Tyrant** — v1 final boss gating Truegold.
- **The four Dreadgods** (Bleeding Phoenix, Weeping Dragon, Silent King, Wandering
  Titan) — v2 endgame raid bosses.
- Elites: hungry remnants, Dreadgod cultists (ambush squads).

## Structures

- **Ruined shrines** (loot: manuals, pills), **Ninecloud-style vault** (rare loot),
  **Labyrinth entrance** (v2 dungeon).

## Guide & UI

- **Sacred Arts menu** (keybind, like The Awakening's Main Menu): Path info,
  advancement progress bar, goals & rotating trials with HUD tracker.
- HUD: madra bar, technique icons with madra costs, advancement toast on stage-up.
- Keybinds: 4 technique slots + Cycle key + menu key. Fully rebindable.

## Items (consumables/systems)

- Foundation Manual (enroll), Remnant Core (reroll Path), Madra Pills (restore madra /
  boost cycling), Iron Body Reset? — **no** (Iron Body is permanent by design).

## Multiplayer

- Teams / friendly-fire toggle, optional unique-Path-per-player limit (server config).

## Build status (2026-10-21)

MVP core systems implemented and committed (35 Java files, zero syntax errors):
- Player capability: stage, Path, mastery, madra pool, Iron Body, Goldsign, cycling
- Madra: passive regen + hold-to-cycle, HUD bar overlay
- 6 Paths x 4 techniques + ultimate (30 total), keybind cast (V/B/N/M, sneak+M = ultimate)
- Advancement Foundation -> Truegold with trials; Copper Sight, Iron Body choice screen, Goldsign
- Items: Foundation Manual (opens Path selection menu — choose your Path, consumes the manual), Remnant Core (Path switch), Madra Pill
- Sacred Arts menu (K): Path, stage, madra/mastery/kills, Iron Body, Goldsign, advancement requirement, technique list with lock states
- Server→client data sync (login, on change, every 2s); item textures via vanilla placeholders; /sacredarts status|enroll debug commands
- Recipes, full en_us lang, server config, GitHub Actions cloud build
- Not yet in: Remnant mobs / Tyrant boss, Dreadgods, iconic weapons, Dross, Thousand-Mile Cloud,
  structures, in-game guide menu, Curios trinket integration

Next milestones: (1) playtest v0.2 (Path menu, stats menu, sync) and fix what's actually broken, (2) Remnant mob family +
Remnant Tyrant boss, (3) iconic weapons batch 1 (scissors, Wintersteel blade, hunger arm, Eclipse).

v0.2 changes (2026-10-21): enrollment changed from random Path assignment to player choice via selection menu (playtest feedback); working jar v0.1.0 delivered to Kaleb and first cloud build compiled clean after 1.20.1 API fixes.

v0.3 changes (2026-10-21): FIXED root cause of silent enrollment failure — capability attach listener used Player.class filter but Forge fires AttachCapabilitiesEvent with Entity's base class as generic type, so it never matched (verified against Forge source); now Entity.class + instanceof. HUD rewritten to read synced ClientData (client capability copy never enrolls). Manual gets enchantment glint. UI click sound on Path buttons.

v0.4 changes (2026-10-21): level system — level 1-100, XP from cycling/kills/casts (config-tunable), stage caps (Foundation 5, Copper 10, Iron 20, Jade 25, Lowgold 35, Highgold 60, Truegold 100). All techniques unlocked from enrollment; power scales with level (1 + (level-1) * powerPerLevel). K toggles the stats menu. Stats menu redesigned with Stats/Techniques tabs, level XP bar, advancement progress, and per-technique descriptions. Fox Fire reworked: sprays ender-dragon fire (cone) instead of fireballs. New commands: /sacredarts setlevel, /sacredarts setstage.

## Technical notes (Forge 1.20.1)

- Player data via Forge capabilities (stage, Path, mastery, madra, iron body, goldsign).
- Curios API optional integration for relic/belt trinket slots (Forge-only — one more
  reason Forge was the right loader pick).
- Server-authoritative ability logic; config-driven numbers; datagen for loot/recipes.
- CI: GitHub Actions builds the jar on every push (see `.github/workflows/build.yml`).
