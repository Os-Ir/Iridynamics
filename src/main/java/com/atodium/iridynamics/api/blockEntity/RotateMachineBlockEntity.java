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

public abstract class RotateMachineBlockEntity extends SyncedBlockEntity implements ITickable, IRotateNodeHolder<Machine> {
    protected Machine rotate;
    private double inertia, friction;

    public RotateMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.rotate = RotateModule.machine(this.direction());
        this.setInertia(1.0);
    }

    public abstract Direction direction();

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
        return MathUtil.castAngle(this.rotate.getAngle(this.direction()));
    }

    public double inertia() {
        return this.inertia;
    }

    public double friction() {
        return this.friction;
    }

    protected void setInertia(double inertia) {
        this.inertia = inertia;
        this.rotate.setInertia(inertia);
    }

    public void setFriction(double friction) {
        this.friction = friction;
        this.rotate.setFriction(friction);
    }

    @Override
    protected void writeSyncData(CompoundTag tag) {
        tag.put("rotateSync", RotateModule.writeSyncTag(this.rotate));
    }

    @Override
    protected void readSyncData(CompoundTag tag) {
        RotateModule.readSyncTag(this.rotate, tag.getCompound("rotateSync"));
    }
}