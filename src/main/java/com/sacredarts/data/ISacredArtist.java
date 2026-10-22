package com.sacredarts.data;

import com.sacredarts.advancement.Stage;
import com.sacredarts.path.Path;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

/** Per-player sacred arts data: stage, path, mastery, madra pool, body, goldsign. */
public interface ISacredArtist extends INBTSerializable<CompoundTag> {

    boolean isEnrolled();

    /** Enroll a new sacred artist on a Path. Resets all progress. */
    void enroll(Path path);

    Stage getStage();
    void setStage(Stage stage);

    Path getPath();
    void setPath(Path path);

    int getMastery();
    void addMastery(int amount);
    void setMastery(int mastery);

    int getLevel();
    void setLevel(int level);
    int getLevelXp();
    void addLevelXp(int amount);

    /** Technique power multiplier from level: 1.0 at level 1, growing per level. */
    default double getPowerMultiplier() {
        return 1.0 + (getLevel() - 1)
                * com.sacredarts.config.SacredArtsConfig.TECHNIQUE_POWER_PER_LEVEL.get();
    }

    double getMadra();
    double getMaxMadra();
    void setMadra(double madra);
    /** Returns false if there wasn't enough madra. */
    boolean spendMadra(double amount);

    double getCycledTotal();
    void addCycled(double amount);

    int getKills();
    void addKill();

    IronBody getIronBody();
    void setIronBody(IronBody body);

    boolean hasGoldsign();
    void setGoldsign(boolean goldsign);

    boolean isCycling();
    void setCycling(boolean cycling);

    boolean isCopperSightEnabled();
    void setCopperSightEnabled(boolean enabled);

    /** Black Flame striker charge-up: ticks remaining, 0 = not charging. */
    int getStrikerChargeTicks();
    void setStrikerChargeTicks(int ticks);
    float getStrikerChargeDamage();
    void setStrikerChargeDamage(float damage);

    /** Black Flame enforcer: ticks of flame particles remaining. */
    int getCloakTicks();
    void setCloakTicks(int ticks);

    /** Black Flame forger fire fist: charge ticks remaining, 0 = not charging. */
    int getFistChargeTicks();
    void setFistChargeTicks(int ticks);
    float getFistChargeDamage();
    void setFistChargeDamage(float damage);

    /** Endless Sword enforcer: ticks of silver glint remaining. */
    int getSwordGlintTicks();
    void setSwordGlintTicks(int ticks);

    /** Endless Sword ruler: ticks of sword aura remaining. */
    int getSwordAuraTicks();
    void setSwordAuraTicks(int ticks);

    /** Hollow King enforcer: ticks of spectral armor remaining. */
    int getArmorTicks();
    void setArmorTicks(int ticks);

    /** Hollow Domain: ticks during which technique casting is blocked. */
    int getSilenceTicks();
    void setSilenceTicks(int ticks);
}
