package com.sacredarts.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class SacredArtsConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.DoubleValue PASSIVE_REGEN_PCT;
    public static final ForgeConfigSpec.DoubleValue CYCLING_REGEN_PCT;
    public static final ForgeConfigSpec.IntValue MASTERY_PER_CAST;
    public static final ForgeConfigSpec.IntValue MASTERY_PER_KILL;
    public static final ForgeConfigSpec.IntValue LEVEL_XP_PER_LEVEL;
    public static final ForgeConfigSpec.IntValue LEVEL_XP_CYCLING;
    public static final ForgeConfigSpec.IntValue LEVEL_XP_PER_KILL;
    public static final ForgeConfigSpec.IntValue LEVEL_XP_PER_CAST;
    public static final ForgeConfigSpec.DoubleValue TECHNIQUE_POWER_PER_LEVEL;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("madra");
        PASSIVE_REGEN_PCT = builder
                .comment("Passive madra regen, as a fraction of max madra per second.")
                .defineInRange("passiveRegenPerSecond", 0.03, 0.0, 1.0);
        CYCLING_REGEN_PCT = builder
                .comment("Madra regen while cycling, as a fraction of max madra per second.")
                .defineInRange("cyclingRegenPerSecond", 0.20, 0.0, 2.0);
        builder.pop();

        builder.push("mastery");
        MASTERY_PER_CAST = builder
                .comment("Path Mastery gained per technique cast.")
                .defineInRange("perCast", 1, 0, 100);
        MASTERY_PER_KILL = builder
                .comment("Path Mastery gained per hostile mob kill.")
                .defineInRange("perKill", 5, 0, 100);
        builder.pop();

        builder.push("level");
        LEVEL_XP_PER_LEVEL = builder
                .comment("Level XP required to go from one level to the next.")
                .defineInRange("xpPerLevel", 100, 1, 10000);
        LEVEL_XP_CYCLING = builder
                .comment("Level XP granted every 10 ticks while cycling madra.")
                .defineInRange("xpCycling", 1, 0, 100);
        LEVEL_XP_PER_KILL = builder
                .comment("Level XP granted per hostile mob kill.")
                .defineInRange("xpPerKill", 10, 0, 1000);
        LEVEL_XP_PER_CAST = builder
                .comment("Level XP granted per technique cast.")
                .defineInRange("xpPerCast", 5, 0, 1000);
        TECHNIQUE_POWER_PER_LEVEL = builder
                .comment("Technique power multiplier gained per level above 1.")
                .defineInRange("powerPerLevel", 0.08, 0.0, 1.0);
        builder.pop();

        SPEC = builder.build();
    }
}
