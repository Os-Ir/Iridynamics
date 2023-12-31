package com.atodium.iridynamics.client.renderer.block;

import com.atodium.iridynamics.api.capability.ForgingCapability;
import com.atodium.iridynamics.api.capability.IForging;
import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.atodium.iridynamics.client.renderer.RendererUtil;
import com.atodium.iridynamics.common.block.equipment.AnvilBlock;
import com.atodium.iridynamics.common.blockEntity.equipment.AnvilBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.item.ItemStack;

public class AnvilRenderer implements BlockEntityRenderer<AnvilBlockEntity> {
    public static final AnvilRenderer INSTANCE = new AnvilRenderer();

    @Override
    public void render(AnvilBlockEntity anvil, float partialTick, PoseStack transform, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        AnvilBlockEntity.Inventory inventory = anvil.getInventory();
        VertexConsumer consumer = buffer.getBuffer(RenderType.cutout());
        ItemStack left = inventory.left();
        ItemStack right = inventory.right();
        transform.pushPose();
        RendererUtil.transformToDirection(transform, anvil.getBlockState().getValue(AnvilBlock.DIRECTION).getOpposite());
        if (!left.isEmpty())
            left.getCapability(ForgingCapability.FORGING).ifPresent((forging) -> this.renderItem(left, forging, 0.125, transform, consumer, combinedLight, combinedOverlay));
        if (!right.isEmpty())
            right.getCapability(ForgingCapability.FORGING).ifPresent((forging) -> this.renderItem(right, forging, 0.5625, transform, consumer, combinedLight, combinedOverlay));
        transform.popPose();
    }

    private void renderItem(ItemStack stack, IForging forging, double x, PoseStack transform, VertexConsumer consumer, int combinedLight, int combinedOverlay) {
        int color = MaterialBase.getItemMaterial(stack).getRenderInfo().color();
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                float thickness = (float) forging.getThickness(i, j);
                if (thickness <= 0.0f) continue;
                transform.pushPose();
                transform.translate(x + i * 0.0625, 0.5625, 0.3125 + j * 0.0625);
                RendererUtil.renderColorCuboid(transform, consumer, color, combinedLight, combinedOverlay, 0.0f, 0.0f, 0.0f, 0.0625f, 0.0625f * thickness, 0.0625f);
                transform.popPose();
            }
        }
    }
}