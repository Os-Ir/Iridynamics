package com.atodium.iridynamics.api.recipe.impl;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public interface ISpecialRecipe<T extends Container> extends Recipe<T> {
    @Override
    default ItemStack assemble(T container) {
        return getResultItem().copy();
    }

    @Override
    default boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    default boolean isSpecial() {
        return true;
    }
}