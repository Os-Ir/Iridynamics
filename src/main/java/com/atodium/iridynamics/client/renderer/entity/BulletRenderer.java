package com.atodium.iridynamics.client.renderer.entity;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.client.model.entity.BulletModel;
import com.atodium.iridynamics.common.entity.BulletEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class BulletRenderer extends EntityRenderer<BulletEntity> {
    public static final ResourceLocation TEXTURE = Iridynamics.rl("textures/entity/bullet.png");

    private final BulletModel model;

    public BulletRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new BulletModel();
    }

    @Override
    public ResourceLocation getTextureLocation(BulletEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(BulletEntity entity, float entityYaw, float partialTicks, PoseStack transform, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, transform, buffer, packedLight);
        VertexConsumer builder = buffer.getBuffer(this.model.renderType(this.getTextureLocation(entity)));
        transform.pushPose();
        transform.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot())));
        transform.mulPose(Vector3f.XP.rotationDegrees(-Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));
        this.model.renderToBuffer(transform, builder, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        transform.popPose();
    }
}