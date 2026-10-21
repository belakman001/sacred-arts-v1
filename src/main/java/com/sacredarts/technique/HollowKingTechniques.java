package com.sacredarts.technique;

import com.sacredarts.data.ISacredArtist;
import com.sacredarts.path.Path;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static com.sacredarts.technique.TechniqueHelper.*;

/** Path of the Hollow King — pure madra, nullification. */
public class HollowKingTechniques {
    public static List<Technique> create() {
        return List.of(
                new Technique("hollow_bolt", Path.HOLLOW_KING, TechniqueType.STRIKER, 10, 0) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        shootSpectral(player, 5);
                        shootSpectral(player, 5);
                    }
                },
                new Technique("hollow_kings_armor", Path.HOLLOW_KING, TechniqueType.ENFORCER, 12, 10) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        selfEffect(player, MobEffects.DAMAGE_RESISTANCE, 30, 2);
                    }
                },
                new Technique("still_air", Path.HOLLOW_KING, TechniqueType.RULER, 14, 25) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        for (LivingEntity e : targetsAround(player, 8)) {
                            debuff(e, MobEffects.MOVEMENT_SLOWDOWN, 15, 1);
                            debuff(e, MobEffects.WEAKNESS, 15, 0);
                        }
                        particles(player, net.minecraft.core.particles.ParticleTypes.END_ROD, 4, 30);
                    }
                },
                new Technique("hollow_kings_crown", Path.HOLLOW_KING, TechniqueType.FORGER, 14, 40) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        shootShulker(player, 5);
                    }
                }
        );
    }

    public static Technique ultimate() {
        return new Technique("hollow_king_manifest", Path.HOLLOW_KING, TechniqueType.ENFORCER, 40, 120) {
            @Override public void cast(ServerPlayer player, ISacredArtist data) {
                selfEffect(player, MobEffects.DAMAGE_RESISTANCE, 20, 3);
                for (LivingEntity e : targetsAround(player, 10)) {
                    Vec3 away = e.position().subtract(player.position()).normalize();
                    e.setDeltaMovement(away.scale(2.5).add(0, 0.6, 0));
                    e.hurtMarked = true;
                }
                shootShulker(player, 8);
            }
        };
    }
}
