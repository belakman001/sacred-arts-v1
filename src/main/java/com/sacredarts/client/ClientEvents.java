package com.sacredarts.client;

import com.sacredarts.SacredArts;
import com.sacredarts.advancement.Stage;
import com.sacredarts.aura.AuraHelper;
import com.sacredarts.aura.AuraType;
import com.sacredarts.entity.ModEntities;
import com.sacredarts.network.CastTechniquePacket;
import com.sacredarts.network.CyclePacket;
import com.sacredarts.network.IronBodyActivePacket;
import com.sacredarts.network.ModNetwork;
import com.sacredarts.network.RequestSpiritScanPacket;
import com.sacredarts.network.ToggleCopperSightPacket;
import com.sacredarts.network.ToggleJadeSensePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.client.event.EntityRenderersEvent;
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
            event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "spirit_scan", SpiritScanOverlay.HUD);
        }

        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ModEntities.FORGED_WISP.get(), ParticleOnlyRenderer::new);
            event.registerEntityRenderer(ModEntities.FIRE_FIST.get(), ParticleOnlyRenderer::new);
            event.registerEntityRenderer(ModEntities.BLADE.get(), ParticleOnlyRenderer::new);
            event.registerEntityRenderer(ModEntities.NEEDLE.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(ModEntities.REMNANT.get(), ParticleOnlyRenderer::new);
        }
    }

    @Mod.EventBusSubscriber(modid = SacredArts.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ForgeBus {
        private static boolean wasCycling = false;
        private static int lastScannedId = -1;
        private static long lastScanTick = -100;

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
                // Opening only — closing is handled by StatsScreen.keyPressed,
                // because IN_GAME keybinds don't fire while a screen is open.
                ClientPacketHandler.openStatsScreen();
            }
            if (KeyBindings.COPPER_SIGHT.consumeClick()) {
                ModNetwork.CHANNEL.sendToServer(new ToggleCopperSightPacket());
            }
            if (KeyBindings.JADE_SENSE.consumeClick()) {
                ModNetwork.CHANNEL.sendToServer(new ToggleJadeSensePacket());
            }
            if (KeyBindings.IRON_BODY.consumeClick()) {
                ModNetwork.CHANNEL.sendToServer(new IronBodyActivePacket());
            }
        }

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.level == null) return;
            boolean down = KeyBindings.CYCLE.isDown();
            if (down != wasCycling) {
                wasCycling = down;
                ModNetwork.CHANNEL.sendToServer(new CyclePacket(down));
            }
            if (!ClientData.enrolled) return;
            long time = mc.level.getGameTime();
            tickAuraVision(mc, time);
            tickSpiritScan(mc, time);
        }

        /**
         * Copper Sight reveals the hidden world: vital aura motes drifting in the
         * air (colored by the biome's aura) and ore veins marked by their aura.
         * While cycling, nearby motes stream toward the caster.
         */
        private static void tickAuraVision(Minecraft mc, long time) {
            if (!ClientData.copperSightEnabled
                    || !Stage.byId(ClientData.stageId).atLeast(Stage.COPPER)) return;
            Vec3 center = mc.player.position().add(0, 1, 0);
            BlockPos pos = mc.player.blockPosition();

            if (time % 10 == 0) {
                for (int i = 0; i < 2; i++) {
                    double x = center.x + (mc.player.getRandom().nextDouble() - 0.5) * 10;
                    double y = center.y + mc.player.getRandom().nextDouble() * 2.5;
                    double z = center.z + (mc.player.getRandom().nextDouble() - 0.5) * 10;
                    AuraType aura = AuraHelper.randomBiomeAura(mc.level, pos, mc.player.getRandom());
                    double vx = 0, vy = 0.01, vz = 0;
                    if (ClientData.cycling) {
                        // Motes stream into the caster while cycling.
                        Vec3 dir = center.subtract(x, y, z).normalize().scale(0.08);
                        vx = dir.x;
                        vy = dir.y;
                        vz = dir.z;
                    }
                    mc.level.addParticle(aura.dust(0.8f), x, y, z, vx, vy, vz);
                }
            }

            if (time % 40 == 0) {
                // Ore veins glow with their aura — cap the markers to stay light.
                int found = 0;
                for (int dx = -6; dx <= 6 && found < 12; dx++) {
                    for (int dy = -6; dy <= 6 && found < 12; dy++) {
                        for (int dz = -6; dz <= 6 && found < 12; dz++) {
                            BlockPos p = pos.offset(dx, dy, dz);
                            AuraType aura = AuraHelper.getOreAura(mc.level.getBlockState(p).getBlock());
                            if (aura != null) {
                                mc.level.addParticle(aura.dust(1.0f),
                                        p.getX() + 0.5, p.getY() + 1.05, p.getZ() + 0.5,
                                        0, 0.02, 0);
                                found++;
                            }
                        }
                    }
                }
            }
        }

        /** Jade Sense: look at a living thing to read its spirit. */
        private static void tickSpiritScan(Minecraft mc, long time) {
            if (!ClientData.jadeSenseEnabled
                    || !Stage.byId(ClientData.stageId).atLeast(Stage.JADE)) return;
            if (time % 10 != 0) return;
            HitResult hit = mc.hitResult;
            if (!(hit instanceof EntityHitResult ehr)) {
                lastScannedId = -1;
                return;
            }
            int id = ehr.getEntity().getId();
            if (id != lastScannedId || time - lastScanTick >= 20) {
                lastScannedId = id;
                lastScanTick = time;
                ModNetwork.CHANNEL.sendToServer(new RequestSpiritScanPacket(id));
            }
        }
    }
}
