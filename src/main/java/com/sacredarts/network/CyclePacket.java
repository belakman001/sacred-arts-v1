package com.sacredarts.network;

import com.sacredarts.data.ModCapabilities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

/** Client -> server: cycling key state changed. */
public class CyclePacket {
    private final boolean cycling;

    public CyclePacket(boolean cycling) {
        this.cycling = cycling;
    }

    public CyclePacket(FriendlyByteBuf buf) {
        this.cycling = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(cycling);
    }

    public void handle(CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null) {
                player.getCapability(ModCapabilities.SACRED_ARTIST).ifPresent(d -> d.setCycling(cycling));
            }
        });
        ctx.setPacketHandled(true);
    }
}
