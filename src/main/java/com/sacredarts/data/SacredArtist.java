package com.sacredarts.data;

import com.sacredarts.advancement.Stage;
import com.sacredarts.path.Path;
import net.minecraft.nbt.CompoundTag;

public class SacredArtist implements ISacredArtist {
    private boolean enrolled = false;
    private Stage stage = Stage.FOUNDATION;
    private Path path = Path.BLACK_FLAME;
    private int mastery = 0;
    private int level = 1;
    private int levelXp = 0;
    private double madra = 0;
    private double cycledTotal = 0;
    private int kills = 0;
    private IronBody ironBody = IronBody.NONE;
    private boolean goldsign = false;
    private boolean cycling = false;
    private boolean copperSightEnabled = true;
    private int strikerChargeTicks = 0;
    private float strikerChargeDamage = 0;
    private int cloakTicks = 0;
    private int fistChargeTicks = 0;
    private float fistChargeDamage = 0;
    private int swordGlintTicks = 0;
    private int swordAuraTicks = 0;
    private int armorTicks = 0;
    private int silenceTicks = 0;

    @Override public boolean isEnrolled() { return enrolled; }

    @Override public Stage getStage() { return stage; }
    @Override public void setStage(Stage stage) {
        this.stage = stage;
        this.enrolled = true;
        if (madra > getMaxMadra()) madra = getMaxMadra();
    }

    @Override public Path getPath() { return path; }
    @Override public void setPath(Path path) { this.path = path; }

    @Override public int getMastery() { return mastery; }
    @Override public void addMastery(int amount) { this.mastery = Math.max(0, mastery + amount); }
    @Override public void setMastery(int mastery) { this.mastery = Math.max(0, mastery); }

    @Override public int getLevel() { return level; }
    @Override public void setLevel(int level) {
        this.level = Math.max(1, Math.min(100, level));
        this.levelXp = 0;
    }
    @Override public int getLevelXp() { return levelXp; }
    @Override public void addLevelXp(int amount) {
        if (amount <= 0 || !enrolled) return;
        int max = stage.getMaxLevel();
        if (level >= max) {
            levelXp = 0;
            return;
        }
        levelXp += amount;
        int threshold = com.sacredarts.config.SacredArtsConfig.LEVEL_XP_PER_LEVEL.get();
        while (levelXp >= threshold && level < max) {
            levelXp -= threshold;
            level++;
        }
        if (level >= max) levelXp = 0;
    }

    @Override public double getMadra() { return madra; }
    @Override public double getMaxMadra() { return stage.getMaxMadra(); }
    @Override public void setMadra(double madra) {
        this.madra = Math.max(0, Math.min(madra, getMaxMadra()));
    }

    @Override public boolean spendMadra(double amount) {
        if (madra < amount) return false;
        madra -= amount;
        return true;
    }

    @Override public double getCycledTotal() { return cycledTotal; }
    @Override public void addCycled(double amount) {
        this.cycledTotal += amount;
        setMadra(madra + amount);
    }

    @Override public int getKills() { return kills; }
    @Override public void addKill() { this.kills++; }

    @Override public IronBody getIronBody() { return ironBody; }
    @Override public void setIronBody(IronBody body) { this.ironBody = body; }

    @Override public boolean hasGoldsign() { return goldsign; }
    @Override public void setGoldsign(boolean goldsign) { this.goldsign = goldsign; }

    @Override public boolean isCycling() { return cycling; }
    @Override public void setCycling(boolean cycling) { this.cycling = cycling; }

    @Override public boolean isCopperSightEnabled() { return copperSightEnabled; }
    @Override public void setCopperSightEnabled(boolean enabled) { this.copperSightEnabled = enabled; }

