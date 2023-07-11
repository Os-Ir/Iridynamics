package com.atodium.iridynamics.api.gui;

import com.mojang.blaze3d.vertex.PoseStack;

@FunctionalInterface
public interface IContainerRenderer {
    void draw(ModularContainer container, PoseStack transform, float x, float y, float width, float height);
}