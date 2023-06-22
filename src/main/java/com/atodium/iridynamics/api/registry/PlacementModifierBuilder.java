package com.atodium.iridynamics.api.registry;

import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class PlacementModifierBuilder<T extends PlacementModifier> extends AbstractBuilder<PlacementModifierType<T>> {
    protected final Supplier<PlacementModifierType<T>> supplier;

    protected PlacementModifierBuilder(ModRegistry registry, String name, Supplier<PlacementModifierType<T>> supplier) {
        super(registry, name);
        this.supplier = supplier;
    }

    public static <T extends PlacementModifier> PlacementModifierBuilder<T> builder(ModRegistry registry, String name, Supplier<PlacementModifierType<T>> supplier) {
        return new PlacementModifierBuilder<>(registry, name, supplier);
    }

    @Override
    public RegistryObject<PlacementModifierType<T>> register() {
        super.register();
        return this.registry.getPlacementModifierRegister().register(this.name, this.supplier);
    }
}