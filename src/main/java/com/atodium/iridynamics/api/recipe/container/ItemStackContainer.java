package com.atodium.iridynamics.api.recipe.container;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ItemStackContainer implements Container {
    private ItemStack stack;

    public ItemStackContainer() {
        this(ItemStack.EMPTY);
    }

    public ItemStackContainer(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public ItemStack getItem() {
        return this.stack.copy();
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot == 0 ? this.stack.copy() : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        if (slot != 1) return ItemStack.EMPTY;
        int remove = Math.min(this.stack.getCount(), count);
        ItemStack result = this.stack.copy();
        result.setCount(remove);
        this.stack.shrink(remove);
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot != 1) return ItemStack.EMPTY;
        ItemStack result = this.stack.copy();
        this.stack = ItemStack.EMPTY;
        return result;
    }

    public void setItemStack(ItemStack stack) {
        this.stack = stack.copy();
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot == 1) this.stack = stack.copy();
    }

    @Override
    public void setChanged() {

    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        this.stack = ItemStack.EMPTY;
    }
}