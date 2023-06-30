package com.atodium.iridynamics.common.blockEntity.rotate;

import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.api.module.rotate.Axle;
import com.atodium.iridynamics.api.module.rotate.RotateModule;
import com.atodium.iridynamics.common.block.rotate.AxleBlock;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public class AxleBlockEntity extends SyncedBlockEntity {
    private boolean isClientSetup;
    private Axle rotate;

    public AxleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.AXLE.get(), pos, state);
    }

    public void setupRotate(ServerLevel level) {
        this.rotate = RotateModule.axle(this.getBlockState().getValue(AxleBlock.DIRECTION));
        RotateModule.addRotateBlock(level, this.getBlockPos(), this.rotate);
        this.markForSync();
    }

    @Override
    protected CompoundTag writeSyncData(CompoundTag tag) {
        if (!this.isClientSetup) {
            tag.put("rotate", RotateModule.writeRotateNode(this.rotate));
            this.isClientSetup = true;
        }
        tag.put("rotateSync", RotateModule.writeSyncTag(this.rotate));
        return tag;
    }

    @Override
    protected void readSyncData(CompoundTag tag) {
        if (tag.contains("rotate")) this.rotate = (Axle) RotateModule.readRotateNode(tag.getCompound("rotate"));
        RotateModule.readSyncTag(this.rotate, tag.getCompound("rotateSync"));
    }

    @Override
    protected void saveToTag(CompoundTag tag) {
        tag.put("rotate", RotateModule.writeRotateNode(this.rotate));
    }

    @Override
    protected void loadFromTag(CompoundTag tag) {
        this.rotate = (Axle) RotateModule.readRotateNode(tag.getCompound("rotate"));
        if (this.rotate == null) this.rotate = RotateModule.axle(this.getBlockState().getValue(AxleBlock.DIRECTION));
    }
}