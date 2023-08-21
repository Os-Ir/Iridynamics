package com.atodium.iridynamics.api.pipe;

import com.atodium.iridynamics.api.util.data.DataUtil;
import com.atodium.iridynamics.api.util.data.PosDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.material.Fluid;

public class LiquidPipeModule {
    public static boolean hasLiquidPipeContainer(ServerLevel level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof ILiquidPipeNodeContainer;
    }

    public static ILiquidPipeNodeContainer readLiquidPipeContainer(ServerLevel level, BlockPos pos) {
        return hasLiquidPipeContainer(level, pos) ? DataUtil.cast(level.getBlockEntity(pos)) : null;
    }

    public static ILiquidPipeNode readPipeNode(ServerLevel level, PosDirection pos) {
        return hasLiquidPipeContainer(level, pos.pos()) ? readLiquidPipeContainer(level, pos.pos()).getDirectionNode(pos.direction()) : null;
    }

    public static Fluid getPipeFluid(ServerLevel level, PosDirection pos) {
        return LiquidPipeSavedData.get(level).getFluid(pos);
    }

    public static ILiquidPipeNode getBlockNode(ServerLevel level, PosDirection pos) {
        return LiquidPipeSavedData.get(level).getPosNode(pos);
    }

    public static int tryAddFluid(ServerLevel level, PosDirection pos, Fluid fluid, int amount) {
        if (LiquidPipeSavedData.get(level).trySetFluid(pos, fluid))
            return getBlockNode(level, pos).addFluidAmount(amount);
        return amount;
    }

    public static void updatePipeBlock(ServerLevel level, BlockPos pos) {
        LiquidPipeSavedData data = LiquidPipeSavedData.get(level);
        data.removeAllNodesIn(pos);
        if (hasLiquidPipeContainer(level, pos)) data.addNodeContainer(pos, readLiquidPipeContainer(level, pos));
    }

    public static void addPipeBlock(ServerLevel level, BlockPos pos) {
        if (hasLiquidPipeContainer(level, pos))
            LiquidPipeSavedData.get(level).addNodeContainer(pos, readLiquidPipeContainer(level, pos));
    }

    public static void removePipeBlock(ServerLevel level, BlockPos pos) {
        LiquidPipeSavedData.get(level).removeAllNodesIn(pos);
    }
}