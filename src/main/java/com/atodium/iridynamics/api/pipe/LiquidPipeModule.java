package com.atodium.iridynamics.api.pipe;

import com.atodium.iridynamics.api.util.data.DataUtil;
import com.atodium.iridynamics.api.util.data.PosDirection;
import net.minecraft.server.level.ServerLevel;

public class LiquidPipeModule {
    public static ILiquidPipeNode readPipeNode(ServerLevel level, PosDirection pos) {
        return DataUtil.cast(level.getBlockEntity(pos.pos()));
    }

    public static void tick(ServerLevel level) {
        LiquidPipeSavedData.get(level).tryTick(level, level.getGameTime());
    }
}