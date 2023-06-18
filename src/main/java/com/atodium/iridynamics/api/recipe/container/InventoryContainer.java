package com.atodium.iridynamics.api.recipe.container;

import net.minecraft.world.item.ItemStack;

public class InventoryContainer extends EmptyContainer {
    private ItemStack[] stacks;

    public InventoryContainer(ItemStack... stacks) {
        this.stacks = stacks;
    }

    public void setItemStack(int index, ItemStack stack) {
        this.stacks[index] = stack;
    }


    public void setItemStacks(ItemStack[] stacks) {
        this.stacks = stacks;
    }

    public ItemStack getItemStack(int index) {
        return this.stacks[index];
    }

    public ItemStack[] getAllItemStacks() {
        return this.stacks;
    }
}