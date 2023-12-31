package com.atodium.iridynamics.common.blockEntity.equipment;

import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.api.recipe.ModRecipeTypes;
import com.atodium.iridynamics.api.recipe.RecipeUtil;
import com.atodium.iridynamics.api.recipe.container.ItemStackContainer;
import com.atodium.iridynamics.api.recipe.impl.WashingRecipe;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class BasinBlockEntity extends SyncedBlockEntity {
    public BasinBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BASIN.get(), pos, state);
    }

    public ItemStack[] wash(ItemStack stack) {
        WashingRecipe recipe = RecipeUtil.getRecipe(this.level, ModRecipeTypes.WASHING.get(), RecipeUtil.container(stack));
        if (recipe == null) return new ItemStack[0];
        ItemStack[] output = new ItemStack[recipe.outputCount()];
        ItemStackContainer container = new ItemStackContainer(stack);
        for (int i = 0; i < output.length; i++) output[i] = recipe.assemble(container);
        recipe.consume(stack);
        return output;
    }

    @Override
    protected void writeSyncData(CompoundTag tag) {

    }

    @Override
    protected void readSyncData(CompoundTag tag) {

    }

    @Override
    protected void saveToTag(CompoundTag tag) {

    }

    @Override
    protected void loadFromTag(CompoundTag tag) {

    }
}