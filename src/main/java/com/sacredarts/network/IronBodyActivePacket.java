package com.sacredarts.network;

import com.sacredarts.data.IronBody;
import com.sacredarts.data.ModCapabilities;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Vector3f;

import java.util.function.Supplier;

/**
 * Client -> server: trigger the player's Iron Body active (F key).
 * Bloodforged — Blood Surge: heal. Raindrop — Rain Step: dash.
 * Oakheart — Deep Roots: brief knockback immunity + resistance.
 */
public class IronBodyActivePacket {
    public IronBodyActivePacket() {}

    public IronBodyActivePacket(FriendlyByteBuf buf) {}

    public void encode(FriendlyByteBuf buf) {}

    public static void handle(IronBodyActivePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            player.getCapability(ModCapabilities.SACRED_ARTIST).ifPresent(data -> {
                if (!data.isEnrolled() || data.getIronBody() == IronBody.NONE) return;
                if (data.getIronBodyCooldownTicks() > 0) {
                    player.displayClientMessage(
                            Component.translatable("message.sacredarts.iron_body_cooldown"), true);
                    return;
                }
                switch (data.getIronBody()) {
                    case BLOODFORGED -> {
                        if (!data.spendMadra(15)) {
                            player.displayClientMessage(
                                    Component.translatable("message.sacredarts.no_madra"), true);
                            return;
                        }
                        player.heal(6.0f);
                        data.setIronBodyCooldownTicks(300);
                        burst(player, new DustParticleOptions(new Vector3f(1.0f, 0.1f, 0.1f), 1.2f));
                        player.serverLevel().playSound(null, player.blockPosition(),
                                SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0f, 0.8f);
                    }
                    case RAINDROP -> {
                        if (!data.spendMadra(10)) {
                            player.displayClientMessage(
                                    Component.translatable("message.sacredarts.no_madra"), true);
                            return;
                        }
                        Vec3 look = player.getLookAngle();
                        player.setDeltaMovement(look.scale(1.6).add(0, 0.25, 0));
                        player.hurtMarked = true;
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, 1, false, false, true));
                        data.setIronBodyCooldownTicks(160);
                        burst(player, new DustParticleOptions(new Vector3f(0.4f, 0.7f, 1.0f), 1.0f));
                        player.serverLevel().playSound(null, player.blockPosition(),
                                SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0f, 1.2f);
                    }
                    case OAKHEART -> {
                        if (!data.spendMadra(10)) {
                            player.displayClientMessage(
                                    Component.translatable("message.sacredarts.no_madra"), true);
                            return;
                        }
                        data.setOakheartRootTicks(120);
                        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 120, 1, false, false, true));
                        data.setIronBodyCooldownTicks(300);
                        burst(player, new DustParticleOptions(new Vector3f(0.55f, 0.4f, 0.2f), 1.0f));
                        player.serverLevel().playSound(null, player.blockPosition(),
                                SoundEvents.WOOD_PLACE, SoundSource.PLAYERS, 1.0f, 0.8f);
                    }
                    default -> {
                        return;
                    }
                }
                ModNetwork.syncPlayer(player);
            });
        });
        ctx.get().setPacketHandled(true);
    }

    private static void burst(ServerPlayer player, DustParticleOptions particle) {
        Vec3 p = player.position().add(0, 1, 0);
        for (int i = 0; i < 20; i++) {
            double a = player.getRandom().nextDouble() * Math.PI * 2;
            double r = 0.6 + player.getRandom().nextDouble() * 0.4;
            player.serverLevel().sendParticles(particle,
                    p.x + Math.cos(a) * r, p.y + (player.getRandom().nextDouble() - 0.5) * 1.5,
                    p.z + Math.sin(a) * r, 1, 0, 0, 0, 0);
        }
    }
}
