package com.atodium.iridynamics.common.blockEntity.rotate;

import com.atodium.iridynamics.api.blockEntity.ISavedDataTickable;
import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.api.rotate.IRotateNode;
import com.atodium.iridynamics.api.rotate.RotateModule;
import com.atodium.iridynamics.api.rotate.axle.AxleCoverType;
import com.atodium.iridynamics.api.util.math.IntFraction;
import com.atodium.iridynamics.api.util.math.MathUtil;
import com.atodium.iridynamics.common.block.rotate.AxleBlock;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class AxleBlockEntity extends SyncedBlockEntity implements IRotateNode, ISavedDataTickable {
    private double angle, angularVelocity;
    private final AxleCoverType[] covers;

    public AxleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.AXLE.get(), pos, state);
        this.covers = new AxleCoverType[6];
    }

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
        return MathUtil.castAngle(this.getAngle(direction()));
    }

    private Direction direction() {
        return this.getBlockState().getValue(AxleBlock.DIRECTION);
    }

    private AxleBlock.AxleType axleType() {
        return ((AxleBlock) this.getBlockState().getBlock()).axleType();
    }

    @Override
    public boolean isConnectable(Direction direction) {
        return this.direction() == direction || this.direction() == direction.getOpposite();
    }

    @Override
    public boolean isRelated(Direction from, Direction to) {
        return (this.direction() == from && this.direction() == to.getOpposite()) || (this.direction() == to && this.direction() == from.getOpposite());
    }

    @Override
    public IntFraction getRelation(Direction from, Direction to) {
        if (this.isRelated(from, to)) return IntFraction.NEG_ONE;
        return null;
    }

    @Override
    public double getAngle(Direction direction) {
        return direction == this.direction() ? this.angle : direction == this.direction().getOpposite() ? MathUtil.castAngle(-this.angle) : 0.0;
    }

    @Override
    public double getAngularVelocity(Direction direction) {
        return direction == this.direction() ? this.angularVelocity : direction == this.direction().getOpposite() ? -this.angularVelocity : 0.0;
    }

    @Override
    public void setAngle(Direction direction, double angle) {
        if (direction == this.direction()) this.angle = MathUtil.castAngle(angle);
        if (direction == this.direction().getOpposite()) this.angle = MathUtil.castAngle(-angle);
    }

    @Override
    public void setAngularVelocity(Direction direction, double angularVelocity) {
        if (direction == this.direction()) this.angularVelocity = angularVelocity;
        if (direction == this.direction().getOpposite()) this.angularVelocity = -angularVelocity;
    }

    @Override
    public double getInertia(Direction direction) {
        if (this.isConnectable(direction)) return this.axleType().inertia() / 2.0;
        return 0.0;
    }

    @Override
    public double getTorque(Direction direction) {
        return 0.0;
    }

    @Override
    public double getFriction(Direction direction) {
        if (this.isConnectable(direction)) return this.axleType().friction() / 2.0;
        return 0.0;
    }

    @Override
    public double maxAngularVelocity(Direction direction) {
        return this.axleType().maxAngularVelocity();
    }

    @Override
    protected void writeSyncData(CompoundTag tag) {
        tag.putDouble("angle", this.angle);
        tag.putDouble("angularVelocity", this.angularVelocity);
        int[] coverIndexes = new int[6];
        for (int i = 0; i < 6; i++) coverIndexes[i] = this.covers[i] == null ? -1 : this.covers[i].index();
        tag.putIntArray("covers", coverIndexes);
    }

    @Override
    protected void readSyncData(CompoundTag tag) {
        this.angle = tag.getDouble("angle");
        this.angularVelocity = tag.getDouble("angularVelocity");
        int[] coverIndexes = tag.getIntArray("covers");
        for (int i = 0; i < 6; i++) this.covers[i] = AxleCoverType.getTypeByIndex(coverIndexes[i]);
    }

    @Override
    protected void saveToTag(CompoundTag tag) {
        tag.putDouble("angle", this.angle);
        tag.putDouble("angularVelocity", this.angularVelocity);
        int[] coverIndexes = new int[6];
        for (int i = 0; i < 6; i++) coverIndexes[i] = this.covers[i] == null ? -1 : this.covers[i].index();
        tag.putIntArray("covers", coverIndexes);
    }

    @Override
    protected void loadFromTag(CompoundTag tag) {
        this.angle = tag.getDouble("angle");
        this.angularVelocity = tag.getDouble("angularVelocity");
        int[] coverIndexes = tag.getIntArray("covers");
        for (int i = 0; i < 6; i++) this.covers[i] = AxleCoverType.getTypeByIndex(coverIndexes[i]);
    }
}