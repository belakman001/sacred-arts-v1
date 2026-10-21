package com.sacredarts.technique;

import com.sacredarts.data.ISacredArtist;
import com.sacredarts.path.Path;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

import static com.sacredarts.technique.TechniqueHelper.*;

/** Path of the Sandviper — poison. */
public class SandviperTechniques {
    public static List<Technique> create() {
        return List.of(
                new Technique("venom_spit", Path.SANDVIPER, TechniqueType.STRIKER, 10) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        double p = data.getPowerMultiplier();
                        for (int i = 0; i < 2; i++) {
                            shootArrow(player, (float) (4 * p),
                                    new MobEffectInstance(MobEffects.POISON, (int) (200 * p), 0));
                        }
                    }
                },
                new Technique("serpents_swiftness", Path.SANDVIPER, TechniqueType.ENFORCER, 10) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        double p = data.getPowerMultiplier();
                        selfEffect(player, MobEffects.MOVEMENT_SPEED, (int) (20 * p), 1);
                        selfEffect(player, MobEffects.JUMP, (int) (20 * p), 0);
                    }
                },
                new Technique("miasma_cloud", Path.SANDVIPER, TechniqueType.RULER, 14) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        double p = data.getPowerMultiplier();
                        poisonCloud(player, 12, 3.0f, (int) (20 * p), 1);
                    }
                },
                new Technique("venom_needles", Path.SANDVIPER, TechniqueType.FORGER, 12) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        double p = data.getPowerMultiplier();
                        for (int i = 0; i < 5; i++) {
                            shootArrow(player, (float) (3 * p),
                                    new MobEffectInstance(MobEffects.POISON, (int) (160 * p), 0));
                        }
                    }
                }
        );
    }

    public static Technique ultimate() {
        return new Technique("venom_tide", Path.SANDVIPER, TechniqueType.RULER, 40) {
            @Override public void cast(ServerPlayer player, ISacredArtist data) {
                double p = data.getPowerMultiplier();
                for (LivingEntity e : targetsAround(player, 8)) {
                    hurt(e, player, (float) (10 * p));
                    debuff(e, MobEffects.POISON, (int) (15 * p), 1);
                }
                poisonCloud(player, 2, 5.0f, (int) (15 * p), 1);
                particles(player, net.minecraft.core.particles.ParticleTypes.ENTITY_EFFECT, 4, 40);
            }
        };
    }
}
