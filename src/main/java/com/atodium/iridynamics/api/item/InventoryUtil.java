package com.atodium.iridynamics.api.item;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class InventoryUtil {
    public static Inventory inventory(int slots) {
        return inventory(slots, 64);
    }

    public static Inventory inventory(int slots, int slotLimit) {
        return inventory(slots, (slot) -> slotLimit);
    }

    public static Inventory inventory(int slots, SlotLimitSupplier slotLimit) {
        return new Inventory(slots, slotLimit);
    }

    public static PredicateInventory predicateInventory(int slots, ItemPredicate predicate) {
        return predicateInventory(slots, 64, predicate);
    }

    public static PredicateInventory predicateInventory(int slots, int slotLimit, ItemPredicate predicate) {
        return predicateInventory(slots, (slot) -> slotLimit, predicate);
    }

    public static PredicateInventory predicateInventory(int slots, SlotLimitSupplier slotLimit, ItemPredicate predicate) {
        return new PredicateInventory(slots, slotLimit, predicate);
    }

    public static class Inventory extends ItemStackHandler {
        private final SlotLimitSupplier slotLimit;

        public Inventory(int slots, SlotLimitSupplier slotLimit) {
            super(slots);
            this.slotLimit = slotLimit;
        }

        public ItemStack take(int slot) {
            this.validateSlotIndex(slot);
            ItemStack stack = this.getStackInSlot(slot);
            this.setStackInSlot(slot, ItemStack.EMPTY);
            return stack;
        }

        public ItemStack put(int slot, ItemStack stack) {
            return this.insertItem(slot, stack, false);
        }

        @Override
        public int getSlotLimit(int slot) {
            return this.slotLimit.slotLimit(slot);
        }
    }

    public static class PredicateInventory extends Inventory {
        private final ItemPredicate predicate;

        public PredicateInventory(int slots, SlotLimitSupplier slotLimit, ItemPredicate predicate) {
            super(slots, slotLimit);
            this.predicate = predicate;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return this.predicate.test(slot, stack);
        }
    }

    @FunctionalInterface
    public interface SlotLimitSupplier {
        int slotLimit(int slot);
    }

    @FunctionalInterface
    public interface ItemPredicate {
        boolean test(int slot, ItemStack stack);
    }
}