package com.atodium.iridynamics.common.blockEntity.equipment;

import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.api.capability.CarvingCapability;
import com.atodium.iridynamics.api.capability.ICarving;
import com.atodium.iridynamics.api.item.InventoryUtil;
import com.atodium.iridynamics.common.block.ModBlocks;
import com.atodium.iridynamics.common.block.equipment.CarvingTableBlock;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;

public class CarvingTableBlockEntity extends SyncedBlockEntity {
    private final InventoryUtil.Inventory inventory;

    public CarvingTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CARVING_TABLE.get(), pos, state);
        this.inventory = InventoryUtil.predicateInventory(1, 1, (slot, stack) -> validateItem(stack));
    }

    public static boolean validateItem(ItemStack stack) {
        return stack.getCapability(CarvingCapability.CARVING).isPresent();
    }

    public void markForItemChange() {
        this.updateBlockState();
        this.markDirty();
        this.sendSyncPacket();
    }

    public InventoryUtil.Inventory getInventory() {
        return this.inventory;
    }

    public boolean isEmpty() {
        return this.inventory.getStackInSlot(0).isEmpty();
    }

    public ItemStack takeItem() {
        ItemStack stack = this.inventory.take(0);
        if (stack.isEmpty()) return ItemStack.EMPTY;
        this.markForItemChange();
        return stack;
    }

    public ItemStack addItem(ItemStack stack) {
        if (this.inventory.getStackInSlot(0).isEmpty() && validateItem(stack)) {
            ItemStack result = this.inventory.insertItem(0, stack, false);
            this.markForItemChange();
            return result;
        }
        return stack;
    }

    public boolean carve(int x, int y) {
        if (this.isEmpty()) return false;
        ICarving carving = this.inventory.getStackInSlot(0).getCapability(CarvingCapability.CARVING).orElseThrow(NullPointerException::new);
        if (carving.carve(x, y)) {
            this.markForItemChange();
            return true;
        }
        return false;
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
    protected void writeSyncData(CompoundTag tag) {
        tag.put("inventory", this.inventory.serializeNBT());
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
}