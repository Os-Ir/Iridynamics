package com.atodium.iridynamics.api.material.type;

public abstract class FluidMaterial extends MaterialBase {
    public FluidMaterial(String name) {
        super(name);
    }

    public FluidMaterial(String name, long flags) {
        super(name, flags);
    }
}