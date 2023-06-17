package com.atodium.iridynamics.api.material.type;

import com.atodium.iridynamics.Iridynamics;
import net.minecraft.resources.ResourceLocation;

public class PlasticityMaterial extends DustMaterial {
    public static final ResourceLocation REGISTRY_NAME = new ResourceLocation(Iridynamics.MODID, "plasticity");
    public static final long DEFAULT_FLAGS = combineFlags(GENERATE_DUST, GENERATE_PLATE, GENERATE_ROD, GENERATE_GEAR, GENERATE_INGOT, GENERATE_FOIL, GENERATE_SCREW, GENERATE_SPRING, GENERATE_RING, GENERATE_WIRE, GENERATE_ROTOR, GENERATE_NUGGET);

    public PlasticityMaterial(String name) {
        super(name);
    }

    public PlasticityMaterial(String name, long flags) {
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