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

public class TransformableVertexList {
    private final ModelVertexList vertices;
    private final PoseStack modelTransform;

    public TransformableVertexList(BufferBuilder.DrawState drawState, ByteBuffer buffer) {
        this.vertices = new ModelVertexList(buffer, drawState.vertexCount(), drawState.format().getVertexSize());
        this.modelTransform = new PoseStack();
    }

    public void render(PoseStack renderTransform, VertexConsumer consumer) {
        if (this.vertices.isEmpty()) return;
        Matrix4f matrix = renderTransform.last().pose().copy();
        Matrix3f normalMatrix = renderTransform.last().normal().copy();
        matrix.multiply(this.modelTransform.last().pose());
        normalMatrix.mul(this.modelTransform.last().normal());
        Vector4f pos = new Vector4f();
        Vector3f normal = new Vector3f();
        for (int i = 0; i < this.vertices.vertexCount(); i++) {
            pos.set(this.vertices.x(i), this.vertices.y(i), this.vertices.z(i), 1.0f);
            pos.transform(matrix);
            consumer.vertex(pos.x(), pos.y(), pos.z());
            consumer.uv(this.vertices.u(i), this.vertices.v(i));
            normal.set(this.vertices.nx(i), this.vertices.ny(i), this.vertices.nz(i));
            normal.transform(normalMatrix);
            consumer.normal(normal.x(), normal.y(), normal.z());
            consumer.uv2(LevelRenderer.getLightColor(Minecraft.getInstance().level, new BlockPos(pos.x(), pos.y(), pos.z())));
            consumer.endVertex();
        }
        this.reset();
    }

    public TransformableVertexList reset() {
        while (!modelTransform.clear()) modelTransform.popPose();
        modelTransform.pushPose();
        return this;
    }

    public TransformableVertexList translate(float x, float y, float z) {
        this.modelTransform.translate(x, y, z);
        return this;
    }

    public TransformableVertexList multiply(Quaternion quaternion) {
        this.modelTransform.mulPose(quaternion);
        return this;
    }

    public TransformableVertexList rotate(Direction axis, float angle) {
        if (angle == 0.0) return this;
        return this.multiply(axis.step().rotation(angle));
    }

    public TransformableVertexList rotateCentered(Direction direction, float angle) {
        this.translate(0.5f, 0.5f, 0.5f).rotate(direction, angle).translate(-0.5f, -0.5f, -0.5f);
        return this;
    }

    public TransformableVertexList rotateCentered(Quaternion q) {
        this.translate(0.5f, 0.5f, 0.5f).multiply(q).translate(-0.5f, -0.5f, -0.5f);
        return this;
    }

    public TransformableVertexList scale(float x, float y, float z) {
        this.modelTransform.scale(x, y, z);
        return this;
    }

    public TransformableVertexList pushPose() {
        this.modelTransform.pushPose();
        return this;
    }

    public TransformableVertexList popPose() {
        this.modelTransform.popPose();
        return this;
    }

    public TransformableVertexList mulPose(Matrix4f pose) {
        this.modelTransform.last().pose().multiply(pose);
        return this;
    }

    public TransformableVertexList mulNormal(Matrix3f normal) {
        this.modelTransform.last().normal().mul(normal);
        return this;
    }

    public TransformableVertexList transform(PoseStack stack) {
        this.modelTransform.last().pose().multiply(stack.last().pose());
        this.modelTransform.last().normal().mul(stack.last().normal());
        return this;
    }
}