package com.sacredarts.aura;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.util.RandomSource;

/**
 * Where vital aura lives: which aura types a biome breathes, and which aura
 * an ore vein carries. Used by Copper Sight (vision) and cycling (gathering).
 *
 * Aura is not only in the land — it answers to what happens around the
 * artist. getAmbientAuras layers those momentary auras over the biome's own:
 * Blood when you strike a foe, Dream on a night in a village, Wind in the
 * high air, Sword near a blade, Destruction where things have exploded.
 */
public class AuraHelper {

    /** At and above this Y, the high air carries Wind aura. */
    public static final int WIND_Y = 120;
    /** How long Blood lingers after a strike (ticks). */
    private static final long BLOOD_TICKS = 100;
    /** How long Destruction lingers after an explosion (ticks). */
    private static final long EXPLOSION_TICKS = 200;
    /** How close a villager must be for a village to dream. */
    private static final double VILLAGE_RADIUS = 32;
    /** How close a dropped sword must be to breathe Sword aura. */
    private static final double SWORD_RADIUS = 8;

    /** Recent explosions, server-side only: position + game time. */
    private static final ArrayDeque<ExplosionRecord> RECENT_EXPLOSIONS = new ArrayDeque<>();

    private record ExplosionRecord(Vec3 pos, long time) {
    }

    /** The aura types drifting in the air at this position. Never empty. */
    public static AuraType[] getBiomeAuras(Level level, BlockPos pos) {
        // Dimensions first.
        if (level.dimension() == Level.NETHER) {
            return new AuraType[]{AuraType.FIRE, AuraType.DESTRUCTION, AuraType.SHADOW};
        }
        if (level.dimension() == Level.END) {
            return new AuraType[]{AuraType.FORCE, AuraType.SHADOW};
        }
        // Deep underground is shadow and earth, whatever the biome above.
        if (pos.getY() < 0) {
            return new AuraType[]{AuraType.SHADOW, AuraType.EARTH};
        }
        String biome = level.getBiome(pos).unwrapKey()
                .map(k -> k.location().getPath()).orElse("");
        List<AuraType> out = new ArrayList<>();
        if (biome.contains("desert")) {
            out.add(AuraType.FIRE);
            out.add(AuraType.LIGHT);
        } else if (biome.contains("ocean") || biome.contains("river")) {
            out.add(AuraType.WATER);
        } else if (biome.contains("jungle")) {
            out.add(AuraType.LIFE);
            out.add(AuraType.DREAM);
        } else if (biome.contains("forest") || biome.contains("grove")
                || biome.contains("cherry") || biome.contains("birch")) {
            out.add(AuraType.LIFE);
            if (biome.contains("cherry")) out.add(AuraType.DREAM);
        } else if (biome.contains("swamp") || biome.contains("mangrove")) {
            out.add(AuraType.WATER);
            out.add(AuraType.BLOOD);
        } else if (biome.contains("mountain") || biome.contains("peaks")
                || biome.contains("stony") || biome.contains("jagged")) {
            out.add(AuraType.EARTH);
            out.add(AuraType.FORCE);
            out.add(AuraType.WIND);
        } else if (biome.contains("snow") || biome.contains("frozen")
                || biome.contains("ice") || biome.contains("cold")) {
            out.add(AuraType.WATER);
            out.add(AuraType.WIND);
        } else if (biome.contains("beach")) {
            out.add(AuraType.WATER);
            out.add(AuraType.WIND);
        } else if (biome.contains("badlands") || biome.contains("mesa")
                || biome.contains("savanna")) {
            out.add(AuraType.FIRE);
            out.add(AuraType.EARTH);
        } else if (biome.contains("mushroom")) {
            out.add(AuraType.DREAM);
            out.add(AuraType.LIFE);
        } else if (biome.contains("deep_dark")) {
            out.add(AuraType.SHADOW);
            out.add(AuraType.EARTH);
        } else if (biome.contains("dripstone")) {
            out.add(AuraType.EARTH);
            out.add(AuraType.SHADOW);
        } else if (biome.contains("lush")) {
            out.add(AuraType.LIFE);
            out.add(AuraType.WATER);
        } else {
            // Plains, meadows, flower fields and anything unlisted.
            out.add(AuraType.WIND);
            out.add(AuraType.LIFE);
        }
        return out.toArray(new AuraType[0]);
    }

    /** Pick one of the biome's aura types at random — for drifting motes. */
    public static AuraType randomBiomeAura(Level level, BlockPos pos, RandomSource random) {
        AuraType[] auras = getBiomeAuras(level, pos);
        return auras[random.nextInt(auras.length)];
    }

