package com.atodium.iridynamics.api.rotate;

import com.atodium.iridynamics.api.util.data.DataUtil;
import com.atodium.iridynamics.api.util.data.PosDirection;
import com.atodium.iridynamics.api.util.math.IntFraction;
import com.atodium.iridynamics.api.util.math.MathUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
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

public class RotateNetwork implements INBTSerializable<CompoundTag> {
    private final RotateSavedData savedData;
    private final Map<PosDirection, IRotateNode> allNodes;
    private final Map<ChunkPos, Integer> chunkNodeCount;
    private PosDirection center;
    private boolean locked;
    private final Map<PosDirection, IntFraction> scaleMap;
    private double angularMomentum, angle, angularVelocity;
    private long lastTickTime;

    public RotateNetwork(RotateSavedData savedData) {
        this.savedData = savedData;
        this.allNodes = Maps.newHashMap();
        this.chunkNodeCount = Maps.newHashMap();
        this.scaleMap = Maps.newHashMap();
    }

    private ServerLevel level() {
        return this.savedData.level();
    }

    protected boolean isEmpty() {
        return this.allNodes.isEmpty();
    }

    protected boolean isLocked() {
        return this.locked;
    }

    protected Set<ChunkPos> getAllChunks() {
        return this.chunkNodeCount.keySet();
    }

    protected boolean contains(PosDirection info) {
        return this.allNodes.containsKey(info);
    }

    protected boolean shouldTick(ServerLevel level) {
        for (PosDirection pos : this.allNodes.keySet())
            if (level.shouldTickBlocksAt(ChunkPos.asLong(pos.pos()))) return true;
        return false;
    }

    protected void tryTick(long time) {
        if (time <= this.lastTickTime) return;
        this.lastTickTime = time;
        if (this.locked) {
            this.angularMomentum = 0.0;
            this.angle = 0.0;
            for (Map.Entry<PosDirection, IRotateNode> entry : this.allNodes.entrySet()) {
                PosDirection info = entry.getKey();
                BlockPos pos = info.pos();
                IRotateNode node = entry.getValue();
                node.setAngle(info.direction(), 0.0);
                node.setAngularVelocity(info.direction(), 0.0);
            }
        } else {
            this.angle = MathUtil.castAngle(this.angle + this.angularVelocity / 20.0, MathUtil.TWO_PI / this.updateAngularVelocity());
            Set<BlockPos> received = Sets.newHashSet();
            for (Map.Entry<PosDirection, IRotateNode> entry : this.allNodes.entrySet()) {
                PosDirection info = entry.getKey();
                BlockPos pos = info.pos();
                IRotateNode node = entry.getValue();
                double scale = this.scaleMap.get(info).doubleValue();
                node.setAngle(info.direction(), MathUtil.castAngle(this.angle * scale));
                node.setAngularVelocity(info.direction(), this.angularVelocity * scale);
            }
        }
    }

    protected double updateAngularVelocity() {
        double scaledInertia = 0.0;
        double scaledTorque = 0.0;
        double maxAngularVelocity = Double.MAX_VALUE;
        double minScale = Double.MAX_VALUE;
        int frictionDirection = Double.compare(this.angularMomentum, 0.0);
        for (Map.Entry<PosDirection, IRotateNode> entry : this.allNodes.entrySet()) {
            PosDirection info = entry.getKey();
            Direction direction = info.direction();
            IRotateNode node = entry.getValue();
            double scale = this.scaleMap.get(info).doubleValue();
            minScale = Math.min(minScale, Math.abs(scale));
            scaledInertia += node.getInertia(direction) * scale * scale;
            scaledTorque += node.getTorque(direction) * scale;
            scaledTorque -= Math.abs(node.getFriction(info.direction()) * scale) * frictionDirection;
            maxAngularVelocity = Math.min(maxAngularVelocity, Math.abs(node.maxAngularVelocity(direction) / scale));
        }
        this.angularMomentum += scaledTorque / 20.0;
        this.angularVelocity = this.angularMomentum / scaledInertia;
        if (this.angularVelocity > maxAngularVelocity) {
            this.angularVelocity = maxAngularVelocity;
            this.angularMomentum = scaledInertia * maxAngularVelocity;
        } else if (-this.angularVelocity > maxAngularVelocity) {
            this.angularVelocity = -maxAngularVelocity;
            this.angularMomentum = -scaledInertia * maxAngularVelocity;
        }
        return minScale;
    }

    protected PosDirection center() {
        return this.center;
    }

    protected IntFraction scale(PosDirection info) {
        return this.scaleMap.get(info);
    }

