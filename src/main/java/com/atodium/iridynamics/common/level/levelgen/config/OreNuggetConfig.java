package com.atodium.iridynamics.common.level.levelgen.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class OreNuggetConfig implements FeatureConfiguration {
    public static final OreNuggetConfig INSTANCE = new OreNuggetConfig();
    public static final Codec<OreNuggetConfig> CODEC = Codec.INT.comapFlatMap((n) -> DataResult.success(INSTANCE), (config) -> 0);
}