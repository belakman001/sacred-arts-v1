package com.sacredarts.advancement;

import com.sacredarts.config.SacredArtsConfig;
import com.sacredarts.data.IronBody;
import com.sacredarts.data.ISacredArtist;
import com.sacredarts.network.ModNetwork;
import com.sacredarts.network.OpenIronBodyPacket;
import com.sacredarts.technique.BlackFlameTechniques;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.PacketDistributor;

/** Per-tick sacred arts logic: madra regen, cycling, senses, iron body, stage-ups. */
public class AdvancementHandler {
    private static final double FOUNDATION_CYCLED = 500;
    private static final int COPPER_KILLS = 20;
    private static final int IRON_MASTERY = 25;
    private static final int JADE_MASTERY = 50;
    private static final double JADE_CYCLED = 5000;
    private static final int LOWGOLD_MASTERY = 100;
    private static final int HIGHGOLD_MASTERY = 150;

    public static void tick(ServerPlayer player, ISacredArtist data) {
        long time = player.level().getGameTime();
        double max = data.getMaxMadra();

        if (data.isCycling()) {
            data.addCycled(max * SacredArtsConfig.CYCLING_REGEN_PCT.get() / 20.0);
            if (time % 10 == 0) {
                data.addLevelXp(SacredArtsConfig.LEVEL_XP_CYCLING.get());
                var p = player.position().add(0, 1, 0);
                player.serverLevel().sendParticles(ParticleTypes.END_ROD,
                        p.x, p.y, p.z, 4, 0.5, 0.5, 0.5, 0.02);
            }
        } else {
            data.setMadra(data.getMadra() + max * SacredArtsConfig.PASSIVE_REGEN_PCT.get() / 20.0);
        }

        if (time % 20 == 0) {
            checkAdvancement(player, data);
        }

        // Copper Sight: hostile entities glow through walls (toggle with G).
        if (data.getStage().atLeast(Stage.COPPER) && data.isCopperSightEnabled() && time % 100 == 0) {
            var pos = player.position();
            AABB box = new AABB(pos.x - 24, pos.y - 24, pos.z - 24, pos.x + 24, pos.y + 24, pos.z + 24);
            for (LivingEntity e : player.level().getEntitiesOfClass(LivingEntity.class, box,
                    e -> e instanceof Enemy && e.isAlive())) {
                e.addEffect(new MobEffectInstance(MobEffects.GLOWING, 120, 0, false, false, true));
            }
        }

        // Iron Body passives.
        if (data.getIronBody() == IronBody.BLOODFORGED && time % 100 == 0) {
            if (player.getHealth() < player.getMaxHealth()) {
                player.heal(1.0f);
            }
        } else if (data.getIronBody() == IronBody.RAINDROP && time % 200 == 0) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 220, 0, false, false, true));
        }

        // Black Flame striker charge-up: a black ball grows in the hand, then the beam fires.
        if (data.getStrikerChargeTicks() > 0) {
            int left = data.getStrikerChargeTicks() - 1;
            data.setStrikerChargeTicks(left);
            spawnChargeBall(player,
                    BlackFlameTechniques.STRIKER_CHARGE_TICKS - left,
                    BlackFlameTechniques.STRIKER_CHARGE_TICKS);
            if (left <= 0) {
                com.sacredarts.technique.TechniqueHelper.blackBeam(
                        player, 16, data.getStrikerChargeDamage(), true);
            }
        }

        // Black Flame enforcer: black + orange flames for the whole boost.
        if (data.getCloakTicks() > 0) {
            data.setCloakTicks(data.getCloakTicks() - 1);
            if (time % 2 == 0) {
                spawnCloakFlames(player);
            }
        }

        // Black Flame forger fire fist charge-up: a fist of flame forms, then shoots forward.
        if (data.getFistChargeTicks() > 0) {
            int left = data.getFistChargeTicks() - 1;
            data.setFistChargeTicks(left);
            spawnFistCharge(player,
                    BlackFlameTechniques.FIST_CHARGE_TICKS - left,
                    BlackFlameTechniques.FIST_CHARGE_TICKS);
            if (left <= 0) {
                com.sacredarts.technique.BlackFlameTechniques.fireFist(player, data);
            }
        }

        // Endless Sword enforcer: silver glint for the whole duration.
        if (data.getSwordGlintTicks() > 0) {
            data.setSwordGlintTicks(data.getSwordGlintTicks() - 1);
            if (time % 2 == 0) {
                spawnSwordGlint(player);
            }
        }

        // Endless Sword ruler: sword aura field.
        if (data.getSwordAuraTicks() > 0) {
            data.setSwordAuraTicks(data.getSwordAuraTicks() - 1);
            tickSwordAura(player, data, time);
        }

        // Keep the client's HUD and menus fresh.
        if (time % 40 == 0) {
            ModNetwork.syncPlayer(player);
        }
    }

    /** A fist of black + orange flame forming in the hand. */
    private static void spawnFistCharge(ServerPlayer player, int elapsed, int total) {
        float progress = Math.min(1f, (float) elapsed / total);
        net.minecraft.world.phys.Vec3 eye = player.getEyePosition();
        net.minecraft.world.phys.Vec3 look = player.getLookAngle();
        net.minecraft.world.phys.Vec3 hand = eye.add(look.scale(0.6)).add(0, -0.35, 0);
        double radius = 0.2 + progress * 0.35;
        int count = 6 + (int) (progress * 10);
        for (int i = 0; i < count; i++) {
            double a = player.getRandom().nextDouble() * Math.PI * 2;
            double b = player.getRandom().nextDouble() * Math.PI - Math.PI / 2;
            double x = hand.x + Math.cos(a) * Math.cos(b) * radius;
            double y = hand.y + Math.sin(b) * radius;
            double z = hand.z + Math.sin(a) * Math.cos(b) * radius;
            player.serverLevel().sendParticles(net.minecraft.core.particles.ParticleTypes.LARGE_SMOKE,
                    x, y, z, 1, 0.03, 0.03, 0.03, 0.01);
            player.serverLevel().sendParticles(net.minecraft.core.particles.ParticleTypes.FLAME,
                    x, y, z, 1, 0.03, 0.03, 0.03, 0.01);
        }
    }

    /** Silver glint swirling around the player. */
    private static void spawnSwordGlint(ServerPlayer player) {
        net.minecraft.world.phys.Vec3 pos = player.position();
        double angle = player.getRandom().nextDouble() * Math.PI * 2;
        double r = 0.8;
        double x = pos.x + Math.cos(angle) * r;
        double z = pos.z + Math.sin(angle) * r;
        double y = pos.y + player.getRandom().nextDouble() * 1.8;
        player.serverLevel().sendParticles(net.minecraft.core.particles.ParticleTypes.END_ROD,
                x, y, z, 1, 0.05, 0.05, 0.05, 0.02);
    }

    /** Sword Aura Field: slice marks in the air, a ringing sound, and every weapon
     *  near the caster strikes entities around it (even held weapons). */
    private static void tickSwordAura(ServerPlayer player,
                                      com.sacredarts.data.ISacredArtist data, long time) {
        net.minecraft.server.level.ServerLevel level = player.serverLevel();
        net.minecraft.world.phys.Vec3 center = player.position();

        if (time % 10 == 0) {
            // Slice marks in the air around the caster.
            for (int i = 0; i < 4; i++) {
                double x = center.x + (player.getRandom().nextDouble() - 0.5) * 8;
                double y = center.y + 1 + player.getRandom().nextDouble() * 2;
                double z = center.z + (player.getRandom().nextDouble() - 0.5) * 8;
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.SWEEP_ATTACK,
                        x, y, z, 1, 0, 0, 0, 0);
            }
            // Every weapon within 20 blocks strikes entities within 3 blocks of it.
            applySwordAuraDamage(player, data);
        }
        if (time % 20 == 0) {
            // Ringing, like struck steel.
            level.playSound(null, player.blockPosition(),
                    net.minecraft.sounds.SoundEvents.BLOCK_ANVIL_HIT,
                    net.minecraft.sounds.SoundSource.PLAYERS, 0.4f, 1.9f);
        }
    }

    private static void applySwordAuraDamage(ServerPlayer player,
                                             com.sacredarts.data.ISacredArtist data) {
        float damage = (float) (5 * data.getPowerMultiplier());
        net.minecraft.server.level.ServerLevel level = player.serverLevel();
        net.minecraft.world.phys.Vec3 center = player.position();
        net.minecraft.world.phys.AABB scan = new net.minecraft.world.phys.AABB(
                center.x - 20, center.y - 10, center.z - 20,
                center.x + 20, center.y + 10, center.z + 20);

        // Weapons lying on the ground.
        for (net.minecraft.world.entity.item.ItemEntity item :
                level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class, scan)) {
            if (isWeapon(item.getItem())) {
                swordAuraStrike(level, player, item.position(), null, damage);
            }
        }
        // Weapons being held (players and mobs, either hand).
        for (net.minecraft.world.entity.LivingEntity e :
                level.getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class, scan)) {
            if (isWeapon(e.getMainHandItem())) {
                swordAuraStrike(level, player, e.position(), e, damage);
            }
            if (isWeapon(e.getOffhandItem())) {
                swordAuraStrike(level, player, e.position(), e, damage);
            }
        }
    }

    private static void swordAuraStrike(net.minecraft.server.level.ServerLevel level,
                                        ServerPlayer caster,
                                        net.minecraft.world.phys.Vec3 weaponPos,
                                        @javax.annotation.Nullable
                                        net.minecraft.world.entity.LivingEntity holder,
                                        float damage) {
        net.minecraft.world.phys.AABB hit = new net.minecraft.world.phys.AABB(
                weaponPos.x - 3, weaponPos.y - 3, weaponPos.z - 3,
                weaponPos.x + 3, weaponPos.y + 3, weaponPos.z + 3);
        for (net.minecraft.world.entity.LivingEntity victim :
                level.getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class, hit,
                        v -> v != caster && v != holder && v.isAlive()
                                && v.distanceToSqr(weaponPos) <= 9)) {
            com.sacredarts.technique.TechniqueHelper.hurt(victim, caster, damage);
            level.sendParticles(net.minecraft.core.particles.ParticleTypes.SWEEP_ATTACK,
                    victim.getX(), victim.getY() + 1, victim.getZ(), 1, 0, 0, 0, 0);
        }
    }

    private static boolean isWeapon(net.minecraft.world.item.ItemStack stack) {
        net.minecraft.world.item.Item item = stack.getItem();
        return item instanceof net.minecraft.world.item.SwordItem
                || item instanceof net.minecraft.world.item.AxeItem
                || item instanceof net.minecraft.world.item.TridentItem;
    }

    /** Small, dense black ball in the hand: dark smoke core + orange flame wisps. */
    private static void spawnChargeBall(ServerPlayer player, int elapsed, int total) {
        float progress = Math.min(1f, (float) elapsed / total);
        net.minecraft.world.phys.Vec3 eye = player.getEyePosition();
        net.minecraft.world.phys.Vec3 look = player.getLookAngle();
        net.minecraft.world.phys.Vec3 hand = eye.add(look.scale(0.6)).add(0, -0.35, 0);
        double radius = 0.15 + progress * 0.3;
        int count = 4 + (int) (progress * 8);
        for (int i = 0; i < count; i++) {
            double a = player.getRandom().nextDouble() * Math.PI * 2;
            double b = player.getRandom().nextDouble() * Math.PI - Math.PI / 2;
            double x = hand.x + Math.cos(a) * Math.cos(b) * radius;
            double y = hand.y + Math.sin(b) * radius;
            double z = hand.z + Math.sin(a) * Math.cos(b) * radius;
            player.serverLevel().sendParticles(net.minecraft.core.particles.ParticleTypes.LARGE_SMOKE,
                    x, y, z, 1, 0.02, 0.02, 0.02, 0.01);
            player.serverLevel().sendParticles(net.minecraft.core.particles.ParticleTypes.FLAME,
                    x, y, z, 1, 0.02, 0.02, 0.02, 0.01);
        }
    }

    /** Ring of black + orange flame around the player. */
    private static void spawnCloakFlames(ServerPlayer player) {
        net.minecraft.world.phys.Vec3 pos = player.position();
        double angle = player.getRandom().nextDouble() * Math.PI * 2;
        double r = 0.7;
        double x = pos.x + Math.cos(angle) * r;
        double z = pos.z + Math.sin(angle) * r;
        double y = pos.y + player.getRandom().nextDouble() * 1.8;
        player.serverLevel().sendParticles(net.minecraft.core.particles.ParticleTypes.LARGE_SMOKE,
                x, y, z, 1, 0.08, 0.08, 0.08, 0.01);
        player.serverLevel().sendParticles(net.minecraft.core.particles.ParticleTypes.FLAME,
                x, y, z, 1, 0.08, 0.08, 0.08, 0.01);
    }

    private static void checkAdvancement(ServerPlayer player, ISacredArtist data) {
        Stage stage = data.getStage();
        boolean up = switch (stage) {
            case FOUNDATION -> data.getCycledTotal() >= FOUNDATION_CYCLED;
            case COPPER -> data.getKills() >= COPPER_KILLS;
            case IRON -> data.getMastery() >= IRON_MASTERY;
            case JADE -> data.getMastery() >= JADE_MASTERY && data.getCycledTotal() >= JADE_CYCLED;
            case LOWGOLD -> data.getMastery() >= LOWGOLD_MASTERY;
            case HIGHGOLD -> data.getMastery() >= HIGHGOLD_MASTERY;
            default -> false;
        };
        if (!up) return;

        Stage next = stage.next();
        if (next == null) return;
        data.setStage(next);
        data.setMadra(data.getMaxMadra());
        player.displayClientMessage(
                Component.translatable("message.sacredarts.stage_up", next.getDisplayName()), false);
        player.serverLevel().playSound(null, player.blockPosition(),
                SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0f, 1.0f);
        var p = player.position().add(0, 1, 0);
        player.serverLevel().sendParticles(ParticleTypes.TOTEM_OF_UNDYING,
                p.x, p.y, p.z, 30, 0.5, 0.8, 0.5, 0.1);

        if (next == Stage.IRON) {
            ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new OpenIronBodyPacket());
        }
        if (next == Stage.LOWGOLD) {
            data.setGoldsign(true);
            player.displayClientMessage(Component.translatable("message.sacredarts.goldsign",
                    Component.translatable("path.sacredarts." + data.getPath().getId() + ".goldsign")), false);
        }
        ModNetwork.syncPlayer(player);
    }
}
