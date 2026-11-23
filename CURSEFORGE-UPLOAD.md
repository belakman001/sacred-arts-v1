# Sacred Arts — CurseForge Upload Pack

Drafted 2026-10-22 by Kbub. Nothing here is published or uploaded; the version bump
still needs Kaleb's OK.

## The one pending action (needs his yes)

Bump `mod_version` in `gradle.properties` from `0.1.0` to `0.9.0`, commit, push.
GitHub Actions builds the jar automatically; that jar is the upload candidate.
Verified: `mods.toml` reads `version="${mod_version}"`, so no other file changes are needed.

## About that GitHub failure email in his inbox

The "Run failed: Build mod jar - main (7b917da)" notification (2026-10-22T03:58:37Z)
was the *first* v0.9 build attempt — it failed on the particle API. The rebuild
(`707fa94`) and the crash hotfix (`5b2ee29`) both passed, and Kaleb playtested those
jars. The failure is superseded; the email can be archived.

## Upload steps (CurseForge)

1. Create/log into a CurseForge account → Create Project.
2. Game: Minecraft. Loader: Forge. Game version: 1.20.1.
3. Project name: **Sacred Arts** (mod id `sacredarts`).
4. Upload the 0.9.0 jar from the passing GitHub Actions run; set the release type
   to Beta (playtested, but pre-1.0).
5. Paste the description draft below; add 2–3 in-game screenshots if he has them.
6. Submit. New projects go through review — typically a day or two before files
   go live. That's normal.

## Requirements for players (for the description page)

- Minecraft 1.20.1, Forge 47+ (builds against 47.4.0). No other dependencies.

## Description draft

> **Sacred Arts** — a cultivation mod: cycle madra, walk a Path, master techniques,
> and advance from Foundation to Monarch.
>
> Enroll with a Foundation Manual and let a Path choose you: Black Flame, Twin Stars,
> Endless Sword, Hollow King, White Fox, or Sandviper — each with Striker, Enforcer,
> Ruler, Forger, and Ultimate techniques. Abilities cost madra (no cooldowns: if you
> can pay, you can cast). Fight to raise Path Mastery, then clear trials to advance
> Copper → Iron → Jade → Lowgold → Highgold → Truegold, unlocking Copper Sight, a
> permanent Iron Body choice, Goldsigns, and more.
>
> *Unofficial fan work inspired by the Cradle series by Will Wight. Not affiliated
> with or endorsed by Will Wight or Hidden Gnome Publishing.*

## Suggestion (not a gate)

The last jar he playtested included the Sandviper crash hotfix; one quick
playtest confirming the hotfix works before uploading would be prudent — but the
upload itself works regardless.
