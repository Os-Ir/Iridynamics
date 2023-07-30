package com.atodium.iridynamics.api.material.type;

import com.atodium.iridynamics.Iridynamics;
import net.minecraft.resources.ResourceLocation;

public class OreMaterial extends DustMaterial {
    public static final ResourceLocation REGISTRY_NAME = Iridynamics.rl("ore");
    public static final long DEFAULT_FLAGS = combineFlags(GENERATE_DUST, GENERATE_ORE);

    public OreMaterial(String name) {
        super(name);
    }

    public OreMaterial(String name, long flags) {
        super(name, flags);
    }

    @Override
    public ResourceLocation getRegistryName() {
        return REGISTRY_NAME;
    }

    @Override
    public long getDefaultFlags() {
        return DEFAULT_FLAGS;
    }
}