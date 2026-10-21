package com.sacredarts.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class SacredArtsConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.DoubleValue PASSIVE_REGEN_PCT;
    public static final ForgeConfigSpec.DoubleValue CYCLING_REGEN_PCT;
    public static final ForgeConfigSpec.IntValue MASTERY_PER_CAST;
    public static final ForgeConfigSpec.IntValue MASTERY_PER_KILL;

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

        SPEC = builder.build();
    }
}
