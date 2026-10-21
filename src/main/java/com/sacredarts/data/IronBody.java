package com.sacredarts.data;

import net.minecraft.network.chat.Component;

/**
 * Iron Body choice at the Iron stage. Permanent — choose wisely.
 * (Bloodforged and Raindrop are canon; Oakheart is our invention.)
 */
public enum IronBody {
    NONE("none"),
    BLOODFORGED("bloodforged"),
    RAINDROP("raindrop"),
    OAKHEART("oakheart");

    private final String id;

    IronBody(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Component getDisplayName() {
        return Component.translatable("iron_body.sacredarts." + id);
    }

    public Component getDescription() {
        return Component.translatable("iron_body.sacredarts." + id + ".desc");
    }

    public static IronBody byId(String id) {
        for (IronBody b : values()) {
            if (b.id.equals(id)) return b;
        }
        return NONE;
    }
}
