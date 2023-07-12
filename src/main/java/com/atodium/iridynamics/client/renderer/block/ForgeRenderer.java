package com.atodium.iridynamics.client.renderer.block;

import com.atodium.iridynamics.api.capability.ForgingCapability;
import com.atodium.iridynamics.api.capability.IForging;
import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.atodium.iridynamics.client.renderer.RendererUtil;
import com.atodium.iridynamics.common.block.equipment.ForgeBlock;
import com.atodium.iridynamics.common.blockEntity.equipment.ForgeBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;

public class ForgeRenderer implements BlockEntityRenderer<ForgeBlockEntity> {
    public static final ForgeRenderer INSTANCE = new ForgeRenderer();

    @Override
    public void render(ForgeBlockEntity forge, float partialTick, PoseStack transform, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ForgeBlockEntity.Inventory inventory = forge.getInventory();
        VertexConsumer consumer = buffer.getBuffer(RenderType.cutout());
        ItemStack left = inventory.left();
        ItemStack right = inventory.right();
        transform.pushPose();
        RendererUtil.transformToDirection(transform, forge.getBlockState().getValue(ForgeBlock.DIRECTION).getOpposite());
        if (!left.isEmpty()) {
            LazyOptional<IForging> optionalLeft = left.getCapability(ForgingCapability.FORGING);
            if (optionalLeft.isPresent())
                this.renderMaterialItem(left, optionalLeft.orElseThrow(NullPointerException::new), 0.0625, transform, consumer, combinedLight, combinedOverlay);
            else this.renderSimpleItem(left, 0.28125f, itemRenderer, transform, buffer, combinedLight, combinedOverlay);
        }
        if (!right.isEmpty()) {
            LazyOptional<IForging> optionalRight = right.getCapability(ForgingCapability.FORGING);
            if (optionalRight.isPresent())
                this.renderMaterialItem(right, optionalRight.orElseThrow(NullPointerException::new), 0.5, transform, consumer, combinedLight, combinedOverlay);
            else
                this.renderSimpleItem(right, 0.71875f, itemRenderer, transform, buffer, combinedLight, combinedOverlay);
        }
        transform.popPose();
    }

    private void renderMaterialItem(ItemStack stack, IForging forging, double x, PoseStack transform, VertexConsumer consumer, int combinedLight, int combinedOverlay) {
        int color = MaterialBase.getItemMaterial(stack).getRenderInfo().color();
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                float thickness = (float) forging.getThickness(i, j);
                if (thickness <= 0.0f) continue;
                transform.pushPose();
                transform.translate(x + i * 0.0625, 0.0625, 0.375 + j * 0.0625);
                RendererUtil.renderColorCuboid(transform, consumer, color, combinedLight, combinedOverlay, 0.0f, 0.0f, 0.0f, 0.0625f, 0.0625f * thickness, 0.0625f);
                transform.popPose();
            }
        }
    }

    private void renderSimpleItem(ItemStack stack, double x, ItemRenderer renderer, PoseStack transform, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        transform.pushPose();
        transform.translate(x, 0.125f, 0.625f);
        transform.scale(0.4f, 0.4f, 0.4f);
        transform.mulPose(Vector3f.XP.rotationDegrees(90.0f));
        transform.mulPose(Vector3f.ZP.rotationDegrees(180.0f));
        renderer.renderStatic(stack, ItemTransforms.TransformType.FIXED, combinedLight, combinedOverlay, transform, buffer, 0);
        transform.popPose();
    }
}