package com.sacredarts.advancement;

import net.minecraft.network.chat.Component;

/**
 * The sacred artist's advancement ladder, Foundation to Monarch.
 * v1 MVP plays Foundation -> Truegold; Lord realm and beyond are data-ready for v2.
 */
public enum Stage {
    FOUNDATION("foundation", 20),
    COPPER("copper", 40),
    IRON("iron", 60),
    JADE("jade", 80),
    LOWGOLD("lowgold", 110),
    HIGHGOLD("highgold", 140),
    TRUEGOLD("truegold", 180),
    UNDERLORD("underlord", 240),
    OVERLORD("overlord", 300),
    ARCHLORD("archlord", 380),
    SAGE("sage", 500),
    HERALD("herald", 500),
    MONARCH("monarch", 800);

    private final String id;
    private final double maxMadra;

    Stage(String id, double maxMadra) {
        this.id = id;
        this.maxMadra = maxMadra;
    }

    public String getId() {
        return id;
    }

    public double getMaxMadra() {
        return maxMadra;
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
