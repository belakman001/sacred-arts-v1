package com.sacredarts.network;

import com.sacredarts.data.ModCapabilities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Client -> server: toggle Copper Sight on/off. */
public class ToggleCopperSightPacket {
    public ToggleCopperSightPacket() {}

    public ToggleCopperSightPacket(FriendlyByteBuf buf) {}

    public void encode(FriendlyByteBuf buf) {}

    public static void handle(ToggleCopperSightPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            player.getCapability(ModCapabilities.SACRED_ARTIST).ifPresent(data -> {
                if (!data.isEnrolled()) return;
                data.setCopperSightEnabled(!data.isCopperSightEnabled());
                player.displayClientMessage(Component.translatable(data.isCopperSightEnabled()
                        ? "message.sacredarts.copper_sight_on"
                        : "message.sacredarts.copper_sight_off"), true);
                ModNetwork.syncPlayer(player);
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
