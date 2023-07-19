package com.atodium.iridynamics.api.multiblock;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Optional;

public abstract class StructureInfo<T extends StructureInfo.StructureData> {
    public abstract ResourceLocation id();

    public abstract T createEmptyData();

    public abstract Optional<T> validate(MultiblockStructure structure);

    public void onStructureFinish(ServerLevel level, StructureData data, MultiblockStructure structure) {

    }

    public void onStructureDestroyed(ServerLevel level, StructureData data, MultiblockStructure structure) {

    }

    public abstract static class StructureData implements INBTSerializable<CompoundTag> {

    }
}