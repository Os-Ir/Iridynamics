package com.atodium.iridynamics.api.multiblock;

import com.atodium.iridynamics.api.util.data.DataUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Deque;
import java.util.Map;
import java.util.Set;

public class MultiblockStructure implements INBTSerializable<CompoundTag> {
    private final MultiblockSavedData savedData;
    protected final Map<BlockPos, Block> allBlocks;
    private final Map<ChunkPos, Integer> chunkBlockCount;
    private Map<BlockPos, Block> structureBlocksCache;
    private BlockPos root, size;
    private StructureInfo<?> structureInfo;
    private StructureInfo.StructureData structureData;

    public MultiblockStructure(MultiblockSavedData savedData) {
        this.savedData = savedData;
        this.allBlocks = Maps.newHashMap();
        this.chunkBlockCount = Maps.newHashMap();
    }

    @SuppressWarnings("unchecked")
    public <T extends StructureInfo.StructureData> StructureInfo<T> structureInfo() {
        return (StructureInfo<T>) this.structureInfo;
    }

    @SuppressWarnings("unchecked")
    public <T extends StructureInfo.StructureData> T structureData() {
        return (T) this.structureData;
    }

    public Map<BlockPos, Block> structureBlocks() {
        if (this.structureBlocksCache == null) {
            this.structureBlocksCache = Maps.newHashMap();
            this.allBlocks.forEach((pos, block) -> this.structureBlocksCache.put(pos.subtract(this.root), block));
        }
        return this.structureBlocksCache;
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

    public Set<ChunkPos> getAllChunks() {
        return this.chunkBlockCount.keySet();
    }

    public boolean contains(BlockPos pos) {
        return this.allBlocks.containsKey(pos);
    }

    protected Map<BlockPos, Block> searchAllBlocks(BlockPos pos, Direction direction) {
        Map<BlockPos, Block> relatives = Maps.newHashMap();
        Deque<BlockPos> task = Lists.newLinkedList();
        BlockPos start = pos.relative(direction);
        task.addLast(start);
        relatives.put(start, this.allBlocks.get(start));
        while (!task.isEmpty()) {
            BlockPos poll = task.pollFirst();
            if (!this.allBlocks.containsKey(poll)) continue;
            for (Direction to : DataUtil.DIRECTIONS) {
                BlockPos relative = poll.relative(to);
                if (relative.equals(pos) || !this.allBlocks.containsKey(relative) || relatives.containsKey(relative))
                    continue;
                task.addLast(relative);
                relatives.put(relative, this.allBlocks.get(relative));
            }
        }
        return relatives;
    }

    protected void combine(ServerLevel level, MultiblockStructure... structures) {
        for (MultiblockStructure structure : structures) {
            this.allBlocks.putAll(structure.allBlocks);
            this.savedData.removeStructure(structure);
            structure.chunkBlockCount.forEach((chunk, count) -> {
                if (this.chunkBlockCount.containsKey(chunk))
                    this.chunkBlockCount.put(chunk, this.chunkBlockCount.get(chunk) + count);
                else this.chunkBlockCount.put(chunk, count);
            });
        }
        this.updateStructure(level);
    }

    protected MultiblockStructure addAllBlocks(ServerLevel level, Map<BlockPos, Block> blocks) {
        for (Map.Entry<BlockPos, Block> entry : blocks.entrySet()) {
            BlockPos pos = entry.getKey();
            if (this.allBlocks.containsKey(pos)) continue;
            this.allBlocks.put(pos, entry.getValue());
            this.addChunkCount(new ChunkPos(pos));
        }
        this.updateStructure(level);
        return this;
    }

    private MultiblockStructure addAllBlocksInternal(Map<BlockPos, Block> blocks) {
        for (Map.Entry<BlockPos, Block> entry : blocks.entrySet()) {
            BlockPos pos = entry.getKey();
            if (this.allBlocks.containsKey(pos)) continue;
            this.allBlocks.put(pos, entry.getValue());
            this.addChunkCount(new ChunkPos(pos));
        }
        this.updateStructureInternal();
        return this;
    }

    protected MultiblockStructure addBlock(ServerLevel level, BlockPos pos, Block block) {
        if (this.allBlocks.containsKey(pos)) return this;
        this.allBlocks.put(pos, block);
        this.addChunkCount(new ChunkPos(pos));
        this.updateStructure(level);
        return this;
    }

    protected MultiblockStructure removeBlock(ServerLevel level, BlockPos pos) {
        if (!this.allBlocks.containsKey(pos)) return this;
        this.allBlocks.remove(pos);
        if (this.allBlocks.isEmpty()) {
            this.savedData.removeStructure(this);
            this.destroyStructure(level);
        } else {
            this.removeChunkCount(new ChunkPos(pos));
            this.updateStructure(level);
        }
        return this;
    }

    private void addChunkCount(ChunkPos chunk) {
        int newCount = this.chunkBlockCount.compute(chunk, (c, oldCount) -> oldCount == null ? 1 : oldCount + 1);
        if (newCount == 1) this.savedData.addStructureChunk(chunk, this);
    }

    private void removeChunkCount(ChunkPos chunk) {
        int newCount = this.chunkBlockCount.computeIfPresent(chunk, (c, oldCount) -> Math.max(oldCount - 1, 0));
        if (newCount == 0) {
            this.chunkBlockCount.remove(chunk);
            this.savedData.removeStructureChunk(chunk, this);
        }
    }

    private void updateStructureInternal() {
        System.out.println(this.structureInfo);
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;
        for (BlockPos pos : this.allBlocks.keySet()) {
            minX = Math.min(minX, pos.getX());
            minY = Math.min(minY, pos.getY());
            minZ = Math.min(minZ, pos.getZ());
            maxX = Math.max(maxX, pos.getX());
            maxY = Math.max(maxY, pos.getY());
            maxZ = Math.max(maxZ, pos.getZ());
        }
        this.root = new BlockPos(minX, minY, minZ);
        this.size = new BlockPos(maxX - minX + 1, maxY - minY + 1, maxZ - minZ + 1);
        this.structureBlocksCache = null;
        Pair<StructureInfo<?>, StructureInfo.StructureData> result = MultiblockModule.validateStructure(this);
        if (result != null) {
            this.structureInfo = result.getKey();
            this.structureData = result.getValue();
        }
    }

    protected void updateStructure(ServerLevel level) {
        this.destroyStructure(level);
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;
        for (BlockPos pos : this.allBlocks.keySet()) {
            minX = Math.min(minX, pos.getX());
            minY = Math.min(minY, pos.getY());
            minZ = Math.min(minZ, pos.getZ());
            maxX = Math.max(maxX, pos.getX());
            maxY = Math.max(maxY, pos.getY());
            maxZ = Math.max(maxZ, pos.getZ());
        }
        this.root = new BlockPos(minX, minY, minZ);
        this.size = new BlockPos(maxX - minX + 1, maxY - minY + 1, maxZ - minZ + 1);
        this.structureBlocksCache = null;
        Pair<StructureInfo<?>, StructureInfo.StructureData> result = MultiblockModule.validateStructure(this);
        if (result != null) {
            this.structureInfo = result.getKey();
            this.structureData = result.getValue();
            this.structureInfo.onStructureFinish(level, this.structureData, this);
        }
    }

    protected void destroyStructure(ServerLevel level) {
        if (this.structureInfo != null) this.structureInfo.onStructureDestroyed(level, this.structureData, this);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag blocksTag = new ListTag();
        for (Map.Entry<BlockPos, Block> entry : this.allBlocks.entrySet()) {
            CompoundTag blockTag = new CompoundTag();
            BlockPos pos = entry.getKey();
            blockTag.putString("block", MultiblockModule.getBlockId(entry.getValue()).toString());
            blockTag.putInt("x", pos.getX());
            blockTag.putInt("y", pos.getY());
            blockTag.putInt("z", pos.getZ());
            blocksTag.add(blockTag);
        }
        tag.put("blocks", blocksTag);
        if (this.structureInfo != null) tag.putString("info", this.structureInfo.id().toString());
        if (this.structureData != null) tag.put("data", this.structureData.serializeNBT());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.allBlocks.clear();
        ListTag blocksTag = tag.getList("blocks", Tag.TAG_COMPOUND);
        Map<BlockPos, Block> blocks = Maps.newHashMap();
        for (int i = 0; i < blocksTag.size(); i++) {
            CompoundTag blockTag = blocksTag.getCompound(i);
            blocks.put(new BlockPos(blockTag.getInt("x"), blockTag.getInt("y"), blockTag.getInt("z")), MultiblockModule.getBlock(new ResourceLocation(blockTag.getString("block"))));
        }
        this.addAllBlocksInternal(blocks);
        if (tag.contains("info"))
            this.structureInfo = MultiblockModule.getStructureInfo(new ResourceLocation(tag.getString("info")));
        if (this.structureInfo != null) this.structureData = this.structureInfo.createEmptyData();
        if (tag.contains("data")) this.structureData.deserializeNBT(tag.getCompound("data"));
    }
}