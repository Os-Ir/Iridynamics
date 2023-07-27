package com.atodium.iridynamics.client.renderer.block;

import com.atodium.iridynamics.api.capability.LiquidContainerCapability;
import com.atodium.iridynamics.client.renderer.RendererUtil;
import com.atodium.iridynamics.common.blockEntity.equipment.CrucibleBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;

public class CrucibleRenderer implements BlockEntityRenderer<CrucibleBlockEntity> {
    public static final CrucibleRenderer INSTANCE = new CrucibleRenderer();

    @Override
    public void render(CrucibleBlockEntity crucible, float partialTick, PoseStack transform, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        VertexConsumer consumer = buffer.getBuffer(RenderType.cutout());
        LiquidContainerCapability container = crucible.getLiquidContainer();
        if (container.isEmpty()) return;
        transform.pushPose();
        transform.translate(0.1875, 0.1875 + ((double) container.usedCapacity()) / container.liquidCapacity() * 0.8125, 0.1875);
        RendererUtil.renderColorFace(transform, consumer, container.getAllMaterials().keySet().stream().toList().get(0).getRenderInfo().color(), combinedLight, combinedOverlay, Direction.UP, 0.0f, 0.0f, 0.0f, 0.625f, 0.0f, 0.625f, 0.0f, 1.0f, 0.0f);
        transform.popPose();
    }
}