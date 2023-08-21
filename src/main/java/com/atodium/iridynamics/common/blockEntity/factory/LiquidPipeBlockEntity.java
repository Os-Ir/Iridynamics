package com.atodium.iridynamics.common.blockEntity.factory;

import com.atodium.iridynamics.api.blockEntity.ISavedDataTickable;
import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.api.pipe.ILiquidPipeNode;
import com.atodium.iridynamics.api.pipe.ILiquidPipeNodeContainer;
import com.atodium.iridynamics.api.pipe.LiquidPipeModule;
import com.atodium.iridynamics.common.block.factory.LiquidPipeBlock;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class LiquidPipeBlockEntity extends SyncedBlockEntity implements ILiquidPipeNodeContainer, ILiquidPipeNode, ISavedDataTickable {
    public static final int CAPACITY = 2000;
    public static final int FLOW_RATE = 100;

    public int amount;

    public LiquidPipeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LIQUID_PIPE.get(), pos, state);
    }

    @Override
    public void blockTick(Level level, BlockPos pos, BlockState state) {
//        System.out.println(pos + ": " + state.getValue(LiquidPipeBlock.CONNECTION));
    }

    public void updateBlockState(int index) {
        if (this.level.isClientSide) return;
        this.level.getBlockEntity(this.getBlockPos(), ModBlockEntities.LIQUID_PIPE.get()).ifPresent((pipe) -> {
            this.level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().getBlock().defaultBlockState().setValue(LiquidPipeBlock.CONNECTION, index));
            this.level.setBlockEntity(pipe);
        });
        LiquidPipeModule.updatePipeBlock((ServerLevel) this.level, this.getBlockPos());
    }

    @Override
    public boolean contains(Direction direction) {
        return true;
    }

    @Override
    public boolean connected(Direction direction) {
        return LiquidPipeBlock.isConnected(this.getBlockState().getValue(LiquidPipeBlock.CONNECTION), direction);
    }

    @Override
    public ILiquidPipeNode[] getAllBlockNodes() {
        return new ILiquidPipeNode[]{this};
    }

    @Override
    public int capacity() {
        return CAPACITY;
    }

    @Override
    public int fluidAmount() {
        return this.amount;
    }

    @Override
    public int addFluidAmount(int amount) {
        int add = this.amount + amount;
        if (add > CAPACITY) {
            int remain = add - CAPACITY;
            this.amount = CAPACITY;
            return remain;
        }
        this.amount = add;
        return 0;
    }

    @Override
    public boolean canInput() {
        return true;
    }

    @Override
    public boolean canOutput() {
        return true;
    }

    @Override
    public int maxFlowRate(Direction direction) {
        return FLOW_RATE;
    }

    @Override
    protected void writeSyncData(CompoundTag tag) {
        tag.putInt("amount", this.amount);
    }

    @Override
    protected void readSyncData(CompoundTag tag) {
        this.amount = tag.getInt("amount");
    }

    @Override
    protected void saveToTag(CompoundTag tag) {
        tag.putInt("amount", this.amount);
    }

    @Override
    protected void loadFromTag(CompoundTag tag) {
        this.amount = tag.getInt("amount");
    }
}