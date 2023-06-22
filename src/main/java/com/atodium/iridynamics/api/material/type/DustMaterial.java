package com.atodium.iridynamics.api.material.type;

import com.atodium.iridynamics.Iridynamics;
import net.minecraft.resources.ResourceLocation;

public class DustMaterial extends MaterialBase {
    public static final ResourceLocation REGISTRY_NAME = Iridynamics.rl("dust");
    public static final long DEFAULT_FLAGS = combineFlags(GENERATE_DUST);

    public DustMaterial(String name) {
        super(name);
    }

    public DustMaterial(String name, long flags) {
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