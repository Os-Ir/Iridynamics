package com.atodium.iridynamics.common.blockEntity.factory.smelter;

import com.atodium.iridynamics.api.blockEntity.ITickable;
import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class MultiblockCrucibleBlockEntity extends SyncedBlockEntity implements ITickable {
    public static final double HEAT_CAPACITY = 100000.0, INVENTORY_RESISTANCE = 0.01;
    public static final double[] RESISTANCE = new double[]{0.02, 0.2, 0.1, 0.1, 0.1, 0.1};
    public static final int CAPACITY = 9216;

    public MultiblockCrucibleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MULTIBLOCK_CRUCIBLE.get(), pos, state);
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {

    }

    public ItemStack tryAddItem(ItemStack stack) {
        return stack;
    }

    @Override
    protected void writeSyncData(CompoundTag tag) {

    }

    @Override
    protected void readSyncData(CompoundTag tag) {

    }

    @Override
    protected void saveToTag(CompoundTag tag) {

    }

    @Override
    protected void loadFromTag(CompoundTag tag) {

    }
}