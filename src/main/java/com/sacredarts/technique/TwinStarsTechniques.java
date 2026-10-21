package com.sacredarts.technique;

import com.sacredarts.data.ISacredArtist;
import com.sacredarts.path.Path;
import net.minecraft.server.level.ServerPlayer;
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
                        for (LivingEntity e : targetsAround(player, 8)) {
                            debuff(e, MobEffects.WEAKNESS, (int) (15 * p), 0);
                            debuff(e, MobEffects.MOVEMENT_SLOWDOWN, (int) (15 * p), 0);
                        }
                        particles(player, net.minecraft.core.particles.ParticleTypes.END_ROD, 4, 40);
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

    public static Technique ultimate() {
        return new Technique("twin_stars_collision", Path.TWIN_STARS, TechniqueType.STRIKER, 40) {
            @Override public void cast(ServerPlayer player, ISacredArtist data) {
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
