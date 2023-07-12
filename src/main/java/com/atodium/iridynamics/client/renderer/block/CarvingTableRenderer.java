package com.atodium.iridynamics.client.renderer.block;

import com.atodium.iridynamics.api.capability.CarvingCapability;
import com.atodium.iridynamics.api.capability.ICarving;
import com.atodium.iridynamics.api.module.CarvingModule;
import com.atodium.iridynamics.client.renderer.RendererUtil;
import com.atodium.iridynamics.common.blockEntity.equipment.CarvingTableBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.item.ItemStack;

public class CarvingTableRenderer implements BlockEntityRenderer<CarvingTableBlockEntity> {
    public static final CarvingTableRenderer INSTANCE = new CarvingTableRenderer();

    @Override
    public void render(CarvingTableBlockEntity table, float partialTick, PoseStack transform, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        ItemStack stack = table.getInventory().getStackInSlot(0);
        if (stack.isEmpty()) return;
        VertexConsumer consumer = buffer.getBuffer(RenderType.cutout());
        int color = CarvingModule.getItemColor(stack);
        ICarving carving = stack.getCapability(CarvingCapability.CARVING).orElseThrow(NullPointerException::new);
        transform.pushPose();
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                int thickness = carving.getThickness(i, j);
                if (thickness == 0) continue;
                transform.pushPose();
                transform.translate(0.125 + i * 0.0625, 0.25, 0.125 + j * 0.0625);
                RendererUtil.renderColorCuboid(transform, consumer, color, combinedLight, combinedOverlay, 0.0f, 0.0f, 0.0f, 0.0625f, 0.0625f * thickness, 0.0625f);
                transform.popPose();
            }
        }
        transform.popPose();
    }
}