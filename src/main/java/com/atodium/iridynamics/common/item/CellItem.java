package com.atodium.iridynamics.common.item;

import com.atodium.iridynamics.api.fluid.FluidModule;
import com.atodium.iridynamics.api.material.type.FluidMaterial;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class CellItem extends Item {
    public static final int CAPACITY = 1000;

    public CellItem(Item.Properties properties) {
        super(properties);
    }

    public static boolean isCellItem(ItemStack stack) {
        return stack.getItem() instanceof CellItem;
    }

    public static FluidMaterial getCellItemMaterial(ItemStack stack) {
        if (!isCellItem(stack)) return null;
        LazyOptional<IFluidHandlerItem> optional = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
        if (!optional.isPresent()) return null;
        IFluidHandlerItem container = optional.orElseThrow(NullPointerException::new);
        return container.getFluidInTank(0).isEmpty() ? null : FluidModule.getFluidMaterial(container.getFluidInTank(0).getFluid());
    }

    public static ItemStack createEmpty() {
        return createEmpty(1);
    }

    public static ItemStack createEmpty(int count) {
        return new ItemStack(ModItems.CELL.get(), count);
    }

    public static ItemStack createFilled(Fluid fluid) {
        return createFilled(fluid, 1);
    }

    public static ItemStack createFilled(Fluid fluid, int count) {
        ItemStack stack = createEmpty(count);
        stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent((container) -> container.fill(new FluidStack(fluid, CAPACITY), IFluidHandler.FluidAction.EXECUTE));
        return stack;
    }

    public static ItemStack createFilled(FluidMaterial material) {
        return createFilled(material, 1);
    }

    public static ItemStack createFilled(FluidMaterial material, int count) {
        ItemStack stack = createEmpty(count);
        stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent((container) -> container.fill(new FluidStack(FluidModule.getFluid(material), CAPACITY), IFluidHandler.FluidAction.EXECUTE));
        return stack;
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> list) {
        if (this.allowdedIn(tab)) {
            list.add(createEmpty());
            FluidModule.FLUIDS.values().forEach((fluid) -> list.add(createFilled(fluid)));
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        FluidMaterial material = getCellItemMaterial(stack);
        return material == null ? new TranslatableComponent("item.iridynamics.empty_cell") : new TranslatableComponent("item.iridynamics.cell", material.getLocalizedName());
    }
}