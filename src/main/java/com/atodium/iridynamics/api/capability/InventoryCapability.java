package com.atodium.iridynamics.api.capability;

import com.atodium.iridynamics.Iridynamics;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public class InventoryCapability implements IItemHandlerModifiable, ICapabilitySerializable<CompoundTag> {
    public static final ResourceLocation KEY = Iridynamics.rl("forging");
    public static final Capability<IItemHandlerModifiable> INVENTORY = CapabilityManager.get(new CapabilityToken<>() {
    });

    private final ItemStackHandler inventory;

    public InventoryCapability(int slots) {
        this.inventory = new ItemStackHandler(slots);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == INVENTORY) return LazyOptional.of(() -> this).cast();
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return this.inventory.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.inventory.deserializeNBT(tag);
    }

    @Override
    public int getSlots() {
        return this.inventory.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.inventory.getStackInSlot(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return this.inventory.insertItem(slot, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return this.inventory.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return this.inventory.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return this.inventory.isItemValid(slot, stack);
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        this.inventory.setStackInSlot(slot, stack);
    }
}