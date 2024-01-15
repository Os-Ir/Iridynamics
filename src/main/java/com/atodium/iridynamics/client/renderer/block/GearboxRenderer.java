package com.atodium.iridynamics.client.renderer.block;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.model.SpecialJsonModel;
import com.atodium.iridynamics.api.model.TransformableModelVertexList;
import com.atodium.iridynamics.client.renderer.RendererUtil;
import com.atodium.iridynamics.common.blockEntity.rotate.GearboxBlockEntity;
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

public class GearboxRenderer implements BlockEntityRenderer<GearboxBlockEntity> {
    public static final GearboxRenderer INSTANCE = new GearboxRenderer();

    public static TransformableModelVertexList model = null;

    @Override
    public void render(GearboxBlockEntity gearbox, float partialTicks, PoseStack transform, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        if (model == null) model = SpecialJsonModel.createTransformableModel(Iridynamics.rl("block/gearbox_axle"));
        Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance().getTextureAtlas(RendererUtil.BLOCKS_ATLAS);
        VertexConsumer consumer = buffer.getBuffer(RenderType.solid());
        for (Direction direction : Direction.values()) {
            if (!gearbox.isConnectable(direction)) continue;
            transform.pushPose();
            transform.translate(0.5, 0.5, 0.5);
            if (direction.get2DDataValue() >= 0) transform.mulPose(Vector3f.YP.rotationDegrees(RendererUtil.getDirectionAngel(direction)));
            else transform.mulPose(Vector3f.XP.rotationDegrees(direction == Direction.UP ? 90.0f : -90.0f));
            transform.mulPose(Vector3f.ZP.rotationDegrees((float) Math.toDegrees(gearbox.getRenderAngle(direction, partialTicks))));
            transform.translate(-0.5, -0.5, -0.5);
            model.render(transform, consumer, gearbox.getBlockPos());
            transform.popPose();
        }
    }
}