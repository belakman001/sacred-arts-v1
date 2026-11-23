package com.sacredarts.aura;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import org.joml.Vector3f;

/** Vital aura types — the ambient essence of the world that sacred artists cycle. */
public enum AuraType {
    FIRE("fire", 1.0f, 0.35f, 0.05f),
    WATER("water", 0.15f, 0.45f, 1.0f),
    EARTH("earth", 0.55f, 0.4f, 0.2f),
    WIND("wind", 0.85f, 0.9f, 0.95f),
    LIFE("life", 0.25f, 0.9f, 0.3f),
    SHADOW("shadow", 0.45f, 0.2f, 0.7f),
    SWORD("sword", 0.85f, 0.85f, 0.9f),
    BLOOD("blood", 0.7f, 0.05f, 0.1f),
    FORCE("force", 0.65f, 0.4f, 1.0f),
    DREAM("dream", 1.0f, 0.6f, 0.85f),
    LIGHT("light", 1.0f, 0.85f, 0.3f),
    DESTRUCTION("destruction", 0.35f, 0.02f, 0.08f);

    private final String id;
    private final float r;
    private final float g;
    private final float b;

    AuraType(String id, float r, float g, float b) {
        this.id = id;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public String getId() {
        return id;
    }

    public Component getDisplayName() {
        return Component.translatable("aura.sacredarts." + id);
    }

    /** Colored dust particle for this aura type, sized for motes. */
    public DustParticleOptions dust(float scale) {
        return new DustParticleOptions(new Vector3f(r, g, b), scale);
    }

    public static AuraType byId(String id) {
        for (AuraType a : values()) {
            if (a.id.equals(id)) return a;
        }
        return WIND;
    }
}
