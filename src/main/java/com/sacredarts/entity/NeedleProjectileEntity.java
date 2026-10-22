package com.sacredarts.entity;

import com.sacredarts.technique.TechniqueHelper;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

/** Sandviper striker/forger: a bright lime-green toxic needle. */
public class NeedleProjectileEntity extends ThrowableItemProjectile {
    /** Lime green dust. */
    private static final DustParticleOptions LIME =
            new DustParticleOptions(new org.joml.Vector3f(0.2f, 1.0f, 0.1f), 1.0f);

    private float damage = 4;
    private int poisonDuration = 200;
    private int poisonAmplifier = 0;

    public NeedleProjectileEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public NeedleProjectileEntity(EntityType<? extends ThrowableItemProjectile> type,
                                  LivingEntity shooter, Level level) {
        super(type, shooter, level);
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public void setPoison(MobEffectInstance poison) {
        // Store as primitives — robust across hit handling.
        this.poisonDuration = poison.getDuration();
        this.poisonAmplifier = poison.getAmplifier();
    }

    @Override
    protected Item getDefaultItem() {
        return Items.ARROW;
    }

    @Override
    public void tick() {
        super.tick();
        // Bright lime-green trail.
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(LIME,
                    getX(), getY(), getZ(), 3, 0.15, 0.15, 0.15, 0.1);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (result.getEntity() instanceof LivingEntity living) {
            // Poison FIRST so it lands even if damage has issues.
            living.addEffect(new MobEffectInstance(MobEffects.POISON, poisonDuration, poisonAmplifier));
            Entity owner = getOwner();
            if (owner instanceof ServerPlayer sp) {
                TechniqueHelper.hurt(living, sp, damage);
            } else {
                living.hurt(living.damageSources().indirectMagic(this, this), damage);
            }
            if (level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(LIME,
                        living.getX(), living.getY() + 1, living.getZ(), 10, 0.5, 0.5, 0.5, 0.5);
            }
        }
        discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        discard();
    }
}
