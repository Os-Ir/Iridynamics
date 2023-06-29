package com.atodium.iridynamics.common.level.levelgen.feature;

import com.atodium.iridynamics.common.block.ModBlocks;
import com.atodium.iridynamics.common.level.levelgen.config.StickConfig;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class StickFeature extends Feature<StickConfig> {
    public StickFeature(Codec<StickConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<StickConfig> context) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();
        if (level.getBlockState(pos.below()).isFaceSturdy(level, pos, Direction.UP)) {
            this.setBlock(level, pos, ModBlocks.PLACED_STICK.get().defaultBlockState());
            return true;
        }
        return false;
    }
}