package com.atodium.iridynamics.api.blockEntity;

import com.atodium.iridynamics.api.util.data.DataUtil;
import com.atodium.iridynamics.client.renderer.RendererUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public interface ITipInfoRenderer {
    default void render(Camera camera, BlockHitResult result, PoseStack transform, MultiBufferSource buffer, float partialTicks) {
        VertexConsumer consumer = buffer.getBuffer(RenderType.cutout());
        BlockPos blockPos = result.getBlockPos();
        Vec3 cameraPos = camera.getPosition();
        Vec3 hitPos = result.getLocation();
        Vec3 toward = hitPos.subtract(cameraPos);
        Direction closest = Direction.NORTH;
        double min = 0.0;
        for (Direction direction : DataUtil.HORIZONTALS) {
            double dot = toward.x * direction.getStepX() + toward.y * direction.getStepY() + toward.z * direction.getStepZ();
            if (min > dot) {
                closest = direction;
                min = dot;
            }
        }
        float width = this.width();
        float height = this.height();
        transform.pushPose();
        transform.translate(blockPos.getX() - cameraPos.x + (closest == Direction.NORTH || closest == Direction.EAST ? 1.0 : 0.0), blockPos.getY() - cameraPos.y + 1.0, blockPos.getZ() - cameraPos.z + (closest == Direction.SOUTH || closest == Direction.EAST ? 1.0 : 0.0));
        transform.mulPose(Vector3f.YP.rotationDegrees(RendererUtil.getDirectionAngel(closest.getOpposite())));
        RendererUtil.renderColorFace(transform, consumer, 0xffffffff, 255, 0, Direction.SOUTH, 0.0f, 0.0f, 0.0f, width, height, 0.0f, 0.0f, 0.0f, 1.0f);
        transform.popPose();
    }

    float width();

    float height();

    void renderInfo(Camera camera, BlockHitResult result, PoseStack transform, MultiBufferSource buffer, float partialTicks);
}