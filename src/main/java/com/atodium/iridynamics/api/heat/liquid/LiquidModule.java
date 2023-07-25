package com.atodium.iridynamics.api.heat.liquid;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public class LiquidModule {
    public static void addLiquidContainer(ServerLevel level, BlockPos pos) {
        LiquidContainerSavedData.get(level).addLiquidContainer(pos);
    }

    public static void addLiquidContainer(ServerLevel level, BlockPos pos, int count) {
        LiquidContainerSavedData.get(level).addLiquidContainer(pos, count);
    }

    public static void removeLiquidContainer(ServerLevel level, BlockPos pos) {
        LiquidContainerSavedData.get(level).removeLiquidContainer(pos);
    }

    public static void removeLiquidContainer(ServerLevel level, BlockPos pos, int count) {
        LiquidContainerSavedData.get(level).removeLiquidContainer(pos, count);
    }

    public static void removeAllLiquidContainer(ServerLevel level, BlockPos pos) {
        LiquidContainerSavedData.get(level).removeAllLiquidContainer(pos);
    }

    public static boolean hasLiquidContainer(ServerLevel level, BlockPos pos) {
        return LiquidContainerSavedData.get(level).hasLiquidContainer(pos);
    }
}