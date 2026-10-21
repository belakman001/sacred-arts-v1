package com.sacredarts.network;

import com.sacredarts.technique.TechniqueHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

/** Client -> server: cast technique in slot 0-3. Sneak + slot 3 = ultimate. */
public class CastTechniquePacket {
    private final int slot;
    private final boolean sneaking;

    public CastTechniquePacket(int slot, boolean sneaking) {
        this.slot = slot;
        this.sneaking = sneaking;
    }

    public CastTechniquePacket(FriendlyByteBuf buf) {
        this.slot = buf.readInt();
        this.sneaking = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(slot);
        buf.writeBoolean(sneaking);
    }

    public void handle(CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null) {
                TechniqueHandler.castSlot(player, slot, sneaking);
            }
        });
        ctx.setPacketHandled(true);
    }
}
