package com.atodium.iridynamics.api.registry;

import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ConfiguredFeatureBuilder<H extends FeatureConfiguration, F extends Feature<H>> extends AbstractBuilder<ConfiguredFeature<H, F>> {
    protected final Supplier<ConfiguredFeature<H, F>> supplier;

    protected ConfiguredFeatureBuilder(ModRegistry registry, String name, Supplier<ConfiguredFeature<H, F>> supplier) {
        super(registry, name);
        this.supplier = supplier;
    }

    public static <H extends FeatureConfiguration, F extends Feature<H>> ConfiguredFeatureBuilder<H, F> builder(ModRegistry registry, String name, Supplier<ConfiguredFeature<H, F>> supplier) {
        return new ConfiguredFeatureBuilder<>(registry, name, supplier);
    }

    @Override
    public RegistryObject<ConfiguredFeature<H, F>> register() {
        super.register();
        return this.registry.getConfiguredFeatureRegistry().register(this.name, this.supplier);
    }
}