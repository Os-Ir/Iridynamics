package com.atodium.iridynamics.common.levelgen;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.common.levelgen.config.OreNuggetConfig;
import com.atodium.iridynamics.common.levelgen.feature.OreNuggetFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.registries.RegistryObject;

public class ModFeatures {
    public static final RegistryObject<Feature<OreNuggetConfig>> ORE_NUGGET = Iridynamics.REGISTRY.feature("ore_nugget", OreNuggetFeature::new, OreNuggetConfig.CODEC).register();

    public static void init() {

    }
}