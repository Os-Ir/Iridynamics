package com.atodium.iridynamics.api.blockEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public interface ITipInfoRenderer {
    default void render(Camera camera, BlockHitResult hit, PoseStack transform, MultiBufferSource buffer, float partialTicks) {
        Vec3 cameraPos = camera.getPosition();
        Vec3 hitPos = hit.getLocation();
        Vec3 toward = hitPos.subtract(hitPos);
        Vec3 width = toward.cross(new Vec3(0.0, 1.0, 0.0)).normalize();
        Vec3 height = width.cross(toward).normalize();
    }
}