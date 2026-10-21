package com.sacredarts.advancement;

import net.minecraft.network.chat.Component;

/**
 * The sacred artist's advancement ladder, Foundation to Monarch.
 * v1 MVP plays Foundation -> Truegold; Lord realm and beyond are data-ready for v2.
 */
public enum Stage {
    FOUNDATION("foundation", 20, 5),
    COPPER("copper", 40, 10),
    IRON("iron", 60, 20),
    JADE("jade", 80, 25),
    LOWGOLD("lowgold", 110, 35),
    HIGHGOLD("highgold", 140, 60),
    TRUEGOLD("truegold", 180, 100),
    UNDERLORD("underlord", 240, 100),
    OVERLORD("overlord", 300, 100),
    ARCHLORD("archlord", 380, 100),
    SAGE("sage", 500, 100),
    HERALD("herald", 500, 100),
    MONARCH("monarch", 800, 100);

    private final String id;
    private final double maxMadra;
    private final int maxLevel;

    Stage(String id, double maxMadra, int maxLevel) {
        this.id = id;
        this.maxMadra = maxMadra;
        this.maxLevel = maxLevel;
    }

    public String getId() {
        return id;
    }

    public double getMaxMadra() {
        return maxMadra;
    }

    /** Highest level attainable while at this stage. */
    public int getMaxLevel() {
        return maxLevel;
    }

    public Component getDisplayName() {
        return Component.translatable("stage.sacredarts." + id);
    }

    /** Next stage, or null at Monarch. */
    public Stage next() {
        Stage[] all = values();
        int i = ordinal() + 1;
        return i < all.length ? all[i] : null;
    }

    public static Stage byId(String id) {
        for (Stage s : values()) {
            if (s.id.equals(id)) return s;
        }
        return FOUNDATION;
    }

    public boolean atLeast(Stage other) {
        return ordinal() >= other.ordinal();
    }
}
