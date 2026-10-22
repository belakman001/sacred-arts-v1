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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Black Flame Forger technique: a forged wisp of black flame.
 * Phase 1 (2s): hovers above the caster, building power (growing black/orange
 * particles), while flame swirls around the caster's hand.
 * Phase 2: hunts whoever the caster is looking at, then detonates.
 * Pure particles — no model (see ForgedWispRenderer, intentionally empty).
 */
public class ForgedWispEntity extends Entity {
    private static final int CHARGE_TIME = 40; // 2 seconds of build-up
    private static final int HUNT_TIMEOUT = 120; // 6 seconds of hunting, then fizzle

    private int age = 0;
    private UUID ownerUUID;
    private float damage = 10;
    private LivingEntity target;

    public ForgedWispEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public void setOwner(ServerPlayer owner) {
        this.ownerUUID = owner.getUUID();
    }

    public void setDamage(float damage) {
        this.damage = damage;
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

        if (age <= CHARGE_TIME) {
            // Phase 1: hover above the caster, building power.
            Vec3 anchor = owner.position().add(0, 3.2, 0);
            Vec3 to = anchor.subtract(position());
            setPos(position().add(to.scale(0.15)));
            float progress = (float) age / CHARGE_TIME;
            spawnBuildParticles(serverLevel, progress);
            spawnHandParticles(serverLevel, owner);
            if (age == CHARGE_TIME) {
                target = acquireTarget(owner);
            }
        } else {
            // Phase 2: hunt.
            spawnHandParticles(serverLevel, owner);
            spawnTrailParticles(serverLevel);
            if (target != null && target.isAlive()) {
                Vec3 to = target.getEyePosition().subtract(position());
                double dist = to.length();
                if (dist < 1.5) {
                    TechniqueHelper.hurt(target, owner, damage);
                    target.setSecondsOnFire(6);
                    TechniqueHelper.blackBurstAt(serverLevel, position(), 3.0, damage * 0.5f, owner);
                    discard();
                    return;
                }
                setPos(position().add(to.normalize().scale(0.55)));
            } else if (age > CHARGE_TIME + HUNT_TIMEOUT) {
                TechniqueHelper.blackBurstAt(serverLevel, position(), 2.0, damage * 0.3f, owner);
                discard();
                return;
            }
        }
    }

    @Nullable
    private LivingEntity acquireTarget(ServerPlayer owner) {
        List<LivingEntity> targets = TechniqueHelper.targetsInFront(owner, 24, 3);
        return targets.stream()
                .min(Comparator.comparingDouble(e -> e.distanceTo(owner)))
                .orElse(null);
    }

    /** Growing ball of black + orange flame while charging. */
    private void spawnBuildParticles(ServerLevel level, float progress) {
        Vec3 p = position();
        double radius = 0.25 + progress * 0.55;
        int count = 4 + (int) (progress * 10);
        for (int i = 0; i < count; i++) {
            double a = random.nextDouble() * Math.PI * 2;
            double b = random.nextDouble() * Math.PI - Math.PI / 2;
            double x = p.x + Math.cos(a) * Math.cos(b) * radius;
            double y = p.y + 0.3 + Math.sin(b) * radius;
            double z = p.z + Math.sin(a) * Math.cos(b) * radius;
            level.sendParticles(ParticleTypes.LARGE_SMOKE, x, y, z, 1, 0.05, 0.05, 0.05, 0.01);
            level.sendParticles(ParticleTypes.FLAME, x, y, z, 1, 0.05, 0.05, 0.05, 0.01);
        }
    }

    /** Trail while hunting. */
    private void spawnTrailParticles(ServerLevel level) {
        Vec3 p = position();
        level.sendParticles(ParticleTypes.LARGE_SMOKE, p.x, p.y + 0.3, p.z, 2, 0.2, 0.2, 0.2, 0.02);
        level.sendParticles(ParticleTypes.FLAME, p.x, p.y + 0.3, p.z, 2, 0.2, 0.2, 0.2, 0.02);
    }

    /** Black + orange flame swirling around the caster's hand while the wisp lives. */
    private void spawnHandParticles(ServerLevel level, ServerPlayer owner) {
        Vec3 eye = owner.getEyePosition();
        Vec3 look = owner.getLookAngle();
        Vec3 hand = eye.add(look.scale(0.6)).add(0, -0.35, 0);
        double a = random.nextDouble() * Math.PI * 2;
        double r = 0.35;
        double x = hand.x + Math.cos(a) * r;
        double z = hand.z + Math.sin(a) * r;
        level.sendParticles(ParticleTypes.LARGE_SMOKE, x, hand.y, z, 1, 0.05, 0.05, 0.05, 0.01);
        level.sendParticles(ParticleTypes.FLAME, x, hand.y, z, 1, 0.05, 0.05, 0.05, 0.01);
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        age = tag.getInt("age");
        damage = tag.getFloat("damage");
        if (tag.hasUUID("owner")) ownerUUID = tag.getUUID("owner");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("age", age);
        tag.putFloat("damage", damage);
        if (ownerUUID != null) tag.putUUID("owner", ownerUUID);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
