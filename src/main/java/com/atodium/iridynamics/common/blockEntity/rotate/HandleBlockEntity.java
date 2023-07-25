package com.atodium.iridynamics.common.blockEntity.rotate;

import com.atodium.iridynamics.api.blockEntity.IRotateNodeHolder;
import com.atodium.iridynamics.api.blockEntity.ITickable;
import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.api.rotate.Handle;
import com.atodium.iridynamics.api.rotate.RotateModule;
import com.atodium.iridynamics.api.util.math.MathUtil;
import com.atodium.iridynamics.common.block.rotate.HandleBlock;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class HandleBlockEntity extends SyncedBlockEntity implements ITickable, IRotateNodeHolder<Handle> {
    public static final double MAX_TORQUE = 400.0, MAX_POWER = 100.0;

    private Handle rotate;
    private boolean isLeft;
    private int handleTick;

    public HandleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HANDLE.get(), pos, state);
        this.rotate = RotateModule.handle(this.getBlockState().getValue(HandleBlock.DIRECTION));
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;
        if (this.handleTick > 0) {
            double torque;
            double o = this.rotate.getAngularVelocity(state.getValue(HandleBlock.DIRECTION));
            if (this.isLeft) torque = o > 0.0 ? Math.min(MAX_TORQUE, MAX_POWER / o) : MAX_TORQUE;
            else torque = o < 0.0 ? -Math.min(MAX_TORQUE, -MAX_POWER / o) : -MAX_TORQUE;
            this.rotate.setTorque(torque);
            this.handleTick--;
        } else this.rotate.setTorque(0.0);
        RotateModule.tryTick((ServerLevel) level, pos);
        this.sendSyncPacket();
    }

    public void setupRotate(ServerLevel level) {
        RotateModule.addRotateBlock(level, this.getBlockPos(), this.rotate);
        this.sendSyncPacket();
    }

    public double getRenderAngle(float partialTicks) {
        Direction direction = this.getBlockState().getValue(HandleBlock.DIRECTION);
        return MathUtil.castAngle(this.rotate.getAngle(direction));
    }

    public void handle(boolean isLeft) {
        this.isLeft = isLeft;
        this.handleTick += 4;
    }

    @Override
    public void receiveRotateNode(Handle node) {
        this.rotate = node;
    }

    @Override
    protected CompoundTag writeSyncData(CompoundTag tag) {
        tag.put("rotateSync", RotateModule.writeSyncTag(this.rotate));
        return tag;
    }

    @Override
    protected void readSyncData(CompoundTag tag) {
        RotateModule.readSyncTag(this.rotate, tag.getCompound("rotateSync"));
    }

    @Override
    protected void saveToTag(CompoundTag tag) {
        tag.putBoolean("isLeft", this.isLeft);
        tag.putInt("handleTick", this.handleTick);
    }

    @Override
    protected void loadFromTag(CompoundTag tag) {
        this.isLeft = tag.getBoolean("isLeft");
        this.handleTick = tag.getInt("handleTick");
    }
}