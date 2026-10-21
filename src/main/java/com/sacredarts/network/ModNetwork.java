package com.sacredarts.network;

import com.sacredarts.SacredArts;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.SimpleChannel;

public class ModNetwork {
    public static final SimpleChannel CHANNEL = ChannelBuilder
            .named(new ResourceLocation(SacredArts.MODID, "main"))
            .networkProtocolVersion(1)
            .clientAcceptedVersions((status, version) -> true)
            .serverAcceptedVersions((status, version) -> true)
            .simpleChannel();

    public static void register() {
        int id = 0;
        CHANNEL.messageBuilder(CastTechniquePacket.class, id++)
                .encoder(CastTechniquePacket::encode)
                .decoder(CastTechniquePacket::new)
                .consumerMainThread(CastTechniquePacket::handle)
                .add();
        CHANNEL.messageBuilder(CyclePacket.class, id++)
                .encoder(CyclePacket::encode)
                .decoder(CyclePacket::new)
                .consumerMainThread(CyclePacket::handle)
                .add();
        CHANNEL.messageBuilder(SelectIronBodyPacket.class, id++)
                .encoder(SelectIronBodyPacket::encode)
                .decoder(SelectIronBodyPacket::new)
                .consumerMainThread(SelectIronBodyPacket::handle)
                .add();
        CHANNEL.messageBuilder(OpenIronBodyPacket.class, id++)
                .encoder(OpenIronBodyPacket::encode)
                .decoder(OpenIronBodyPacket::new)
                .consumerMainThread(OpenIronBodyPacket::handle)
                .add();
    }
}
