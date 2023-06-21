package com.atodium.iridynamics.api.gui;

import com.atodium.iridynamics.api.gui.widget.*;
import com.atodium.iridynamics.api.util.data.UnorderedRegistry;
import com.atodium.iridynamics.common.network.ModNetworkHandler;
import com.atodium.iridynamics.common.network.ModularGuiOpenPacket;
import com.mojang.blaze3d.vertex.PoseStack;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;
import net.minecraftforge.network.NetworkDirection;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;


public class ModularGuiInfo {
    public static final UnorderedRegistry<ResourceLocation, IGuiHolderCodec<?>> CODEC = new UnorderedRegistry<>();

    private ModularContainer container;
    private final Player player;
    private final int width, height;
    private Component title;
    private final IWidgetRenderer background;
    private final Map<Integer, IWidget> widgets;
    private final List<Consumer<ModularContainer>> openListeners, closeListeners;
    private int[] args;

    public ModularGuiInfo(int width, int height, IWidgetRenderer background, Map<Integer, IWidget> widgets, List<Consumer<ModularContainer>> openListeners, List<Consumer<ModularContainer>> closeListeners, Player player, int[] args) {
        this.width = width;
        this.height = height;
        this.background = background;
        this.widgets = widgets;
        this.openListeners = openListeners;
        this.closeListeners = closeListeners;
        this.player = player;
        this.args = args;
    }

    public static void openModularGui(IModularGuiHolder<?> holder, ServerPlayer player, int... args) {
        openModularGui(holder, player, new IModularGuiHolder[0], args);
    }

