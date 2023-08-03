package com.atodium.iridynamics.api.registry;

import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class FluidBuilder extends AbstractBuilder<Fluid> {
    protected final Supplier<Fluid> supplier;

    protected FluidBuilder(ModRegistry registry, String name, Supplier<Fluid> supplier) {
        super(registry, name);
        this.supplier = supplier;
    }

    public static FluidBuilder builder(ModRegistry registry, String name, Supplier<Fluid> supplier) {
        return new FluidBuilder(registry, name, supplier);
    }

    @Override
    public RegistryObject<Fluid> register() {
        super.register();
        return this.registry.getFluidRegistry().register(this.name, this.supplier);
    }
}