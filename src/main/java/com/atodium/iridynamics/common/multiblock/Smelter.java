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

import java.util.Map;
import java.util.Optional;

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
    public Optional<SmelterData> validate(MultiblockStructure structure) {
        Map<BlockPos, Block> blocks = structure.structureBlocks();
        BlockPos root = structure.root();
        int dx = structure.range().getX();
        int dy = structure.range().getY();
        int dz = structure.range().getZ();
        if (!MathUtil.between(dx, 3, 7) || !MathUtil.between(dy, 2, 7) || !MathUtil.between(dz, 3, 7))
            return Optional.empty();
        Block smelterWall = ModBlocks.SMELTER_WALL.get();
        for (Block block : blocks.values()) if (block != smelterWall) return Optional.empty();
        StructureLayer[] allLayers = MultiblockModule.allLayer(blocks, dx, dy, dz);
        if (!allLayers[0].isFilled(smelterWall)) return Optional.empty();
        for (int y = 1; y < dy; y++) if (!allLayers[y].isSurrounded(smelterWall)) return Optional.empty();
        return Optional.of(new SmelterData(root.getX(), root.getY(), root.getZ(), dx, dy, dz));
    }

    @Override
    public void onStructureFinish(ServerLevel level, StructureData data, MultiblockStructure structure) {
        SmelterData smelterData = (SmelterData) data;
        for (int x = 0; x < smelterData.dx; x++)
            for (int y = 0; y < smelterData.dy; y++)
                for (int z = 0; z < smelterData.dz; z++)
                    LiquidModule.addLiquidContainer(level, new BlockPos(smelterData.rx + x, smelterData.ry + y, smelterData.rz + z));
    }

    @Override
    public void onStructureDestroyed(ServerLevel level, StructureData data, MultiblockStructure structure) {
        SmelterData smelterData = (SmelterData) data;
        for (int x = 0; x < smelterData.dx; x++)
            for (int y = 0; y < smelterData.dy; y++)
                for (int z = 0; z < smelterData.dz; z++)
                    LiquidModule.removeLiquidContainer(level, new BlockPos(smelterData.rx + x, smelterData.ry + y, smelterData.rz + z));
    }

    public static class SmelterData extends StructureData {
        private int rx, ry, rz, dx, dy, dz;

        public SmelterData() {

        }

        public SmelterData(int rx, int ry, int rz, int dx, int dy, int dz) {
            this.rx = rx;
            this.ry = ry;
            this.rz = rz;
            this.dx = dx;
            this.dy = dy;
            this.dz = dz;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putInt("dx", this.dx);
            tag.putInt("dy", this.dy);
            tag.putInt("dz", this.dz);
            tag.putInt("rx", this.rx);
            tag.putInt("ry", this.ry);
            tag.putInt("rz", this.rz);
            return null;
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            this.dx = tag.getInt("dx");
            this.dy = tag.getInt("dy");
            this.dz = tag.getInt("dz");
            this.rx = tag.getInt("rx");
            this.ry = tag.getInt("ry");
            this.rz = tag.getInt("rz");
        }
    }
}