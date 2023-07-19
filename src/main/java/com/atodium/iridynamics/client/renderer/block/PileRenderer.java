package com.atodium.iridynamics.client.renderer.block;

import com.atodium.iridynamics.client.renderer.RendererUtil;
import com.atodium.iridynamics.common.blockEntity.PileBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class PileRenderer implements BlockEntityRenderer<PileBlockEntity> {
    public static final PileRenderer INSTANCE = new PileRenderer();

    @Override
    public void render(PileBlockEntity pile, float partialTick, PoseStack transform, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance().getTextureAtlas(RendererUtil.BLOCKS_ATLAS);
        VertexConsumer consumer = buffer.getBuffer(RenderType.cutout());
        transform.pushPose();
        for (int i = 0; i < pile.getHeight(); i++) {
            TextureAtlasSprite texture = atlas.apply(pile.getPileItemInfo(i).texture());
            RendererUtil.renderFace(transform, consumer, texture, combinedLight, combinedOverlay, Direction.NORTH, 0.0f, 0.0625f * i, 0.0f, 1.0f, 0.0625f * (i + 1), 1.0f, 0.0f, 0.0f, -1.0f, 16.0f, 1.0f, 0.0f, 15.0f - i);
            RendererUtil.renderFace(transform, consumer, texture, combinedLight, combinedOverlay, Direction.SOUTH, 0.0f, 0.0625f * i, 0.0f, 1.0f, 0.0625f * (i + 1), 1.0f, 0.0f, 0.0f, 1.0f, 16.0f, 1.0f, 0.0f, 15.0f - i);
            RendererUtil.renderFace(transform, consumer, texture, combinedLight, combinedOverlay, Direction.WEST, 0.0f, 0.0625f * i, 0.0f, 1.0f, 0.0625f * (i + 1), 1.0f, -1.0f, 0.0f, 0.0f, 16.0f, 1.0f, 0.0f, 15.0f - i);
            RendererUtil.renderFace(transform, consumer, texture, combinedLight, combinedOverlay, Direction.EAST, 0.0f, 0.0625f * i, 0.0f, 1.0f, 0.0625f * (i + 1), 1.0f, 1.0f, 0.0f, 0.0f, 16.0f, 1.0f, 0.0f, 15.0f - i);
            if (i == 0)
                RendererUtil.renderFace(transform, consumer, texture, combinedLight, combinedOverlay, Direction.UP, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, -1.0f, 0.0f, 16.0f, 16.0f);
            if (i == pile.getHeight() - 1)
                RendererUtil.renderFace(transform, consumer, texture, combinedLight, combinedOverlay, Direction.UP, 0.0f, 0.0625f * pile.getHeight(), 0.0f, 1.0f, 0.0625f * pile.getHeight(), 1.0f, 0.0f, 1.0f, 0.0f, 16.0f, 16.0f);
        }
        transform.popPose();
    }
}