package com.atodium.iridynamics.api.pipe;

import com.atodium.iridynamics.api.util.data.DataUtil;
import com.atodium.iridynamics.api.util.data.PosDirection;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Deque;
import java.util.Map;
import java.util.Set;

public class LiquidPipeNetwork implements INBTSerializable<CompoundTag> {
    private final LiquidPipeSavedData savedData;
    private final Map<PosDirection, ILiquidPipeNode> allNodes;
    private final Map<ChunkPos, Integer> chunkNodeCount;
    private PosDirection center;

    public LiquidPipeNetwork(LiquidPipeSavedData savedData) {
        this.savedData = savedData;
        this.allNodes = Maps.newHashMap();
        this.chunkNodeCount = Maps.newHashMap();
    }

    private ServerLevel level() {
        return this.savedData.level();
    }

    protected boolean isEmpty() {
        return this.allNodes.isEmpty();
    }

    protected Set<ChunkPos> getAllChunks() {
        return this.chunkNodeCount.keySet();
    }

    protected boolean contains(PosDirection info) {
        return this.allNodes.containsKey(info);
    }

    protected PosDirection center() {
        return this.center;
    }

    protected void tryTick(ServerLevel level) {

    }

    protected Map<PosDirection, ILiquidPipeNode> searchAllNodes(PosDirection info) {
        Map<PosDirection, ILiquidPipeNode> relatives = Maps.newHashMap();
        Deque<PosDirection> task = Lists.newLinkedList();
        BlockPos origin = info.pos();
        task.addLast(info);
        while (!task.isEmpty()) {
            PosDirection relative = task.pollFirst().relative();
            ILiquidPipeNode relativeNode = this.allNodes.get(relative);
            if (relativeNode == null || relative.pos().equals(origin) || relatives.containsKey(relative)) continue;
            relatives.put(relative, relativeNode);
            for (Direction to : DataUtil.DIRECTIONS) {
                if (to != relative.direction() && relativeNode.isRelated(relative.direction(), to)) {
                    PosDirection change = relative.change(to);
                    task.addLast(change);
                    relatives.put(change, relativeNode);
                }
            }
        }
        return relatives;
    }

    protected void combine(LiquidPipeNetwork... networks) {
        for (LiquidPipeNetwork network : networks) {
            this.allNodes.putAll(network.allNodes);
            this.savedData.removeNetwork(network);
            network.chunkNodeCount.forEach((chunk, count) -> {
                if (this.chunkNodeCount.containsKey(chunk))
                    this.chunkNodeCount.put(chunk, this.chunkNodeCount.get(chunk) + count);
                else this.chunkNodeCount.put(chunk, count);
            });
        }
    }

    protected LiquidPipeNetwork addAllNodes(Map<PosDirection, ILiquidPipeNode> nodes) {
        for (Map.Entry<PosDirection, ILiquidPipeNode> entry : nodes.entrySet()) {
            PosDirection info = entry.getKey();
            if (this.allNodes.containsKey(info)) continue;
            this.allNodes.put(info, entry.getValue());
            this.addChunkCount(info.chunk());
            if (this.allNodes.size() == 1) this.center = info;
        }
        return this;
    }

    protected LiquidPipeNetwork addNode(PosDirection info, ILiquidPipeNode node) {
        if (this.allNodes.containsKey(info)) return this;
        this.allNodes.put(info, node);
        this.addChunkCount(info.chunk());
        if (this.allNodes.size() == 1) this.center = info;
        return this;
    }

    protected LiquidPipeNetwork removeNode(PosDirection info) {
        if (!this.allNodes.containsKey(info)) return this;
        this.allNodes.remove(info);
        if (this.allNodes.isEmpty()) this.savedData.removeNetwork(this);
        else {
            if (this.center.equals(info)) this.center = this.allNodes.keySet().toArray(new PosDirection[0])[0];
            this.removeChunkCount(info.chunk());
        }
        return this;
    }

    private void addChunkCount(ChunkPos chunk) {
        int newCount = this.chunkNodeCount.compute(chunk, (c, oldCount) -> oldCount == null ? 1 : oldCount + 1);
        if (newCount == 1) this.savedData.addNetworkChunk(chunk, this);
    }

    private void removeChunkCount(ChunkPos chunk) {
        int newCount = this.chunkNodeCount.computeIfPresent(chunk, (c, oldCount) -> Math.max(oldCount - 1, 0));
        if (newCount == 0) {
            this.chunkNodeCount.remove(chunk);
            this.savedData.removeNetworkChunk(chunk, this);
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag nodesTag = new ListTag();
        for (PosDirection pos : this.allNodes.keySet()) nodesTag.add(pos.save());
        tag.put("nodes", nodesTag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.allNodes.clear();
        ListTag nodesTag = tag.getList("nodes", Tag.TAG_COMPOUND);
        int size = nodesTag.size();
        Map<PosDirection, ILiquidPipeNode> nodes = Maps.newHashMap();
        for (int i = 0; i < size; i++) {
            PosDirection pos = PosDirection.load(nodesTag.getCompound(i));
            nodes.put(pos, LiquidPipeModule.readPipeNode(this.level(), pos));
        }
        this.addAllNodes(nodes);
    }
}