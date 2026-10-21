package com.sacredarts.network;

import com.sacredarts.data.ModCapabilities;
import com.sacredarts.item.ModItems;
import com.sacredarts.path.Path;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Client -> server: player chose their Path from the selection menu. */
public class SelectPathPacket {
    private final String pathId;
    private final InteractionHand hand;

    public SelectPathPacket(String pathId, InteractionHand hand) {
        this.pathId = pathId;
        this.hand = hand;
    }

    public SelectPathPacket(FriendlyByteBuf buf) {
        this.pathId = buf.readUtf();
        this.hand = buf.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(pathId);
        buf.writeBoolean(hand == InteractionHand.MAIN_HAND);
    }

    public static void handle(SelectPathPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            player.getCapability(ModCapabilities.SACRED_ARTIST).ifPresent(data -> {
                if (data.isEnrolled()) return;
                if (!player.getItemInHand(msg.hand).is(ModItems.FOUNDATION_MANUAL.get())) return;
                Path chosen = Path.byId(msg.pathId);
                data.enroll(chosen);
                player.getItemInHand(msg.hand).shrink(1);
                player.displayClientMessage(
                        Component.translatable("message.sacredarts.enrolled", chosen.getDisplayName()), false);
                player.displayClientMessage(
                        Component.translatable("message.sacredarts.enrolled_aspects", chosen.getAspects()), false);
                player.displayClientMessage(
                        Component.translatable("message.sacredarts.enrolled_hint"), false);
                player.serverLevel().playSound(null, player.blockPosition(),
                        SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0f, 1.2f);
                ModNetwork.syncPlayer(player);
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
