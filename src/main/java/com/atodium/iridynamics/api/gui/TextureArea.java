package com.atodium.iridynamics.api.gui;

import com.atodium.iridynamics.api.gui.widget.IWidgetRenderer;
import com.atodium.iridynamics.client.renderer.RendererUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;

public class TextureArea implements IWidgetRenderer {
    private final ResourceLocation location;
    private final float x, y, width, height;

    public TextureArea(ResourceLocation location, float x, float y, float width, float height) {
        this.location = location;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public static TextureArea createFullTexture(ResourceLocation location) {
        return createTexture(location, 0, 0, 1, 1);
    }

    public static TextureArea createTexture(ResourceLocation location, float x, float y, float width, float height) {
        return new TextureArea(location, x, y, width, height);
    }

    public static TextureArea createSubTexture(TextureArea area, float x, float y, float width, float height) {
        return new TextureArea(area.location, area.x + x * area.width, area.y + y * area.height, width * area.width, height * area.height);
    }

    @Override
    public void draw(PoseStack transform, int x, int y, int width, int height) {
        RendererUtil.bindTexture(this.location);
        RendererUtil.drawScaledTexturedRect(transform, x, y, width, height, this.x, this.y, this.width, this.height);
    }

    public void draw(PoseStack transform, int x, int y, int width, int height, float alpha) {
        RendererUtil.bindTexture(this.location);
        RendererUtil.drawScaledTexturedRect(transform, x, y, width, height, this.x, this.y, this.width, this.height, alpha);
    }

    public void drawSubArea(PoseStack transform, int x, int y, int width, int height, float subX, float subY, float subWidth, float subHeight) {
        RendererUtil.bindTexture(this.location);
        RendererUtil.drawScaledTexturedRect(transform, x, y, width, height, this.x + subX * this.width, this.y + subY * this.height, subWidth * this.width, subHeight * this.height);
    }

    public void drawSubArea(PoseStack transform, int x, int y, int width, int height, float subX, float subY, float subWidth, float subHeight, float alpha) {
        RendererUtil.bindTexture(this.location);
        RendererUtil.drawScaledTexturedRect(transform, x, y, width, height, this.x + subX * this.width, this.y + subY * this.height, subWidth * this.width, subHeight * this.height, alpha);
    }
}