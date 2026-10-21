package com.sacredarts.technique;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/** Shared server-side building blocks for technique implementations. */
public final class TechniqueHelper {
    private TechniqueHelper() {}

    public static List<LivingEntity> targetsAround(ServerPlayer player, double radius) {
        Vec3 pos = player.position();
        AABB box = new AABB(pos.x - radius, pos.y - radius, pos.z - radius,
                pos.x + radius, pos.y + radius, pos.z + radius);
        return player.level().getEntitiesOfClass(LivingEntity.class, box,
                e -> e != player && e.isAlive() && e.distanceTo(player) <= radius);
    }

    public static List<LivingEntity> targetsInFront(ServerPlayer player, double range, double halfWidth) {
        Vec3 eye = player.getEyePosition();
        Vec3 end = eye.add(player.getLookAngle().scale(range));
        AABB box = new AABB(eye, end).inflate(halfWidth);
        return player.level().getEntitiesOfClass(LivingEntity.class, box,
                e -> e != player && e.isAlive());
    }

    public static void hurt(LivingEntity target, ServerPlayer src, float amount) {
        target.hurt(src.damageSources().indirectMagic(src, src), amount);
    }

    /** Damages everything along the look vector, with flame particles. */
    public static void beam(ServerPlayer player, double range, float damage, boolean ignite) {
        ServerLevel level = player.serverLevel();
        Vec3 eye = player.getEyePosition();
        Vec3 dir = player.getLookAngle();
        for (double d = 1.0; d <= range; d += 0.75) {
            Vec3 p = eye.add(dir.scale(d));
            level.sendParticles(ParticleTypes.FLAME, p.x, p.y, p.z, 3, 0.2, 0.2, 0.2, 0.02);
            AABB box = new AABB(p.x - 1, p.y - 1, p.z - 1, p.x + 1, p.y + 1, p.z + 1);
            for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box,
                    e -> e != player && e.isAlive())) {
                hurt(e, player, damage);
                if (ignite) e.setSecondsOnFire(5);
            }
        }
        level.playSound(null, player.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0f, 0.8f);
    }

    /** Radial burst with explosion particles (no block damage). */
    public static void burst(ServerPlayer player, double radius, float damage) {
        ServerLevel level = player.serverLevel();
        Vec3 pos = player.position().add(0, 1, 0);
        level.sendParticles(ParticleTypes.EXPLOSION, pos.x, pos.y, pos.z, 12, 1.5, 1.0, 1.5, 0.05);
        for (LivingEntity e : targetsAround(player, radius)) {
            hurt(e, player, damage);
        }
        level.playSound(null, player.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.8f, 1.2f);
    }

    public static void shootArrow(ServerPlayer player, float damage, MobEffectInstance... effects) {
        Arrow arrow = new Arrow(player.level(), player);
        arrow.setBaseDamage(damage);
        for (MobEffectInstance e : effects) arrow.addEffect(e);
        arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0f, 3.0f, 1.0f);
        player.level().addFreshEntity(arrow);
        player.level().playSound(null, player.blockPosition(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    public static void shootSpectral(ServerPlayer player, float damage) {
        SpectralArrow arrow = new SpectralArrow(player.level(), player);
        arrow.setBaseDamage(damage);
        arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0f, 3.0f, 1.0f);
        player.level().addFreshEntity(arrow);
        player.level().playSound(null, player.blockPosition(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0f, 1.2f);
    }

    public static void shootFireball(ServerPlayer player) {
        Vec3 eye = player.getEyePosition();
        Vec3 dir = player.getLookAngle();
        SmallFireball fb = new SmallFireball(player.level(), player, dir.x, dir.y, dir.z);
        fb.setPos(eye.x, eye.y, eye.z);
        player.level().addFreshEntity(fb);
        player.level().playSound(null, player.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0f, 1.4f);
    }

    /** Homing spirit-stars; falls back to unguided if no target in sight. */
    public static void shootShulker(ServerPlayer player, int count) {
        List<LivingEntity> targets = targetsInFront(player, 24, 4);
        if (targets.isEmpty()) targets = targetsAround(player, 12);
        for (int i = 0; i < count; i++) {
            LivingEntity target = targets.isEmpty() ? null : targets.get(i % targets.size());
            ShulkerBullet bullet = new ShulkerBullet(player.level(), player, target, Direction.Axis.Y);
            Vec3 eye = player.getEyePosition();
            bullet.setPos(eye.x, eye.y - 0.5 + (i * 0.3), eye.z);
            player.level().addFreshEntity(bullet);
        }
        player.level().playSound(null, player.blockPosition(), SoundEvents.SHULKER_SHOOT, SoundSource.PLAYERS, 1.0f, 1.2f);
    }

    /** Cone of ender-dragon fire: dragon breath particles + damage along the look vector. */
    public static void dragonSpray(ServerPlayer player, double range, float damage) {
        ServerLevel level = player.serverLevel();
        Vec3 eye = player.getEyePosition();
        Vec3 dir = player.getLookAngle();
        for (double d = 1.0; d <= range; d += 0.5) {
            double spread = 0.15 + d * 0.12;
            Vec3 p = eye.add(dir.scale(d));
            level.sendParticles(ParticleTypes.DRAGON_BREATH, p.x, p.y, p.z, 4, spread, spread, spread, 0.02);
            AABB box = new AABB(p.x - 0.8, p.y - 0.8, p.z - 0.8, p.x + 0.8, p.y + 0.8, p.z + 0.8);
            for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box,
                    e -> e != player && e.isAlive())) {
                hurt(e, player, damage);
            }
        }
        level.playSound(null, player.blockPosition(), SoundEvents.ENTITY_ENDER_DRAGON_SHOOT,
                SoundSource.PLAYERS, 0.8f, 1.2f);
    }

    /** Lingering poison cloud at the aimed point. */
    public static void poisonCloud(ServerPlayer player, double range, float radius, int seconds, int amplifier) {
        Vec3 eye = player.getEyePosition();
        Vec3 end = eye.add(player.getLookAngle().scale(range));
        BlockHitResult hit = player.level().clip(
                new ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        Vec3 at = hit.getLocation();
        AreaEffectCloud cloud = new AreaEffectCloud(player.level(), at.x, at.y + 0.5, at.z);
        cloud.setRadius(radius);
        cloud.setDuration(seconds * 20);
        cloud.setWaitTime(20);
        cloud.addEffect(new MobEffectInstance(MobEffects.POISON, seconds * 20, amplifier));
        player.level().addFreshEntity(cloud);
        player.level().playSound(null, player.blockPosition(), SoundEvents.BREWING_STAND_BREW,
                SoundSource.PLAYERS, 1.0f, 0.8f);
    }

    public static void selfEffect(ServerPlayer player, MobEffect effect, int seconds, int amplifier) {
        player.addEffect(new MobEffectInstance(effect, seconds * 20, amplifier, false, false, true));
    }

    public static void debuff(LivingEntity target, MobEffect effect, int seconds, int amplifier) {
        target.addEffect(new MobEffectInstance(effect, seconds * 20, amplifier, false, true, true));
    }

    public static void particles(ServerPlayer player, net.minecraft.core.particles.SimpleParticleType type,
                                 double radius, int count) {
        ServerLevel level = player.serverLevel();
        Vec3 pos = player.position().add(0, 1, 0);
        level.sendParticles(type, pos.x, pos.y, pos.z, count, radius, radius / 2, radius, 0.05);
    }
}
