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
                new Technique("dragons_breath", Path.BLACK_FLAME, TechniqueType.STRIKER, 12, 0) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        beam(player, 16, 8, true);
                    }
                },
                new Technique("burning_cloak", Path.BLACK_FLAME, TechniqueType.ENFORCER, 10, 10) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        selfEffect(player, MobEffects.MOVEMENT_SPEED, 20, 1);
                        for (LivingEntity e : targetsAround(player, 4)) {
                            e.setSecondsOnFire(4);
                        }
                        particles(player, net.minecraft.core.particles.ParticleTypes.FLAME, 1.5, 20);
                    }
                },
                new Technique("void_dragons_dance", Path.BLACK_FLAME, TechniqueType.RULER, 14, 25) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        for (LivingEntity e : targetsAround(player, 6)) {
                            hurt(e, player, 6);
                            e.setSecondsOnFire(6);
                        }
                        particles(player, net.minecraft.core.particles.ParticleTypes.FLAME, 3, 40);
                    }
                },
                new Technique("cinder_claws", Path.BLACK_FLAME, TechniqueType.FORGER, 10, 40) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        selfEffect(player, MobEffects.DAMAGE_BOOST, 30, 1);
                    }
                }
        );
    }

    public static Technique ultimate() {
        return new Technique("the_dragon_descends", Path.BLACK_FLAME, TechniqueType.FORGER, 40, 120) {
            @Override public void cast(ServerPlayer player, ISacredArtist data) {
                burst(player, 8, 20);
                for (LivingEntity e : targetsAround(player, 8)) {
                    e.setSecondsOnFire(10);
                }
            }
        };
    }
}
