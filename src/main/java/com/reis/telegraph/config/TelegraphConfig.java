package com.reis.telegraph.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class TelegraphConfig {

    public static final ForgeConfigSpec.IntValue MAX_SIGNAL_RANGE;
    public static final ForgeConfigSpec.IntValue RELAY_BOOST_DISTANCE;
    public static final ForgeConfigSpec.DoubleValue POLE_QUALITY_BONUS;
    public static final ForgeConfigSpec.BooleanValue ENABLE_QUALITY_EFFECTS;

    public static final ForgeConfigSpec SPEC;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Signal and Network Settings").push("signal");

        MAX_SIGNAL_RANGE = builder
            .comment("Maximum cable-hop distance before signal fails. Default: 256")
            .defineInRange("maxSignalRange", 256, 1, 4096);

        RELAY_BOOST_DISTANCE = builder
            .comment("Each relay station reduces effective path distance by this amount. Default: 64")
            .defineInRange("relayBoostDistance", 64, 0, 1024);

        POLE_QUALITY_BONUS = builder
            .comment("Each pole/insulator hop reduces effective distance by this fraction of total. Default: 0.1")
            .defineInRange("poleQualityBonus", 0.1, 0.0, 1.0);

        ENABLE_QUALITY_EFFECTS = builder
            .comment("If false, quality checks are skipped — all messages deliver normally regardless of range.")
            .define("enableQualityEffects", true);

        builder.pop();
        SPEC = builder.build();
    }
}
