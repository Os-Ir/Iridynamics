package com.atodium.iridynamics.api.gui.widget;

import com.atodium.iridynamics.api.gui.IContainerRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RendererWidget extends WidgetBase {
    private final IContainerRenderer renderer;

    public RendererWidget(int x, int y, int width, int height, IWidgetRenderer renderer) {
        this(x, y, width, height, (container, transform, px, py, pw, ph) -> renderer.draw(transform, px, py, pw, ph));
    }

    public RendererWidget(int x, int y, int width, int height, IContainerRenderer renderer) {
        super(x, y, width, height);
        this.renderer = renderer;
    }

    @Override
    public WidgetBase setRenderer(IWidgetRenderer renderer) {
        throw new UnsupportedOperationException();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderBg(PoseStack transform, float partialTicks, int mouseX, int mouseY, int guiLeft, int guiTop) {
        this.renderer.draw(this.info.getContainer(), transform, guiLeft + this.getX(), guiTop + this.getY(), this.getWidth(), this.getHeight());
    }
}