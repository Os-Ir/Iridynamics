package com.atodium.iridynamics.common.blockEntity;

import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class BasinBlockEntity extends SyncedBlockEntity {
    public BasinBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BASIN.get(), pos, state);
    }

    @Override
    protected CompoundTag writeSyncData(CompoundTag tag) {
        return tag;
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