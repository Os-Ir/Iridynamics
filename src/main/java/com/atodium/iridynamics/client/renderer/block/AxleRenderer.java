package com.atodium.iridynamics.client.renderer.block;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.client.renderer.RendererUtil;
import com.atodium.iridynamics.common.block.rotate.AxleBlock;
import com.atodium.iridynamics.common.blockEntity.rotate.AxleBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class AxleRenderer implements BlockEntityRenderer<AxleBlockEntity> {
    public static final AxleRenderer INSTANCE = new AxleRenderer();

    @Override
    public void render(AxleBlockEntity axle, float partialTicks, PoseStack transform, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance().getTextureAtlas(RendererUtil.BLOCKS_ATLAS);
        TextureAtlasSprite texture1 = atlas.apply(Iridynamics.rl("block/axle_1"));
        TextureAtlasSprite texture2 = atlas.apply(Iridynamics.rl("block/axle_2"));
        TextureAtlasSprite texture3 = atlas.apply(Iridynamics.rl("block/axle_3"));
        VertexConsumer consumer = buffer.getBuffer(RenderType.cutout());
        Direction direction = axle.getBlockState().getValue(AxleBlock.DIRECTION);
        transform.pushPose();
        transform.translate(0.5, 0.5, 0.5);
        if (direction.get2DDataValue() >= 0)
            transform.mulPose(Vector3f.YP.rotationDegrees(RendererUtil.getDirectionAngel(direction)));
        else transform.mulPose(Vector3f.XP.rotationDegrees(direction == Direction.UP ? 90.0f : -90.0f));
        transform.mulPose(Vector3f.ZP.rotationDegrees((float) Math.toDegrees(axle.getRenderAngle(partialTicks))));
        transform.translate(-0.125, -0.125, -0.5);
        RendererUtil.renderFace(transform, consumer, texture1, combinedLight, combinedOverlay, Direction.UP, 0.0f, 0.0f, 0.0f, 0.25f, 0.25f, 1.0f, 0.0f, 1.0f, 0.0f, 4.0f, 16.0f);
        RendererUtil.renderFace(transform, consumer, texture1, combinedLight, combinedOverlay, Direction.DOWN, 0.0f, 0.0f, 0.0f, 0.25f, 0.25f, 1.0f, 0.0f, -1.0f, 0.0f, 4.0f, 16.0f);
        RendererUtil.renderFace(transform, consumer, texture2, combinedLight, combinedOverlay, Direction.WEST, 0.0f, 0.0f, 0.0f, 0.25f, 0.25f, 1.0f, -1.0f, 0.0f, 0.0f, 16.0f, 4.0f);
        RendererUtil.renderFace(transform, consumer, texture2, combinedLight, combinedOverlay, Direction.EAST, 0.0f, 0.0f, 0.0f, 0.25f, 0.25f, 1.0f, 1.0f, 0.0f, 0.0f, 16.0f, 4.0f);
        RendererUtil.renderFace(transform, consumer, texture3, combinedLight, combinedOverlay, Direction.NORTH, 0.0f, 0.0f, 0.0f, 0.25f, 0.25f, 1.0f, 0.0f, 0.0f, -1.0f, 4.0f, 4.0f);
        RendererUtil.renderFace(transform, consumer, texture3, combinedLight, combinedOverlay, Direction.SOUTH, 0.0f, 0.0f, 0.0f, 0.25f, 0.25f, 1.0f, 0.0f, 0.0f, 1.0f, 4.0f, 4.0f);
        transform.popPose();
    }
}