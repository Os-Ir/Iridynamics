package com.atodium.iridynamics.common.blockEntity.rotate;

import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.api.module.rotate.Gearbox;
import com.atodium.iridynamics.api.module.rotate.RotateModule;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public class GearboxBlockEntity extends SyncedBlockEntity {
    private boolean isClientSetup;
    private Gearbox rotate;

    public GearboxBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GEARBOX.get(), pos, state);
    }

    public void setupRotate(ServerLevel level) {
        this.rotate = RotateModule.gearbox(Direction.NORTH, Direction.SOUTH, 1, 1);
        RotateModule.addRotateBlock(level, this.getBlockPos(), this.rotate);
        this.markForSync();
    }

    public void updateDirection(Direction direction) {
        if (this.rotate.isConnectable(direction)) {
            int newGearA = this.rotate.getDirectionA() == direction ? (this.rotate.getGearA() == 1 ? 2 : 1) : this.rotate.getGearA();
            int newGearB = this.rotate.getDirectionB() == direction ? (this.rotate.getGearB() == 1 ? 2 : 1) : this.rotate.getGearB();
            this.rotate = RotateModule.gearbox(this.rotate.getDirectionA(), this.rotate.getDirectionB(), newGearA, newGearB);
        } else {
            this.rotate = RotateModule.gearbox(this.rotate.getDirectionB(), direction, this.rotate.getGearB(), 1);
        }
        this.updateGearbox();
    }

    private void updateGearbox() {
        RotateModule.updateRotateBlock((ServerLevel) this.level, this.getBlockPos(), this.rotate);
        this.isClientSetup = false;
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
        if (tag.contains("rotate")) this.rotate = (Gearbox) RotateModule.readRotateNode(tag.getCompound("rotate"));
        RotateModule.readSyncTag(this.rotate, tag.getCompound("rotateSync"));
    }

    @Override
    protected void saveToTag(CompoundTag tag) {
        tag.put("rotate", RotateModule.writeRotateNode(this.rotate));
    }

    @Override
    protected void loadFromTag(CompoundTag tag) {
        this.rotate = (Gearbox) RotateModule.readRotateNode(tag.getCompound("rotate"));
        if (this.rotate == null) this.rotate = RotateModule.gearbox(Direction.NORTH, Direction.SOUTH, 1, 1);
    }
}