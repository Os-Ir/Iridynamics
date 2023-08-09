package com.atodium.iridynamics.common.blockEntity.rotate;

import com.atodium.iridynamics.api.blockEntity.ISavedDataTickable;
import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.api.rotate.IRotateNode;
import com.atodium.iridynamics.api.rotate.RotateModule;
import com.atodium.iridynamics.api.util.math.IntFraction;
import com.atodium.iridynamics.api.util.math.MathUtil;
import com.atodium.iridynamics.common.block.rotate.AxleBlock;
import com.atodium.iridynamics.common.block.rotate.HandleBlock;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class HandleBlockEntity extends SyncedBlockEntity implements IRotateNode, ISavedDataTickable {
    public static final double MAX_TORQUE = 400.0, MAX_POWER = 100.0;

    private boolean isLeft;
    private int handleTick;
    private double torque, angle, angularVelocity;

    public HandleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HANDLE.get(), pos, state);
    }

    @Override
    public void blockTick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;
        if (this.handleTick > 0) {
            double o = this.getAngularVelocity(state.getValue(HandleBlock.DIRECTION));
            if (this.isLeft) this.torque = o > 0.0 ? Math.min(MAX_TORQUE, MAX_POWER / o) : MAX_TORQUE;
            else this.torque = o < 0.0 ? -Math.min(MAX_TORQUE, -MAX_POWER / o) : -MAX_TORQUE;
            this.handleTick--;
        } else this.torque = 0.0;
        this.sendSyncPacket();
    }

    public void setupRotate(ServerLevel level) {
        RotateModule.updateRotateBlock(level, this.getBlockPos());
        this.sendSyncPacket();
    }

    public double getRenderAngle(float partialTicks) {
        Direction direction = this.getBlockState().getValue(HandleBlock.DIRECTION);
        return MathUtil.castAngle(this.getAngle(direction));
    }

    public void handle(boolean isLeft) {
        this.isLeft = isLeft;
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
        if (this.isConnectable(direction)) return 20.0;
        return 0.0;
    }

    @Override
    public double getTorque(Direction direction) {
        return this.torque;
    }

    @Override
    public double getFriction(Direction direction) {
        if (this.isConnectable(direction)) return 0.4;
        return 0.0;
    }

    @Override
    public double maxAngularVelocity(Direction direction) {
        return 300.0;
    }

    @Override
    protected void writeSyncData(CompoundTag tag) {
        tag.putDouble("torque", this.torque);
        tag.putDouble("angle", this.angle);
        tag.putDouble("angularVelocity", this.angularVelocity);
    }

    @Override
    protected void readSyncData(CompoundTag tag) {
        this.torque = tag.getDouble("torque");
        this.angle = tag.getDouble("angle");
        this.angularVelocity = tag.getDouble("angularVelocity");
    }

    @Override
    protected void saveToTag(CompoundTag tag) {
        tag.putDouble("torque", this.torque);
        tag.putDouble("angle", this.angle);
        tag.putDouble("angularVelocity", this.angularVelocity);
    }

    @Override
    protected void loadFromTag(CompoundTag tag) {
        this.torque = tag.getDouble("torque");
        this.angle = tag.getDouble("angle");
        this.angularVelocity = tag.getDouble("angularVelocity");
    }
}