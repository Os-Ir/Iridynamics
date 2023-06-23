package com.atodium.iridynamics.client.renderer.block;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.capability.LiquidContainerCapability;
import com.atodium.iridynamics.client.renderer.RendererUtil;
import com.atodium.iridynamics.common.blockEntity.equipment.MoldBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class MoldRenderer implements BlockEntityRenderer<MoldBlockEntity> {
    public static final MoldRenderer INSTANCE = new MoldRenderer();

    @Override
    public void render(MoldBlockEntity mold, float partialTick, PoseStack transform, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        TextureAtlasSprite texture = Minecraft.getInstance().getTextureAtlas(RendererUtil.BLOCKS_ATLAS).apply(Iridynamics.rl("block/white"));
        VertexConsumer consumer = buffer.getBuffer(RenderType.cutout());
        LiquidContainerCapability container = mold.getLiquidContainer();
        if (container.isEmpty()) return;
        System.out.println(1);
        transform.pushPose();
        transform.translate(0.3125, 0.125 + ((double) container.usedCapacity()) / container.liquidCapacity() * 0.125, 0.1875);
        RendererUtil.renderFace(transform, consumer, texture, container.getAllMaterials().keySet().stream().toList().get(0).getRenderInfo().color(), combinedLight, combinedOverlay, RendererUtil.buildPositiveYVertices(0.0f, 0.0f, 0.0f, 0.375f, 0.0f, 0.625f), 0.0f, 1.0f, 0.0f, 6, 10);
        transform.popPose();
    }
}