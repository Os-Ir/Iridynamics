package com.atodium.iridynamics.common.blockEntity.rotate;

import com.atodium.iridynamics.api.blockEntity.IRotateNodeHolder;
import com.atodium.iridynamics.api.blockEntity.ITickable;
import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.api.module.rotate.Escapement;
import com.atodium.iridynamics.api.module.rotate.IRotateNode;
import com.atodium.iridynamics.api.module.rotate.RotateModule;
import com.atodium.iridynamics.api.util.math.MathUtil;
import com.atodium.iridynamics.common.block.rotate.AxleBlock;
import com.atodium.iridynamics.common.block.rotate.EscapementBlock;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class EscapementBlockEntity extends SyncedBlockEntity implements ITickable, IRotateNodeHolder {
    private Escapement rotate;

    public EscapementBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ESCAPEMENT.get(), pos, state);
        this.rotate = RotateModule.escapement(state.getValue(EscapementBlock.DIRECTION));
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;
        RotateModule.tryTick((ServerLevel) level, pos);
        this.markForSync();
    }

    public void setupRotate(ServerLevel level) {
        RotateModule.addRotateBlock(level, this.getBlockPos(), this.rotate);
        this.markForSync();
    }

    public double getRenderAngle(float partialTicks) {
        Direction direction = this.getBlockState().getValue(AxleBlock.DIRECTION);
        return MathUtil.castAngle(this.rotate.getAngle(direction));
    }

    @Override
    public void receive(IRotateNode node) {
        this.rotate = (Escapement) node;
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