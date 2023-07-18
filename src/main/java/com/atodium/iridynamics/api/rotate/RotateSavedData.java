package com.atodium.iridynamics.api.rotate;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.util.data.DataUtil;
import com.atodium.iridynamics.api.util.data.DirectionInfo;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;
import org.apache.commons.compress.utils.Lists;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class RotateSavedData extends SavedData {
    public static final String ID = Iridynamics.MODID + "_rotate";

    private final List<RotateNetwork> allNetworks;
    private final Map<ChunkPos, List<RotateNetwork>> chunkNetworks;

    public RotateSavedData() {
        this.allNetworks = Lists.newArrayList();
        this.chunkNetworks = Maps.newHashMap();
    }

    public static RotateSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(RotateSavedData::load, RotateSavedData::new, ID);
    }

    private static RotateSavedData load(CompoundTag tag) {
        RotateSavedData data = new RotateSavedData();
        ListTag networksTag = tag.getList("networks", Tag.TAG_COMPOUND);
        for (int i = 0; i < networksTag.size(); i++) {
            RotateNetwork network = new RotateNetwork(data);
            network.deserializeNBT(networksTag.getCompound(i));
            data.allNetworks.add(network);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag networksTag = new ListTag();
        for (RotateNetwork allNetwork : this.allNetworks) networksTag.add(allNetwork.serializeNBT());
        tag.put("networks", networksTag);
        return tag;
    }

    public void tryTick(ServerLevel level, BlockPos pos, long tick) {
        for (Direction to : DataUtil.DIRECTIONS) {
            RotateNetwork network = this.getPosNetwork(new DirectionInfo(pos, to));
            if (network != null) network.tryTick(level, tick);
        }
    }

    public void addNode(BlockPos pos, IRotateNode node) {
        EnumMap<Direction, RotateNetwork> relatives = Maps.newEnumMap(Direction.class);
        EnumMap<Direction, Boolean> finish = Maps.newEnumMap(Direction.class);
        for (Direction to : DataUtil.DIRECTIONS) {
            if (!node.isConnectable(to)) finish.put(to, true);
            else finish.put(to, false);
            DirectionInfo toInfo = new DirectionInfo(pos, to).relative();
            RotateNetwork networkTo = this.getPosNetwork(toInfo);
            relatives.put(to, networkTo);
        }
        for (Map.Entry<Direction, RotateNetwork> outer : relatives.entrySet()) {
            Direction direction = outer.getKey();
            RotateNetwork network = outer.getValue();
            if (finish.get(direction) || network == null) continue;
            finish.put(direction, true);
            network.addNode(new DirectionInfo(pos, direction), node);
            for (Map.Entry<Direction, RotateNetwork> inner : relatives.entrySet()) {
                Direction innerDirection = inner.getKey();
                RotateNetwork innerNetwork = inner.getValue();
                if (finish.get(innerDirection)) continue;
                if (innerNetwork == network || node.isRelated(direction, innerDirection)) {
                    finish.put(innerDirection, true);
                    network.addNode(new DirectionInfo(pos, innerDirection), node);
                    if (innerNetwork != network && innerNetwork != null) network.combine(innerNetwork);
                }
            }
        }
        for (Direction direction : DataUtil.DIRECTIONS) {
            if (finish.get(direction)) continue;
            RotateNetwork network = new RotateNetwork(this);
            network.addNode(new DirectionInfo(pos, direction), node);
            finish.put(direction, true);
            for (Direction innerDirection : DataUtil.DIRECTIONS) {
                if (finish.get(innerDirection) || !node.isRelated(direction, innerDirection)) continue;
                network.addNode(new DirectionInfo(pos, innerDirection), node);
                finish.put(innerDirection, true);
            }
            this.allNetworks.add(network);
        }
        this.setDirty();
    }

    public void removeNode(BlockPos pos) {
        EnumMap<Direction, RotateNetwork> relatives = Maps.newEnumMap(Direction.class);
        EnumMap<Direction, Boolean> finish = Maps.newEnumMap(Direction.class);
        for (Direction to : DataUtil.DIRECTIONS) {
            DirectionInfo toInfo = new DirectionInfo(pos, to);
            RotateNetwork networkTo = this.getPosNetwork(toInfo);
            finish.put(to, networkTo == null);
            relatives.put(to, networkTo);
        }
        for (Direction direction : DataUtil.DIRECTIONS) {
            if (finish.get(direction)) continue;
            RotateNetwork network = relatives.get(direction);
            EnumSet<Direction> connected = EnumSet.noneOf(Direction.class);
            connected.add(direction);
            finish.put(direction, true);
            for (Direction innerDirection : DataUtil.DIRECTIONS) {
                if (finish.get(innerDirection) || network != relatives.get(innerDirection)) continue;
                connected.add(innerDirection);
                finish.put(innerDirection, true);
            }
            if (network.isEmpty()) continue;
            this.removeNetwork(network);
            for (Direction toSearch : connected) {
                Map<DirectionInfo, IRotateNode> subNodes = network.searchAllNodes(new DirectionInfo(pos, toSearch));
                if (!subNodes.isEmpty() && this.getPosNetwork(subNodes.keySet().toArray(new DirectionInfo[0])[0]) == null) {
                    RotateNetwork subNetwork = new RotateNetwork(this);
                    subNetwork.addAllNodes(subNodes);
                    subNetwork.setAngularVelocity(network.angularVelocity(subNetwork.center()));
                    subNetwork.setAngle(network.angle(subNetwork.center()));
                    this.allNetworks.add(subNetwork);
                }
            }
        }
        this.setDirty();
    }

    protected void removeNetwork(RotateNetwork network) {
        this.allNetworks.remove(network);
        network.getAllChunks().forEach((chunk) -> {
            if (this.chunkNetworks.containsKey(chunk)) this.chunkNetworks.get(chunk).remove(network);
        });
    }

    protected RotateNetwork getPosNetwork(DirectionInfo pos) {
        ChunkPos chunk = pos.chunk();
        if (!this.chunkNetworks.containsKey(chunk)) return null;
        for (RotateNetwork network : this.chunkNetworks.get(chunk)) if (network.contains(pos)) return network;
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