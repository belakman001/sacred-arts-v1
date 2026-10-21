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
                new Technique("venom_spit", Path.SANDVIPER, TechniqueType.STRIKER, 10, 0) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        for (int i = 0; i < 2; i++) {
                            shootArrow(player, 4, new MobEffectInstance(MobEffects.POISON, 200, 0));
                        }
                    }
                },
                new Technique("serpents_swiftness", Path.SANDVIPER, TechniqueType.ENFORCER, 10, 10) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        selfEffect(player, MobEffects.MOVEMENT_SPEED, 20, 1);
                        selfEffect(player, MobEffects.JUMP, 20, 0);
                    }
                },
                new Technique("miasma_cloud", Path.SANDVIPER, TechniqueType.RULER, 14, 25) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        poisonCloud(player, 12, 3.0f, 20, 1);
                    }
                },
                new Technique("venom_needles", Path.SANDVIPER, TechniqueType.FORGER, 12, 40) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        for (int i = 0; i < 5; i++) {
                            shootArrow(player, 3, new MobEffectInstance(MobEffects.POISON, 160, 0));
                        }
                    }
                }
        );
    }

    public static Technique ultimate() {
        return new Technique("venom_tide", Path.SANDVIPER, TechniqueType.RULER, 40, 120) {
            @Override public void cast(ServerPlayer player, ISacredArtist data) {
                for (LivingEntity e : targetsAround(player, 8)) {
                    hurt(e, player, 10);
                    debuff(e, MobEffects.POISON, 15, 1);
                }
                poisonCloud(player, 2, 5.0f, 15, 1);
                particles(player, net.minecraft.core.particles.ParticleTypes.ENTITY_EFFECT, 4, 40);
            }
        };
    }
}
