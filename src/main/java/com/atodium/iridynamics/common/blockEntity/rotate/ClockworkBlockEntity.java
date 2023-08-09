package com.atodium.iridynamics.common.blockEntity.rotate;

import com.atodium.iridynamics.api.blockEntity.ISavedDataTickable;
import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.api.rotate.IRotateNode;
import com.atodium.iridynamics.api.rotate.RotateModule;
import com.atodium.iridynamics.api.util.math.IntFraction;
import com.atodium.iridynamics.api.util.math.MathUtil;
import com.atodium.iridynamics.common.block.rotate.AxleBlock;
import com.atodium.iridynamics.common.block.rotate.ClockworkBlock;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ClockworkBlockEntity extends SyncedBlockEntity implements IRotateNode, ISavedDataTickable {
    public static final double MAX_TORQUE = 1000.0;
    public static final double MAX_ANGLE = MathUtil.TWO_PI * 10;

    private int handleTick;
    private double angle, angularVelocity, clockworkAngle, torque;

    public ClockworkBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CLOCKWORK.get(), pos, state);
    }

    @Override
    public void blockTick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;
        if (this.handleTick > 0) {
            this.clockworkAngle = Math.min(this.clockworkAngle + MAX_ANGLE / 200.0, MAX_ANGLE);
            this.handleTick--;
            this.torque = 0.0;
        } else {
            this.torque = this.clockworkAngle / MAX_ANGLE * MAX_TORQUE;
            double tickAngle = this.tickAngleChange();
            if (tickAngle > 0) this.clockworkAngle = Math.max(this.clockworkAngle - tickAngle, 0.0);
        }
        this.sendSyncPacket();
    }

    public void setupRotate(ServerLevel level) {
        RotateModule.updateRotateBlock(level, this.getBlockPos());
        this.sendSyncPacket();
    }

    public double getRenderAngle(float partialTicks) {
        return MathUtil.castAngle(this.getAngle(this.getBlockState().getValue(ClockworkBlock.DIRECTION)));
    }

    public void handle() {
        this.handleTick += 4;
    }

    private Direction direction() {
        return this.getBlockState().getValue(AxleBlock.DIRECTION);
    }

    @Override
    public boolean isConnectable(Direction direction) {
        return this.direction() == direction;
    }

    @Override
    public boolean isRelated(Direction from, Direction to) {
        return false;
    }

    @Override
    public IntFraction getRelation(Direction from, Direction to) {
        return null;
    }

    @Override
    public double getAngle(Direction direction) {
        return direction == this.direction() ? this.angle : 0.0;
    }

    @Override
    public double getAngularVelocity(Direction direction) {
        return direction == this.direction() ? this.angularVelocity : 0.0;
    }

    @Override
    public void setAngle(Direction direction, double angle) {
        if (direction == this.direction()) this.angle = MathUtil.castAngle(angle);
    }

    @Override
    public void setAngularVelocity(Direction direction, double angularVelocity) {
        if (direction == this.direction()) this.angularVelocity = angularVelocity;
    }

    @Override
    public double getInertia(Direction direction) {
        return 30.0;
    }

    @Override
    public double getTorque(Direction direction) {
        return this.torque;
    }

    @Override
    public double getFriction(Direction direction) {
        if (this.isConnectable(direction)) return 1.0;
        return 0.0;
    }

    @Override
    public double maxAngularVelocity(Direction direction) {
        return 300.0;
    }

    public double tickAngleChange() {
        return this.angularVelocity / 20.0;
    }

    @Override
    protected void writeSyncData(CompoundTag tag) {
        tag.putDouble("angle", this.angle);
        tag.putDouble("angularVelocity", this.angularVelocity);
        tag.putDouble("clockworkAngle", this.clockworkAngle);
        tag.putDouble("torque", this.torque);
    }

    @Override
    protected void readSyncData(CompoundTag tag) {
        this.angle = tag.getDouble("angle");
        this.angularVelocity = tag.getDouble("angularVelocity");
        this.clockworkAngle = tag.getDouble("clockworkAngle");
        this.torque = tag.getDouble("torque");
    }

    @Override
    protected void saveToTag(CompoundTag tag) {
        tag.putDouble("angle", this.angle);
        tag.putDouble("angularVelocity", this.angularVelocity);
        tag.putDouble("clockworkAngle", this.clockworkAngle);
        tag.putDouble("torque", this.torque);
    }

    @Override
    protected void loadFromTag(CompoundTag tag) {
        this.angle = tag.getDouble("angle");
        this.angularVelocity = tag.getDouble("angularVelocity");
        this.clockworkAngle = tag.getDouble("clockworkAngle");
        this.torque = tag.getDouble("torque");
    }
}