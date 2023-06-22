package com.atodium.iridynamics.api.material.type;

import com.atodium.iridynamics.Iridynamics;
import net.minecraft.resources.ResourceLocation;

public class GemMaterial extends DustMaterial {
    public static final ResourceLocation REGISTRY_NAME = Iridynamics.rl("gem");
    public static final long DEFAULT_FLAGS = combineFlags(GENERATE_DUST, GENERATE_PLATE, GENERATE_CRYSTAL, GENERATE_ROD, GENERATE_GEAR, GENERATE_SCREW, GENERATE_RING);

    public GemMaterial(String name) {
        super(name);
    }

    public GemMaterial(String name, long flags) {
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