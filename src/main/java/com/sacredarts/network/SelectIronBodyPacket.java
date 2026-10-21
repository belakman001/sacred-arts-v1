package com.sacredarts.network;

import com.sacredarts.data.IronBody;
import com.sacredarts.data.ModCapabilities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

/** Client -> server: player chose their Iron Body. */
public class SelectIronBodyPacket {
    private static final UUID OAKHEART_ID = UUID.fromString("7d3b8c2a-1e4f-4a5b-9c6d-8e7f0a1b2c3d");

    private final IronBody body;

    public SelectIronBodyPacket(IronBody body) {
        this.body = body;
    }

    public SelectIronBodyPacket(FriendlyByteBuf buf) {
        this.body = IronBody.byId(buf.readUtf());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(body.getId());
    }

    public static void handle(SelectIronBodyPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            player.getCapability(ModCapabilities.SACRED_ARTIST).ifPresent(d -> {
                if (d.getIronBody() != IronBody.NONE) return; // already chosen — permanent
                d.setIronBody(msg.body);
                if (msg.body == IronBody.OAKHEART) {
                    var attr = player.getAttribute(Attributes.MAX_HEALTH);
                    if (attr != null && attr.getModifier(OAKHEART_ID) == null) {
                        attr.addPermanentModifier(new AttributeModifier(
                                OAKHEART_ID, "sacredarts:oakheart", 4.0,
                                AttributeModifier.Operation.ADDITION));
                    }
                }
                player.displayClientMessage(
                        Component.translatable("message.sacredarts.iron_body_chosen", msg.body.getDisplayName()), false);
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
