package com.sacredarts.technique;

import com.sacredarts.data.ISacredArtist;
import com.sacredarts.path.Path;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/** A single sacred-art technique: madra cost, mastery gate, server-side effect. */
public abstract class Technique {
    private final String id;
    private final Path path;
    private final TechniqueType type;
    private final int madraCost;
    private final int masteryRequired;

    protected Technique(String id, Path path, TechniqueType type, int madraCost, int masteryRequired) {
        this.id = id;
        this.path = path;
        this.type = type;
        this.madraCost = madraCost;
        this.masteryRequired = masteryRequired;
    }

    public String getId() { return id; }
    public Path getPath() { return path; }
    public TechniqueType getType() { return type; }
    public int getMadraCost() { return madraCost; }
    public int getMasteryRequired() { return masteryRequired; }

    public Component getDisplayName() {
        return Component.translatable("technique.sacredarts." + id);
    }

    public abstract void cast(ServerPlayer player, ISacredArtist data);
}
