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
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Deque;
import java.util.Map;
import java.util.Set;

public class MultiblockStructure implements INBTSerializable<CompoundTag> {
    private final MultiblockSavedData savedData;
    private final Map<BlockPos, Block> allBlocks;
    private final Map<ChunkPos, Integer> chunkBlockCount;
    private BlockPos root;

    public MultiblockStructure(MultiblockSavedData savedData) {
        this.savedData = savedData;
        this.allBlocks = Maps.newHashMap();
        this.chunkBlockCount = Maps.newHashMap();
    }

    protected BlockPos root() {
        return this.root;
    }

    protected boolean isEmpty() {
        return this.allBlocks.isEmpty();
    }

    protected Set<ChunkPos> getAllChunks() {
        return this.chunkBlockCount.keySet();
    }

    protected boolean contains(BlockPos pos) {
        return this.allBlocks.containsKey(pos);
    }

    protected Map<BlockPos, Block> searchAllBlocks(BlockPos pos, Direction direction) {
        Map<BlockPos, Block> relatives = Maps.newHashMap();
        Deque<BlockPos> task = Lists.newLinkedList();
        task.addLast(pos.relative(direction));
        while (!task.isEmpty()) {
            BlockPos poll = task.pollFirst();
            if (!this.allBlocks.containsKey(poll)) continue;
            for (Direction to : DataUtil.DIRECTIONS) {
                BlockPos relative = poll.relative(to);
                if (relative.equals(pos) || !this.allBlocks.containsKey(relative)) continue;
                task.addLast(relative);
                relatives.put(relative, this.allBlocks.get(relative));
            }
        }
        return relatives;
    }

    protected void combine(MultiblockStructure... structures) {
        for (MultiblockStructure structure : structures) {
            this.allBlocks.putAll(structure.allBlocks);
            this.savedData.removeStructure(structure);
            structure.chunkBlockCount.forEach((chunk, count) -> {
                if (this.chunkBlockCount.containsKey(chunk))
                    this.chunkBlockCount.put(chunk, this.chunkBlockCount.get(chunk) + count);
                else this.chunkBlockCount.put(chunk, count);
            });
        }
        this.updateStructure();
    }

    protected MultiblockStructure addAllBlocks(Map<BlockPos, Block> blocks) {
        for (Map.Entry<BlockPos, Block> entry : blocks.entrySet()) {
            BlockPos pos = entry.getKey();
            if (this.allBlocks.containsKey(pos)) continue;
            this.allBlocks.put(pos, entry.getValue());
            this.addChunkCount(new ChunkPos(pos));
        }
        this.updateStructure();
        return this;
    }

    protected MultiblockStructure addNode(BlockPos pos, Block block) {
        if (this.allBlocks.containsKey(pos)) return this;
        this.allBlocks.put(pos, block);
        this.addChunkCount(new ChunkPos(pos));
        this.updateStructure();
        return this;
    }

    protected MultiblockStructure removeNode(BlockPos pos) {
        if (!this.allBlocks.containsKey(pos)) return this;
        this.allBlocks.remove(pos);
        if (this.allBlocks.isEmpty()) this.savedData.removeStructure(this);
        else {
            this.removeChunkCount(new ChunkPos(pos));
            this.updateStructure();
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

    private void updateStructure() {
        int mx = Integer.MAX_VALUE;
        int my = Integer.MAX_VALUE;
        int mz = Integer.MAX_VALUE;
        for (BlockPos pos : this.allBlocks.keySet()) {
            mx = Math.min(mx, pos.getX());
            my = Math.min(my, pos.getY());
            mz = Math.min(mz, pos.getZ());
        }
        this.root = new BlockPos(mx, my, mz);
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
        this.addAllBlocks(blocks);
    }
}