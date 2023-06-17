package com.atodium.iridynamics.client.model.entity;

import com.atodium.iridynamics.common.entity.BulletEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;

import java.util.Collections;

public class BulletModel extends EntityModel<BulletEntity> {
    private final ModelPart model;

    public BulletModel() {
        this.model = new ModelPart(Collections.emptyList(), Collections.emptyMap());
        this.model.setPos(-0.5f, -0.5f, -0.5f);
        this.model.setPos(0, 0, 0);
    }

    @Override
    public void setupAnim(BulletEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertex, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.model.render(poseStack, vertex, packedLight, packedOverlay, red, green, blue, alpha);
    }
}