    /**
     * The aura types in the air around this player right now: the biome's
     * own auras, plus any the moment calls for. Never empty.
     * Blood: the player struck a foe in the last 5 seconds.
     * Dream: night, with villagers nearby. Wind: high air (Y 120+).
     * Sword: a blade in hand, or one lying nearby.
     * Destruction: an explosion went off nearby in the last 10 seconds
     * (server-tracked; the client sees the burst instead).
     */
    public static AuraType[] getAmbientAuras(Level level, Player player) {
        List<AuraType> out = new ArrayList<>(Arrays.asList(getBiomeAuras(level, player.blockPosition())));
        LivingEntity lastHurt = player.getLastHurtMob();
        if (lastHurt != null && level.getGameTime() - player.getLastHurtMobTimestamp() < BLOOD_TICKS) {
            addIfMissing(out, AuraType.BLOOD);
        }
        if (level.isNight() && !level.getEntitiesOfClass(Villager.class,
                player.getBoundingBox().inflate(VILLAGE_RADIUS)).isEmpty()) {
            addIfMissing(out, AuraType.DREAM);
        }
        if (player.getY() >= WIND_Y) {
            addIfMissing(out, AuraType.WIND);
        }
        if (getSwordTier(player) >= 0) {
            addIfMissing(out, AuraType.SWORD);
        }
        if (hasRecentExplosion(level, player.blockPosition())) {
            addIfMissing(out, AuraType.DESTRUCTION);
        }
        return out.toArray(new AuraType[0]);
    }

    private static void addIfMissing(List<AuraType> out, AuraType aura) {
        if (!out.contains(aura)) out.add(aura);
    }

    /**
     * Pick one ambient aura at random, weighted so a better sword breathes
     * more Sword aura: a blade's weight is its tier level + 1 (wood 1,
     * stone 2, iron 3, diamond 4, netherite 5). Everything else weighs 1.
     */
    public static AuraType randomAmbientAura(Level level, Player player, RandomSource random) {
        AuraType[] ambient = getAmbientAuras(level, player);
        int swordWeight = getSwordTier(player) + 1;
        int total = 0;
        for (AuraType a : ambient) total += (a == AuraType.SWORD ? swordWeight : 1);
        int roll = random.nextInt(total);
        for (AuraType a : ambient) {
            int w = (a == AuraType.SWORD ? swordWeight : 1);
            if (roll < w) return a;
            roll -= w;
        }
        return ambient[0];
    }

    /**
     * Best sword tier the player holds or has lying nearby: -1 if no blade,
     * otherwise the tier level (wood 0, stone 1, iron 2, diamond 3,
     * netherite 4). Gold counts as wood-tier.
     */
    public static int getSwordTier(Player player) {
        int best = -1;
        for (net.minecraft.world.item.ItemStack hand :
                List.of(player.getMainHandItem(), player.getOffhandItem())) {
            if (hand.getItem() instanceof SwordItem sword) {
                best = Math.max(best, sword.getTier().getLevel());
            }
        }
        for (ItemEntity item : player.level().getEntitiesOfClass(ItemEntity.class,
                player.getBoundingBox().inflate(SWORD_RADIUS))) {
            if (item.getItem().getItem() instanceof SwordItem sword) {
                best = Math.max(best, sword.getTier().getLevel());
            }
        }
        return best;
    }

    /** Remember an explosion (server-side) and flare Destruction at the site. */
    public static void recordExplosion(Level level, Vec3 pos) {
        RECENT_EXPLOSIONS.addLast(new ExplosionRecord(pos, level.getGameTime()));
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(AuraType.DESTRUCTION.dust(1.5f),
                    pos.x, pos.y + 0.5, pos.z, 40, 1.5, 1.5, 1.5, 0.05);
        }
    }

    /** True if an explosion went off within 16 blocks in the last 10 seconds. */
    public static boolean hasRecentExplosion(Level level, BlockPos pos) {
        long now = level.getGameTime();
        while (!RECENT_EXPLOSIONS.isEmpty()
                && now - RECENT_EXPLOSIONS.peekFirst().time() > EXPLOSION_TICKS) {
            RECENT_EXPLOSIONS.removeFirst();
        }
        Vec3 center = Vec3.atCenterOf(pos);
        for (ExplosionRecord record : RECENT_EXPLOSIONS) {
            if (record.pos().distanceToSqr(center) < 16 * 16) return true;
        }
        return false;
    }

    /** The aura carried by an ore block, or null if it carries none. */
    @Nullable
    public static AuraType getOreAura(Block block) {
        if (block == Blocks.DIAMOND_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE) return AuraType.FORCE;
        if (block == Blocks.EMERALD_ORE || block == Blocks.DEEPSLATE_EMERALD_ORE) return AuraType.LIFE;
        if (block == Blocks.REDSTONE_ORE || block == Blocks.DEEPSLATE_REDSTONE_ORE) return AuraType.FIRE;
        if (block == Blocks.LAPIS_ORE || block == Blocks.DEEPSLATE_LAPIS_ORE) return AuraType.DREAM;
        if (block == Blocks.GOLD_ORE || block == Blocks.DEEPSLATE_GOLD_ORE) return AuraType.LIGHT;
        if (block == Blocks.IRON_ORE || block == Blocks.DEEPSLATE_IRON_ORE) return AuraType.EARTH;
        if (block == Blocks.COPPER_ORE || block == Blocks.DEEPSLATE_COPPER_ORE) return AuraType.WIND;
        if (block == Blocks.COAL_ORE || block == Blocks.DEEPSLATE_COAL_ORE) return AuraType.SHADOW;
        if (block == Blocks.NETHER_QUARTZ_ORE) return AuraType.FIRE;
        if (block == Blocks.NETHER_GOLD_ORE) return AuraType.FIRE;
        if (block == Blocks.ANCIENT_DEBRIS) return AuraType.DESTRUCTION;
        return null;
    }
}
