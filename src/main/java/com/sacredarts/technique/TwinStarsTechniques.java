package com.sacredarts.technique;

import com.sacredarts.data.ISacredArtist;
import com.sacredarts.path.Path;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static com.sacredarts.technique.TechniqueHelper.*;

/** Path of Twin Stars — pure madra, twin cores. */
public class TwinStarsTechniques {
    public static List<Technique> create() {
        return List.of(
                new Technique("empty_palm", Path.TWIN_STARS, TechniqueType.STRIKER, 10) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        double p = data.getPowerMultiplier();
                        for (LivingEntity e : targetsInFront(player, 5, 1.5)) {
                            hurt(e, player, (float) (4 * p));
                            debuff(e, MobEffects.WEAKNESS, (int) (10 * p), 1);
                            debuff(e, MobEffects.MOVEMENT_SLOWDOWN, (int) (5 * p), 0);
                        }
                        particles(player, net.minecraft.core.particles.ParticleTypes.END_ROD, 1.5, 15);
                    }
                },
                new Technique("soul_cloak", Path.TWIN_STARS, TechniqueType.ENFORCER, 10) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        double p = data.getPowerMultiplier();
                        selfEffect(player, MobEffects.NIGHT_VISION, (int) (60 * p), 0);
                        selfEffect(player, MobEffects.MOVEMENT_SPEED, (int) (20 * p), 0);
                    }
                },
                new Technique("hollow_domain", Path.TWIN_STARS, TechniqueType.RULER, 16) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        double p = data.getPowerMultiplier();
                        double radius = HollowKingTechniques.DOMAIN_RADIUS;
                        int seconds = (int) (15 * p);
                        // Weaken and slow everything in the Domain.
                        for (LivingEntity e : targetsAround(player, radius)) {
                            debuff(e, MobEffects.WEAKNESS, seconds, 0);
                            debuff(e, MobEffects.MOVEMENT_SLOWDOWN, seconds, 0);
                        }
                        // Like the Hollow King's Domain: clear techniques and silence.
                        HollowKingTechniques.clearTechniquesIn(player, radius);
                        // White particles marking the affected area.
                        spawnDomainParticles(player, radius);
                        player.serverLevel().playSound(null, player.blockPosition(),
                                SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 1.0f, 1.0f);
                    }
                },
                new Technique("twin_barrier", Path.TWIN_STARS, TechniqueType.FORGER, 12) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        double p = data.getPowerMultiplier();
                        selfEffect(player, MobEffects.DAMAGE_RESISTANCE, (int) (30 * p), 1);
                        selfEffect(player, MobEffects.ABSORPTION, (int) (30 * p), 1);
                    }
                }
        );
    }

    /** White particles marking the Domain's affected area: a ground ring plus drifting motes. */
    private static void spawnDomainParticles(ServerPlayer player, double radius) {
        net.minecraft.server.level.ServerLevel level = player.serverLevel();
        Vec3 center = player.position();
        // Ground ring.
        int ringCount = 48;
        for (int i = 0; i < ringCount; i++) {
            double a = (Math.PI * 2 * i) / ringCount;
            double x = center.x + Math.cos(a) * radius;
            double z = center.z + Math.sin(a) * radius;
            level.sendParticles(net.minecraft.core.particles.ParticleTypes.END_ROD,
                    x, center.y + 0.2, z, 1, 0.1, 0.1, 0.1, 0.01);
        }
        // Drifting white motes throughout the volume.
        for (int i = 0; i < 40; i++) {
            double a = player.getRandom().nextDouble() * Math.PI * 2;
            double r = Math.sqrt(player.getRandom().nextDouble()) * radius;
            double x = center.x + Math.cos(a) * r;
            double z = center.z + Math.sin(a) * r;
            double y = center.y + player.getRandom().nextDouble() * 4;
            level.sendParticles(net.minecraft.core.particles.ParticleTypes.WHITE_ASH,
                    x, y, z, 1, 0.05, 0.05, 0.05, 0.01);
        }
    }

    public static Technique ultimate() {
        return new Technique("twin_stars_collision", Path.TWIN_STARS, TechniqueType.STRIKER, 40) {            @Override public void cast(ServerPlayer player, ISacredArtist data) {
                double p = data.getPowerMultiplier();
                burst(player, 8, (float) (16 * p));
                for (LivingEntity e : targetsAround(player, 8)) {
                    Vec3 v = e.getDeltaMovement().add(0, 1.2, 0);
                    e.setDeltaMovement(v);
                    e.hurtMarked = true;
                }
            }
        };
    }
}
