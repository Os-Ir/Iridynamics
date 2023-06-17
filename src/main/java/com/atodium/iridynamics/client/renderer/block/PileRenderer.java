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
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class PileRenderer implements BlockEntityRenderer<PileBlockEntity> {
    public static final PileRenderer INSTANCE = new PileRenderer();

    @Override
    public void render(PileBlockEntity pile, float partialTick, PoseStack transform, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance().getTextureAtlas(RendererUtil.BLOCKS_ATLAS);
        VertexConsumer consumer = buffer.getBuffer(RenderType.cutout());
        for (int i = 0; i < pile.getHeight(); i++) {
            TextureAtlasSprite texture = atlas.apply(pile.getPileItemInfo(i).getTextureName());
            transform.pushPose();
            transform.translate(0.0, 0.0625 * i, 0.0);
            RendererUtil.renderCuboid(transform, consumer, texture, combinedLight, combinedOverlay, 0.0f, 0.0f, 0.0f, 1.0f, 0.0625f, 1.0f);
            transform.popPose();
        }
    }
}