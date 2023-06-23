package com.atodium.iridynamics.common.blockEntity.equipment;

import com.atodium.iridynamics.api.blockEntity.IIgnitable;
import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.common.block.equipment.BlowerBlock;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlowerBlockEntity extends SyncedBlockEntity {
    public static final long BLOW_INTERVAL = 40;
    public static final int BLOW_VOLUME = 400;

    private long lastBlowTime;

    public BlowerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BLOWER.get(), pos, state);
    }

    public boolean blow() {
        long time = this.level.getGameTime();
        if (time - this.lastBlowTime < BLOW_INTERVAL) return false;
        Direction direction = this.getBlockState().getValue(BlowerBlock.DIRECTION);
        BlockPos targetPos = this.getBlockPos().relative(direction);
        BlockEntity targetEntity = this.level.getBlockEntity(targetPos);
        if (!(targetEntity instanceof IIgnitable ignitable)) return false;
        ignitable.blow(direction.getOpposite(), BLOW_VOLUME);
        this.lastBlowTime = time;
        return true;
    }

    @Override
    protected CompoundTag writeSyncData(CompoundTag tag) {
        tag.putLong("lastBlowTime", this.lastBlowTime);
        return tag;
    }

    @Override
    protected void readSyncData(CompoundTag tag) {
        this.lastBlowTime = tag.getLong("lastBlowTime");
    }

    @Override
    protected void saveToTag(CompoundTag tag) {
        tag.putLong("lastBlowTime", this.lastBlowTime);
    }

    @Override
    protected void loadFromTag(CompoundTag tag) {
        this.lastBlowTime = tag.getLong("lastBlowTime");
    }
}