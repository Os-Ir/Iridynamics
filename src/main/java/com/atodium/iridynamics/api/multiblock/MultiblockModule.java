package com.atodium.iridynamics.api.multiblock;

import com.atodium.iridynamics.api.util.data.DataUtil;
import com.atodium.iridynamics.api.util.data.UnorderedRegistry;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.Deque;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class MultiblockModule {
    public static final UnorderedRegistry<ResourceLocation, Block> MULTIBLOCKS = new UnorderedRegistry<>();
    public static final UnorderedRegistry<ResourceLocation, StructureInfo<?>> STRUCTURES = new UnorderedRegistry<>();
    public static final UnorderedRegistry<ResourceLocation, AssembledStructureInfo<?>> ASSEMBLED_STRUCTURES = new UnorderedRegistry<>();

    public static void registerBlock(ResourceLocation id, Block block) {
        MULTIBLOCKS.register(id, block);
    }

    public static void registerStructure(StructureInfo<?> info) {
        STRUCTURES.register(info.id(), info);
    }

    public static boolean validateBlock(Block block) {
        return MULTIBLOCKS.containsValue(block);
    }

    public static void registerAssembledStructure(AssembledStructureInfo<?> info) {
        ASSEMBLED_STRUCTURES.register(info.id(), info);
    }

    public static Block getStructureBlockById(ResourceLocation id) {
        if (MULTIBLOCKS.containsKey(id)) return MULTIBLOCKS.get(id);
        return Blocks.AIR;
    }

    public static ResourceLocation getStructureBlockId(Block block) {
        return MULTIBLOCKS.getKeyForValue(block);
    }

    public static StructureInfo<?> getStructureInfo(ResourceLocation id) {
        return STRUCTURES.get(id);
    }

    public static AssembledStructureInfo<?> getAssembledStructureInfo(ResourceLocation id) {
        return ASSEMBLED_STRUCTURES.get(id);
    }

    public static Optional<AssembledMultiblockStructure> tryAssemble(ServerLevel level, BlockPos checkPoint) {
        return MultiblockSavedData.get(level).tryAssemble(checkPoint);
    }

    public static boolean isBlockAssembled(ServerLevel level, BlockPos pos) {
        return MultiblockSavedData.get(level).isBlockAssembled(pos);
    }

    public static void setBlock(ServerLevel level, BlockPos pos, Block block) {
        MultiblockSavedData.get(level).setBlock(pos, block);
    }

    public static void removeBlock(ServerLevel level, BlockPos pos) {
        MultiblockSavedData.get(level).removeBlock(pos);
    }

    public static MultiblockStructure getStructure(ServerLevel level, BlockPos pos) {
        return MultiblockSavedData.get(level).getStructure(pos);
    }

    public static StructureLayer layer(Map<BlockPos, Block> blocks, int layer, int sizeX, int sizeZ) {
        return new StructureLayer(blocks, layer, sizeX, sizeZ);
    }

    public static StructureLayer[] allLayer(Map<BlockPos, Block> blocks, int sizeX, int sizeY, int sizeZ) {
        StructureLayer[] layers = new StructureLayer[sizeY];
        for (int y = 0; y < sizeY; y++) layers[y] = new StructureLayer(blocks, y, sizeX, sizeZ);
        return layers;
    }

    public static Optional<Pair<StructureInfo<?>, LazyOptional<StructureInfo.StructureData>>> validateStructure(MultiblockStructure structure) {
        for (StructureInfo<?> info : STRUCTURES.values()) {
            LazyOptional<StructureInfo.StructureData> optional = info.validate(structure).cast();
            if (optional.isPresent()) return Optional.of(Pair.of(info, optional));
        }
        return Optional.empty();
    }

    public static Optional<Pair<AssembledStructureInfo<?>, AssembledStructureInfo.StructureData>> validateAssembledStructure(ServerLevel level, BlockPos checkPoint) {
        for (AssembledStructureInfo<?> info : ASSEMBLED_STRUCTURES.values()) {
            LazyOptional<AssembledStructureInfo.StructureData> optional = info.validate(level, checkPoint).cast();
            if (optional.isPresent()) return Optional.of(Pair.of(info, optional.orElse(null)));
        }
        return Optional.empty();
    }

    public static BlockSearchResult searchBlock(ServerLevel level, BlockPos checkPoint, Predicate<BlockState> predicate) {
        BlockState checkPointBlock = level.getBlockState(checkPoint);
        if (!predicate.test(checkPointBlock))
            return new BlockSearchResult(Collections.emptyMap(), checkPoint, BlockPos.ZERO, false);
        int minX, maxX, minY, maxY, minZ, maxZ;
        Map<BlockPos, BlockState> allBlocks = Maps.newHashMap();
        Deque<BlockPos> task = Lists.newLinkedList();
        minX = maxX = checkPoint.getX();
        minY = maxY = checkPoint.getY();
        minZ = maxZ = checkPoint.getZ();
        allBlocks.put(checkPoint, checkPointBlock);
        task.addLast(checkPoint);
        while (!task.isEmpty()) {
            BlockPos poll = task.poll();
            for (Direction to : DataUtil.DIRECTIONS) {
                BlockPos relative = poll.relative(to);
                BlockState relativeBlock = level.getBlockState(relative);
                if (allBlocks.containsKey(relative) || !predicate.test(relativeBlock)) continue;
                minX = Math.min(minX, relative.getX());
                minY = Math.min(minY, relative.getY());
                minZ = Math.min(minZ, relative.getZ());
                maxX = Math.max(maxX, relative.getX());
                maxY = Math.max(maxY, relative.getY());
                maxZ = Math.max(maxZ, relative.getZ());
                allBlocks.put(relative, relativeBlock);
                task.addLast(relative);
            }
        }
        int sizeX = maxX - minX + 1;
        int sizeY = maxY - minY + 1;
        int sizeZ = maxZ - minZ + 1;
        return new BlockSearchResult(Collections.emptyMap(), new BlockPos(minX, minY, minZ), new BlockPos(sizeX, sizeY, sizeZ), sizeX * sizeY * sizeZ == allBlocks.size());
    }

    public record BlockSearchResult(Map<BlockPos, BlockState> allBlocks, BlockPos root, BlockPos size,
                                    boolean isFilled) {
        public boolean isCube() {
            return this.size.getX() == this.size.getY() && this.size.getY() == this.size.getZ();
        }
    }
}
