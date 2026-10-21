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
                new Technique("rippling_sword", Path.ENDLESS_SWORD, TechniqueType.STRIKER, 10, 0) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        for (int i = 0; i < 3; i++) shootSpectral(player, 6);
                    }
                },
                new Technique("flowing_sword", Path.ENDLESS_SWORD, TechniqueType.ENFORCER, 10, 10) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        selfEffect(player, MobEffects.MOVEMENT_SPEED, 20, 1);
                    }
                },
                new Technique("sword_aura_field", Path.ENDLESS_SWORD, TechniqueType.RULER, 14, 25) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        for (LivingEntity e : targetsAround(player, 6)) {
                            hurt(e, player, 5);
                        }
                        particles(player, net.minecraft.core.particles.ParticleTypes.CRIT, 3, 30);
                    }
                },
                new Technique("hidden_sword", Path.ENDLESS_SWORD, TechniqueType.FORGER, 12, 40) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        selfEffect(player, MobEffects.INVISIBILITY, 10, 0);
                        selfEffect(player, MobEffects.DAMAGE_BOOST, 15, 0);
                    }
                }
        );
    }

    public static Technique ultimate() {
        return new Technique("final_sword", Path.ENDLESS_SWORD, TechniqueType.STRIKER, 40, 120) {
            @Override public void cast(ServerPlayer player, ISacredArtist data) {
                for (LivingEntity e : targetsInFront(player, 6, 2)) {
                    hurt(e, player, 24);
                    Vec3 away = e.position().subtract(player.position()).normalize();
                    e.setDeltaMovement(away.scale(2).add(0, 0.5, 0));
                    e.hurtMarked = true;
                }
                particles(player, net.minecraft.core.particles.ParticleTypes.SWEEP_ATTACK, 3, 20);
            }
        };
    }
}
