package com.atodium.iridynamics.common.blockEntity.equipment;

import com.atodium.iridynamics.api.blockEntity.SyncedBlockEntity;
import com.atodium.iridynamics.common.blockEntity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class BarrelBlockEntity extends SyncedBlockEntity {
    private final FluidTank tank;

    public BarrelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BARREL.get(), pos, state);
        this.tank = new FluidTank(8000);
    }

    public void handle(Player player, InteractionHand hand, Level level, BlockPos pos, Direction direction) {
        FluidUtil.interactWithFluidHandler(player, hand, level, pos, direction);
        this.markDirty();
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction direction) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return LazyOptional.of(() -> this.tank).cast();
        return super.getCapability(capability, direction);
    }

    @Override
    protected void writeSyncData(CompoundTag tag) {

    }

    @Override
    protected void readSyncData(CompoundTag tag) {

    }

    @Override
    protected void saveToTag(CompoundTag tag) {
        this.tank.writeToNBT(tag);
    }

    @Override
    protected void loadFromTag(CompoundTag tag) {
        this.tank.readFromNBT(tag);
    }
}