package com.atodium.iridynamics.common.blockEntity.rotate;

import com.atodium.iridynamics.api.blockEntity.IRotateNodeHolder;
import com.atodium.iridynamics.api.blockEntity.ITickable;
import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.api.rotate.Clockwork;
import com.atodium.iridynamics.api.rotate.RotateModule;
import com.atodium.iridynamics.api.util.math.MathUtil;
import com.atodium.iridynamics.common.block.rotate.ClockworkBlock;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ClockworkBlockEntity extends SyncedBlockEntity implements ITickable, IRotateNodeHolder<Clockwork> {
    public static final double MAX_TORQUE = 1000.0;
    public static final double MAX_ANGLE = MathUtil.TWO_PI * 10;

    private Clockwork rotate;
    private int handleTick;
    private double angle;

    public ClockworkBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CLOCKWORK.get(), pos, state);
        this.rotate = RotateModule.clockwork(this.getBlockState().getValue(ClockworkBlock.DIRECTION));
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;
        if (this.handleTick > 0) {
            this.angle = Math.min(this.angle + MAX_ANGLE / 200.0, MAX_ANGLE);
            this.handleTick--;
            this.rotate.setTorque(0.0);
        } else {
            this.rotate.setTorque(this.angle / MAX_ANGLE * MAX_TORQUE);
            double tickAngle = this.rotate.tickAngleChange();
            if (tickAngle > 0) this.angle = Math.max(this.angle - tickAngle, 0.0);
        }
        this.sendSyncPacket();
    }

    public void setupRotate(ServerLevel level) {
        RotateModule.addRotateBlock(level, this.getBlockPos(), this.rotate);
        this.sendSyncPacket();
    }

    public double getRenderAngle(float partialTicks) {
        return MathUtil.castAngle(this.rotate.getAngle(this.getBlockState().getValue(ClockworkBlock.DIRECTION)));
    }

    public void handle() {
        this.handleTick += 4;
    }

    @Override
    public void receiveRotateNode(Clockwork node) {
        this.rotate = node;
    }

    @Override
    protected void writeSyncData(CompoundTag tag) {
        tag.put("rotateSync", RotateModule.writeSyncTag(this.rotate));
    }

    @Override
    protected void readSyncData(CompoundTag tag) {
        RotateModule.readSyncTag(this.rotate, tag.getCompound("rotateSync"));
    }

    @Override
    protected void saveToTag(CompoundTag tag) {

    }

    @Override
    protected void loadFromTag(CompoundTag tag) {

    }
}