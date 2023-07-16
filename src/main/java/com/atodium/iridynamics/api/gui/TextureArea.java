package com.atodium.iridynamics.api.gui;

import com.atodium.iridynamics.api.gui.widget.IWidgetRenderer;
import com.atodium.iridynamics.api.util.math.MathUtil;
import com.atodium.iridynamics.client.renderer.RendererUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

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
        return createTexture(location, 0.0f, 0.0f, 1.0f, 1.0f);
    }

    public static TextureArea createTexture(ResourceLocation location, float x, float y, float width, float height) {
        return new TextureArea(location, x, y, width, height);
    }

    public static TextureArea createSubTexture(TextureArea area, float x, float y, float width, float height) {
        return new TextureArea(area.location, area.x + x * area.width, area.y + y * area.height, width * area.width, height * area.height);
    }

    @Override
    public void draw(PoseStack transform, float x, float y, float width, float height) {
        RendererUtil.bindTexture(this.location);
        RendererUtil.drawScaledTexturedRect(transform, x, y, width, height, this.x, this.y, this.width, this.height);
    }

    public void draw(PoseStack transform, float x, float y, float width, float height, float alpha) {
        RendererUtil.bindTexture(this.location);
        RendererUtil.drawScaledTexturedRect(transform, x, y, width, height, this.x, this.y, this.width, this.height, alpha);
    }

    public void drawSubArea(PoseStack transform, float x, float y, float width, float height, float subX, float subY, float subWidth, float subHeight) {
        RendererUtil.bindTexture(this.location);
        RendererUtil.drawScaledTexturedRect(transform, x, y, width, height, this.x + subX * this.width, this.y + subY * this.height, subWidth * this.width, subHeight * this.height);
    }

    public void drawSubArea(PoseStack transform, float x, float y, float width, float height, float subX, float subY, float subWidth, float subHeight, float alpha) {
        RendererUtil.bindTexture(this.location);
        RendererUtil.drawScaledTexturedRect(transform, x, y, width, height, this.x + subX * this.width, this.y + subY * this.height, subWidth * this.width, subHeight * this.height, alpha);
    }

    public void drawInRange(PoseStack transform, float x, float y, float width, float height, float minX, float maxX, float minY, float maxY) {
        float xi = Mth.clamp(x, minX, maxX);
        float xa = Mth.clamp(x + width, minX, maxX);
        float yi = Mth.clamp(y, minY, maxY);
        float ya = Mth.clamp(y + height, minY, maxY);
        if (MathUtil.isEquals(xi, xa) || MathUtil.isEquals(yi, ya)) return;
        RendererUtil.bindTexture(this.location);
        RendererUtil.drawScaledTexturedRect(transform, xi, yi, xa - xi, ya - yi, this.x + this.width * (xi - x) / width, this.y + this.height * (yi - y) / width, this.width * (xa - xi) / width, this.height * (ya - yi) / height);
    }

    public void drawInRange(PoseStack transform, float x, float y, float width, float height, float minX, float maxX, float minY, float maxY, float alpha) {
        float xi = Mth.clamp(x, minX, maxX);
        float xa = Mth.clamp(x + width, minX, maxX);
        float yi = Mth.clamp(y, minY, maxY);
        float ya = Mth.clamp(y + height, minY, maxY);
        if (MathUtil.isEquals(xi, xa) || MathUtil.isEquals(yi, ya)) return;
        RendererUtil.bindTexture(this.location);
        RendererUtil.drawScaledTexturedRect(transform, xi, yi, ya - yi, xa - xi, this.x + this.width * (xi - x) / width, this.y + this.height * (yi - y) / width, this.width * (xa - xi) / width, this.height * (ya - yi) / height, alpha);
    }
}