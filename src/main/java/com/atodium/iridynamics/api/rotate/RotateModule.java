package com.atodium.iridynamics.api.rotate;

import com.atodium.iridynamics.api.util.data.DataUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public class RotateModule {
    public static final double WOOD_MAX_ANGULAR_VELOCITY = Math.PI * 10;
    public static final double BRONZE_MAX_ANGULAR_VELOCITY = Math.PI * 40;
    public static final double STEEL_MAX_ANGULAR_VELOCITY = Math.PI * 60;

    public static boolean hasRotateNode(ServerLevel level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof IRotateNode;
    }

    public static IRotateNode getLevelRotateNode(ServerLevel level, BlockPos pos) {
        return DataUtil.cast(level.getBlockEntity(pos));
    }

    public static void tryTick(ServerLevel level, BlockPos pos) {
        RotateSavedData.get(level).tryTick(level, pos, level.getGameTime());
    }

    public static void updateRotateBlock(ServerLevel level, BlockPos pos) {
        RotateSavedData data = RotateSavedData.get(level);
        data.removeNode(pos);
        if (hasRotateNode(level, pos)) data.addNode(pos, getLevelRotateNode(level, pos));
    }

    public static void addRotateBlock(ServerLevel level, BlockPos pos) {
        if (hasRotateNode(level, pos)) RotateSavedData.get(level).addNode(pos, getLevelRotateNode(level, pos));
    }

    public static void removeRotateBlock(ServerLevel level, BlockPos pos) {
        RotateSavedData.get(level).removeNode(pos);
    }
}