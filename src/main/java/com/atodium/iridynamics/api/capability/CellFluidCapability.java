package com.atodium.iridynamics.api.capability;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.common.item.CellItem;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class CellFluidCapability implements IFluidHandlerItem, ICapabilitySerializable<CompoundTag> {
    public static final ResourceLocation KEY = Iridynamics.rl("cell_fluid");

    private final ItemStack container;
    private FluidStack stack;

    public CellFluidCapability(ItemStack container) {
        this(container, FluidStack.EMPTY);
    }

    public CellFluidCapability(ItemStack container, FluidStack stack) {
        this.container = container;
        this.stack = stack;
    }

    @Override
    public ItemStack getContainer() {
        return this.container;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return this.stack;
    }

    @Override
    public int getTankCapacity(int tank) {
        return CellItem.CAPACITY;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return this.stack.isEmpty() || this.stack.isFluidEqual(stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !this.isFluidValid(0, resource)) return 0;
        if (action.simulate()) {
            if (this.stack.isEmpty()) return Math.min(CellItem.CAPACITY, resource.getAmount());
            if (!this.stack.isFluidEqual(resource)) return 0;
            return Math.min(CellItem.CAPACITY - this.stack.getAmount(), resource.getAmount());
        }
        if (this.stack.isEmpty()) {
            this.stack = new FluidStack(resource, Math.min(CellItem.CAPACITY, resource.getAmount()));
            return this.stack.getAmount();
        }
        if (!this.stack.isFluidEqual(resource)) return 0;
        if (this.stack.getAmount() + resource.getAmount() <= CellItem.CAPACITY) {
            this.stack.grow(resource.getAmount());
            return resource.getAmount();
        }
        int add = CellItem.CAPACITY - this.stack.getAmount();
        this.stack.setAmount(CellItem.CAPACITY);
        return add;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        return this.drain(resource.getAmount(), action);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        int drained = Math.min(maxDrain, this.stack.getAmount());
        FluidStack result = new FluidStack(this.stack, drained);
        if (action.execute() && drained > 0) this.stack.shrink(drained);
        return result;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction direction) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
            return LazyOptional.of(() -> this).cast();
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        this.stack.writeToNBT(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.stack = FluidStack.loadFluidStackFromNBT(tag);
    }
}