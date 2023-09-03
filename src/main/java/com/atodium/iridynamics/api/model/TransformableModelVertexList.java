package com.atodium.iridynamics.api.model;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.nio.ByteBuffer;

public class TransformableModelVertexList {
    private final ModelVertexList vertices;
    private final PoseStack modelTransform;

    public TransformableModelVertexList(BufferBuilder.DrawState drawState, ByteBuffer buffer) {
        this.vertices = new ModelVertexList(buffer, drawState.vertexCount(), drawState.format().getVertexSize());
        this.modelTransform = new PoseStack();
    }

    public void render(PoseStack renderTransform, VertexConsumer consumer, BlockPos renderPos) {
        if (this.vertices.isEmpty()) return;
        Matrix4f matrix = renderTransform.last().pose().copy();
        Matrix3f normalMatrix = renderTransform.last().normal().copy();
        matrix.multiply(this.modelTransform.last().pose());
        normalMatrix.mul(this.modelTransform.last().normal());
        Vector4f pos = new Vector4f();
        Vector3f normal = new Vector3f();
        for (int i = 0; i < this.vertices.vertexCount(); i++) {
            float x = this.vertices.x(i);
            float y = this.vertices.y(i);
            float z = this.vertices.z(i);
            pos.set(x, y, z, 1.0f);
            pos.transform(matrix);
            consumer.vertex(pos.x(), pos.y(), pos.z());
            consumer.color(0xffffffff);
            consumer.uv(this.vertices.u(i), this.vertices.v(i));
            normal.set(this.vertices.nx(i), this.vertices.ny(i), this.vertices.nz(i));
            normal.transform(normalMatrix);
            consumer.uv2(LevelRenderer.getLightColor(Minecraft.getInstance().level, renderPos));
            consumer.normal(normal.x(), normal.y(), normal.z());
            consumer.endVertex();
        }
        this.reset();
    }

    public TransformableModelVertexList reset() {
        while (!modelTransform.clear()) modelTransform.popPose();
        modelTransform.pushPose();
        return this;
    }

    public TransformableModelVertexList translate(float x, float y, float z) {
        this.modelTransform.translate(x, y, z);
        return this;
    }

    public TransformableModelVertexList multiply(Quaternion quaternion) {
        this.modelTransform.mulPose(quaternion);
        return this;
    }

    public TransformableModelVertexList rotate(Direction axis, float angle) {
        if (angle == 0.0) return this;
        return this.multiply(axis.step().rotation(angle));
    }

    public TransformableModelVertexList rotateCentered(Direction direction, float angle) {
        this.translate(0.5f, 0.5f, 0.5f).rotate(direction, angle).translate(-0.5f, -0.5f, -0.5f);
        return this;
    }

    public TransformableModelVertexList rotateCentered(Quaternion q) {
        this.translate(0.5f, 0.5f, 0.5f).multiply(q).translate(-0.5f, -0.5f, -0.5f);
        return this;
    }

    public TransformableModelVertexList scale(float x, float y, float z) {
        this.modelTransform.scale(x, y, z);
        return this;
    }

    public TransformableModelVertexList pushPose() {
        this.modelTransform.pushPose();
        return this;
    }

    public TransformableModelVertexList popPose() {
        this.modelTransform.popPose();
        return this;
    }

    public TransformableModelVertexList mulPose(Matrix4f pose) {
        this.modelTransform.last().pose().multiply(pose);
        return this;
    }

    public TransformableModelVertexList mulNormal(Matrix3f normal) {
        this.modelTransform.last().normal().mul(normal);
        return this;
    }

    public TransformableModelVertexList transform(PoseStack stack) {
        this.modelTransform.last().pose().multiply(stack.last().pose());
        this.modelTransform.last().normal().mul(stack.last().normal());
        return this;
    }
}