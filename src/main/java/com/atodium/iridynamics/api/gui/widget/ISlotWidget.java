package com.atodium.iridynamics.api.gui.widget;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public interface ISlotWidget extends IWidget {
    ItemStack TAG = ItemStack.EMPTY.copy();

    Slot getSlot();

    void setSlotCount(int count);

    int getSlotCount();

    default boolean canMergeSlot(ItemStack stack) {
        return false;
    }

    default ItemStack onItemTake(Player player, ItemStack stack) {
        return TAG;
    }
}