package com.atodium.iridynamics.client.renderer.block;

import com.atodium.iridynamics.common.blockEntity.equipment.CrushingBoardBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

public class CrushingBoardRenderer implements BlockEntityRenderer<CrushingBoardBlockEntity> {
    public static final CrushingBoardRenderer INSTANCE = new CrushingBoardRenderer();

    @Override
    public void render(CrushingBoardBlockEntity board, float partialTick, PoseStack transform, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        ItemStack stack = board.getInventory().getStackInSlot(0);
        if (stack.isEmpty()) return;
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        VertexConsumer consumer = buffer.getBuffer(RenderType.cutout());
        transform.pushPose();
        if (stack.getItem() instanceof BlockItem) {
            transform.translate(0.5f, 0.75f, 0.5f);
            itemRenderer.renderStatic(stack, ItemTransforms.TransformType.FIXED, combinedLight, combinedOverlay, transform, buffer, 0);
        } else {
            transform.translate(0.5f, 0.53125f, 0.5f);
            transform.mulPose(Vector3f.XP.rotationDegrees(90.0f));
            transform.mulPose(Vector3f.ZP.rotationDegrees(180.0f));
            itemRenderer.renderStatic(stack, ItemTransforms.TransformType.FIXED, combinedLight, combinedOverlay, transform, buffer, 0);
        }
        transform.popPose();
    }
}