package com.atodium.iridynamics.api.blockEntity;

import com.atodium.iridynamics.api.rotate.IRotateNode;
import com.atodium.iridynamics.api.rotate.RotateModule;
import com.atodium.iridynamics.api.util.math.IntFraction;
import com.atodium.iridynamics.api.util.math.MathUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class RotateMachineBlockEntity extends SyncedBlockEntity implements IRotateNode, ISavedDataTickable {
    private double inertia, friction, angle, angularVelocity;

    public RotateMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.setInertia(1.0);
    }

    public abstract Direction direction();

    @Override
    public void blockTick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;
        this.sendSyncPacket();
    }

    public void setupRotate(ServerLevel level) {
        RotateModule.updateRotateBlock(level, this.getBlockPos());
        this.sendSyncPacket();
    }

    public double getRenderAngle(float partialTicks) {
        return MathUtil.castAngle(this.getAngle(this.direction()));
    }

    public double inertia() {
        return this.inertia;
    }

    public double friction() {
        return this.friction;
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
        return this.inertia;
    }

    @Override
    public double getTorque(Direction direction) {
        return 0.0;
    }

    @Override
    public double getFriction(Direction direction) {
        return this.friction;
    }

    @Override
    public double maxAngularVelocity(Direction direction) {
        return 300.0;
    }

    public double tickAngleChange() {
        return Math.abs(this.angularVelocity / 20.0);
    }

    public void setInertia(double inertia) {
        this.inertia = inertia;
    }

    public void setFriction(double friction) {
        this.friction = friction;
    }

    @Override
    protected void writeSyncData(CompoundTag tag) {
        tag.putDouble("angle", this.angle);
        tag.putDouble("angularVelocity", this.angularVelocity);
    }

    @Override
    protected void readSyncData(CompoundTag tag) {
        this.angle = tag.getDouble("angle");
        this.angularVelocity = tag.getDouble("angularVelocity");
    }

    @Override
    protected void saveToTag(CompoundTag tag) {
        tag.putDouble("angle", this.angle);
        tag.putDouble("angularVelocity", this.angularVelocity);
    }

    @Override
    protected void loadFromTag(CompoundTag tag) {
        this.angle = tag.getDouble("angle");
        this.angularVelocity = tag.getDouble("angularVelocity");
    }
}