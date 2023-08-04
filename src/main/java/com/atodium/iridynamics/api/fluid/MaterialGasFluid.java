package com.atodium.iridynamics.api.fluid;

import com.atodium.iridynamics.api.material.type.GasMaterial;
import net.minecraftforge.fluids.FluidStack;

public class MaterialGasFluid extends ContainerFluid {
    private final GasMaterial material;

    public MaterialGasFluid(GasMaterial material) {
        this.material = material;
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