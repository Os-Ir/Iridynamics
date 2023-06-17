package com.atodium.iridynamics.api.blockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;

public interface ITickable {
    static <T extends BlockEntity> BlockEntityTicker<T> ticker() {
        return (level, pos, state, entity) -> ((ITickable) entity).tick(level, pos, state);
    }

    void tick(Level level, BlockPos pos, BlockState state);
}