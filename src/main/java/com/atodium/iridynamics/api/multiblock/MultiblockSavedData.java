package com.atodium.iridynamics.api.multiblock;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.util.data.DataUtil;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.saveddata.SavedData;
import org.apache.commons.compress.utils.Lists;

import java.util.*;

public class MultiblockSavedData extends SavedData {
    public static final String ID = Iridynamics.MODID + "_multiblock";

    private final List<MultiblockStructure> allStructures;
    private final Map<ChunkPos, List<MultiblockStructure>> chunkStructures;

    public MultiblockSavedData() {
        this.allStructures = Lists.newArrayList();
        this.chunkStructures = Maps.newHashMap();
    }

    public static MultiblockSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(MultiblockSavedData::load, MultiblockSavedData::new, ID);
    }

    private static MultiblockSavedData load(CompoundTag tag) {
        MultiblockSavedData data = new MultiblockSavedData();
        ListTag structuresTag = tag.getList("structures", Tag.TAG_COMPOUND);
        for (int i = 0; i < structuresTag.size(); i++) {
            MultiblockStructure network = new MultiblockStructure(data);
            network.deserializeNBT(structuresTag.getCompound(i));
            data.allStructures.add(network);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag structuresTag = new ListTag();
        for (MultiblockStructure structure : this.allStructures) structuresTag.add(structure.serializeNBT());
        tag.put("structures", structuresTag);
        return tag;
    }

    public void setBlock(ServerLevel level, BlockPos pos, Block block) {
        if (!MultiblockModule.validateBlock(block)) return;
        Set<MultiblockStructure> relatives = Sets.newHashSet();
        for (Direction direction : DataUtil.DIRECTIONS) {
            BlockPos relative = pos.relative(direction);
            MultiblockStructure structureTo = this.getStructure(relative);
            if (structureTo != null) relatives.add(structureTo);
        }
        if (relatives.isEmpty()) this.allStructures.add(new MultiblockStructure(this).addBlock(level, pos, block));
        else if (relatives.size() == 1) relatives.toArray(new MultiblockStructure[0])[0].addBlock(level, pos, block);
        else {
            MultiblockStructure[] relativesArray = relatives.toArray(new MultiblockStructure[0]);
            MultiblockStructure[] combine = new MultiblockStructure[relativesArray.length - 1];
            System.arraycopy(relativesArray, 1, combine, 0, relativesArray.length - 1);
            relativesArray[0].combine(level, combine);
            relativesArray[0].addBlock(level, pos, block);
        }
        this.setDirty();
    }

    public void removeBlock(ServerLevel level, BlockPos pos) {
        if (this.getStructure(pos) == null) return;
        EnumMap<Direction, MultiblockStructure> relatives = Maps.newEnumMap(Direction.class);
        EnumMap<Direction, Boolean> finish = Maps.newEnumMap(Direction.class);
        boolean single = true;
        for (Direction direction : DataUtil.DIRECTIONS) {
            BlockPos relative = pos.relative(direction);
            MultiblockStructure networkTo = this.getStructure(relative);
            if (networkTo != null) single = false;
            finish.put(direction, networkTo == null);
            relatives.put(direction, networkTo);
        }
        if (single) {
            MultiblockStructure structure = this.getStructure(pos);
            structure.destroyStructure(level);
            this.removeStructure(structure);
        }
        for (Direction direction : DataUtil.DIRECTIONS) {
            if (finish.get(direction)) continue;
            MultiblockStructure structure = relatives.get(direction);
            EnumSet<Direction> connected = EnumSet.noneOf(Direction.class);
            connected.add(direction);
            finish.put(direction, true);
            for (Direction innerDirection : DataUtil.DIRECTIONS) {
                if (finish.get(innerDirection) || structure != relatives.get(innerDirection)) continue;
                connected.add(innerDirection);
                finish.put(innerDirection, true);
            }
            if (structure.isEmpty()) continue;
            structure.destroyStructure(level);
            this.removeStructure(structure);
            for (Direction toSearch : connected) {
                Map<BlockPos, Block> subBlocks = structure.searchAllBlocks(pos, toSearch);
                if (!subBlocks.isEmpty() && this.getStructure(subBlocks.keySet().toArray(new BlockPos[0])[0]) == null) {
                    MultiblockStructure subStructure = new MultiblockStructure(this);
                    subStructure.addAllBlocks(level, subBlocks);
                    this.allStructures.add(subStructure);
                }
            }
        }
        this.setDirty();
    }

    public MultiblockStructure getStructure(BlockPos pos) {
        ChunkPos chunk = new ChunkPos(pos);
        if (!this.chunkStructures.containsKey(chunk)) return null;
        for (MultiblockStructure network : this.chunkStructures.get(chunk)) if (network.contains(pos)) return network;
        return null;
    }

    protected void removeStructure(MultiblockStructure structure) {
        this.allStructures.remove(structure);
        structure.getAllChunks().forEach((chunk) -> {
            if (this.chunkStructures.containsKey(chunk)) this.chunkStructures.get(chunk).remove(structure);
        });
    }

    protected void addStructureChunk(ChunkPos chunk, MultiblockStructure structure) {
        if (!this.chunkStructures.containsKey(chunk)) this.chunkStructures.put(chunk, Lists.newArrayList());
        this.chunkStructures.get(chunk).add(structure);
    }

    protected void removeStructureChunk(ChunkPos chunk, MultiblockStructure structure) {
        if (this.chunkStructures.computeIfPresent(chunk, (c, list) -> {
            list.remove(structure);
            return list;
        }).isEmpty()) this.chunkStructures.remove(chunk);
    }
}