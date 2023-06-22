package com.atodium.iridynamics.api.material.type;

import com.atodium.iridynamics.Iridynamics;
import net.minecraft.resources.ResourceLocation;

public class LiquidMaterial extends MaterialBase {
    public static final ResourceLocation REGISTRY_NAME = Iridynamics.rl("liquid");
    public static final long DEFAULT_FLAGS = 0L;

    public LiquidMaterial(String name) {
        super(name);
    }

    public LiquidMaterial(String name, long flags) {
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