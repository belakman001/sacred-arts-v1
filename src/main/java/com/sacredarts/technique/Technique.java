package com.sacredarts.technique;

import com.sacredarts.data.ISacredArtist;
import com.sacredarts.path.Path;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * A single sacred-art technique: madra cost and server-side effect.
 * All techniques are unlocked from enrollment; they grow stronger with level.
 */
public abstract class Technique {
    private final String id;
    private final Path path;
    private final TechniqueType type;
    private final int madraCost;

    protected Technique(String id, Path path, TechniqueType type, int madraCost) {
        this.id = id;
        this.path = path;
        this.type = type;
        this.madraCost = madraCost;
    }

    public String getId() { return id; }
    public Path getPath() { return path; }
    public TechniqueType getType() { return type; }
    public int getMadraCost() { return madraCost; }

    public Component getDisplayName() {
        return Component.translatable("technique.sacredarts." + id);
    }

    public Component getDescription() {
        return Component.translatable("technique.sacredarts." + id + ".desc");
    }

    public abstract void cast(ServerPlayer player, ISacredArtist data);
}
