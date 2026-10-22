package com.sacredarts.technique;

import com.sacredarts.data.ISacredArtist;
import com.sacredarts.entity.ModEntities;
import com.sacredarts.entity.NeedleProjectileEntity;
import com.sacredarts.path.Path;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

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
                            shootNeedle(player, (float) (4 * p),
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
                            shootNeedle(player, (float) (3 * p),
                                    new MobEffectInstance(MobEffects.POISON, (int) (160 * p), 0));
                        }
                    }
                }
        );
    }

    /** Fires a bright toxic-green needle. */
    private static void shootNeedle(ServerPlayer player, float damage, MobEffectInstance poison) {
        NeedleProjectileEntity needle = new NeedleProjectileEntity(
                ModEntities.NEEDLE.get(), player, player.serverLevel());
        needle.setDamage(damage);
        needle.setPoison(poison);
        needle.setNoGravity(true);
        Vec3 dir = player.getLookAngle();
        // Slight spread.
        Vec3 side = new Vec3(-dir.z, 0, dir.x).normalize();
        double spread = (player.getRandom().nextDouble() - 0.5) * 0.12;
        Vec3 v = dir.add(side.scale(spread)).add(0, (player.getRandom().nextDouble() - 0.5) * 0.05, 0)
                .normalize().scale(2.8);
        needle.setDeltaMovement(v);
        needle.hurtMarked = true;
        player.serverLevel().addFreshEntity(needle);
        player.serverLevel().playSound(null, player.blockPosition(),
                SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0f, 1.4f);
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
