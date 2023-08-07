package com.atodium.iridynamics.api.pipe;

import net.minecraft.core.Direction;
import net.minecraft.world.level.material.Fluid;

public interface ILiquidPipeNode {
    boolean isConnectable(Direction direction);

    boolean isRelated(Direction from, Direction to);

    int capacity(Direction direction);

    int fluidAmount(Direction direction);

    void setFluidAmount(Direction direction);

    Fluid fluid(Direction direction);

    boolean canInput(Direction direction);

    boolean canOutput(Direction direction);
}