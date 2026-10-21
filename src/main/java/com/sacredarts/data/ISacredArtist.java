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
}
