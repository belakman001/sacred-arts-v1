package com.sacredarts.network;

import com.sacredarts.advancement.Stage;
import com.sacredarts.data.ModCapabilities;
import com.sacredarts.path.Path;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

/**
 * Client -> server: "what am I looking at?" while Jade Sense is active.
 * The server answers with a SpiritScanResultPacket.
 */
public class RequestSpiritScanPacket {
    private final int entityId;

    public RequestSpiritScanPacket(int entityId) {
        this.entityId = entityId;
    }

    public RequestSpiritScanPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
    }

    public static void handle(RequestSpiritScanPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            if (!(player.level() instanceof ServerLevel level)) return;
            Entity target = level.getEntity(msg.entityId);
            if (target == null) return;
            String text = describe(player, target);
            ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                    new SpiritScanResultPacket(text));
        });
        ctx.get().setPacketHandled(true);
    }

    private static String describe(ServerPlayer scanner, Entity target) {
        if (target instanceof ServerPlayer sp) {
            String path = "?";
            String stage = "?";
            int lvl = 1;
            var cap = sp.getCapability(ModCapabilities.SACRED_ARTIST).resolve();
            if (cap.isPresent()) {
                var data = cap.get();
                if (data.isEnrolled()) {
                    path = Path.byId(data.getPath().getId()).getDisplayName().getString();
                    stage = data.getStage().getDisplayName().getString();
                    lvl = data.getLevel();
                } else {
                    return sp.getName().getString() + "\nWalks no Path";
                }
            }
            return sp.getName().getString() + "\n" + path + " • " + stage + "\nLevel " + lvl;
        }
        if (target instanceof LivingEntity living) {
            String spirit;
            float max = living.getMaxHealth();
            if (max >= 100) spirit = "Apex";
            else if (max >= 40) spirit = "Strong";
            else if (max >= 20) spirit = "Moderate";
            else spirit = "Weak";
            return living.getName().getString()
                    + "\nHealth " + (int) living.getHealth() + " / " + (int) max
                    + "\nSpirit: " + spirit;
        }
        return target.getName().getString();
    }
}
