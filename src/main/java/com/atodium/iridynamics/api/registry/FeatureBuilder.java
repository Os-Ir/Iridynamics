package com.atodium.iridynamics.api.registry;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;

public class FeatureBuilder<H extends FeatureConfiguration> extends AbstractBuilder<Feature<H>> {
    protected final Function<Codec<H>, Feature<H>> supplier;
    protected final Codec<H> codec;

    protected FeatureBuilder(ModRegistry registry, String name, Function<Codec<H>, Feature<H>> supplier, Codec<H> codec) {
        super(registry, name);
        this.supplier = supplier;
        this.codec = codec;
    }

    public static <H extends FeatureConfiguration> FeatureBuilder<H> builder(ModRegistry registry, String name, Function<Codec<H>, Feature<H>> supplier, Codec<H> codec) {
        return new FeatureBuilder<>(registry, name, supplier, codec);
    }

    @Override
    public RegistryObject<Feature<H>> register() {
        super.register();
        return this.registry.getFeatureRegistry().register(this.name, () -> this.supplier.apply(this.codec));
    }
}