    public static void openModularGui(IModularGuiHolder<?> holder, ServerPlayer player, IModularGuiHolder<?>[] parentHolders, int... args) {
        if (player.level.isClientSide) return;
        if (player instanceof FakePlayer)
            throw new IllegalArgumentException("The player of the gui info can not be fake");
        IGuiHolderCodec<?> codec = holder.getCodec();
        if (!CODEC.containsValue(codec)) throw new IllegalArgumentException("The gui holder codec is unregistered");
        player.doCloseContainer();
        player.nextContainerCounter();
        ModularGuiInfo guiInfo = holder.createGuiInfo(player);
        guiInfo.title = holder.getTitle(player);
        guiInfo.args = args;
        guiInfo.initWidgets();
        ModularContainer container = new ModularContainer(null, player.containerCounter, guiInfo, ArrayUtils.add(parentHolders, holder));
        container.setDataBlocked(true);
        container.broadcastChanges();
        List<FriendlyByteBuf> updateData = new ArrayList<>(container.getBlockedData());
        container.clearBlockedData();
        container.setDataBlocked(false);
        FriendlyByteBuf holderBuf = new FriendlyByteBuf(Unpooled.buffer());
        holderBuf.writeResourceLocation(codec.getRegistryName());
        codec.writeHolder(holderBuf, holder);
        FriendlyByteBuf[] parentHolderBuf = new FriendlyByteBuf[parentHolders.length];
        for (int i = 0; i < parentHolderBuf.length; i++) {
            IGuiHolderCodec<?> parentCodec = parentHolders[i].getCodec();
            if (!CODEC.containsValue(parentCodec))
                throw new IllegalArgumentException("The gui holder codec is unregistered");
            parentHolderBuf[i] = new FriendlyByteBuf(Unpooled.buffer());
            parentHolderBuf[i].writeResourceLocation(parentCodec.getRegistryName());
            parentCodec.writeHolder(parentHolderBuf[i], parentHolders[i]);
        }
        ModNetworkHandler.CHANNEL.sendTo(new ModularGuiOpenPacket(holderBuf, parentHolderBuf, updateData, player.containerCounter, args), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        player.containerMenu = container;
        player.initMenu(container);
        MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, container));
    }

    public static void backToParentGui(ModularContainer container, int... args) {
        IModularGuiHolder<?>[] parentHolders = container.getParentGuiHolders();
        if (parentHolders.length >= 2)
            openModularGui(parentHolders[parentHolders.length - 2], (ServerPlayer) container.getGuiInfo().player, Arrays.copyOf(parentHolders, parentHolders.length - 2), args);
    }

    public static void refreshGui(ModularContainer container, int... args) {
        IModularGuiHolder<?>[] parentHolders = container.getParentGuiHolders();
        openModularGui(parentHolders[parentHolders.length - 1], (ServerPlayer) container.getGuiInfo().player, Arrays.copyOf(parentHolders, parentHolders.length - 1), args);
    }

    @OnlyIn(Dist.CLIENT)
    public static void openClientModularGui(int window, IModularGuiHolder<?> holder, IModularGuiHolder<?>[] parentHolders, List<FriendlyByteBuf> updateTag, int... args) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        ModularGuiInfo guiInfo = holder.createGuiInfo(player);
        guiInfo.title = holder.getTitle(player);
        guiInfo.args = args;
        guiInfo.initWidgets();
        ModularScreen screen = new ModularScreen(window, guiInfo, ArrayUtils.add(parentHolders, holder), player.getInventory(), guiInfo.title);
        updateTag.forEach((buf) -> {
            IWidget widget = guiInfo.container.getGuiInfo().getWidget(buf.readInt());
            widget.receiveMessageFromServer(buf);
        });
        player.containerMenu = guiInfo.container;
        Minecraft.getInstance().setScreen(screen);
    }

    public static void registerGuiHolderCodec(IGuiHolderCodec<?> codec) {
        CODEC.register(codec.getRegistryName(), codec);
    }

    public IWidgetRenderer getBackground() {
        return this.background;
    }

    public void setContainer(ModularContainer container) {
        this.container = container;
    }

    public ModularContainer getContainer() {
        return this.container;
    }

    public Player getPlayer() {
        return this.player;
    }

    public int[] getArgs() {
        return this.args;
    }

    public int getArg(int i) {
        return this.args[i];
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public IWidget getWidget(int id) {
        return this.widgets.get(id);
    }

    public List<IWidget> getWidgets() {
        List<IWidget> list = new ArrayList<>();
        this.widgets.forEach((id, widget) -> list.add(widget));
        return list;
    }

    public void onGuiOpen() {
        this.openListeners.forEach((listener) -> listener.accept(this.container));
    }

    public void onGuiClosed() {
        this.closeListeners.forEach((listener) -> listener.accept(this.container));
    }

    @OnlyIn(Dist.CLIENT)
    public void renderBg(PoseStack transform, float partialTicks, int mouseX, int mouseY, int guiLeft, int guiTop) {
        this.widgets.forEach((id, widget) -> {
            if (widget.isEnable()) widget.renderBg(transform, partialTicks, mouseX, mouseY, guiLeft, guiTop);
        });
    }

    @OnlyIn(Dist.CLIENT)
    public void renderLabels(PoseStack transform, int mouseX, int mouseY, int guiLeft, int guiTop) {
        this.widgets.forEach((id, widget) -> {
            if (widget.isEnable()) widget.renderLabels(transform, mouseX, mouseY, guiLeft, guiTop);
        });
    }

    public void handleMouseHovered(int mouseX, int mouseY) {
        this.container.addMouseHoveredData(mouseX, mouseY);
        this.widgets.forEach((id, widget) -> {
            if (widget.isEnable() && widget.isInRange(mouseX, mouseY)) widget.onMouseHovered(mouseX, mouseY);
        });
    }

    public boolean handleMouseClicked(double mouseX, double mouseY, int button) {
        this.widgets.forEach((id, widget) -> {
            if (widget.isEnable() && widget.isInRange(mouseX, mouseY)) widget.onMouseClicked(mouseX, mouseY, button);
        });
        return true;
    }

    public boolean handleMouseReleased(double mouseX, double mouseY, int button) {
        this.widgets.forEach((id, widget) -> {
            if (widget.isEnable() && widget.isInRange(mouseX, mouseY)) widget.onMouseReleased(mouseX, mouseY, button);
        });
        return true;
    }

    public boolean handleMouseClickMove(double mouseX, double mouseY, int button, double dragX, double dragY) {
        this.widgets.forEach((id, widget) -> {
            if (widget.isEnable() && widget.isInRange(mouseX, mouseY))
                widget.onMouseClickMove(mouseX, mouseY, button, dragX, dragY);
        });
        return true;
    }

    public void initWidgets() {
        this.widgets.forEach((id, widget) -> widget.initWidget(this));
    }

    public static Builder builder() {
        return new Builder(176, 166);
    }

    public static Builder builder(int width, int height) {
        return new Builder(width, height);
    }

    public static class Builder {
        private int internalCount;
        private final int width, height;
        private IWidgetRenderer background;
        private final Map<Integer, IWidget> widgets;
        private final List<Consumer<ModularContainer>> openListeners, closeListeners;

        public Builder(int width, int height) {
            this.internalCount = -1;
            this.width = width;
            this.height = height;
            this.background = ModularScreen.DEFAULT_BACKGROUND;
            this.widgets = new HashMap<>();
            this.openListeners = new ArrayList<>();
            this.closeListeners = new ArrayList<>();
            this.internalWidget(new ButtonWidget(-1, this.width - 19, -9, 9, 9, (data, container) -> backToParentGui(container, container.getGuiInfo().args)).setRenderer(ModularScreen.BACK));
            this.internalWidget(new ButtonWidget(-2, this.width - 9, -9, 9, 9, (data, container) -> refreshGui(container, container.getGuiInfo().args)).setRenderer(ModularScreen.REFRESH));
        }

        public Builder background(IWidgetRenderer background) {
            this.background = background;
            return this;
        }

        public Builder widget(int id, IWidget widget) {
            this.widgets.put(id, widget);
            widget.setWidgetId(id);
            return this;
        }

        public Builder slot(int id, int x, int y, IItemHandler inventory, int index) {
            return this.slot(id, x, y, inventory, index, true, true);
        }

        public Builder slot(int id, int x, int y, IItemHandler inventory, int index, boolean canPutItems, boolean canTakeItems) {
            SlotWidget widget = new SlotWidget(x, y, inventory, index, canPutItems, canTakeItems);
            this.widgets.put(id, widget);
            widget.setWidgetId(id);
            return this;
        }

        public Builder renderer(int id, int x, int y, int width, int height, IWidgetRenderer renderer) {
            RendererWidget widget = new RendererWidget(x, y, width, height, renderer);
            this.widgets.put(id, widget);
            widget.setWidgetId(id);
            return this;
        }

        public Builder renderer(int id, int x, int y, int width, int height, RendererWidget.IContainerRenderer renderer) {
            RendererWidget widget = new RendererWidget(x, y, width, height, renderer);
            this.widgets.put(id, widget);
            widget.setWidgetId(id);
            return this;
        }

        public Builder progress(int id, int x, int y, int width, int height, MoveType moveType, Supplier<Float> supplier, TextureArea texture) {
            ProgressWidget widget = new ProgressWidget(x, y, width, height, supplier, moveType);
            widget.setTexture(texture);
            this.widgets.put(id, widget);
            widget.setWidgetId(id);
            return this;
        }

        private Builder internalWidget(IWidget widget) {
            this.widgets.put(this.internalCount, widget);
            widget.setWidgetId(this.internalCount);
            this.internalCount--;
            return this;
        }

        public Builder playerInventory(Inventory inventory) {
            return this.playerInventory(inventory, 8, 84, false);
        }

        public Builder playerInventory(Inventory inventory, int x, int y) {
            return this.playerInventory(inventory, x, y, false);
        }

        public Builder playerInventory(Inventory inventory, boolean lockSelectedSlot) {
            return this.playerInventory(inventory, 8, 84, lockSelectedSlot);
        }

        public Builder playerInventory(Inventory inventory, int x, int y, boolean lockSelectedSlot) {
            for (int i = 0; i < 9; i++) {
                if (lockSelectedSlot && inventory.selected == i)
                    this.internalWidget(new SlotWidget(x + i * 18, y + 58, new PlayerMainInvWrapper(inventory), i, false, false));
                else this.internalWidget(new SlotWidget(x + i * 18, y + 58, new PlayerMainInvWrapper(inventory), i));
            }
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 9; j++)
                    this.internalWidget(new SlotWidget(x + j * 18, y + i * 18, new PlayerMainInvWrapper(inventory), (i + 1) * 9 + j));
            return this;
        }

        public Builder openListener(Consumer<ModularContainer> listener) {
            this.openListeners.add(listener);
            return this;
        }

        public Builder closeListener(Consumer<ModularContainer> listener) {
            this.closeListeners.add(listener);
            return this;
        }

        public ModularGuiInfo build(Player player, int... args) {
            if (player instanceof FakePlayer)
                throw new IllegalArgumentException("The player of the gui info can't be fake");
            return new ModularGuiInfo(this.width, this.height, this.background, this.widgets, this.openListeners, this.closeListeners, player, args);
        }
    }
}