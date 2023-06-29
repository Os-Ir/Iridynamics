package com.atodium.iridynamics.common.level.levelgen.feature;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.common.level.levelgen.config.StickConfig;
import com.atodium.iridynamics.common.level.levelgen.config.OreNuggetConfig;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import net.minecraft.core.Holder;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Map;

public class ModFeatures {
    public static final Map<ResourceLocation, Holder<PlacedFeature>> PLACED_FEATURE = Maps.newHashMap();

    public static final RegistryObject<Feature<StickConfig>> STICK = Iridynamics.REGISTRY.feature("stick", StickFeature::new, StickConfig.CODEC).register();
    public static final RegistryObject<Feature<OreNuggetConfig>> ORE_NUGGET = Iridynamics.REGISTRY.feature("ore_nugget", OreNuggetFeature::new, OreNuggetConfig.CODEC).register();

    public static void init() {

    }

    public static void setup() {
        ResourceLocation stickLocation = Iridynamics.rl("stick");
        ResourceLocation oreNuggetLocation = Iridynamics.rl("ore_nugget");
        PLACED_FEATURE.put(stickLocation, register(stickLocation, STICK, StickConfig.INSTANCE, ImmutableList.of(InSquarePlacement.spread(), HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG), CountPlacement.of(2))));
        PLACED_FEATURE.put(oreNuggetLocation, register(oreNuggetLocation, ORE_NUGGET, OreNuggetConfig.INSTANCE, ImmutableList.of(InSquarePlacement.spread(), HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG), CountPlacement.of(10))));
    }

    private static <H extends FeatureConfiguration, F extends Feature<H>> Holder<PlacedFeature> register(ResourceLocation location, RegistryObject<F> feature, H config, List<PlacementModifier> modifiers) {
        Holder<ConfiguredFeature<?, ?>> configured = BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_FEATURE, location, new ConfiguredFeature<>(feature.get(), config));
        return BuiltinRegistries.register(BuiltinRegistries.PLACED_FEATURE, location, new PlacedFeature(configured, modifiers));
    }
}