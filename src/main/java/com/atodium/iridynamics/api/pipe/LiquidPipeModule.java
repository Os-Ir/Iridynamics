package com.atodium.iridynamics.api.pipe;

import com.atodium.iridynamics.api.util.data.DataUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public class LiquidPipeModule {
    public static ILiquidPipeNode readPipeNode(ServerLevel level, BlockPos pos) {
        return DataUtil.cast(level.getBlockEntity(pos));
    }

    public static void tick(ServerLevel level, BlockPos pos) {
        LiquidPipeSavedData.get(level).tryTick(level, pos, level.getGameTime());
    }
}