package com.atodium.iridynamics.api.registry;

import net.minecraftforge.registries.RegistryObject;

public abstract class AbstractBuilder<T> {
    protected final ModRegistry registry;
    protected final String name;
    protected boolean built;

    protected AbstractBuilder(ModRegistry registry, String name) {
        this.registry = registry;
        this.name = name;
        this.built = false;
    }

    public ModRegistry getRegistry() {
        return this.registry;
    }

    public RegistryObject<T> register() {
        if (this.built) throw new IllegalStateException("This Builder has already registered");
        this.built = true;
        return null;
    }
}