package com.atodium.iridynamics.api.pipe;

import com.atodium.iridynamics.api.util.data.PosDirection;
import com.atodium.iridynamics.api.util.math.MathUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.*;

public class LiquidPipeNetwork implements INBTSerializable<CompoundTag> {
    public static final int MAX_UPDATE_IN_TICK = 20;
    public static final Comparator<PosDirection> POS_COMPARATOR = (posA, posB) -> Integer.compare(posB.pos().getY(), posA.pos().getY());

    private final LiquidPipeSavedData savedData;
    private final Map<PosDirection, ILiquidPipeNode> allPosNodes;
    private final Map<ChunkPos, Integer> chunkNodeCount;
    private long lastTickTime;

    public LiquidPipeNetwork(LiquidPipeSavedData savedData) {
        this.savedData = savedData;
        this.allPosNodes = Maps.newHashMap();
        this.chunkNodeCount = Maps.newHashMap();
    }

    private ServerLevel level() {
        return this.savedData.level();
    }

    protected boolean isEmpty() {
        return this.allPosNodes.isEmpty();
    }

    protected Set<ChunkPos> getAllChunks() {
        return this.chunkNodeCount.keySet();
    }

    protected boolean contains(PosDirection info) {
        return this.allPosNodes.containsKey(info);
    }

    protected boolean shouldTick(ServerLevel level) {
        for (PosDirection pos : this.allPosNodes.keySet())
            if (level.shouldTickBlocksAt(ChunkPos.asLong(pos.pos()))) return true;
        return false;
    }

    protected void tryTick(long time) {
        if (time <= this.lastTickTime) return;
        this.lastTickTime = time;
        Object2DoubleMap<PosDirection> pressures = this.updatePressure();
        for (int i = 0; i < MAX_UPDATE_IN_TICK; i++) if (this.updateLiquidContainer(pressures)) break;
    }

    private boolean updateLiquidContainer(Object2DoubleMap<PosDirection> pressures) {
        boolean exit = true;
        Set<ILiquidPipeNode> finish = Sets.newHashSet();
        for (PosDirection pos : pressures.keySet()) {
            ILiquidPipeNode node = this.allPosNodes.get(pos);
            PosDirection relative = pos.relative();
            ILiquidPipeNode relativeNode = this.allPosNodes.get(relative);
            if (relativeNode == null || finish.contains(node) || finish.contains(relativeNode)) continue;
            finish.add(node);
            finish.add(relativeNode);
            double dp = pressures.getDouble(pos) - pressures.getDouble(relative);
            if (pos.direction() == Direction.UP) dp -= 1.0;
            else if (pos.direction() == Direction.DOWN) dp += 1.0;
            int flow = Math.min(node.maxFlowRate(pos.direction()), relativeNode.maxFlowRate(relative.direction()));
            flow = MathUtil.isEquals(dp, 0.0) ? 0 : dp > 0.0 ? Math.min(flow, Math.min(node.fluidAmount(), relativeNode.remainCapacity())) : -Math.min(flow, Math.min(node.remainCapacity(), relativeNode.fluidAmount()));
            node.addFluidAmount(-flow);
            relativeNode.addFluidAmount(flow);
            if (flow != 0) exit = false;
        }
        return exit;
    }

    private Object2DoubleMap<PosDirection> updatePressure() {
        Object2DoubleMap<PosDirection> pressures = new Object2DoubleOpenHashMap<>();
        List<PosDirection> sortedNodes = List.copyOf(this.allPosNodes.keySet());
        sortedNodes.sort(POS_COMPARATOR);
        for (PosDirection pos : sortedNodes) {
            if (pressures.containsKey(pos)) continue;
            ILiquidPipeNode node = this.allPosNodes.get(pos);
            double nodePressure = node.scaledPressure();
            for (Direction direction : node.connectableDirections()) pressures.put(pos.change(direction), nodePressure);
            if (node.full()) for (Direction direction : node.connectableDirections()) {
                Deque<PosDirection> task = Lists.newLinkedList();
                task.addLast(pos.change(direction));
                while (!task.isEmpty()) {
                    PosDirection from = task.pollFirst();
                    PosDirection relative = from.relative();
                    ILiquidPipeNode relativeNode = this.allPosNodes.get(relative);
                    if (relativeNode == null || pressures.containsKey(relative) || !relativeNode.full()) continue;
                    if (from.direction() == Direction.UP) nodePressure -= 1.0;
                    else if (from.direction() == Direction.DOWN) nodePressure += 1.0;
                    for (Direction relativeDirection : relativeNode.connectableDirections()) {
                        pressures.put(pos.change(relativeDirection), nodePressure);
                        if (relativeDirection != relative.direction())
                            task.addLast(relative.change(relativeDirection));
                    }
                }
            }
        }
        return pressures;
    }

    protected Map<PosDirection, ILiquidPipeNode> searchAllNodes(PosDirection pos) {
        Map<PosDirection, ILiquidPipeNode> relatives = Maps.newHashMap();
        Deque<PosDirection> task = Lists.newLinkedList();
        BlockPos origin = pos.pos();
        task.addLast(pos);
        while (!task.isEmpty()) {
            PosDirection relative = task.pollFirst().relative();
            ILiquidPipeNode relativeNode = this.allPosNodes.get(relative);
            if (relativeNode == null || relative.pos().equals(origin) || relatives.containsKey(relative)) continue;
            for (Direction relativeDirection : relativeNode.connectableDirections()) {
                relatives.put(relative.change(relativeDirection), relativeNode);
                if (relativeDirection != relative.direction()) task.addLast(relative.change(relativeDirection));
            }
        }
        return relatives;
    }

    protected void combine(LiquidPipeNetwork... networks) {
        for (LiquidPipeNetwork network : networks) {
            this.allPosNodes.putAll(network.allPosNodes);
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
            if (this.allPosNodes.containsKey(info)) continue;
            this.allPosNodes.put(info, entry.getValue());
            this.addChunkCount(info.chunk());
        }
        return this;
    }

    protected LiquidPipeNetwork addNode(PosDirection info, ILiquidPipeNode node) {
        if (this.allPosNodes.containsKey(info)) return this;
        this.allPosNodes.put(info, node);
        this.addChunkCount(info.chunk());
        return this;
    }

    protected LiquidPipeNetwork removeNode(PosDirection info) {
        if (!this.allPosNodes.containsKey(info)) return this;
        this.allPosNodes.remove(info);
        if (this.allPosNodes.isEmpty()) this.savedData.removeNetwork(this);
        else this.removeChunkCount(info.chunk());
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
        for (PosDirection pos : this.allPosNodes.keySet()) nodesTag.add(pos.save());
        tag.put("nodes", nodesTag);
        tag.putLong("lastTickTime", this.lastTickTime);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.allPosNodes.clear();
        ListTag nodesTag = tag.getList("nodes", Tag.TAG_COMPOUND);
        int size = nodesTag.size();
        Map<PosDirection, ILiquidPipeNode> nodes = Maps.newHashMap();
        for (int i = 0; i < size; i++) {
            PosDirection pos = PosDirection.load(nodesTag.getCompound(i));
            nodes.put(pos, LiquidPipeModule.readPipeNode(this.level(), pos.pos()));
        }
        this.addAllNodes(nodes);
        this.lastTickTime = tag.getLong("lastTickTime");
    }
}