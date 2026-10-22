package com.sacredarts.technique;

import com.sacredarts.data.ISacredArtist;
import com.sacredarts.entity.BladeProjectileEntity;
import com.sacredarts.entity.FireFistEntity;
import com.sacredarts.entity.ForgedWispEntity;
import com.sacredarts.entity.ModEntities;
import com.sacredarts.entity.NeedleProjectileEntity;
import com.sacredarts.path.Path;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static com.sacredarts.technique.TechniqueHelper.*;

/** Path of the Hollow King — pure madra, nullification. */
public class HollowKingTechniques {
    /** Radius of The Hollow Domain. */
    public static final double DOMAIN_RADIUS = 20.0;
    /** Ticks targets inside the Domain cannot cast techniques (5 seconds). */
    public static final int DOMAIN_SILENCE_TICKS = 100;

    public static List<Technique> create() {
        return List.of(
                new Technique("the_hollow_spear", Path.HOLLOW_KING, TechniqueType.STRIKER, 10) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        double p = data.getPowerMultiplier();
                        // A white beacon-beam, fired twice in quick succession.
                        whiteBeam(player, 16, (float) (5 * p));
                        whiteBeam(player, 16, (float) (5 * p));
                    }
                },
                new Technique("hollow_kings_armor", Path.HOLLOW_KING, TechniqueType.ENFORCER, 12) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        double p = data.getPowerMultiplier();
                        int seconds = (int) (30 * p);
                        selfEffect(player, MobEffects.DAMAGE_RESISTANCE, seconds, 2);
                        // Spectral armor for the whole duration (tick handler).
                        data.setArmorTicks(seconds * 20);
                    }
                },
                new Technique("the_hollow_domain", Path.HOLLOW_KING, TechniqueType.RULER, 14) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        double p = data.getPowerMultiplier();
                        int seconds = (int) (15 * p);
                        // Slow and weaken everything in the Domain.
                        for (LivingEntity e : targetsAround(player, DOMAIN_RADIUS)) {
                            debuff(e, MobEffects.MOVEMENT_SLOWDOWN, seconds, 1);
                            debuff(e, MobEffects.WEAKNESS, seconds, 0);
                        }
                        // Clear active techniques and silence casting in the Domain.
                        clearTechniquesIn(player, DOMAIN_RADIUS);
                        // Pale expanding ring.
                        particles(player, net.minecraft.core.particles.ParticleTypes.END_ROD, 4, 60);
                        player.serverLevel().playSound(null, player.blockPosition(),
                                SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 1.0f, 0.8f);
                    }
                },
                new Technique("hollow_kings_crown", Path.HOLLOW_KING, TechniqueType.FORGER, 14) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        shootShulker(player, 5);
                    }
                }
        );
    }

    /** Clears active technique effects/entities in a radius and silences players (not the caster). */
    public static void clearTechniquesIn(ServerPlayer caster, double radius) {
        net.minecraft.server.level.ServerLevel level = caster.serverLevel();
        net.minecraft.world.phys.Vec3 center = caster.position();
        net.minecraft.world.phys.AABB box = new net.minecraft.world.phys.AABB(
                center.x - radius, center.y - 10, center.z - radius,
                center.x + radius, center.y + 10, center.z + radius);

        // Silence other sacred artists and wipe their in-flight technique state.
        for (ServerPlayer p : level.getEntitiesOfClass(ServerPlayer.class, box,
                p -> p != caster && p.isAlive())) {
            p.getCapability(com.sacredarts.data.ModCapabilities.SACRED_ARTIST).ifPresent(data -> {
                data.setStrikerChargeTicks(0);
                data.setFistChargeTicks(0);
                data.setCloakTicks(0);
                data.setSwordGlintTicks(0);
                data.setSwordAuraTicks(0);
                data.setArmorTicks(0);
                data.setSilenceTicks(DOMAIN_SILENCE_TICKS);
                p.displayClientMessage(
                        net.minecraft.network.chat.Component.translatable(
                                "message.sacredarts.silenced"), true);
            });
        }

        // Destroy technique projectiles/entities in the radius.
        for (net.minecraft.world.entity.Entity e : level.getEntitiesOfClass(
                net.minecraft.world.entity.Entity.class, box,
                e -> e instanceof ForgedWispEntity
                        || e instanceof FireFistEntity
                        || e instanceof BladeProjectileEntity
                        || e instanceof NeedleProjectileEntity)) {
            e.discard();
        }
    }

    public static Technique ultimate() {
        return new Technique("hollow_king_manifest", Path.HOLLOW_KING, TechniqueType.ENFORCER, 40) {
            @Override public void cast(ServerPlayer player, ISacredArtist data) {
                double p = data.getPowerMultiplier();
                selfEffect(player, MobEffects.DAMAGE_RESISTANCE, (int) (20 * p), 3);
                for (LivingEntity e : targetsAround(player, 10)) {
                    Vec3 away = e.position().subtract(player.position()).normalize();
                    e.setDeltaMovement(away.scale(2.5).add(0, 0.6, 0));
                    e.hurtMarked = true;
                }
                shootShulker(player, 8);
            }
        };
    }
}
