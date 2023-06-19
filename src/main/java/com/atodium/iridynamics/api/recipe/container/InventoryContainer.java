package com.atodium.iridynamics.api.recipe.container;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;

public class InventoryContainer implements Container {
    private final ItemStack[] stacks;

    public InventoryContainer(int size) {
        this.stacks = new ItemStack[size];
    }

    public ItemStack[][] toGrid(int side) {
        if (this.stacks.length != side * side)
            throw new IllegalArgumentException("Can not transform InventoryContainer with size [ " + this.stacks.length + " ] to a grid with side length [ " + side + " ]");
        ItemStack[][] grid = new ItemStack[side][side];
        for (int i = 0; i < side; i++) for (int j = 0; j < side; j++) grid[i][j] = this.stacks[i * side + j].copy();
        return grid;
    }

    public ItemStack[] getAllItemStacks() {
        return Arrays.copyOf(this.stacks, this.stacks.length);
    }

    public boolean validateSlot(int slot) {
        return 0 <= slot && slot < this.stacks.length;
    }

    @Override
    public int getContainerSize() {
        return this.stacks.length;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getItem(int slot) {
        if (this.validateSlot(slot)) return this.stacks[slot].copy();
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        if (!this.validateSlot(slot)) return ItemStack.EMPTY;
        int remove = Math.min(this.stacks[slot].getCount(), count);
        ItemStack result = this.stacks[slot].copy();
        result.setCount(remove);
        this.stacks[slot].shrink(remove);
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (!this.validateSlot(slot)) return ItemStack.EMPTY;
        ItemStack result = this.stacks[slot].copy();
        this.stacks[slot] = ItemStack.EMPTY;
        return result;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (this.validateSlot(slot)) this.stacks[slot] = stack.copy();
    }

    @Override
    public void setChanged() {

    }

    @Override
    public boolean stillValid(Player p_18946_) {
        return true;
    }

    @Override
    public void clearContent() {
        Arrays.fill(this.stacks, ItemStack.EMPTY);
    }
}