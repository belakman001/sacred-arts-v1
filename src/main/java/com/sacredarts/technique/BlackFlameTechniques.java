package com.sacredarts.technique;

import com.sacredarts.data.ISacredArtist;
import com.sacredarts.path.Path;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

import static com.sacredarts.technique.TechniqueHelper.*;

/** Path of Black Flame — fire + destruction. */
public class BlackFlameTechniques {
    public static List<Technique> create() {
        return List.of(
                new Technique("dragons_breath", Path.BLACK_FLAME, TechniqueType.STRIKER, 12) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        double p = data.getPowerMultiplier();
                        beam(player, 16, (float) (8 * p), true);
                    }
                },
                new Technique("burning_cloak", Path.BLACK_FLAME, TechniqueType.ENFORCER, 10) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        double p = data.getPowerMultiplier();
                        selfEffect(player, MobEffects.MOVEMENT_SPEED, (int) (20 * p), 1);
                        for (LivingEntity e : targetsAround(player, 4)) {
                            e.setSecondsOnFire((int) (4 * p));
                        }
                        particles(player, net.minecraft.core.particles.ParticleTypes.FLAME, 1.5, 20);
                    }
                },
                new Technique("void_dragons_dance", Path.BLACK_FLAME, TechniqueType.RULER, 14) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        double p = data.getPowerMultiplier();
                        for (LivingEntity e : targetsAround(player, 6)) {
                            hurt(e, player, (float) (6 * p));
                            e.setSecondsOnFire((int) (6 * p));
                        }
                        particles(player, net.minecraft.core.particles.ParticleTypes.FLAME, 3, 40);
                    }
                },
                new Technique("cinder_claws", Path.BLACK_FLAME, TechniqueType.FORGER, 10) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        double p = data.getPowerMultiplier();
                        selfEffect(player, MobEffects.DAMAGE_BOOST, (int) (30 * p), 1);
                    }
                }
        );
    }

    public static Technique ultimate() {
        return new Technique("the_dragon_descends", Path.BLACK_FLAME, TechniqueType.FORGER, 40) {
            @Override public void cast(ServerPlayer player, ISacredArtist data) {
                double p = data.getPowerMultiplier();
                burst(player, 8, (float) (20 * p));
                for (LivingEntity e : targetsAround(player, 8)) {
                    e.setSecondsOnFire((int) (10 * p));
                }
            }
        };
    }
}
