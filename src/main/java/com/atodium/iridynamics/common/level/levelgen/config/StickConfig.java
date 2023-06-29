package com.atodium.iridynamics.common.level.levelgen.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class StickConfig implements FeatureConfiguration {
    public static final StickConfig INSTANCE = new StickConfig();
    public static final Codec<StickConfig> CODEC = Codec.INT.comapFlatMap((n) -> DataResult.success(INSTANCE), (config) -> 0);
}