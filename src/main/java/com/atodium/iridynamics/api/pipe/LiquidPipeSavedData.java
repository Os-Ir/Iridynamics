package com.atodium.iridynamics.api.pipe;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.util.data.DataUtil;
import com.atodium.iridynamics.api.util.data.PosDirection;
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

public class LiquidPipeSavedData extends SavedData {
    public static final String ID = Iridynamics.MODID + "_liquid_pipe";

    private final ServerLevel level;
    private final List<LiquidPipeNetwork> allNetworks;
    private final Map<ChunkPos, List<LiquidPipeNetwork>> chunkNetworks;
    private long lastTickTime;

    public LiquidPipeSavedData(ServerLevel level) {
        this.level = level;
        this.allNetworks = Lists.newArrayList();
        this.chunkNetworks = Maps.newHashMap();
    }

    public static LiquidPipeSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent((tag) -> load(level, tag), () -> new LiquidPipeSavedData(level), ID);
    }

    private static LiquidPipeSavedData load(ServerLevel level, CompoundTag tag) {
        LiquidPipeSavedData data = new LiquidPipeSavedData(level);
        ListTag networksTag = tag.getList("networks", Tag.TAG_COMPOUND);
        for (int i = 0; i < networksTag.size(); i++) {
            LiquidPipeNetwork network = new LiquidPipeNetwork(data);
            network.deserializeNBT(networksTag.getCompound(i));
            data.allNetworks.add(network);
        }
        data.lastTickTime = tag.getLong("lastTickTime");
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag networksTag = new ListTag();
        for (LiquidPipeNetwork allNetwork : this.allNetworks) networksTag.add(allNetwork.serializeNBT());
        tag.put("networks", networksTag);
        tag.putLong("lastTickTime", this.lastTickTime);
        return tag;
    }

    public void tryTick(ServerLevel level, long time) {
        if (this.lastTickTime < time) {
            this.allNetworks.forEach((network) -> network.tryTick(level));
            this.lastTickTime = time;
        }
    }

    public void addNode(BlockPos pos, ILiquidPipeNode node) {
        EnumMap<Direction, LiquidPipeNetwork> relatives = Maps.newEnumMap(Direction.class);
        EnumMap<Direction, Boolean> finish = Maps.newEnumMap(Direction.class);
        for (Direction to : DataUtil.DIRECTIONS) {
            if (!node.isConnectable(to)) finish.put(to, true);
            else finish.put(to, false);
            PosDirection toInfo = new PosDirection(pos, to).relative();
            LiquidPipeNetwork networkTo = this.getPosNetwork(toInfo);
            relatives.put(to, networkTo);
        }
        for (Map.Entry<Direction, LiquidPipeNetwork> outer : relatives.entrySet()) {
            Direction direction = outer.getKey();
            LiquidPipeNetwork network = outer.getValue();
            if (finish.get(direction) || network == null) continue;
            finish.put(direction, true);
            network.addNode(new PosDirection(pos, direction), node);
            for (Map.Entry<Direction, LiquidPipeNetwork> inner : relatives.entrySet()) {
                Direction innerDirection = inner.getKey();
                LiquidPipeNetwork innerNetwork = inner.getValue();
                if (finish.get(innerDirection)) continue;
                if (innerNetwork == network || node.isRelated(direction, innerDirection)) {
                    finish.put(innerDirection, true);
                    network.addNode(new PosDirection(pos, innerDirection), node);
                    if (innerNetwork != network && innerNetwork != null) network.combine(innerNetwork);
                }
            }
        }
        for (Direction direction : DataUtil.DIRECTIONS) {
            if (finish.get(direction)) continue;
            LiquidPipeNetwork network = new LiquidPipeNetwork(this);
            network.addNode(new PosDirection(pos, direction), node);
            finish.put(direction, true);
            for (Direction innerDirection : DataUtil.DIRECTIONS) {
                if (finish.get(innerDirection) || !node.isRelated(direction, innerDirection)) continue;
                network.addNode(new PosDirection(pos, innerDirection), node);
                finish.put(innerDirection, true);
            }
            this.allNetworks.add(network);
        }
        this.setDirty();
    }

    public void removeNode(BlockPos pos) {
        EnumMap<Direction, LiquidPipeNetwork> relatives = Maps.newEnumMap(Direction.class);
        EnumMap<Direction, Boolean> finish = Maps.newEnumMap(Direction.class);
        for (Direction to : DataUtil.DIRECTIONS) {
            PosDirection toInfo = new PosDirection(pos, to);
            LiquidPipeNetwork networkTo = this.getPosNetwork(toInfo);
            finish.put(to, networkTo == null);
            relatives.put(to, networkTo);
        }
        for (Direction direction : DataUtil.DIRECTIONS) {
            if (finish.get(direction)) continue;
            LiquidPipeNetwork network = relatives.get(direction);
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
                Map<PosDirection, ILiquidPipeNode> subNodes = network.searchAllNodes(new PosDirection(pos, toSearch));
                if (!subNodes.isEmpty() && this.getPosNetwork(subNodes.keySet().toArray(new PosDirection[0])[0]) == null) {
                    LiquidPipeNetwork subNetwork = new LiquidPipeNetwork(this);
                    subNetwork.addAllNodes(subNodes);
                    this.allNetworks.add(subNetwork);
                }
            }
        }
        this.setDirty();
    }

    protected ServerLevel level() {
        return this.level;
    }

    protected void removeNetwork(LiquidPipeNetwork network) {
        this.allNetworks.remove(network);
        network.getAllChunks().forEach((chunk) -> {
            if (this.chunkNetworks.containsKey(chunk)) this.chunkNetworks.get(chunk).remove(network);
        });
    }

    protected LiquidPipeNetwork getPosNetwork(PosDirection pos) {
        ChunkPos chunk = pos.chunk();
        if (!this.chunkNetworks.containsKey(chunk)) return null;
        for (LiquidPipeNetwork network : this.chunkNetworks.get(chunk)) if (network.contains(pos)) return network;
        return null;
    }

    protected void addNetworkChunk(ChunkPos chunk, LiquidPipeNetwork network) {
        if (!this.chunkNetworks.containsKey(chunk)) this.chunkNetworks.put(chunk, Lists.newArrayList());
        this.chunkNetworks.get(chunk).add(network);
    }

    protected void removeNetworkChunk(ChunkPos chunk, LiquidPipeNetwork network) {
        if (this.chunkNetworks.computeIfPresent(chunk, (c, list) -> {
            list.remove(network);
            return list;
        }).isEmpty()) this.chunkNetworks.remove(chunk);
    }
}