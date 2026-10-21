package com.sacredarts.technique;

import net.minecraft.network.chat.Component;

/** The four canonical technique types: how madra is expressed. */
public enum TechniqueType {
    STRIKER("striker"),
    ENFORCER("enforcer"),
    RULER("ruler"),
    FORGER("forger");

    private final String id;

    TechniqueType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Component getDisplayName() {
        return Component.translatable("technique_type.sacredarts." + id);
    }
}
