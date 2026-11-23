package com.sacredarts.network;

import com.sacredarts.advancement.Stage;
import com.sacredarts.data.ModCapabilities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Client -> server: toggle Jade Sense on/off (requires Jade stage). */
public class ToggleJadeSensePacket {
    public ToggleJadeSensePacket() {}

    public ToggleJadeSensePacket(FriendlyByteBuf buf) {}

    public void encode(FriendlyByteBuf buf) {}

    public static void handle(ToggleJadeSensePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            player.getCapability(ModCapabilities.SACRED_ARTIST).ifPresent(data -> {
                if (!data.isEnrolled() || !data.getStage().atLeast(Stage.JADE)) return;
                data.setJadeSenseEnabled(!data.isJadeSenseEnabled());
                player.displayClientMessage(Component.translatable(data.isJadeSenseEnabled()
                        ? "message.sacredarts.jade_sense_on"
                        : "message.sacredarts.jade_sense_off"), true);
                ModNetwork.syncPlayer(player);
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
