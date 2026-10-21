package com.sacredarts.path;

import net.minecraft.network.chat.Component;

/** The six launch Paths. Each combines one or two madra aspects. */
public enum Path {
    BLACK_FLAME("black_flame"),
    TWIN_STARS("twin_stars"),
    ENDLESS_SWORD("endless_sword"),
    HOLLOW_KING("hollow_king"),
    WHITE_FOX("white_fox"),
    SANDVIPER("sandviper");

    private final String id;

    Path(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Component getDisplayName() {
        return Component.translatable("path.sacredarts." + id);
    }

    public Component getAspects() {
        return Component.translatable("path.sacredarts." + id + ".aspects");
    }

    public static Path byId(String id) {
        for (Path p : values()) {
            if (p.id.equals(id)) return p;
        }
        return BLACK_FLAME;
    }
}
