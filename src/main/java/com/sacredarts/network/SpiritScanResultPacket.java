package com.sacredarts.network;

import com.sacredarts.client.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Server -> client: the result of a Spirit Scan (lines joined with \n). */
public class SpiritScanResultPacket {
    private final String text;

    public SpiritScanResultPacket(String text) {
        this.text = text;
    }

    public SpiritScanResultPacket(FriendlyByteBuf buf) {
        this.text = buf.readUtf();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(text);
    }

    public static void handle(SpiritScanResultPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ClientPacketHandler.spiritScanResult(msg.text));
        ctx.get().setPacketHandled(true);
    }
}
