package com.atodium.iridynamics.common.levelgen.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class OreNuggetConfig implements FeatureConfiguration {
    public static final OreNuggetConfig INSTANCE = new OreNuggetConfig();
    public static final Codec<OreNuggetConfig> CODEC = Codec.INT.comapFlatMap((n) -> {
        System.out.println("Data to config");
        return DataResult.success(INSTANCE);
    }, (config) -> {
        System.out.println("Config to zero data");
        return 0;
    });
}