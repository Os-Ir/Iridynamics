package com.atodium.iridynamics.api.gui.widget;

import com.atodium.iridynamics.api.gui.ModularContainer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ScaledDraggableWidget extends WidgetBase {
    private final IDraggableRenderer renderer;
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

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderBg(PoseStack transform, float partialTicks, int mouseX, int mouseY, int guiLeft, int guiTop) {
        this.renderer.draw(this.info.getContainer(), transform, guiLeft + this.x, guiTop + this.y, this.width, this.height, this.moveX, this.moveY, this.scale);
    }

    @Override
    public boolean onMouseClickMove(double mouseX, double mouseY, int button, double dragX, double dragY) {
        this.moveX += dragX / this.scale;
        this.moveY += dragY / this.scale;
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
}