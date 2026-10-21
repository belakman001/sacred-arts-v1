package com.sacredarts.network;

import com.sacredarts.client.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Server -> client: open the Iron Body choice screen. */
public class OpenIronBodyPacket {

    public OpenIronBodyPacket() {}

    public OpenIronBodyPacket(FriendlyByteBuf buf) {}

    public void encode(FriendlyByteBuf buf) {}

    public static void handle(OpenIronBodyPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(ClientPacketHandler::openIronBodyScreen);
        ctx.get().setPacketHandled(true);
    }
}
