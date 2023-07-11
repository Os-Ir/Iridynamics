package com.atodium.iridynamics.api.gui.widget;

import com.atodium.iridynamics.api.gui.ISyncedWidgetList;
import com.atodium.iridynamics.api.gui.ModularGuiInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WidgetBase implements IWidget {
    protected ModularGuiInfo info;
    protected ISyncedWidgetList widgetList;
    protected int x, y, width, height;
    protected IWidgetRenderer renderer;
    protected int id;
    protected boolean isEnable;

    public WidgetBase(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isEnable = true;
    }

    public WidgetBase setRenderer(IWidgetRenderer renderer) {
        this.renderer = renderer;
        return this;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderBg(PoseStack transform, float partialTicks, int mouseX, int mouseY, int guiLeft, int guiTop) {
        if (this.renderer != null)
            this.renderer.draw(transform, guiLeft + this.x, guiTop + this.y, this.width, this.height);
    }

    @Override
    public ModularGuiInfo getGuiInfo() {
        return this.info;
    }

    @Override
    public void setGuiInfo(ModularGuiInfo info) {
        this.info = info;
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
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean isEnable() {
        return this.isEnable;
    }

    @Override
    public void setEnable(boolean enable) {
        this.isEnable = enable;
    }
}