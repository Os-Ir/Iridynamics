package com.atodium.iridynamics.api.gui.widget;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotWidget extends WidgetBase implements ISlotWidget {
    private final Slot slot;
    private int count;
    private final boolean canTakeItems, canPutItems;

    public SlotWidget(int x, int y, IItemHandler inventory, int index) {
        this(x, y, inventory, index, true, true);
    }

    public SlotWidget(int x, int y, IItemHandler inventory, int index, boolean canPutItems, boolean canTakeItems) {
        super(x, y, 16, 16);
        this.slot = new WidgetSlotItemHandler(inventory, index, this.x, this.y);
        this.canPutItems = canPutItems;
        this.canTakeItems = canTakeItems;
    }

    @Override
    public void setSlotCount(int count) {
        this.count = count;
    }

    @Override
    public int getSlotCount() {
        return this.count;
    }

    @Override
    public Slot getSlot() {
        return this.slot;
    }

    @Override
    public void setEnable(boolean enable) {
        if (this.isEnable ^ enable && this.widgetList != null) this.widgetList.notifySlotChange(this, enable);
        this.isEnable = enable;
    }

    public boolean canTakeItem(Player player) {
        return this.isEnable && this.canTakeItems;
    }

    public boolean canPutItem(ItemStack stack) {
        return this.isEnable && this.canPutItems;
    }

    protected class WidgetSlotItemHandler extends SlotItemHandler {
        public WidgetSlotItemHandler(IItemHandler inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return super.mayPlace(stack) && SlotWidget.this.canPutItem(stack);
        }

        @Override
        public boolean mayPickup(Player player) {
            return super.mayPickup(player) && SlotWidget.this.canTakeItem(player);
        }

        @Override
        public boolean isActive() {
            return SlotWidget.this.isEnable;
        }
    }
}