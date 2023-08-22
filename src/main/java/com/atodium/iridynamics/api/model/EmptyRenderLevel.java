package com.atodium.iridynamics.api.model;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.LayerLightEventListener;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public interface EmptyRenderLevel extends BlockAndTintGetter {
    EmptyRenderLevel INSTANCE = new StaticLightImpl(0, 15);
    EmptyRenderLevel FULL_BRIGHT = new StaticLightImpl(15, 15);
    EmptyRenderLevel FULL_DARK = new StaticLightImpl(0, 0);

    @Override
    default BlockEntity getBlockEntity(BlockPos pos) {
        return null;
    }

    @Override
    default BlockState getBlockState(BlockPos pos) {
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    default FluidState getFluidState(BlockPos pos) {
        return Fluids.EMPTY.defaultFluidState();
    }

    @Override
    default int getHeight() {
        return 1;
    }

    @Override
    default int getMinBuildHeight() {
        return 0;
    }

    @Override
    default float getShade(Direction direction, boolean shaded) {
        return 1.0f;
    }

    @Override
    default int getBlockTint(BlockPos pos, ColorResolver resolver) {
        Biome biome = Minecraft.getInstance().getConnection().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getOrThrow(Biomes.PLAINS);
        return resolver.getColor(biome, pos.getX(), pos.getZ());
    }

    class StaticLightImpl implements EmptyRenderLevel {
        private final LevelLightEngine lightEngine;

        public StaticLightImpl(int blockLight, int skyLight) {
            this.lightEngine = new LevelLightEngine(new LightChunkGetter() {
                @Override
                public BlockGetter getChunkForLighting(int p_63023_, int p_63024_) {
                    return StaticLightImpl.this;
                }

                @Override
                public BlockGetter getLevel() {
                    return StaticLightImpl.this;
                }
            }, false, false) {
                private final LayerLightEventListener blockListener = createStaticListener(blockLight);
                private final LayerLightEventListener skyListener = createStaticListener(skyLight);

                @Override
                public LayerLightEventListener getLayerListener(LightLayer layer) {
                    return layer == LightLayer.BLOCK ? this.blockListener : this.skyListener;
                }
            };
        }

        private static LayerLightEventListener createStaticListener(int light) {
            return new LayerLightEventListener() {
                @Override
                public void checkBlock(BlockPos pos) {

                }

                @Override
                public void onBlockEmissionIncrease(BlockPos pos, int emissionLevel) {

                }

                @Override
                public boolean hasLightWork() {
                    return false;
                }

                @Override
                public int runUpdates(int pos, boolean isQueueEmpty, boolean updateBlockLight) {
                    return pos;
                }

                @Override
                public void updateSectionStatus(SectionPos pos, boolean isQueueEmpty) {

                }

                @Override
                public void enableLightSources(ChunkPos pos, boolean isQueueEmpty) {

                }

                @Override
                public DataLayer getDataLayerData(SectionPos pos) {
                    return null;
                }

                @Override
                public int getLightValue(BlockPos pos) {
                    return light;
                }
            };
        }

        @Override
        public LevelLightEngine getLightEngine() {
            return this.lightEngine;
        }
    }
}