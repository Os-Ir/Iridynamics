package com.atodium.iridynamics.common.level.data.rotate;

import com.atodium.iridynamics.api.blockEntity.IRotateNode;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Map;

public class RotateSavedData extends SavedData {
    public final List<RotateNetwork> allNetworks;
    public final Map<ChunkPos, List<RotateNetwork>> chunkNetworks;

    public RotateSavedData() {
        this.allNetworks = Lists.newArrayList();
        this.chunkNetworks = Maps.newHashMap();
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        return tag;
    }

    public void addNode(BlockPos pos, IRotateNode node) {
        List<RotateNetwork> relatives = Lists.newArrayList();
        for (Direction to : RotateNetwork.ORDER) {
            if (!node.isConnectable(to)) continue;
            BlockPos toPos = pos.relative(to);
            RotateNetwork networkTo = this.getPosNetwork(toPos);
            if (networkTo != null && !relatives.contains(networkTo)) relatives.add(networkTo);
        }
        if (relatives.isEmpty()) {
            RotateNetwork self = new RotateNetwork(this);
            self.addNode(pos, node);
            this.allNetworks.add(self);
        } else if (relatives.size() == 1) {
            RotateNetwork self = relatives.get(0);
            self.addNode(pos, node);
        } else {
            RotateNetwork base = relatives.get(0);
            RotateNetwork[] toCombine = new RotateNetwork[relatives.size() - 1];
            for (int i = 0; i < toCombine.length; i++) toCombine[i] = relatives.get(i + 1);
            base.combine(toCombine);
            base.addNode(pos, node);
        }
        this.setDirty();
    }

    public void removeNode(BlockPos pos) {
        RotateNetwork network = this.getPosNetwork(pos);
        List<BlockPos> unlinkedNetworks = Lists.newArrayList();
        outer:
        for (Direction to : RotateNetwork.ORDER) {
            BlockPos toPos = pos.relative(to);
            Map<BlockPos, IRotateNode> relatives = network.searchAllNodes(pos, to);
            for (BlockPos base : unlinkedNetworks) if (relatives.containsKey(base)) break outer;
            RotateNetwork sub = new RotateNetwork(this);
            sub.addAllNodes(relatives);
            this.allNetworks.add(sub);
        }
    }

    protected void removeNetwork(RotateNetwork network) {
        this.allNetworks.remove(network);
        network.getAllChunks().forEach((chunk) -> {
            if (this.chunkNetworks.containsKey(chunk)) this.chunkNetworks.get(chunk).remove(network);
        });
    }

    protected RotateNetwork getPosNetwork(BlockPos pos) {
        ChunkPos chunk = new ChunkPos(pos);
        if (!this.chunkNetworks.containsKey(chunk)) return null;
        for (RotateNetwork network : this.chunkNetworks.get(chunk))
            if (network.containsPos(pos)) return network;
        return null;
    }

    protected void addNetworkChunk(ChunkPos chunk, RotateNetwork network) {
        if (!this.chunkNetworks.containsKey(chunk)) this.chunkNetworks.put(chunk, Lists.newArrayList());
        this.chunkNetworks.get(chunk).add(network);
    }

    protected void removeNetworkChunk(ChunkPos chunk, RotateNetwork network) {
        if (this.chunkNetworks.computeIfPresent(chunk, (c, list) -> {
            list.remove(network);
            return list;
        }).isEmpty()) this.chunkNetworks.remove(chunk);
    }
}