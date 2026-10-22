package com.sacredarts.entity;

import com.sacredarts.technique.TechniqueHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

/** Sandviper striker/forger: a bright toxic-green needle. */
public class NeedleProjectileEntity extends ThrowableItemProjectile {
    private float damage = 4;
    private MobEffectInstance poison;

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
        this.poison = poison;
    }

    @Override
    protected Item getDefaultItem() {
        return Items.ARROW;
    }

    @Override
    public void tick() {
        super.tick();
        // Bright toxic-green trail.
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.ENTITY_EFFECT,
                    getX(), getY(), getZ(), 4, 0.1, 1.0, 0.1, 0.5);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity target = result.getEntity();
        Entity owner = getOwner();
        if (target instanceof LivingEntity living && owner instanceof ServerPlayer sp) {
            TechniqueHelper.hurt(living, sp, damage);
            if (poison != null) {
                living.addEffect(new MobEffectInstance(poison));
            }
            if (level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.ENTITY_EFFECT,
                        target.getX(), target.getY() + 1, target.getZ(), 12, 0.1, 1.0, 0.1, 1.0);
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
