package com.atodium.iridynamics.common.level.data.rotate;

import com.atodium.iridynamics.api.blockEntity.IRotateNode;
import com.atodium.iridynamics.api.util.math.IntFraction;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ChunkPos;

import java.util.Deque;
import java.util.EnumMap;
import java.util.Map;

public class RotateNetwork {
    public static final Direction[] ORDER = Direction.values();

    private final RotateSavedData savedData;
    public final Map<BlockPos, IRotateNode> allNodes;
    public final Map<ChunkPos, Integer> chunkNodeCount;
    public BlockPos center;
    public Direction centerDirection;
    public boolean locked;
    private final Map<BlockPos, EnumMap<Direction, IntFraction>> scaleMap;
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

    protected ImmutableMap<BlockPos, IRotateNode> getAllNodes() {
        return ImmutableMap.copyOf(this.allNodes);
    }

    protected ImmutableSet<ChunkPos> getAllChunks() {
        return ImmutableSet.copyOf(this.chunkNodeCount.keySet());
    }

    protected boolean containsPos(BlockPos pos) {
        return this.allNodes.containsKey(pos);
    }

    protected Map<BlockPos, IRotateNode> searchAllNodes(BlockPos pos, Direction direction) {
        Map<BlockPos, IRotateNode> relatives = Maps.newHashMap();
        Deque<BlockPos> task = Lists.newLinkedList();
        task.addLast(pos);
        while (!task.isEmpty()) {
            BlockPos current = task.pollFirst();
            for (Direction to : ORDER) {
                BlockPos posTo = current.relative(to);
                if (!posTo.equals(pos) && !relatives.containsKey(posTo) && this.containsPos(posTo)) {
                    task.offerLast(posTo);
                    relatives.put(posTo, this.allNodes.get(posTo));
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

    protected void addAllNodes(Map<BlockPos, IRotateNode> nodes) {
        for (Map.Entry<BlockPos, IRotateNode> entry : nodes.entrySet()) {
            BlockPos pos = entry.getKey();
            if (this.allNodes.containsKey(pos)) continue;
            IRotateNode node = entry.getValue();
            this.allNodes.put(pos, node);
            this.addBlockPos(pos);
            if (this.allNodes.size() == 1) {
                this.center = pos;
                for (Direction direction : Direction.values())
                    if (node.isConnectable(direction)) this.centerDirection = direction;
            }
        }
        this.updateStructure();
    }

    protected void addNode(BlockPos pos, IRotateNode node) {
        if (this.allNodes.containsKey(pos)) return;
        this.allNodes.put(pos, node);
        this.addBlockPos(pos);
        if (this.allNodes.size() == 1) {
            this.center = pos;
            for (Direction direction : Direction.values())
                if (node.isConnectable(direction)) this.centerDirection = direction;
        }
        this.updateStructure();
    }

    protected void removeNode(BlockPos pos) {
        if (!this.allNodes.containsKey(pos)) return;
        this.allNodes.remove(pos);
        if (this.allNodes.isEmpty()) {
            this.savedData.removeNetwork(this);
        } else {
            if (this.center.equals(pos)) {
                this.center = this.allNodes.keySet().toArray(new BlockPos[0])[0];
                IRotateNode centerNode = this.allNodes.get(this.center);
                for (Direction direction : Direction.values())
                    if (centerNode.isConnectable(direction)) this.centerDirection = direction;
            }
            this.removeBlockPos(pos);
            this.updateStructure();
        }
    }

    private void addBlockPos(BlockPos pos) {
        ChunkPos chunk = new ChunkPos(pos);
        int newCount = this.chunkNodeCount.compute(chunk, (c, oldCount) -> oldCount == null ? 1 : oldCount + 1);
        if (newCount == 1) this.savedData.addNetworkChunk(chunk, this);
    }

    private void removeBlockPos(BlockPos pos) {
        ChunkPos chunk = new ChunkPos(pos);
        int newCount = this.chunkNodeCount.computeIfPresent(chunk, (c, oldCount) -> Math.max(oldCount - 1, 0));
        if (newCount == 0) {
            this.chunkNodeCount.remove(chunk);
            this.savedData.removeNetworkChunk(chunk, this);
        }
    }

    private void updateStructure() {
        System.out.println("-------------------------------------");
        this.scaleMap.clear();
        this.locked = false;
        IRotateNode centerNode = this.allNodes.get(this.center);
        for (Direction to : ORDER) {
            if (to == this.centerDirection || !centerNode.isRelated(this.centerDirection, to)) continue;
            IntFraction s = centerNode.getRelation(this.centerDirection, to);
            this.putScale(this.center, to, s);
            if (this.search(this.center.relative(to), to.getOpposite(), s.negate())) {
                this.locked = true;
                break;
            }
            System.out.println("-------------------");
        }
        if (!this.locked) {
            this.putScale(this.center, this.centerDirection, IntFraction.ONE);
            if (this.search(this.center.relative(this.centerDirection), this.centerDirection.getOpposite(), IntFraction.NEG_ONE))
                this.locked = true;
        }
    }

    private boolean search(BlockPos pos, Direction direction, IntFraction scale) {
        System.out.println(pos + " " + direction + " " + scale);
        if (this.scaleMap.containsKey(pos) && this.scaleMap.get(pos).containsKey(direction))
            return this.putScale(pos, direction, scale);
        else if (this.putScale(pos, direction, scale)) return true;
        if (!this.containsPos(pos)) return false;
        IRotateNode node = this.allNodes.get(pos);
        for (Direction to : ORDER) {
            if (to == direction || !node.isRelated(direction, to)) continue;
            IntFraction s = scale.multiply(node.getRelation(direction, to));
            this.putScale(pos, to, s);
            if (this.search(pos.relative(to), to.getOpposite(), s.negate())) return true;
        }
        return false;
    }

    private boolean putScale(BlockPos pos, Direction direction, IntFraction scale) {
        if (this.scaleMap.containsKey(pos) && this.scaleMap.get(pos).containsKey(direction) && !scale.equals(this.scaleMap.get(pos).get(direction)))
            return true;
        if (!this.scaleMap.containsKey(pos)) this.scaleMap.put(pos, new EnumMap<>(Direction.class));
        this.scaleMap.get(pos).put(direction, scale);
        return false;
    }
}