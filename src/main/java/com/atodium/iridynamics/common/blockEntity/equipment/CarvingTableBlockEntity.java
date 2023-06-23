package com.atodium.iridynamics.common.blockEntity.equipment;

import com.atodium.iridynamics.api.blockEntity.ITickable;
import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.api.capability.CarvingCapability;
import com.atodium.iridynamics.api.capability.ICarving;
import com.atodium.iridynamics.common.block.equipment.CarvingTableBlock;
import com.atodium.iridynamics.common.block.ModBlocks;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

public class CarvingTableBlockEntity extends SyncedBlockEntity implements ITickable {
    private final Inventory inventory;
    private boolean itemUpdateFlag;

    public CarvingTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CARVING_TABLE.get(), pos, state);
        this.inventory = new Inventory();
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide && this.itemUpdateFlag) this.updateBlockState();
    }

    public void markItemUpdate() {
        this.itemUpdateFlag = true;
    }

    public void markForItemChange() {
        this.markItemUpdate();
        this.markDirty();
        this.markForSync();
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public void updateBlockState() {
        this.level.getBlockEntity(this.getBlockPos(), ModBlockEntities.CARVING_TABLE.get()).ifPresent((table) -> {
            int height = 0;
            LazyOptional<ICarving> optional = table.inventory.getStackInSlot(0).getCapability(CarvingCapability.CARVING);
            if (optional.isPresent()) height = optional.orElseThrow(NullPointerException::new).getOriginalThickness();
            this.level.setBlockAndUpdate(this.getBlockPos(), ModBlocks.CARVING_TABLE.get().defaultBlockState().setValue(CarvingTableBlock.HEIGHT, height));
            this.level.setBlockEntity(table);
        });
    }

    @Override
    protected CompoundTag writeSyncData(CompoundTag tag) {
        tag.put("inventory", this.inventory.serializeNBT());
        return tag;
    }

    @Override
    protected void readSyncData(CompoundTag tag) {
        this.inventory.deserializeNBT(tag.getCompound("inventory"));
    }

    @Override
    protected void saveToTag(CompoundTag tag) {
        tag.put("inventory", this.inventory.serializeNBT());
    }

    @Override
    protected void loadFromTag(CompoundTag tag) {
        this.inventory.deserializeNBT(tag.getCompound("inventory"));
    }

    public static class Inventory extends ItemStackHandler {
        public Inventory() {
            super(1);
        }

        public ItemStack take(int slot) {
            this.validateSlotIndex(slot);
            ItemStack stack = this.getStackInSlot(slot);
            this.setStackInSlot(slot, ItemStack.EMPTY);
            return stack;
        }

        public ItemStack put(int slot, ItemStack stack) {
            return this.insertItem(slot, stack, false);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.getCapability(CarvingCapability.CARVING).isPresent();
        }
    }
}