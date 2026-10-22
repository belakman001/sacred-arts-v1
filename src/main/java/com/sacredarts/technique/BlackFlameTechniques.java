package com.sacredarts.technique;

import com.sacredarts.data.ISacredArtist;
import com.sacredarts.entity.FireFistEntity;
import com.sacredarts.entity.ForgedWispEntity;
import com.sacredarts.entity.ModEntities;
import com.sacredarts.path.Path;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static com.sacredarts.technique.TechniqueHelper.*;

/** Path of Black Flame — fire + destruction. */
public class BlackFlameTechniques {
    /** Ticks the striker's black ball charges in the hand before the beam fires. */
    public static final int STRIKER_CHARGE_TICKS = 25;
    /** Ticks the fire fist charges in the hand before it shoots forward. */
    public static final int FIST_CHARGE_TICKS = 20;

    public static List<Technique> create() {
        return List.of(
                new Technique("dragons_breath", Path.BLACK_FLAME, TechniqueType.STRIKER, 12) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        double p = data.getPowerMultiplier();
                        // Charge a black ball in the hand; the beam fires when the
                        // charge completes (handled in AdvancementHandler.tick).
                        data.setStrikerChargeDamage((float) (8 * p));
                        data.setStrikerChargeTicks(STRIKER_CHARGE_TICKS);
                        player.serverLevel().playSound(null, player.blockPosition(),
                                net.minecraft.sounds.SoundEvents.BLAZE_SHOOT,
                                net.minecraft.sounds.SoundSource.PLAYERS, 0.7f, 0.5f);
                    }
                },
                new Technique("burning_cloak", Path.BLACK_FLAME, TechniqueType.ENFORCER, 10) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        double p = data.getPowerMultiplier();
                        int seconds = (int) (20 * p);
                        selfEffect(player, MobEffects.MOVEMENT_SPEED, seconds, 1);
                        for (LivingEntity e : targetsAround(player, 4)) {
                            e.setSecondsOnFire((int) (4 * p));
                        }
                        // Black + orange flames for the whole boost (tick handler).
                        data.setCloakTicks(seconds * 20);
                    }
                },
                new Technique("void_dragons_dance", Path.BLACK_FLAME, TechniqueType.RULER, 14) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        double p = data.getPowerMultiplier();
                        // Forge a wisp overhead: it builds for 3s, then hunts
                        // whoever the caster is looking at.
                        ForgedWispEntity wisp = new ForgedWispEntity(
                                ModEntities.FORGED_WISP.get(), player.serverLevel());
                        wisp.setOwner(player);
                        wisp.setDamage((float) (10 * p));
                        Vec3 pos = player.position().add(0, 3, 0);
                        wisp.setPos(pos.x, pos.y, pos.z);
                        player.serverLevel().addFreshEntity(wisp);
                        player.serverLevel().playSound(null, player.blockPosition(),
                                net.minecraft.sounds.SoundEvents.BLAZE_SHOOT,
                                net.minecraft.sounds.SoundSource.PLAYERS, 0.8f, 0.7f);
                    }
                },
                new Technique("cinder_claws", Path.BLACK_FLAME, TechniqueType.FORGER, 10) {
                    @Override public void cast(ServerPlayer player, ISacredArtist data) {
                        double p = data.getPowerMultiplier();
                        // Charge a fire fist in the hand; it shoots forward when the
                        // charge completes (handled in AdvancementHandler.tick).
                        data.setFistChargeDamage((float) (10 * p));
                        data.setFistChargeTicks(FIST_CHARGE_TICKS);
                        player.serverLevel().playSound(null, player.blockPosition(),
                                net.minecraft.sounds.SoundEvents.BLAZE_SHOOT,
                                net.minecraft.sounds.SoundSource.PLAYERS, 0.7f, 0.6f);
                    }
                }
        );
    }

    public static Technique ultimate() {
        return new Technique("the_dragon_descends", Path.BLACK_FLAME, TechniqueType.FORGER, 40) {
            @Override public void cast(ServerPlayer player, ISacredArtist data) {
                double p = data.getPowerMultiplier();
                blackBurst(player, 8, (float) (20 * p));
                for (LivingEntity e : targetsAround(player, 8)) {
                    e.setSecondsOnFire((int) (10 * p));
                }
            }
        };
    }

    /** Fires the charged fire fist. Called from AdvancementHandler.tick. */
    public static void fireFist(ServerPlayer player, ISacredArtist data) {
        FireFistEntity fist = new FireFistEntity(
                ModEntities.FIRE_FIST.get(), player.serverLevel());
        fist.setOwner(player);
        fist.setDamage(data.getFistChargeDamage());
        Vec3 eye = player.getEyePosition();
        Vec3 dir = player.getLookAngle();
        Vec3 start = eye.add(dir.scale(1.0));
        fist.setPos(start.x, start.y - 0.3, start.z);
        fist.setDirection(dir);
        player.serverLevel().addFreshEntity(fist);
    }
}
