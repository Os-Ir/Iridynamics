package com.atodium.iridynamics.api.pipe;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.blockEntity.SavedDataTickManager;
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
import net.minecraft.world.level.material.Fluid;
import org.apache.commons.compress.utils.Lists;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class LiquidPipeSavedData extends SavedDataTickManager.TickableSavedData {
    public static final String ID = Iridynamics.MODID + "_liquid_pipe";

    private final ServerLevel level;
    private final List<LiquidPipeNetwork> allNetworks;
    private final Map<ChunkPos, List<LiquidPipeNetwork>> chunkNetworks;

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
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag networksTag = new ListTag();
        for (LiquidPipeNetwork allNetwork : this.allNetworks) networksTag.add(allNetwork.serializeNBT());
        tag.put("networks", networksTag);
        return tag;
    }

    @Override
    public void tryTick(ServerLevel level, BlockPos pos, long time) {
        for (Direction direction : DataUtil.DIRECTIONS) {
            LiquidPipeNetwork network = this.getPosNetwork(new PosDirection(pos, direction));
            if (network != null) network.tryTick(time);
        }
    }

    public Fluid getFluid(PosDirection pos) {
        LiquidPipeNetwork network = this.getPosNetwork(pos);
        return network == null ? null : network.fluid();
    }

    public boolean trySetFluid(PosDirection pos, Fluid fluid) {
        LiquidPipeNetwork network = this.getPosNetwork(pos);
        return network != null && network.trySetFluid(fluid);
    }

    public void addNodeContainer(BlockPos pos, ILiquidPipeNodeContainer block) {
        this.addAllNodes(pos, block.getAllBlockNodes());
    }

    public void addAllNodes(BlockPos pos, ILiquidPipeNode... nodes) {
        for (Direction direction : DataUtil.DIRECTIONS) {
            boolean flag = false;
            for (ILiquidPipeNode node : nodes) {
                if (flag && node.contains(direction))
                    throw new IllegalArgumentException("Different liquid pipe nodes can not connect to the same direction");
                if (node.contains(direction)) flag = true;
            }
        }
        for (ILiquidPipeNode node : nodes) this.addSingleNode(pos, node);
        this.setDirty();
    }

    private void addSingleNode(BlockPos pos, ILiquidPipeNode node) {
        EnumMap<Direction, LiquidPipeNetwork> relatives = Maps.newEnumMap(Direction.class);
        List<Direction> contained = node.containedDirections();
        Direction firstDirection = null;
        for (Direction direction : node.connectedDirections()) {
            PosDirection posTo = new PosDirection(pos, direction).relative();
            LiquidPipeNetwork network = this.getPosNetwork(posTo);
            if (network == null || !network.node(posTo).connected(posTo.direction())) continue;
            if (firstDirection == null) firstDirection = direction;
            relatives.put(direction, network);
        }
        if (firstDirection == null) {
            LiquidPipeNetwork network = new LiquidPipeNetwork(this);
            for (Direction direction : contained) network.addNode(new PosDirection(pos, direction), node);
            this.allNetworks.add(network);
        } else {
            LiquidPipeNetwork network = relatives.get(firstDirection);
            for (Direction direction : contained) network.addNode(new PosDirection(pos, direction), node);
            for (Direction direction : relatives.keySet()) {
                LiquidPipeNetwork innerNetwork = relatives.get(direction);
                if (innerNetwork != network) {
                    network.combine(innerNetwork);
                    if (network.isFluidEmpty()) network.trySetFluid(innerNetwork.fluid());
                }
            }
        }
    }

    public void removeAllNodesIn(BlockPos pos) {
        EnumMap<Direction, LiquidPipeNetwork> relatives = Maps.newEnumMap(Direction.class);
        EnumMap<Direction, Boolean> finish = Maps.newEnumMap(Direction.class);
        for (Direction to : DataUtil.DIRECTIONS) {
            PosDirection posTo = new PosDirection(pos, to);
            LiquidPipeNetwork networkTo = this.getPosNetwork(posTo);
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
                PosDirection posToSearch = new PosDirection(pos, toSearch);
                if (!network.node(posToSearch).connected(toSearch)) continue;
                Map<PosDirection, ILiquidPipeNode> subNodes = network.searchAllNodes(posToSearch);
                if (!subNodes.isEmpty() && this.getPosNetwork(subNodes.keySet().toArray(new PosDirection[0])[0]) == null) {
                    LiquidPipeNetwork subNetwork = new LiquidPipeNetwork(this);
                    subNetwork.addAllNodes(subNodes);
                    subNetwork.trySetFluid(network.fluid());
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

    protected ILiquidPipeNode getPosNode(PosDirection pos) {
        ChunkPos chunk = pos.chunk();
        if (!this.chunkNetworks.containsKey(chunk)) return null;
        for (LiquidPipeNetwork network : this.chunkNetworks.get(chunk))
            if (network.contains(pos)) return network.node(pos);
        return null;
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