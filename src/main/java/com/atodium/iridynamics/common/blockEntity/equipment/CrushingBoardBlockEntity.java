package com.atodium.iridynamics.common.blockEntity.equipment;

import com.atodium.iridynamics.api.blockEntity.ITickable;
import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.api.capability.HeatCapability;
import com.atodium.iridynamics.api.heat.IHeat;
import com.atodium.iridynamics.api.item.InventoryUtil;
import com.atodium.iridynamics.api.recipe.ModRecipeTypes;
import com.atodium.iridynamics.api.recipe.RecipeUtil;
import com.atodium.iridynamics.api.recipe.container.ItemStackContainer;
import com.atodium.iridynamics.api.recipe.impl.CrushingRecipe;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemHandlerHelper;

public class CrushingBoardBlockEntity extends SyncedBlockEntity implements ITickable {
    private boolean recipeUpdateFlag;
    private CrushingRecipe recipe;
    private final InventoryUtil.Inventory inventory;
    private int progress;

    public CrushingBoardBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRUSHING_BOARD.get(), pos, state);
        this.inventory = InventoryUtil.inventory(1);
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!this.level.isClientSide && this.recipeUpdateFlag) {
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

    public boolean crush(Player player) {
        if (this.recipe == null || this.recipe.temperature() > this.getContentTemperature()) return false;
        if (this.progress == this.recipe.count() - 1) {
            this.progress = 0;
            ItemStackContainer container = RecipeUtil.container(this.inventory.getStackInSlot(0));
            ItemStack result = this.recipe.assemble(container);
            this.recipe.consume(container);
            if (this.inventory.getStackInSlot(0).isEmpty()) this.inventory.setStackInSlot(0, result);
            else ItemHandlerHelper.giveItemToPlayer(player, result);
            this.markForItemChange();
        } else {
            this.progress++;
            this.markDirty();
        }
        return true;
    }

    public double getContentTemperature() {
        LazyOptional<IHeat> optional = this.inventory.getStackInSlot(0).getCapability(HeatCapability.HEAT);
        return optional.isPresent() ? optional.orElseThrow(NullPointerException::new).getTemperature() : 0.0;
    }

    public boolean isEmpty() {
        return this.inventory.getStackInSlot(0).isEmpty();
    }

    public InventoryUtil.Inventory getInventory() {
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
}