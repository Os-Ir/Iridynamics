package com.atodium.iridynamics.api.multiblock;

import com.atodium.iridynamics.api.util.data.UnorderedRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class MultiblockModule {
    public static final UnorderedRegistry<ResourceLocation, Block> MULTIBLOCK = new UnorderedRegistry<>();

    public static void register(ResourceLocation id, Block block) {
        MULTIBLOCK.register(id, block);
    }

    public static boolean validateBlock(Block block) {
        return MULTIBLOCK.containsValue(block);
    }

    public static Block getBlock(ResourceLocation id) {
        if (MULTIBLOCK.containsKey(id)) return MULTIBLOCK.get(id);
        return Blocks.AIR;
    }

    public static ResourceLocation getBlockId(Block block) {
        return MULTIBLOCK.getKeyForValue(block);
    }

    public static void setBlock(ServerLevel level, BlockPos pos, Block block) {
        MultiblockSavedData.get(level).setBlock(pos, block);
    }
}
