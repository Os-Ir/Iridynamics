package com.atodium.iridynamics.api.recipe.container;

import net.minecraft.world.item.ItemStack;

public class ItemStackContainer extends EmptyContainer {
    private ItemStack stack;

    public ItemStackContainer() {
        this(ItemStack.EMPTY);
    }

    public ItemStackContainer(ItemStack stack) {
        this.stack = stack;
    }

    public void setItemStack(ItemStack stack) {
        this.stack = stack;
    }

    public ItemStack getItemStack() {
        return this.stack;
    }
}