    @Override public int getStrikerChargeTicks() { return strikerChargeTicks; }
    @Override public void setStrikerChargeTicks(int ticks) { this.strikerChargeTicks = Math.max(0, ticks); }
    @Override public float getStrikerChargeDamage() { return strikerChargeDamage; }
    @Override public void setStrikerChargeDamage(float damage) { this.strikerChargeDamage = damage; }
    @Override public int getCloakTicks() { return cloakTicks; }
    @Override public void setCloakTicks(int ticks) { this.cloakTicks = Math.max(0, ticks); }
    @Override public int getFistChargeTicks() { return fistChargeTicks; }
    @Override public void setFistChargeTicks(int ticks) { this.fistChargeTicks = Math.max(0, ticks); }
    @Override public float getFistChargeDamage() { return fistChargeDamage; }
    @Override public void setFistChargeDamage(float damage) { this.fistChargeDamage = damage; }
    @Override public int getSwordGlintTicks() { return swordGlintTicks; }
    @Override public void setSwordGlintTicks(int ticks) { this.swordGlintTicks = Math.max(0, ticks); }
    @Override public int getSwordAuraTicks() { return swordAuraTicks; }
    @Override public void setSwordAuraTicks(int ticks) { this.swordAuraTicks = Math.max(0, ticks); }
    @Override public int getArmorTicks() { return armorTicks; }
    @Override public void setArmorTicks(int ticks) { this.armorTicks = Math.max(0, ticks); }
    @Override public int getSilenceTicks() { return silenceTicks; }
    @Override public void setSilenceTicks(int ticks) { this.silenceTicks = Math.max(0, ticks); }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("enrolled", enrolled);
        tag.putString("stage", stage.getId());
        tag.putString("path", path.getId());
        tag.putInt("mastery", mastery);
        tag.putInt("level", level);
        tag.putInt("levelXp", levelXp);
        tag.putDouble("madra", madra);
        tag.putDouble("cycledTotal", cycledTotal);
        tag.putInt("kills", kills);
        tag.putString("ironBody", ironBody.getId());
        tag.putBoolean("goldsign", goldsign);
        tag.putBoolean("copperSightEnabled", copperSightEnabled);
        tag.putInt("strikerChargeTicks", strikerChargeTicks);
        tag.putFloat("strikerChargeDamage", strikerChargeDamage);
        tag.putInt("cloakTicks", cloakTicks);
        tag.putInt("fistChargeTicks", fistChargeTicks);
        tag.putFloat("fistChargeDamage", fistChargeDamage);
        tag.putInt("swordGlintTicks", swordGlintTicks);
        tag.putInt("swordAuraTicks", swordAuraTicks);
        tag.putInt("armorTicks", armorTicks);
        tag.putInt("silenceTicks", silenceTicks);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        enrolled = tag.getBoolean("enrolled");
        stage = Stage.byId(tag.getString("stage"));
        path = Path.byId(tag.getString("path"));
        mastery = tag.getInt("mastery");
        level = Math.max(1, tag.getInt("level"));
        levelXp = tag.getInt("levelXp");
        madra = tag.getDouble("madra");
        cycledTotal = tag.getDouble("cycledTotal");
        kills = tag.getInt("kills");
        ironBody = IronBody.byId(tag.getString("ironBody"));
        goldsign = tag.getBoolean("goldsign");
        copperSightEnabled = !tag.contains("copperSightEnabled") || tag.getBoolean("copperSightEnabled");
        strikerChargeTicks = tag.getInt("strikerChargeTicks");
        strikerChargeDamage = tag.getFloat("strikerChargeDamage");
        cloakTicks = tag.getInt("cloakTicks");
        fistChargeTicks = tag.getInt("fistChargeTicks");
        fistChargeDamage = tag.getFloat("fistChargeDamage");
        swordGlintTicks = tag.getInt("swordGlintTicks");
        swordAuraTicks = tag.getInt("swordAuraTicks");
        armorTicks = tag.getInt("armorTicks");
        silenceTicks = tag.getInt("silenceTicks");
        cycling = false;
    }

    public void enroll(Path path) {
        this.enrolled = true;
        this.path = path;
        this.stage = Stage.FOUNDATION;
        this.mastery = 0;
        this.level = 1;
        this.levelXp = 0;
        this.madra = getMaxMadra();
        this.cycledTotal = 0;
        this.kills = 0;
        this.ironBody = IronBody.NONE;
        this.goldsign = false;
        this.cycling = false;
    }
}
