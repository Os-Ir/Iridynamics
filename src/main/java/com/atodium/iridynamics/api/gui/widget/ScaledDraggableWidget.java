package com.atodium.iridynamics.api.gui.widget;

import com.atodium.iridynamics.api.gui.ModularContainer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ScaledDraggableWidget extends WidgetBase {
    private final IDraggableRenderer renderer;
    private IMouseEventListener hoveredListener, clickListener, releaseListener;
    private float scale, moveX, moveY;

    public ScaledDraggableWidget(int x, int y, int width, int height, IDraggableRenderer renderer) {
        super(x, y, width, height);
        this.renderer = renderer;
        this.scale = 1.0f;
    }

    @Override
    public WidgetBase setRenderer(IWidgetRenderer renderer) {
        throw new UnsupportedOperationException();
    }

    public ScaledDraggableWidget setHoveredListener(IMouseEventListener hoveredListener) {
        this.hoveredListener = hoveredListener;
        return this;
    }


    public ScaledDraggableWidget setClickListener(IMouseEventListener clickListener) {
        this.clickListener = clickListener;
        return this;
    }

    public ScaledDraggableWidget setReleaseListener(IMouseEventListener releaseListener) {
        this.releaseListener = releaseListener;
        return this;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderBg(PoseStack transform, float partialTicks, int mouseX, int mouseY, int guiLeft, int guiTop) {
        this.renderer.draw(this.info.getContainer(), transform, guiLeft + this.x, guiTop + this.y, this.width, this.height, this.moveX, this.moveY, this.scale);
    }

    @Override
    public void onMouseHovered(int mouseX, int mouseY) {
        if (this.hoveredListener != null)
            this.hoveredListener.mouseEvent(this.info.getContainer(), this.moveX, this.moveY, this.scale, mouseX, mouseY, 0);
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (this.clickListener == null) return false;
        this.clickListener.mouseEvent(this.info.getContainer(), this.moveX, this.moveY, this.scale, mouseX, mouseY, button);
        return true;
    }

    @Override
    public boolean onMouseReleased(double mouseX, double mouseY, int button) {
        if (this.releaseListener == null) return false;
        this.releaseListener.mouseEvent(this.info.getContainer(), this.moveX, this.moveY, this.scale, mouseX, mouseY, button);
        return true;
    }

    @Override
    public boolean onMouseClickMove(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0) {
            this.moveX += dragX;
            this.moveY += dragY;
        }
        return true;
    }

    @Override
    public boolean onMouseScrolled(double mouseX, double mouseY, double move) {
        this.scale = (float) Math.max(this.scale + move * 0.2f, 0.2f);
        return true;
    }

    @FunctionalInterface
    public interface IDraggableRenderer {
        void draw(ModularContainer container, PoseStack transform, float x, float y, float width, float height, float moveX, float moveY, float scale);
    }

    @FunctionalInterface
    public interface IMouseEventListener {
        void mouseEvent(ModularContainer container, float moveX, float moveY, float scale, double mouseX, double mouseY, int button);
    }
}