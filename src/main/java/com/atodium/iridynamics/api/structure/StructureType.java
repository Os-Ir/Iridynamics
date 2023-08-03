package com.atodium.iridynamics.api.structure;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class StructureType<T extends MovingStructure<T>> {
    private final ResourceLocation id;
    private final Supplier<MovingStructure<T>> supplier;

    public StructureType(ResourceLocation id, Supplier<MovingStructure<T>> supplier) {
        this.id = id;
        this.supplier = supplier;
    }

    public ResourceLocation id() {
        return this.id;
    }

    public MovingStructure<T> create() {
        return this.supplier.get();
    }
}