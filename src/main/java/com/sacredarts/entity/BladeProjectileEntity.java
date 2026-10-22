package com.sacredarts.entity;

import com.sacredarts.technique.TechniqueHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

/**
 * Endless Sword striker: a flying crescent slash (invisible, leaves
 * sweep-attack slice marks). Looks like The Endless Sword's aura.
 */
public class BladeProjectileEntity extends ThrowableItemProjectile {
    private float damage = 6;

    public BladeProjectileEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public BladeProjectileEntity(EntityType<? extends ThrowableItemProjectile> type,
                                 LivingEntity shooter, Level level) {
        super(type, shooter, level);
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    @Override
    protected Item getDefaultItem() {
        return Items.IRON_SWORD;
    }

    @Override
    public void tick() {
        super.tick();
        // Slice-mark trail.
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK,
                    getX(), getY() + 0.2, getZ(), 2, 0.3, 0.2, 0.3, 0);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity target = result.getEntity();
        Entity owner = getOwner();
        if (target instanceof LivingEntity living && owner instanceof ServerPlayer sp) {
            TechniqueHelper.hurt(living, sp, damage);
            if (level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK,
                        target.getX(), target.getY() + 1, target.getZ(), 3, 0.5, 0.5, 0.5, 0);
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
