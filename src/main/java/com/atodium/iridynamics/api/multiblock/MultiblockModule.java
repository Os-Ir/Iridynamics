package com.atodium.iridynamics.api.multiblock;

import com.atodium.iridynamics.api.multiblock.assembled.AssembledMultiblockStructure;
import com.atodium.iridynamics.api.multiblock.assembled.AssembledStructureInfo;
import com.atodium.iridynamics.api.util.data.UnorderedRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.Optional;

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
}
