package com.sacredarts.technique;

import com.sacredarts.data.ISacredArtist;
import com.sacredarts.entity.BladeProjectileEntity;
import com.sacredarts.entity.ModEntities;
import com.sacredarts.path.Path;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static com.sacredarts.technique.TechniqueHelper.*;

/** Path of the Endless Sword — sword madra. */
public class EndlessSwordTechniques {
    public static List<Technique> create() {
        return List.of(
                new Technique("rippling_sword", Path.ENDLESS_SWORD, TechniqueType.STRIKER, 10) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        double p = data.getPowerMultiplier();
                        // Throw three blades (flying iron swords).
                        for (int i = 0; i < 3; i++) {
                            BladeProjectileEntity blade = new BladeProjectileEntity(
                                    ModEntities.BLADE.get(), player, player.serverLevel());
                            blade.setDamage((float) (6 * p));
                            blade.setNoGravity(true);
                            Vec3 dir = player.getLookAngle();
                            // Slight spread so they fan out.
                            double spread = (i - 1) * 0.08;
                            Vec3 v = dir.add(new Vec3(-dir.z, 0, dir.x).normalize().scale(spread))
                                    .normalize().scale(2.2);
                            blade.setDeltaMovement(v);
                            blade.hurtMarked = true;
                            player.serverLevel().addFreshEntity(blade);
                        }
                        player.serverLevel().playSound(null, player.blockPosition(),
                                SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0f, 1.2f);
                    }
                },
                new Technique("flowing_sword", Path.ENDLESS_SWORD, TechniqueType.ENFORCER, 10) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        double p = data.getPowerMultiplier();
                        int seconds = (int) (20 * p);
                        selfEffect(player, MobEffects.MOVEMENT_SPEED, seconds, 1);
                        // Silver glint for the whole duration (tick handler).
                        data.setSwordGlintTicks(seconds * 20);
                    }
                },
                new Technique("sword_aura_field", Path.ENDLESS_SWORD, TechniqueType.RULER, 14) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        double p = data.getPowerMultiplier();
                        // For a time, every weapon near the caster strikes on its own.
                        data.setSwordAuraTicks((int) (10 * p) * 20);
                        player.serverLevel().playSound(null, player.blockPosition(),
                                SoundEvents.BLOCK_BELL_RESONATE, SoundSource.PLAYERS, 0.6f, 1.6f);
                    }
                },
                new Technique("hidden_sword", Path.ENDLESS_SWORD, TechniqueType.FORGER, 12) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        double p = data.getPowerMultiplier();
                        selfEffect(player, MobEffects.INVISIBILITY, (int) (10 * p), 0);
                        selfEffect(player, MobEffects.DAMAGE_BOOST, (int) (15 * p), 0);
                    }
                }
        );
    }

    public static Technique ultimate() {
        return new Technique("final_sword", Path.ENDLESS_SWORD, TechniqueType.STRIKER, 40) {
            @Override public void cast(ServerPlayer player, ISacredArtist data) {
                double p = data.getPowerMultiplier();
                for (LivingEntity e : targetsInFront(player, 6, 2)) {
                    hurt(e, player, (float) (24 * p));
                    Vec3 away = e.position().subtract(player.position()).normalize();
                    e.setDeltaMovement(away.scale(2).add(0, 0.5, 0));
                    e.hurtMarked = true;
                }
                particles(player, net.minecraft.core.particles.ParticleTypes.SWEEP_ATTACK, 3, 20);
            }
        };
    }
}
