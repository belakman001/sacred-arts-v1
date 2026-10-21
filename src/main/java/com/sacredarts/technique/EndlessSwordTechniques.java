package com.sacredarts.technique;

import com.sacredarts.data.ISacredArtist;
import com.sacredarts.path.Path;
import net.minecraft.server.level.ServerPlayer;
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
                        for (int i = 0; i < 3; i++) shootSpectral(player, (float) (6 * p));
                    }
                },
                new Technique("flowing_sword", Path.ENDLESS_SWORD, TechniqueType.ENFORCER, 10) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        double p = data.getPowerMultiplier();
                        selfEffect(player, MobEffects.MOVEMENT_SPEED, (int) (20 * p), 1);
                    }
                },
                new Technique("sword_aura_field", Path.ENDLESS_SWORD, TechniqueType.RULER, 14) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        double p = data.getPowerMultiplier();
                        for (LivingEntity e : targetsAround(player, 6)) {
                            hurt(e, player, (float) (5 * p));
                        }
                        particles(player, net.minecraft.core.particles.ParticleTypes.CRIT, 3, 30);
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
