package com.sacredarts.data;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import com.sacredarts.SacredArts;

public class ModCapabilities {
    public static final Capability<ISacredArtist> SACRED_ARTIST =
            CapabilityManager.get(new CapabilityToken<>() {});

    @Mod.EventBusSubscriber(modid = SacredArts.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Registration {
        @SubscribeEvent
        public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
            event.register(ISacredArtist.class);
        }
    }
}
