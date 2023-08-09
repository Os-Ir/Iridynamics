package com.atodium.iridynamics.api.blockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface ISavedDataTickable extends ITickable {
    @Override
    default void tick(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide) SavedDataTickManager.tick((ServerLevel) level, pos);
        this.blockTick(level, pos, state);
    }

    void blockTick(Level level, BlockPos pos, BlockState state);
}