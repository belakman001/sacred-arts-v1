package com.sacredarts.technique;

import com.sacredarts.data.ISacredArtist;
import com.sacredarts.path.Path;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.util.List;

import static com.sacredarts.technique.TechniqueHelper.*;

/** Path of the White Fox — light + dream, illusion. */
public class WhiteFoxTechniques {
    public static List<Technique> create() {
        return List.of(
                new Technique("fox_fire", Path.WHITE_FOX, TechniqueType.STRIKER, 8) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        double p = data.getPowerMultiplier();
                        dragonSpray(player, 7, (float) (6 * p));
                        for (LivingEntity e : targetsInFront(player, 8, 2.5)) {
                            debuff(e, MobEffects.MOVEMENT_SLOWDOWN, (int) (4 * p), 0); // illusory terror
                        }
                    }
                },
                new Technique("fox_tail", Path.WHITE_FOX, TechniqueType.ENFORCER, 10) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        double p = data.getPowerMultiplier();
                        selfEffect(player, MobEffects.INVISIBILITY, (int) (8 * p), 0);
                        selfEffect(player, MobEffects.MOVEMENT_SPEED, (int) (10 * p), 1);
                    }
                },
                new Technique("fox_dream", Path.WHITE_FOX, TechniqueType.RULER, 14) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        List<LivingEntity> mobs = targetsAround(player, 8).stream()
                                .filter(e -> e instanceof Mob).toList();
                        for (LivingEntity e : mobs) {
                            // Turn the dream against them: each mob sees another as the threat.
                            LivingEntity other = mobs.get(player.getRandom().nextInt(mobs.size()));
                            if (other != e && other instanceof Mob) {
                                ((Mob) e).setTarget(other);
                            }
                        }
                        particles(player, net.minecraft.core.particles.ParticleTypes.WITCH, 4, 30);
                    }
                },
                new Technique("fox_mirror", Path.WHITE_FOX, TechniqueType.FORGER, 12) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        double p = data.getPowerMultiplier();
                        selfEffect(player, MobEffects.INVISIBILITY, (int) (6 * p), 0);
                        selfEffect(player, MobEffects.MOVEMENT_SPEED, (int) (10 * p), 0);
                        for (LivingEntity e : targetsAround(player, 8)) {
                            debuff(e, MobEffects.BLINDNESS, (int) (5 * p), 0);
                        }
                    }
                }
        );
    }

    public static Technique ultimate() {
        return new Technique("grand_illusion", Path.WHITE_FOX, TechniqueType.RULER, 40) {
            @Override public void cast(ServerPlayer player, ISacredArtist data) {
                double p = data.getPowerMultiplier();
                for (LivingEntity e : targetsAround(player, 16)) {
                    debuff(e, MobEffects.DARKNESS, (int) (10 * p), 0);
                    debuff(e, MobEffects.BLINDNESS, (int) (10 * p), 0);
                    debuff(e, MobEffects.MOVEMENT_SLOWDOWN, (int) (20 * p), 1);
                }
                particles(player, net.minecraft.core.particles.ParticleTypes.WITCH, 8, 60);
            }
        };
    }
}
