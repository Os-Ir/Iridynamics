package com.atodium.iridynamics.api.multiblock;

import com.atodium.iridynamics.api.util.data.DataUtil;
import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Set;

public class AssembledMultiblockStructure implements INBTSerializable<CompoundTag> {
    private final MultiblockSavedData savedData;
    private final Set<BlockPos> allBlocks;
    private BlockPos root, size;
    private AssembledStructureInfo<?> structureInfo;
    private AssembledStructureInfo.StructureData structureData;

    public AssembledMultiblockStructure(MultiblockSavedData savedData) {
        this.savedData = savedData;
        this.allBlocks = Sets.newHashSet();
    }

    public AssembledMultiblockStructure(MultiblockSavedData savedData, Set<BlockPos> allBlocks, AssembledStructureInfo<?> structureInfo, AssembledStructureInfo.StructureData structureData) {
        this.savedData = savedData;
        this.allBlocks = allBlocks;
        this.structureInfo = structureInfo;
        this.structureData = structureData;
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;
        for (BlockPos pos : this.allBlocks) {
            minX = Math.min(minX, pos.getX());
            minY = Math.min(minY, pos.getY());
            minZ = Math.min(minZ, pos.getZ());
            maxX = Math.max(maxX, pos.getX());
            maxY = Math.max(maxY, pos.getY());
            maxZ = Math.max(maxZ, pos.getZ());
        }
        this.root = new BlockPos(minX, minY, minZ);
        this.size = new BlockPos(maxX - minX + 1, maxY - minY + 1, maxZ - minZ + 1);
    }

    public <T extends AssembledStructureInfo.StructureData> AssembledStructureInfo<T> structureInfo() {
        return DataUtil.cast(this.structureInfo);
    }

    public <T extends AssembledStructureInfo.StructureData> T structureData() {
        return DataUtil.cast(this.structureData);
    }

    public BlockPos root() {
        return this.root;
    }

    public BlockPos size() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.allBlocks.isEmpty();
    }

    public boolean contains(BlockPos pos) {
        return this.allBlocks.contains(pos);
    }

    public Set<BlockPos> allBlocks() {
        return this.allBlocks;
    }

    public void finishStructure(ServerLevel level) {
        this.structureInfo.onStructureFinish(level, this.structureData(), this);
    }

    public void destroyStructure(ServerLevel level) {
        this.structureInfo.onStructureDestroyed(level, this.structureData(), this);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag blocksTag = new ListTag();
        for (BlockPos pos : this.allBlocks) blocksTag.add(DataUtil.saveBlockPos(pos));
        tag.put("blocks", blocksTag);
        tag.put("root", DataUtil.saveBlockPos(this.root));
        tag.put("size", DataUtil.saveBlockPos(this.size));
        tag.putString("info", this.structureInfo.id().toString());
        tag.put("data", this.structureData.serializeNBT());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.allBlocks.clear();
        ListTag blocksTag = tag.getList("blocks", Tag.TAG_COMPOUND);
        for (int i = 0; i < blocksTag.size(); i++) this.allBlocks.add(DataUtil.loadBlockPos(blocksTag.getCompound(i)));
        this.root = DataUtil.loadBlockPos(tag.getCompound("root"));
        this.size = DataUtil.loadBlockPos(tag.getCompound("size"));
        this.structureInfo = MultiblockModule.getAssembledStructureInfo(new ResourceLocation(tag.getString("info")));
        this.structureData = this.structureInfo.createEmptyData();
        this.structureData.deserializeNBT(tag.getCompound("data"));
        this.allBlocks.forEach((pos) -> this.savedData.setBlockAssembledStructure(pos, this));
    }
}