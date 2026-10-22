package com.sacredarts.entity;

import com.sacredarts.technique.TechniqueHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Black Flame Forger technique: a fist of black + orange flame that shoots
 * forward. Pure particles — no model (see ParticleOnlyRenderer).
 */
public class FireFistEntity extends Entity {
    private static final int MAX_AGE = 40; // 2 seconds of flight max

    private int age = 0;
    private UUID ownerUUID;
    private float damage = 8;
    private Vec3 direction = Vec3.ZERO;

    public FireFistEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public void setOwner(ServerPlayer owner) {
        this.ownerUUID = owner.getUUID();
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public void setDirection(Vec3 direction) {
        this.direction = direction.normalize();
    }

    @Nullable
    private ServerPlayer getOwner() {
        if (ownerUUID == null || !(level() instanceof ServerLevel serverLevel)) return null;
        return serverLevel.getServer().getPlayerList().getPlayer(ownerUUID);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;
        ServerLevel serverLevel = (ServerLevel) level();
        age++;

        ServerPlayer owner = getOwner();
        if (owner == null || !owner.isAlive()) {
            discard();
            return;
        }

        Vec3 from = position();
        Vec3 to = from.add(direction.scale(0.9));

        // Stop at blocks.
        BlockHitResult blockHit = level().clip(
                new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (blockHit.getType() != HitResult.Type.MISS) {
            TechniqueHelper.blackBurstAt(serverLevel, blockHit.getLocation(), 2.5, damage * 0.5f, owner);
            discard();
            return;
        }
        setPos(to);

        // Fist visual: dense ball of black + orange flame.
        for (int i = 0; i < 5; i++) {
            double ox = (random.nextDouble() - 0.5) * 0.7;
            double oy = (random.nextDouble() - 0.5) * 0.7;
            double oz = (random.nextDouble() - 0.5) * 0.7;
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                    to.x + ox, to.y + 0.3 + oy, to.z + oz, 1, 0.05, 0.05, 0.05, 0.01);
            serverLevel.sendParticles(ParticleTypes.FLAME,
                    to.x + ox, to.y + 0.3 + oy, to.z + oz, 1, 0.05, 0.05, 0.05, 0.01);
        }

        // Hit check.
        AABB box = getBoundingBox().inflate(0.9);
        List<LivingEntity> hits = serverLevel.getEntitiesOfClass(LivingEntity.class, box,
                e -> e != owner && e.isAlive());
        if (!hits.isEmpty()) {
            for (LivingEntity e : hits) {
                TechniqueHelper.hurt(e, owner, damage);
                e.setSecondsOnFire(4);
            }
            TechniqueHelper.blackBurstAt(serverLevel, to, 2.5, damage * 0.5f, owner);
            discard();
            return;
        }

        if (age > MAX_AGE) discard();
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        age = tag.getInt("age");
        damage = tag.getFloat("damage");
        if (tag.hasUUID("owner")) ownerUUID = tag.getUUID("owner");
        direction = new Vec3(tag.getDouble("dx"), tag.getDouble("dy"), tag.getDouble("dz"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("age", age);
        tag.putFloat("damage", damage);
        if (ownerUUID != null) tag.putUUID("owner", ownerUUID);
        tag.putDouble("dx", direction.x);
        tag.putDouble("dy", direction.y);
        tag.putDouble("dz", direction.z);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
