package com.atodium.iridynamics.common.multiblock;

import com.atodium.iridynamics.Iridynamics;
import com.atodium.iridynamics.api.liquid.LiquidModule;
import com.atodium.iridynamics.api.multiblock.MultiblockModule;
import com.atodium.iridynamics.api.multiblock.MultiblockStructure;
import com.atodium.iridynamics.api.multiblock.StructureInfo;
import com.atodium.iridynamics.api.multiblock.StructureLayer;
import com.atodium.iridynamics.api.util.math.MathUtil;
import com.atodium.iridynamics.common.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Map;

public class Smelter extends StructureInfo<Smelter.SmelterData> {
    public static final ResourceLocation ID = Iridynamics.rl("smelter");
    public static final Smelter INSTANCE = new Smelter();

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public SmelterData createEmptyData() {
        return new SmelterData();
    }

    @Override
    public LazyOptional<SmelterData> validate(MultiblockStructure structure) {
        Map<BlockPos, Block> blocks = structure.structureBlocks();
        BlockPos root = structure.root();
        int sizeX = structure.size().getX();
        int sizeY = structure.size().getY();
        int sizeZ = structure.size().getZ();
        if (!MathUtil.between(sizeX, 3, 7) || !MathUtil.between(sizeY, 2, 7) || !MathUtil.between(sizeZ, 3, 7))
            return LazyOptional.empty();
        Block smelterWall = ModBlocks.SMELTER_WALL.get();
        for (Block block : blocks.values()) if (block != smelterWall) return LazyOptional.empty();
        StructureLayer[] allLayers = MultiblockModule.allLayer(blocks, sizeX, sizeY, sizeZ);
        if (!allLayers[0].isFilled(smelterWall)) return LazyOptional.empty();
        for (int y = 1; y < sizeY; y++) if (!allLayers[y].isSurrounded(smelterWall)) return LazyOptional.empty();
        return LazyOptional.of(() -> new SmelterData(root.getX(), root.getY(), root.getZ(), sizeX, sizeY, sizeZ));
    }

    @Override
    public void onStructureFinish(ServerLevel level, StructureData data, MultiblockStructure structure) {
        SmelterData smelterData = (SmelterData) data;
        for (int x = 1; x < smelterData.sizeX - 1; x++)
            for (int y = 1; y < smelterData.sizeY; y++)
                for (int z = 1; z < smelterData.sizeZ - 1; z++)
                    LiquidModule.addLiquidContainer(level, new BlockPos(smelterData.rx + x, smelterData.ry + y, smelterData.rz + z));
    }

    @Override
    public void onStructureDestroyed(ServerLevel level, StructureData data, MultiblockStructure structure) {
        SmelterData smelterData = (SmelterData) data;
        for (int x = 1; x < smelterData.sizeX - 1; x++)
            for (int y = 1; y < smelterData.sizeY; y++)
                for (int z = 1; z < smelterData.sizeZ - 1; z++)
                    LiquidModule.removeLiquidContainer(level, new BlockPos(smelterData.rx + x, smelterData.ry + y, smelterData.rz + z));
    }

    public static class SmelterData extends StructureData {
        private int rx, ry, rz, sizeX, sizeY, sizeZ;

        public SmelterData() {

        }

        public SmelterData(int rx, int ry, int rz, int sizeX, int sizeY, int sizeZ) {
            this.rx = rx;
            this.ry = ry;
            this.rz = rz;
            this.sizeX = sizeX;
            this.sizeY = sizeY;
            this.sizeZ = sizeZ;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putInt("sizeX", this.sizeX);
            tag.putInt("sizeY", this.sizeY);
            tag.putInt("sizeZ", this.sizeZ);
            tag.putInt("rx", this.rx);
            tag.putInt("ry", this.ry);
            tag.putInt("rz", this.rz);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            this.sizeX = tag.getInt("sizeX");
            this.sizeY = tag.getInt("sizeY");
            this.sizeZ = tag.getInt("sizeZ");
            this.rx = tag.getInt("rx");
            this.ry = tag.getInt("ry");
            this.rz = tag.getInt("rz");
        }
    }
}