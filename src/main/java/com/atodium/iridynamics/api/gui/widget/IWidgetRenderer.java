package com.atodium.iridynamics.api.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;

@FunctionalInterface
public interface IWidgetRenderer {
    IWidgetRenderer EMPTY = new EmptyWidgetRenderer();

    void draw(PoseStack transform, int x, int y, int width, int height);

    default IWidgetRenderer merge(IWidgetRenderer renderer) {
        return (transform, x, y, width, height) -> {
            this.draw(transform, x, y, width, height);
            renderer.draw(transform, x, y, width, height);
        };
    }

    class EmptyWidgetRenderer implements IWidgetRenderer {
        @Override
        public void draw(PoseStack transform, int x, int y, int width, int height) {

        }
    }
}