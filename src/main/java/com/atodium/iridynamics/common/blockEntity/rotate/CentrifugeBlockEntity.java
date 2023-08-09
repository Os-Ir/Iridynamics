package com.atodium.iridynamics.common.blockEntity.rotate;

import com.atodium.iridynamics.api.blockEntity.RotateMachineBlockEntity;
import com.atodium.iridynamics.api.item.InventoryUtil;
import com.atodium.iridynamics.api.item.ItemUtil;
import com.atodium.iridynamics.api.recipe.ModRecipeTypes;
import com.atodium.iridynamics.api.recipe.RecipeUtil;
import com.atodium.iridynamics.api.recipe.container.ItemStackContainer;
import com.atodium.iridynamics.api.recipe.impl.CentrifugeRecipe;
import com.atodium.iridynamics.common.block.rotate.CentrifugeBlock;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CentrifugeBlockEntity extends RotateMachineBlockEntity {
    public static final double INERTIA = 30.0, FRICTION = 1.0;

    private boolean recipeUpdateFlag;
    private final InventoryUtil.Inventory inventory;
    private CentrifugeRecipe recipe;
    private double progress;

    public CentrifugeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CENTRIFUGE.get(), pos, state);
        this.inventory = InventoryUtil.inventory(1);
        this.setInertia(INERTIA);
        this.setFriction(FRICTION);
    }

    @Override
    public Direction direction() {
        return this.getBlockState().getValue(CentrifugeBlock.DIRECTION);
    }

    @Override
    public void blockTick(Level level, BlockPos pos, BlockState state) {
        if (this.level.isClientSide) return;
        if (this.recipeUpdateFlag) {
            this.updateRecipe();
            this.recipeUpdateFlag = false;
        }
        if (this.recipe != null) {
            this.progress += this.tickAngleChange();
            if (this.progress >= this.recipe.angle()) {
                this.progress = 0.0;
                ItemStackContainer container = RecipeUtil.container(this.inventory.getStackInSlot(0));
                ItemStack result = this.recipe.assemble(container);
                this.recipe.consume(container);
                if (this.inventory.getStackInSlot(0).isEmpty()) this.inventory.setStackInSlot(0, result);
                else ItemUtil.spawnItem(this.level, this.getBlockPos(), result);
                this.markForItemChange();
            } else this.markDirty();
        }
        super.blockTick(level, pos, state);
    }

    public void markRecipeUpdate() {
        this.recipeUpdateFlag = true;
    }

    public void markForItemChange() {
        this.markRecipeUpdate();
        this.markDirty();
    }

    public void updateRecipe() {
        this.recipe = RecipeUtil.getRecipe(this.level, ModRecipeTypes.CENTRIFUGE.get(), RecipeUtil.container(this.inventory.getStackInSlot(0)));
        if (this.recipe == null) {
            this.progress = 0.0;
            this.setFriction(FRICTION);
        } else this.setFriction(FRICTION + this.recipe.torque());
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
    protected void saveToTag(CompoundTag tag) {
        tag.put("inventory", this.inventory.serializeNBT());
    }

    @Override
    protected void loadFromTag(CompoundTag tag) {
        this.inventory.deserializeNBT(tag.getCompound("inventory"));
    }
}