package com.atodium.iridynamics.client.renderer.block;

import com.atodium.iridynamics.client.renderer.RendererUtil;
import com.atodium.iridynamics.common.blockEntity.PlacedStoneBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;

import java.util.Random;

public class PlacedStoneRenderer implements BlockEntityRenderer<PlacedStoneBlockEntity> {
    public static final PlacedStoneRenderer INSTANCE = new PlacedStoneRenderer();

    @Override
    public void render(PlacedStoneBlockEntity stone, float partialTick, PoseStack transform, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        VertexConsumer consumer = buffer.getBuffer(RenderType.cutout());
        Random rand = new Random(stone.getBlockPos().hashCode());
        int height = rand.nextInt(5) + 4;
        int widthX = rand.nextInt(5) + 4;
        int widthZ = rand.nextInt(5) + 4;
        int x = (int) (rand.nextFloat() * (16 - widthX));
        int z = (int) (rand.nextFloat() * (16 - widthZ));
        transform.pushPose();
        transform.translate(x / 16.0, 0.0, z / 16.0);
        RendererUtil.renderColorCuboid(transform, consumer, stone.getMaterial().getRenderInfo().color(), combinedLight, combinedOverlay, 0.0f, 0.0f, 0.0f, widthX / 16.0f, height / 16.0f, widthZ / 16.0f);
        transform.popPose();
    }
}