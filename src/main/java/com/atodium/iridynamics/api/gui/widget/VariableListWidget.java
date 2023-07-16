package com.atodium.iridynamics.api.gui.widget;

import com.atodium.iridynamics.api.gui.ISyncedWidgetList;
import com.atodium.iridynamics.api.gui.ModularGuiInfo;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class VariableListWidget implements IWidget, ISyncedWidgetList {
    protected ModularGuiInfo guiInfo;
    protected ISyncedWidgetList widgetList;
    protected int id;
    protected final Map<Integer, IWidget> widgets;

    public VariableListWidget() {
        this.widgets = new HashMap<>();
    }

    public VariableListWidget addWidget(int widgetId, IWidget widget) {
        if (this.widgets.containsKey(widgetId)) this.removeWidget(widgetId);
        widget.setWidgetList(this);
        this.widgets.put(widgetId, widget);
        if (widget instanceof ISlotWidget && this.guiInfo != null) this.notifySlotChange((ISlotWidget) widget, true);
        return this;
    }

    public VariableListWidget removeWidget(int widgetId) {
        if (this.widgets.containsKey(widgetId)) {
            IWidget widget = this.widgets.get(widgetId);
            widget.setWidgetList(null);
            if (widget instanceof ISlotWidget) this.notifySlotChange((ISlotWidget) widget, false);
            this.widgets.remove(widgetId);
        }
        return this;
    }

    public int getWidgetsCount() {
        return this.widgets.size();
    }

    public IWidget getWidget(int widgetId) {
        return this.widgets.get(widgetId);
    }

    public Map<Integer, IWidget> getAllWidgets() {
        return ImmutableMap.copyOf(this.widgets);
    }

    public Map<Integer, ISlotWidget> getAllSlotWidgets() {
        return this.widgets.entrySet().stream().filter((entry) -> entry.getValue() instanceof ISlotWidget).collect(Collectors.toMap(Map.Entry::getKey, (entry) -> (ISlotWidget) entry.getValue()));
    }

    @Override
    public void writeToClient(int widgetId, Consumer<FriendlyByteBuf> consumer) {
        this.writePacketToClient(this.id, (buf) -> {
            buf.writeInt(widgetId);
            consumer.accept(buf);
        });
    }

    @Override
    public void writeToServer(int widgetId, Consumer<FriendlyByteBuf> consumer) {
        this.writePacketToServer(this.id, (buf) -> {
            buf.writeInt(widgetId);
            consumer.accept(buf);
        });
    }

    @Override
    public void notifySlotChange(ISlotWidget widget, boolean isEnable) {
        this.guiInfo.getContainer().notifySlotChange(widget, isEnable);
    }

    @Override
    public ModularGuiInfo getGuiInfo() {
        return this.guiInfo;
    }

    @Override
    public void setGuiInfo(ModularGuiInfo info) {
        this.guiInfo = info;
    }

    @Override
    public ISyncedWidgetList getWidgetList() {
        return this.widgetList;
    }

    @Override
    public void setWidgetList(ISyncedWidgetList list) {
        this.widgetList = list;
    }

    @Override
    public int getWidgetId() {
        return this.id;
    }

    @Override
    public void setWidgetId(int id) {
        this.id = id;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public void setWidth(int width) {

    }

    @Override
    public void setHeight(int height) {

    }

    @Override
    public int getX() {
        return 0;
    }

    @Override
    public int getY() {
        return 0;
    }

    @Override
    public void setX(int x) {

    }

    @Override
    public void setY(int y) {

    }

    @Override
    public boolean isEnable() {
        return true;
    }

    @Override
    public void setEnable(boolean enable) {

    }

    @Override
    public boolean isInRange(double x, double y) {
        for (IWidget widget : this.widgets.values()) if (widget.isInRange(x, y)) return true;
        return false;
    }

    @Override
    public void initWidget(ModularGuiInfo info) {
        this.setGuiInfo(info);
        this.widgets.forEach((id, widget) -> widget.initWidget(info));
    }

    @Override
    public void receiveMessageFromServer(FriendlyByteBuf buf) {
        this.widgets.get(buf.readInt()).receiveMessageFromServer(buf);
    }

    @Override
    public void receiveMessageFromClient(FriendlyByteBuf buf) {
        this.widgets.get(buf.readInt()).receiveMessageFromClient(buf);
    }

    @Override
    public void detectAndSendChanges() {
        this.widgets.forEach((id, widget) -> widget.detectAndSendChanges());
    }

    @Override
    public void onMouseHovered(int mouseX, int mouseY) {
        this.widgets.forEach((id, widget) -> {
            if (widget.isEnable() && widget.isInRange(mouseX, mouseY)) widget.onMouseHovered(mouseX, mouseY);
        });
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        this.widgets.forEach((id, widget) -> {
            if (widget.isEnable() && widget.isInRange(mouseX, mouseY)) widget.onMouseClicked(mouseX, mouseY, button);
        });
        return true;
    }

    @Override
    public boolean onMouseReleased(double mouseX, double mouseY, int button) {
        this.widgets.forEach((id, widget) -> {
            if (widget.isEnable() && widget.isInRange(mouseX, mouseY)) widget.onMouseReleased(mouseX, mouseY, button);
        });
        return true;
    }

    @Override
    public boolean onMouseClickMove(double mouseX, double mouseY, int button, double dragX, double dragY) {
        this.widgets.forEach((id, widget) -> {
            if (widget.isEnable() && widget.isInRange(mouseX, mouseY))
                widget.onMouseClickMove(mouseX, mouseY, button, dragX, dragY);
        });
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderLabels(PoseStack transform, int mouseX, int mouseY, int guiLeft, int guiTop) {
        this.widgets.forEach((id, widget) -> {
            if (widget.isEnable()) widget.renderLabels(transform, mouseX, mouseY, guiLeft, guiTop);
        });
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderBg(PoseStack transform, float partialTicks, int mouseX, int mouseY, int guiLeft, int guiTop) {
        this.widgets.forEach((id, widget) -> {
            if (widget.isEnable()) widget.renderBg(transform, partialTicks, mouseX, mouseY, guiLeft, guiTop);
        });
    }
}