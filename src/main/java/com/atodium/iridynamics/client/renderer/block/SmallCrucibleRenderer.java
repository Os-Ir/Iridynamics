package com.atodium.iridynamics.client.renderer.block;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.capability.LiquidContainerCapability;
import com.atodium.iridynamics.client.renderer.RendererUtil;
import com.atodium.iridynamics.common.blockEntity.equipment.SmallCrucibleBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

public class SmallCrucibleRenderer implements BlockEntityRenderer<SmallCrucibleBlockEntity> {
    public static final SmallCrucibleRenderer INSTANCE = new SmallCrucibleRenderer();

    @Override
    public void render(SmallCrucibleBlockEntity crucible, float partialTick, PoseStack transform, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        TextureAtlasSprite texture = Minecraft.getInstance().getTextureAtlas(RendererUtil.BLOCKS_ATLAS).apply(Iridynamics.rl("block/white"));
        VertexConsumer consumer = buffer.getBuffer(RenderType.cutout());
        LiquidContainerCapability container = crucible.getLiquidContainer();
        if (container.isEmpty()) return;
        transform.pushPose();
        transform.translate(0.3125, 0.1875 + ((double) container.usedCapacity()) / container.liquidCapacity() * 0.8125, 0.3125);
        RendererUtil.renderFace(transform, consumer, texture, container.getAllMaterials().keySet().stream().toList().get(0).getRenderInfo().color(), combinedLight, combinedOverlay, Direction.UP, 0.0f, 0.0f, 0.0f, 0.375f, 0.0f, 0.375f, 0.0f, 1.0f, 0.0f, 6, 6);
        transform.popPose();
    }
}