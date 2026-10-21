package com.sacredarts.client;

import com.sacredarts.SacredArts;
import com.sacredarts.network.CastTechniquePacket;
import com.sacredarts.network.CyclePacket;
import com.sacredarts.network.ModNetwork;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ClientEvents {

    @Mod.EventBusSubscriber(modid = SacredArts.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModBus {
        @SubscribeEvent
        public static void onRegisterKeys(RegisterKeyMappingsEvent event) {
            KeyBindings.registerAll(event);
        }

        @SubscribeEvent
        public static void onRegisterOverlays(RegisterGuiOverlaysEvent event) {
            event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "madra_bar", MadraOverlay.HUD);
        }
    }

    @Mod.EventBusSubscriber(modid = SacredArts.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ForgeBus {
        private static boolean wasCycling = false;

        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.screen != null) return;
            boolean sneak = mc.player.isShiftKeyDown();
            if (KeyBindings.TECH_1.consumeClick()) {
                ModNetwork.CHANNEL.sendToServer(new CastTechniquePacket(0, sneak));
            } else if (KeyBindings.TECH_2.consumeClick()) {
                ModNetwork.CHANNEL.sendToServer(new CastTechniquePacket(1, sneak));
            } else if (KeyBindings.TECH_3.consumeClick()) {
                ModNetwork.CHANNEL.sendToServer(new CastTechniquePacket(2, sneak));
            } else if (KeyBindings.TECH_4.consumeClick()) {
                ModNetwork.CHANNEL.sendToServer(new CastTechniquePacket(3, sneak));
            }
            if (KeyBindings.MENU.consumeClick()) {
                ClientPacketHandler.openStatsScreen();
            }
        }

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;
            boolean down = KeyBindings.CYCLE.isDown();
            if (down != wasCycling) {
                wasCycling = down;
                ModNetwork.CHANNEL.sendToServer(new CyclePacket(down));
            }
        }
    }
}
