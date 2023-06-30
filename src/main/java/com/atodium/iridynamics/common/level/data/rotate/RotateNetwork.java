package com.atodium.iridynamics.common.level.data.rotate;

import com.atodium.iridynamics.api.blockEntity.IRotateNode;
import com.atodium.iridynamics.api.module.rotate.RotateModule;
import com.atodium.iridynamics.api.util.data.DirectionInfo;
import com.atodium.iridynamics.api.util.math.IntFraction;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Deque;
import java.util.Map;

public class RotateNetwork implements INBTSerializable<CompoundTag> {
    public static final Direction[] ORDER = Direction.values();

    private final RotateSavedData savedData;
    public final Map<DirectionInfo, IRotateNode> allNodes;
    public final Map<ChunkPos, Integer> chunkNodeCount;
    public DirectionInfo center;
    public boolean locked;
    private final Map<DirectionInfo, IntFraction> scaleMap;
    private double scaledInertia, angularMomentum;

    public RotateNetwork(RotateSavedData savedData) {
        this.savedData = savedData;
        this.allNodes = Maps.newHashMap();
        this.chunkNodeCount = Maps.newHashMap();
        this.scaleMap = Maps.newHashMap();
    }

    protected RotateSavedData getSavedData() {
        return this.savedData;
    }

    protected boolean isEmpty() {
        return this.allNodes.isEmpty();
    }

    protected boolean isLocked() {
        return this.locked;
    }

    protected ImmutableMap<DirectionInfo, IRotateNode> getAllNodes() {
        return ImmutableMap.copyOf(this.allNodes);
    }

    protected ImmutableSet<ChunkPos> getAllChunks() {
        return ImmutableSet.copyOf(this.chunkNodeCount.keySet());
    }

    protected boolean contains(DirectionInfo info) {
        return this.allNodes.containsKey(info);
    }

    protected Map<DirectionInfo, IRotateNode> searchAllNodes(DirectionInfo info) {
        Map<DirectionInfo, IRotateNode> relatives = Maps.newHashMap();
        Deque<DirectionInfo> task = Lists.newLinkedList();
        BlockPos origin = info.pos();
        task.addLast(info);
        while (!task.isEmpty()) {
            DirectionInfo relative = task.pollFirst().relative();
            IRotateNode relativeNode = this.allNodes.get(relative);
            if (relativeNode == null || relative.pos().equals(origin) || relatives.containsKey(relative)) continue;
            relatives.put(relative, relativeNode);
            for (Direction to : ORDER) {
                if (to != relative.direction() && relativeNode.isRelated(relative.direction(), to)) {
                    DirectionInfo change = relative.change(to);
                    task.addLast(change);
                    relatives.put(change, relativeNode);
                }
            }
        }
        return relatives;
    }

    protected void combine(RotateNetwork... networks) {
        for (RotateNetwork network : networks) {
            this.allNodes.putAll(network.allNodes);
            this.savedData.removeNetwork(network);
            network.chunkNodeCount.forEach((chunk, count) -> {
                if (this.chunkNodeCount.containsKey(chunk))
                    this.chunkNodeCount.put(chunk, this.chunkNodeCount.get(chunk) + count);
                else this.chunkNodeCount.put(chunk, count);
            });
        }
        this.updateStructure();
    }

    protected RotateNetwork addAllNodes(Map<DirectionInfo, IRotateNode> nodes) {
        for (Map.Entry<DirectionInfo, IRotateNode> entry : nodes.entrySet()) {
            DirectionInfo info = entry.getKey();
            if (this.allNodes.containsKey(info)) continue;
            this.allNodes.put(info, entry.getValue());
            this.addChunkCount(info.chunk());
            if (this.allNodes.size() == 1) this.center = info;
        }
        this.updateStructure();
        return this;
    }

    protected RotateNetwork addNode(DirectionInfo info, IRotateNode node) {
        if (this.allNodes.containsKey(info)) return this;
        this.allNodes.put(info, node);
        this.addChunkCount(info.chunk());
        if (this.allNodes.size() == 1) this.center = info;
        this.updateStructure();
        return this;
    }

    protected RotateNetwork removeNode(DirectionInfo info) {
        if (!this.allNodes.containsKey(info)) return this;
        this.allNodes.remove(info);
        if (this.allNodes.isEmpty()) {
            this.savedData.removeNetwork(this);
        } else {
            if (this.center.equals(info)) {
                this.center = this.allNodes.keySet().toArray(new DirectionInfo[0])[0];
                IRotateNode centerNode = this.allNodes.get(this.center);
            }
            this.removeChunkCount(info.chunk());
            this.updateStructure();
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

    private void updateStructure() {
        this.scaleMap.clear();
        this.locked = false;
        Deque<DirectionInfo> task = Lists.newLinkedList();
        IRotateNode centerNode = this.allNodes.get(this.center);
        Direction centerDirection = this.center.direction();
        task.addLast(this.center);
        this.scaleMap.put(this.center, IntFraction.ONE);
        for (Direction to : ORDER) {
            if (to != centerDirection && centerNode.isRelated(centerDirection, to)) {
                DirectionInfo change = this.center.change(to);
                task.addLast(change);
                this.scaleMap.put(change, centerNode.getRelation(centerDirection, to));
            }
        }
        while (!task.isEmpty()) {
            DirectionInfo poll = task.pollFirst();
            DirectionInfo relative = poll.relative();
            Direction relativeDirection = relative.direction();
            IRotateNode relativeNode = this.allNodes.get(relative);
            IntFraction relativeScale = this.scaleMap.get(poll).negate();
            if (relativeNode == null) continue;
            if (this.scaleMap.containsKey(relative)) {
                if (this.scaleMap.containsKey(relative) && !relativeScale.equals(this.scaleMap.get(relative))) {
                    this.locked = true;
                    return;
                }
                continue;
            }
            for (Direction to : ORDER) {
                if (to != relative.direction() && relativeNode.isRelated(relative.direction(), to)) {
                    DirectionInfo change = relative.change(to);
                    task.addLast(change);
                    this.scaleMap.put(change, relativeScale.multiply(relativeNode.getRelation(relativeDirection, to)));
                }
            }
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag nodesTag = new ListTag();
        for (Map.Entry<DirectionInfo, IRotateNode> entry : this.allNodes.entrySet()) {
            CompoundTag nodeTag = new CompoundTag();
            IRotateNode node = entry.getValue();
            IRotateNode.Serializer serializer = node.serializer();
            nodeTag.putString("id", RotateModule.SERIALIZERS.getKeyForValue(serializer).toString());
            nodeTag.put("pos", entry.getKey().save());
            nodeTag.put("node", serializer.serialize(node));
        }
        tag.put("nodes", nodesTag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.allNodes.clear();
        ListTag nodesTag = tag.getList("nodes", Tag.TAG_COMPOUND);
        int size = nodesTag.size();
        Map<DirectionInfo, IRotateNode> nodes = Maps.newHashMap();
        for (int i = 0; i < size; i++) {
            CompoundTag nodeTag = nodesTag.getCompound(i);
            IRotateNode.Serializer serializer = RotateModule.SERIALIZERS.get(new ResourceLocation(nodeTag.getString("id")));
            nodes.put(DirectionInfo.load(nodeTag.getCompound("pos")), serializer.deserialize(nodeTag.getCompound("node")));
        }
        this.addAllNodes(nodes);
        this.updateStructure();
    }
}