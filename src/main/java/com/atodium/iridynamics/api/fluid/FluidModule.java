package com.atodium.iridynamics.api.fluid;

import com.atodium.iridynamics.api.material.ModMaterials;
import com.atodium.iridynamics.api.material.type.FluidMaterial;
import com.atodium.iridynamics.api.material.type.GasMaterial;
import com.atodium.iridynamics.api.material.type.LiquidMaterial;
import com.atodium.iridynamics.api.util.data.UnorderedRegistry;
import com.atodium.iridynamics.common.item.CellItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class FluidModule {
    public static final UnorderedRegistry<LiquidMaterial, Fluid> LIQUID_FLUIDS = new UnorderedRegistry<>();
    public static final UnorderedRegistry<GasMaterial, Fluid> GAS_FLUIDS = new UnorderedRegistry<>();
    public static final UnorderedRegistry<FluidMaterial, Fluid> FLUIDS = new UnorderedRegistry<>();

    public static void registerLiquid(LiquidMaterial material, Fluid fluid) {
        LIQUID_FLUIDS.register(material, fluid);
        FLUIDS.register(material, fluid);
    }

    public static void registerLiquid(MaterialLiquidFluid fluid) {
        LIQUID_FLUIDS.register(fluid.material(), fluid);
        FLUIDS.register(fluid.material(), fluid);
    }

    public static void registerGas(GasMaterial material, Fluid fluid) {
        GAS_FLUIDS.register(material, fluid);
        FLUIDS.register(material, fluid);
    }

    public static void registerGas(MaterialGasFluid fluid) {
        GAS_FLUIDS.register(fluid.material(), fluid);
        FLUIDS.register(fluid.material(), fluid);
    }

    public static ItemStack getFluidCellItem(Fluid fluid) {
        return CellItem.createFilled(fluid);
    }

    public static ItemStack filledCellItem(FluidMaterial material) {
        if (FLUIDS.containsKey(material)) return CellItem.createFilled(material);
        return emptyCellItem();
    }

    public static ItemStack emptyCellItem() {
        return CellItem.createEmpty();
    }

    public static FluidMaterial getFluidMaterial(Fluid fluid) {
        return FLUIDS.getKeyForValue(fluid);
    }

    public static Fluid getFluid(FluidMaterial material) {
        if (material instanceof LiquidMaterial liquidMaterial && LIQUID_FLUIDS.containsKey(liquidMaterial))
            return LIQUID_FLUIDS.get(liquidMaterial);
        if (material instanceof GasMaterial gasMaterial && GAS_FLUIDS.containsKey(gasMaterial))
            return GAS_FLUIDS.get(gasMaterial);
        return null;
    }
}