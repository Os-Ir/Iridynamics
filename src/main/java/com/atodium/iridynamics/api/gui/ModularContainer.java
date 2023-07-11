package com.atodium.iridynamics.api.gui;

import com.atodium.iridynamics.api.gui.widget.ISlotWidget;
import com.atodium.iridynamics.api.gui.widget.IWidget;
import com.atodium.iridynamics.api.gui.widget.VariableListWidget;
import com.atodium.iridynamics.api.recipe.container.EmptyContainer;
import com.atodium.iridynamics.api.util.math.Vector2i;
import com.atodium.iridynamics.network.ModNetworkHandler;
import com.atodium.iridynamics.network.ModularGuiTaskPacket;
import com.atodium.iridynamics.network.ModularToClientPacket;
import com.atodium.iridynamics.network.ModularToServerPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.network.NetworkDirection;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;

public class ModularContainer extends AbstractContainerMenu implements ISyncedWidgetList {
    private final ModularGuiInfo info;
    private final IModularGuiHolder<?>[] parentGuiHolders;
    private final Map<ISlotWidget, Slot> slotMap;
    private final Deque<Pair<Long, Vector2i>> mouseHoveredData;
    private final List<FriendlyByteBuf> blockedData;
    private boolean isDataBlocked;

    public ModularContainer(MenuType<ModularContainer> type, int id, ModularGuiInfo info, IModularGuiHolder<?>[] parentGuiHolders) {
        super(type, id);
        this.info = info;
        info.setContainer(this);
        this.parentGuiHolders = parentGuiHolders;
        this.slotMap = new HashMap<>();
        this.mouseHoveredData = new LinkedList<>();
        this.blockedData = new ArrayList<>();
        this.isDataBlocked = false;
        info.getWidgets().forEach((widget) -> {
            widget.setWidgetList(this);
            if (widget instanceof ISlotWidget && widget.isEnable()) {
                Slot slot = ((ISlotWidget) widget).getSlot();
                this.slotMap.put((ISlotWidget) widget, slot);
                this.addSlot(slot);
                ((ISlotWidget) widget).setSlotCount(slot.index);
            }
            if (widget instanceof VariableListWidget list) {
                list.getAllSlotWidgets().values().forEach((slotWidget) -> {
                    Slot slot = slotWidget.getSlot();
                    this.slotMap.put(slotWidget, slot);
                    this.addSlot(slot);
                    slotWidget.setSlotCount(slot.index);
                });
            }
        });
        info.onGuiOpen();
    }

    public ModularGuiInfo guiInfo() {
        return this.info;
    }

    public Player player() {
        return this.info.getPlayer();
    }

    @SuppressWarnings("unchecked")
    public static List<ContainerListener> getContainerListeners(AbstractContainerMenu container) {
        try {
            return (List<ContainerListener>) ObfuscationReflectionHelper.findField(AbstractContainerMenu.class, "f_38848_").get(container);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public IModularGuiHolder<?> getGuiHolder() {
        return this.parentGuiHolders[this.parentGuiHolders.length - 1];
    }

    public IModularGuiHolder<?>[] getParentGuiHolders() {
        return this.parentGuiHolders;
    }

    public void setDataBlocked(boolean blocked) {
        this.isDataBlocked = blocked;
    }

    public List<FriendlyByteBuf> getBlockedData() {
        return this.blockedData;
    }

    public void clearBlockedData() {
        this.blockedData.clear();
    }

    public void addMouseHoveredData(int mouseX, int mouseY) {
        this.addMouseHoveredData(mouseX, mouseY, System.currentTimeMillis());
    }

    public void addMouseHoveredData(int mouseX, int mouseY, long time) {
        while (!this.mouseHoveredData.isEmpty() && this.mouseHoveredData.getFirst().getLeft() < time - 10000)
            this.mouseHoveredData.removeFirst();
        this.mouseHoveredData.addLast(Pair.of(time, Vector2i.of(mouseX, mouseY)));
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.info.onGuiClosed();
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        List<ContainerListener> listListener = getContainerListeners(this);
        for (int i = 0; i < this.slots.size(); i++) {
            ItemStack stack = this.slots.get(i).getItem();
            if (CapabilityUpdateListener.shouldSync(stack)) for (ContainerListener listener : listListener)
                if (listener instanceof CapabilityUpdateListener) listener.slotChanged(this, i, stack);
        }
        this.info.getWidgets().forEach(IWidget::detectAndSendChanges);
        IModularGuiHolder<?> holder = this.getGuiHolder();
        int[] tasks = holder.getTasksToExecute(this);
        if (tasks.length > 0) {
            for (int id : tasks) holder.executeTask(this, id);
            if (!this.info.getPlayer().level.isClientSide)
                ModNetworkHandler.CHANNEL.sendTo(new ModularGuiTaskPacket(this.containerId, tasks), ((ServerPlayer) this.info.getPlayer()).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    @Override
    public void writeToClient(int widgetId, Consumer<FriendlyByteBuf> consumer) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(widgetId);
        consumer.accept(buf);
        if (this.isDataBlocked) this.blockedData.add(buf);
        else
            ModNetworkHandler.CHANNEL.sendTo(new ModularToClientPacket(buf, this.containerId), ((ServerPlayer) this.info.getPlayer()).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    @Override
    public void writeToServer(int widgetId, Consumer<FriendlyByteBuf> data) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(widgetId);
        data.accept(buf);
        ModNetworkHandler.CHANNEL.sendToServer(new ModularToServerPacket(buf, this.containerId));
    }

    @Override
    public void notifySlotChange(ISlotWidget widget, boolean isEnable) {
        if (isEnable && !this.slotMap.containsKey(widget)) {
            Slot slotAdd = widget.getSlot();
            this.slotMap.put(widget, slotAdd);
            OptionalInt optional = this.slots.stream().filter((slot) -> slot instanceof EmptySlot).mapToInt((slot) -> slot.index).findFirst();
            if (optional.isPresent()) {
                int idx = optional.getAsInt();
                slotAdd.index = idx;
                this.slots.set(idx, slotAdd);
                this.setInventoryItemStacks(idx, ItemStack.EMPTY);
                widget.setSlotCount(idx);
            } else {
                this.addSlot(slotAdd);
                widget.setSlotCount(slotAdd.index);
            }
        } else if (!isEnable && this.slotMap.containsKey(widget)) {
            Slot slotRemove = widget.getSlot();
            this.slotMap.remove(widget);
            EmptySlot emptySlot = new EmptySlot();
            emptySlot.index = slotRemove.index;
            this.slots.set(slotRemove.index, emptySlot);
            this.setInventoryItemStacks(slotRemove.index, ItemStack.EMPTY);
        }
    }

    @SuppressWarnings("unchecked")
    protected void setInventoryItemStacks(int slotNumber, ItemStack stack) {
        try {
            Field fieldItemStacks = ObfuscationReflectionHelper.findField(AbstractContainerMenu.class, "f_38841_");
            NonNullList<ItemStack> lastSlots = (NonNullList<ItemStack>) fieldItemStacks.get(this);
            lastSlots.set(slotNumber, stack);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static class EmptySlot extends Slot {
        public EmptySlot() {
            super(EmptyContainer.INSTANCE, 0, 0, 0);
        }

        @Override
        public ItemStack getItem() {
            return ItemStack.EMPTY;
        }

        @Override
        public void set(ItemStack stack) {

        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public boolean mayPickup(Player player) {
            return false;
        }

        @Override
        public boolean isActive() {
            return false;
        }
    }
}