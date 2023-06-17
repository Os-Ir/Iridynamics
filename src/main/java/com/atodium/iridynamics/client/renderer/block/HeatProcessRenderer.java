package com.atodium.iridynamics.client.renderer.block;

import com.atodium.iridynamics.client.renderer.RendererUtil;
import com.atodium.iridynamics.common.blockEntity.HeatProcessBlockEntity;
import com.atodium.iridynamics.common.blockEntity.PileBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class HeatProcessRenderer implements BlockEntityRenderer<HeatProcessBlockEntity> {
    public static final HeatProcessRenderer INSTANCE = new HeatProcessRenderer();

    @Override
    public void render(HeatProcessBlockEntity process, float partialTick, PoseStack transform, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance().getTextureAtlas(RendererUtil.BLOCKS_ATLAS);
        VertexConsumer consumer = buffer.getBuffer(RenderType.cutout());
        TextureAtlasSprite texture = atlas.apply(process.getContentInfo().getTextureName());
        for (int i = 0; i < process.getHeight(); i++) {
            transform.pushPose();
            transform.translate(0.0, 0.0625 * i, 0.0);
            RendererUtil.renderCuboid(transform, consumer, texture, combinedLight, combinedOverlay, 0.0f, 0.0f, 0.0f, 1.0f, 0.0625f, 1.0f);
            transform.popPose();
        }
    }
}