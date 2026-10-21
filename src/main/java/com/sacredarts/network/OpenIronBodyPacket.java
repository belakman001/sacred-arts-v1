package com.sacredarts.network;

import com.sacredarts.client.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

/** Server -> client: open the Iron Body choice screen. */
public class OpenIronBodyPacket {

    public OpenIronBodyPacket() {}

    public OpenIronBodyPacket(FriendlyByteBuf buf) {}

    public void encode(FriendlyByteBuf buf) {}

    public void handle(CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(ClientPacketHandler::openIronBodyScreen);
        ctx.setPacketHandled(true);
    }
}
