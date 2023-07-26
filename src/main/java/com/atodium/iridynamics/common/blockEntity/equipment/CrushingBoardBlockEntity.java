package com.atodium.iridynamics.common.blockEntity.equipment;

import com.atodium.iridynamics.api.blockEntity.ITickable;
import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.api.recipe.ModRecipeTypes;
import com.atodium.iridynamics.api.recipe.RecipeUtil;
import com.atodium.iridynamics.api.recipe.impl.CrushingRecipe;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

public class CrushingBoardBlockEntity extends SyncedBlockEntity implements ITickable {
    private boolean recipeUpdateFlag;
    private CrushingRecipe recipe;
    private final Inventory inventory;
    private int progress;

    public CrushingBoardBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRUSHING_BOARD.get(), pos, state);
        this.inventory = new Inventory();
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (this.recipeUpdateFlag) {
            this.updateRecipe();
            this.recipeUpdateFlag = false;
        }
    }

    public void markRecipeUpdate() {
        this.recipeUpdateFlag = true;
    }

    public void markForItemChange() {
        this.markRecipeUpdate();
        this.markDirty();
        this.sendSyncPacket();
    }

    public void updateRecipe() {
        this.recipe = RecipeUtil.getRecipe(this.level, ModRecipeTypes.CRUSHING.get(), RecipeUtil.container(this.inventory.getStackInSlot(0)));
        if (this.recipe == null) this.progress = 0;
    }

    public boolean crush() {
        if (this.recipe == null) return false;
        if (this.progress == this.recipe.count() - 1) {
            this.progress = 0;
            this.inventory.setStackInSlot(0, this.recipe.assemble(RecipeUtil.container(this.inventory.getStackInSlot(0))));
            this.markForItemChange();
        } else {
            this.progress++;
            this.markDirty();
        }
        return true;
    }

    public boolean isEmpty() {
        return this.inventory.getStackInSlot(0).isEmpty();
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public ItemStack takeItem() {
        ItemStack stack = this.inventory.take(0);
        if (stack.isEmpty()) return ItemStack.EMPTY;
        this.markForItemChange();
        return stack;
    }


    public ItemStack addItem(ItemStack stack) {
        if (this.inventory.getStackInSlot(0).isEmpty()) {
            ItemStack result = this.inventory.insertItem(0, stack, false);
            this.markForItemChange();
            return result;
        }
        return stack;
    }

    @Override
    protected void writeSyncData(CompoundTag tag) {
        tag.put("inventory", this.inventory.serializeNBT());
        tag.putInt("progress", this.progress);
    }

    @Override
    protected void readSyncData(CompoundTag tag) {
        this.inventory.deserializeNBT(tag.getCompound("inventory"));
        this.progress = tag.getInt("progress");
        this.markRecipeUpdate();
    }

    @Override
    protected void saveToTag(CompoundTag tag) {
        tag.put("inventory", this.inventory.serializeNBT());
        tag.putInt("progress", this.progress);
    }

    @Override
    protected void loadFromTag(CompoundTag tag) {
        this.inventory.deserializeNBT(tag.getCompound("inventory"));
        this.progress = tag.getInt("progress");
        this.markRecipeUpdate();
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

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
    }
}