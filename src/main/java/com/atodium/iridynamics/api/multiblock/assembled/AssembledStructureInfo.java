package com.atodium.iridynamics.api.multiblock.assembled;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Set;

public abstract class AssembledStructureInfo<T extends AssembledStructureInfo.StructureData> {
    public abstract ResourceLocation id();

    public abstract T createEmptyData();

    public abstract LazyOptional<T> validate(ServerLevel level, BlockPos checkPoint);

    public void onStructureFinish(ServerLevel level, T data, AssembledMultiblockStructure structure) {

    }

    public void onStructureDestroyed(ServerLevel level, T data, AssembledMultiblockStructure structure) {

    }

    public abstract static class StructureData implements INBTSerializable<CompoundTag> {
        public abstract Set<BlockPos> allAssembledBlocks();
    }
}