package com.atodium.iridynamics.common.blockEntity.equipment;

import com.atodium.iridynamics.api.blockEntity.ITickable;
import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.api.capability.ForgingCapability;
import com.atodium.iridynamics.api.capability.HeatCapability;
import com.atodium.iridynamics.api.capability.IForging;
import com.atodium.iridynamics.api.capability.IHeat;
import com.atodium.iridynamics.api.material.MaterialEntry;
import com.atodium.iridynamics.common.block.equipment.AnvilBlock;
import com.atodium.iridynamics.common.block.ModBlocks;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

public class AnvilBlockEntity extends SyncedBlockEntity implements ITickable {
    private final Inventory inventory;
    private boolean itemUpdateFlag;

    public AnvilBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ANVIL.get(), pos, state);
        this.inventory = new Inventory();
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide && this.itemUpdateFlag) this.updateBlockState();
    }

    public void markItemUpdate() {
        this.itemUpdateFlag = true;
    }

    public void markForItemChange() {
        this.markItemUpdate();
        this.markDirty();
        this.sendSyncPacket();
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public ItemStack takeItem(int slot) {
        ItemStack stack = this.inventory.take(slot);
        if (stack.isEmpty()) return ItemStack.EMPTY;
        this.markForItemChange();
        return stack;
    }

    public ItemStack putItem(int slot, ItemStack stack) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        this.markForItemChange();
        return this.inventory.put(slot, stack);
    }

    public boolean hit(int slot, int x, int y, boolean heavy) {
        LazyOptional<IForging> optional = this.inventory.getStackInSlot(slot).getCapability(ForgingCapability.FORGING);
        if (!optional.isPresent()) return false;
        IForging forging = optional.orElseThrow(NullPointerException::new);
        if (forging.hit(x, y, 0.1, heavy ? 1 : 0)) {
            this.markForItemChange();
            return true;
        }
        return false;
    }

    public boolean carve(int slot, int x, int y) {
        LazyOptional<IForging> optional = this.inventory.getStackInSlot(slot).getCapability(ForgingCapability.FORGING);
        if (!optional.isPresent()) return false;
        IForging forging = optional.orElseThrow(NullPointerException::new);
        if (forging.carve(x, y)) {
            this.markForItemChange();
            return true;
        }
        return false;
    }

    public ItemStack weld() {
        ItemStack left = this.inventory.left();
        ItemStack right = this.inventory.right();
        if (left.isEmpty() || right.isEmpty() || !left.getCapability(HeatCapability.HEAT).isPresent() || !right.getCapability(HeatCapability.HEAT).isPresent())
            return ItemStack.EMPTY;
        MaterialEntry entryLeft = MaterialEntry.getItemMaterialEntry(left);
        MaterialEntry entryRight = MaterialEntry.getItemMaterialEntry(right);
        if (!entryLeft.equals(entryRight) || !entryLeft.shape().hasWeldingResult()) return ItemStack.EMPTY;
        IHeat heatLeft = left.getCapability(HeatCapability.HEAT).orElseThrow(NullPointerException::new);
        IHeat heatRight = right.getCapability(HeatCapability.HEAT).orElseThrow(NullPointerException::new);
        double point = entryLeft.material().getHeatInfo().getMeltingPoint() * 0.9;
        if (point > heatLeft.getTemperature() || point > heatRight.getTemperature()) return ItemStack.EMPTY;
        this.inventory.takeLeft();
        this.inventory.takeRight();
        ItemStack result = MaterialEntry.getMaterialItemStack(entryLeft.shape().getWeldingResult(), entryLeft.material());
        result.getCapability(HeatCapability.HEAT).ifPresent((heat) -> heat.setTemperature((heatLeft.getTemperature() + heatRight.getTemperature()) / 2.0));
        return result;
    }

    public void updateBlockState() {
        this.level.getBlockEntity(this.getBlockPos(), ModBlockEntities.ANVIL.get()).ifPresent((anvil) -> {
            int left = 0;
            int right = 0;
            LazyOptional<IForging> optionalLeft = anvil.inventory.left().getCapability(ForgingCapability.FORGING);
            LazyOptional<IForging> optionalRight = anvil.inventory.right().getCapability(ForgingCapability.FORGING);
            if (optionalLeft.isPresent())
                left = (int) Math.round(optionalLeft.orElseThrow(NullPointerException::new).getMaxThickness());
            if (optionalRight.isPresent())
                right = (int) Math.round(optionalRight.orElseThrow(NullPointerException::new).getMaxThickness());
            this.level.setBlockAndUpdate(this.getBlockPos(), ModBlocks.ANVIL.get().defaultBlockState().setValue(AnvilBlock.DIRECTION, anvil.getBlockState().getValue(AnvilBlock.DIRECTION)).setValue(AnvilBlock.HEIGHT_LEFT, left).setValue(AnvilBlock.HEIGHT_RIGHT, right));
            this.level.setBlockEntity(anvil);
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
            return stack.getCapability(ForgingCapability.FORGING).isPresent();
        }
    }
}