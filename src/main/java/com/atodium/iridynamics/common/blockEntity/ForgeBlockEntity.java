package com.atodium.iridynamics.common.blockEntity;

import com.atodium.iridynamics.api.blockEntity.ITickable;
import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.api.capability.HeatCapability;
import com.atodium.iridynamics.api.heat.HeatUtil;
import com.atodium.iridynamics.api.heat.impl.SolidPhasePortrait;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

public class ForgeBlockEntity extends SyncedBlockEntity implements ITickable {
    public static final double RESISTANCE_ITEM = 0.005;

    private final HeatCapability heat;
    private final Inventory inventory;

    public ForgeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FORGE.get(), pos, state);
        this.heat = new HeatCapability(new SolidPhasePortrait(450000.0), new double[]{0.01, 0.5, 0.1, 0.1, 0.1, 0.1});
        this.inventory = new Inventory();
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide) {
            HeatUtil.blockHeatExchange(level, pos, state, this, false);
            this.inventory.left().getCapability(HeatCapability.HEAT).ifPresent((item) -> HeatUtil.heatExchange(this.heat, item, RESISTANCE_ITEM));
            this.inventory.right().getCapability(HeatCapability.HEAT).ifPresent((item) -> HeatUtil.heatExchange(this.heat, item, RESISTANCE_ITEM));
            this.markDirty();
        }
    }

    public double getTemperature() {
        return this.heat.getTemperature();
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction direction) {
        if (capability == HeatCapability.HEAT) {
            return LazyOptional.of(() -> this.heat).cast();
        }
        return super.getCapability(capability, direction);
    }

    @Override
    protected CompoundTag writeSyncData(CompoundTag tag) {
        tag.put("heat", this.heat.serializeNBT());
        tag.put("inventory", this.inventory.serializeNBT());
        return tag;
    }

    @Override
    protected void readSyncData(CompoundTag tag) {
        this.heat.deserializeNBT(tag.getCompound("heat"));
        this.inventory.deserializeNBT(tag.getCompound("inventory"));
    }

    @Override
    protected void saveToTag(CompoundTag tag) {
        tag.put("heat", this.heat.serializeNBT());
        tag.put("inventory", this.inventory.serializeNBT());
    }

    @Override
    protected void loadFromTag(CompoundTag tag) {
        this.heat.deserializeNBT(tag.getCompound("heat"));
        this.inventory.deserializeNBT(tag.getCompound("inventory"));
    }

    public static class Inventory extends ItemStackHandler {
        public Inventory() {
            super(2);
        }

        public ItemStack left() {
            return this.getStackInSlot(0);
        }

        public ItemStack right() {
            return this.getStackInSlot(1);
        }

        public ItemStack take(int slot) {
            this.validateSlotIndex(slot);
            ItemStack stack = this.getStackInSlot(slot);
            this.setStackInSlot(slot, ItemStack.EMPTY);
            return stack;
        }

        public ItemStack takeLeft() {
            ItemStack stack = this.getStackInSlot(0);
            this.setStackInSlot(0, ItemStack.EMPTY);
            return stack;
        }

        public ItemStack takeRight() {
            ItemStack stack = this.getStackInSlot(1);
            this.setStackInSlot(1, ItemStack.EMPTY);
            return stack;
        }

        public ItemStack put(int slot, ItemStack stack) {
            return this.insertItem(slot, stack, false);
        }

        public ItemStack putLeft(ItemStack stack) {
            return this.insertItem(0, stack, false);
        }

        public ItemStack putRight(ItemStack stack) {
            return this.insertItem(1, stack, false);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.getCapability(HeatCapability.HEAT).isPresent();
        }
    }
}