package com.atodium.iridynamics.common.blockEntity.equipment;

import com.atodium.iridynamics.api.blockEntity.ITickable;
import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CrucibleBlockEntity extends SyncedBlockEntity implements ITickable {
    public CrucibleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRUCIBLE.get(), pos, state);
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {

    }

    @Override
    protected void writeSyncData(CompoundTag tag) {

    }

    @Override
    protected void readSyncData(CompoundTag tag) {

    }

    @Override
    protected void saveToTag(CompoundTag tag) {

    }

    @Override
    protected void loadFromTag(CompoundTag tag) {

    }
}