package com.sacredarts.entity;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.joml.Vector3f;

/**
 * A Remnant: the lingering spirit of a slain hostile mob, a ghostly echo that
 * drifts where it died for about a minute. It can be struck down like any
 * foe — or a cycling sacred artist standing close can absorb it, drinking
 * its spirit as madra and Path Mastery. Pure particles, no model.
 */
public class RemnantEntity extends Entity {
    private static final int LIFETIME = 1200; // 60 seconds
    private static final float MAX_HEALTH = 20.0f;

    private float health = MAX_HEALTH;
    private int age = 0;
    private int colorRgb = 0xB3D9FF;
    private String sourceName = "Remnant";
    private Vec3 spawnPos = Vec3.ZERO;

    public RemnantEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    /** Spawn a Remnant where a mob died, colored by what it was. */
    public static void spawnFor(ServerLevel level, LivingEntity dead) {
        RemnantEntity remnant = new RemnantEntity(ModEntities.REMNANT.get(), level);
        remnant.setPos(dead.getX(), dead.getY(), dead.getZ());
        remnant.spawnPos = dead.position();
        remnant.sourceName = dead.getName().getString();
        remnant.colorRgb = colorFor(dead);
        level.addFreshEntity(remnant);
    }

    private static int colorFor(LivingEntity mob) {
        if (mob instanceof Blaze) return 0xFF6B1A;
        if (mob instanceof Creeper) return 0x40E64D;
        if (mob instanceof Spider) return 0x8A4FFF;
        if (mob instanceof EnderMan) return 0xA66BFF;
        return 0xB3D9FF; // pale ghost blue
    }

    public int getColorRgb() {
        return colorRgb;
    }

    public DustParticleOptions dust(float scale) {
        float r = ((colorRgb >> 16) & 0xFF) / 255.0f;
        float g = ((colorRgb >> 8) & 0xFF) / 255.0f;
        float b = (colorRgb & 0xFF) / 255.0f;
        return new DustParticleOptions(new Vector3f(r, g, b), scale);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (level().isClientSide) return false;
        health -= amount;
        if (level() instanceof ServerLevel serverLevel) {
            Vec3 p = position().add(0, 1, 0);
            serverLevel.sendParticles(dust(1.0f), p.x, p.y, p.z, 6, 0.4, 0.5, 0.4, 0.05);
        }
        if (health <= 0) {
            if (level() instanceof ServerLevel serverLevel) {
                Vec3 p = position().add(0, 1, 0);
                serverLevel.sendParticles(dust(1.4f), p.x, p.y, p.z, 20, 0.5, 0.6, 0.5, 0.08);
            }
            discard();
        }
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;
        ServerLevel serverLevel = (ServerLevel) level();
        age++;
        if (age > LIFETIME) {
            Vec3 p = position().add(0, 1, 0);
            serverLevel.sendParticles(dust(1.0f), p.x, p.y, p.z, 10, 0.4, 0.5, 0.4, 0.05);
            discard();
            return;
        }
        // Slow ghostly wander around where it died, bobbing gently.
        double t = age * 0.05;
        double x = spawnPos.x + Math.sin(t * 0.7) * 0.8;
        double y = spawnPos.y + 1.0 + Math.sin(t * 1.3) * 0.25;
        double z = spawnPos.z + Math.cos(t * 0.5) * 0.8;
        setPos(x, y, z);
        // Ghostly shimmer.
        Vec3 p = position();
        serverLevel.sendParticles(dust(0.8f),
                p.x + (random.nextDouble() - 0.5) * 0.8,
                p.y + random.nextDouble() * 1.6,
                p.z + (random.nextDouble() - 0.5) * 0.8,
                1, 0, 0.02, 0, 0.01);
        if (age % 20 == 0) {
            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.END_ROD,
                    p.x, p.y + 1.0, p.z, 1, 0.1, 0.1, 0.1, 0.02);
        }
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        health = tag.getFloat("health");
        age = tag.getInt("age");
        colorRgb = tag.getInt("colorRgb");
        sourceName = tag.getString("sourceName");
        spawnPos = new Vec3(tag.getDouble("spawnX"), tag.getDouble("spawnY"), tag.getDouble("spawnZ"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putFloat("health", health);
        tag.putInt("age", age);
        tag.putInt("colorRgb", colorRgb);
        tag.putString("sourceName", sourceName);
        tag.putDouble("spawnX", spawnPos.x);
        tag.putDouble("spawnY", spawnPos.y);
        tag.putDouble("spawnZ", spawnPos.z);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
