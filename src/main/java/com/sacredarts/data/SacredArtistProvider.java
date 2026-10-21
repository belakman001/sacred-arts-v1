package com.sacredarts.data;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

/** Capability provider attaching a SacredArtist to every player. */
public class SacredArtistProvider implements ICapabilityProvider {
    private final SacredArtist backend = new SacredArtist();
    private final LazyOptional<ISacredArtist> optional = LazyOptional.of(() -> backend);

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, net.minecraft.core.Direction side) {
        return cap == ModCapabilities.SACRED_ARTIST ? optional.cast() : LazyOptional.empty();
    }

    public void invalidate() {
        optional.invalidate();
    }

    public CompoundTag serialize() {
        return backend.serializeNBT();
    }

    public void deserialize(CompoundTag tag) {
        backend.deserializeNBT(tag);
    }
}
