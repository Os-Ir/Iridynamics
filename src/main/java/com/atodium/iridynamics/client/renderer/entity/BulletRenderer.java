package com.atodium.iridynamics.client.renderer.entity;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.model.SpecialJsonModel;
import com.atodium.iridynamics.api.model.TransformableModelVertexList;
import com.atodium.iridynamics.client.renderer.RendererUtil;
import com.atodium.iridynamics.common.entity.BulletEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class BulletRenderer extends EntityRenderer<BulletEntity> {
    public static final ResourceLocation TEXTURE = Iridynamics.rl("textures/entity/bullet.png");

    public static TransformableModelVertexList model = null;

    public BulletRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(BulletEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(BulletEntity entity, float entityYaw, float partialTicks, PoseStack transform, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, transform, buffer, packedLight);
        if (model == null) model = SpecialJsonModel.createTransformableModel(Iridynamics.rl("entity/bullet"));
        transform.pushPose();
        transform.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot())));
        transform.mulPose(Vector3f.XP.rotationDegrees(-Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));
        model.render(transform, buffer.getBuffer(RenderType.solid()), RendererUtil.castPosition(entity.position()));
        transform.popPose();
    }
}