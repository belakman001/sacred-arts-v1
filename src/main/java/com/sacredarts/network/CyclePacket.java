package com.sacredarts.network;

import com.sacredarts.data.ModCapabilities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

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

    public static void handle(CyclePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                player.getCapability(ModCapabilities.SACRED_ARTIST).ifPresent(d -> d.setCycling(msg.cycling));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
