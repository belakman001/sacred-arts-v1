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

/** Hollow King striker: a glowing white spear that shoots forward like a trident. */
public class SpearProjectileEntity extends ThrowableItemProjectile {
    private float damage = 6;

    public SpearProjectileEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public SpearProjectileEntity(EntityType<? extends ThrowableItemProjectile> type,
                                 LivingEntity shooter, Level level) {
        super(type, shooter, level);
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    @Override
    protected Item getDefaultItem() {
        return Items.TRIDENT;
    }

    @Override
    public void tick() {
        super.tick();
        // Glowing white trail.
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.END_ROD,
                    getX(), getY(), getZ(), 3, 0.2, 0.2, 0.2, 0.02);
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
                serverLevel.sendParticles(ParticleTypes.END_ROD,
                        target.getX(), target.getY() + 1, target.getZ(), 10, 0.5, 0.5, 0.5, 0.05);
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