    protected double angle(PosDirection info) {
        if (!this.scaleMap.containsKey(info)) return 0.0;
        return MathUtil.castAngle(this.angle * this.scaleMap.get(info).doubleValue());
    }

    protected double angularVelocity(PosDirection info) {
        if (!this.scaleMap.containsKey(info)) return 0.0;
        if (MathUtil.isEquals(this.angularVelocity, 0.0)) this.updateAngularVelocity();
        return this.angularVelocity * this.scaleMap.get(info).doubleValue();
    }

    protected void setAngularVelocity(double angularVelocity) {
        double scaledInertia = 0.0;
        int frictionDirection = Double.compare(this.angularMomentum, 0.0);
        for (Map.Entry<PosDirection, IRotateNode> entry : this.allNodes.entrySet()) {
            PosDirection info = entry.getKey();
            IRotateNode node = entry.getValue();
            double scale = this.scaleMap.get(info).doubleValue();
            scaledInertia += node.getInertia(info.direction()) * scale * scale;
        }
        this.angularVelocity = angularVelocity;
        this.angularMomentum = angularVelocity * scaledInertia;
    }

    protected void setAngle(double angle) {
        this.angle = angle;
    }

    protected Map<PosDirection, IRotateNode> searchAllNodes(PosDirection info) {
        Map<PosDirection, IRotateNode> relatives = Maps.newHashMap();
        Deque<PosDirection> task = Lists.newLinkedList();
        BlockPos origin = info.pos();
        task.addLast(info);
        while (!task.isEmpty()) {
            PosDirection relative = task.pollFirst().relative();
            IRotateNode relativeNode = this.allNodes.get(relative);
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

    protected RotateNetwork addAllNodes(Map<PosDirection, IRotateNode> nodes) {
        for (Map.Entry<PosDirection, IRotateNode> entry : nodes.entrySet()) {
            PosDirection info = entry.getKey();
            if (this.allNodes.containsKey(info)) continue;
            this.allNodes.put(info, entry.getValue());
            this.addChunkCount(info.chunk());
            if (this.allNodes.size() == 1) this.center = info;
        }
        this.updateStructure();
        return this;
    }

    protected RotateNetwork addNode(PosDirection info, IRotateNode node) {
        if (this.allNodes.containsKey(info)) return this;
        this.allNodes.put(info, node);
        this.addChunkCount(info.chunk());
        if (this.allNodes.size() == 1) this.center = info;
        this.updateStructure();
        return this;
    }

    protected RotateNetwork removeNode(PosDirection info) {
        if (!this.allNodes.containsKey(info)) return this;
        this.allNodes.remove(info);
        if (this.allNodes.isEmpty()) this.savedData.removeNetwork(this);
        else {
            if (this.center.equals(info)) this.center = this.allNodes.keySet().toArray(new PosDirection[0])[0];
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
        Deque<PosDirection> task = Lists.newLinkedList();
        IRotateNode centerNode = this.allNodes.get(this.center);
        Direction centerDirection = this.center.direction();
        task.addLast(this.center);
        this.scaleMap.put(this.center, IntFraction.ONE);
        for (Direction to : DataUtil.DIRECTIONS) {
            if (to != centerDirection && centerNode.isRelated(centerDirection, to)) {
                PosDirection change = this.center.change(to);
                task.addLast(change);
                this.scaleMap.put(change, centerNode.getRelation(centerDirection, to));
            }
        }
        while (!task.isEmpty()) {
            PosDirection poll = task.pollFirst();
            PosDirection relative = poll.relative();
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
            this.scaleMap.put(relative, relativeScale);
            for (Direction to : DataUtil.DIRECTIONS) {
                if (to != relative.direction() && relativeNode.isRelated(relative.direction(), to)) {
                    PosDirection change = relative.change(to);
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
        for (Map.Entry<PosDirection, IRotateNode> entry : this.allNodes.entrySet()) nodesTag.add(entry.getKey().save());
        tag.put("nodes", nodesTag);
        tag.putDouble("angularMomentum", this.angularMomentum);
        tag.putDouble("angle", this.angle);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.allNodes.clear();
        ListTag nodesTag = tag.getList("nodes", Tag.TAG_COMPOUND);
        int size = nodesTag.size();
        Map<PosDirection, IRotateNode> nodes = Maps.newHashMap();
        for (int i = 0; i < size; i++) {
            PosDirection pos = PosDirection.load(nodesTag.getCompound(i));
            nodes.put(pos, RotateModule.getLevelRotateNode(this.level(), pos.pos()));
        }
        this.addAllNodes(nodes);
        this.angularMomentum = tag.getDouble("angularMomentum");
        this.angle = tag.getDouble("angle");
    }
}