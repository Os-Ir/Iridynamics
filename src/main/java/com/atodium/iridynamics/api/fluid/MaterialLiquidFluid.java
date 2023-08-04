package com.atodium.iridynamics.api.fluid;

import com.atodium.iridynamics.api.material.type.LiquidMaterial;
import net.minecraftforge.fluids.FluidStack;

public class MaterialLiquidFluid extends ContainerFluid {
    private final LiquidMaterial material;

    public MaterialLiquidFluid(LiquidMaterial material) {
        this.material = material;
    }

    @Override
    public String getLocalizedName(FluidStack stack) {
        return this.material.getLocalizedName();
    }
}