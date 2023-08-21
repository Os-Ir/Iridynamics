package com.atodium.iridynamics.api.pipe;

import net.minecraft.core.Direction;

public interface ILiquidPipeNodeContainer {
    ILiquidPipeNode[] getAllBlockNodes();

    default ILiquidPipeNode getDirectionNode(Direction direction) {
        for (ILiquidPipeNode node : this.getAllBlockNodes()) if (node.contains(direction)) return node;
        return null;
    }
}