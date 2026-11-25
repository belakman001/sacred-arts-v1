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

- **Abandoned Shrine** (built by Kaleb, 7x8x8, mossy stone brick/cobble) — spawns in
  forests (random spread, spacing 18, separation 6). A chiseled bookshelf holds the
  Foundation Manual (written in by a structure processor at generation — the Structure
  Block save kept only the "slot occupied" flag, not the placeholder book's item data),
  and a chest rolls the shrine loot table (Madra Pills, Remnant Core, Aura Vial,
  glow berries, amethyst, gold, books). Open idea: taking the manual triggers something
  via the bookshelf's redstone signal (light the rune circle, hidden compartment,
  guardian Remnant).
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
- **Soul Smithing** (idea, Nov 24 — not designed yet): craft special items, tools, and
  weapons from colored Remnant Cores (black, gold, red, blue, light green). The cores
  are in-game as plain items for now, awaiting the design.

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

v0.5 changes (2026-10-21): Copper Sight toggle (G keybind + server packet, persisted in NBT, shown in stats menu). Fixed K not closing the menu: IN_GAME keybinds don't fire while a screen is open, so StatsScreen now handles its own close key via keyPressed.

v0.6 changes (2026-10-21): Black Flame flavor rework — Striker (Dragon's Breath) now charges a growing black ball in the hand (~1.25s) then fires a black beam (aim at fire time); Enforcer (Burning Cloak) wreathed in black/orange flames for the full speed-boost duration; Forger (Cinder Claws) reworked into a Forged Wisp entity that builds overhead for 2s (black/orange particles, hand flames on caster) then hunts the caster's look target and detonates; Ultimate (The Dragon Descends) erupts in black/orange flame. Done button removed from stats menu (K/ESC close it).

v0.7 changes (2026-10-21): Wisp moved to Void Dragon's Dance (Ruler) with a longer 3s build-up (was on Cinder Claws by mistake). Cinder Claws (Forger) is now a charging fire fist that shoots forward as a particle projectile. Striker beam reworked into a thick black beacon-beam (denser core, wider); charge-up particles made smaller. The Dragon Descends gets far more particles. Endless Sword: Striker (Rippling Sword) now throws 3 flying iron-sword blades; Enforcer (Flowing Sword) has a silver glint for its whole duration; Ruler (Sword Aura Field) is now a 10s aura — slice marks in the air, ringing steel sound, and every weapon within 20 blocks (ground or held) damages entities within 3 blocks of it.

v0.8 changes (2026-10-21): Black Flame striker beam made MUCH denser black (was mostly flame); Cinder Claw particles reduced. Sword Aura Field renamed to The Endless Sword (aura ring made louder). Rippling Sword now requires a held sword and fires crescent slash waves (invisible, sweep-trail) matching The Endless Sword's style. Hollow Bolt renamed to The Hollow Spear — glowing white trident projectiles. Hollow King's Armor now shows spectral white armor (particle shell) for its duration. Still Air renamed to The Hollow Domain (20 blocks): slows+weakens, clears active techniques/entities, silences casting for 5s. Sandviper grants poison immunity. Venom Spit/Needles now fire bright toxic-green needle projectiles.

v0.9 changes (2026-10-21): The Endless Sword ring is now a true bell (BELL_RESONATE). Twin Stars Hollow Domain now matches the Hollow King's (20-block clear + 5s silence) with white area particles (ground ring + drifting motes). The Hollow Spear is now a white beacon-beam (no more trident items). Hollow King's Armor uses faint WHITE_ASH for a translucent spectral look. Sandviper poison immunity is now event-based (bulletproof — poison can never be applied); poison clouds now target the nearest mob in the look direction instead of always landing at max range.

v0.9 hotfix (2026-10-21): MobEffectEvent.Added is NOT cancellable in 1.20.1 — the event-based poison immunity crashed the Sandviper ultimate. Removed the handler; poison immunity is tick-based removeEffect only.

v0.10 changes (2026-11-23): Copper Sight reveals the hidden world — vital aura motes drift in the air, colored by the biome's aura (12 aura types: Fire, Water, Earth, Wind, Life, Shadow, Sword, Blood, Force, Dream, Light, Destruction), streaming into the caster while cycling; ore veins are marked by their aura type. Jade Sense (J key, Jade stage+): every living thing within 32 blocks glows through walls, color-coded — hostile red, passive green, other players white — via scoreboard teams + glowing effect. Spirit Scan: look at a target with Jade Sense on to read its Path/stage/level (players) or health/spirit strength (mobs) in a readout near the crosshair. Jade Sense state synced and shown in the stats menu.

v0.11 changes (2026-11-23): Iron Body actives (F key). Bloodforged Blood Surge: 15 madra, heal 6, 15s cooldown. Raindrop Rain Step: 10 madra, forward dash + Speed II, 8s cooldown. Oakheart Deep Roots: 10 madra, 6s knockback immunity (LivingKnockBackEvent cancel) + Resistance II, 15s cooldown. Oakheart's +2 hearts of max health is now actually applied (permanent MAX_HEALTH attribute modifier — it was described but never implemented). Iron Body active shown in the stats menu with an (F) hint.

v0.12 changes (2026-11-23): Vital aura is now a real resource (0-100, gold bar under the madra bar, shown in the stats menu). Cycling in a biome that breathes your Path's aura types draws it in (+2/s, small madra trickle, motes streaming into the caster). Path affinities: Black Flame Fire+Destruction, Twin Stars Light+Force, Endless Sword Sword+Force, Hollow King Shadow+Force, White Fox Light+Dream, Sandviper Blood+Life. Aura Vial (glass bottle + amethyst shard): bottle 25 stored aura; Filled Aura Vial drinks back +25 aura and +20 madra, returns the empty vial. Remnants: slain hostile mobs have a 40% chance to leave a ghostly spirit that drifts where it died for ~60s (colored by mob type, attackable, 20 HP). Cycling near one absorbs it: +10 madra, +2 mastery, +5 XP. Foundations for Gold+ are now in place: aura feeds soulfire, Remnants feed advancement and binding.

v0.14 changes (2026-11-24): The "Welcome to Sacred Arts" guide book — a vanilla written book (15 pages: the six Paths and their auras, how to start, the advancement ladder with exact requirements, what each stage grants, madra and level caps, Copper Sight and Jade Sense, every aura's color and where it lives, and the keybinds). Every player spawns with it: given once on first login (persistent flag; existing players get it on next login). Contextual aura: aura now answers to what happens around you, layered over the biome's own auras — Blood for 5 seconds after you strike a foe, Dream on a night in a village (villagers within 32 blocks), Wind above Y 120, Sword near a blade in hand or on the ground (weighted by tier: wood 1, stone 2, iron 3, diamond 4, netherite 5 — a better sword breathes more Sword aura and feeds an Endless Sword artist more while cycling), Destruction for 10 seconds after an explosion (with a Destruction-colored burst at the blast). Cycling gains from any matching ambient aura, not just the biome's.

v0.13 changes (2026-11-24): The Sacred White Fox is in the game — Kaleb's Blockbench model (white with lavender-purple markings, three tails fanning out), with a walk animation, leg swings, and a tail wag. It wanders snowy forests as a passive mob (spawn egg in the creative tab for now; natural spawning + the cycle-to-bond contract come later). Also: /sacredarts shrine places the Abandoned Shrine at your feet for testing. Shrine worldgen fix: the structure JSON now carries use_expansion_hack (missing it crashed world create/load with "Failed to load registries"); loot-table set_count now uses the proper uniform number format. Shrine canopy fix: project_start_to_heightmap changed from WORLD_SURFACE_WG (measures from the leaf tops in a forest) to OCEAN_FLOOR_WG (solid ground), so the shrine lands on the forest floor instead of in the trees. Shrine size fix: jigsaw size 1 → 7 — surveyed all 9 vanilla jigsaw structures, sizes are 6-7, ours was the only one below 6. Items are 3D now: Sav's 9 textures moved from flat generated sprites to thin boxes (manual + vials 4 thick, pill + 5 cores 6 thick) — the flat models' 1px edge washed out against Sav's light-edged art, so dropped items read as flat pictures spinning; the boxes read as solid objects.

Unreleased (2026-11-24): Abandoned Shrine — Kaleb's own build (7x8x8, mossy stone brick/cobble, moss carpet, lantern, bookshelves), now generating in forests. A structure processor puts a real Foundation Manual into slot 1 of the chiseled bookshelf at generation and attaches the shrine loot table to the chest. Sacred Beast contracts are beast-specific (Fox: cycle near until accepted; Serpent: duel; Crane: catch and feed; Tortoise: Orthos-like Black Flame, horse-sized and rideable — cycle beside him to a threshold, then ride and tame). Remnant strength is now relative to the player (same level: Weak/passive; player stronger: it flees; Remnant stronger: Strong/hostile); Sacred Beasts and Wandering Sacred Artists always leave a Remnant at Jade+. Vanilla mobs will be replaced gradually by dread beasts and Wandering Sacred Artists.

## Technical notes (Forge 1.20.1)

- Player data via Forge capabilities (stage, Path, mastery, madra, iron body, goldsign).
- Curios API optional integration for relic/belt trinket slots (Forge-only — one more
  reason Forge was the right loader pick).
- Server-authoritative ability logic; config-driven numbers; datagen for loot/recipes.
- CI: GitHub Actions builds the jar on every push (see `.github/workflows/build.yml`).
