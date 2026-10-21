package com.sacredarts.network;

import com.sacredarts.SacredArts;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

/** 1.20.1-style simple channel (NetworkRegistry, not the newer ChannelBuilder API). */
public class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SacredArts.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, CastTechniquePacket.class,
                CastTechniquePacket::encode,
                CastTechniquePacket::new,
                CastTechniquePacket::handle);
        CHANNEL.registerMessage(id++, CyclePacket.class,
                CyclePacket::encode,
                CyclePacket::new,
                CyclePacket::handle);
        CHANNEL.registerMessage(id++, SelectIronBodyPacket.class,
                SelectIronBodyPacket::encode,
                SelectIronBodyPacket::new,
                SelectIronBodyPacket::handle);
        CHANNEL.registerMessage(id++, OpenIronBodyPacket.class,
                OpenIronBodyPacket::encode,
                OpenIronBodyPacket::new,
                OpenIronBodyPacket::handle);
        CHANNEL.registerMessage(id++, SyncDataPacket.class,
                SyncDataPacket::encode,
                SyncDataPacket::new,
                SyncDataPacket::handle);
        CHANNEL.registerMessage(id++, SelectPathPacket.class,
                SelectPathPacket::encode,
                SelectPathPacket::new,
                SelectPathPacket::handle);
        CHANNEL.registerMessage(id++, ToggleCopperSightPacket.class,
                ToggleCopperSightPacket::encode,
                ToggleCopperSightPacket::new,
                ToggleCopperSightPacket::handle);
    }

    /** Push the player's full sacred artist state to their client. */
    public static void syncPlayer(net.minecraft.server.level.ServerPlayer player) {
        player.getCapability(com.sacredarts.data.ModCapabilities.SACRED_ARTIST).ifPresent(data ->
                CHANNEL.send(net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                        new SyncDataPacket(data)));
    }
}
