package com.atodium.iridynamics.api.gui.plan;

import com.atodium.iridynamics.api.gui.widget.IWidgetRenderer;
import net.minecraft.core.BlockPos;

public interface IPlanBlockEntity {
    int getPlanCount();

    IWidgetRenderer getOverlayRenderer(int count);

    void callback(int count);

    BlockPos getBlockPos();
}