package com.atodium.iridynamics.common.levelgen.feature;

import com.atodium.iridynamics.common.block.ModBlocks;
import com.atodium.iridynamics.common.levelgen.config.OreNuggetConfig;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import java.util.Random;

public class OreNuggetFeature extends Feature<OreNuggetConfig> {
    public OreNuggetFeature(Codec<OreNuggetConfig> codec) {
        super(codec);
        System.out.println("Creating Feature!");
    }

    @Override
    public boolean place(FeaturePlaceContext<OreNuggetConfig> context) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();
        Random rand = context.random();
        OreNuggetConfig config = context.config();
        System.out.println(pos);
        this.setBlock(level, pos, ModBlocks.FORGE.get().defaultBlockState());
        return false;
    }
}