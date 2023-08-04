package com.atodium.iridynamics.api.fluid;

import com.atodium.iridynamics.api.material.type.LiquidMaterial;
import net.minecraftforge.fluids.FluidStack;

public class MaterialLiquidFluid extends ContainerFluid {
    private final LiquidMaterial material;

    public MaterialLiquidFluid(LiquidMaterial material) {
        this.material = material;
        FluidModule.registerLiquid(this);
    }

    public LiquidMaterial material() {
        return this.material;
    }

    @Override
    public String localizedName(FluidStack stack) {
        return this.material.getLocalizedName();
    }

    @Override
    public String translationKey() {
        return this.material.getUnlocalizedName();
    }

    @Override
    public int color() {
        return this.material.getRenderInfo().color();
    }
}