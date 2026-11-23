package com.sacredarts.aura;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Where vital aura lives: which aura types a biome breathes, and which aura
 * an ore vein carries. Used by Copper Sight (vision) and cycling (gathering).
 */
public class AuraHelper {

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
    public static AuraType randomBiomeAura(Level level, BlockPos pos, Random random) {
        AuraType[] auras = getBiomeAuras(level, pos);
        return auras[random.nextInt(auras.length)];
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
