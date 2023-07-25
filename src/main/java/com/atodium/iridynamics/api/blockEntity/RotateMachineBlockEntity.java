package com.atodium.iridynamics.api.blockEntity;

import com.atodium.iridynamics.api.rotate.Machine;
import com.atodium.iridynamics.api.rotate.RotateModule;
import com.atodium.iridynamics.api.util.math.MathUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class RotateMachineBlockEntity extends SyncedBlockEntity implements ITickable, IRotateNodeHolder<Machine> {
    protected Supplier<Direction> direction;
    protected Machine rotate;
    protected double inertia, friction;

    public RotateMachineBlockEntity(BlockEntityType<?> type, Supplier<Direction> direction, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.direction = direction;
        this.rotate = RotateModule.machine(direction.get());
        this.inertia = 1.0;
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;
        RotateModule.tryTick((ServerLevel) level, pos);
        this.sendSyncPacket();
    }

    @Override
    public void receiveRotateNode(Machine node) {
        this.rotate = node;
    }

    public void setupRotate(ServerLevel level) {
        RotateModule.addRotateBlock(level, this.getBlockPos(), this.rotate);
        this.sendSyncPacket();
    }

    public double getRenderAngle(float partialTicks) {
        return MathUtil.castAngle(this.rotate.getAngle(this.direction.get()));
    }

    public double inertia() {
        return this.inertia;
    }

    public double friction() {
        return this.friction;
    }

    protected void setInertia(double inertia) {
        this.inertia = inertia;
    }

    public void setFriction(double friction) {
        this.friction = friction;
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

    }

    @Override
    protected void loadFromTag(CompoundTag tag) {

    }
}