package com.atodium.iridynamics.api.blockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class SyncedBlockEntity extends BlockEntity {
    public SyncedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected abstract CompoundTag writeSyncData(CompoundTag tag);

    protected abstract void readSyncData(CompoundTag tag);

    protected abstract void saveToTag(CompoundTag tag);

    protected abstract void loadFromTag(CompoundTag tag);

    public void markForBlockUpdate() {
        if (this.level != null) {
            BlockState state = this.level.getBlockState(this.worldPosition);
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
            this.setChanged();
        }
    }

    public void markForSync() {
        this.sendPacket();
        this.setChanged();
    }

    public void markDirty() {
        this.setChanged();
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        super.saveAdditional(tag);
        return this.writeSyncData(tag);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.load(tag);
        this.readSyncData(tag);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet) {
        CompoundTag tag = packet.getTag();
        if (tag == null) tag = new CompoundTag();
        super.load(tag);
        this.readSyncData(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        this.saveToTag(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.loadFromTag(tag);
    }

    public void sendPacket() {
        ClientboundBlockEntityDataPacket packet = this.getUpdatePacket();
        if (packet != null && this.level instanceof ServerLevel serverLevel)
            serverLevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(this.worldPosition), false).forEach((player) -> player.connection.send(packet));
    }
}